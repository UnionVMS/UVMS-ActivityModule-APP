/*
 Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

 This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
 and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.ec.fisheries.ers.mapper.subscription;

import static junit.framework.TestCase.assertTrue;
import static junitparams.JUnitParamsRunner.$;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;

import eu.europa.ec.fisheries.ers.fa.entities.FluxFaReportMessageEntity;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.mapper.ActivityEntityToModelMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapperImpl;
import eu.europa.ec.fisheries.uvms.commons.message.impl.JAXBUtils;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;

@RunWith(JUnitParamsRunner.class)
public class ActivityEntityToModelMapperTest {

    private ActivityEntityToModelMapper modelMapper = new ActivityEntityToModelMapper();
    private Unmarshaller unmarshaller;
    private FluxFaReportMessageMapper incomingFAReportMapper = new FluxFaReportMessageMapperImpl();

    @Before
    public void setUp() throws Exception
    {
        JAXBContext context = JAXBContext.newInstance(FLUXFAReportMessage.class);
        unmarshaller = context.createUnmarshaller();
    }

    @Test
    @Parameters(method = "resources")
    public void testMapToFLUXFAReportMessage(String resource) throws Exception {

        FLUXFAReportMessage fluxfaReportMessage = sourceToEntity(resource);
        FluxFaReportMessageEntity entity = incomingFAReportMapper.mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.MANUAL, new FluxFaReportMessageEntity());

        FLUXFAReportMessage target = modelMapper.mapToFLUXFAReportMessage(new ArrayList<>(entity.getFaReportDocuments()));

        String controlSource = JAXBUtils.marshallJaxBObjectToString(getFirstElement(fluxfaReportMessage));
        String testSource = JAXBUtils.marshallJaxBObjectToString(getFirstElement(target));

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreAttributeOrder(true);

        DetailedDiff diff = new DetailedDiff(new org.custommonkey.xmlunit.Diff(controlSource, testSource));
        assertTrue("XML are similar " + diff, diff.similar());

    }

    private FAReportDocument getFirstElement(FLUXFAReportMessage source) {
        return source.getFAReportDocuments().get(0);
    }

    private FLUXFAReportMessage sourceToEntity(String resource) throws JAXBException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
        return (FLUXFAReportMessage) unmarshaller.unmarshal(is);
    }

    protected Object[] resources(){

        return $(
                //$("fa_flux_message.xml"),
                //$("fa_flux_message2.xml"),
                //$("fa_flux_message3.xml"),
                //$("fa_flux_message4.xml"),
                //$("fa_flux_message5.xml"),
                //$("fa_flux_message6.xml"),
                $("fa_flux_message7.xml")

        );
    }
}
