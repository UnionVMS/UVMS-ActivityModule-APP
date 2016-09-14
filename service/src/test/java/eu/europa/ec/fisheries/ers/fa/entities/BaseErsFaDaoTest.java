/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.uvms.BaseDAOTest;

import static com.ninja_squad.dbsetup.Operations.*;


public abstract class BaseErsFaDaoTest extends BaseDAOTest{
	
	 protected static final Operation DELETE_ALL = sequenceOf(
			  deleteAllFrom("activity.activity_size_distribution"),
	            deleteAllFrom("activity.activity_fa_catch"),
	            deleteAllFrom("activity.activity_fa_report_document"),
	            deleteAllFrom("activity.activity_flux_report_document"),
	           deleteAllFrom("activity.activity_vessel_transport_means"),
			 deleteAllFrom("activity.activity_fishing_trip"),
			 deleteAllFrom("activity.activity_fishing_trip_Identifier")


	    );


	protected static final Operation INSERT_ERS_FISHING_TRIP_DATA = sequenceOf(
			insertInto("activity.activity_fishing_trip")
					.columns("id", "type_code", "type_code_list_id", "fa_catch_id", "fishing_activity_id")
					.values(1, "JFO", "EU_TRIP_ID", 1, 1)
					.build(),
			insertInto("activity.activity_fishing_trip")
					.columns("id", "type_code", "type_code_list_id", "fa_catch_id", "fishing_activity_id")
					.values(2, "JFO", "EU_TRIP_ID", 2, 2)
					.build(),
			insertInto("activity.activity_fishing_trip")
					.columns("id", "type_code", "type_code_list_id", "fa_catch_id", "fishing_activity_id")
					.values(3, "JFO", "EU_TRIP_ID", 3, 3)
					.build(),
			insertInto("activity.activity_fishing_trip")
					.columns("id", "type_code", "type_code_list_id", "fa_catch_id", "fishing_activity_id")
					.values(4, "JFO", "EU_TRIP_ID", 4, 4)
					.build(),
			insertInto("activity.activity_fishing_trip")
					.columns("id", "type_code", "type_code_list_id", "fa_catch_id", "fishing_activity_id")
					.values(5, "JFO", "EU_TRIP_ID", 5, 5)
					.build()
	);

	protected static final Operation INSERT_ERS_FISHING_TRIP_IDENTIFIER_DATA = sequenceOf(
			insertInto("activity.activity_fishing_trip_Identifier")
					.columns("id", "fishing_trip_id", "trip_id", "trip_scheme_id")
					.values(1, 1, "NOR-TRP-20160517234053706", "EU_TRIP_ID")
					.build(),
			insertInto("activity.activity_fishing_trip_Identifier")
					.columns("id", "fishing_trip_id", "trip_id", "trip_scheme_id")
					.values(2, 2, "NOR-TRP-20160517234053706", "EU_TRIP_ID")
					.build(),
			insertInto("activity.activity_fishing_trip_Identifier")
					.columns("id", "fishing_trip_id", "trip_id", "trip_scheme_id")
					.values(3, 3, "NOR-TRP-20160517234053706", "EU_TRIP_ID")
					.build(),
			insertInto("activity.activity_fishing_trip_Identifier")
					.columns("id", "fishing_trip_id", "trip_id", "trip_scheme_id")
					.values(4, 4, "NOR-TRP-20160517234053705", "EU_TRIP_ID")
					.build(),
			insertInto("activity.activity_fishing_trip_Identifier")
					.columns("id", "fishing_trip_id", "trip_id", "trip_scheme_id")
					.values(5, 5, "NOR-TRP-20160517234053707", "EU_TRIP_ID")
					.build()
	);
	    

