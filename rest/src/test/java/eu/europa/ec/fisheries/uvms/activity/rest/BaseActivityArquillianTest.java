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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessCodeEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProcessEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.AapProductEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPartyRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.ContactPersonEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.DelimitedPeriodEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaCatchEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FaReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingActivityEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingGearRoleEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FishingTripIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxLocationEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxPartyIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportDocumentEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxReportIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearCharacteristicEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.GearProblemEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.SizeDistributionClassCodeEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.SizeDistributionEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.StructuredAddressEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselIdentifierEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.entities.VesselTransportMeansEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.service.FluxMessageService;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import eu.europa.ec.fisheries.uvms.rest.security.UnionVMSFeature;
import eu.europa.ec.mare.usm.jwt.JwtTokenHandler;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ArquillianSuiteDeployment
public abstract class BaseActivityArquillianTest {
    private static final String ACTIVITY_REST_TEST = "activity-rest-test";

    // Note: The order here is important because of foreign keys in the db
    private static final List<Class> ENTITY_CLASSES_TO_TRUNCATE_AFTER_TEST = ImmutableList.of(
            FaReportIdentifierEntity.class,
            FishingTripIdentifierEntity.class,
            FluxPartyIdentifierEntity.class,
            FluxReportIdentifierEntity.class,
            VesselIdentifierEntity.class,

            GearProblemEntity.class,
            StructuredAddressEntity.class,
            SizeDistributionClassCodeEntity.class,
            GearCharacteristicEntity.class,
            FluxLocationEntity.class,
            DelimitedPeriodEntity.class,
            FishingGearRoleEntity.class,
            FishingGearEntity.class,
            ContactPartyRoleEntity.class,
            ContactPartyEntity.class,
            ContactPersonEntity.class,
            AapProductEntity.class,
            AapProcessCodeEntity.class,
            AapProcessEntity.class,
            FaCatchEntity.class,
            SizeDistributionEntity.class,
            VesselTransportMeansEntity.class,
            FishingTripEntity.class,
            FishingActivityEntity.class,
            FaReportDocumentEntity.class,
            FluxCharacteristicEntity.class,
            FluxReportDocumentEntity.class,
            FluxFaReportMessageEntity.class,
            FluxPartyEntity.class
            );

    public static final String ANONYMIZED_FLUX_MESSAGES_FOLDER_NAME = "anonymized_flux_messages";
    private static final Set<String> ANONYMIZED_FLUX_MESSAGES = ImmutableSet.of(
            "flux1_anonymized.xml",
            "flux2_anonymized.xml",
            "flux3_anonymized.xml"
    );

    @EJB
    private JwtTokenHandler tokenHandler;

    @EJB
    private FluxMessageService fluxMessageService;

    @Inject
    private UserTransaction userTransaction;

    protected String authToken;

    @Deployment(name = ACTIVITY_REST_TEST)
    public static WebArchive createDeployment() {
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, ACTIVITY_REST_TEST + ".war");

        File[] dependencyFiles = Maven
                .configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .importRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
        testWar.addAsLibraries(dependencyFiles);

        testWar.addAsLibraries(Maven.configureResolver()
                .workOffline()
                .loadPomFromFile("pom.xml")
                .resolve("eu.europa.ec.fisheries.uvms.activity:service")
                .withTransitivity().asFile());

        testWar.addPackages(true,
                "eu.europa.ec.fisheries.uvms.activity.rest");

        testWar.deleteClass(UserRoleInterceptor.class);

        testWar.addAsResource("logback-test.xml");

        for (String anonymizedFluxMessageFileName : ANONYMIZED_FLUX_MESSAGES) {
            testWar.addAsResource(ANONYMIZED_FLUX_MESSAGES_FOLDER_NAME + "/" + anonymizedFluxMessageFileName);
        }

        testWar.delete("/WEB-INF/web.xml");
        testWar.addAsWebInfResource("mock-web.xml", "web.xml");

        return testWar;
    }

    protected WebTarget getWebTarget() {
        ObjectMapper objectMapper = new ObjectMapper();
        Client client = ClientBuilder.newClient();
        client.register(objectMapper);

        return client.target("http://localhost:8080/" + ACTIVITY_REST_TEST + "/rest");
    }

    private String getToken(UnionVMSFeature... features) {
        List<Integer> featureIds = Stream.of(features)
                .map(UnionVMSFeature::getFeatureId)
                .collect(Collectors.toList());

        return tokenHandler.createToken("user", featureIds);
    }

    protected void setUp() throws NamingException, ServiceException, JAXBException, IOException, SystemException, NotSupportedException {
        InitialContext ctx = new InitialContext();
        ctx.rebind("java:global/spatial_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST);
        ctx.rebind("java:global/mdr_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/mdrmock");
        ctx.rebind("java:global/asset_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/assetmock");
        ctx.rebind("java:global/movement_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/movementmock");
        ctx.rebind("java:global/spatial_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/spatialmock");

        authToken = getToken();

        userTransaction.begin();

        populateFluxTestData();
    }

    @After
    public void tearDown() throws SystemException {
        userTransaction.rollback();
    }

    private void populateFluxTestData() throws IOException, JAXBException, ServiceException {
        for (String anonymizedFluxMessageFileName : ANONYMIZED_FLUX_MESSAGES) {
            FLUXFAReportMessage fluxMessage = getMessageFromTestResource(ANONYMIZED_FLUX_MESSAGES_FOLDER_NAME + "/" + anonymizedFluxMessageFileName);
            fluxMessageService.saveFishingActivityReportDocuments(fluxMessage, FaReportSourceEnum.FLUX);
        }
    }

    private FLUXFAReportMessage getMessageFromTestResource(String fileName) throws IOException, JAXBException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = contextClassLoader.getResourceAsStream(fileName)) {
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                BufferedReader reader = new BufferedReader(isr);
                String fileAsString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                return JAXBUtils.unMarshallMessage(fileAsString, FLUXFAReportMessage.class);
            }
        }
    }
}
