package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.AsyncContext;
import java.io.IOException;
import java.util.List;

import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;

public class CsvExportTask implements Runnable {
    
    private AsyncContext asyncCtx;
    private ManagedExecutorService managedExecutorService;
    private FishingActivityQuery fishingActivityQuery;
    private ActivityService activityService;
    private List<Dataset> datasets;

    public CsvExportTask(AsyncContext asyncCtx, ManagedExecutorService managedExecutorService, FishingActivityQuery fishingActivityQuery, ActivityService activityService, List<Dataset> datasets) {
        this.asyncCtx = asyncCtx;
        this.managedExecutorService = managedExecutorService;
        this.fishingActivityQuery = fishingActivityQuery;
        this.activityService = activityService;
        this.datasets = datasets;
    }

    @Override
    public void run() {
        try {
            List<String> activities = activityService.getFishingActivityListByQueryAsStringArray(fishingActivityQuery, datasets);

            if(!activities.isEmpty()) {
                activities.forEach(asyncCtx.getResponse().getWriter()::println);
                asyncCtx.getResponse().getWriter().flush();
                
                PaginationDto pagination = fishingActivityQuery.getPagination();
                pagination.setOffset(pagination.getOffset() + CsvServlet.PAGE_SIZE);
                managedExecutorService.execute(new CsvExportTask(asyncCtx, managedExecutorService, fishingActivityQuery, activityService, datasets));
            } else {
                asyncCtx.complete();
            }
        } catch (ServiceException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
