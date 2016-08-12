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

package eu.europa.ec.fisheries.uvms.activity.model.dto.fareport.details;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by padhyad on 8/11/2016.
 */
public class ContactPersonDetailsDTO {

    @JsonProperty("title")
    private String title;

    @JsonProperty("givenName")
    private String givenName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("familyName")
    private String familyName;

    @JsonProperty("familyNamePrefix")
    private String familyNamePrefix;

    @JsonProperty("nameSuffix")
    private String nameSuffix;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("alias")
    private String alias;

    public ContactPersonDetailsDTO() {
    }

    public ContactPersonDetailsDTO(String title, String givenName, String middleName, String familyName, String familyNamePrefix, String nameSuffix, String gender, String alias) {
        this.title = title;
        this.givenName = givenName;
        this.middleName = middleName;
        this.familyName = familyName;
        this.familyNamePrefix = familyNamePrefix;
        this.nameSuffix = nameSuffix;
        this.gender = gender;
        this.alias = alias;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("givenName")
    public String getGivenName() {
        return givenName;
    }

    @JsonProperty("givenName")
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @JsonProperty("middleName")
    public String getMiddleName() {
        return middleName;
    }

    @JsonProperty("middleName")
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @JsonProperty("familyName")
    public String getFamilyName() {
        return familyName;
    }

    @JsonProperty("familyName")
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @JsonProperty("familyNamePrefix")
    public String getFamilyNamePrefix() {
        return familyNamePrefix;
    }

    @JsonProperty("familyNamePrefix")
    public void setFamilyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
    }

    @JsonProperty("nameSuffix")
    public String getNameSuffix() {
        return nameSuffix;
    }

    @JsonProperty("nameSuffix")
    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonProperty("alias")
    public String getAlias() {
        return alias;
    }

    @JsonProperty("alias")
    public void setAlias(String alias) {
        this.alias = alias;
    }
}
