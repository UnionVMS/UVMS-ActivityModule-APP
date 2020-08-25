package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.ers.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.ers.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.ers.service.dto.FACatchModel;
import eu.europa.ec.fisheries.ers.service.dto.GearMapperModel;
import eu.europa.ec.fisheries.ers.service.dto.TranshipmentLandingModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.doReturn;


public class JasperReportServiceBeanTest {

    JasperReportServiceBean bean;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bean = new JasperReportServiceBean();
    }

    @Test
    public void calculateDuration() {
        JasperReportServiceBean bean = new JasperReportServiceBean();
        String duration = bean.calculateDuration(3720000l);
        Assert.assertEquals("1h 2m",duration);
    }

    @Test
    public void groupByDateActivitySpecies() {

        List<FishingActivityEntity> faList = new ArrayList<>();
        FishingActivityEntity fa = new FishingActivityEntity();
        Date date = new Date();
        fa.setCalculatedStartTime(date);
        fa.setTypeCode("FISHING_OPERATION");
        Set<FaCatchEntity> catches = new HashSet<>();
        FaCatchEntity catch01 = new FaCatchEntity();
        catch01.setTypeCode("AA");
        catch01.setSpeciesCode("LOV");
        catch01.setTypeCode("ONBOARD");
        catches.add(catch01);
        FaCatchEntity catch02 = new FaCatchEntity();
        catch02.setTypeCode("AA");
        catch02.setSpeciesCode("TAR");
        catch02.setFishClassCode("TARR");
        catch02.setTypeCode("ONBOARD");
        catches.add(catch02);
        FaCatchEntity catch03 = new FaCatchEntity();
        catch03.setTypeCode("BB");
        catch03.setSpeciesCode("TAR");
        catch03.setTypeCode("ONBOARD");
        catches.add(catch03);
        FaCatchEntity catch04 = new FaCatchEntity();
        catch04.setTypeCode("AA");
        catch04.setSpeciesCode("TAR");
        catch04.setTypeCode("ONBOARD");
        catches.add(catch04);

        fa.setFaCatchs(catches);
        faList.add(fa);

        FishingActivityEntity fa1 = new FishingActivityEntity();
        Date date1 = addXDays(new Date(),1);
        fa1.setCalculatedStartTime(date1);
        fa1.setTypeCode("FISHING_OPERATION");
        Set<FaCatchEntity> catches1 = new HashSet<>();
        FaCatchEntity catch11 = new FaCatchEntity();
        catch11.setTypeCode("DD");
        catch11.setSpeciesCode("LOV");
        catch11.setTypeCode("ONBOARD");
        catches1.add(catch11);
        FaCatchEntity catch12 = new FaCatchEntity();
        catch12.setTypeCode("CC");
        catch12.setSpeciesCode("LOV");
        catch12.setTypeCode("ONBOARD");
        catches1.add(catch12);
        FaCatchEntity catch13 = new FaCatchEntity();
        catch13.setTypeCode("AA");
        catch13.setSpeciesCode("SPA");
        catch13.setTypeCode("ONBOARD");
        catches1.add(catch13);
        FaCatchEntity catch14 = new FaCatchEntity();
        catch14.setTypeCode("BB");
        catch14.setSpeciesCode("TAR");
        catch14.setTypeCode("ONBOARD");
        catches1.add(catch14);

        fa1.setFaCatchs(catches1);
        faList.add(fa1);
        Map<String, Map<String, Map<String, List<FishingActivityEntity>>>> stringMapMap = bean.groupByDateActivitySpecies(faList);
        Set<String> keys = stringMapMap.keySet();


        Assert.assertTrue(keys.contains(bean.formatDay(date)));
        Assert.assertTrue(keys.contains(bean.formatDay(date1)));

        Collection<Map<String, Map<String, List<FishingActivityEntity>>>> values = stringMapMap.values();
        for(Map<String, Map<String, List<FishingActivityEntity>>> entry:values){
            Assert.assertTrue(entry.keySet().contains("CATCHES"));
            for(Map<String, List<FishingActivityEntity>> species : entry.values()){

                if(species.keySet().size() == 3){
                    Assert.assertTrue(species.keySet().contains("LOV"));
                    Assert.assertTrue(species.keySet().contains("TAR"));
                    Assert.assertTrue(species.keySet().contains("SPA"));
                }
                if(species.keySet().size() == 2){
                    Assert.assertTrue(species.keySet().contains("LOV"));
                    Assert.assertTrue(species.keySet().contains("TAR"));
                }
            }
        }
    }

    private Date addXDays(Date date,int days){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days );
        return cal.getTime();
    }

    @Test
    public void getMeshSize() {
        Set<GearCharacteristicEntity> characteristics = new HashSet<>();
        GearCharacteristicEntity characteristicEntity1 = new GearCharacteristicEntity();
        characteristicEntity1.setTypeCode("ME");
        characteristicEntity1.setValueMeasure(140.00);
        characteristicEntity1.setValueMeasureUnitCode("MMT");
        characteristics.add(characteristicEntity1);
        GearCharacteristicEntity characteristicEntity2 = new GearCharacteristicEntity();
        characteristicEntity2.setTypeCode("GM");
        characteristicEntity2.setValueMeasure(100.00);
        characteristicEntity2.setValueMeasureUnitCode("MTR");
        characteristics.add(characteristicEntity2);
        GearCharacteristicEntity characteristicEntity3 = new GearCharacteristicEntity();
        characteristicEntity3.setTypeCode("HE");
        characteristicEntity3.setValueMeasure(100.00);
        characteristicEntity3.setValueMeasureUnitCode("MMT");
        characteristics.add(characteristicEntity3);
        JasperReportServiceBean spy = PowerMockito.spy(bean);

        doReturn("100.00 MTR").when(spy).getDimensionValue(characteristicEntity2);
        doReturn("100.00 MMT").when(spy).getDimensionValue(characteristicEntity3);

        Map<String, Set<String>> meshSize = spy.getMeshSize(characteristics);
        Map<String,Set<String>> expectedMap = new HashMap<>();
        Set<String> dimensions = new HashSet<>();
        dimensions.add("HE: 100.00 MMT");
        dimensions.add("GM: 100.00 MTR");
        expectedMap.put("140.0 MMT",dimensions);

        Assert.assertEquals(meshSize,expectedMap);
    }

    @Test
    public void gearMapper() {
        List<GearMapperModel> gearGrouping = new ArrayList<>();
        Map<Integer,Integer> gearTypeOfFA = new HashMap<>();
        FishingActivityEntity fa = new FishingActivityEntity();
        fa.setId(1);
        Set<FishingGearEntity> gearSet = new HashSet<>();
        FishingGearEntity fg = new FishingGearEntity();
        fg.setTypeCode("PS");
        Set<GearCharacteristicEntity> gearCharacteristics = new HashSet<>();
        GearCharacteristicEntity characteristicEntity1 = new GearCharacteristicEntity();
        characteristicEntity1.setTypeCode("ME");
        characteristicEntity1.setValueMeasure(140.00);
        characteristicEntity1.setValueMeasureUnitCode("MMT");
        gearCharacteristics.add(characteristicEntity1);
        GearCharacteristicEntity characteristicEntity2 = new GearCharacteristicEntity();
        characteristicEntity2.setTypeCode("GM");
        characteristicEntity2.setValueMeasure(100.00);
        characteristicEntity2.setValueMeasureUnitCode("MTR");
        gearCharacteristics.add(characteristicEntity2);
        GearCharacteristicEntity characteristicEntity3 = new GearCharacteristicEntity();
        characteristicEntity3.setTypeCode("HE");
        characteristicEntity3.setValueMeasure(100.00);
        characteristicEntity3.setValueMeasureUnitCode("MMT");
        gearCharacteristics.add(characteristicEntity3);
        fg.setGearCharacteristics(gearCharacteristics);
        gearSet.add(fg);
        fa.setFishingGears(gearSet);

        FishingActivityEntity fa1 = new FishingActivityEntity();
        fa1.setId(2);
        Set<FishingGearEntity> gearSet1 = new HashSet<>();
        FishingGearEntity fg1 = new FishingGearEntity();
        fg1.setTypeCode("PS");
        Set<GearCharacteristicEntity> gearCharacteristics1 = new HashSet<>();
        GearCharacteristicEntity characteristicEntity11 = new GearCharacteristicEntity();
        characteristicEntity11.setTypeCode("ME");
        characteristicEntity11.setValueMeasure(140.00);
        characteristicEntity11.setValueMeasureUnitCode("MMT");
        gearCharacteristics1.add(characteristicEntity11);
        GearCharacteristicEntity characteristicEntity12 = new GearCharacteristicEntity();
        characteristicEntity12.setTypeCode("GM");
        characteristicEntity12.setValueMeasure(100.00);
        characteristicEntity12.setValueMeasureUnitCode("MTR");
        gearCharacteristics1.add(characteristicEntity12);
        fg1.setGearCharacteristics(gearCharacteristics1);
        gearSet1.add(fg1);
        fa1.setFishingGears(gearSet1);

        FishingActivityEntity fa2 = new FishingActivityEntity();
        fa2.setId(3);
        Set<FishingGearEntity> gearSet2 = new HashSet<>();
        FishingGearEntity fg2 = new FishingGearEntity();
        fg2.setTypeCode("PS");
        Set<GearCharacteristicEntity> gearCharacteristics2 = new HashSet<>();
        GearCharacteristicEntity characteristicEntity21 = new GearCharacteristicEntity();
        characteristicEntity21.setTypeCode("ME");
        characteristicEntity21.setValueMeasure(140.00);
        characteristicEntity21.setValueMeasureUnitCode("MMT");
        gearCharacteristics2.add(characteristicEntity21);
        GearCharacteristicEntity characteristicEntity22 = new GearCharacteristicEntity();
        characteristicEntity22.setTypeCode("GM");
        characteristicEntity22.setValueMeasure(100.00);
        characteristicEntity22.setValueMeasureUnitCode("MTR");
        gearCharacteristics2.add(characteristicEntity22);
        GearCharacteristicEntity characteristicEntity23 = new GearCharacteristicEntity();
        characteristicEntity23.setTypeCode("BE");
        characteristicEntity23.setValueMeasure(100.00);
        characteristicEntity23.setValueMeasureUnitCode("MMT");
        gearCharacteristics2.add(characteristicEntity23);
        fg2.setGearCharacteristics(gearCharacteristics2);
        gearSet2.add(fg2);
        fa2.setFishingGears(gearSet2);

        FishingActivityEntity fa3 = new FishingActivityEntity();
        fa3.setId(4);
        Set<FishingGearEntity> gearSet3 = new HashSet<>();
        FishingGearEntity fg3 = new FishingGearEntity();
        fg3.setTypeCode("PS");
        Set<GearCharacteristicEntity> gearCharacteristics3 = new HashSet<>();
        GearCharacteristicEntity characteristicEntity31 = new GearCharacteristicEntity();
        characteristicEntity31.setTypeCode("ME");
        characteristicEntity31.setValueMeasure(140.00);
        characteristicEntity31.setValueMeasureUnitCode("MMT");
        gearCharacteristics3.add(characteristicEntity31);
        GearCharacteristicEntity characteristicEntity32 = new GearCharacteristicEntity();
        characteristicEntity32.setTypeCode("GM");
        characteristicEntity32.setValueMeasure(100.00);
        characteristicEntity32.setValueMeasureUnitCode("MTR");
        gearCharacteristics3.add(characteristicEntity32);
        GearCharacteristicEntity characteristicEntity33 = new GearCharacteristicEntity();
        characteristicEntity33.setTypeCode("BE");
        characteristicEntity33.setValueMeasure(100.00);
        characteristicEntity33.setValueMeasureUnitCode("MMT");
        gearCharacteristics2.add(characteristicEntity33);
        fg3.setGearCharacteristics(gearCharacteristics3);
        gearSet3.add(fg3);
        fa3.setFishingGears(gearSet3);

        JasperReportServiceBean spy = PowerMockito.spy(bean);

        doReturn("100.00 MTR").when(spy).getDimensionValue(characteristicEntity2);
        doReturn("100.00 MMT").when(spy).getDimensionValue(characteristicEntity3);
        doReturn("100.00 MTR").when(spy).getDimensionValue(characteristicEntity12);
        doReturn("100.00 MTR").when(spy).getDimensionValue(characteristicEntity22);
        doReturn("100.00 MMT").when(spy).getDimensionValue(characteristicEntity23);
        doReturn("100.00 MTR").when(spy).getDimensionValue(characteristicEntity32);
        doReturn("100.00 MMT").when(spy).getDimensionValue(characteristicEntity33);

        spy.gearMapper(fa,gearGrouping,gearTypeOfFA);
        spy.gearMapper(fa1,gearGrouping,gearTypeOfFA);
        spy.gearMapper(fa2,gearGrouping,gearTypeOfFA);
        spy.gearMapper(fa3,gearGrouping,gearTypeOfFA);

        Set<GearMapperModel> set = new HashSet<>(gearGrouping);
        Assert.assertEquals(set.size(), gearGrouping.size());
        Assert.assertEquals(3, gearGrouping.size());
        Assert.assertEquals(4, gearTypeOfFA.size());
    }

    @Test
    public void testProductWeightAndCountTranshipmentWithMeasurestOnAapProduct() {
        TranshipmentLandingModel model = new TranshipmentLandingModel();
        model.setSpecies("COD");
        Set<FaCatchEntity> catches = new HashSet<>();

        FaCatchEntity catchEntity = new FaCatchEntity();
        catchEntity.setFishClassCode("LSC");
        catchEntity.setSpeciesCode("SAL");
        catchEntity.setTypeCode("UNLOADED");
        Set<AapProcessEntity> processes = new HashSet<>();
        AapProcessEntity process = new AapProcessEntity();

        Set<AapProductEntity> products = new HashSet<>();
        AapProductEntity product = new AapProductEntity();
        product.setWeightMeasure(123.00);
        product.setUnitQuantity(123.0);
        AapProductEntity product1 = new AapProductEntity();
        product1.setWeightMeasure(456.00);
        product1.setUnitQuantity(172.00);
        products.add(product);
        products.add(product1);
        process.setAapProducts(products);
        processes.add(process);
        catchEntity.setAapProcesses(processes);

        FaCatchEntity catchEntity1 = new FaCatchEntity();
        catchEntity1.setTypeCode("UNLOADED");
        catchEntity1.setSpeciesCode("COD");
        catchEntity1.setFishClassCode("LSC");

        Set<AapProcessEntity> processes1 = new HashSet<>();
        AapProcessEntity process1 = new AapProcessEntity();

        Set<AapProductEntity> products1 = new HashSet<>();
        AapProductEntity product2 = new AapProductEntity();
        product2.setWeightMeasure(124.00);
        product2.setUnitQuantity(457.0);
        AapProductEntity product3 = new AapProductEntity();
        product3.setWeightMeasure(457.00);
        product3.setUnitQuantity(77.00);
        products1.add(product2);
        products1.add(product3);
        process1.setAapProducts(products1);
        processes1.add(process1);
        catchEntity1.setAapProcesses(processes1);
        catches.add(catchEntity);
        catches.add(catchEntity1);
        bean.getCatchesWeightAndCountBySpecies(catches,model);

        Assert.assertEquals(581, model.getWeightLSC(), 0.0);
        Assert.assertEquals(534, model.getNbLSC(), 0.0);

        model.setSpecies("SAL");
        model.setWeightLSC(0);
        model.setNbLSC(0);

        bean.getCatchesWeightAndCountBySpecies(catches,model);
        Assert.assertEquals(579, model.getWeightLSC(), 0.0);
        Assert.assertEquals(295, model.getNbLSC(), 0.0);

        Assert.assertEquals(0, model.getWeightBMS(), 0.0);
        Assert.assertEquals(0, model.getNbBMS(), 0.0);
    }

    @Test
    public void testProductWeightAndCountTranshipmentWithMeasuresOnCatch() {
        TranshipmentLandingModel model = new TranshipmentLandingModel();
        model.setSpecies("COD");
        Set<FaCatchEntity> catches = new HashSet<>();

        FaCatchEntity catchEntity = new FaCatchEntity();
        catchEntity.setTypeCode("UNLOADED");
        catchEntity.setSpeciesCode("SAL");
        catchEntity.setWeightMeasure(124.00);
        catchEntity.setUnitQuantity(457.0);
        catchEntity.setFishClassCode("LSC");

        FaCatchEntity catchEntity1 = new FaCatchEntity();
        catchEntity1.setTypeCode("UNLOADED");
        catchEntity1.setSpeciesCode("COD");
        catchEntity1.setWeightMeasure(123.00);
        catchEntity1.setUnitQuantity(123.0);
        catchEntity1.setFishClassCode("BMS");

        FaCatchEntity catchEntity3 = new FaCatchEntity();
        catchEntity3.setTypeCode("UNLOADED");
        catchEntity3.setSpeciesCode("SAL");
        catchEntity3.setWeightMeasure(750.00);
        catchEntity3.setUnitQuantity(23.00);
        catchEntity3.setFishClassCode("BMS");

        FaCatchEntity catchEntity4 = new FaCatchEntity();
        catchEntity4.setTypeCode("UNLOADED");
        catchEntity4.setSpeciesCode("COD");
        catchEntity4.setWeightMeasure(357.00);
        catchEntity4.setUnitQuantity(91.0);
        catchEntity4.setFishClassCode("LSC");


        catches.add(catchEntity);
        catches.add(catchEntity1);
        catches.add(catchEntity3);
        catches.add(catchEntity4);
        bean.getCatchesWeightAndCountBySpecies(catches,model);

        Assert.assertEquals(357.00, model.getWeightLSC(), 0.0);
        Assert.assertEquals(91.0, model.getNbLSC(), 0.0);
        Assert.assertEquals(123, model.getWeightBMS(), 0.0);
        Assert.assertEquals(123, model.getNbBMS(), 0.0);

        model.setSpecies("SAL");
        model.setWeightLSC(0);
        model.setNbLSC(0);
        model.setWeightBMS(0);
        model.setNbBMS(0);

        bean.getCatchesWeightAndCountBySpecies(catches,model);
        Assert.assertEquals(124, model.getWeightLSC(), 0.0);
        Assert.assertEquals(457, model.getNbLSC(), 0.0);
        Assert.assertEquals(750.00, model.getWeightBMS(), 0.0);
        Assert.assertEquals(23.00, model.getNbBMS(), 0.0);
    }

    @Test
    public void getProductWeightAndCountCatches() {
        FACatchModel model = new FACatchModel();
        model.setSpecies("COD");

        Set<FaCatchEntity> catches = new HashSet<>();

        FaCatchEntity catchEntity = new FaCatchEntity();
        catchEntity.setTypeCode("ONBOARD");
        catchEntity.setUnitQuantity(123.0);
        catchEntity.setWeightMeasure(444.0);
        catchEntity.setSpeciesCode("SAL");
        catchEntity.setFishClassCode("LSC");

        FaCatchEntity catchEntity1 = new FaCatchEntity();
        catchEntity1.setTypeCode("ONBOARD");
        catchEntity1.setUnitQuantity(457.0);
        catchEntity1.setWeightMeasure(356.0);
        catchEntity1.setSpeciesCode("COD");
        catchEntity1.setFishClassCode("LSC");

        FaCatchEntity catchEntity2 = new FaCatchEntity();
        catchEntity2.setTypeCode("ONBOARD");
        catchEntity2.setUnitQuantity(200.0);
        catchEntity2.setWeightMeasure(200.0);
        catchEntity2.setSpeciesCode("COD");
        catchEntity2.setFishClassCode("LSC");

        catches.add(catchEntity);
        catches.add(catchEntity1);
        catches.add(catchEntity2);
        bean.getCatchesWeightAndCountBySpecies(catches,model);

        Assert.assertEquals(556, model.getWeightLSC(), 0.0);
        Assert.assertEquals(657, model.getNbLSC(), 0.0);
        Assert.assertEquals(0, model.getWeightBMS(), 0.0);
        Assert.assertEquals(0, model.getNbBMS(), 0.0);
    }

}