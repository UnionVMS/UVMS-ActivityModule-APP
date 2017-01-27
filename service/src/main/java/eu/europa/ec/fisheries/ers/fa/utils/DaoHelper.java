package eu.europa.ec.fisheries.ers.fa.utils;

import eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity;
import eu.europa.ec.fisheries.ers.service.search.FilterMap;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.ers.service.search.GroupCriteriaMapper;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.GroupCriteria;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sanera on 27/01/2017.
 */
@Slf4j
public class DaoHelper {

    public static FaCatchSummaryCustomEntity mapObjectArrayToFaCatchSummaryCustomEntity(Object[] catchSummaryArr, FishingActivityQuery query) throws ServiceException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        // FaCatchSummaryCustomEntity faCatchSummaryCustomEntity;
        Map<GroupCriteria, GroupCriteriaMapper> groupMappings = FilterMap.getGroupByMapping();
        List<GroupCriteria> groupList = query.getGroupByFields();
        if (ArrayUtils.isEmpty(catchSummaryArr))
            return new FaCatchSummaryCustomEntity();
        int objectArrSize = (catchSummaryArr.length - 1);
        if (objectArrSize != groupList.size())  // do not include count field from object array
            throw new ServiceException("selected number of SQL fields do not match with grouping criterias asked by user ");


        Class cls = Class.forName("eu.europa.ec.fisheries.ers.fa.entities.FaCatchSummaryCustomEntity");
        Object faCatchSummaryCustomEntityObj = cls.newInstance();
        Class parameterType = String.class;

        for (int i = 0; i < objectArrSize; i++) {
            GroupCriteria criteria = groupList.get(i);

            if (GroupCriteria.DATE.equals(criteria))
                parameterType = Date.class;

            GroupCriteriaMapper mapper = groupMappings.get(criteria);
            Method method = cls.getDeclaredMethod(mapper.getMethodName(), parameterType);
            method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[i]);
            parameterType = String.class;
        }

        Method method = cls.getDeclaredMethod("setCount", Long.TYPE);
        method.invoke(faCatchSummaryCustomEntityObj, catchSummaryArr[objectArrSize]);

        return (FaCatchSummaryCustomEntity) faCatchSummaryCustomEntityObj;


    }


    public static List<GroupCriteria> mapAreaGroupCriteriaToAllSchemeIdTypes(List<GroupCriteria> groupList){

        if(!groupList.contains(GroupCriteria.AREA))
            return groupList;

        groupList.remove(GroupCriteria.AREA);

        groupList.add(GroupCriteria.TERRITORY);
        groupList.add(GroupCriteria.FAO_AREA);
        groupList.add(GroupCriteria.ICES_STAT_RECTANGLE);
        groupList.add(GroupCriteria.EFFORT_ZONE);
        groupList.add(GroupCriteria.RFMO);
        groupList.add(GroupCriteria.GFCM_GSA);
        groupList.add(GroupCriteria.GFCM_STAT_RECTANGLE);



        return groupList;
    }
}