	  	protected static final Operation INSERT_ERS_FISHING_ACTIVITY_DATA = sequenceOf(
	            insertInto("activity.activity_fishing_activity")
	                    .columns("id", "type_code", "type_code_listid", "occurence", "reason_code", "reason_code_list_id", "vessel_activity_code","vessel_activity_code_list_id",
	                    		"fishery_type_code","fishery_type_code_list_id","species_target_code","species_target_code_list_id","operation_quantity","fishing_duration_measure","source_vessel_char_id",
								"dest_vessel_char_id","flap_document_id","flap_document_scheme_id","fa_report_document_id")
	                 .values(1, "DEPARTURE","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2014-12-12"),"REASONCODE","REASON_CODE_LIST","VESSEL_ACTIVITY","VESSEL_CODE_LIST",
	                   		"FISHERY_CODE","FISHERY_CODE_LIST","SPECIES_CODE","SPECIES_CODE_LIST",23,11.20,null,null,null,null,1)
	                    .build(),
				insertInto("activity.activity_fishing_activity")
						.columns("id", "type_code", "type_code_listid", "occurence", "reason_code", "reason_code_list_id", "vessel_activity_code","vessel_activity_code_list_id",
								"fishery_type_code","fishery_type_code_list_id","species_target_code","species_target_code_list_id","operation_quantity","fishing_duration_measure","source_vessel_char_id","dest_vessel_char_id","flap_document_id","flap_document_scheme_id","fa_report_document_id")
						.values(2, "ARRIVAL","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2015-12-12"),"REASONCODE","REASON_CODE_LIST","VESSEL_ACTIVITY","VESSEL_CODE_LIST",
								"FISHERY_CODE","FISHERY_CODE_LIST","SPECIES_CODE","SPECIES_CODE_LIST",23,11.20,null,null,null,null,1)
						.build(),
				insertInto("activity.activity_fishing_activity")
						.columns("id", "type_code", "type_code_listid", "occurence", "reason_code", "reason_code_list_id", "vessel_activity_code","vessel_activity_code_list_id",
								"fishery_type_code","fishery_type_code_list_id","species_target_code","species_target_code_list_id","operation_quantity","fishing_duration_measure","source_vessel_char_id","dest_vessel_char_id","flap_document_id","flap_document_scheme_id","fa_report_document_id")
						.values(3, "LANDING","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2013-12-12"),"REASONCODE","REASON_CODE_LIST","VESSEL_ACTIVITY","VESSEL_CODE_LIST",
								"FISHERY_CODE","FISHERY_CODE_LIST","SPECIES_CODE","SPECIES_CODE_LIST",23,11.20,null,null,null,null,2)
						.build(),
				insertInto("activity.activity_fishing_activity")
						.columns("id", "type_code", "type_code_listid", "occurence", "reason_code", "reason_code_list_id", "vessel_activity_code","vessel_activity_code_list_id",
								"fishery_type_code","fishery_type_code_list_id","species_target_code","species_target_code_list_id","operation_quantity","fishing_duration_measure","source_vessel_char_id","dest_vessel_char_id","flap_document_id","flap_document_scheme_id","fa_report_document_id")
						.values(4, "FISHING_OPERATION","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2012-12-12"),"REASONCODE","REASON_CODE_LIST","VESSEL_ACTIVITY","VESSEL_CODE_LIST",
								"FISHERY_CODE","FISHERY_CODE_LIST","SPECIES_CODE","SPECIES_CODE_LIST",23,11.20,null,null,null,null,2)
						.build(),
				insertInto("activity.activity_fishing_activity")
						.columns("id", "type_code", "type_code_listid", "occurence", "reason_code", "reason_code_list_id", "vessel_activity_code","vessel_activity_code_list_id",
								"fishery_type_code","fishery_type_code_list_id","species_target_code","species_target_code_list_id","operation_quantity","fishing_duration_measure","source_vessel_char_id","dest_vessel_char_id","flap_document_id","flap_document_scheme_id","fa_report_document_id")
						.values(5, "FISHING_OPERATION","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2011-12-12"),"REASONCODE","REASON_CODE_LIST","VESSEL_ACTIVITY","VESSEL_CODE_LIST",
								"FISHERY_CODE","FISHERY_CODE_LIST","SPECIES_CODE","SPECIES_CODE_LIST",23,11.20,null,null,null,null,2)
						.build()
	   );


		protected static final Operation INSERT_ERS_SIZE_DISTRIBUTION_DATA = sequenceOf(
	            insertInto("activity.activity_size_distribution")
	                    .columns("id", "class_code", "class_code_list_id", "category_code", "category_code_list_id")
	                    .values(1, "LSC","FA_SIZE_CLASS","FA_SIZE_CATEGORY","FA_SIZE_CATEGORY")
	                    .build()
	    );
		
		protected static final Operation INSERT_ERS_FA_CATCH_DATA = sequenceOf(
	            insertInto("activity.activity_fa_catch")
	                    .columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "species_code", "species_code_listid", "unit_quantity","weight_measure","weight_measure_unit_code","weight_measure_list_id","usage_code","usage_code_list_id","weighing_means_code","weighing_means_code_list_id","size_distribution_id")
	                    .values(1, 1,"AREA","FLUX_LOCATION_TYPE","beagle1","CODE_LIST",12,10.00,"kg","KG_LISTID","PROD_USAGE","PROD_USAGE_LISTID","WM_CODE","WEIGHT_MEASURE",1)
	                    .build(),
				insertInto("activity.activity_fa_catch")
						.columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "species_code", "species_code_listid", "unit_quantity","weight_measure","weight_measure_unit_code","weight_measure_list_id","usage_code","usage_code_list_id","weighing_means_code","weighing_means_code_list_id","size_distribution_id")
						.values(2, 2,"AREA","FLUX_LOCATION_TYPE","beagle1","CODE_LIST",12,10.00,"kg","KG_LISTID","PROD_USAGE","PROD_USAGE_LISTID","WM_CODE","WEIGHT_MEASURE",1)
						.build(),
				insertInto("activity.activity_fa_catch")
						.columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "species_code", "species_code_listid", "unit_quantity","weight_measure","weight_measure_unit_code","weight_measure_list_id","usage_code","usage_code_list_id","weighing_means_code","weighing_means_code_list_id","size_distribution_id")
						.values(3, 3,"AREA","FLUX_LOCATION_TYPE","beagle1","CODE_LIST",12,10.00,"kg","KG_LISTID","PROD_USAGE","PROD_USAGE_LISTID","WM_CODE","WEIGHT_MEASURE",1)
						.build(),
				insertInto("activity.activity_fa_catch")
						.columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "species_code", "species_code_listid", "unit_quantity","weight_measure","weight_measure_unit_code","weight_measure_list_id","usage_code","usage_code_list_id","weighing_means_code","weighing_means_code_list_id","size_distribution_id")
						.values(4, 4,"AREA","FLUX_LOCATION_TYPE","beagle1","CODE_LIST",12,10.00,"kg","KG_LISTID","PROD_USAGE","PROD_USAGE_LISTID","WM_CODE","WEIGHT_MEASURE",1)
						.build(),
				insertInto("activity.activity_fa_catch")
						.columns("id", "fishing_activity_id", "type_code", "type_code_list_id", "species_code", "species_code_listid", "unit_quantity","weight_measure","weight_measure_unit_code","weight_measure_list_id","usage_code","usage_code_list_id","weighing_means_code","weighing_means_code_list_id","size_distribution_id")
						.values(5, 5,"AREA","FLUX_LOCATION_TYPE","beagle1","CODE_LIST",12,10.00,"kg","KG_LISTID","PROD_USAGE","PROD_USAGE_LISTID","WM_CODE","WEIGHT_MEASURE",1)
						.build()
	   );


	protected static final Operation INSERT_ERS_AAP_PROCESS_DATA = sequenceOf(
			insertInto("activity.activity_aap_process")
					.columns("id", "type_code", "type_code_list_id", "conversion_factor", "fa_catch_id")
					.values(1, "FISH_PRESENTATION", "FLUX_ PROCESS_TYPE", 3, 1)
					.build(),
			insertInto("activity.activity_aap_process")
					.columns("id", "type_code", "type_code_list_id", "conversion_factor", "fa_catch_id")
					.values(2, "FISH_PRESERVATION", "FLUX_ PROCESS_TYPE", 3, 1)
					.build()
	);

	protected static final Operation INSERT_ERS_AAP_PRODUCT_DATA = sequenceOf(
			insertInto("activity.activity_aap_product")
					.columns("id", "packaging_type_code", "packaging_type_code_list_id", "packaging_unit_avarage_weight","packaging_unit_count", "aap_process_id", "unit_quantity", "weight_measure","species_code",
							"spacies_code_list_id", "weighing_means_code", "weighting_means_code_list_id","usage_code", "usage_code_list_id")
					.values(1, "FISH1", "FISH_PACKAGING", null,null, 1, 2, 15,"HAD", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS","IND", "PROD_USAGE")
					.build(),
			insertInto("activity.activity_aap_product")
					.columns("id", "packaging_type_code", "packaging_type_code_list_id", "packaging_unit_avarage_weight","packaging_unit_count", "aap_process_id", "unit_quantity", "weight_measure","species_code",
							"spacies_code_list_id", "weighing_means_code", "weighting_means_code_list_id","usage_code", "usage_code_list_id")
					.values(2, "FISH1", "FISH_PACKAGING", null,null, 1, 2, 10,"HAD", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS","IND", "PROD_USAGE")
					.build(),
			insertInto("activity.activity_aap_product")
					.columns("id", "packaging_type_code", "packaging_type_code_list_id", "packaging_unit_avarage_weight","packaging_unit_count", "aap_process_id", "unit_quantity", "weight_measure","species_code",
							"spacies_code_list_id", "weighing_means_code", "weighting_means_code_list_id","usage_code", "usage_code_list_id")
					.values(3, "FISH1", "FISH_PACKAGING", null,null, 1, 2, 34,"PLE", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS","IND", "PROD_USAGE")
					.build(),
			insertInto("activity.activity_aap_product")
					.columns("id", "packaging_type_code", "packaging_type_code_list_id", "packaging_unit_avarage_weight","packaging_unit_count", "aap_process_id", "unit_quantity", "weight_measure","species_code",
							"spacies_code_list_id", "weighing_means_code", "weighting_means_code_list_id","usage_code", "usage_code_list_id")
					.values(4, "FISH1", "FISH_PACKAGING", null,null, 1, 2, 34,"PLE", "FAO_SPECIES", "WEIGHED", "WEIGHT_MEANS","IND", "PROD_USAGE")
					.build()
	);


	   protected static final Operation INSERT_ERS_FISHING_GEAR_DATA = sequenceOf(
			insertInto("activity.activity_fishing_gear")
					.columns("id", "fa_catch_id", "fishing_activity_id", "gear_problem_id", "type_code","type_code_list_id", "role_code", "role_code_list_id")
					.values(1, 1, 1, null, "GEAR_TYPE","GEAR_TYPE_LIST", "DEPLOYED", "FA_GEAR_ROLE")
					.build(),
			   insertInto("activity.activity_fishing_gear")
					   .columns("id", "fa_catch_id", "fishing_activity_id", "gear_problem_id", "type_code","type_code_list_id", "role_code", "role_code_list_id")
					   .values(2, 1, 1, null, "GEAR_TYPE","GEAR_TYPE_LIST", "DEPLOYED", "FA_GEAR_ROLE")
					   .build(),
			   insertInto("activity.activity_fishing_gear")
					   .columns("id", "fa_catch_id", "fishing_activity_id", "gear_problem_id", "type_code","type_code_list_id", "role_code", "role_code_list_id")
					   .values(3, 1, 1, null, "GEAR_TYPE","GEAR_TYPE_LIST", "DEPLOYED", "FA_GEAR_ROLE")
					   .build(),
			   insertInto("activity.activity_fishing_gear")
					   .columns("id", "fa_catch_id", "fishing_activity_id", "gear_problem_id", "type_code","type_code_list_id", "role_code", "role_code_list_id")
					   .values(4, 1, 1, null, "GEAR_TYPE","GEAR_TYPE_LIST", "DEPLOYED", "FA_GEAR_ROLE")
					   .build()
			  );
		

		
		//flux_report_document_id
		protected static final Operation INSERT_ERS_FLUX_REPORT_DOCUMENT_DATA = sequenceOf(
	            insertInto("activity.activity_flux_report_document")
	                    .columns("id", "flux_report_document_id", "reference_id", "creation_datetime","purpose_code","purpose_code_list_id","purpose","owner_flux_party_id","owner_flux_party_name")
	                    .values(1, "ID 1","ID 1",java.sql.Date.valueOf("2014-12-12"),"9","CODELISTID","PURPOSE","OWNER1","OWNER_NAME1")
	                    .build(),
				insertInto("activity.activity_flux_report_document")
						.columns("id", "flux_report_document_id", "reference_id", "creation_datetime","purpose_code","purpose_code_list_id","purpose","owner_flux_party_id","owner_flux_party_name")
						 .values(2, "FLUX_REPORT_DOCUMENT2", null, "2016-06-27 07:47:31.711","9", "PURPOSE_CODE_LIST", null, "OWNER2","OWNER_NAME2")
						.build(),
				insertInto("activity.activity_flux_report_document")
						.columns("id", "flux_report_document_id", "reference_id", "creation_datetime","purpose_code","purpose_code_list_id","purpose","owner_flux_party_id","owner_flux_party_name")
						.values(3, "ID 3","ID 1",java.sql.Date.valueOf("2014-12-12"),"5","CODELISTID","PURPOSE","OWNER3","OWNER_NAME3")
						.build(),
				insertInto("activity.activity_flux_report_document")
						.columns("id", "flux_report_document_id", "reference_id", "creation_datetime","purpose_code","purpose_code_list_id","purpose","owner_flux_party_id","owner_flux_party_name")
						.values(4, "ID 4", "ID 3", java.sql.Date.valueOf("2015-12-12"),"5", "PURPOSE_CODE_LIST", null, "OWNER4","OWNER_NAME4")
						.build()
	  );

		
			
		//vessel_transport_means_id
		protected static final Operation INSERT_ERS_VESSEL_TRANSPORT_MEANS_DATA = sequenceOf(
	            insertInto("activity.activity_vessel_transport_means")
	                    .columns("id", "role_code", "role_code_list_id", "name","registration_event_id","flap_document_id","flap_document_scheme_id")
	                   .values(1, "ROLE_CODE","LIST_ID","vessel1",null,"SCHEMA_ID","NAME")
	                    .build(),
				insertInto("activity.activity_vessel_transport_means")
						.columns("id", "role_code", "role_code_list_id", "name","registration_event_id","flap_document_id","flap_document_scheme_id")
						.values(2, "ROLE_CODE","LIST_ID","vessel2",null,"SCHEMA_ID","NAME")
						.build(),
				insertInto("activity.activity_vessel_transport_means")
						.columns("id", "role_code", "role_code_list_id", "name","registration_event_id","flap_document_id","flap_document_scheme_id")
						.values(3, "ROLE_CODE","LIST_ID","vessel3",null,"SCHEMA_ID","NAME")
						.build(),
				insertInto("activity.activity_vessel_transport_means")
						.columns("id", "role_code", "role_code_list_id", "name","registration_event_id","flap_document_id","flap_document_scheme_id")
						.values(4, "ROLE_CODE","LIST_ID","vessel4",null,"SCHEMA_ID","NAME")
						.build()
	  );

	protected static final Operation INSERT_ERS_VESSEL_IDENTIFIERS_DATA = sequenceOf(
			insertInto("activity.activity_vessel_identifier")
					.columns("id", "vessel_transport_mean_id", "vessel_identifier_id", "vessel_identifier_scheme_id")
					.values(1, 1,"CFR123","CFR")
					.build(),
			insertInto("activity.activity_vessel_identifier")
					.columns("id", "vessel_transport_mean_id", "vessel_identifier_id", "vessel_identifier_scheme_id")
					.values(2, 1,"IRCS123","IRCS")
					.build(),
			insertInto("activity.activity_vessel_identifier")
					.columns("id", "vessel_transport_mean_id", "vessel_identifier_id", "vessel_identifier_scheme_id")
					.values(3, 1,"EXT_MARK123","EXT_MARK")
					.build(),
			insertInto("activity.activity_vessel_identifier")
					.columns("id", "vessel_transport_mean_id", "vessel_identifier_id", "vessel_identifier_scheme_id")
					.values(4, 2,"CFR123","CFR")
					.build(),
			insertInto("activity.activity_vessel_identifier")
					.columns("id", "vessel_transport_mean_id", "vessel_identifier_id", "vessel_identifier_scheme_id")
					.values(5, 2,"EXT_MARK123","EXT_MARK")
					.build()

	);


	protected static final Operation INSERT_FLUX_LOCATION = sequenceOf(
			insertInto("activity.activity_flux_location")
					.columns("id", "fa_catch_id", "fishing_activity_id", "type_code", "type_code_list_id","country_id", "country_id_scheme_id", "rfmo_code", "longitude", "latitude",
							"flux_location_type", "flux_location_identifier", "flux_location_identifier_scheme_id","geopolitical_region_code", "geopolitical_region_code_list_id", "name",
							"sovereign_rights_country_code", "jurisdiction_country_code","altitude","system_id")
					.values(1, 1, 1, "AREA", "FAO_AREA","ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null,"AREA", "27.4.b", "FAO_AREA","GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
							"FLUX_LOCATION_NAME",null, null, null,null)
					.build(),
			insertInto("activity.activity_flux_location")
					.columns("id", "fa_catch_id", "fishing_activity_id", "type_code", "type_code_list_id","country_id", "country_id_scheme_id", "rfmo_code", "longitude", "latitude",
							"flux_location_type", "flux_location_identifier", "flux_location_identifier_scheme_id","geopolitical_region_code", "geopolitical_region_code_list_id", "name",
							"sovereign_rights_country_code", "jurisdiction_country_code","altitude","system_id")
					.values(2, 1, 1, "AREA", "FAO_AREA","ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null,"AREA", "GBR", "TERRITORY","GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
							"FLUX_LOCATION_NAME",null, null, null,null)
					.build(),
			insertInto("activity.activity_flux_location")
					.columns("id", "fa_catch_id", "fishing_activity_id", "type_code", "type_code_list_id","country_id", "country_id_scheme_id", "rfmo_code", "longitude", "latitude",
							"flux_location_type", "flux_location_identifier", "flux_location_identifier_scheme_id","geopolitical_region_code", "geopolitical_region_code_list_id", "name",
							"sovereign_rights_country_code", "jurisdiction_country_code","altitude","system_id")
					.values(3, 1, 1, "AREA", "FAO_AREA","ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null,"AREA", "38F1", "ICES_STAT_RECTANGLE","GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
							"FLUX_LOCATION_NAME",null, null, null,null)
					.build(),
			insertInto("activity.activity_flux_location")
					.columns("id", "fa_catch_id", "fishing_activity_id", "type_code", "type_code_list_id","country_id", "country_id_scheme_id", "rfmo_code", "longitude", "latitude",
							"flux_location_type", "flux_location_identifier", "flux_location_identifier_scheme_id","geopolitical_region_code", "geopolitical_region_code_list_id", "name",
							"sovereign_rights_country_code", "jurisdiction_country_code","altitude","system_id")
					.values(4, 1, 1, "LOCATION", "FAO_AREA","ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null,"AREA", "38F1", "ICES_STAT_RECTANGLE","GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
							"FLUX_LOCATION_NAME",null, null, null,null)
					.build(),
			insertInto("activity.activity_flux_location")
					.columns("id", "fa_catch_id", "fishing_activity_id", "type_code", "type_code_list_id","country_id", "country_id_scheme_id", "rfmo_code", "longitude", "latitude",
							"flux_location_type", "flux_location_identifier", "flux_location_identifier_scheme_id","geopolitical_region_code", "geopolitical_region_code_list_id", "name",
							"sovereign_rights_country_code", "jurisdiction_country_code","altitude","system_id")
					.values(5, 1, 1, "LOCATION", "FAO_AREA","ISO", "SCHEME_COUNTRY_ID1", "NAFO", null, null,"AREA", "GBR", "TERRITORY","GEOGRAPHICAL_REGION", "GEOGRAPHICAL_REGION_LISTID",
							"FLUX_LOCATION_NAME",null, null, null,null)
					.build()
	);


	protected static final Operation INSERT_STRUCTURED_ADDRESS = sequenceOf(
			insertInto("activity.activity_structured_address")
					.columns("id", "block_name", "building_name", "city_name", "city_subdivision_name","country", "country_name","country_subdivision_name", "address_id","plot_id", "post_office_box",
							"postcode", "streetname", "flux_location_id","contact_party_id", "structured_address_type")
					.values(1, "SDS", "SDS", "CXV", "VCVB","CVCV", "GHH", "YUU", "JHJ","JGH", "CVGH","GHJ", "TYT", 1,1, "DFS")
					.build()
	);


	protected static final Operation INSERT_CONTACT_PERSON = sequenceOf(
			insertInto("activity.activity_contact_person")
					.columns("id", "title", "given_name", "middle_name", "family_name", "family_name_prefix","name_suffix", "gender", "alias")
					.values(1, "Mr", "MARK", "DAVID", "BOSE", "ARR","PI", "MALE", null)
					.build(),
			insertInto("activity.activity_contact_person")
					.columns("id", "title", "given_name", "middle_name", "family_name", "family_name_prefix",
							"name_suffix", "gender", "alias")
					.values(2, "Mrs", "TIA", "DAVID", "BOSE", "ARR","PI", "FEMALE", null)
					.build()

	);

	protected static final Operation INSERT_CONTACT_PARTY = sequenceOf(
			insertInto("activity.activity_contact_party")
					.columns("id", "vessel_transport_means_id", "role_code", "role_code_list_id","contact_person_id")
					.values(1, 1, "ROLECODE1", "FLUX_CONTACT_ ROLE1",	1)
					.build(),
			insertInto("activity.activity_contact_party")
					.columns("id", "vessel_transport_means_id", "role_code", "role_code_list_id","contact_person_id")
					.values(2, 2, "ROLECODE2", "FLUX_CONTACT_ ROLE2",	2)
					.build()

	);


	protected static final Operation INSERT_DELIMITED_PERIOD = sequenceOf(
			insertInto("activity.activity_delimited_period")
					.columns("id", "start_date", "end_date", "duration", "fishing_activity_id", "fishing_trip_id")
					.values(1, "2010-06-27 07:47:31.711", "2013-06-27 07:47:31.711", 1, 1, 1)
					.build(),
			insertInto("activity.activity_delimited_period")
					.columns("id", "start_date", "end_date", "duration", "fishing_activity_id", "fishing_trip_id")
					.values(2, "2012-05-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, 1, 2)
					.build(),
			insertInto("activity.activity_delimited_period")
					.columns("id", "start_date", "end_date", "duration", "fishing_activity_id", "fishing_trip_id")
					.values(3, "2013-06-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, 1, 3)
					.build(),
			insertInto("activity.activity_delimited_period")
					.columns("id", "start_date", "end_date", "duration", "fishing_activity_id", "fishing_trip_id")
					.values(4, "2009-06-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, 1, 4)
					.build(),
			insertInto("activity.activity_delimited_period")
					.columns("id", "start_date", "end_date", "duration", "fishing_activity_id", "fishing_trip_id")
					.values(5, "2011-06-27 07:47:31.711", "2016-06-27 07:47:31.711", 1, 1, 5)
					.build()

	);
	 

	protected static final Operation INSERT_ERS_FA_REPORT_DOCUMENT_DATA = sequenceOf(
	            insertInto("activity.activity_fa_report_document")
	                    .columns("id", "type_code", "type_code_list_id", "accepted_datetime", "flux_report_document_id","vessel_transport_means_id","fmc_marker","fmc_marker_list_id","status","source")
	                    .values(1,"DECLARATION","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2014-12-12"),1,1,"fmc","fmc_list","new","FLUX")
	                    .build(),
				insertInto("activity.activity_fa_report_document")
						.columns("id", "type_code", "type_code_list_id", "accepted_datetime", "flux_report_document_id","vessel_transport_means_id","fmc_marker","fmc_marker_list_id","status","source")
						.values(2,"DECLARATION","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2014-12-12"),2,2,"fmc","fmc_list","new","FLUX")
						.build(),
				insertInto("activity.activity_fa_report_document")
						.columns("id", "type_code", "type_code_list_id", "accepted_datetime", "flux_report_document_id","vessel_transport_means_id","fmc_marker","fmc_marker_list_id","status","source")
						.values(3,"DECLARATION","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2014-12-12"),3,3,"fmc","fmc_list","new","FLUX")
						.build(),
				insertInto("activity.activity_fa_report_document")
						.columns("id", "type_code", "type_code_list_id", "accepted_datetime", "flux_report_document_id","vessel_transport_means_id","fmc_marker","fmc_marker_list_id","status","source")
						.values(4,"NOTIFICATION","FLUX_LOCATION_TYPE",java.sql.Date.valueOf("2014-12-12"),4,4,"fmc","fmc_list","new","FLUX")
						.build()
	  );

		protected static final Operation INSERT_ERS_FA_REPORT_IDENTIFIER_DATA = sequenceOf(
				insertInto("activity.activity_fa_report_identifier")
					.columns("id", "fa_report_document_id", "fa_report_identifier_id", "fa_report_identifier_scheme_id")
					.values(1, 1, "ID 1", "test schema id 1")
					.build()
		);

		 @Override
	    protected String getSchema() {
	        return "activity";
	    }

	    @Override
	    protected String getPersistenceUnitName() {
	        return "testPU";
	    }

}