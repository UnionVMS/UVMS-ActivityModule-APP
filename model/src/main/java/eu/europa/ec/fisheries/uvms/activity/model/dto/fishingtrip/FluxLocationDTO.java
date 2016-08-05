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

/**
 * Created by sanera on 04/08/2016.
 */
public class FluxLocationDTO {

    private String locationType;
    private String fluxLocationListId;
    private String fluxLocationIdentifier;
    private Double longitude;
    private Double latitude;
    private String rfmoCode;


    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getFluxLocationListId() {
        return fluxLocationListId;
    }

    public void setFluxLocationListId(String fluxLocationListId) {
        this.fluxLocationListId = fluxLocationListId;
    }

    public String getFluxLocationIdentifier() {
        return fluxLocationIdentifier;
    }

    public void setFluxLocationIdentifier(String fluxLocationIdentifier) {
        this.fluxLocationIdentifier = fluxLocationIdentifier;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getRfmoCode() {
        return rfmoCode;
    }

    public void setRfmoCode(String rfmoCode) {
        this.rfmoCode = rfmoCode;
    }
}
