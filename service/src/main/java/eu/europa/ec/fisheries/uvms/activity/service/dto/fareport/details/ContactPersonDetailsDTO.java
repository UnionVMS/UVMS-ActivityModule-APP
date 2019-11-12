/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries © European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.service.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactPersonDetailsDTO {


    @JsonIgnore
    private String title;

    @JsonProperty("firstName")
    private String givenName;

    @JsonIgnore
    private String middleName;

    @JsonProperty("lastName")
    private String familyName;

    @JsonIgnore
    private String familyNamePrefix;

    @JsonIgnore
    private String nameSuffix;

    @JsonIgnore
    private String gender;

    private String alias;



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFamilyNamePrefix() {
        return familyNamePrefix;
    }

    public void setFamilyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
    }

    public String getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JsonProperty("characteristics")
    public Map<String, String> getCharacteristicsMap() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("title", this.getTitle());
        stringStringHashMap.put("middleName", this.getMiddleName());
        stringStringHashMap.put("familyNamePrefix", this.getFamilyNamePrefix());
        stringStringHashMap.put("nameSuffix", this.getNameSuffix());
        stringStringHashMap.put("gender", this.getGender());
        stringStringHashMap.values().removeAll(Collections.singleton(null));
        if (stringStringHashMap.isEmpty()) {
            stringStringHashMap = null;
        }
        return stringStringHashMap;
    }

}
