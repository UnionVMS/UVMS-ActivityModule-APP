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

package eu.europa.ec.fisheries.ers.service.bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.config.ActivityConfigDTO;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by padhyad on 8/25/2016.
 */
public class PreferenceConfigServiceBeanTest {

    @InjectMocks
    private PreferenceConfigServiceBean preferenceConfigService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private String adminConfig = "{\n" +
            "                \"faReportConfig\": {\n" +
            "                    \"faReport\": [\n" +
            "                        \"typeCode\",\n" +
            "                        \"fmcMarker\",\n" +
            "                        \"acceptedDateTime\",\n" +
            "                        \"creationDateTime\",\n" +
            "                        \"fluxReportDocumentId\",\n" +
            "                        \"purposeCode\",\n" +
            "                        \"referenceId\",\n" +
            "                        \"ownerFluxPartyId\",\n" +
            "                        \"status\",\n" +
            "                        \"fishingActivity\",\n" +
            "                        \"vessel\"\n" +
            "                    ],\n" +
            "                    \"fishingActivity\": [\n" +
            "                        \"sourceVesselId\",\n" +
            "                        \"sourceVesselTypeCode\",\n" +
            "                        \"destVesselId\",\n" +
            "                        \"destVesselTypeCode\",\n" +
            "                        \"activityTypeCode\",\n" +
            "                        \"occurence\",\n" +
            "                        \"reasonCode\",\n" +
            "                        \"vesselActivityCode\",\n" +
            "                        \"fisheryTypeCode\",\n" +
            "                        \"speciesTargetCode\",\n" +
            "                        \"operationQuantity\",\n" +
            "                        \"fishingDurationMeasure\",\n" +
            "                        \"flapDocumentId\",\n" +
            "                        \"fishingActivityIds\",\n" +
            "                        \"fishingTrip\",\n" +
            "                        \"faCatches\",\n" +
            "                        \"fishingGears\",\n" +
            "                        \"gearProblems\",\n" +
            "                        \"delimitedPeriods\",\n" +
            "                        \"fluxCharacteristics\",\n" +
            "                        \"fluxLocations\"\n" +
            "                    ],\n" +
            "                    \"fishingTrip\": [\n" +
            "                        \"tripType\",\n" +
            "                        \"tripId\",\n" +
            "                        \"delimitedPeriods\"\n" +
            "                    ],\n" +
            "                    \"delimitedPeriod\": [\n" +
            "                        \"startDate\",\n" +
            "                        \"endDate\",\n" +
            "                        \"duration\"\n" +
            "                    ],\n" +
            "                    \"faCatches\": [\n" +
            "                        \"typeCode\",\n" +
            "                        \"speciesCode\",\n" +
            "                        \"unitQuantity\",\n" +
            "                        \"weightMeasure\",\n" +
            "                        \"weightMeasureUnitCode\",\n" +
            "                        \"usageCode\",\n" +
            "                        \"weighingMeansCode\",\n" +
            "                        \"sizeDistributionClassCode\",\n" +
            "                        \"sizeDistributionCategoryCode\",\n" +
            "                        \"fishingGears\",\n" +
            "                        \"specifiedFluxLocations\",\n" +
            "                        \"destFluxLocations\",\n" +
            "                        \"fluxCharacteristics\",\n" +
            "                        \"fishingTrips\",\n" +
            "                        \"aapProcess\",\n" +
            "                        \"aapStock\"\n" +
            "                    ],\n" +
            "                    \"fishingGears\": [\n" +
            "                        \"gearType\",\n" +
            "                        \"gearRole\",\n" +
            "                        \"gearCharacteristics\"\n" +
            "                    ],\n" +
            "                    \"gearCharacteristics\": [\n" +
            "                        \"typeCode\",\n" +
            "                        \"description\",\n" +
            "                        \"valueMeasure\",\n" +
            "                        \"dateTime\",\n" +
            "                        \"indicator\",\n" +
            "                        \"code\",\n" +
            "                        \"text\",\n" +
            "                        \"quantity\"\n" +
            "                    ],\n" +
            "                    \"FluxLocations\": [\n" +
            "                        \"typeCode\",\n" +
            "                        \"countryId\",\n" +
            "                        \"rfmoCode\",\n" +
            "                        \"longitude\",\n" +
            "                        \"latitude\",\n" +
            "                        \"altitude\",\n" +
            "                        \"fluxLocationType\",\n" +
            "                        \"fluxLocationIdentifier\",\n" +
            "                        \"geopoliticalRegionCode\",\n" +
            "                        \"name\",\n" +
            "                        \"sovereignRightsCountryCode\",\n" +
            "                        \"jurisdictionCountryCode\",\n" +
            "                        \"systemId\",\n" +
            "                        \"fluxCharacteristics\",\n" +
            "                        \"physicalStructuredAddress\",\n" +
            "                        \"postalStructuredAddress\"\n" +
            "                    ],\n" +
            "                    \"fluxCharacteristics\": [\n" +
            "                        \"type\",\n" +
            "                        \"measure\",\n" +
            "                        \"dateTime\",\n" +
            "                        \"indicator\",\n" +
            "                        \"code\",\n" +
            "                        \"text\",\n" +
            "                        \"quantity\",\n" +
            "                        \"description\"\n" +
            "                    ],\n" +
            "                    \"structuredAddress\": [\n" +
            "                        \"blockName\",\n" +
            "                        \"buildingName\",\n" +
            "                        \"cityName\",\n" +
            "                        \"citySubdivisionName\",\n" +
            "                        \"country\",\n" +
            "                        \"countryName\",\n" +
            "                        \"countrySubdivisionName\",\n" +
            "                        \"addressId\",\n" +
            "                        \"plotId\",\n" +
            "                        \"postOfficeBox\",\n" +
            "                        \"postcode\",\n" +
            "                        \"streetname\"\n" +
            "                    ],\n" +
            "                    \"aapProcess\": [\n" +
            "                        \"typeCode\",\n" +
            "                        \"conversionFactor\",\n" +
            "                        \"aapProduct\"\n" +
            "                    ],\n" +
            "                    \"aapProduct\": [\n" +
            "                        \"packagingTypeCode\",\n" +
            "                        \"packagingUnitAverageWeight\",\n" +
            "                        \"packagingUnitCount\"\n" +
            "                    ],\n" +
            "                    \"aapStock\": [\n" +
            "                        \"stockId\"\n" +
            "                    ],\n" +
            "                    \"gearProblems\": [\n" +
            "                        \"problemType\",\n" +
            "                        \"affectedQuantity\",\n" +
            "                        \"recoveryMeasure\",\n" +
            "                        \"fishingGears\"\n" +
            "                    ],\n" +
            "                    \"vessel\": [\n" +
            "                        \"vesselRole\",\n" +
            "                        \"vesselIds\",\n" +
            "                        \"vesselName\",\n" +
            "                        \"registrationDateTime\",\n" +
            "                        \"registrationEventDescription\",\n" +
            "                        \"registrationLocationDescription\",\n" +
            "                        \"registrationRegion\",\n" +
            "                        \"registrationLocationName\",\n" +
            "                        \"registrationType\",\n" +
            "                        \"registrationLocationCountryId\",\n" +
            "                        \"contactParty\"\n" +
            "                    ],\n" +
            "                    \"contactParty\": [\n" +
            "                        \"role\",\n" +
            "                        \"contactPersonDetails\",\n" +
            "                        \"addressDetails\"\n" +
            "                    ],\n" +
            "                    \"contactPerson\": [\n" +
            "                        \"title\",\n" +
            "                        \"givenName\",\n" +
            "                        \"middleName\",\n" +
            "                        \"familyName\",\n" +
            "                        \"familyNamePrefix\",\n" +
            "                        \"nameSuffix\",\n" +
            "                        \"gender\",\n" +
            "                        \"alias\"\n" +
            "                    ]\n" +
            "                }\n" +
            "                }";

