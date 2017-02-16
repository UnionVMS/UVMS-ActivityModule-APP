package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.ers.service.FaCatchReportService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityFeaturesEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.ActivityExceptionInterceptor;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.util.IUserRoleInterceptor;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.resource.UnionVMSResource;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 17/01/2017.
 */
@Path("/test")
@Slf4j
@Stateless
public class TestResource extends UnionVMSResource {

    private final static Logger LOG = LoggerFactory.getLogger(TestResource.class);

    @Context
    private UriInfo context;

    @EJB
    private USMService usmService;

    @EJB
    private FaCatchReportService reportService;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Interceptors(ActivityExceptionInterceptor.class)
    @IUserRoleInterceptor(requiredUserRole = {ActivityFeaturesEnum.FISHING_TRIP_SUMMARY})
    public Response getTestServiceResult(@Context HttpServletRequest request,
                                              @Context HttpServletResponse response
                                             ) throws ServiceException {
        FishingActivityQuery query = new FishingActivityQuery();
        Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();

        List<GroupCriteria> groupByFields = new ArrayList<>();
        groupByFields.add(GroupCriteria.DATE_MONTH);
        //  groupByFields.add(GroupCriteria.SIZE_CLASS);
        //groupByFields.add(GroupCriteria.SPECIES);
       // groupByFields.add(GroupCriteria.AREA);
        query.setGroupByFields(groupByFields);

        searchCriteriaMap.put(SearchFilter.SOURCE, "FLUX");

        query.setSearchCriteriaMap(searchCriteriaMap);
        reportService.getCatchSummaryReportForWeb(query);
        return createSuccessResponse();
    }
}
