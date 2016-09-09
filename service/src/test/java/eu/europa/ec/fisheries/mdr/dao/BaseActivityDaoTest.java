/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.dao;

import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.uvms.BaseDAOTest;

import static com.ninja_squad.dbsetup.Operations.*;

public abstract class BaseActivityDaoTest extends BaseDAOTest {


    protected static final Operation DELETE_ALL_MDR_CR_NAFO_STOCK = sequenceOf(
            deleteAllFrom("activity.mdr_cr_nafo_stock")            
    );

    protected static final Operation INSERT_MDR_CR_NAFO_STOCK_REFERENCE_DATA = sequenceOf(
            insertInto("activity.mdr_cr_nafo_stock")
                    .columns("id", "created_on", "refreshable", "species_code", "species_name", "area_code", "area_description")
                    .values(1L, java.sql.Date.valueOf("2014-12-12"), "Y", "ANG", "Lophius americanus", "N3NO", "NAFO 3N, 3O = FAO 21.3.N + 21.3.O")
                    .build()
    );
    
    protected static final Operation DELETE_ALL_ACTION_TYPE = sequenceOf(
            deleteAllFrom("activity.mdr_action_type")            
    );

    protected static final Operation INSERT_MDR_ACTION_TYPE = sequenceOf(
            insertInto("activity.mdr_action_type")
                    .columns("id", "created_on", "refreshable", "code", "description")
                    .values(1L, java.sql.Date.valueOf("2014-12-12"), "Y", "C", "Creation")
                    .build()
    );

    protected static final Operation INSERT_MDR_CODELIST_STATUS = sequenceOf(
            insertInto("activity.mdr_codelist_status")
                    .columns("id").values(1L)
                    .build()
    );

   
    protected String getSchema() {
        return "activity";
    }

    protected String getPersistenceUnitName() {
        return "testPU";
    }
}