    private String userConfig = "{\n" +
            "    \"faReportConfig\": {\n" +
            "        \"faReport\": [\n" +
            "            \"typeCode\",\n" +
            "            \"fmcMarker\",\n" +
            "            \"acceptedDateTime\",\n" +
            "            \"creationDateTime\",\n" +
            "            \"fluxReportDocumentId\",\n" +
            "            \"purposeCode\",\n" +
            "            \"referenceId\",\n" +
            "            \"ownerFluxPartyId\",\n" +
            "            \"status\",\n" +
            "            \"fishingActivity\",\n" +
            "            \"vessel\"\n" +
            "        ],\n" +
            "        \"fishingActivity\": [\n" +
            "            \"sourceVesselId\",\n" +
            "            \"sourceVesselTypeCode\",\n" +
            "            \"destVesselId\",\n" +
            "            \"destVesselTypeCode\",\n" +
            "            \"activityTypeCode\",\n" +
            "            \"occurence\",\n" +
            "            \"reasonCode\",\n" +
            "            \"vesselActivityCode\",\n" +
            "            \"fisheryTypeCode\",\n" +
            "            \"speciesTargetCode\",\n" +
            "            \"operationQuantity\",\n" +
            "            \"fishingDurationMeasure\",\n" +
            "            \"flapDocumentId\",\n" +
            "            \"fishingActivityIds\",\n" +
            "            \"fishingTrip\",\n" +
            "            \"faCatches\",\n" +
            "            \"fishingGears\",\n" +
            "            \"gearProblems\",\n" +
            "            \"delimitedPeriods\",\n" +
            "            \"fluxCharacteristics\",\n" +
            "            \"fluxLocations\"\n" +
            "        ],\n" +
            "        \"fishingTrip\": [\n" +
            "            \"tripType\",\n" +
            "            \"tripId\",\n" +
            "            \"delimitedPeriods\"\n" +
            "        ],\n" +
            "        \"delimitedPeriod\": [\n" +
            "            \"startDate\",\n" +
            "            \"endDate\",\n" +
            "            \"duration\"\n" +
            "        ],\n" +
            "        \"contactPerson\": [\n" +
            "            \"title\",\n" +
            "            \"givenName\",\n" +
            "            \"middleName\",\n" +
            "            \"familyName\",\n" +
            "            \"familyNamePrefix\",\n" +
            "            \"nameSuffix\",\n" +
            "            \"gender\",\n" +
            "            \"alias\"\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    @Test
    public void TestGetAdminConfig() throws Exception {
        ActivityConfigDTO source = getConfiguration(adminConfig);
        ActivityConfigDTO adminConfigDto = preferenceConfigService.getAdminConfig(adminConfig);

        assertEquals(source.getFaReportConfig().getFluxLocations(), adminConfigDto.getFaReportConfig().getFluxLocations());
        assertEquals(source.getFaReportConfig().getGearCharacteristics(), adminConfigDto.getFaReportConfig().getGearCharacteristics());
        assertEquals(source.getFaReportConfig().getFishingGears(), adminConfigDto.getFaReportConfig().getFishingGears());
        assertEquals(source.getFaReportConfig().getFaCatches(), adminConfigDto.getFaReportConfig().getFaCatches());
        assertEquals(source.getFaReportConfig().getAapProcess(), adminConfigDto.getFaReportConfig().getAapProcess());
        assertEquals(source.getFaReportConfig().getAapProduct(), adminConfigDto.getFaReportConfig().getAapProduct());
        assertEquals(source.getFaReportConfig().getAapStock(), adminConfigDto.getFaReportConfig().getAapStock());
        assertEquals(source.getFaReportConfig().getContactParty(), adminConfigDto.getFaReportConfig().getContactParty());
        assertEquals(source.getFaReportConfig().getContactPerson(), adminConfigDto.getFaReportConfig().getContactPerson());
        assertEquals(source.getFaReportConfig().getDelimitedPeriod(), adminConfigDto.getFaReportConfig().getDelimitedPeriod());
        assertEquals(source.getFaReportConfig().getFaReport(), adminConfigDto.getFaReportConfig().getFaReport());
        assertEquals(source.getFaReportConfig().getFishingActivity(), adminConfigDto.getFaReportConfig().getFishingActivity());
        assertEquals(source.getFaReportConfig().getFishingTrip(), adminConfigDto.getFaReportConfig().getFishingTrip());
        assertEquals(source.getFaReportConfig().getGearProblems(), adminConfigDto.getFaReportConfig().getGearProblems());
        assertEquals(source.getFaReportConfig().getStructuredAddress(), adminConfigDto.getFaReportConfig().getStructuredAddress());
        assertEquals(source.getFaReportConfig().getVessel(), adminConfigDto.getFaReportConfig().getVessel());
    }

