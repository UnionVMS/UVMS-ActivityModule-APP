/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.ActivityRulesModuleService;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.exception.ActivityModuleException;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SyncAsyncRequestType;
import eu.europa.ec.fisheries.uvms.activity.rest.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.commons.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import eu.europa.ec.fisheries.uvms.rules.model.exception.RulesModelMarshallException;
import eu.europa.ec.fisheries.uvms.rules.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.spatial.model.constants.USMSpatial;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

/**
 * Created by sanera on 04/08/2016.
 */
@Path("/trip")
@Slf4j
@Stateless

public class FishingTripResource extends UnionVMSResource {

    private final static Logger LOG = LoggerFactory.getLogger(FishingTripResource.class);

    @Context
    private UriInfo context;

    @EJB
    private ActivityService activityService;

    @EJB
    private FishingTripService fishingTripService;

    @EJB
    private ActivityRulesModuleService rulesService;

    @EJB
    private USMService usmService;

    @GET
    @Path("/reports/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripSummary(@Context HttpServletRequest request,
                                          @Context HttpServletResponse response,
                                          @HeaderParam("scopeName") String scopeName,
                                          @HeaderParam("roleName") String roleName,
                                          @PathParam("fishingTripId") String fishingTripId) throws ServiceException {

        LOG.info("Fishing Trip summary from fishing trip : " + fishingTripId);
        String username = request.getRemoteUser();
        List<Dataset> datasets = usmService.getDatasetsPerCategory(USMSpatial.USM_DATASET_CATEGORY, username, USMSpatial.APPLICATION_NAME, roleName, scopeName);
        LOG.info("Fishing Trip summary from fishing trip : "+fishingTripId);
        return createSuccessResponse(fishingTripService.getFishingTripSummaryAndReports(fishingTripId, datasets));
    }

    @GET
    @Path("/vessel/details/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getVesselDetails(@Context HttpServletRequest request,
                                     @Context HttpServletResponse response,
                                     @PathParam("fishingTripId") String fishingTripId) throws ServiceException {

        LOG.info("Getting Vessels details for trip : " + fishingTripId);
        return createSuccessResponse(fishingTripService.getVesselDetailsForFishingTrip(fishingTripId));
    }

    @GET
    @Path("/messages/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripMessageCounter(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response,
                                                 @PathParam("fishingTripId") String fishingTripId) throws ServiceException {

        LOG.info("Message counters for fishing trip : " + fishingTripId);
        return createSuccessResponse(fishingTripService.getMessageCountersForTripId(fishingTripId));
    }

    @GET
    @Path("/catches/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripCatchReports(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response,
                                                 @PathParam("fishingTripId") String fishingTripId) throws ServiceException {

        LOG.info("Catches for fishing trip : "+fishingTripId);
        return createSuccessResponse(fishingTripService.retrieveFaCatchesForFishingTrip(fishingTripId));
    }

    @GET
    @Path("/cronology/{tripId}/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getCronologyOfFishingTrip(@Context HttpServletRequest request,
                                              @Context HttpServletResponse response,
                                              @PathParam("tripId") String tripId,
                                              @PathParam("count") Integer count) throws ServiceException {
        return createSuccessResponse(fishingTripService.getCronologyOfFishingTrip(tripId, count));
    }

    @GET
    @Path("/mapData/{tripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getTripMapData(@Context HttpServletRequest request,
                                              @Context HttpServletResponse response,
                                              @PathParam("tripId") String tripId
                                              ) throws ServiceException {
        return createSuccessResponse(fishingTripService.getTripMapDetailsForTripId(tripId));
    }

    @GET
    @Path("/catchevolution/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getFishingTripCatchEvolution(@Context HttpServletRequest request,
                                                 @Context HttpServletResponse response,
                                                 @PathParam("fishingTripId") String fishingTripId) throws ServiceException {
        LOG.info("Catch evolution for fishing trip : " + fishingTripId);
        return createSuccessResponse(fishingTripService.retrieveCatchEvolutionForFishingTrip(fishingTripId));
    }

