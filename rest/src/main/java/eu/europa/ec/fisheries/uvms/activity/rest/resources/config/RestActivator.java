package eu.europa.ec.fisheries.uvms.activity.rest.resources.config;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import eu.europa.ec.fisheries.uvms.activity.rest.resources.FishingActivityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.ec.fisheries.uvms.activity.rest.resources.ActivateMdrSynchronizationResource;

@ApplicationPath("/rest")
public class RestActivator extends Application {

    final static Logger LOG = LoggerFactory.getLogger(RestActivator.class);

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> set = new HashSet<>();

    public RestActivator() {
        set.add(ActivateMdrSynchronizationResource.class);
        set.add(FishingActivityResource.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return set;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}