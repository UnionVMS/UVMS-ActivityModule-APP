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

package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.activity.service.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import org.junit.Test;

import javax.json.bind.Jsonb;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ActivityConfigServiceBeanTest {

    private ActivityConfigServiceBean preferenceConfigService = new ActivityConfigServiceBean();

    @Test
    public void getAdminConfig() {
        // Given
        ActivityConfigDTO source = getConfiguration(adminConfig);

        // When
        ActivityConfigDTO adminConfigDto = preferenceConfigService.getAdminConfig(adminConfig);

        // THen
        assertEquals(source.getFishingActivityConfig().getSummaryReport().getValues(), adminConfigDto.getFishingActivityConfig().getSummaryReport().getValues());
        assertEquals(source.getFishingActivityConfig().getSummaryReport().getOrder(), adminConfigDto.getFishingActivityConfig().getSummaryReport().getOrder());
    }

    @Test
    public void getUserConfig() {
        // Given
        ActivityConfigDTO userConfigDto = preferenceConfigService.getUserConfig(userConfig, adminConfig);

        // When
        ActivityConfigDTO user = getConfiguration(userConfig);

        // Then
        assertEquals(user.getFishingActivityConfig().getSummaryReport().getValues(), userConfigDto.getFishingActivityConfig().getSummaryReport().getValues());
        assertEquals(user.getFishingActivityConfig().getSummaryReport().getOrder(), userConfigDto.getFishingActivityConfig().getSummaryReport().getOrder());
    }

    @Test
    public void saveAdminConfig() {
        // Given
        ActivityConfigDTO adminConfigDto = getConfiguration(adminConfig);

        // When
        String adminJson = preferenceConfigService.saveAdminConfig(adminConfigDto);

        // Then
        assertEquals(getJson(adminConfigDto), adminJson);
    }

    @Test
    public void saveUserConfig() {
        // Given
        String updatedConfig = adminConfig;
        ActivityConfigDTO updatedConfigDto = getConfiguration(updatedConfig);

        // When
        String userJson = preferenceConfigService.saveUserConfig(updatedConfigDto, userConfig);

        // Then
        assertEquals(getJson(updatedConfigDto), userJson);
    }

    @Test
    public void resetUserConfig() {
        // Given
        String resetConfig = "{\n" +
                "                    \"fishingActivityConfig\": {\n" +
                "                        \"summaryReport\": {\n" +
                "                           \n" +
                "                        }\n" +
                "                    }\n" +
                "                }";
        ActivityConfigDTO activityConfigDTO = getConfiguration(resetConfig);

        // When
        String userJson = preferenceConfigService.resetUserConfig(activityConfigDTO, userConfig);

        // Then
        ActivityConfigDTO updated = getConfiguration(userJson);
        assertNull(updated.getFishingActivityConfig().getSummaryReport());
    }

    private ActivityConfigDTO getConfiguration(String configString) {
        Jsonb jsonb = new JsonBConfigurator().getContext(null);
        return jsonb.fromJson(configString, ActivityConfigDTO.class);
    }

    private String getJson(ActivityConfigDTO config) {
        Jsonb jsonb = new JsonBConfigurator().getContext(null);
        return jsonb.toJson(config);
    }

    private String adminConfig = "{\n" +
            "                    \"fishingActivityConfig\": {\n" +
            "                        \"summaryReport\": {\n" +
            "                        }\n" +
            "                    }\n" +
            "                }";

    private String userConfig = "{\n" +
            "                    \"fishingActivityConfig\": {\n" +
            "                        \"summaryReport\": {\n" +
            "                            \"values\": [\n" +
            "                                \"FAReportType\",\n" +
            "                                \"activityType\",\n" +
            "                                \"occurrence\",\n" +
            "                                \"purposeCode\",\n" +
            "                                \"dataSource\",\n" +
            "                                \"fromName\",\n" +
            "                                \"startDate\",\n" +
            "                                \"endDate\",\n" +
            "                                \"cfr\",\n" +
            "                                \"ircs\",\n" +
            "                                \"extMark\",\n" +
            "                                \"uvi\",\n" +
            "                                \"iccat\",\n" +
            "                                \"gfcm\",\n" +
            "                                \"areas\",\n" +
            "                                \"port\",\n" +
            "                                \"fishingGear\",\n" +
            "                                \"speciesCode\",\n" +
            "                                \"quantity\"\n" +
            "                            ],\n" +
            "                            \"order\": [\n" +
            "                                \"FAReportType\",\n" +
            "                                \"activityType\",\n" +
            "                                \"occurrence\",\n" +
            "                                \"purposeCode\",\n" +
            "                                \"dataSource\",\n" +
            "                                \"fromName\",\n" +
            "                                \"startDate\",\n" +
            "                                \"endDate\",\n" +
            "                                \"cfr\",\n" +
            "                                \"ircs\",\n" +
            "                                \"extMark\",\n" +
            "                                \"uvi\",\n" +
            "                                \"iccat\",\n" +
            "                                \"gfcm\",\n" +
            "                                \"areas\",\n" +
            "                                \"port\",\n" +
            "                                \"fishingGear\",\n" +
            "                                \"speciesCode\",\n" +
            "                                \"quantity\"\n" +
            "                            ]\n" +
            "                        }\n" +
            "                    }\n" +
            "                }";
}
