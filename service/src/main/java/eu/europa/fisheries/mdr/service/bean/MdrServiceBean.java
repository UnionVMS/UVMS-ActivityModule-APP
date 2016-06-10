package eu.europa.fisheries.mdr.service.bean;

import eu.europa.ec.fisheries.uvms.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.fisheries.mdr.service.MdrService;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

//@Stateless
//@Slf4j
//@MessageDriven
public class MdrServiceBean implements MdrService {

//	@EJB
//	private MdrRepository mdrRepository;
	
	 //@Schedule(minute="*", hour="*", persistent=false, info="AUTO_TIMER_0")
	    public void atSchedule() throws InterruptedException, JAXBException, MessageException, JMSException, ServiceException {
	       /* try {

	                // do something on application startup
	                InputStream deploymentDescInStream = getDeploymentDescriptorRequest();
	                if (deploymentDescInStream != null) {
	                    JAXBContext jaxBcontext = JAXBContext.newInstance(DeployApplicationRequest.class);
	                    javax.xml.bind.Unmarshaller um = jaxBcontext.createUnmarshaller();

	                    DeployApplicationRequest applicationDefinition = (DeployApplicationRequest) um.unmarshal(deploymentDescInStream);

	                    if (!isAppDeployed(applicationDefinition.getApplication())) {
	                        usmService.deployApplicationDescriptor(applicationDefinition.getApplication());
	                    } else if(mustRedeploy()) {
	                        usmService.redeployApplicationDescriptor(applicationDefinition.getApplication());
	                    }
	                } else {
	                    log.error("USM deployment descriptor is not provided, therefore, the JMS deployment message cannot be sent.");
	                }
	                stopTimer(); // Stop timer as there is no exception and communication to USM is successful

	        } catch (ServiceException e) {
	            count ++;
	            LOG.info("Failed to connect to USM. Retry count " + count);
	            if (count == 5) { // Stop timer after 5 retry
	                stopTimer();
	                throw new ServiceException("Deployment failed : Could not connect to USM");
	            }
	        } */
	        
	        //List<ResponseType> str = null;
//	        
//	        for() {
//	        	
//	        	
//	        }
	        
//	        MdrEntityMapper.INSTANCE.mapSomeReponseObjectToMasterDataRegistry(projectionEntity);
	    }
	    

}
