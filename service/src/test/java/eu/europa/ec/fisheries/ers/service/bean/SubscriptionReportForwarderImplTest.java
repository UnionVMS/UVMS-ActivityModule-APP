/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.enterprise.inject.Produces;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.AssetModuleService;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMappingContext;
import eu.europa.ec.fisheries.schema.movement.v1.MovementMetaDataAreaType;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityAreas;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.FluxReportIdentifier;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ReportToSubscription;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.JAXBMarshaller;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FishingActivity;

/**
 * Test class for {@link SubscriptionReportForwarderImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionReportForwarderImplTest {

    @Mock @Produces
    ActivitySubscriptionServiceBean activitySubscriptionServiceBean;

    @InjectMocks
    SubscriptionReportForwarderImpl sut;

    @Test
    @SneakyThrows
    public void testForwardReportToSubscriptionActivitiesWithAreas() {
        FluxFaReportMessageMappingContext ctx = new FluxFaReportMessageMappingContext();// mock(FluxFaReportMessageMappingContext.class);
        FLUXFAReportMessage fluxfaReportMessage = getFluxFaReportMessage("FLUXFAReportMessage.xml");
        FluxFaReportMessageEntity fluxFaReportMessageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(ctx, fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());
        putAreasToCtx(ctx, Arrays.asList(createMovementDataType("id00", "areaType00", "name00"), createMovementDataType("id01", "areaType01", "name01"), createMovementDataType("id02", "areaType02", "name02")),
                        Arrays.asList(createMovementDataType("id10", "areaType10", "name10"), createMovementDataType("id11", "areaType11", "name11")));

        @SuppressWarnings({"unchecked", "rawtypes"})
        ArgumentCaptor<List<ReportToSubscription>> faReportCaptor = ArgumentCaptor.forClass((Class)List.class);

        sut.forwardReportToSubscription(ctx, fluxFaReportMessageEntity);

        verify(activitySubscriptionServiceBean, times(1)).sendForwardReportToSubscriptionRequest(faReportCaptor.capture());

        Assert.assertEquals(1, faReportCaptor.getAllValues().size());

        List<FluxReportIdentifier> reportIds1 = faReportCaptor.getValue().get(0).getFluxFaReportMessageIds();
        Assert.assertEquals(1, reportIds1.size());
        Assert.assertEquals("9db2de40-e3ee-4c98-887a-fb2042d2b691", reportIds1.get(0).getId());
        Assert.assertEquals("UUID", reportIds1.get(0).getSchemeId());

        List<FishingActivity> fishingActivities1 = faReportCaptor.getValue().get(0).getFishingActivities();
        Assert.assertEquals(1, fishingActivities1.size());
        Assert.assertEquals("AREA_EXIT", fishingActivities1.get(0).getTypeCode().getValue());

        String faReportType1 = faReportCaptor.getValue().get(0).getFaReportType();
        Assert.assertEquals("DECLARATION", faReportType1);

        List<ActivityAreas> activityAreas1 = faReportCaptor.getValue().get(0).getActivityAreas();
        Assert.assertEquals(3, activityAreas1.get(0).getAreas().size());
        Assert.assertEquals("id00", activityAreas1.get(0).getAreas().get(0).getRemoteId());

        List<FluxReportIdentifier> reportIds2 = faReportCaptor.getValue().get(1).getFluxFaReportMessageIds();
        Assert.assertEquals(1, reportIds2.size());
        Assert.assertEquals("b0481f16-4327-493a-ba85-7eae2e6ae650", reportIds2.get(0).getId());
        Assert.assertEquals("UUID", reportIds2.get(0).getSchemeId());

        List<FishingActivity> fishingActivities2 = faReportCaptor.getValue().get(1).getFishingActivities();
        Assert.assertEquals(1, fishingActivities2.size());
        Assert.assertEquals("AREA_ENTRY", fishingActivities2.get(0).getTypeCode().getValue());

        String faReportType2 = faReportCaptor.getValue().get(1).getFaReportType();
        Assert.assertEquals("DECLARATION", faReportType2);

        List<ActivityAreas> activityAreas2 = faReportCaptor.getValue().get(1).getActivityAreas();
        Assert.assertEquals(2, activityAreas2.get(0).getAreas().size());
        Assert.assertEquals("id10", activityAreas2.get(0).getAreas().get(0).getRemoteId());
    }

    @Test
    @SneakyThrows
    public void testForwardReportToSubscriptionActivitiesWithAreasForOneActivity() {
        FluxFaReportMessageMappingContext ctx = new FluxFaReportMessageMappingContext();// mock(FluxFaReportMessageMappingContext.class);
        FLUXFAReportMessage fluxfaReportMessage = getFluxFaReportMessage("FLUXFAReportMessage.xml");
        FluxFaReportMessageEntity fluxFaReportMessageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(ctx, fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());
        putAreasToCtx(ctx, Arrays.asList(createMovementDataType("id00", "areaType00", "name00"), createMovementDataType("id01", "areaType01", "name01"), createMovementDataType("id02", "areaType02", "name02")));

        @SuppressWarnings({"unchecked", "rawtypes"})
        ArgumentCaptor<List<ReportToSubscription>> faReportCaptor = ArgumentCaptor.forClass((Class)List.class);

        sut.forwardReportToSubscription(ctx, fluxFaReportMessageEntity);

        verify(activitySubscriptionServiceBean, times(1)).sendForwardReportToSubscriptionRequest(faReportCaptor.capture());

        Assert.assertEquals(1, faReportCaptor.getAllValues().size());

        List<FluxReportIdentifier> reportIds1 = faReportCaptor.getValue().get(0).getFluxFaReportMessageIds();
        Assert.assertEquals(1, reportIds1.size());
        Assert.assertEquals("9db2de40-e3ee-4c98-887a-fb2042d2b691", reportIds1.get(0).getId());
        Assert.assertEquals("UUID", reportIds1.get(0).getSchemeId());

        List<FishingActivity> fishingActivities1 = faReportCaptor.getValue().get(0).getFishingActivities();
        Assert.assertEquals(1, fishingActivities1.size());
        Assert.assertEquals("AREA_EXIT", fishingActivities1.get(0).getTypeCode().getValue());

        String faReportType1 = faReportCaptor.getValue().get(0).getFaReportType();
        Assert.assertEquals("DECLARATION", faReportType1);

        List<ActivityAreas> activityAreas1 = faReportCaptor.getValue().get(0).getActivityAreas();
        Assert.assertEquals(3, activityAreas1.get(0).getAreas().size());
        Assert.assertEquals("id00", activityAreas1.get(0).getAreas().get(0).getRemoteId());

        List<FluxReportIdentifier> reportIds2 = faReportCaptor.getValue().get(1).getFluxFaReportMessageIds();
        Assert.assertEquals(1, reportIds2.size());
        Assert.assertEquals("b0481f16-4327-493a-ba85-7eae2e6ae650", reportIds2.get(0).getId());
        Assert.assertEquals("UUID", reportIds2.get(0).getSchemeId());

        List<FishingActivity> fishingActivities2 = faReportCaptor.getValue().get(1).getFishingActivities();
        Assert.assertEquals(1, fishingActivities2.size());
        Assert.assertEquals("AREA_ENTRY", fishingActivities2.get(0).getTypeCode().getValue());

        String faReportType2 = faReportCaptor.getValue().get(1).getFaReportType();
        Assert.assertEquals("DECLARATION", faReportType2);

        List<ActivityAreas> activityAreas2 = faReportCaptor.getValue().get(1).getActivityAreas();
        Assert.assertEquals(0, activityAreas2.get(0).getAreas().size());
    }

    @Test
    @SneakyThrows
    public void testForwardReportToSubscriptionActivitiesWithNoAreas() {
        FluxFaReportMessageMappingContext ctx = new FluxFaReportMessageMappingContext();// mock(FluxFaReportMessageMappingContext.class);
        FLUXFAReportMessage fluxfaReportMessage = getFluxFaReportMessage("FLUXFAReportMessage.xml");
        FluxFaReportMessageEntity fluxFaReportMessageEntity = new FluxFaReportMessageMapper().mapToFluxFaReportMessage(ctx, fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());
        putAreasToCtx(ctx);

        @SuppressWarnings({"unchecked", "rawtypes"})
        ArgumentCaptor<List<ReportToSubscription>> faReportCaptor = ArgumentCaptor.forClass((Class)List.class);

        sut.forwardReportToSubscription(ctx, fluxFaReportMessageEntity);

        verify(activitySubscriptionServiceBean, times(1)).sendForwardReportToSubscriptionRequest(faReportCaptor.capture());

        Assert.assertEquals(1, faReportCaptor.getAllValues().size());

        List<FluxReportIdentifier> reportIds1 = faReportCaptor.getValue().get(0).getFluxFaReportMessageIds();
        Assert.assertEquals(1, reportIds1.size());
        Assert.assertEquals("9db2de40-e3ee-4c98-887a-fb2042d2b691", reportIds1.get(0).getId());
        Assert.assertEquals("UUID", reportIds1.get(0).getSchemeId());

        List<FishingActivity> fishingActivities1 = faReportCaptor.getValue().get(0).getFishingActivities();
        Assert.assertEquals(1, fishingActivities1.size());
        Assert.assertEquals("AREA_EXIT", fishingActivities1.get(0).getTypeCode().getValue());

        String faReportType1 = faReportCaptor.getValue().get(0).getFaReportType();
        Assert.assertEquals("DECLARATION", faReportType1);

        List<ActivityAreas> activityAreas1 = faReportCaptor.getValue().get(0).getActivityAreas();
        Assert.assertEquals(0, activityAreas1.get(0).getAreas().size());

        List<FluxReportIdentifier> reportIds2 = faReportCaptor.getValue().get(1).getFluxFaReportMessageIds();
        Assert.assertEquals(1, reportIds2.size());
        Assert.assertEquals("b0481f16-4327-493a-ba85-7eae2e6ae650", reportIds2.get(0).getId());
        Assert.assertEquals("UUID", reportIds2.get(0).getSchemeId());

        List<FishingActivity> fishingActivities2 = faReportCaptor.getValue().get(1).getFishingActivities();
        Assert.assertEquals(1, fishingActivities2.size());
        Assert.assertEquals("AREA_ENTRY", fishingActivities2.get(0).getTypeCode().getValue());

        String faReportType2 = faReportCaptor.getValue().get(1).getFaReportType();
        Assert.assertEquals("DECLARATION", faReportType2);

        List<ActivityAreas> activityAreas2 = faReportCaptor.getValue().get(1).getActivityAreas();
        Assert.assertEquals(0, activityAreas2.get(0).getAreas().size());
    }

    @SafeVarargs
    private final void putAreasToCtx(FluxFaReportMessageMappingContext ctx, List<MovementMetaDataAreaType>... areas) {
        List<List<MovementMetaDataAreaType>> activityAreas = new ArrayList<>(Arrays.asList(areas));
        ctx.getEntitiesMap().forEach((key,value) -> {
            if(!activityAreas.isEmpty()) {
                ctx.put(key, activityAreas.remove(0));
            }
        });
    }

    private MovementMetaDataAreaType createMovementDataType(String remoteId, String areaType, String name) {
        MovementMetaDataAreaType movementMetaDataAreaType = new MovementMetaDataAreaType();
        movementMetaDataAreaType.setRemoteId(remoteId);
        movementMetaDataAreaType.setAreaType(areaType);
        movementMetaDataAreaType.setName(name);
        return movementMetaDataAreaType;
    }

    @SneakyThrows
    private FLUXFAReportMessage getFluxFaReportMessage(String filename) {
        String resource = readResource(filename);
        return JAXBMarshaller.unmarshallString(resource, FLUXFAReportMessage.class);
    }

    private String readResource(String filename) {
        try(InputStream is = this.getClass().getResourceAsStream(filename)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[512];
            int r;
            while ((r = is.read(buf, 0, buf.length)) >= 0) {
                baos.write(buf, 0, r);
            }
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}