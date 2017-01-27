/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.ers.service.bean;

import eu.europa.ec.fisheries.uvms.activity.message.consumer.ActivityConsumerBean;
import eu.europa.ec.fisheries.uvms.activity.message.producer.AssetProducerBean;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.TextMessage;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by kovian on 26/01/2017.
 */
public class AssetModuleServiceTest {

    ConnectionFactory connectionFactory;

    AssetProducerBean assetProducer;

    ActivityConsumerBean activityConsumer;

    AssetModuleServiceBean assetsModuleBean;


    @Before
    public void prepare(){

        assetProducer     = mock(AssetProducerBean.class);
        activityConsumer  = mock(ActivityConsumerBean.class);
        connectionFactory = mock(ConnectionFactory.class);
        assetsModuleBean  = mock(AssetModuleServiceBean.class);
    }

    @Test
    @SneakyThrows
    public void testGetGuidsFromAssets(){//
        doReturn("12222-4rrr-566t-dwq11").when(assetProducer).sendModuleMessage(Mockito.anyString(), any(Destination.class));
        doReturn(null).when(activityConsumer).getMessage("12222-4rrr-566t-dwq11", TextMessage.class);
        doReturn(new ArrayList<>()).when(assetsModuleBean).getGuidsFromAssets(Mockito.anyString());
        assetsModuleBean.getAssetGuids("JEANNE", "test-group");

        // Verify
        Mockito.verify(assetsModuleBean, Mockito.times(1)).getAssetGuids(any(String.class), any(String.class));
    }


}
