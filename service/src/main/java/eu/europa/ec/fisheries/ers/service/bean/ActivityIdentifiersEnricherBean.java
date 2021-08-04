/*
 *
 *  Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2020.
 *
 *  This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 *  the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 *  details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package eu.europa.ec.fisheries.ers.service.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.Map;

import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.fa.utils.IdentifierSourceEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import lombok.extern.slf4j.Slf4j;

/**
 * Class used for enriching instances fo FishingActivityEntity with available identifiers.
 */
@ApplicationScoped
@Slf4j
public class ActivityIdentifiersEnricherBean {
    
    AssetModuleService assetService;
    
    @Inject
    public ActivityIdentifiersEnricherBean(AssetModuleService assetService) {
        this.assetService = assetService;
    }

    /**
     * Constructor for frameworks.
     */
    @SuppressWarnings("unused")
    public ActivityIdentifiersEnricherBean() {
        //NOOP
    }
    
    public void enrichActivityIdentifiers(FishingActivityEntity fishingActivityEntity, VesselTransportMeansEntity vesselTransportMeansEntity, FaReportDocumentEntity faReportDocumentEntity) {
        try {
            Asset asset = assetService.getAssetGuidByIdentifierPrecedence(vesselTransportMeansEntity, faReportDocumentEntity);
            Map<VesselIdentifierSchemeIdEnum, String> existingIds = vesselTransportMeansEntity.getVesselIdentifiersMap();
            if(asset != null) {
                vesselTransportMeansEntity.setGuid(asset.getAssetId().getGuid());

                String cfr = existingIds.get(VesselIdentifierSchemeIdEnum.CFR);
                String altCfr = asset.getCfr();
                if(cfr != null) {
                    fishingActivityEntity.setCfr(cfr);
                    fishingActivityEntity.setCfrAlt(altCfr);
                    fishingActivityEntity.setCfrSrc(IdentifierSourceEnum.MESSAGE);
                } else if(altCfr != null) {
                    fishingActivityEntity.setCfr(altCfr);
                    fishingActivityEntity.setCfrSrc(IdentifierSourceEnum.ASSET);
                }
                String ircs = existingIds.get(VesselIdentifierSchemeIdEnum.IRCS);
                String altIrcs = asset.getIrcs();
                if(ircs != null) {
                    fishingActivityEntity.setIrcs(ircs);
                    fishingActivityEntity.setIrcsAlt(altIrcs);
                    fishingActivityEntity.setIrcsSrc(IdentifierSourceEnum.MESSAGE);
                } else if(altIrcs != null) {
                    fishingActivityEntity.setIrcs(altIrcs);
                    fishingActivityEntity.setIrcsSrc(IdentifierSourceEnum.ASSET);
                }
                String extMark = existingIds.get(VesselIdentifierSchemeIdEnum.EXT_MARK);
                String altExtMark  = asset.getExternalMarking();
                if(extMark != null) {
                    fishingActivityEntity.setExtMark(extMark);
                    fishingActivityEntity.setExtMarkAlt(altExtMark);
                    fishingActivityEntity.setExtMarkSrc(IdentifierSourceEnum.MESSAGE);
                } else if(altExtMark != null) {
                    fishingActivityEntity.setExtMark(altExtMark);
                    fishingActivityEntity.setExtMarkSrc(IdentifierSourceEnum.ASSET);
                }
                String uvi = existingIds.get(VesselIdentifierSchemeIdEnum.UVI);
                String altUvi  = asset.getUvi();
                if (uvi != null) {
                    fishingActivityEntity.setUvi(uvi);
                    fishingActivityEntity.setUviAlt(altUvi);
                    fishingActivityEntity.setUviSrc(IdentifierSourceEnum.MESSAGE);
                } else if(altUvi != null) {
                    fishingActivityEntity.setUvi(altUvi);
                    fishingActivityEntity.setUviSrc(IdentifierSourceEnum.ASSET);
                }
                String iccat = existingIds.get(VesselIdentifierSchemeIdEnum.ICCAT);
                String altIccat  = asset.getIccat();
                if (iccat != null) {
                    fishingActivityEntity.setIccat(iccat);
                    fishingActivityEntity.setIccatAlt(altIccat);
                    fishingActivityEntity.setIccatSrc(IdentifierSourceEnum.MESSAGE);
                } else if(altIccat != null) {
                    fishingActivityEntity.setIccat(altIccat);
                    fishingActivityEntity.setIccatSrc(IdentifierSourceEnum.ASSET);
                }
                String gfcm = existingIds.get(VesselIdentifierSchemeIdEnum.GFCM);
                String altGfcm = asset.getGfcm();
                if (gfcm != null) {
                    fishingActivityEntity.setGfcm(gfcm);
                    fishingActivityEntity.setGfcmAlt(altGfcm);
                    fishingActivityEntity.setGfcmSrc(IdentifierSourceEnum.MESSAGE);
                } else if(altGfcm != null) {
                    fishingActivityEntity.setGfcm(altGfcm);
                    fishingActivityEntity.setGfcmSrc(IdentifierSourceEnum.ASSET);
                }
            }
        } catch (ServiceException e) {
            log.error("[ERROR] Error while trying to get guids from Assets Module!");
        }
    }
}
