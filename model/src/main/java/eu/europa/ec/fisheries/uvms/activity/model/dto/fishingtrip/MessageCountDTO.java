/*
 *
 * Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries European Union, 2015-2016.
 *
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package eu.europa.ec.fisheries.uvms.activity.model.dto.fishingtrip;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sanera on 24/08/2016.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageCountDTO {

    @JsonProperty("noOfReports")
    private int noOfReports;

    @JsonProperty("noOfDeclarations")
    private int noOfDeclarations;

    @JsonProperty("noOfNotifications")
    private int noOfNotifications;

    @JsonProperty("noOfCorrections")
    private int noOfCorrections;

    @JsonProperty("noOfFishingOperations")
    private int noOfFishingOperations;

    @JsonProperty("noOfDeletions")
    private int noOfDeletions;

    @JsonProperty("noOfCancellations")
    private int noOfCancellations;


    @JsonProperty("noOfReports")
    public int getNoOfReports() {
        return noOfReports;
    }
    @JsonProperty("noOfReports")
    public void setNoOfReports(int noOfReports) {
        this.noOfReports = noOfReports;
    }
    @JsonProperty("noOfDeclarations")
    public int getNoOfDeclarations() {
        return noOfDeclarations;
    }
    @JsonProperty("noOfDeclarations")
    public void setNoOfDeclarations(int noOfDeclarations) {
        this.noOfDeclarations = noOfDeclarations;
    }
    @JsonProperty("noOfNotifications")
    public int getNoOfNotifications() {
        return noOfNotifications;
    }
    @JsonProperty("noOfNotifications")
    public void setNoOfNotifications(int noOfNotifications) {
        this.noOfNotifications = noOfNotifications;
    }
    @JsonProperty("noOfCorrections")
    public int getNoOfCorrections() {
        return noOfCorrections;
    }
    @JsonProperty("noOfCorrections")
    public void setNoOfCorrections(int noOfCorrections) {
        this.noOfCorrections = noOfCorrections;
    }
    @JsonProperty("noOfFishingOperations")
    public int getNoOfFishingOperations() {
        return noOfFishingOperations;
    }
    @JsonProperty("noOfFishingOperations")
    public void setNoOfFishingOperations(int noOfFishingOperations) {
        this.noOfFishingOperations = noOfFishingOperations;
    }
    @JsonProperty("noOfDeletions")
    public int getNoOfDeletions() {
        return noOfDeletions;
    }
    @JsonProperty("noOfDeletions")
    public void setNoOfDeletions(int noOfDeletions) {
        this.noOfDeletions = noOfDeletions;
    }
    @JsonProperty("noOfCancellations")
    public int getNoOfCancellations() {
        return noOfCancellations;
    }
    @JsonProperty("noOfCancellations")
    public void setNoOfCancellations(int noOfCancellations) {
        this.noOfCancellations = noOfCancellations;
    }

}
