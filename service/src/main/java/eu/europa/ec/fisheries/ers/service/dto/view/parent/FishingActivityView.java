/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.dto.view.parent;

/**
 * Created by kovian on 08/02/2017.
 */
public class FishingActivityView {

    /**
     * All the fields annotaed with @JsonView(FishingActivityView.CommonView.class) will be inherited by all the Views.
     */
    public interface CommonView {}

    public interface Arrival extends CommonView {}
    public interface NotificationOfArrival extends CommonView {}
    public interface FishingOperation extends CommonView {}
    public interface Discard extends CommonView {}
    public interface Landing extends CommonView {}
    public interface Transhipment extends CommonView {}
    public interface NotificationOfTranshipment extends CommonView {}
    public interface Departure extends CommonView {}
    public interface AreaEntry extends CommonView {}
    public interface AreaExit extends CommonView {}
    public interface JointFishingOperation extends CommonView {}
    public interface Relocation extends CommonView {}
    public interface NotificationOfRelocation extends CommonView {}

}
