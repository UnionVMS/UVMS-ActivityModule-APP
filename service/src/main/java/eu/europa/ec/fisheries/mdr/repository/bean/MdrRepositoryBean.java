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
import eu.europa.ec.fisheries.mdr.dao.MdrStatusDao;
import eu.europa.ec.fisheries.mdr.domain.ActivityConfiguration;
import eu.europa.ec.fisheries.mdr.domain.MdrCodeListStatus;
import eu.europa.ec.fisheries.mdr.domain.base.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.domain.constants.AcronymListState;
import eu.europa.ec.fisheries.mdr.exception.ActivityCacheInitException;
import eu.europa.ec.fisheries.mdr.mapper.MasterDataRegistryEntityCacheFactory;
import eu.europa.ec.fisheries.mdr.mapper.MdrEntityMapper;
import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import un.unece.uncefact.data.standard.response.FLUXMDRReturnMessage;
import un.unece.uncefact.data.standard.response.FLUXResponseDocumentType;
import un.unece.uncefact.data.standard.response.IDType;
import un.unece.uncefact.data.standard.response.MDRDataSetType;

import javax.annotation.PostConstruct;
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

	private MdrStatusDao statusDao;

	private MasterDataRegistryDao mdrDao;

    @PostConstruct
    public void init() {
    	bulkOperationsDao = new MdrBulkOperationsDao(em);
    	mdrDao 			  = new MasterDataRegistryDao<>(em);
    	mdrConfigDao      = new ActivityConfigurationDao(em);
		statusDao         = new MdrStatusDao(em);
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
	public void updateMdrEntity(FLUXMDRReturnMessage response){
		// Response is OK
		final FLUXResponseDocumentType fluxResponseDocument = response.getFLUXResponseDocument();
		if(fluxResponseDocument.getResponseCode().toString().toUpperCase() != "NOK") {
			List<MasterDataRegistry> mdrEntityRows = MdrEntityMapper.mapJAXBObjectToMasterDataType(response);
			final MDRDataSetType mdrDataSet = response.getMDRDataSet();
			if (CollectionUtils.isNotEmpty(mdrEntityRows)) {
				try {
					bulkOperationsDao.singleEntityBulkDeleteAndInsert(mdrEntityRows);
					statusDao.updateStatusSuccessForAcronym(mdrDataSet, AcronymListState.SUCCESS, DateUtils.nowUTC().toDate());
				} catch (ServiceException e) {
					statusDao.updateStatusFailedForAcronym(mdrEntityRows.get(0).getAcronym());
					log.error("Transaction rolled back! Couldn't persist mdr Entity : ", e);
				}
			} else {
				log.error("Got Message from Flux related to MDR but, the list is empty! So, nothing is going to be persisted!");
			}
		// Response is NOT OK
		} else {
			final IDType referencedID = fluxResponseDocument.getReferencedID();
			if(referencedID != null && StringUtils.isNotEmpty(referencedID.getValue())){//, but has referenceID
				statusDao.updateStatusFailedForAcronym(extractAcronymFromReferenceId(referencedID.getValue()));
			} else {//, and doesn't have referenceID
                log.error("[[ERROR]] The MDR response received in activity was NOK and has no referenceId!!");
            }
		}
	}

	private String extractAcronymFromReferenceId(String responseReferenceID) {
		return responseReferenceID.split("--")[0];
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
    public List<MdrCodeListStatus> findAllStatuses() throws ServiceException {
        return statusDao.getAllAcronymsStatuses();
    }

	@Override
    public MdrCodeListStatus findStatusByAcronym(String acronym){
    	return statusDao.getStatusForAcronym(acronym);
    }

	@Override
	/**
	 * This method is searching for code list items for a given code list by its acronym. The search is using Hibernate Search API, which is based on Lucene indexing, for high performance.
	 * @param acronym of the code list which the method is filtering. [Mandatory parameter]
	 * @param offset is the number of the first returned element
	 * @param pageSize is the total number of items to be returned
	 * @param sortBy is a field name which will be used for searching
	 * @param isReversed is a boolean flag that defines whether the sorting is reversed
	 * @param filter is a free text string that is used for code lists search
	 * @param searchOnAttributes if filter is specified, this field is mandatory. It's an array of all fields that will be used for filtering
	 * @return a list of code list items (instances of MasterDataRegistry class)
	 * @throws ServiceException
	 */
	public List<? extends MasterDataRegistry> findCodeListItemsByAcronymAndFilter(String acronym, Integer offset, Integer pageSize, String sortBy, Boolean isReversed, String filter, String searchAttribute) throws ServiceException {

		log.debug("[START] findCodeListItemsByAcronymAndFilter(acronym=[{}], offset=[{}], pageSize=[{}], sortBy=[{}], isReversed=[{}], filter=[{}], searchOnAttribute=[{}])");
        FullTextQuery query = buildCodeListItemsQuery(acronym, filter, searchAttribute);

        if (offset != null) {
            query.setFirstResult(offset);
        }

        if (pageSize != null) {
            query.setMaxResults(pageSize);
        }

        if (StringUtils.isNotBlank(sortBy)) {
            query.setSort(new Sort(new SortField(sortBy, SortField.STRING, isReversed)));
        }

        log.debug("[END] findCodeListItemsByAcronymAndFilter(...)");
		return query.getResultList();
	}

    @Override
    /**
     * This method is searching for code list items for a given code list by its acronym. The search is using Hibernate Search API, which is based on Lucene indexing, for high performance.
     * @param acronym of the code list which the method is filtering. [Mandatory parameter]
     * @param offset is the number of the first returned element
     * @param pageSize is the total number of items to be returned
     * @param sortBy is a field name which will be used for searching
     * @param isReversed is a boolean flag that defines whether the sorting is reversed
     * @param filter is a free text string that is used for code lists search
     * @param searchOnAttributes if filter is specified, this field is mandatory. It's an array of all fields that will be used for filtering
     * @return a the total count of search results
     * @throws ServiceException
     */
    public int countCodeListItemsByAcronymAndFilter(String acronym, String filter, String searchAttribute) throws ServiceException {
        log.debug("[START] countCodeListItemsByAcronymAndFilter(acronym=[{}], offset=[{}], pageSize=[{}], sortBy=[{}], isReversed=[{}], filter=[{}], searchOnAttribute=[{}])");
        FullTextQuery query = buildCodeListItemsQuery(acronym, filter, searchAttribute);
        log.debug("[END] countCodeListItemsByAcronymAndFilter(...)");
        return query.getResultSize();
    }

    private FullTextQuery buildCodeListItemsQuery(String acronym, String filter, String searchAttributes) throws ServiceException {
        FullTextQuery persistenceQuery;

        try {
            if (StringUtils.isBlank(acronym)) {
                throw new IllegalArgumentException("No acronym parameter is provided.");
            }

            if (StringUtils.isBlank(filter) || StringUtils.isBlank(searchAttributes)) {
                throw new IllegalArgumentException("No search attributes are provided.");
            }

            MasterDataRegistry codeListObj = MasterDataRegistryEntityCacheFactory.getInstance().getNewInstanceForEntity(acronym);
            FullTextEntityManager fullTextEntityManager =
                    org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

            // create native Lucene query using the query DSL
            // alternatively you can write the Lucene query using the Lucene query parser
            // or the Lucene programmatic API. The Hibernate Search DSL is recommended though
            QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder().forEntity(codeListObj.getClass()).get();
            org.apache.lucene.search.Query query = qb
                    .keyword()
                    .wildcard()
                    .onField(searchAttributes)
                    .matching(filter)
                    .createQuery();

            log.debug("Using lucene query: {}", query.toString() );

            // wrap Lucene query in a javax.persistence.Query
            persistenceQuery = fullTextEntityManager.createFullTextQuery(query, codeListObj.getClass());

            log.debug("Using hibernate query: {}", persistenceQuery.getParameters());
            // em.getTransaction().begin();
            // execute search

            // em.getTransaction().commit();
            // em.close();
        } catch ( IllegalArgumentException | ActivityCacheInitException e) {
            throw new ServiceException("Unable to execute search query due to internal server error.", e);
        }

        return persistenceQuery;
    }


}