    @GET
    @Path("/requestupdate/{fishingTripId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    //@IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.SEND_UPDATE_TRIP_REQUEST})
    public Response requestTripUpdateFromTripId(@Context HttpServletRequest request,
                                                @Context HttpServletResponse response,
                                                @PathParam("fishingTripId") String fishingTripId) throws ServiceException {
        LOG.info("[INFO] Going to send FaQuery related to Trip with id : " + fishingTripId);
        try {
            rulesService.composeAndSendTripUpdateFaQueryToRules(fishingTripId);
        } catch (ActivityModuleException e) {
            return createErrorResponse("Error while trying to send Update Trip Query!" + e.getMessage());
        }
        return createSuccessResponse();
    }

    // TODO : Remove me after integration test is complete
    @GET
    @Path("/requestupdate/mockedfareport")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    // @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.SEND_UPDATE_TRIP_REQUEST})
    public Response mockerReportToPlugin(@Context HttpServletRequest request,
                                                @Context HttpServletResponse response) throws ServiceException {
        LOG.info("Going to process sending of FaReportMessage....");
        try {
            rulesService.sendSyncAsyncFaReportToRules((FLUXFAReportMessage)JAXBMarshaller.unMarshallMessage(mockedFaReport, FLUXFAReportMessage.class), "SOMEON", SyncAsyncRequestType.ASYNC, null);
        } catch (ActivityModuleException | RulesModelMarshallException e) {
            return createErrorResponse("Error while trying to send Update Trip Query!" + e.getMessage());
        }
        return createSuccessResponse();
    }


    String mockedFaReport = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<rsm:FLUXFAReportMessage xmlns:ram=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:udt=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:rsm=\"urn:un:unece:uncefact:data:standard:FLUXFAReportMessage:3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"urn:un:unece:uncefact:data:standard:FLUXFAReportMessage:3 xsd/FLUXFAReportMessage_3p1/FLUXFAReportMessage_3p1.xsd\">\n" +
            "   <rsm:FLUXReportDocument>\n" +
            "\t  <ram:ID schemeID=\"UUID\">22341761-6511-4a0d-b78b-800461b55OOO</ram:ID>\n" +
            "\t  <ram:CreationDateTime>\n" +
            "\t\t <udt:DateTime>2017-10-31T11:54:19Z</udt:DateTime>\n" +
            "\t  </ram:CreationDateTime>\n" +
            "\t  <ram:PurposeCode listID=\"FLUX_GP_PURPOSE\">9</ram:PurposeCode>\n" +
            "\t  <ram:OwnerFLUXParty>\n" +
            "\t\t <ram:ID schemeID=\"FLUX_GP_PARTY\">XEU</ram:ID>\n" +
            "\t  </ram:OwnerFLUXParty>\n" +
            "   </rsm:FLUXReportDocument>\n" +
            "   <rsm:FAReportDocument>\n" +
            "\t  <ram:TypeCode listID=\"FLUX_FA_REPORT_TYPE\">DECLARATION</ram:TypeCode>\n" +
            "\t  <ram:AcceptanceDateTime>\n" +
            "\t\t <udt:DateTime>2017-06-08T08:51:00Z</udt:DateTime>\n" +
            "\t  </ram:AcceptanceDateTime>\n" +
            "\t  <ram:RelatedFLUXReportDocument>\n" +
            "\t\t <ram:ID schemeID=\"UUID\">22341761-6511-4a0d-b78b-800461b55OOO</ram:ID>\n" +
            "\t\t <ram:CreationDateTime>\n" +
            "\t\t\t<udt:DateTime>2017-06-08T08:52:00Z</udt:DateTime>\n" +
            "\t\t </ram:CreationDateTime>\n" +
            "\t\t <ram:PurposeCode listID=\"FLUX_GP_PURPOSE\">9</ram:PurposeCode>\n" +
            "\t\t <ram:OwnerFLUXParty>\n" +
            "\t\t\t<ram:ID schemeID=\"FLUX_GP_PARTY\">CYP</ram:ID>\n" +
            "\t\t </ram:OwnerFLUXParty>\n" +
            "\t  </ram:RelatedFLUXReportDocument>\n" +
            "\t  <ram:SpecifiedFishingActivity>\n" +
            "\t\t <ram:TypeCode listID=\"FLUX_FA_TYPE\">DEPARTURE</ram:TypeCode>\n" +
            "\t\t <ram:OccurrenceDateTime>\n" +
            "\t\t\t<udt:DateTime>2017-06-08T08:50:00Z</udt:DateTime>\n" +
            "\t\t </ram:OccurrenceDateTime>\n" +
            "\t\t <ram:ReasonCode listID=\"FA_REASON_DEPARTURE\">FIS</ram:ReasonCode>\n" +
            "\t\t <ram:SpeciesTargetCode listID=\"TARGET_SPECIES_GROUP\">DEMERSAL</ram:SpeciesTargetCode>\n" +
            "\t\t <ram:SpecifiedFACatch>\n" +
            "\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">ANF</ram:SpeciesCode>\n" +
            "\t\t\t<ram:WeightMeasure unitCode=\"KGM\">512.4</ram:WeightMeasure>\n" +
            "\t\t\t<ram:TypeCode listID=\"FA_CATCH_TYPE\">ONBOARD</ram:TypeCode>\n" +
            "\t\t\t<ram:RelatedFishingTrip>\n" +
            "\t\t\t   <ram:ID schemeID=\"EU_TRIP_ID\">CYP-TRP-20170531000000000009</ram:ID>\n" +
            "\t\t\t</ram:RelatedFishingTrip>\n" +
            "\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t   <ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FISH_PRESENTATION\">GUT</ram:TypeCode>\n" +
            "\t\t\t   <ram:ConversionFactorNumeric>1.22</ram:ConversionFactorNumeric>\n" +
            "\t\t\t   <ram:ResultAAPProduct>\n" +
            "\t\t\t\t  <ram:PackagingUnitQuantity unitCode=\"C62\">42.0</ram:PackagingUnitQuantity>\n" +
            "\t\t\t\t  <ram:PackagingTypeCode listID=\"FISH_PACKAGING\">BOX</ram:PackagingTypeCode>\n" +
            "\t\t\t\t  <ram:PackagingUnitAverageWeightMeasure unitCode=\"KGM\">10.0</ram:PackagingUnitAverageWeightMeasure>\n" +
            "\t\t\t   </ram:ResultAAPProduct>\n" +
            "\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t<ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t   <ram:ID schemeID=\"FAO_AREA\">27.9.b.1</ram:ID>\n" +
            "\t\t\t</ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t<ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t   <ram:ID schemeID=\"ICES_STAT_RECTANGLE\">11D6</ram:ID>\n" +
            "\t\t\t</ram:SpecifiedFLUXLocation>\n" +
            "\t\t </ram:SpecifiedFACatch>\n" +
            "\t\t <ram:RelatedFLUXLocation>\n" +
            "\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</ram:TypeCode>\n" +
            "\t\t\t<ram:ID schemeID=\"LOCATION\">ESVGO</ram:ID>\n" +
            "\t\t </ram:RelatedFLUXLocation>\n" +
            "\t\t <ram:SpecifiedFishingGear>\n" +
            "\t\t\t<ram:TypeCode listID=\"GEAR_TYPE\">OTB</ram:TypeCode>\n" +
            "\t\t\t<ram:RoleCode listID=\"FA_GEAR_ROLE\">ONBOARD</ram:RoleCode>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">MT</ram:TypeCode>\n" +
            "\t\t\t   <ram:Value>OTB-2</ram:Value>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">ME</ram:TypeCode>\n" +
            "\t\t\t   <ram:ValueMeasure unitCode=\"MMT\">120</ram:ValueMeasure>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">GD</ram:TypeCode>\n" +
            "\t\t\t   <ram:Value>Gear1</ram:Value>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t </ram:SpecifiedFishingGear>\n" +
            "\t\t <ram:SpecifiedFishingGear>\n" +
            "\t\t\t<ram:TypeCode listID=\"GEAR_TYPE\">PTB</ram:TypeCode>\n" +
            "\t\t\t<ram:RoleCode listID=\"FA_GEAR_ROLE\">ONBOARD</ram:RoleCode>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">ME</ram:TypeCode>\n" +
            "\t\t\t   <ram:ValueMeasure unitCode=\"MMT\">80</ram:ValueMeasure>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">GM</ram:TypeCode>\n" +
            "\t\t\t   <ram:ValueMeasure unitCode=\"MTR\">200</ram:ValueMeasure>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">GD</ram:TypeCode>\n" +
            "\t\t\t   <ram:Value>Gear2</ram:Value>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t </ram:SpecifiedFishingGear>\n" +
            "\t\t <ram:SpecifiedFishingTrip>\n" +
            "\t\t\t<ram:ID schemeID=\"EU_TRIP_ID\">CYP-TRP-20170608000000000010</ram:ID>\n" +
            "\t\t </ram:SpecifiedFishingTrip>\n" +
            "\t  </ram:SpecifiedFishingActivity>\n" +
            "\t  <ram:SpecifiedVesselTransportMeans>\n" +
            "\t\t <ram:ID schemeID=\"CFR\">CYP123456789</ram:ID>\n" +
            "\t\t <ram:ID schemeID=\"IRCS\">IRCS6</ram:ID>\n" +
            "\t\t <ram:ID schemeID=\"EXT_MARK\">XR006</ram:ID>\n" +
            "\t\t <ram:Name>Golf</ram:Name>\n" +
            "\t\t <ram:RegistrationVesselCountry>\n" +
            "\t\t\t<ram:ID schemeID=\"TERRITORY\">CYP</ram:ID>\n" +
            "\t\t </ram:RegistrationVesselCountry>\n" +
            "\t\t <ram:SpecifiedContactParty>\n" +
            "\t\t\t<ram:RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</ram:RoleCode>\n" +
            "\t\t\t<ram:SpecifiedStructuredAddress>\n" +
            "\t\t\t   <ram:StreetName>Street Q</ram:StreetName>\n" +
            "\t\t\t   <ram:CityName>City R</ram:CityName>\n" +
            "\t\t\t   <ram:CountryID schemeID=\"TERRITORY\">CYP</ram:CountryID>\n" +
            "\t\t\t</ram:SpecifiedStructuredAddress>\n" +
            "\t\t\t<ram:SpecifiedContactPerson>\n" +
            "\t\t\t   <ram:GivenName>Jamie</ram:GivenName>\n" +
            "\t\t\t   <ram:FamilyName>G</ram:FamilyName>\n" +
            "\t\t\t   <ram:Alias>Master G</ram:Alias>\n" +
            "\t\t\t</ram:SpecifiedContactPerson>\n" +
            "\t\t </ram:SpecifiedContactParty>\n" +
            "\t  </ram:SpecifiedVesselTransportMeans>\n" +
            "   </rsm:FAReportDocument>\n" +
            "   <rsm:FAReportDocument>\n" +
            "\t  <ram:TypeCode listID=\"FLUX_FA_REPORT_TYPE\">DECLARATION</ram:TypeCode>\n" +
            "\t  <ram:AcceptanceDateTime>\n" +
            "\t\t <udt:DateTime>2017-06-08T08:51:00Z</udt:DateTime>\n" +
            "\t  </ram:AcceptanceDateTime>\n" +
            "\t  <ram:RelatedFLUXReportDocument>\n" +
            "\t\t <ram:ID schemeID=\"UUID\">12341761-6511-4a0d-b78b-800461b55XXX</ram:ID>\n" +
            "\t\t <ram:CreationDateTime>\n" +
            "\t\t\t<udt:DateTime>2017-06-08T08:52:00Z</udt:DateTime>\n" +
            "\t\t </ram:CreationDateTime>\n" +
            "\t\t <ram:PurposeCode listID=\"FLUX_GP_PURPOSE\">9</ram:PurposeCode>\n" +
            "\t\t <ram:OwnerFLUXParty>\n" +
            "\t\t\t<ram:ID schemeID=\"FLUX_GP_PARTY\">CYP</ram:ID>\n" +
            "\t\t </ram:OwnerFLUXParty>\n" +
            "\t  </ram:RelatedFLUXReportDocument>\n" +
            "\t  <ram:SpecifiedFishingActivity>\n" +
            "\t\t <ram:TypeCode listID=\"FLUX_FA_TYPE\">DEPARTURE</ram:TypeCode>\n" +
            "\t\t <ram:OccurrenceDateTime>\n" +
            "\t\t\t<udt:DateTime>2017-06-08T08:50:00Z</udt:DateTime>\n" +
            "\t\t </ram:OccurrenceDateTime>\n" +
            "\t\t <ram:ReasonCode listID=\"FA_REASON_DEPARTURE\">FIS</ram:ReasonCode>\n" +
            "\t\t <ram:SpeciesTargetCode listID=\"TARGET_SPECIES_GROUP\">DEMERSAL</ram:SpeciesTargetCode>\n" +
            "\t\t <ram:SpecifiedFACatch>\n" +
            "\t\t\t<ram:SpeciesCode listID=\"FAO_SPECIES\">ANF</ram:SpeciesCode>\n" +
            "\t\t\t<ram:WeightMeasure unitCode=\"KGM\">512.4</ram:WeightMeasure>\n" +
            "\t\t\t<ram:TypeCode listID=\"FA_CATCH_TYPE\">ONBOARD</ram:TypeCode>\n" +
            "\t\t\t<ram:RelatedFishingTrip>\n" +
            "\t\t\t   <ram:ID schemeID=\"EU_TRIP_ID\">CYP-TRP-20170531000000000009</ram:ID>\n" +
            "\t\t\t</ram:RelatedFishingTrip>\n" +
            "\t\t\t<ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t   <ram:ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ram:ClassCode>\n" +
            "\t\t\t</ram:SpecifiedSizeDistribution>\n" +
            "\t\t\t<ram:AppliedAAPProcess>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FISH_PRESENTATION\">GUT</ram:TypeCode>\n" +
            "\t\t\t   <ram:ConversionFactorNumeric>1.22</ram:ConversionFactorNumeric>\n" +
            "\t\t\t   <ram:ResultAAPProduct>\n" +
            "\t\t\t\t  <ram:PackagingUnitQuantity unitCode=\"C62\">42.0</ram:PackagingUnitQuantity>\n" +
            "\t\t\t\t  <ram:PackagingTypeCode listID=\"FISH_PACKAGING\">BOX</ram:PackagingTypeCode>\n" +
            "\t\t\t\t  <ram:PackagingUnitAverageWeightMeasure unitCode=\"KGM\">10.0</ram:PackagingUnitAverageWeightMeasure>\n" +
            "\t\t\t   </ram:ResultAAPProduct>\n" +
            "\t\t\t</ram:AppliedAAPProcess>\n" +
            "\t\t\t<ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t   <ram:ID schemeID=\"FAO_AREA\">27.9.b.1</ram:ID>\n" +
            "\t\t\t</ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t<ram:SpecifiedFLUXLocation>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</ram:TypeCode>\n" +
            "\t\t\t   <ram:ID schemeID=\"ICES_STAT_RECTANGLE\">11D6</ram:ID>\n" +
            "\t\t\t</ram:SpecifiedFLUXLocation>\n" +
            "\t\t </ram:SpecifiedFACatch>\n" +
            "\t\t <ram:RelatedFLUXLocation>\n" +
            "\t\t\t<ram:TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</ram:TypeCode>\n" +
            "\t\t\t<ram:ID schemeID=\"LOCATION\">ESVGO</ram:ID>\n" +
            "\t\t </ram:RelatedFLUXLocation>\n" +
            "\t\t <ram:SpecifiedFishingGear>\n" +
            "\t\t\t<ram:TypeCode listID=\"GEAR_TYPE\">OTB</ram:TypeCode>\n" +
            "\t\t\t<ram:RoleCode listID=\"FA_GEAR_ROLE\">ONBOARD</ram:RoleCode>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">MT</ram:TypeCode>\n" +
            "\t\t\t   <ram:Value>OTB-2</ram:Value>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">ME</ram:TypeCode>\n" +
            "\t\t\t   <ram:ValueMeasure unitCode=\"MMT\">120</ram:ValueMeasure>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">GD</ram:TypeCode>\n" +
            "\t\t\t   <ram:Value>Gear1</ram:Value>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t </ram:SpecifiedFishingGear>\n" +
            "\t\t <ram:SpecifiedFishingGear>\n" +
            "\t\t\t<ram:TypeCode listID=\"GEAR_TYPE\">PTB</ram:TypeCode>\n" +
            "\t\t\t<ram:RoleCode listID=\"FA_GEAR_ROLE\">ONBOARD</ram:RoleCode>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">ME</ram:TypeCode>\n" +
            "\t\t\t   <ram:ValueMeasure unitCode=\"MMT\">80</ram:ValueMeasure>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">GM</ram:TypeCode>\n" +
            "\t\t\t   <ram:ValueMeasure unitCode=\"MTR\">200</ram:ValueMeasure>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t<ram:ApplicableGearCharacteristic>\n" +
            "\t\t\t   <ram:TypeCode listID=\"FA_GEAR_CHARACTERISTIC\">GD</ram:TypeCode>\n" +
            "\t\t\t   <ram:Value>Gear2</ram:Value>\n" +
            "\t\t\t</ram:ApplicableGearCharacteristic>\n" +
            "\t\t </ram:SpecifiedFishingGear>\n" +
            "\t\t <ram:SpecifiedFishingTrip>\n" +
            "\t\t\t<ram:ID schemeID=\"EU_TRIP_ID\">CYP-TRP-20170608000000000010</ram:ID>\n" +
            "\t\t </ram:SpecifiedFishingTrip>\n" +
            "\t  </ram:SpecifiedFishingActivity>\n" +
            "\t  <ram:SpecifiedVesselTransportMeans>\n" +
            "\t\t <ram:ID schemeID=\"CFR\">CYP123456789</ram:ID>\n" +
            "\t\t <ram:ID schemeID=\"IRCS\">IRCS6</ram:ID>\n" +
            "\t\t <ram:ID schemeID=\"EXT_MARK\">XR006</ram:ID>\n" +
            "\t\t <ram:Name>Golf</ram:Name>\n" +
            "\t\t <ram:RegistrationVesselCountry>\n" +
            "\t\t\t<ram:ID schemeID=\"TERRITORY\">CYP</ram:ID>\n" +
            "\t\t </ram:RegistrationVesselCountry>\n" +
            "\t\t <ram:SpecifiedContactParty>\n" +
            "\t\t\t<ram:RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</ram:RoleCode>\n" +
            "\t\t\t<ram:SpecifiedStructuredAddress>\n" +
            "\t\t\t   <ram:StreetName>Street Q</ram:StreetName>\n" +
            "\t\t\t   <ram:CityName>City R</ram:CityName>\n" +
            "\t\t\t   <ram:CountryID schemeID=\"TERRITORY\">CYP</ram:CountryID>\n" +
            "\t\t\t</ram:SpecifiedStructuredAddress>\n" +
            "\t\t\t<ram:SpecifiedContactPerson>\n" +
            "\t\t\t   <ram:GivenName>Jamie</ram:GivenName>\n" +
            "\t\t\t   <ram:FamilyName>G</ram:FamilyName>\n" +
            "\t\t\t   <ram:Alias>Master G</ram:Alias>\n" +
            "\t\t\t</ram:SpecifiedContactPerson>\n" +
            "\t\t </ram:SpecifiedContactParty>\n" +
            "\t  </ram:SpecifiedVesselTransportMeans>\n" +
            "   </rsm:FAReportDocument>\n" +
            "</rsm:FLUXFAReportMessage>";
}