    @Test
    public void testGetUserConfig() throws Exception {
        ActivityConfigDTO userConfigDto = preferenceConfigService.getUserConfig(userConfig, adminConfig);
        ActivityConfigDTO admin = getConfiguration(adminConfig);
        ActivityConfigDTO user = getConfiguration(userConfig);

        assertEquals(admin.getFaReportConfig().getFluxLocations(), userConfigDto.getFaReportConfig().getFluxLocations());
        assertEquals(admin.getFaReportConfig().getGearCharacteristics(), userConfigDto.getFaReportConfig().getGearCharacteristics());
        assertEquals(admin.getFaReportConfig().getFishingGears(), userConfigDto.getFaReportConfig().getFishingGears());
        assertEquals(admin.getFaReportConfig().getFaCatches(), userConfigDto.getFaReportConfig().getFaCatches());
        assertEquals(admin.getFaReportConfig().getAapProcess(), userConfigDto.getFaReportConfig().getAapProcess());
        assertEquals(admin.getFaReportConfig().getAapProduct(), userConfigDto.getFaReportConfig().getAapProduct());
        assertEquals(admin.getFaReportConfig().getAapStock(), userConfigDto.getFaReportConfig().getAapStock());
        assertEquals(admin.getFaReportConfig().getContactParty(), userConfigDto.getFaReportConfig().getContactParty());
        assertEquals(user.getFaReportConfig().getContactPerson(), userConfigDto.getFaReportConfig().getContactPerson());
        assertEquals(user.getFaReportConfig().getDelimitedPeriod(), userConfigDto.getFaReportConfig().getDelimitedPeriod());
        assertEquals(user.getFaReportConfig().getFaReport(), userConfigDto.getFaReportConfig().getFaReport());
        assertEquals(user.getFaReportConfig().getFishingActivity(), userConfigDto.getFaReportConfig().getFishingActivity());
        assertEquals(user.getFaReportConfig().getFishingTrip(), userConfigDto.getFaReportConfig().getFishingTrip());
        assertEquals(admin.getFaReportConfig().getGearProblems(), userConfigDto.getFaReportConfig().getGearProblems());
        assertEquals(admin.getFaReportConfig().getStructuredAddress(), userConfigDto.getFaReportConfig().getStructuredAddress());
        assertEquals(admin.getFaReportConfig().getVessel(), userConfigDto.getFaReportConfig().getVessel());
    }

