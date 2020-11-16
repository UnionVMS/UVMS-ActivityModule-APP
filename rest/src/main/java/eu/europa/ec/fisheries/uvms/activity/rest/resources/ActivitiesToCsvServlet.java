package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.ers.fa.utils.CsvExportType;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;
import eu.europa.ec.fisheries.uvms.spatial.model.constants.USMSpatial;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;

@WebServlet(
		asyncSupported = true,
		urlPatterns = "/rest/csv/activities/*"
)
public class ActivitiesToCsvServlet extends HttpServlet {
	
	public static final int PAGE_SIZE = 200;
	public static final String COLUMN_TITLES =	
			"Report Type, Activity Type, Purpose Code, Data Source, From, Start Date, " +
			"End Date, CFR, IRCS, Ext. Mark., UVI, ICCAT, GFCM, Areas, Port, Gear, Species, Weight (kg)";

	@Resource
	private ManagedExecutorService managedExecutorService;
	
	@Inject
	private ActivityService activityService;
	
	@Inject
	private USMService usmService;


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		FishingActivityQuery fishingActivityQuery = new ObjectMapper().readValue(req.getInputStream(), FishingActivityQuery.class);
		
		
		PaginationDto paginationDto = new PaginationDto(0,PAGE_SIZE);
		fishingActivityQuery.setPagination(paginationDto);


		List<Dataset> datasets = null;
		try {
			datasets = usmService.getDatasetsPerCategory(USMSpatial.USM_DATASET_CATEGORY,
							req.getRemoteUser(), USMSpatial.APPLICATION_NAME, req.getHeader("roleName"), req.getHeader("scopeName"));
		} catch (ServiceException e) {
			throw new IOException(e);
		}

		resp.setContentType("text/csv");
		resp.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
		AsyncContext asyncCtx = req.startAsync();
		asyncCtx.setTimeout(20000000);
		
		asyncCtx.getResponse().getWriter().println(COLUMN_TITLES);
		asyncCtx.getResponse().getWriter().flush();
		
		managedExecutorService.execute(new CsvExportTask(CsvExportType.ACTIVITY, asyncCtx, managedExecutorService, fishingActivityQuery, activityService, null, datasets));
	}
}
