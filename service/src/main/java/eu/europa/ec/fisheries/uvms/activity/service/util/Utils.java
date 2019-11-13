/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.util;


import lombok.NoArgsConstructor;

import java.util.Collections;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Utils {

    /**
     * Add a quantity to another quantity checking that neither of the values is null;
     * Furthermore if the value calculated up until now is different then null then it returns this value instead of null
     *
     * @param actualMeasureToAdd
     * @param measureSubTotalToAddTo
     * @return
     */
    public static Double addDoubles(Double actualMeasureToAdd, Double measureSubTotalToAddTo) {
        if (actualMeasureToAdd != null && !(Math.abs(actualMeasureToAdd - 0.0) < 0.00000001)) {
            if (measureSubTotalToAddTo == null) {
                return actualMeasureToAdd;
            }

            return actualMeasureToAdd + measureSubTotalToAddTo;
        }

        return measureSubTotalToAddTo;
    }

    public static <T> Iterable<T> safeIterable(Iterable<T> iterable) {
        return iterable == null ? Collections.<T>emptyList() : iterable;
    }
}
