package eu.europa.ec.fisheries.ers.service.mdr.gateway.impl;


import eu.europa.ec.fisheries.ers.service.mdr.gateway.ActivityMdrGateway;
import eu.europa.ec.fisheries.uvms.mdr.rest.client.MdrClient;
import lombok.extern.slf4j.Slf4j;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListRequest;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetCodeListResponse;
import un.unece.uncefact.data.standard.mdr.communication.MdrGetLastRefreshDateResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ActivityMdrGatewayImpl implements ActivityMdrGateway {

    @Inject
    private MdrClient mdrClient;

    @Override
    public MdrGetCodeListResponse getMdrCodeList(MdrGetCodeListRequest request) {
        return mdrClient.getSingleMdrCodeListMessage(request);
    }

    @Override
    public MdrGetLastRefreshDateResponse getLastRefreshDate() {
        return mdrClient.getLastRefreshDate();
    }
}
