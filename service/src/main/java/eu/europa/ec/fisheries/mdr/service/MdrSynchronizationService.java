package eu.europa.ec.fisheries.mdr.service;

import javax.ejb.Local;

@Local
public interface MdrSynchronizationService {

	void manualStartMdrSynchronization();

}
