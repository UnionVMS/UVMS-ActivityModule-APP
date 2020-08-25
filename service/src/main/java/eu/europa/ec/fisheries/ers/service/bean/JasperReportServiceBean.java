package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FishingActivityTypeEnum;
import eu.europa.ec.fisheries.ers.service.JasperReportService;
import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.dto.FACatchModel;
import eu.europa.ec.fisheries.ers.service.dto.GearMapperModel;
import eu.europa.ec.fisheries.ers.service.dto.GearModel;
import eu.europa.ec.fisheries.ers.service.dto.HeaderModel;
import eu.europa.ec.fisheries.ers.service.dto.LogbookModel;
import eu.europa.ec.fisheries.ers.service.dto.MasterModel;
import eu.europa.ec.fisheries.ers.service.dto.PortLogBookModel;
import eu.europa.ec.fisheries.ers.service.dto.TranshipmentLandingModel;
import eu.europa.ec.fisheries.ers.service.dto.TripInfoLogBookModel;
import eu.europa.ec.fisheries.ers.service.dto.VesselIdentifierLogBookModel;
import eu.europa.ec.fisheries.ers.service.mapper.FishingTripIdWithGeometryMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FaCatchTypeEnum;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Stateless
@Local(JasperReportService.class)
@Transactional
@Slf4j
public class JasperReportServiceBean implements JasperReportService {

    private static final String GFCM = "GFCM";
    private static final String IRCS = "IRCS";
    private static final String UVI = "UVI";
    private static final String EXT_MARK = "EXT_MARK";
    private static final String ICCAT = "ICCAT";
    private static final String CFR = "CFR";
    private static final String RECEIVER = "RECEIVER";
    private static final String CATCHING_VESSEL = "CATCHING_VESSEL";
    private static final String PARTICIPATING_VESSEL = "PARTICIPATING_VESSEL";
    private static final String PAIR_FISHING_PARTNER = "PAIR_FISHING_PARTNER";
    private static final String DONOR = "DONOR";
    private static final String P = "P";
    private static final String C = "C";
    private static final String O = "O";
    private static final String LSC = "LSC";
    private static final String BMS = "BMS";
    private static final String FAO_AREA = "FAO_AREA";
    private static final String GFCM_GSA = "GFCM_GSA";
    private static final String STAT_RECTANGLE = "STAT_RECTANGLE";
    private static final String TERRITORY = "TERRITORY";
    private static final String POSITION = "POSITION";
    private static final String LOCATION = "LOCATION";
    private static final String GEAR_RETRIEVAL = "GEAR_RETRIEVAL";
    private static final String GEAR_SHOT = "GEAR_SHOT";
    private static final String CATCHES = "CATCHES";
    private static final String DISCARDS = "DISCARDS";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String FA_CHARACTERISTIC = "FA_CHARACTERISTIC";
    private static final String FISHING_DEPTH = "FISHING_DEPTH";
    private static final String CODE = "CODE";
    private static final String MEASURE = "MEASURE";
    private static final String QUANTITY = "QUANTITY";
    private static final String TEXT = "TEXT";
    private static final String N_A = "N/A";
    private static final String ME = "ME";
    private static final String TRANSHIPMENT = "TRANSHIPMENT";
    private static final String DESTINATION_LOCATION = "DESTINATION_LOCATION";
    private static final String RELOCATION = "RELOCATION";
    private static final String LANDING = "LANDING";
    private static final String FISH_PRESENTATION = "FISH_PRESENTATION";
    private static final String RETAINED = "RETAINED";


    @EJB
    private MdrModuleService mdrModuleServiceBean;
    private HashMap<String,String> mdrGearCharacteristic;

