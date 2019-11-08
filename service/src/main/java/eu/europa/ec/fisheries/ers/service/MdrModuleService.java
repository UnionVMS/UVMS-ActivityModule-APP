package eu.europa.ec.fisheries.ers.service;

import java.util.List;
import java.util.Map;

/**
 * Created by patilva on 06/04/2017.
 */
public interface MdrModuleService {

    /**
     * API to call MDR Module by JMS and get the VesselIdentifier type code
     *
     * @param acronym as a pramter
     * @return list of vesselidentifiertype codes
     */
    Map<String, List<String>> getAcronymFromMdr(String acronym, String filter, List<String> columns, Integer nrOfResults, String... returnColumns);
}
