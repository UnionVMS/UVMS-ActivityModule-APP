package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.bean.ActivityService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Created by sanera on 28/06/2016.
 */
@Path("/activity")
@Slf4j
@Stateless
public class ActivityResource  extends UnionVMSResource {
    private final static Logger LOG = LoggerFactory.getLogger(ActivityResource.class);

    @Context
    private UriInfo context;


    @EJB
    private ActivityService activityService;





    @POST
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/listByQuery")
    public Response listActivityReportsByQuery(FishingActivityQuery fishingActivityQuery) {

        LOG.info("call getFishingActivityDao ");

        if(fishingActivityQuery==null)
            return  createErrorResponse("Query to find list is null.");

        List<FishingActivityReportDTO> dtoList=activityService.getFishingActivityListByQuery(fishingActivityQuery);
        for(FishingActivityReportDTO dto: dtoList){
            LOG.info("dto :"+dto.toString());
        }
         Response responseMethod = createSuccessResponse(dtoList);
        LOG.info("successful");
        return responseMethod;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listActivityReports() {

        LOG.info("call getFishingActivityDao ");
        List<FishingActivityReportDTO> dtoList=activityService.getFishingActivityList();
        Response responseMethod = createSuccessResponse(dtoList);
        LOG.info("successful");
        return responseMethod;
    }



}
