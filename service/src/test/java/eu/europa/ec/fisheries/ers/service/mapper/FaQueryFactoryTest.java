package eu.europa.ec.fisheries.ers.service.mapper;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import eu.europa.ec.fisheries.ers.service.mapper.view.base.FaQueryFactory;
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
        FAQuery query = FaQueryFactory.createFaQueryForVesselId("submitter", "12345678", "CFR", true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getID());
        Assert.assertNotNull(query.getSubmittedDateTime());
        Assert.assertEquals("submitter", query.getSubmitterFLUXParty().getIDS().get(0).getValue());
        Assert.assertEquals("12345678", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("CFR", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals("Y", query.getSimpleFAQueryParameters().get(1).getValueCode().getValue());
        Assert.assertEquals("2019-01-01T10:00:00", query.getSpecifiedDelimitedPeriod().getStartDateTime().getDateTime().toString());
        Assert.assertEquals("2019-02-01T10:00:00", query.getSpecifiedDelimitedPeriod().getEndDateTime().getDateTime().toString());
        Assert.assertNotNull(query.getSpecifiedDelimitedPeriod());
    }
}
