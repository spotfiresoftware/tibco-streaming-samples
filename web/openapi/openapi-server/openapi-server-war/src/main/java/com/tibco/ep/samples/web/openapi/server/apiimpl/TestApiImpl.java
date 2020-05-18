package com.tibco.ep.samples.web.openapi.server.apiimpl;

import com.tibco.ep.samples.web.openapi.server.api.TestApi;
import com.tibco.ep.samples.web.openapi.server.model.Message;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/test")
public class TestApiImpl implements TestApi {

    @GET
    @Produces({ "application/json" })
    @Override
    public Response testGet() {
        Message message = new Message();
        message.setMessage("Hello, TIBCO!");
        return Response.ok().entity(message).build();
    }
}