    @Test
    public void testSaveAdminConfig() throws Exception {
        ActivityConfigDTO adminConfigDto = getConfiguration(adminConfig);
        String adminJson = preferenceConfigService.saveAdminConfig(adminConfigDto);
        assertEquals(getJson(adminConfigDto), adminJson);
    }

    @Test
    public void testSaveUserConfig() throws Exception {
        String defaultConfig = "{\n" +
                "    \"faReportConfig\": {}\n" +
                "}";
        ActivityConfigDTO userConfigDto = getConfiguration(userConfig);
        String userJson = preferenceConfigService.saveUserConfig(userConfigDto, defaultConfig);
        assertEquals(getJson(userConfigDto), userJson);
    }

    @Test
    public void testResetUserConfig() throws Exception {
        String resetConfig = "{\n" +
                "    \"faReportConfig\": {\n" +
                "        \"faReport\": []\n" +
                "    }\n" +
                "}";
        ActivityConfigDTO activityConfigDTO = getConfiguration(resetConfig);
        String userJson = preferenceConfigService.resetUserConfig(activityConfigDTO, userConfig);
        ActivityConfigDTO user = getConfiguration(userConfig);
        ActivityConfigDTO updated = getConfiguration(userJson);
        assertNull(updated.getFaReportConfig().getFaReport());
        assertEquals(user.getFaReportConfig().getFishingActivity(), updated.getFaReportConfig().getFishingActivity());
        assertEquals(user.getFaReportConfig().getDelimitedPeriod(), updated.getFaReportConfig().getDelimitedPeriod());
        assertEquals(user.getFaReportConfig().getFishingTrip(), updated.getFaReportConfig().getFishingTrip());
        assertEquals(user.getFaReportConfig().getContactPerson(), updated.getFaReportConfig().getContactPerson());
    }

    private ActivityConfigDTO getConfiguration(String configString) throws IOException {
        if (configString == null || configString.isEmpty()) {
            return new ActivityConfigDTO();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper.readValue(configString, ActivityConfigDTO.class);
    }

    private String getJson(ActivityConfigDTO config) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(config);
    }
}
