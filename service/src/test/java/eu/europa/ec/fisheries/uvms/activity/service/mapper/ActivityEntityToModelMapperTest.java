/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.uvms.activity.service.mapper;

import eu.europa.ec.fisheries.uvms.activity.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.uvms.activity.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.FANamespaceMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.ActivityEntityToModelMapper;
import eu.europa.ec.fisheries.uvms.activity.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.ElementSelectors;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class ActivityEntityToModelMapperTest {

    private Unmarshaller unmarshaller;
    private FluxFaReportMessageMapper incomingFAReportMapper = new FluxFaReportMessageMapper();

    @Before
    public void setUp() throws Exception {
        JAXBContext context = JAXBContext.newInstance(FLUXFAReportMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Test
    @Parameters(method = "resources")
    public void testMapToFLUXFAReportMessage(String resource) throws Exception {

        FLUXFAReportMessage fluxfaReportMessage = sourceToEntity(resource);
        FluxFaReportMessageEntity entity = incomingFAReportMapper.mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.MANUAL, new FluxFaReportMessageEntity());

        FLUXFAReportMessage target = ActivityEntityToModelMapper.INSTANCE.mapToFLUXFAReportMessage(new ArrayList<>(entity.getFaReportDocuments()));

        String controlSource = JAXBUtils.marshallJaxBObjectToString(getFirstElement(fluxfaReportMessage), "ISO-8859-15", true, new FANamespaceMapper());
        String testSource = JAXBUtils.marshallJaxBObjectToString(getFirstElement(target), "ISO-8859-15", true, new FANamespaceMapper());

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        String clearControlSource = clearEmptyTags(controlSource);
        String clearTestSource = clearEmptyTags(testSource);

        org.xmlunit.diff.Diff myDiffSimilar = DiffBuilder
                .compare(clearControlSource)
                .withTest(clearTestSource)
                .ignoreWhitespace()
                .ignoreComments()
                .checkForSimilar()
                .withNodeMatcher(
                         new DefaultNodeMatcher(ElementSelectors.and(
                                 ElementSelectors.byNameAndText,
                                 ElementSelectors.byNameAndAllAttributes)))
                .build();

        assertFalse("XML similar " + myDiffSimilar.toString(), myDiffSimilar.hasDifferences());    }

    private Object[] resources() {

        return $(
                $("fa_flux_message4.xml"),
                $("fa_flux_message5.xml"),
                $("fa_flux_message6.xml"),
                $("fa_flux_message7.xml"),
                $("fa_flux_message8.xml"),
                $("UNFA_IRCS6_14_ARRIVAL_NOT_ESFNE_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_15_CANCEL_ARRIVAL_NOT_ESFNE_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_19_LANDING_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_18_ARRIVAL_DCL_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_07_DELETE_FOP4_withError_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_09_EXIT_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS5_08B_TRA-LOAD_EST-TRP-20170531000000000001.xml"),
                $("UNFA_IRCS4_08_ARRIVAL_DCL_SVN-TRP-SVN-TRP-20170622000000000008.xml"),
                $("UNFA_IRCS4_07_ARRIVAL_NOT_SVN-TRP-SVN-TRP-20170622000000000008.xml"),
                $("UNFA_IRCS4_06_BFT-TRANSFER_DECL_SVN-TRP-SVN-TRP-20170622000000000008.xml"),
                $("UNFA_IRCS4_04_BFT-TRANSFER_NOT_SVN-TRP-SVN-TRP-20170622000000000008.xml"),
                $("UNFA_IRCS6_08A_TRA-UNL_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_01_DEPARTURE_COB_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_02_FOP1_CYP-TRP-20170608000000000010.xml"),
                $("UNFA_IRCS6_03_ENTRY_CYP-TRP-20170608000000000010.xml"),
                //$("UNFA_IRCS6_04_FOP2PAIR_CYP-TRP-20170608000000000010.xml"),
                $("multipleReports.xml"),
                $("multipleReports2.xml")

        );
    }

    private String clearEmptyTags(String testSource) {
        testSource = testSource
                .replaceAll("<([a-zA-Z][a-zA-Z0-9]*)[^>]*/>", "") // clear tags like  <udt:IndicatorString/>
                .replaceAll("\n?\\s*<(\\w+)></\\1>", "")
                .replaceAll("<ram:SpecifiedPhysicalFLUXGeographicalCoordinate>\\s*</ram:SpecifiedPhysicalFLUXGeographicalCoordinate>", "")
                .replaceAll("<ram:ValueIndicator>\\s*</ram:ValueIndicator>","")
                .replaceAll("(?m)^[ \t]*\r?\n", "");// clear empty lines

        return testSource;
    }

    private FAReportDocument getFirstElement(FLUXFAReportMessage source) {
        return source.getFAReportDocuments().get(0);
    }

    private FLUXFAReportMessage sourceToEntity(String resource) throws JAXBException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        return (FLUXFAReportMessage) unmarshaller.unmarshal(is);
    }
}
