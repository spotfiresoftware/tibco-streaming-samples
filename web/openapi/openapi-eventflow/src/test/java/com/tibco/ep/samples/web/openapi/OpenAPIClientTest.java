package com.tibco.ep.samples.web.openapi;

import com.kabira.platform.property.Status;
import com.streambase.sb.StreamBaseException;
import com.streambase.sb.unittest.ServerManagerFactory;
import com.tibco.ep.samples.web.openapi.openapiclient.handler.ApiClient;
import com.tibco.ep.samples.web.openapi.openapiclient.handler.ApiException;
import com.tibco.ep.samples.web.openapi.openapiclient.handler.GetTheNodeStatusApi;
import com.tibco.ep.samples.web.openapi.openapiclient.model.NodeStatus;
import com.tibco.ep.testing.framework.Configuration;
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

    /**
     * Set up the server
     *
     * @throws StreamBaseException    on start server error
     * @throws ConfigurationException on configuration failure
     * @throws InterruptedException   on start server error
     */
    @BeforeClass
    public static void setupServer() throws StreamBaseException, ConfigurationException, InterruptedException {
        final HttpAuthenticationFeature AUTHENTICATION_FEATURE = HttpAuthenticationFeature.basic(USERNAME, "");

        Client client = ClientBuilder.newClient();
        client.register(AUTHENTICATION_FEATURE);
        WebTarget webTarget;
        Response response;
        webTarget = client.target(new JerseyUriBuilder().scheme("http").host(ADDRESS).port(PORT).path(HEALTH_CHECK).path(VERSION_NAME).path(STATUS).build());
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
