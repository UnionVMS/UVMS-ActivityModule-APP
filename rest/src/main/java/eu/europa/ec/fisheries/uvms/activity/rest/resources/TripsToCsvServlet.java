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

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.ers.fa.utils.CsvExportType;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.rest.security.bean.USMService;

@WebServlet(
		asyncSupported = true,
		urlPatterns = "/rest/csv/trips/*"
)
public class TripsToCsvServlet extends HttpServlet {
	
	public static final int PAGE_SIZE = 200;
	public static final String COLUMN_TITLES =	
			"Trip ID, F.S., Ext. Mark., IRCS, CFR, UVI, ICCAT, GFCM, First Event, First Event Date, Last Event, Last Event Date, Duration (At Sea), Corrections";

	@Resource
	private ManagedExecutorService managedExecutorService;
	
	@Inject
	private FishingTripService fishingTripService;
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		FishingActivityQuery fishingActivityQuery = new ObjectMapper().readValue(req.getInputStream(), FishingActivityQuery.class);
		
		PaginationDto paginationDto = new PaginationDto(0,PAGE_SIZE);
		fishingActivityQuery.setPagination(paginationDto);
		
		resp.setContentType("text/csv");
		resp.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
		AsyncContext asyncCtx = req.startAsync();
		asyncCtx.setTimeout(20000000);
		
		asyncCtx.getResponse().getWriter().println(COLUMN_TITLES);
		asyncCtx.getResponse().getWriter().flush();
		
		managedExecutorService.execute(new CsvExportTask(CsvExportType.TRIP, asyncCtx, managedExecutorService, fishingActivityQuery, null, fishingTripService, null));
	}
}
