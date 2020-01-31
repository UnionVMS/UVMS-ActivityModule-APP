package eu.europa.ec.fisheries.uvms.activity.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("")
public class UnionVMSMock extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> sets = new HashSet<>();
        sets.add(AssetRestMock.class);
        sets.add(MdrRestMock.class);
        return sets;
    }
}
