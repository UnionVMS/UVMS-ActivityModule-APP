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
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ArquillianSuiteDeployment
public abstract class BaseActivityArquillianTest {
    private static final String ACTIVITY_REST_TEST = "activity-rest-test";

    private static final String ANONYMIZED_FLUX_MESSAGES_FOLDER_NAME = "anonymized_flux_messages";
    private static final List<String> ANONYMIZED_FLUX_MESSAGES = Arrays.asList(
            "flux001_anonymized.xml",
            "flux002_anonymized.xml",
            "flux003_anonymized.xml",
            "flux004_anonymized.xml",
//            "flux005_anonymized.xml", same trip as in flux004
            "flux006_anonymized.xml",
//            "flux007_anonymized.xml", same trip as in flux004
            "flux008_anonymized.xml"
    );

    public static String[] resources() {
        return new String[] {
                "fa_flux_message4.xml",
                "fa_flux_message5.xml",
                "fa_flux_message6.xml",
                "fa_flux_message7.xml",
                "fa_flux_message8.xml",
                "UNFA_IRCS6_14_ARRIVAL_NOT_ESFNE_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_15_CANCEL_ARRIVAL_NOT_ESFNE_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_19_LANDING_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_18_ARRIVAL_DCL_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_07_DELETE_FOP4_withError_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_09_EXIT_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS5_08B_TRA-LOAD_EST-TRP-20170531000000000001.xml",
                "UNFA_IRCS4_08_ARRIVAL_DCL_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS4_07_ARRIVAL_NOT_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS4_06_BFT-TRANSFER_DECL_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS4_04_BFT-TRANSFER_NOT_SVN-TRP-SVN-TRP-20170622000000000008.xml",
                "UNFA_IRCS6_08A_TRA-UNL_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_01_DEPARTURE_COB_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_02_FOP1_CYP-TRP-20170608000000000010.xml",
                "UNFA_IRCS6_03_ENTRY_CYP-TRP-20170608000000000010.xml",
                //"UNFA_IRCS6_04_FOP2PAIR_CYP-TRP-20170608000000000010.xml",
                "multipleReports.xml",
                "multipleReports2.xml"
        };
    }

    private static boolean hasPopulatedTestData = false;

    @EJB
    private JwtTokenHandler tokenHandler;

    @EJB
    private FluxMessageService fluxMessageService;

    protected String authToken;

    @Deployment(name = ACTIVITY_REST_TEST)
    public static WebArchive createDeployment() {
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, ACTIVITY_REST_TEST + ".war");

        File[] dependencyFiles = Maven
                .configureResolver()
                .loadPomFromFile("pom.xml")
                .importRuntimeAndTestDependencies()
                .resolve()
                .withTransitivity()
                .asFile();
        testWar.addAsLibraries(dependencyFiles);

        testWar.addPackages(true,
                "eu.europa.ec.fisheries.uvms.activity");

        testWar.deleteClass(UserRoleInterceptor.class);

        testWar.addAsResource("logback-test.xml");
        testWar.addAsResource("META-INF/persistence.xml");
        testWar.addAsResource("fa_flux_message.xml");


        for (String anonymizedFluxMessageFileName : ANONYMIZED_FLUX_MESSAGES) {
            testWar.addAsResource(ANONYMIZED_FLUX_MESSAGES_FOLDER_NAME + "/" + anonymizedFluxMessageFileName);
        }

        for(String resource : resources()) {
            testWar.addAsResource(resource);
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
        ctx.rebind("java:global/mdr_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/mdrmock");
        ctx.rebind("java:global/asset_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/assetmock");
        ctx.rebind("java:global/movement_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/movementmock");
        ctx.rebind("java:global/spatial_endpoint", "http://localhost:8080/" + ACTIVITY_REST_TEST + "/spatialmock");

        authToken = getToken();

        if (!hasPopulatedTestData) {
            populateFluxTestData();
            hasPopulatedTestData = true;
        }
    }

    @Transactional
    public void populateFluxTestData() throws IOException, JAXBException, ServiceException {
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
