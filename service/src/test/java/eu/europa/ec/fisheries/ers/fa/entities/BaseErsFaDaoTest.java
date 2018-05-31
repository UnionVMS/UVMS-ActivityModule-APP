/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportStatusType;
import eu.europa.ec.fisheries.uvms.BaseDAOTest;


public abstract class BaseErsFaDaoTest extends BaseDAOTest {

    public static final Operation ERS_REFERENCE_DATA = sequenceOf(
            insertInto("activity.activity_flux_fa_report_message")
                    .row()
                    .column("id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_flux_party")
                    .row()
                    .column("id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_flux_report_document")
                    .row()
                    .column("id", 1)
                    .column("creation_datetime", java.sql.Date.valueOf("2016-06-27"))
                    .column("purpose_code", "9")
                    .column("purpose_code_list_id", "PURPOSE_CODE_LIST")
                    .column("flux_party_id", 1)
                    .column("flux_fa_report_message_id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_fa_report_document")
                    .row()
                    .column("id", 1)
                    .column("flux_report_document_id", 1)
                    .column("flux_fa_report_message_id", 1)
                    .column("accepted_datetime", java.sql.Date.valueOf("2014-12-01"))
                    .end()
                    .build(),
            insertInto("activity.activity_fishing_activity")
                    .row()
                    .column("ID", 1)
                    .column("type_code", "DEPARTURE")
                    .column("type_code_listid", "FLUX_LOCATION_TYPE")
                    .column("fa_report_document_id", 1)
                    .end().build(),
            insertInto("activity.activity_vessel_transport_means")
                    .row()
                    .column("id", 1)
                    .column("fishing_activity_id", 1)
                    .column("fa_report_document_id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_size_distribution")
                    .row()
                    .column("id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_fa_catch")
                    .row()
                    .column("id", 1)
                    .column("fishing_activity_id", 1)
                    .column("size_distribution_id", 1)
                    .column("type_code", "DIS")
                    .column("type_code_list_id", "FLUX_LOCATION_TYPE")
                    .column("species_code", "BFT")
                    .column("species_code_listid", "CODE_LIST")
                    .end()
                    .build(),
            insertInto("activity.activity_fishing_trip")
                    .row()
                    .column("id", 1)
                    .column("type_code", "JFO")
                    .column("fa_catch_id", 1)
                    .column("fishing_activity_id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_delimited_period")
                    .row()
                    .column("id", 1)
                    .column("fishing_activity_id", 1)
                    .column("fishing_trip_id", 1)
                    .end()
                    .build(),
            insertInto("activity.activity_fishing_trip_Identifier")
                    .row()
                    .column("id", 1)
                    .column("trip_id", 1)
                    .column("fishing_trip_id", 1)
                    .column("trip_scheme_id", "TRIP_SCHEME_ID")
                    .end()
                    .build(),
            insertInto("activity.activity_vessel_identifier")
                    .row()
                    .column("id", 1)
                    .column("vessel_transport_mean_id", 1)
                    .end()
                    .build()
    );
    protected static final Operation DELETE_ALL = sequenceOf(
            deleteAllFrom("activity.activity_size_distribution"),
            deleteAllFrom("activity.activity_fa_catch"),
            deleteAllFrom("activity.activity_fa_report_document"),
            deleteAllFrom("activity.activity_flux_report_document"),
            deleteAllFrom("activity.activity_vessel_transport_means"),
            deleteAllFrom("activity.activity_fishing_trip"),
            deleteAllFrom("activity.activity_fishing_trip_Identifier"),
            deleteAllFrom("activity.activity_flux_fa_report_message")
    );
    protected static final Operation INSERT_ERS_FISHING_TRIP_DATA = sequenceOf(
            insertInto("activity.activity_fishing_trip")
                    .columns("id", "type_code", "type_code_list_id", "fa_catch_id", "fishing_activity_id")
                    .values(1, "JFO", "EU_TRIP_ID", 1, 1)
                    .values(2, "JFO", "EU_TRIP_ID", 2, 2)
                    .values(3, "JFO", "EU_TRIP_ID", 3, 3)
                    .values(4, "JFO", "EU_TRIP_ID", 4, 4)
                    .values(5, "JFO", "EU_TRIP_ID", 5, 5)
                    .values(6, "JFO", "EU_TRIP_ID", 6, 6)
                    .build()

    );
    protected static final Operation INSERT_ERS_FISHING_TRIP_IDENTIFIER_DATA = sequenceOf(
            insertInto("activity.activity_fishing_trip_Identifier")
                    .columns("id", "fishing_trip_id", "trip_id", "trip_scheme_id","calculated_trip_start_date","calculated_trip_end_date")
                    .values(1, 1, "NOR-TRP-20160517234053706", "EU_TRIP_ID","2016-03-17 19:09:00","2017-05-13 11:00:00")
                    .values(2, 2, "NOR-TRP-20160517234053706", "EU_TRIP_ID","2016-03-17 19:09:00","2017-05-13 11:00:00")
                    .values(3, 3, "NOR-TRP-20160517234053706", "EU_TRIP_ID","2016-03-17 19:09:00","2017-05-13 11:00:00")
                    .values(4, 4, "NOR-TRP-20160517234053705", "EU_TRIP_ID","2016-03-17 19:09:00","2017-05-13 11:00:00")
                    .values(5, 5, "NOR-TRP-20160517234053707", "EU_TRIP_ID","2016-03-17 19:09:00","2017-05-13 11:00:00")
                    .values(6, 6, "NOR-TRP-20160517234053708", "EU_TRIP_ID","2016-03-17 19:09:00","2017-05-13 11:00:00")
                    .build()

    );
    protected static final Operation INSERT_ERS_FISHING_ACTIVITY_DATA = sequenceOf(
            insertInto("activity.activity_fishing_activity")
                    .row()
                    .column("ID", 1L)
                    .column("type_code", "DEPARTURE")
                    .column("type_code_listid", "FLUX_LOCATION_TYPE")
                    .column("occurence", java.sql.Date.valueOf("2014-12-12"))
                    .column("reason_code", "REASONCODE")
                    .column("reason_code_list_id", "REASON_CODE_LIST")
                    .column("vessel_activity_code", "VESSEL_ACTIVITY")
                    .column("vessel_activity_code_list_id", "VESSEL_CODE_LIST")
                    .column("fishery_type_code", "FISHERY_CODE")
                    .column("fishery_type_code_list_id", "FISHERY_CODE_LIST")
                    .column("species_target_code", "SPECIES_CODE")
                    .column("species_target_code_list_id", "SPECIES_CODE_LIST")
                    .column("operationsquantity_value", 23)
                    .column("operationsquantity_unitcode", "C62")
                    .column("calculated_operation_quantity", 23.00)
                    .column("fishing_duration_measure", 11.20)
                    .column("fishing_duration_measure_code", "C62")
                    .column("calculated_fishing_duration", 11.20)
                    .column("fa_report_document_id", 1)
                    .column("related_fishing_activity_id", 1)
                    .column("vessel_transport_guid", "AS1222")
                    .column("flag_state", "NLD")
                    .column("calculated_start_time", "2017-02-29 00:00:00")
                    .end().build(),
            insertInto("activity.activity_fishing_activity")
                    .columns("id", "type_code", "type_code_listid", "occurence", "reason_code", "reason_code_list_id", "vessel_activity_code", "vessel_activity_code_list_id",
                            "fishery_type_code", "fishery_type_code_list_id", "species_target_code", "species_target_code_list_id", "operationsquantity_value", "operationsquantity_unitcode",
                            "calculated_operation_quantity", "fishing_duration_measure", "fishing_duration_measure_code", "calculated_fishing_duration", "source_vessel_char_id",
                            "dest_vessel_char_id",  "fa_report_document_id","related_fishing_activity_id","vessel_transport_guid","flag_state","calculated_start_time")
                    .values(2, "ARRIVAL", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 1,null,"AS1222","NLD","2017-03-29 00:00:00")
                    .values(3, "LANDING", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2013-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null,2,null,"AS1222","NLD","2017-04-29 00:00:00")
                    .values(4, "FISHING_OPERATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2012-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 2,null,"AS1222","NLD","2017-05-29 00:00:00")
                    .values(5, "FISHING_OPERATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2011-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null,  6,null,"AS1222","NLD","2017-06-29 00:00:00")
                    .values(6, "FISHING_OPERATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2011-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 3,null,"AS1222","NLD","2017-07-29 00:00:00")
                    .values(7, "FISHING_OPERATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2011-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 4,null,"AS1222","NLD","2017-08-29 00:00:00")
                    .values(8, "FISHING_OPERATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2011-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 4,null,"AS1222","NLD","2017-09-29 00:00:00")
                    .values(9, "RELOCATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2011-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 4,null,"AS1222","NLD","2017-03-29 00:00:00")
                    .values(10, "JOINT_FISHING_OPERATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2011-12-12"), "REASONCODE", "REASON_CODE_LIST", "VESSEL_ACTIVITY", "VESSEL_CODE_LIST",
                            "FISHERY_CODE", "FISHERY_CODE_LIST", "SPECIES_CODE", "SPECIES_CODE_LIST", 23, "C62", 23.00, 11.20, "C62", 11.20, null, null, 4,9,"AS1222","NLD","2017-03-29 00:00:00")
                    .build()

    );
    protected static final Operation INSERT_ERS_SIZE_DISTRIBUTION_DATA = sequenceOf(
            insertInto("activity.activity_size_distribution")
                    .columns("id", "category_code", "category_code_list_id")
                    .values(1, "FA_SIZE_CATEGORY", "FA_SIZE_CATEGORY")
                    .values(2, "FA_SIZE_CATEGORY", "FA_SIZE_CATEGORY")
                    .build()

    );
    protected static final Operation INSERT_ERS_SIZE_DISTRIBUTION_CLASSCODE_DATA = sequenceOf(
            insertInto("activity.activity_size_distribution_classcode")
                    .columns("id", "size_distribution_id", "class_code", "class_code_list_id")
                    .values(1, 1, "LSC", "FISH_SIZE_CLASS")
                    .values(2, 2, "BMS", "FISH_SIZE_CLASS")
                    .build()
    );
    protected static final Operation INSERT_ERS_FA_CATCH_DATA = sequenceOf(
            insertInto("activity.activity_fa_catch")
                    .columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "species_code", "species_code_listid", "unit_quantity", "unit_quantity_code", "calculated_unit_quantity",
                            "weight_measure", "weight_measure_unit_code", "calculated_weight_measure", "usage_code", "usage_code_list_id", "weighing_means_code", "weighing_means_code_list_id", "territory",
                           "fao_area" ,"ices_stat_rectangle" ,"effort_zone" ,"rfmo" ,"gfcm_gsa" ,"gfcm_stat_rectangle","fish_class_code","size_distribution_id","gear_type_code")
                    .values(1, 7, "DIS", "FLUX_LOCATION_TYPE", "BFT", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","IRL",null,"38F1","J",null,null,null,
                            "LSC", 2,"TBB")
                    .values(2, 7, "DIM", "FLUX_LOCATION_TYPE", "BFT", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null,null,null,
                            "LSC",1,"TBB")
                    .values(3, 6, "DIS", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null,null
                            ,null,"LSC", 2,"TBB")
                    .values(4, 6, "DIM", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE",null,null,null,null,null,
                            null,null,"LSC", 1,"TBB")
                    .values(5, 5, "KEPT_IN_NET", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null,
                            null,null, "LSC",2,"TBB")
                    .values(6, 5, "ONBOARD", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null,
                            null,null,"LSC", 1,"TBB")
                    .values(7, 8, "ONBOARD", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE",null,null,null,null,null,null
                            ,null, "LSC",2,"TBB")
                    .values(8, 9, "KEPT_IN_NET", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null
                            ,null,null, "LSC",1,"TBB")
                    .values(9, 9, "ONBOARD", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null,null,
                            null, "LSC",1,"TBB")
                    .values(10, 9, "ALLOCATED_TO_QUOTA", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,
                            "38F1",null,null,null,null,"LSC", 1,"TBB")
                    .values(11, 10, "ONBOARD", "FLUX_LOCATION_TYPE", "ANF", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","GBR",null,"38F1",null,null,
                            null,null, "LSC",1,"TBB")
                    .values(12, 1, "DIS", "FLUX_LOCATION_TYPE", "BFT", "CODE_LIST", 12, "C62", 12.00, 10.00, "kg", 10.00, "PROD_USAGE", "PROD_USAGE_LISTID", "WM_CODE", "WEIGHT_MEASURE","IRL",null,"38F1","J",null,null,null,
                            "LSC", 2,"TBB")
                    .build()
    );
    protected static final Operation INSERT_ERS_AAP_PROCESS_DATA = sequenceOf(
            insertInto("activity.activity_aap_process")
                    .columns("id", "conversion_factor", "fa_catch_id")
                    .values(1, 3, 1)
                    .values(2, 3, 1)
                    .values(3, 3, 2)
                    .values(4, 3, 3)
                    .values(5, 3, 4)
                    .values(6, 3, 5)
                    .values(7, 3, 6)
                    .values(8, 3, 7)
                    .values(9, 3, 7)
                    .build()

    );
    protected static final Operation INSERT_ERS_AAP_PROCESS_CODE_DATA = sequenceOf(
            insertInto("activity.activity_aap_process_code")
                    .columns("id", "aap_process_id", "type_code", "type_code_list_id")
                    .values(1, 1, "GUT", "FISH_PRESENTATION")
                    .values(2, 1, "FRE", "FISH_PRESENTATION")
                    .values(3, 2, "A", "FISH_FRESHNESS")
                    .values(4, 3, "GUT", "FISH_PRESENTATION")
                    .values(5, 4, "GUT", "FISH_PRESENTATION")
                    .values(6,5, "FRE", "FISH_PRESENTATION")
                    .values(7, 6, "FRE", "FISH_PRESENTATION")
                    .values(8, 7, "FRE", "FISH_PRESENTATION")
                    .values(9,8, "FRE", "FISH_FRESHNESS")
                    .build()
    );
    protected static final Operation INSERT_ERS_AAP_PRODUCT_DATA = sequenceOf(
            insertInto("activity.activity_aap_product")
                    .columns("id", "packaging_type_code", "packaging_type_code_list_id", "packaging_unit_avarage_weight", "packaging_weight_unit_code", "calculated_packaging_weight",
                            "packaging_unit_count", "packaging_unit_count_code", "calculated_packaging_unit_count", "aap_process_id", "unit_quantity", "unit_quantity_code", "calculated_unit_quantity",
                            "weight_measure", "weight_measure_unit_code", "calculated_weight_measure", "species_code",
                            "spacies_code_list_id", "weighing_means_code", "weighting_means_code_list_id", "usage_code", "usage_code_list_id")
                    .values(1, "FISH1", "FISH_PACKAGING", 100.00, "C62", 100.00, 1, "C62", 1.0, 1, 2, "C62", 2.0, 15, "C62", 15.00, "PLE", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS", "IND", "PROD_USAGE")
                    .values(2, "FISH1", "FISH_PACKAGING", 100.00, "C62", 100.00, 1, "C62", 1.0 , 1, 2, "C62", 2.0, 10, "C62", 15.00, "PLE", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS", "IND", "PROD_USAGE")
                    .values(3, "FISH1", "FISH_PACKAGING", 100.00, "C62", 100.00, 1, "C62", 1.0 , 1, 2, "C62", 2.0, 34, "C62", 15.00, "PLE", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS", "IND", "PROD_USAGE")
                    .values(4, "FISH1", "FISH_PACKAGING", 100.00, "C62", 100.00, 1, "C62", 1.0 , 1, 2, "C62", 2.0, 34, "C62", 15.00, "PLE", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS", "IND", "PROD_USAGE")
                    .build()
    );
    protected static final Operation INSERT_ERS_FISHING_GEAR_DATA = sequenceOf(
            insertInto("activity.activity_fishing_gear")
                    .columns("id", "fa_catch_id", "fishing_activity_id", "gear_problem_id", "type_code", "type_code_list_id")
                    .values(1, 1, 1, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(2, 2, 2, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(3, 3, 3, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(4, 4, 4, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(5, 5, 5, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(6, 6, 6, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(7, 7, 7, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .values(8, 7, 7, null, "GEAR_TYPE", "GEAR_TYPE_LIST")
                    .build()
    );
    protected static final Operation INSERT_ERS_FISHING_GEAR_ROLE_DATA = sequenceOf(
            insertInto("activity.activity_fishing_gear_role")
                    .columns("id", "fishing_gear_id", "role_code", "role_code_list_id")
                    .values(1, 1, "DEPLOYED", "FA_GEAR_ROLE")
                    .values(2, 1, "DEPLOYED", "FA_GEAR_ROLE")
                    .values(3, 1, "DEPLOYED", "FA_GEAR_ROLE")
                    .values(4, 1, "DEPLOYED", "FA_GEAR_ROLE")
                    .build()
    );
    protected static final Operation INSERT_ERS_FLUX_PARTY_DATA = sequenceOf(
            insertInto("activity.activity_flux_party")
                    .columns("id", "flux_party_name", "name_language_id")
                    .values(1, "OWNER_NAME1", "EN")
                    .values(2, "OWNER_NAME2", "EN")
                    .values(3, "OWNER_NAME3", "EN")
                    .values(4, "OWNER_NAME4", "EN")
                    .values(5, "OWNER_NAME5", "EN")
                    .values(6, "OWNER_NAME5", "EN")
                    .values(7, "OWNER_NAME5", "EN")
                    .build()
    );
    protected static final Operation INSERT_ERS_FLUX_PARTY_IDENTIFIER_DATA = sequenceOf(
            insertInto("activity.activity_flux_party_identifier")
                    .columns("id", "flux_party_id", "flux_party_identifier_id", "flux_party_identifier_scheme_id")
                    .values(1, 1, "OWNER1", "FLUX_PARTY_SCHEME_ID")
                    .values(2, 1, "OWNER2", "FLUX_PARTY_SCHEME_ID")
                    .values(3, 1, "OWNER3", "FLUX_PARTY_SCHEME_ID")
                    .values(4, 2, "OWNER22", "FLUX_PARTY_SCHEME_ID")
                    .values(5, 3, "OWNER33", "FLUX_PARTY_SCHEME_ID")
                    .values(6, 4, "OWNER4", "FLUX_PARTY_SCHEME_ID")
                    .values(7, 5, "OWNER5", "FLUX_PARTY_SCHEME_ID")
                    .values(8, 5, "OWNER6", "FLUX_PARTY_SCHEME_ID")
                    .build()
    );
    //flux_report_document_id
    protected static final Operation INSERT_ERS_FLUX_REPORT_DOCUMENT_DATA = sequenceOf(
            insertInto("activity.activity_flux_report_document")
                    .columns("id", "reference_id", "creation_datetime", "purpose_code", "purpose_code_list_id", "purpose", "flux_party_id", "flux_fa_report_message_id")
                    .values(1, null, "2016-06-27 07:47:31.711", "9", "PURPOSE_CODE_LIST", null, 1, 1)
                    .values(2, null, "2016-06-27 07:47:31.711", "5", "PURPOSE_CODE_LIST", null, 2, 2)
                    .values(3, null, "2016-06-27 07:47:31.711", "9", "PURPOSE_CODE_LIST", null, 3, 3)
                    .values(4, null, "2016-06-27 07:47:31.711", "5", "PURPOSE_CODE_LIST", null, 4, 4)
                    .values(5, null, "2016-06-27 07:47:31.711", "9", "PURPOSE_CODE_LIST", null, 5, 5)
                    .values(6, null, "2016-06-27 07:47:31.711", "9", "PURPOSE_CODE_LIST", null, 6, 6)
                    .values(7, null, "2016-06-27 07:47:31.711", "9", "PURPOSE_CODE_LIST", null, 7, 7)
                    .build()
    );
    protected static final Operation INSERT_ERS_FLUX_REPORT_IDENTIFIER_DATA = sequenceOf(
            insertInto("activity.activity_flux_report_identifier")
                    .columns("id", "flux_report_document_id", "flux_report_identifier_id", "flux_report_identifier_scheme_id")
                    .values(1, 1, "FLUX_REPORT_ID1", "FLUX_SCHEME_ID1")
                    .values(2, 1, "FLUX_REPORT_ID2", "FLUX_SCHEME_ID1")
                    .values(3, 1, "FLUX_REPORT_ID3", "FLUX_SCHEME_ID1")
                    .values(4, 2, "FLUX_REPORT_ID4", "FLUX_SCHEME_ID1")
                    .values(5, 3, "FLUX_REPORT_ID5", "FLUX_SCHEME_ID1")
                    .values(6, 4, "FLUX_REPORT_ID6", "FLUX_SCHEME_ID1")
                    .values(7, 5, "FLUX_REPORT_ID7", "FLUX_SCHEME_ID1")
                    .values(8, 5, "FLUX_REPORT_ID8", "FLUX_SCHEME_ID1")
                    .build()
    );
    //vessel_transport_means_id
    protected static final Operation INSERT_ERS_VESSEL_TRANSPORT_MEANS_DATA = sequenceOf(
            insertInto("activity.activity_vessel_transport_means")
                    .columns("id", "role_code", "role_code_list_id", "name", "registration_event_id", "country_scheme_id", "country","guid","fishing_activity_id","fa_report_document_id")
                    .values(1, "ROLE_CODE", "LIST_ID", "vessel1", null, "TERRITORY", "FRA","AC1111",1,1)
                    .values(2, "ROLE_CODE", "LIST_ID", "vessel2", null,"TERRITORY", "FRA","AC1111",2,2)
                    .values(3, "ROLE_CODE", "LIST_ID", "vessel3", null, "TERRITORY", "ESP","AC1111",3,3)
                    .values(4, "ROLE_CODE", "LIST_ID", "vessel4", null, "TERRITORY", "ESP","AC1111",4,4)
                    .values(5, "ROLE_CODE", "LIST_ID", "vessel4", null, "TERRITORY", "FRA","AC1111",5,5)
                    .values(6, "ROLE_CODE", "LIST_ID", "vessel4", null, "TERRITORY", "FRA","AC1111",6,6)
                    .values(7, "ROLE_CODE", "LIST_ID", "vessel4", null, "TERRITORY", "FRA","AC1111",7,7)
                    .build()
    );
    protected static final Operation INSERT_ERS_VESSEL_IDENTIFIERS_DATA = sequenceOf(
            insertInto("activity.activity_vessel_identifier")
                    .columns("id", "vessel_transport_mean_id", "vessel_identifier_id", "vessel_identifier_scheme_id")
                    .values(1, 1, "CFR123", "CFR")
                    .values(2, 1, "IRCS123", "IRCS")
                    .values(3, 1, "EXT_MARK123", "EXT_MARK")
                    .values(4, 2, "CFR123", "CFR")
                    .values(5, 2, "EXT_MARK123", "EXT_MARK")
                    .values(6, 5, "EXT_MARK123", "EXT_MARK")
                    .values(7, 6, "EXT_MARK123", "EXT_MARK")
                    .build()
    );
    protected static final Operation INSERT_FLUX_LOCATION = sequenceOf(
            insertInto("activity.activity_flux_location")
                    .columns("id", "fa_catch_id", "fishing_activity_id", "type_code", "type_code_list_id", "country_id", "country_id_scheme_id", "rfmo_code", "longitude", "latitude",
                            "flux_location_type", "flux_location_identifier", "flux_location_identifier_scheme_id", "geopolitical_region_code", "geopolitical_region_code_list_id", "namevalue",
                            "sovereign_rights_country_code", "jurisdiction_country_code", "altitude", "system_id")
                    .values(1, 1, 1, "AREA", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "J", "EFFORT_ZONE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(2, 1, 1, "AREA", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "IRL", "TERRITORY", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(3, 1, 1, "AREA", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(4, 2, 1, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(5, 2, 1, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "GBR", "TERRITORY", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(6, 6, 6, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(8, 5, 7, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "GBR", "TERRITORY", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(7, 6, 6, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "GBR", "TERRITORY", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(9, 5,7, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(10, 3, 7, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "GBR", "TERRITORY", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(11, 3,7, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(12, 9,9, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .values(13, 9,9, "LOCATION", "FAO_AREA", "ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null, "AREA", "38F1", "ICES_STAT_RECTANGLE", "GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
                            "FLUX_LOCATION_NAME", null, null, null, null)
                    .build()

    );

    protected static final Operation INSERT_CONTACT_PERSON = sequenceOf(
            insertInto("activity.activity_contact_person")
                    .columns("id", "title", "given_name", "middle_name", "family_name", "family_name_prefix", "name_suffix", "gender", "alias")
                    .values(1, "Mr", "MARK", "DAVID", "BOSE", "ARR", "PI", "MALE", null)
                    .values(2, "Mrs", "TIA", "DAVID", "BOSE", "ARR", "PI", "FEMALE", null)
                    .build()

    );
    protected static final Operation INSERT_CONTACT_PARTY = sequenceOf(
            insertInto("activity.activity_contact_party")
                    .columns("id", "vessel_transport_means_id", "contact_person_id")
                    .values(1, 1, 1)
                    .values(2, 2, 2)
                    .build()

    );
    protected static final Operation INSERT_CONTACT_PARTY_ROLE = sequenceOf(
            insertInto("activity.activity_contact_party_role")
                    .columns("id", "contact_party_id", "role_code", "role_code_list_id")
                    .values(1, 1, "MASTER", "FLUX_CONTACT_ROLE")
                    .values(2, 1, "AGENT", "FLUX_CONTACT_ROLE")
                    .build()
    );
    protected static final Operation INSERT_DELIMITED_PERIOD = sequenceOf(
            insertInto("activity.activity_delimited_period")
                    .columns("id", "start_date", "end_date", "durationmeasure_value", "durationMeasure_unitcode", "calculated_duration", "fishing_activity_id", "fishing_trip_id")
                    .values(1, "2010-06-27 07:47:31.711", "2013-06-27 07:47:31.711", 1, "C62", 1.00, 1, 1)
                    .values(2, "2012-05-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, "C62", 1.00, 1, 2)
                    .values(3, "2013-06-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, "C62", 1.00, 1, 3)
                    .values(4, "2009-06-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, "C62", 1.00, 1, 4)
                    .values(5, "2011-06-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, "C62", 1.00, 2, 5)
                    .values(6, "2011-06-27 07:47:31.711", "2014-06-27 07:47:31.711", 2, "C62", 1.00, 2, 5)
                    .values(7, "2012-06-27 07:47:31.711", "2014-06-27 07:47:31.711", 3, "C62", 1.00, 3, 5)
                    .values(8, "2011-06-27 07:47:31.711", "2014-06-27 07:47:31.711", 3, "C62", 1.00, 3, 5)
                    .values(9, "2010-06-27 07:47:31.711", "2014-06-27 07:47:31.711", 4, "C62", 1.00, 4, 5)
                    .build()

    );
    protected static final Operation INSERT_ERS_FA_REPORT_DOCUMENT_DATA = sequenceOf(
            insertInto("activity.activity_fa_report_document")
                    .columns("id", "type_code", "type_code_list_id", "accepted_datetime", "flux_report_document_id",  "fmc_marker", "fmc_marker_list_id", "status",
                            "source", "flux_fa_report_message_id")
                    .values(1, "DECLARATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2014-12-12"), 1,  "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .values(2, "DECLARATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-09-12"), 2,  "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .values(3, "DECLARATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-08-12"), 3, "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .values(4, "NOTIFICATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-07-12"), 4, "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .values(5, "NOTIFICATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-10-08"), 5, "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .values(6, "NOTIFICATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-10-08"), 6, "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .values(7, "NOTIFICATION", "FLUX_LOCATION_TYPE", java.sql.Date.valueOf("2015-10-08"), 7, "fmc", "fmc_list", FaReportStatusType.NEW, "FLUX", 1)
                    .build()
    );
    protected static final Operation INSERT_FLUX_FA_REPORT_MESSAGE_DATA = sequenceOf(
            insertInto("activity.activity_flux_fa_report_message")
                    .columns("id")
                    .values(1)
                    .values(2)
                    .values(3)
                    .values(4)
                    .values(5)
                    .values(6)
                    .values(7)
                    .build()
    );
    protected static final Operation INSERT_FLUX_CHARACTERISTIC = sequenceOf(
            insertInto("activity.activity_flap_document")
                    .columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "value_measure","value_measure_unit_code", "calculated_value_measure", "value_date_time","value_indicator", "value_code", "value_text",
                            "value_language_id", "value_quantity","value_quantity_code", "calculated_value_quantity", "description","description_language_id", "fa_catch_id", "specified_flap_document_id",
                            "specified_flux_location_id")
                    .values(1, 1, "TYPECODE_1", "TYPE_CODE_LIST", null, null, null, null,null, null, null, null, null,null, null, null,null, 1, 1, 1)
                    .build(),
            insertInto("activity.activity_flap_document")
                    .columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "value_measure","value_measure_unit_code", "calculated_value_measure", "value_date_time","value_indicator", "value_code", "value_text",
                            "value_language_id", "value_quantity","value_quantity_code", "calculated_value_quantity", "description","description_language_id", "fa_catch_id", "specified_flap_document_id",
                            "specified_flux_location_id")
                    .values(2, 2, "TYPECODE_1", "TYPE_CODE_LIST", null, null, null, null,null, null, null, null, null,null, null, null,null, 2, 2, 2)
                    .build()
    );
    protected static final Operation INSERT_FLAP_DOCUMENT = sequenceOf(
            insertInto("activity.activity_flap_document")
                    .columns("id", "vessel_transport_means_id", "flap_document_id", "flap_document_scheme_id",
                            "fishing_activity_id")
                    .values(1, 1, null, null,1)
                    .build(),
            insertInto("activity.activity_flap_document")
                    .columns("id", "vessel_transport_means_id", "flap_document_id", "flap_document_scheme_id",
                            "fishing_activity_id")
                    .values(2, 2, null, null,2)
                    .build()
    );

    public void prepare() {
        Operation operation = sequenceOf(
                DELETE_ALL,
                INSERT_FLUX_FA_REPORT_MESSAGE_DATA,
                INSERT_ERS_FLUX_PARTY_DATA,
                INSERT_ERS_FLUX_PARTY_IDENTIFIER_DATA,
                INSERT_ERS_FLUX_REPORT_DOCUMENT_DATA,
                INSERT_ERS_FLUX_REPORT_IDENTIFIER_DATA,
                INSERT_ERS_FA_REPORT_DOCUMENT_DATA,
                INSERT_ERS_FISHING_ACTIVITY_DATA,
                INSERT_ERS_VESSEL_TRANSPORT_MEANS_DATA,
                INSERT_ERS_VESSEL_IDENTIFIERS_DATA,
                INSERT_CONTACT_PERSON,
                INSERT_CONTACT_PARTY,
                INSERT_CONTACT_PARTY_ROLE,
                INSERT_ERS_SIZE_DISTRIBUTION_DATA,
                INSERT_ERS_SIZE_DISTRIBUTION_CLASSCODE_DATA,
                INSERT_ERS_FA_CATCH_DATA,
                INSERT_ERS_AAP_PROCESS_DATA,
                INSERT_ERS_AAP_PROCESS_CODE_DATA,
                INSERT_ERS_AAP_PRODUCT_DATA,
                INSERT_FLUX_LOCATION,
                INSERT_ERS_FISHING_GEAR_DATA,
                INSERT_ERS_FISHING_GEAR_ROLE_DATA,
                INSERT_ERS_FISHING_TRIP_DATA,
                INSERT_ERS_FISHING_TRIP_IDENTIFIER_DATA,
                INSERT_DELIMITED_PERIOD);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    public void deleteAll() {
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), sequenceOf(DELETE_ALL));
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @Override
    protected String getSchema() {
        return "activity";
    }

    @Override
    protected String getPersistenceUnitName() {
        return "testPU";
    }

}