package eu.europa.ec.fisheries.uvms.activity.rest;

import com.google.common.collect.Sets;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("")
public class UnionVMSMock extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(
                AssetRestMock.class,
                MdrRestMock.class,
                MovementRestMock.class,
                SpatialRestMock.class
        );
    }
}
