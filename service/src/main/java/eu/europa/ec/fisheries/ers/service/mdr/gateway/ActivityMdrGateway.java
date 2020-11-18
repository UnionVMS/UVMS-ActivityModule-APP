package eu.europa.ec.fisheries.ers.service.mdr.gateway;

import un.unece.uncefact.data.standard.mdr.communication.MdrGetAllCodeListsResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListRequest;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetLastRefreshDateResponse;

public interface ActivityMdrGateway {

    MdrGetCodeListResponse getMdrCodeList(MdrGetCodeListRequest request);

    MdrGetLastRefreshDateResponse getLastRefreshDate();
}
