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

import eu.europa.ec.fisheries.uvms.activity.service.ActivityConfigService;
import eu.europa.ec.fisheries.uvms.activity.service.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.PreferenceConfigMapper;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;

@Stateless
@Transactional
@Slf4j
public class ActivityConfigServiceBean implements ActivityConfigService {

    @Override
    @SneakyThrows
    public ActivityConfigDTO getAdminConfig(String adminConfig) {
        return getConfiguration(adminConfig);
    }

    @Override
    @SneakyThrows
    public ActivityConfigDTO getUserConfig(String userConfig, String adminConfig) {
        return PreferenceConfigMapper.INSTANCE.mergeUserPreference(getConfiguration(adminConfig), getConfiguration(userConfig));
    }

    @Override
    @SneakyThrows
    public String saveAdminConfig(ActivityConfigDTO config) {
        return getJson(config);
    }

    @Override
    @SneakyThrows
    public String saveUserConfig(ActivityConfigDTO updatedConfig, String userConfig) {
        ActivityConfigDTO usmUserConfig = getConfiguration(userConfig);
        ActivityConfigDTO mergedConfig = PreferenceConfigMapper.INSTANCE.mergeUserPreference(usmUserConfig, updatedConfig);
        return getJson(mergedConfig);
    }

    @Override
    @SneakyThrows
    public String resetUserConfig(ActivityConfigDTO resetConfig, String userConfig) {
        ActivityConfigDTO usmUserConfig = getConfiguration(userConfig);
        ActivityConfigDTO mergedConfig = PreferenceConfigMapper.INSTANCE.resetUserPreference(usmUserConfig, resetConfig);
        return getJson(mergedConfig);
    }

    private ActivityConfigDTO getConfiguration(String configString) {
        if (configString == null || configString.isEmpty()) {
            return new ActivityConfigDTO();
        }
        Jsonb jsonb = new JsonBConfigurator().getContext(null);
        return jsonb.fromJson(configString, ActivityConfigDTO.class);
    }

    private String getJson(ActivityConfigDTO config) {
        Jsonb jsonb = new JsonBConfigurator().getContext(null);
        return jsonb.toJson(config);
    }
}
