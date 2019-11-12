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

package eu.europa.ec.fisheries.ers.activity.service.bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.ers.activity.service.ActivityConfigService;
import eu.europa.ec.fisheries.ers.activity.service.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.ers.activity.service.mapper.PreferenceConfigMapper;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.IOException;

@Stateless
@Transactional
@Slf4j
public class ActivityConfigServiceBean implements ActivityConfigService {

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public ActivityConfigDTO getAdminConfig(String adminConfig) throws ServiceException {
        return getConfiguration(adminConfig);
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public ActivityConfigDTO getUserConfig(String userConfig, String adminConfig) throws ServiceException {
        return PreferenceConfigMapper.INSTANCE.mergeUserPreference(getConfiguration(adminConfig), getConfiguration(userConfig));
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public String saveAdminConfig(ActivityConfigDTO config) throws ServiceException {
        return getJson(config);
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public String saveUserConfig(ActivityConfigDTO updatedConfig, String userConfig) throws ServiceException {
        ActivityConfigDTO usmUserConfig = getConfiguration(userConfig);
        ActivityConfigDTO mergedConfig = PreferenceConfigMapper.INSTANCE.mergeUserPreference(usmUserConfig, updatedConfig);
        return getJson(mergedConfig);
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public String resetUserConfig(ActivityConfigDTO resetConfig, String userConfig) throws ServiceException {
        ActivityConfigDTO usmUserConfig = getConfiguration(userConfig);
        ActivityConfigDTO mergedConfig = PreferenceConfigMapper.INSTANCE.resetUserPreference(usmUserConfig, resetConfig);
        return getJson(mergedConfig);
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
