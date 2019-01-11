package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.ers.service.MdrModuleService;
import eu.europa.ec.fisheries.ers.service.ModuleService;
import eu.europa.ec.fisheries.ers.service.mdrcache.MDRAcronymType;
import eu.europa.ec.fisheries.ers.service.mdrcache.MDRCache;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.mdr.communication.ColumnDataType;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kovian on 06/04/2017.
 */
@Stateless
@Transactional
@Slf4j
public class MdrModuleServiceBean extends ModuleService implements MdrModuleService {

    @EJB
    private MDRCache mdrcache;

    @Override
    public void loadCache(){
        mdrcache.loadAllMdrCodeLists();
    }

    @Override
    public Map<String, List<String>> getAcronymFromMdr(String acronym, String filter, List<String> filterColumns, Integer nrOfResults, String... returnColumns) throws ServiceException {
        loadCache();
        Map<String, List<String>> columnNameValuesMap = prepareColumnNameValuesMap(returnColumns);
        List<ObjectRepresentation> codeList = mdrcache.getEntry(MDRAcronymType.fromValue(acronym));
        for (ObjectRepresentation objectRep : codeList) {
            for (ColumnDataType nameVal : objectRep.getFields()) {
                if (columnNameValuesMap.containsKey(nameVal.getColumnName())) {
                    columnNameValuesMap.get(nameVal.getColumnName()).add(nameVal.getColumnValue());
                }
            }
        }
        return columnNameValuesMap;
    }

    private Map<String, List<String>> prepareColumnNameValuesMap(String[] returnColumns) {
        Map<String, List<String>> columnNameValuesMap = new HashMap<>(returnColumns.length);
        for (String columnName : returnColumns) {
            columnNameValuesMap.put(columnName, new ArrayList<>());
        }
        return columnNameValuesMap;
    }
}
