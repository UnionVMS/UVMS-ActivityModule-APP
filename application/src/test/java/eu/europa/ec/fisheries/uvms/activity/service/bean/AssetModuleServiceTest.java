/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.activity.service.bean;

import eu.europa.ec.fisheries.uvms.asset.client.AssetClient;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.commons.service.exception.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetModuleServiceTest {

    @Mock
    private AssetClient assetClient;

    @InjectMocks
    private AssetModuleServiceBean assetsModuleBean;

    @Test
    public void testGetGuidsFromAssets() throws ServiceException {
        // Given
        AssetDTO assetDTO = new AssetDTO();
        UUID assetGuid = UUID.randomUUID();
        assetDTO.setId(assetGuid);

        List<AssetDTO> assetList = new ArrayList<>();
        assetList.add(assetDTO);

        when(assetClient.getAssetList(any(SearchBranch.class))).thenReturn(assetList);

        // When
        List<String> assetGuids = assetsModuleBean.getAssetGuids("JEANNE", UUID.randomUUID().toString());

        // Verify
        assertEquals(1, assetGuids.size());
        assertEquals(assetGuid.toString(), assetGuids.get(0));
    }
}
