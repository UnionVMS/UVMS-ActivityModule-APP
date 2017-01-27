/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import eu.europa.ec.fisheries.ers.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kovian on 25/01/2017.
 */
public class JacksonSubclassSerializerTest {

    @SneakyThrows
    @Test
    public void testSerializing() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        FishingActivityQuery fishQuery = new FishingActivityQuery();

        Map<SearchFilter, String> map = new HashMap<>();
        String val = "someVal";
        map.put(SearchFilter.VESSEL, val);
        fishQuery.setSearchCriteriaMap(map);

        String s = null;
        try {
            s = mapper.writeValueAsString(fishQuery);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(s);

    }

}