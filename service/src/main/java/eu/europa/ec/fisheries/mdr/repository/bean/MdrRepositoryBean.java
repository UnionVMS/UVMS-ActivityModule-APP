/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.mdr.repository.bean;

import eu.europa.ec.fisheries.mdr.dao.ActivityConfigurationDao;
import eu.europa.ec.fisheries.mdr.dao.MasterDataRegistryDao;
import eu.europa.ec.fisheries.mdr.dao.MdrBulkOperationsDao;
import eu.europa.ec.fisheries.mdr.domain.ActivityConfiguration;
import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.domain.MdrStatus;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.mdr.mapper.MdrEntityMapper;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.mdr.repository.MdrStatusRepository;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Stateless
@Slf4j
public class MdrRepositoryBean implements MdrRepository {
	
	@PersistenceContext(unitName = "activityPU")
    private EntityManager em;

	private MdrBulkOperationsDao bulkOperationsDao;
	
	private ActivityConfigurationDao mdrConfigDao;

	@EJB
	private MdrStatusRepository statusRepository;
    
    @SuppressWarnings("rawtypes")
	private MasterDataRegistryDao mdrDao;

    @PostConstruct
    public void init() {
    	bulkOperationsDao = new MdrBulkOperationsDao(em);
    	mdrDao 			  = new MasterDataRegistryDao<>(em);
    	mdrConfigDao      = new ActivityConfigurationDao(em);
    }

	@SuppressWarnings("unchecked")
	public <T extends MasterDataRegistry> List<T> findAllForEntity(Class<T> mdr) throws ServiceException {
		return mdrDao.findAllEntity(mdr.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MasterDataRegistry> List<T> findEntityByHqlQuery(Class<T> type, String hqlQuery, Map<Integer, String> parameters,
			int maxResultLimit) throws ServiceException {
		return mdrDao.findEntityByHqlQuery(type, hqlQuery, parameters, maxResultLimit);
	}
	
	@Override
	public void updateMdrEntity(ResponseType response){
		List<MasterDataRegistry> mdrEntityRows = MdrEntityMapper.mapJAXBObjectToMasterDataType(response);
		if(CollectionUtils.isNotEmpty(mdrEntityRows)){
			try {
				bulkOperationsDao.singleEntityBulkDeleteAndInsert(mdrEntityRows);
				statusRepository.updateStatusSuccessForAcronym(mdrEntityRows.get(0).getAcronym(), AcronymListState.SUCCESS, DateUtils.nowUTC().toDate());
			} catch (ServiceException e) {
				statusRepository.updateStatusFailedForAcronym(mdrEntityRows.get(0).getAcronym());
				log.error("Transaction rolled back! Couldn't persist mdr Entity : "+e.getMessage(),e);
			}
		} else {
			log.error("Got Message from Flux related to MDR but, the list is empty! So, nothing is going to be persisted!");
		}

	}
	
	/*
	 * MDR Configurations.
	 */
	@Override
	public List<ActivityConfiguration> getAllConfigurations() throws ServiceException{
		return mdrConfigDao.findAllConfigurations();
	}
	
	@Override
	public ActivityConfiguration getConfigurationByName(String configName) {
		return mdrConfigDao.findConfiguration(configName);
	}
	
	@Override
    public void changeMdrSchedulerConfiguration(String newCronExpression) throws ServiceException{
    	mdrConfigDao.changeMdrSchedulerConfiguration(newCronExpression);
    }
	
	@Override
	public ActivityConfiguration getMdrSchedulerConfiguration(){
		return mdrConfigDao.getMdrSchedulerConfiguration();
	}
	
	/*
	 * MDR Acronym's statuses.
	 */
	@Override
    public List<MdrStatus> findAllStatuses() throws ServiceException {
        return statusRepository.getAllAcronymsStatuses();
    }

	@Override
    public MdrStatus findStatusByAcronym(String acronym){
    	return statusRepository.getStatusForAcronym(acronym);
    }

}