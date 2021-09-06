package eu.europa.ec.fisheries.ers.service.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.ers.fa.utils.IdentifierSourceEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.util.ActivityDataUtil;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetId;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test class for {@link ActivityIdentifiersEnricherBean}
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityIdentifiersEnricherBeanTest {

    @InjectMocks
    ActivityIdentifiersEnricherBean sut;
    
    @Mock
    private AssetModuleService assetService;
    
    @Test
    @SneakyThrows
    public void testEnrichmentVesselTransportMeansAndAssetIsEmpty() {
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity("LANDING", "FLUX_FA_TYPE" , DateUtils.parseToUTCDate("2014-05-27 07:47:31","yyyy-MM-dd HH:mm:ss"), "FISHING", "FIS", null,null);

        VesselTransportMeansEntity vesselTransportMeansEntity = ActivityDataUtil.getVesselTransportMeansEntity("PAIR_FISHING_PARTNER", "FA_VESSEL_ROLE", "vesselGroup1", null);
        vesselTransportMeansEntity.setVesselIdentifiers(Collections.emptySet());

        FluxReportDocumentEntity fluxReportDocumentEntity1=   ActivityDataUtil.getFluxReportDocumentEntity("FLUX_REPORT_DOCUMENT1",null, DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"),
                "PURPOSE", "PURPOSE_CODE_LIST",null, "OWNER_FLUX_ID1","flux1");

        FaReportDocumentEntity faReportDocumentEntity =  ActivityDataUtil.getFaReportDocumentEntity("Declaration" , "FLUX_FA_REPORT_TYPE", DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"), fluxReportDocumentEntity1,
                vesselTransportMeansEntity, FaReportStatusType.NEW);
        Asset asset = new Asset();
        AssetId assetId = new AssetId();
        assetId.setGuid("guid");
        asset.setAssetId(assetId);
        when(assetService.getAssetsHavingAtLeastOneIdentifier(vesselTransportMeansEntity.getVesselIdentifiers())).thenReturn(Collections.singletonList(asset));
        sut.enrichActivityIdentifiers(fishingActivityEntity, vesselTransportMeansEntity, faReportDocumentEntity);
        
        assertNull(fishingActivityEntity.getCfr());
        assertNull(fishingActivityEntity.getCfrAlt());
        assertNull(fishingActivityEntity.getCfrSrc());

        assertNull(fishingActivityEntity.getIrcs());
        assertNull(fishingActivityEntity.getIrcsAlt());
        assertNull(fishingActivityEntity.getIrcsSrc());

        assertNull(fishingActivityEntity.getUvi());
        assertNull(fishingActivityEntity.getUviAlt());
        assertNull(fishingActivityEntity.getUviSrc());

        assertNull(fishingActivityEntity.getIccat());
        assertNull(fishingActivityEntity.getIccatAlt());
        assertNull(fishingActivityEntity.getIccatSrc());

        assertNull(fishingActivityEntity.getGfcm());
        assertNull(fishingActivityEntity.getGfcmAlt());
        assertNull(fishingActivityEntity.getGfcmSrc());

        assertNull(fishingActivityEntity.getExtMark());
        assertNull(fishingActivityEntity.getExtMarkAlt());
        assertNull(fishingActivityEntity.getExtMarkSrc());
    }
    
    @Test
    @SneakyThrows
    public void testEnrichmentAssetIsEmpty() {
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity("LANDING", "FLUX_FA_TYPE" , DateUtils.parseToUTCDate("2014-05-27 07:47:31","yyyy-MM-dd HH:mm:ss"), "FISHING", "FIS", null,null);

        VesselTransportMeansEntity vesselTransportMeansEntity = ActivityDataUtil.getVesselTransportMeansEntity("PAIR_FISHING_PARTNER", "FA_VESSEL_ROLE", "vesselGroup1", null);
        vesselTransportMeansEntity.setVesselIdentifiers(new HashSet<>(Arrays.asList(
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "CFR123456789", VesselIdentifierSchemeIdEnum.CFR.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedIrcs", VesselIdentifierSchemeIdEnum.IRCS.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedUvi", VesselIdentifierSchemeIdEnum.UVI.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedIccat", VesselIdentifierSchemeIdEnum.ICCAT.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedGfcm", VesselIdentifierSchemeIdEnum.GFCM.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedExtMark", VesselIdentifierSchemeIdEnum.EXT_MARK.value()))));

        FluxReportDocumentEntity fluxReportDocumentEntity1=   ActivityDataUtil.getFluxReportDocumentEntity("FLUX_REPORT_DOCUMENT1",null, DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"),
                "PURPOSE", "PURPOSE_CODE_LIST",null, "OWNER_FLUX_ID1","flux1");

        FaReportDocumentEntity faReportDocumentEntity =  ActivityDataUtil.getFaReportDocumentEntity("Declaration" , "FLUX_FA_REPORT_TYPE", DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"), fluxReportDocumentEntity1,
                vesselTransportMeansEntity, FaReportStatusType.NEW);


        Asset asset = new Asset();
        AssetId assetId = new AssetId();
        assetId.setGuid("guid");
        asset.setAssetId(assetId);
        when(assetService.getAssetsHavingAtLeastOneIdentifier(vesselTransportMeansEntity.getVesselIdentifiers())).thenReturn(Collections.singletonList(asset));
        sut.enrichActivityIdentifiers(fishingActivityEntity, vesselTransportMeansEntity, faReportDocumentEntity);
        assertEquals("CFR123456789", fishingActivityEntity.getCfr());
        assertNull(fishingActivityEntity.getCfrAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getCfrSrc());
        
        assertEquals("reportedIrcs", fishingActivityEntity.getIrcs());
        assertNull(fishingActivityEntity.getIrcsAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getIrcsSrc());
        
        assertEquals("reportedUvi", fishingActivityEntity.getUvi());
        assertNull(fishingActivityEntity.getUviAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getUviSrc());
        
        assertEquals("reportedIccat", fishingActivityEntity.getIccat());
        assertNull(fishingActivityEntity.getIccatAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getIccatSrc());
        
        assertEquals("reportedGfcm", fishingActivityEntity.getGfcm());
        assertNull(fishingActivityEntity.getGfcmAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getGfcmSrc());
        
        assertEquals("reportedExtMark", fishingActivityEntity.getExtMark());
        assertNull(fishingActivityEntity.getExtMarkAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getExtMarkSrc());
    }
    
    @Test
    @SneakyThrows
    public void testEnrichmentVesselTransportMeansAndAssetHaveIdentifiers() {
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity("LANDING", "FLUX_FA_TYPE" , DateUtils.parseToUTCDate("2014-05-27 07:47:31","yyyy-MM-dd HH:mm:ss"), "FISHING", "FIS", null,null);

        VesselTransportMeansEntity vesselTransportMeansEntity = ActivityDataUtil.getVesselTransportMeansEntity("PAIR_FISHING_PARTNER", "FA_VESSEL_ROLE", "vesselGroup1", null);
        vesselTransportMeansEntity.setVesselIdentifiers(new HashSet<>(Arrays.asList(
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "CFR123456789", VesselIdentifierSchemeIdEnum.CFR.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedIrcs", VesselIdentifierSchemeIdEnum.IRCS.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedUvi", VesselIdentifierSchemeIdEnum.UVI.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedIccat", VesselIdentifierSchemeIdEnum.ICCAT.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedGfcm", VesselIdentifierSchemeIdEnum.GFCM.value()),
                                                                new VesselIdentifierEntity(0, vesselTransportMeansEntity, "reportedExtMark", VesselIdentifierSchemeIdEnum.EXT_MARK.value()))));

        FluxReportDocumentEntity fluxReportDocumentEntity1=   ActivityDataUtil.getFluxReportDocumentEntity("FLUX_REPORT_DOCUMENT1",null, DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"),
                "PURPOSE", "PURPOSE_CODE_LIST",null, "OWNER_FLUX_ID1","flux1");

        FaReportDocumentEntity faReportDocumentEntity =  ActivityDataUtil.getFaReportDocumentEntity("Declaration" , "FLUX_FA_REPORT_TYPE", DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"), fluxReportDocumentEntity1,
                vesselTransportMeansEntity, FaReportStatusType.NEW);


        Asset asset = new Asset();
        asset.setCfr("assetCfr");
        asset.setIrcs("assetIrcs");
        asset.setUvi("assetUvi");
        asset.setIccat("assetIccat");
        asset.setGfcm("assetGfcm");
        asset.setExternalMarking("assetExtMark");
        AssetId assetId = new AssetId();
        assetId.setGuid("guid");
        asset.setAssetId(assetId);
        when(assetService.getAssetsHavingAtLeastOneIdentifier(vesselTransportMeansEntity.getVesselIdentifiers())).thenReturn(Collections.singletonList(asset));
        sut.enrichActivityIdentifiers(fishingActivityEntity, vesselTransportMeansEntity, faReportDocumentEntity);
        assertEquals("CFR123456789", fishingActivityEntity.getCfr());
        assertEquals("assetCfr", fishingActivityEntity.getCfrAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getCfrSrc());
        
        assertEquals("reportedIrcs", fishingActivityEntity.getIrcs());
        assertEquals("assetIrcs", fishingActivityEntity.getIrcsAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getIrcsSrc());
        
        assertEquals("reportedUvi", fishingActivityEntity.getUvi());
        assertEquals("assetUvi", fishingActivityEntity.getUviAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getUviSrc());
        
        assertEquals("reportedIccat", fishingActivityEntity.getIccat());
        assertEquals("assetIccat", fishingActivityEntity.getIccatAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getIccatSrc());
        
        assertEquals("reportedGfcm", fishingActivityEntity.getGfcm());
        assertEquals("assetGfcm", fishingActivityEntity.getGfcmAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getGfcmSrc());
        
        assertEquals("reportedExtMark", fishingActivityEntity.getExtMark());
        assertEquals("assetExtMark", fishingActivityEntity.getExtMarkAlt());
        assertEquals(IdentifierSourceEnum.MESSAGE, fishingActivityEntity.getExtMarkSrc());
    }
    
    @Test
    @SneakyThrows
    public void testEnrichmentEmptyVesselTransportMeansAndAssetHasIdentifiers() {
        FishingActivityEntity fishingActivityEntity = ActivityDataUtil.getFishingActivityEntity("LANDING", "FLUX_FA_TYPE" , DateUtils.parseToUTCDate("2014-05-27 07:47:31","yyyy-MM-dd HH:mm:ss"), "FISHING", "FIS", null,null);

        VesselTransportMeansEntity vesselTransportMeansEntity = ActivityDataUtil.getVesselTransportMeansEntity("PAIR_FISHING_PARTNER", "FA_VESSEL_ROLE", "vesselGroup1", null);
        vesselTransportMeansEntity.setVesselIdentifiers(Collections.emptySet());

        FluxReportDocumentEntity fluxReportDocumentEntity1=   ActivityDataUtil.getFluxReportDocumentEntity("FLUX_REPORT_DOCUMENT1",null, DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"),
                "PURPOSE", "PURPOSE_CODE_LIST",null, "OWNER_FLUX_ID1","flux1");

        FaReportDocumentEntity faReportDocumentEntity =  ActivityDataUtil.getFaReportDocumentEntity("Declaration" , "FLUX_FA_REPORT_TYPE", DateUtils.parseToUTCDate("2016-06-27 07:47:31","yyyy-MM-dd HH:mm:ss"), fluxReportDocumentEntity1,
                vesselTransportMeansEntity, FaReportStatusType.NEW);

        Asset asset = new Asset();
        asset.setCfr("assetCfr");
        asset.setIrcs("assetIrcs");
        asset.setUvi("assetUvi");
        asset.setIccat("assetIccat");
        asset.setGfcm("assetGfcm");
        asset.setExternalMarking("assetExtMark");
        AssetId assetId = new AssetId();
        assetId.setGuid("guid");
        asset.setAssetId(assetId);
        when(assetService.getAssetsHavingAtLeastOneIdentifier(vesselTransportMeansEntity.getVesselIdentifiers())).thenReturn(Collections.singletonList(asset));
        sut.enrichActivityIdentifiers(fishingActivityEntity, vesselTransportMeansEntity, faReportDocumentEntity);
        assertEquals("assetCfr", fishingActivityEntity.getCfr());
        assertNull(fishingActivityEntity.getCfrAlt());
        assertEquals(IdentifierSourceEnum.ASSET, fishingActivityEntity.getCfrSrc());
        
        assertEquals("assetIrcs", fishingActivityEntity.getIrcs());
        assertNull(fishingActivityEntity.getIrcsAlt());
        assertEquals(IdentifierSourceEnum.ASSET, fishingActivityEntity.getIrcsSrc());
        
        assertEquals("assetUvi", fishingActivityEntity.getUvi());
        assertNull(fishingActivityEntity.getUviAlt());
        assertEquals(IdentifierSourceEnum.ASSET, fishingActivityEntity.getUviSrc());
        
        assertEquals("assetIccat", fishingActivityEntity.getIccat());
        assertNull(fishingActivityEntity.getIccatAlt());
        assertEquals(IdentifierSourceEnum.ASSET, fishingActivityEntity.getIccatSrc());
        
        assertEquals("assetGfcm", fishingActivityEntity.getGfcm());
        assertNull(fishingActivityEntity.getGfcmAlt());
        assertEquals(IdentifierSourceEnum.ASSET, fishingActivityEntity.getGfcmSrc());
        
        assertEquals("assetExtMark", fishingActivityEntity.getExtMark());
        assertNull(fishingActivityEntity.getExtMarkAlt());
        assertEquals(IdentifierSourceEnum.ASSET, fishingActivityEntity.getExtMarkSrc());
    }

}