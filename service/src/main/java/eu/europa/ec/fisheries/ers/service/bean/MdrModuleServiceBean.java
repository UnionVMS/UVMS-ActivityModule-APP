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

    public static final String CODE_COLUMN = "code";
    public static final String DESCRIPTION_COLUMN = "description";

    @Override
    public Map<String, List<String>> getAcronymFromMdr(String acronym, String... returnColumns) throws ServiceException {
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

    @Override
    public Map<String, String> getPortDescriptionFromMdr(String acronym,String filter) throws ServiceException {
        loadCache();
        Map<String,String> columnNameValuesMap = new HashMap<>();
        List<ObjectRepresentation> codeList = mdrcache.getEntry(MDRAcronymType.fromValue(acronym));
        for (ObjectRepresentation objectRep : codeList) {
            for (ColumnDataType nameVal : objectRep.getFields()) {
                if (CODE_COLUMN.equals(nameVal.getColumnName()) && filter.equals(nameVal.getColumnValue()) ) {
                    columnNameValuesMap.put(filter,getDescriptionColumnValue(objectRep));
                    return columnNameValuesMap;
                }
            }
        }
        return columnNameValuesMap;
    }

    private String getDescriptionColumnValue(ObjectRepresentation objectRep){
        for (ColumnDataType nameVal : objectRep.getFields()) {
            if (DESCRIPTION_COLUMN.equals(nameVal.getColumnName())) {
                return  nameVal.getColumnValue();
            }
        }
        return null;
    }


    private Map<String, List<String>> prepareColumnNameValuesMap(String[] returnColumns) {
        Map<String, List<String>> columnNameValuesMap = new HashMap<>(returnColumns.length);
        for (String columnName : returnColumns) {
            columnNameValuesMap.put(columnName, new ArrayList<>());
        }
        return columnNameValuesMap;
    }
}
