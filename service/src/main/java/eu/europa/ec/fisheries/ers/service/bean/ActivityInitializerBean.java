package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.init.AbstractModuleInitializerBean;

import javax.ejb.Singleton;
import java.io.InputStream;

/**
 * Created by padhyad on 5/2/2016.
 */
@Singleton
public class ActivityInitializerBean extends AbstractModuleInitializerBean {

    @Override
    protected InputStream getDeploymentDescriptorRequest() {
        return this.getClass().getClassLoader().getResourceAsStream("usmDeploymentDescriptor.xml");
    }

    @Override
    protected boolean mustRedeploy() {
        return true;
    }


}
