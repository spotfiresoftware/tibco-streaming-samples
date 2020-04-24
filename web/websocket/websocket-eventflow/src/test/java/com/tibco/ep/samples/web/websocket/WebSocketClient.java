/* *****************************************************************************
 Copyright (C) 2020, TIBCO Software Inc.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors
 may be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

package com.tibco.ep.samples.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpoint;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * WebSocket client class for testing
 */
@ClientEndpoint(configurator = WebSocketClientConfiguration.class)
public class WebSocketClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClient.class);
    private Session userSession = null;
    private String message;
    static final String messageContent = "Hello, TIBCO";

    /**
     * @param container   webSocketContainer
     * @param endpointURI uri of endpoint which want to connect
     * @throws IOException         Unexpected error when close session when connect to server
     * @throws DeploymentException Unexpected error when close session when connect to server
     */
    WebSocketClient(WebSocketContainer container, URI endpointURI) throws IOException, DeploymentException {
        container.connectToServer(this, endpointURI);
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        LOGGER.debug("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @throws IOException IOException
     */
    @OnClose
    public void onClose() throws IOException {
        if (userSession != null) {
            userSession.close();
            this.userSession = null;
        }
        LOGGER.debug("WebSocket is closed");

    }

    /**
     * onError handler, this method will be invoked when the server meets an error
     *
     * @param throwable throwable
     * @throws IOException error when close session
     */
    @OnError
    public void onError(Throwable throwable) throws IOException {
        if (userSession != null) {
            userSession.close();
            this.userSession = null;
        }
        LOGGER.debug("WebSocket is error");

    }

    /**
     * Callback hook for Message Events. This method will be invoked when the server send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        this.message = message;
    }

    /**
     * Send a message.
     */
    void sendMessage() {
        try {
            this.userSession.getBasicRemote().sendText(messageContent);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * @return message
     */
    String getMessage() {
        return this.message;
    }
}
