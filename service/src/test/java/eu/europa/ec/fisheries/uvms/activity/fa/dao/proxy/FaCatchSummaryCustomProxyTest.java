/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy;

import eu.europa.ec.fisheries.uvms.BaseUnitilsTest;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomChildProxy;
import eu.europa.ec.fisheries.uvms.activity.fa.dao.proxy.FaCatchSummaryCustomProxy;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class FaCatchSummaryCustomProxyTest extends BaseUnitilsTest {

    @Test
    public void testEqualsAndHashCode1() {

        FaCatchSummaryCustomProxy proxy1 = new FaCatchSummaryCustomProxy();

        proxy1.setEffortZone("eff");
        proxy1.setFaoArea("fao");


        FaCatchSummaryCustomProxy proxy2 = new FaCatchSummaryCustomProxy();

        proxy2.setEffortZone("eff");
        proxy2.setFaoArea("fao");

        assertTrue(proxy2.equals(proxy1));

    }

    @Test
    public void testEqualsAndHashCode2() {

        FaCatchSummaryCustomProxy proxy1 = new FaCatchSummaryCustomProxy();

        proxy1.setEffortZone("eff");
        proxy1.setFaoArea("fao");
        proxy1.setCount(2);
        proxy1.setFishClass("cl");

        FaCatchSummaryCustomProxy proxy2 = new FaCatchSummaryCustomProxy();

        proxy2.setEffortZone("eff");
        proxy2.setFaoArea("fao");
        proxy2.setCount(3);
        proxy2.setFishClass("class");

        assertTrue(proxy2.equals(proxy1));

    }


    @Test
    public void testEqualsAndHashCode3() {

        FaCatchSummaryCustomChildProxy proxy1 = new FaCatchSummaryCustomChildProxy();

        proxy1.setEffortZone("eff");
        proxy1.setFaoArea("fao");
        proxy1.setCount(2);
        proxy1.setFishClass("cl");
        proxy1.setPresentation("pres");

        FaCatchSummaryCustomChildProxy proxy2 = new FaCatchSummaryCustomChildProxy();

        proxy2.setEffortZone("eff");
        proxy2.setFaoArea("fao");
        proxy2.setCount(3);
        proxy2.setFishClass("class");
        proxy2.setPresentation("present");

        assertTrue(proxy2.equals(proxy1));

    }

}
