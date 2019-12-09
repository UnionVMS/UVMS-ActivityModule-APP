/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.rest;

import eu.europa.ec.fisheries.uvms.activity.rest.resources.ConfigResource;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.FACatchResource;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.FishingActivityResource;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.FishingActivityViewsResource;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.FishingTripResource;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.ReportDocumentResource;
import eu.europa.ec.fisheries.uvms.activity.rest.resources.XMLResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class RestActivator extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(FishingActivityResource.class);
        set.add(FishingTripResource.class);
        set.add(ConfigResource.class);
        set.add(FishingActivityViewsResource.class);
        set.add(FACatchResource.class);
        set.add(XMLResource.class);
        set.add(ReportDocumentResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