    public void generateLogBookReport(String tripId, List<FaReportDocumentEntity> faReportDocumentEntities, OutputStream destination) throws ServiceException {
        this.mdrGearCharacteristic = getGearCharacteristicFromMDR();
        LogbookModel logbookModel = getJasperReportData(faReportDocumentEntities, tripId);


        try (InputStream input = getClass().getClassLoader().getResourceAsStream("logbook.jrxml")){
            JasperDesign jasperDesign = JRXmlLoader.load(input);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,  prepareParams(logbookModel), new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(jasperPrint, destination);
        } catch (JRException | IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private Map<String,Object> prepareParams(LogbookModel logbookModel){
        JRBeanCollectionDataSource portsCollection = new JRBeanCollectionDataSource(logbookModel.getPorts());
        JRBeanCollectionDataSource vesselCollection = new JRBeanCollectionDataSource(logbookModel.getVesselIdentifier());
        JRBeanCollectionDataSource tripCollection = new JRBeanCollectionDataSource(logbookModel.getTripInfo());
        JRBeanCollectionDataSource catchCollection = new JRBeanCollectionDataSource(logbookModel.getCatches());
        JRBeanCollectionDataSource transhipmentLandingCollection = new JRBeanCollectionDataSource(logbookModel.getTranshipmentLandings());
        JRBeanCollectionDataSource masterCollection = new JRBeanCollectionDataSource(logbookModel.getMaster());
        JRBeanCollectionDataSource gearCollection = new JRBeanCollectionDataSource(logbookModel.getGears());
        JRBeanCollectionDataSource headerCollection = new JRBeanCollectionDataSource(logbookModel.getHeader());
        Map<String,Object> params = new HashMap<>();
        params.put("PortParam",portsCollection);
        params.put("VesselParam", vesselCollection);
        params.put("TripParam", tripCollection);
        params.put("CatchParam", catchCollection);
        params.put("TranshipmentLandingParam", transhipmentLandingCollection);
        params.put("MasterParam", masterCollection);
        params.put("GearParam", gearCollection);
        params.put("HeaderParam", headerCollection);
        return params;
    }

    private LogbookModel getJasperReportData(List<FaReportDocumentEntity> faReportDocumentEntities,String tripId){
        LogbookModel logbookModel = new LogbookModel();

        List<FishingActivityEntity> activities = new ArrayList<>();
        List<MasterModel> masterModel = new ArrayList<>();
        List<TripInfoLogBookModel> tripInfoLogBookModelList = new LinkedList<>();
        List<GearMapperModel> gearGrouping = new ArrayList<>();
        Map<Integer,Integer> matchGearTypeToFA = new HashMap<>();
        TripInfoLogBookModel tripInfoLogBookModel = new TripInfoLogBookModel();
        tripInfoLogBookModel.setTripId(tripId);

        Map<String, VesselIdentifierLogBookModel> vesselIdentifierLogBookModelList = new HashMap<>();
        faReportDocumentEntities.forEach(t -> t.getFishingActivities().forEach(fa -> {
            activities.add(fa);
            HashMap<String,String> identifierMap = getIdentifiers(fa);
            String identifier = getIdentifierByOrder(identifierMap);
            gearMapper(fa,gearGrouping,matchGearTypeToFA);
            if(TRANSHIPMENT.equals(fa.getTypeCode()) || LANDING.equals(fa.getTypeCode()) || RELOCATION.equals(fa.getTypeCode())) {
                if(isReceiverVessel(fa) && tripInfoLogBookModelList.isEmpty()){
                    addMasterDetails(masterModel,fa);
                    tripInfoLogBookModelList.add(calculateTripInfoList(tripInfoLogBookModel,identifier,identifierMap,fa));
                }
            }

            if (vesselIdentifierLogBookModelList.get(identifier) == null) {
                createVesselIdentifierLogBookModel(vesselIdentifierLogBookModelList,masterModel,identifierMap,identifier,fa);
            }
        }));

        List<FishingActivityEntity> destinationList = activities.stream().filter(t -> TRANSHIPMENT.equals(t.getTypeCode()) || RELOCATION.equals(t.getTypeCode())).collect(Collectors.toList());
        String portAndCountryOfDestination = calculatePortAndCountryOfDestination(destinationList);
        logbookModel.setPorts(retrievePortLogBookModel(activities));
        logbookModel.setVesselIdentifier( new ArrayList(vesselIdentifierLogBookModelList.values()));
        //.values().stream().sorted(Comparator.comparingInt(VesselIdentifierLogBookModel::getId)).collect(Collectors.toList()))
        logbookModel.setTripInfo(processPortLogBookModel(portAndCountryOfDestination, tripInfoLogBookModelList));
        logbookModel.setCatches(createCatchesModel(activities,matchGearTypeToFA));
        List<FishingActivityEntity> transhipmentLandingRelocList = activities.stream().filter(t -> TRANSHIPMENT.equals(t.getTypeCode()) || LANDING.equals(t.getTypeCode()) || RELOCATION.equals(t.getTypeCode())).collect(Collectors.toList());
        logbookModel.setTranshipmentLandings(createTranshipmentLanding(transhipmentLandingRelocList));
        logbookModel.setGears(createGearsModel(gearGrouping));
        logbookModel.setHeader(Arrays.asList(new HeaderModel(tripId,new Date())));
        logbookModel.setMaster(new ArrayList<>(masterModel));
        return logbookModel;
    }

    private void addMasterDetails(List<MasterModel> masterModel,FishingActivityEntity fa){
        MasterModel master = new MasterModel();
        master.setId("R");
        master.setAddress(getMastersAddress(fa));
        master.setMastersName(getMastersName(fa));
        masterModel.add(master);
    }

    private List<TripInfoLogBookModel> processPortLogBookModel(String portAndCountryOfDestination,List<TripInfoLogBookModel> receiverList){
        if(portAndCountryOfDestination == null){
            return new ArrayList<>();
        }

        if(receiverList.isEmpty()){
            TripInfoLogBookModel logBookModel = new TripInfoLogBookModel();
            logBookModel.setDestination(portAndCountryOfDestination);
            receiverList.add(logBookModel);
        }else{
            receiverList.get(0).setDestination(portAndCountryOfDestination);
        }
        return receiverList;
    }

    private String calculatePortAndCountryOfDestination(List<FishingActivityEntity> faList){
        Set<String> relocationValue = new HashSet<>();
        String country;
        for (FishingActivityEntity fa:faList) {
            country = fa.getFaReportDocument().getVesselTransportMeans() == null || fa.getFaReportDocument().getVesselTransportMeans().isEmpty() ? null : fa.getFaReportDocument().getVesselTransportMeans().iterator().next().getCountry();
            if (TRANSHIPMENT.equals(fa.getTypeCode())){
                FluxCharacteristicEntity characteristicEntity = fa.getFluxCharacteristics().stream().filter(t -> DESTINATION_LOCATION.equals(t.getTypeCode())).findFirst().orElse(null);
                if(characteristicEntity == null){
                    continue;
                }
                String portName = getPortName(characteristicEntity.getFluxLocation());
                String port = portName == null ? formatCoordinates(characteristicEntity.getFluxLocation()):portName;
                if(port!=null) {
                    relocationValue.add(port +","+ country);
                }
            }
            if (RELOCATION.equals(fa.getTypeCode()) && !relocationValue.isEmpty()){
                FluxCharacteristicEntity characteristicEntity = fa.getFluxCharacteristics().stream().filter(t -> RELOCATION.equals(t.getTypeCode())).findFirst().orElse(null);
                if(characteristicEntity == null){
                    continue;
                }
                FluxLocationEntity fluxLocationEntity = characteristicEntity.getFaCatch() == null ? null : characteristicEntity.getFaCatch().getFluxLocations() == null || characteristicEntity.getFaCatch().getFluxLocations().isEmpty() ? null : characteristicEntity.getFaCatch().getFluxLocations().iterator().next();
                String portName = getPortName(fluxLocationEntity);
                String port = portName == null ? formatCoordinates(fluxLocationEntity):portName;
                if(port!=null) {
                    relocationValue.add(port +","+ country);
                }
            }
        }
        return relocationValue.isEmpty()? null: String.join("/", relocationValue);
    }

    private List<GearModel> createGearsModel(List<GearMapperModel> gearGroupings){

                List<GearModel> gearModelList = new ArrayList<>();
                for(GearMapperModel gearEntry:gearGroupings){
                    int i = 0;
                    GearModel model = new GearModel();
                    model.setRowNo(gearEntry.getIdentifier());
                    model.setMeshSize(gearEntry.getMeshCode());
                    model.setGearCode(gearEntry.getGearCode());
                    if(gearEntry.getDimensions() == null || gearEntry.getDimensions().isEmpty()){
                        gearModelList.add(model);
                    }

                    for(String dimensions:gearEntry.getDimensions()){
                        if(i > 0){
                            model = new GearModel();
                        }
                        model.setDimensions(dimensions);
                        gearModelList.add(model);
                        i++;
                    }
                }
                return gearModelList;
    }


    public void gearMapper(FishingActivityEntity fa,List<GearMapperModel> gearGrouping,Map<Integer,Integer> matchGearTypeToFA){
        Set<FishingGearEntity> fishingGears = fa.getFishingGears();
        String gearCode;

        for(FishingGearEntity fishingGearEntity:fishingGears) {
            Map<String, Set<String>> meshSize = getMeshSize(fishingGearEntity.getGearCharacteristics());
            gearCode = fishingGearEntity.getTypeCode();
            for(Map.Entry<String, Set<String>> meshEntry:meshSize.entrySet()) {
                GearMapperModel newGear = createNewGear(gearGrouping.size() + 1, meshEntry, gearCode);
                if(gearGrouping.indexOf(newGear) == -1){
                    gearGrouping.add(newGear);
                    matchGearTypeToFA.put(fa.getId(),newGear.getIdentifier());
                } else {
                    matchGearTypeToFA.put(fa.getId(),gearGrouping.get(gearGrouping.indexOf(newGear)).getIdentifier());
                }
            }
        }
    }


    private GearMapperModel createNewGear(int identifier,Map.Entry<String, Set<String>> meshEntry,String gearCode) {
            GearMapperModel newGear = new GearMapperModel();
            newGear.setGearCode(gearCode);
            newGear.setMeshCode(meshEntry.getKey());
            newGear.setDimensions(meshEntry.getValue());
            newGear.setIdentifier(identifier);
            return newGear;

    }


    public Map<String, Set<String>> getMeshSize(Set<GearCharacteristicEntity> gearCharacteristics){
        Set<String> dimensions = new HashSet<>();
        Map<String, Set<String>> meshSizeMap = new HashMap<>();
        String key = N_A;
        for(GearCharacteristicEntity gearCharacteristicEntity : gearCharacteristics){
            if(ME.equals(gearCharacteristicEntity.getTypeCode())){
                key = gearCharacteristicEntity.getValueMeasure() +" "+ gearCharacteristicEntity.getValueMeasureUnitCode();
            }else {
                dimensions.add(gearCharacteristicEntity.getTypeCode()+": "+ getDimensionValue(gearCharacteristicEntity));
            }
        }

        meshSizeMap.put(key, dimensions);
        return meshSizeMap;
    }

    public String getDimensionValue(GearCharacteristicEntity gearCharacteristicEntity){
        String gearCharacteristic = mdrGearCharacteristic.get(gearCharacteristicEntity.getTypeCode());

        if(CODE.equals(gearCharacteristic)){
            return gearCharacteristicEntity.getValueCode();
        }

        if(MEASURE.equals(gearCharacteristic)){
            return gearCharacteristicEntity.getValueMeasure() +" "+ gearCharacteristicEntity.getValueMeasureUnitCode();
        }

        if(QUANTITY.equals(gearCharacteristic)){
            return gearCharacteristicEntity.getValueQuantity()+ " "+ gearCharacteristicEntity.getValueQuantityCode();
        }

        if(TEXT.equals(gearCharacteristic)){
            return gearCharacteristicEntity.getValueText();
        }

        return "";
    }

    private HashMap<String,String> getGearCharacteristicFromMDR(){
        try {
            Map<String, List<String>> acronymFromMdr = mdrModuleServiceBean.getAcronymFromMdr("FA_GEAR_CHARACTERISTIC", "code", "dataType");
            List<String> codeList = acronymFromMdr.get("code");
            List<String> dataTypeList = acronymFromMdr.get("dataType");
            Iterator<String> i1 = codeList.iterator();
            Iterator<String> i2 = dataTypeList.iterator();
            HashMap<String,String> map = new HashMap<>();
            while (i1.hasNext() && i2.hasNext()) {
                map.put(i1.next(), i2.next());
            }
            return map;
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    private  TripInfoLogBookModel calculateTripInfoList(TripInfoLogBookModel tripInfoLogBookModel,String identifier,Map<String,String> identifiers,FishingActivityEntity fa){

        tripInfoLogBookModel.setCountry(fa.getFaReportDocument().getVesselTransportMeans() == null || fa.getFaReportDocument().getVesselTransportMeans().isEmpty() ? null : fa.getFaReportDocument().getVesselTransportMeans().iterator().next().getCountry());
        tripInfoLogBookModel.setExtMark(getIdentifier(fa, EXT_MARK));
        tripInfoLogBookModel.setIdentifier(identifier);
        tripInfoLogBookModel.setImo(getIdentifier(fa, UVI));
        tripInfoLogBookModel.setIrcs(getIdentifier(fa, IRCS));
        tripInfoLogBookModel.setVesselName(fa.getFaReportDocument().getVesselTransportMeans() == null || fa.getFaReportDocument().getVesselTransportMeans().isEmpty() ? null : fa.getFaReportDocument().getVesselTransportMeans().iterator().next().getName());
        tripInfoLogBookModel.setOther((identifiers.get(GFCM) == null? "":identifiers.get(GFCM)) + " " + (identifiers.get(ICCAT) == null ? "":identifiers.get(ICCAT)));
        return tripInfoLogBookModel;
    }

    private void createVesselIdentifierLogBookModel( Map<String, VesselIdentifierLogBookModel> vesselIdentifierLogBookModelList,List<MasterModel> masterModelList,Map<String,String> identifiers,String identifier,FishingActivityEntity fa){
        VesselIdentifierLogBookModel dto = new VesselIdentifierLogBookModel();
        dto.setId(vesselIdentifierLogBookModelList.size()+1);
        dto.setCfr(identifiers.get(CFR));
        dto.setCountry(fa.getFaReportDocument().getVesselTransportMeans() == null || fa.getFaReportDocument().getVesselTransportMeans().isEmpty() ? null : fa.getFaReportDocument().getVesselTransportMeans().iterator().next().getCountry());
        dto.setVesselName(fa.getFaReportDocument().getVesselTransportMeans() == null || fa.getFaReportDocument().getVesselTransportMeans().isEmpty() ? null : fa.getFaReportDocument().getVesselTransportMeans().iterator().next().getName());
        dto.setExtMark(getIdentifier(fa, EXT_MARK));
        dto.setIrcs(getIdentifier(fa, IRCS));
        dto.setImo(getIdentifier(fa, UVI));
        dto.setRole(getRole(fa));
        dto.setOther( (identifiers.get(GFCM) == null? "":identifiers.get(GFCM)) + " " + (identifiers.get(ICCAT) == null ? "":identifiers.get(ICCAT)) );
        vesselIdentifierLogBookModelList.put(identifier, dto);

        MasterModel masterModel = new MasterModel();
        masterModel.setId(String.valueOf(masterModelList.size() + 1));
        masterModel.setAddress(getMastersAddress(fa));
        masterModel.setMastersName(getMastersName(fa));
        masterModelList.add(masterModel);
    }

    private String getIdentifierByOrder(Map<String,String> identifiers){
        String cfr = identifiers.get(CFR);
        String gfcm = identifiers.get(GFCM);
        String iccat = identifiers.get(ICCAT);
        return cfr == null ? gfcm == null ? iccat : gfcm : cfr;
    }

    private boolean isReceiverVessel(FishingActivityEntity fa){
        for(VesselTransportMeansEntity vesselTransportMeansEntity:fa.getVesselTransportMeans()) {
            if(RECEIVER.equals(vesselTransportMeansEntity.getRoleCode())){
                return true;
            }
        }
        return false;
    }

    private String getRole(FishingActivityEntity fa){

        if(fa.getVesselTransportMeans() == null || fa.getVesselTransportMeans().isEmpty()){
            return null;
        }

        Set<VesselTransportMeansEntity> transportMeansEntitySet = fa.getVesselTransportMeans();
        for(VesselTransportMeansEntity transportMeansEntity:transportMeansEntitySet){
            String roleCode = transportMeansEntity.getRoleCode();
            if(PAIR_FISHING_PARTNER.equals(roleCode) || PARTICIPATING_VESSEL.equals(roleCode)){
                return P;
            }
            if(CATCHING_VESSEL.equals(roleCode)){
                return C;
            }

            if(roleCode != null && !(DONOR.equals(roleCode) || RECEIVER.equals(roleCode))){
                return O;
            }
        }

        return null;
    }

    private String getMastersAddress(FishingActivityEntity fa){
        StructuredAddressEntity structuredAddressEntity = fa.getVesselTransportMeans() == null || fa.getVesselTransportMeans().isEmpty() ?
                null : fa.getVesselTransportMeans().iterator().next().getContactParty() == null || fa.getVesselTransportMeans().iterator().next().getContactParty().isEmpty() ?
                null : fa.getVesselTransportMeans().iterator().next().getContactParty().iterator().next().getStructuredAddresses() == null || fa.getVesselTransportMeans().iterator().next().getContactParty().iterator().next().getStructuredAddresses().isEmpty() ?
                null : fa.getVesselTransportMeans().iterator().next().getContactParty().iterator().next().getStructuredAddresses().iterator().next();

        if(structuredAddressEntity == null){
            return null;
        }

        return  this.isNotNullAppend(structuredAddressEntity.getCityName()) + this.isNotNullAppend(structuredAddressEntity.getStreetName()) + this.isNotNullAppend(structuredAddressEntity.getPostalAreaValue()) + this.isNotNullAppend(structuredAddressEntity.getCountryName());
    }

    private String getMastersName(FishingActivityEntity fa){
        return fa.getVesselTransportMeans() == null || fa.getVesselTransportMeans().isEmpty() ?
                null : fa.getVesselTransportMeans().iterator().next().getContactParty() == null || fa.getVesselTransportMeans().iterator().next().getContactParty().isEmpty() ?
                null : fa.getVesselTransportMeans().iterator().next().getContactParty().iterator().next().getContactPerson() == null  ?
                null : fa.getVesselTransportMeans().iterator().next().getContactParty().iterator().next().getContactPerson().getGivenName();
    }

    private String isNotNullAppend(String s){
        if(s == null){
            return "";
        }
        else return  s + " ";
    }

    private String getIdentifier(FishingActivityEntity fishingActivityEntity, String identifierType){
        Set<VesselIdentifierEntity> identifierEntitySet = new HashSet<>();
        fishingActivityEntity.getFaReportDocument().getVesselTransportMeans().forEach( z-> identifierEntitySet.addAll(z.getVesselIdentifiers()));

        for(VesselIdentifierEntity vesselIdentifier:identifierEntitySet){
            if(identifierType.equals(vesselIdentifier.getVesselIdentifierSchemeId())){
                return vesselIdentifier.getVesselIdentifierId();
            }
        }

        return null;
    }

    public List<TranshipmentLandingModel> createTranshipmentLanding(List<FishingActivityEntity> faList){

        if(faList.isEmpty()){
            return new ArrayList<>();
        }

        Map<String, Map<String, List<FishingActivityEntity>>> groupedFA = groupBySpeciesAndPresentation(faList);
        List<TranshipmentLandingModel> modelList = new ArrayList<>();

        int presentationCounter = 0;
        for( Map.Entry<String, Map<String, List<FishingActivityEntity>>> productBySpecies:groupedFA.entrySet()) {
            TranshipmentLandingModel model = new TranshipmentLandingModel();
            model.setSpecies(productBySpecies.getKey());
            for(Map.Entry<String, List<FishingActivityEntity>> productByPresentation:productBySpecies.getValue().entrySet()){
                if(presentationCounter > 0){
                    model = new TranshipmentLandingModel();
                }
                model.setPresentation(productByPresentation.getKey());
                calculateCatchesWeightBySpeciesAndCategory(productByPresentation.getValue(),model);
                calculateTranshipmentLandingAreas(getLocationsForTranshipmentsLanding(new ArrayList<>(productByPresentation.getValue())),model);

                modelList.add(model);
                presentationCounter++;
            }
            presentationCounter = 0;
        }

        return modelList;
    }

    private void calculateCatchesWeightBySpeciesAndCategory(List<FishingActivityEntity> presentationActivities, TranshipmentLandingModel model){
        for(FishingActivityEntity fa :presentationActivities) {
            getCatchesWeightAndCountBySpecies(fa.getFaCatchs(),model);
        }

        model.setWeightTotal(model.getWeightLSC() + model.getWeightBMS());
        model.setNbTotal(model.getNbLSC() + model.getNbBMS());
    }

    public void getCatchesWeightAndCountBySpecies(Set<FaCatchEntity> catches, TranshipmentLandingModel model){
        double weight,unitQuantity;
        for(FaCatchEntity faCatch: catches){
            if(model.getSpecies() == null || !model.getSpecies().equals(faCatch.getSpeciesCode()) || !FaCatchTypeEnum.UNLOADED.value().equals(faCatch.getTypeCode())){
                continue;
            }
            weight = calculateWeight(faCatch);
            unitQuantity = calculateQuantity(faCatch);

            if(LSC.equals(faCatch.getFishClassCode())) {
                model.setWeightLSC(model.getWeightLSC() + weight);
                model.setNbLSC(model.getNbLSC() +unitQuantity);
            }
            if(BMS.equals(faCatch.getFishClassCode())){
                model.setWeightBMS(model.getWeightBMS() + weight);
                model.setNbBMS(model.getNbBMS() + unitQuantity);
            }
        }
    }

    private double calculateQuantity(FaCatchEntity faCatch){
        double unitQuantity = 0;
        if(faCatch.getAapProcesses() != null && !faCatch.getAapProcesses().isEmpty() ) {

            unitQuantity = faCatch.getAapProcesses().stream().filter( t -> t.getAapProducts() != null && !t.getAapProducts().isEmpty())
                    .map(t -> t.getAapProducts().stream().mapToDouble(r -> r.getUnitQuantity() == null ? 0 : r.getUnitQuantity())).mapToDouble(DoubleStream::sum).sum();
        }

        if(unitQuantity == 0 && faCatch.getUnitQuantity() != null){
            unitQuantity = faCatch.getUnitQuantity();
        }
        return unitQuantity;
    }

    private double calculateWeight(FaCatchEntity faCatch){
        double weight = 0;

        if(faCatch.getAapProcesses() != null && !faCatch.getAapProcesses().isEmpty() ) {

            weight = faCatch.getAapProcesses().stream()
                    .filter(t -> t.getAapProducts() != null && !t.getAapProducts().isEmpty())
                    .map(t -> t.getAapProducts().stream().mapToDouble(r -> r.getWeightMeasure() == null ? 0 : r.getWeightMeasure())).mapToDouble(DoubleStream::sum).sum();
        }

        if(weight == 0 && faCatch.getWeightMeasure() != null){
            weight = faCatch.getWeightMeasure();
        }
        return weight;
    }


    public Map<String, Map<String,  List<FishingActivityEntity>>> groupBySpeciesAndPresentation(List<FishingActivityEntity> faList){
        if(faList == null || faList.isEmpty()){
            return  new HashMap<>();
        }
        //The order of the mapping is as follows: species, presentation
        Map<String, Map<String, List<FishingActivityEntity>>> bySpecies = new HashMap<>();
        for (FishingActivityEntity fa : faList) {
            for (FaCatchEntity faCatch : fa.getFaCatchs()) {
                Map<String, List<FishingActivityEntity>> byPresentation = bySpecies.computeIfAbsent(faCatch.getSpeciesCode(), k -> new HashMap<>());
                for(AapProcessEntity processEntity : faCatch.getAapProcesses()){
                    for(AapProcessCodeEntity codeEntity : processEntity.getAapProcessCode()) {
                        if(!FISH_PRESENTATION.equals(codeEntity.getTypeCodeListId())){
                            continue;
                        }

                        if (byPresentation.get(codeEntity.getTypeCode()) == null) {
                            byPresentation.put(codeEntity.getTypeCode(), new ArrayList<>(Arrays.asList(fa)));
                        } else {
                            //originally instead of a list the implementation was using a Set but the results were "weird". The equals and hashcode are broken because of "@EqualsAndHashCode(of = {"occurence"})" FishingActivityEntity.
                            // Trying a workaround, till the problem is investigated further.
                            if(isElementUnique(byPresentation,fa.getId(),codeEntity.getTypeCode())) {
                                byPresentation.get(codeEntity.getTypeCode()).add(fa);
                            }
                        }
                    }
                }
            }
        }
        return bySpecies;
    }

    private boolean isElementUnique(Map<String, List<FishingActivityEntity>> byPresentation,int faId,String type){
        List<FishingActivityEntity> fishingActivityEntities = byPresentation.get(type);
        return fishingActivityEntities.stream().noneMatch(t -> t.getId() == faId);
    }

    private List<FACatchModel> createCatchesModel(List<FishingActivityEntity> faList,Map<Integer,Integer> matchGearTypeToFA){

        List<FACatchModel> modelList = new LinkedList<>();//maintain order for display purposes
        Map<String, Map<String, Map<String, List<FishingActivityEntity>>>> activitiesToCatches = groupByDateActivitySpecies(faList);

        int activitiesCounter = 0;
        int speciesCounter = 0;
        for( Map.Entry<String, Map<String, Map<String, List<FishingActivityEntity>>>> activitiesByDate:activitiesToCatches.entrySet()) {
            FACatchModel model = new FACatchModel();
            model.setDate(activitiesByDate.getKey());

            for(Map.Entry<String, Map<String, List<FishingActivityEntity>>> activitiesByType:activitiesByDate.getValue().entrySet()){
                if(activitiesCounter > 0){
                    model = new FACatchModel();
                }
                String activityType = activitiesByType.getKey();
                model.setActivityType(activityType);

                for(Map.Entry<String, List<FishingActivityEntity>> activitiesBySpecies:activitiesByType.getValue().entrySet()){
                    if(speciesCounter > 0){
                        model = new FACatchModel();
                    }
                    model.setSpecies(activitiesBySpecies.getKey());

                    for(FishingActivityEntity fa :activitiesBySpecies.getValue()) {
                        getCatchesWeightAndCountBySpecies(fa.getFaCatchs(),model);
                    }

                    if(CATCHES.equals(activityType)){
                        model.setActivityType(CATCHES);
                        modelList.add(model);
                        calculateFishingTimeLocationAndGears(new ArrayList<>(activitiesBySpecies.getValue()),model,matchGearTypeToFA);
                    }
                    if(DISCARDS.equals(activityType)){
                        model.setActivityType(DISCARDS);
                        modelList.add(model);
                    }

                    if(RETAINED.equals(activityType)){
                        model.setActivityType(RETAINED);
                        modelList.add(model);
                        calculateFishingTimeLocationAndGears(new ArrayList<>(activitiesBySpecies.getValue()),model,matchGearTypeToFA);
                    }

                    speciesCounter++;
                }

                activitiesCounter++;
                speciesCounter = 0;
            }
            activitiesCounter = speciesCounter = 0;
        }
        return modelList;
    }


    public void getCatchesWeightAndCountBySpecies(Set<FaCatchEntity> catches, FACatchModel model){
        double weight,unitQuantity;

       for(FaCatchEntity faCatch: catches){

           if(model.getSpecies() == null || !model.getSpecies().equals(faCatch.getSpeciesCode())){
               continue;
           }

           unitQuantity = faCatch.getUnitQuantity() == null ? 0 : faCatch.getUnitQuantity();
           weight = faCatch.getWeightMeasure() == null ? 0 : faCatch.getWeightMeasure();

           if(LSC.equals(faCatch.getFishClassCode())) {
               model.setWeightLSC(model.getWeightLSC() + weight);
               model.setNbLSC(model.getNbLSC() +unitQuantity);
           }
           if(BMS.equals(faCatch.getFishClassCode())){
               model.setWeightBMS(model.getWeightBMS() + weight);
               model.setNbBMS(model.getNbBMS() + weight);
           }
        }
    }


    private void calculateFishingTimeLocationAndGears(List<FishingActivityEntity> activityEntities, FACatchModel model,Map<Integer,Integer> matchGearTypeToFA){
       Set<String> gears = new HashSet<>();
        for(FishingActivityEntity activity:activityEntities) {
            if(activity.getCalculatedOperationQuantity() != null) {
                model.setFop(model.getFop() + activity.getCalculatedOperationQuantity());
            }
            if(matchGearTypeToFA.get(activity.getId()) !=null ){
                gears.add(String.valueOf(matchGearTypeToFA.get(activity.getId())));
            }
            if ((activity.getFluxCharacteristics() != null) && !activity.getFluxCharacteristics().isEmpty()) {
                model.setDepth(getFishingDepth(activity));
            }

        }
        calculateGears(activityEntities,model);
        calculateCatchesAreas(getLocationsForCatches(activityEntities),model);
        if(!gears.isEmpty()) {
            model.setGear(String.join(",", gears));
        }
    }

    private Map<String, List<FluxLocationEntity>> getLocationsForCatches(List<FishingActivityEntity> activityEntities){
        return activityEntities.stream().flatMap(t -> t.getFaCatchs().stream()).collect(Collectors.toSet())
                .stream().flatMap(t -> t.getFluxLocations().stream()).filter( r -> r.getFluxLocationIdentifierSchemeId() != null)
                .collect(Collectors.groupingBy(FluxLocationEntity::getFluxLocationIdentifierSchemeId))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, List<FluxLocationEntity>> getLocationsForTranshipmentsLanding(List<FishingActivityEntity> activityEntities){
         return activityEntities.stream().filter( t-> t.getFluxLocations() != null && !t.getFluxLocations().isEmpty())
                 .flatMap(z ->z.getFluxLocations().stream()).collect(Collectors.toSet()).stream().filter( r -> r.getFluxLocationIdentifierSchemeId() != null)
                 .collect(Collectors.groupingBy(FluxLocationEntity::getFluxLocationIdentifierSchemeId))
                 .entrySet().stream()
                 .filter(e -> e.getValue().size() > 1)
                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void calculateTranshipmentLandingAreas(Map<String, List<FluxLocationEntity>> locationType,TranshipmentLandingModel model){
        model.setFaoArea(locationType.get(FAO_AREA) == null ? null:locationType.get(FAO_AREA).get(0).getFluxLocationIdentifier());
        model.setGfcmArea(locationType.get(GFCM_GSA) == null ? null:locationType.get(GFCM_GSA).get(0).getFluxLocationIdentifier());
        model.setStatRect(locationType.get(STAT_RECTANGLE) == null ? null:locationType.get(STAT_RECTANGLE).get(0).getFluxLocationIdentifier());
        model.setEez(locationType.get(TERRITORY) == null ? null:locationType.get(TERRITORY).get(0).getFluxLocationIdentifier());
    }

    private void calculateCatchesAreas(Map<String, List<FluxLocationEntity>> locationType, FACatchModel model){
        model.setFaoArea(locationType.get(FAO_AREA) == null ? null:locationType.get(FAO_AREA).get(0).getFluxLocationIdentifier());
        model.setGfcmArea(locationType.get(GFCM_GSA) == null ? null:locationType.get(GFCM_GSA).get(0).getFluxLocationIdentifier());
        model.setStatRect(locationType.get(STAT_RECTANGLE) == null ? null:locationType.get(STAT_RECTANGLE).get(0).getFluxLocationIdentifier());
        model.setEez(locationType.get(TERRITORY) == null ? null:locationType.get(TERRITORY).get(0).getFluxLocationIdentifier());
        String position = formatCoordinates(locationType.get(POSITION) == null? null: locationType.get(POSITION).get(0));
        model.setPosition(position == null ? formatCoordinates(locationType.get(LOCATION) == null ? null: locationType.get(LOCATION).get(0)) : position);
    }

    private void calculateGears(List<FishingActivityEntity> activityEntities,FACatchModel model){


        List<FishingActivityEntity> relatedFA = activityEntities.stream().filter(l -> l.getRelatedFishingActivity() != null).map(FishingActivityEntity::getRelatedFishingActivity).collect(Collectors.toList());
        if(relatedFA.isEmpty()){
            return ;
        }
        Date gearShot = null;
        Date gearRetrieval = null;
        //I could have many for a given date, and maybe another gear they are under related fa.
        for(FishingActivityEntity fa :relatedFA){
            if(GEAR_RETRIEVAL.equals(fa.getTypeCode())){
                gearRetrieval = fa.getOccurence();
            }
            if(GEAR_SHOT.equals(fa.getTypeCode())){
                gearShot = fa.getOccurence();
            }
        }

        if(gearShot != null){
            model.setGearSet(getPartOfDate(gearShot));
        }
        if(gearRetrieval != null) {
            model.setGearHaul(getPartOfDate(gearRetrieval));
        }

        if(gearShot != null && gearRetrieval!=null) {
            long diff = gearRetrieval.getTime() - gearShot.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            model.setTotalMin(diffMinutes);
        }
    }

    private String getPartOfDate(Date date){
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":"+String.valueOf(calendar.get(Calendar.MINUTE));
    }

    public Map<String, Map<String, Map<String, List<FishingActivityEntity>>>> groupByDateActivitySpecies(List<FishingActivityEntity> faList){
        if(faList == null || faList.isEmpty()){
            return  new HashMap<>();
        }
            //The order of the mapping is as follows: date, activityType and species
            Map<String, Map<String, Map<String, List<FishingActivityEntity>>>> byDate = new HashMap<>();
            for (FishingActivityEntity fa : faList) {
                Map<String, Map<String, List<FishingActivityEntity>>> byActivity = byDate.computeIfAbsent(formatDay(fa.getCalculatedStartTime()), k -> new HashMap<>());
                for (FaCatchEntity faCatch : fa.getFaCatchs()) {
                    Map<String, List<FishingActivityEntity>> byType = byActivity.computeIfAbsent(decideTypeOfActivity(faCatch.getTypeCode(),fa.getTypeCode()), k -> new HashMap<>());

                    if(byType.get(faCatch.getSpeciesCode()) == null) {
                        byType.put(faCatch.getSpeciesCode(), new ArrayList<>(Arrays.asList(fa)));//arrays.asList returns a fixed size list, wrapper fixes that
                    } else {
                        //originally instead of a list the implementation was using a Set but the results were "weird". The equals and hashcode are broken because of "@EqualsAndHashCode(of = {"occurence"})" FishingActivityEntity.
                        // Trying a workaround, till the problem is investigated further.
                        if(isElementUnique(byType,fa.getId(),(faCatch.getSpeciesCode()))) {
                            byType.get(faCatch.getSpeciesCode()).add(fa);
                        }
                    }
                }
            }
            return byDate;
    }

    private String decideTypeOfActivity(String catchType,String faType){

        FishingActivityTypeEnum fishingActivityTypeEnum = FishingActivityTypeEnum.valueOf(faType);
        FaCatchTypeEnum faCatchTypeEnum = FaCatchTypeEnum.valueOf(catchType);

        if(fishingActivityTypeEnum.equals(FishingActivityTypeEnum.DEPARTURE) && faCatchTypeEnum.equals(FaCatchTypeEnum.ONBOARD)){
            return RETAINED;
        }

        if(fishingActivityTypeEnum.equals(FishingActivityTypeEnum.FISHING_OPERATION) ||
                fishingActivityTypeEnum.equals(FishingActivityTypeEnum.JOINT_FISHING_OPERATION) ||
                fishingActivityTypeEnum.equals(FishingActivityTypeEnum.DISCARD)){
            if(faCatchTypeEnum.equals(FaCatchTypeEnum.DISCARDED)
                    || faCatchTypeEnum.equals(FaCatchTypeEnum.DEMINIMIS)){
                return DISCARDS;
            }
        }

        if(fishingActivityTypeEnum.equals(FishingActivityTypeEnum.JOINT_FISHING_OPERATION) && faCatchTypeEnum.equals(FaCatchTypeEnum.UNLOADED)){
            return DISCARDS;
        }

        if(fishingActivityTypeEnum.equals(FishingActivityTypeEnum.FISHING_OPERATION)
                || fishingActivityTypeEnum.equals(FishingActivityTypeEnum.JOINT_FISHING_OPERATION)){
                return CATCHES;
        }

        if(faCatchTypeEnum.equals(FaCatchTypeEnum.ONBOARD)
                || faCatchTypeEnum.equals(FaCatchTypeEnum.KEPT_IN_NET)){
            return CATCHES;
        }

        if(fishingActivityTypeEnum.equals(FishingActivityTypeEnum.RELOCATION) && faCatchTypeEnum.equals(FaCatchTypeEnum.LOADED) ){
                return CATCHES;
        }


        return UNKNOWN;
    }


    public String formatDay(Date date){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        return fmt.format(date);
    }


    private double getFishingDepth(FishingActivityEntity activity){
        Set<FluxCharacteristicEntity> characteristics = activity.getFluxCharacteristics();
        for(FluxCharacteristicEntity characteristic: characteristics){
            if(FISHING_DEPTH.equals(characteristic.getTypeCode()) && FA_CHARACTERISTIC.equals(characteristic.getTypeCodeListId())){
                return characteristic.getValueMeasure() == null ? 0:characteristic.getValueMeasure();
            }
        }

        if(activity.getRelatedFishingActivity() != null ){
            for(FluxCharacteristicEntity characteristic: activity.getRelatedFishingActivity().getFluxCharacteristics()){
                if(FISHING_DEPTH.equals(characteristic.getTypeCode()) && FA_CHARACTERISTIC.equals(characteristic.getTypeCodeListId())){
                    return characteristic.getValueMeasure() == null ? 0:characteristic.getValueMeasure();
                }
            }
        }
        return 0;
    }

    private HashMap<String,String> getIdentifiers(FishingActivityEntity fishingActivityEntity){
        Set<VesselIdentifierEntity> identifierEntitySet = new HashSet<>();
        fishingActivityEntity.getFaReportDocument().getVesselTransportMeans().forEach( z-> identifierEntitySet.addAll(z.getVesselIdentifiers()));

        HashMap<String,String> identifierMap = new HashMap<>();

        for(VesselIdentifierEntity vesselIdentifier:identifierEntitySet){
            if(CFR.equals(vesselIdentifier.getVesselIdentifierSchemeId()) && vesselIdentifier.getVesselIdentifierId()!= null){
                identifierMap.put(CFR,vesselIdentifier.getVesselIdentifierId());
            }
            if(GFCM.equals(vesselIdentifier.getVesselIdentifierSchemeId()) && vesselIdentifier.getVesselIdentifierId()!= null){
                identifierMap.put(GFCM,"GFCM:"+vesselIdentifier.getVesselIdentifierId());
            }
            if(ICCAT.equals(vesselIdentifier.getVesselIdentifierSchemeId()) && vesselIdentifier.getVesselIdentifierId()!= null){
                identifierMap.put(ICCAT,"ICCAT:"+vesselIdentifier.getVesselIdentifierId());
            }
        }

        return identifierMap;
    }


    private List<PortLogBookModel> retrievePortLogBookModel(List<FishingActivityEntity> activities){
        List<PortLogBookModel> logBookModelList = new ArrayList<>();
        //Departure
        FishingActivityEntity firstDepartureForTrip = FishingTripIdWithGeometryMapper.getFirstDepartureForTrip(activities);
        fillPortLogBookModel(firstDepartureForTrip,logBookModelList);
        //Transhipment
        List<FishingActivityEntity> transhipmentsForTrip = FishingTripIdWithGeometryMapper.getTranshipmentsForTrip(activities);
        transhipmentsForTrip.forEach( f-> fillPortLogBookModel(f,logBookModelList));
        //Landing
        List<FishingActivityEntity> landingsForTrip = FishingTripIdWithGeometryMapper.getLandingsForTrip(activities);
        landingsForTrip.forEach(f ->fillPortLogBookModel(f,logBookModelList));
        return logBookModelList;
    }

    private void fillPortLogBookModel(FishingActivityEntity fa, List<PortLogBookModel> logBookModelList) {

        if(fa == null){
            return;
        }

        PortLogBookModel portLogBookModel = new PortLogBookModel();
        portLogBookModel.setActivityType(fa.getTypeCode());

        FluxLocationEntity fluxLocationEntity = fa.getFluxLocations() == null || fa.getFluxLocations().isEmpty() ? null : fa.getFluxLocations().stream().filter(r -> LOCATION.equals(r.getTypeCode())).findFirst().orElse(null);
        String portName = getPortName(fluxLocationEntity);
        portLogBookModel.setPort(portName == null ? formatCoordinates(fluxLocationEntity):portName);
        portLogBookModel.setDate(fa.getCalculatedStartTime());
        portLogBookModel.setId(fa.getId());
        logBookModelList.add(portLogBookModel);
    }

    private String getPortName(FluxLocationEntity fluxLocationEntity) {

        if(fluxLocationEntity == null || fluxLocationEntity.getTypeCode() == null){
            return null;
        }

        try {
            Map<String,String> codeDescription = mdrModuleServiceBean.getPortDescriptionFromMdr(LOCATION,fluxLocationEntity.getFluxLocationIdentifier());
            return codeDescription.get(fluxLocationEntity.getFluxLocationIdentifier())+"("+fluxLocationEntity.getFluxLocationIdentifier()+")";
        } catch (ServiceException e) {
            e.printStackTrace();
            return null;
        }
    }

     private String formatCoordinates(FluxLocationEntity fluxLocationEntity){
        if(fluxLocationEntity == null || fluxLocationEntity.getLongitude() == null || fluxLocationEntity.getLatitude() == null){
            return null;
        }
        DecimalFormat df = new DecimalFormat("###.######");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(fluxLocationEntity.getLongitude())+"/" + df.format(fluxLocationEntity.getLatitude());
    }

    public String calculateDuration(Long millis) {

        if(millis == 0){
            return "";
        }

        long seconds = millis / 1000;
        long min = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;
        StringBuilder sb  = new StringBuilder();
        if(days != 0) {
            sb.append(days);
            sb.append("d ");
        }
        if(hours != 0) {
            sb.append(hours);
            sb.append("h ");
        }
        sb.append(min);
        sb.append("m");
        return sb.toString();
    }
}
