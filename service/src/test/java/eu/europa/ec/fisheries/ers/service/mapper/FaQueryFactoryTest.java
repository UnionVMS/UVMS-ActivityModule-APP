package eu.europa.ec.fisheries.ers.service.mapper;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.Collections;
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
    public void testCreateFaQuery() {
        FAQuery query = FaQueryFactory.createFAQuery();
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getID());
        Assert.assertNotNull(query.getSubmittedDateTime());
    }

    @Test
    public void testCreateFaQueryForVesselId() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.CFR, "CFR123456789"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "121212"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getID());
        Assert.assertNotNull(query.getSubmittedDateTime());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
        Assert.assertEquals("submitter", query.getSubmitterFLUXParty().getIDS().get(0).getValue());
        Assert.assertEquals("CFR123456789", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("CFR", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals("Y", query.getSimpleFAQueryParameters().get(1).getValueCode().getValue());
        Assert.assertEquals("2019-01-01T10:00:00", query.getSpecifiedDelimitedPeriod().getStartDateTime().getDateTime().toString());
        Assert.assertEquals("2019-02-01T10:00:00", query.getSpecifiedDelimitedPeriod().getEndDateTime().getDateTime().toString());
        Assert.assertNotNull(query.getSpecifiedDelimitedPeriod());
    }

    @Test
    public void testVesselIdentifierPriorityCfr() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.GFCM, "GFCM"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.CFR, "CFR123456789"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.IRCS, "IRCS12"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.UVI, "UVI1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.EXT_MARK, "EXT_MARK1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "ICCAT12"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertEquals("CFR123456789", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("CFR", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
    }

    @Test
    public void testVesselIdentifierPriorityIrcs() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.IRCS, "IRCS12"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.UVI, "UVI1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.GFCM, "GFCM"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.EXT_MARK, "EXT_MARK1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "ICCAT12"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertEquals("IRCS12", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("IRCS", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
    }

    @Test
    public void testVesselIdentifierPriorityOther() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.GFCM, "GFCM VAL"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertEquals("GFCM VAL", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("GFCM", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
    }

    @Test
    public void testVesselIdentifierPriorityUvi() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.GFCM, "GFCM"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.UVI, "UVI1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.EXT_MARK, "EXT_MARK1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "ICCAT12"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertEquals("UVI1212", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("UVI", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
    }

    @Test
    public void testVesselIdentifierPriorityExtMrk() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.GFCM, "GFCM"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.EXT_MARK, "EXT_MARK1212"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "ICCAT12"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertEquals("EXT_MARK1212", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("EXT_MARK", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
    }

    @Test
    public void testVesselIdentifierPriorityIccat() throws DatatypeConfigurationException {
        List<VesselIdentifierType> identifiers = new ArrayList<>();
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.GFCM, "GFCM"));
        identifiers.add(new VesselIdentifierType(VesselIdentifierSchemeIdEnum.ICCAT, "ICCAT12"));
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", identifiers, true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertEquals("ICCAT12", query.getSimpleFAQueryParameters().get(0).getValueID().getValue());
        Assert.assertEquals("ICCAT", query.getSimpleFAQueryParameters().get(0).getValueID().getSchemeID());
        Assert.assertEquals(2, query.getSimpleFAQueryParameters().size());
    }

    @Test
    public void testVesselIdentifierPriorityNoIdentifier() throws DatatypeConfigurationException {
        FAQuery query = FaQueryFactory.createFaQueryWithVesselId("submitter", Collections.emptyList(), true,
                DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-01-01T10:00:00"), DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-02-01T10:00:00"));
        Assert.assertFalse(query.getSimpleFAQueryParameters().stream().anyMatch(p -> p.getTypeCode().getValue().equals(FaQueryFactory.VESSELID)));
        Assert.assertEquals(1, query.getSimpleFAQueryParameters().size());
    }




}
