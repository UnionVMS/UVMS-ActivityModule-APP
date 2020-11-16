package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.AsyncContext;
import java.io.IOException;
import java.util.List;

import eu.europa.ec.fisheries.ers.fa.utils.CsvExportType;
import eu.europa.ec.fisheries.ers.service.ActivityService;
import eu.europa.ec.fisheries.ers.service.FishingTripService;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginationDto;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.wsdl.user.types.Dataset;

public class CsvExportTask implements Runnable {
    
    private CsvExportType type;
    private AsyncContext asyncCtx;
    private ManagedExecutorService managedExecutorService;
    private FishingActivityQuery fishingActivityQuery;
    private ActivityService activityService;
    private FishingTripService fishingTripService;
    private List<Dataset> datasets;

    public CsvExportTask(CsvExportType type, AsyncContext asyncCtx, ManagedExecutorService managedExecutorService, FishingActivityQuery fishingActivityQuery, ActivityService activityService, FishingTripService fishingTripService, List<Dataset> datasets) {
        this.type = type;
        this.asyncCtx = asyncCtx;
        this.managedExecutorService = managedExecutorService;
        this.fishingActivityQuery = fishingActivityQuery;
        this.activityService = activityService;
        this.fishingTripService = fishingTripService;
        this.datasets = datasets;
    }

    @Override
    public void run() {
        try {
            List<String> lines;
            if(type.equals(CsvExportType.ACTIVITY)) {
                lines = activityService.getFishingActivityListByQueryAsStringArray(fishingActivityQuery, datasets);
            } else {
                lines = fishingTripService.getFishingTripsAsStrings(fishingActivityQuery);
            }
            if(!lines.isEmpty()) {
                lines.forEach(asyncCtx.getResponse().getWriter()::println);
                asyncCtx.getResponse().getWriter().flush();
                
                PaginationDto pagination = fishingActivityQuery.getPagination();
                if(type.equals(CsvExportType.ACTIVITY)) {
                    pagination.setOffset(pagination.getOffset() + ActivitiesToCsvServlet.PAGE_SIZE);
                } else {
                    pagination.setOffset(pagination.getOffset() + TripsToCsvServlet.PAGE_SIZE);
                }
                managedExecutorService.execute(new CsvExportTask(type, asyncCtx, managedExecutorService, fishingActivityQuery, activityService, fishingTripService, datasets));
            } else {
                asyncCtx.complete();
            }
        } catch (ServiceException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
