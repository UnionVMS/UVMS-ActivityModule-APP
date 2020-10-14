/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import eu.europa.ec.fisheries.ers.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.ers.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMappingContext;
import eu.europa.ec.fisheries.schema.movement.v1.MovementMetaDataAreaType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityAreas;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.Area;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FluxReportIdentifier;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ReportToSubscription;
import eu.europa.ec.fisheries.uvms.commons.geometry.mapper.GeometryMapper;
import eu.europa.ec.fisheries.uvms.commons.geometry.model.StringWrapper;
import org.apache.commons.lang3.tuple.Pair;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;

@ApplicationScoped
public class SubscriptionReportForwarderImpl implements SubscriptionReportForwarder {

    private ActivitySubscriptionServiceBean activitySubscriptionServiceBean;


    /**
     * Injection constructor.
     *
     * @param activitySubscriptionServiceBean The bean that handles according events
     */
    @Inject
    public SubscriptionReportForwarderImpl(ActivitySubscriptionServiceBean activitySubscriptionServiceBean) {
        this.activitySubscriptionServiceBean = activitySubscriptionServiceBean;
    }

    /**
     * Constructor for frameworks.
     */
    @SuppressWarnings("unused")
    SubscriptionReportForwarderImpl() {
        // NOOP
    }

    @Override
    public void forwardReportToSubscription(FluxFaReportMessageMappingContext ctx, FluxFaReportMessageEntity messageEntity) {
        List<ReportToSubscription> faReports = new ArrayList<>();
        for(FaReportDocumentEntity faReportDocumentEntity: messageEntity.getFaReportDocuments()) {
            List<FluxReportIdentifier> fluxFaReportMessageIds = new ArrayList<>();
            List<FishingActivity> fishingActivities = new ArrayList<>();
            List<ActivityAreas> activityAreas = new ArrayList<>();
            List<String> assetHistoryGuids = new ArrayList<>();
            for(FluxReportIdentifierEntity fluxReportIdentifier: faReportDocumentEntity.getFluxReportDocument().getFluxReportIdentifiers()) {
                fluxFaReportMessageIds.add(new FluxReportIdentifier(fluxReportIdentifier.getFluxReportIdentifierId(), fluxReportIdentifier.getFluxReportIdentifierSchemeId()));
            }
            List<String> activitiesGeometries = new ArrayList<>();
            for(FishingActivityEntity fishingActivityEntity: faReportDocumentEntity.getFishingActivities()) {
                FishingActivity fishingActivity = ctx.getFishingActivity(fishingActivityEntity);
                fishingActivities.add(fishingActivity);
                List<MovementMetaDataAreaType> movementAreas = ctx.getAreasForEntity(fishingActivityEntity);
                if(movementAreas != null) {
                    List<Area> areas = movementAreas.stream().map(area -> new Area(area.getAreaType(), area.getRemoteId(), area.getName())).collect(Collectors.toList());
                    activityAreas.add(new ActivityAreas(areas));
                } else {
                    activityAreas.add(new ActivityAreas(Collections.emptyList()));
                }
                Date activityDate = fishingActivityEntity.getOccurence() != null ? fishingActivityEntity.getOccurence() : getFirstDateFromDelimitedPeriods(fishingActivityEntity.getDelimitedPeriods());
                for(VesselTransportMeansEntity vesselTransportMeansEntity :faReportDocumentEntity.getVesselTransportMeans()) {
                    if(vesselTransportMeansEntity.getGuid() != null && activityDate != null) {
                        Optional.ofNullable(ctx.getAssetHistoryGuid(Pair.of(vesselTransportMeansEntity.getGuid(), activityDate))).ifPresent(assetHistoryGuids::add);
                    }
                }
                Optional.ofNullable(fishingActivityEntity.getGeom())
                        .map(GeometryMapper.INSTANCE::geometryToWkt)
                        .map(StringWrapper::getValue)
                        .ifPresent(activitiesGeometries::add);
            }
            
            faReports.add(new ReportToSubscription(fluxFaReportMessageIds, fishingActivities, faReportDocumentEntity.getTypeCode(), activityAreas, assetHistoryGuids,
                    activitiesGeometries));
        }
        activitySubscriptionServiceBean.sendForwardReportToSubscriptionRequest(faReports);
    }

    private Date getFirstDateFromDelimitedPeriods(Collection<DelimitedPeriodEntity> delimitedPeriods) {
        return delimitedPeriods.stream()
                .map(DelimitedPeriodEntity::getStartDate)
                .filter(Objects::nonNull)
                .min(Date::compareTo)
                .orElse(null);
    }
}
