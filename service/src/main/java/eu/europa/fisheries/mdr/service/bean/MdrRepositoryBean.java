package eu.europa.fisheries.mdr.service.bean;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import eu.europa.ec.fisheries.mdr.dao.MasterDataRegistryDao;
import eu.europa.ec.fisheries.mdr.dao.MdrBulkOperationsDao;
import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.mdr.mapper.MdrEntityMapper;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;

@Stateless
public class MdrRepositoryBean implements MdrRepository {
	
	@PersistenceContext(unitName = "activityPU")
    private EntityManager em;

	private MdrBulkOperationsDao bulkOperationsDao;
    
    @SuppressWarnings("rawtypes")
	private MasterDataRegistryDao mdrDao;

    @PostConstruct
    public void init() {
    	bulkOperationsDao = new MdrBulkOperationsDao(em);
    	mdrDao 			  = new MasterDataRegistryDao<>(em);
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
	
	public void updateMdrEntity(ResponseType response){
		List<MasterDataRegistry> mdrEntityRows = MdrEntityMapper.mappJAXBObjectToMasterDataType(response);
		try {
			bulkOperationsDao.singleEntityBulkDeleteAndInsert(mdrEntityRows);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}


}
