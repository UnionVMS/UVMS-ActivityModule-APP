package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;

/**
 * Created by sanera on 29/06/2016.
 */

public interface ActivityService {
     void getFishingActivityDao(FishingActivityQuery query);
}
