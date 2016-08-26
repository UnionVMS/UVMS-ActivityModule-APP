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
import eu.europa.ec.fisheries.ers.service.mapper.PreferenceConfigMapper;
import eu.europa.ec.fisheries.uvms.activity.model.dto.config.ActivityConfigDTO;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.transaction.Transactional;
import java.io.IOException;

/**
 * Created by padhyad on 8/24/2016.
 */
@Stateless
@Transactional
@Slf4j
public class PreferenceConfigServiceBean implements PreferenceConfigService {

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public ActivityConfigDTO getAdminConfig(String adminConfig) throws ServiceException {
        try {
            return getConfiguration(adminConfig);
        } catch (IOException e) {
            throw new ServiceException("Parse Exception Admin Configuration");
        }
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public ActivityConfigDTO getUserConfig(String userConfig, String adminConfig) throws ServiceException {
        try {
            return PreferenceConfigMapper.INSTANCE.mergeUserPreference(getConfiguration(adminConfig), getConfiguration(userConfig));
        } catch (IOException e) {
            throw new ServiceException("Parse Exception User Configuration");
        }
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public String saveAdminConfig(ActivityConfigDTO config) throws ServiceException {
        try {
            return getJson(config);
        } catch (IOException e) {
            throw new ServiceException("Parse Exception Save Admin Configuration");
        }
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public String saveUserConfig(ActivityConfigDTO updatedConfig, String userConfig) throws ServiceException {
        try {
            ActivityConfigDTO usmUserConfig = getConfiguration(userConfig);
            ActivityConfigDTO mergedConfig = PreferenceConfigMapper.INSTANCE.mergeUserPreference(usmUserConfig, updatedConfig);
            return getJson(mergedConfig);
        } catch (IOException e) {
            throw new ServiceException("Parse Exception Save User Configuration");
        }
    }

    @Override
    @SneakyThrows
    /**
     * {@inheritDoc}
     */
    public String resetUserConfig(ActivityConfigDTO resetConfig, String userConfig) throws ServiceException {
        try {
            ActivityConfigDTO usmUserConfig = getConfiguration(userConfig);
            ActivityConfigDTO mergedConfig = PreferenceConfigMapper.INSTANCE.resetUserPreference(usmUserConfig, resetConfig);
            return getJson(mergedConfig);
        } catch (IOException e) {
            throw new ServiceException("Parse Exception Reset User Configuration");
        }
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
