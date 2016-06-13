package eu.europa.ec.fisheries.mdr.service.bean;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import eu.europa.ec.fisheries.mdr.domain.MasterDataRegistry;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import xeu.ec.fisheries.flux_bl.flux_mdr_codelist._1.ResponseType;

@Local
public interface MdrRepository {

	<T extends MasterDataRegistry> List<T> findAllForEntity(Class<T> mdr) throws ServiceException;
	
	<T extends MasterDataRegistry> List<T> findEntityByHqlQuery(Class<T> type, String hqlQuery, Map<Integer, String> parameters,
			int maxResultLimit) throws ServiceException;
	
	public void updateMdrEntity(ResponseType response);
}
