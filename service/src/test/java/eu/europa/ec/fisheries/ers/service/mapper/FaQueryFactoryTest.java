package eu.europa.ec.fisheries.ers.service.mapper;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import java.util.ArrayList;
import java.util.List;

import eu.europa.ec.fisheries.ers.service.mapper.view.base.FaQueryFactory;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierSchemeIdEnum;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.VesselIdentifierType;
import org.junit.Assert;
import org.junit.Test;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAQuery;
/**
 * Test class for FaQueryFactory
 */
public class FaQueryFactoryTest {

    @Test
    public void createFaQuery() {
        FAQuery query = FaQueryFactory.createFAQuery();
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getID());
        Assert.assertNotNull(query.getSubmittedDateTime());
    }

    @Test
    public void createFaQueryForVesselId() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.CFR, "CFR123456789"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "121212"));
        FAQuery query = FaQueryFactory.createFaQueryForVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getID());
        Assert.assertNotNull(query.getSubmittedDateTime());
        Assert.assertEquals("submitter", query.getSubmitterFLUXParty().getIDS().get(0).getValue());
        Assert.assertEquals("CFR123456789", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("CFR", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals("121212", query.getSimpleFAQueryParameters().get(1).getValueID().getValue());
        Assert.assertEquals("ICCAT", query.getSimpleFAQueryParameters().get(1).getValueID().getSchemeID());
        Assert.assertEquals("Y", query.getSimpleFAQueryParameters().get(2).getValueCode().getValue());
        Assert.assertEquals("2019-01-01T10:00:00", query.getSpecifiedDelimitedPeriod().getStartDateTime().getDateTime().toString());
        Assert.assertEquals("2019-02-01T10:00:00", query.getSpecifiedDelimitedPeriod().getEndDateTime().getDateTime().toString());
        Assert.assertNotNull(query.getSpecifiedDelimitedPeriod());
    }
}
