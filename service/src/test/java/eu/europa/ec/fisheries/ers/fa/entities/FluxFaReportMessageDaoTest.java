/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.fa.entities;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import eu.europa.ec.fisheries.ers.fa.dao.FluxFaReportMessageDao;
import eu.europa.ec.fisheries.ers.fa.utils.FaReportSourceEnum;
import eu.europa.ec.fisheries.ers.service.mapper.FluxFaReportMessageMapper;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kovian on 26/01/2017.
 */
public class FluxFaReportMessageDaoTest extends BaseErsFaDaoTest {

    private FluxFaReportMessageDao fluxFaRepMessageDao;

    @Before
    @SneakyThrows
    public void prepare() {
        Operation operation = sequenceOf(DELETE_ALL);
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
        fluxFaRepMessageDao = new FluxFaReportMessageDao(em);
    }

    @Test
    @SneakyThrows
	@Ignore
    public void testSaveFluxFaReportMessage(){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("fa_flux_message.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(FLUXFAReportMessage.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        FLUXFAReportMessage fluxfaReportMessage = (FLUXFAReportMessage) jaxbUnmarshaller.unmarshal(is);
        FluxFaReportMessageEntity fluxRepMessageEntity = FluxFaReportMessageMapper.INSTANCE.mapToFluxFaReportMessage(fluxfaReportMessage, FaReportSourceEnum.FLUX, new FluxFaReportMessageEntity());
        em.getTransaction().begin();
        fluxFaRepMessageDao.saveFluxFaReportMessage(fluxRepMessageEntity);
        em.flush();
        em.getTransaction().commit();
        List<FluxFaReportMessageEntity> allEntity = fluxFaRepMessageDao.findAllEntity(FluxFaReportMessageEntity.class);
        assertEquals(1, allEntity.size());
        FluxFaReportMessageEntity fluxFaReportMessageEntity = allEntity.get(0);
        assertEquals(1, fluxFaReportMessageEntity.getFaReportDocuments().size());
        assertNotNull(fluxFaReportMessageEntity.getFluxReportDocument());
    }

}
