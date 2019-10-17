/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.activity.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;

@ArquillianSuiteDeployment
public abstract class BaseActivityArquillianTest {

    @Deployment(name = "activity-rest-test")
    public static WebArchive createDeployment() {
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, "activity-rest-test.war");

        File[] dependencyFiles = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
        testWar.addAsLibraries(dependencyFiles);

        testWar.addAsLibraries(Maven.configureResolver().loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.activity:service", "eu.europa.ec.fisheries.uvms.activity:message")
                .withTransitivity().asFile());

        testWar.addPackages(true,
                "eu.europa.ec.fisheries.uvms.activity.rest");

        testWar.addAsResource("logback-test.xml");

        return testWar;
    }

    protected WebTarget getWebTarget() {
        ObjectMapper objectMapper = new ObjectMapper();
        Client client = ClientBuilder.newClient();
        client.register(objectMapper);

        return client.target("http://localhost:8080/activity-rest-test/rest");
    }
}