/* ******************************************************************************
 * Copyright (C) 2020, TIBCO Software Inc.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package com.tibco.ep.samples.web.openapi;

import com.kabira.platform.property.Status;
import com.streambase.sb.StreamBaseException;
import com.tibco.ep.samples.web.openapi.openapiclient.handler.ApiClient;
import com.tibco.ep.samples.web.openapi.openapiclient.handler.ApiException;
import com.tibco.ep.samples.web.openapi.openapiclient.handler.GetTheNodeStatusApi;
import com.tibco.ep.samples.web.openapi.openapiclient.model.NodeStatus;
import com.tibco.ep.testing.framework.ConfigurationException;
import com.tibco.ep.testing.framework.UnitTest;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.BeforeClass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

public class OpenAPIClientTest extends UnitTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPIClientTest.class);

    private static final String USERNAME = System.getProperty("user.name");
    private static final String NODE_NAME = System.getProperty(Status.NODE_NAME);
    private final static String ADDRESS = "localhost";
    //node web server default port number
    private final static int PORT = 8008;
    private final static String HEALTH_CHECK = "healthcheck";
    private final static String VERSION_NAME = "v1";
    private final static String STATUS = "status";
    private static String url;


    /**
     * Wait for the health check web service get deployed
     *
     * @throws InterruptedException on start server error
     */
    @BeforeClass
    public static void waitForWARDeployed() throws InterruptedException {

        Administration administration = new Administration();
        Map<String, String> subs = new HashMap<>();
        subs.put("type", "webservice");
        subs.put("name", HEALTH_CHECK);
        final Results results = administration.execute("display", "web");
        Assert.assertEquals(DtmCommand.COMMAND_SUCCEEDED, results.returnCode());

        url = results.getCommandResults()
                     .get(0)
                     .getResultSet()
                     .getRows()
                     .get(0)
                     .getColumn(results.getCommandResults().get(0).getHeaderColumn("Base URL"));


        final HttpAuthenticationFeature AUTHENTICATION_FEATURE = HttpAuthenticationFeature.basic(USERNAME, "");

        Client client = ClientBuilder.newClient();
        client.register(AUTHENTICATION_FEATURE);
        WebTarget webTarget;
        Response response;
        webTarget = client.target(new JerseyUriBuilder().path(url).path(VERSION_NAME).path(STATUS).build());
        boolean isStarted = false;

        for (int i = 0; i < 60; i++) {
            response = webTarget.request().get();
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                isStarted = true;
                break;
            }
            LOGGER.info("Healthcheck web service is not ready, wait for 1 sec. Then re-try");
            Thread.sleep(1000);
        }

        if (!isStarted) {
            Assert.fail("Starting Healthcheck web service is failed.");
        }
    }

    /**
     * Generated web client api test
     *
     * @throws ApiException error on test
     */
    @Test
    public void getStatusTest() throws ApiException {

        ApiClient apiClient = new ApiClient();
        apiClient.setUsername(USERNAME);
        GetTheNodeStatusApi getTheNodeStatusApi = new GetTheNodeStatusApi();
        getTheNodeStatusApi.setApiClient(apiClient);
        Assert.assertEquals(NodeStatus.NodeStateEnum.STARTED, getTheNodeStatusApi.statusGet().getNodeState());
        Assert.assertEquals(NODE_NAME, getTheNodeStatusApi.statusGet().getNodeName());
    }
}
