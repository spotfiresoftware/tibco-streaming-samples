/* *****************************************************************************
 Copyright (C) 2018-2019, TIBCO Software Inc.

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

package com.tibco.ep.samples.web.websocket.websocketwar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * The class give a simple sample of WebSocket endpoint
 */
@ServerEndpoint(WebSocketEndpoint.PATH)
public class WebSocketEndpoint {

    static final String PATH = "/test";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEndpoint.class);

    /**
     * When a user trys to initiated a new WebSocket connection, this method will be invoke and send a
     * response message says "Connection Established".
     *
     * @param session current session
     */
    @OnOpen
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendText("Connection Established");
        } catch (IOException ex) {
            LOGGER.error("Error on onOpen: {}", ex.getMessage());
        }
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String and send a
     * response message with "Received messages:" prefix.
     *
     * @param message received message
     * @param session current session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText("Received message: " + message);
        } catch (IOException ex) {
            LOGGER.error("Error on onMessage: {}", ex.getMessage());
        }
    }

    /**
     * The user closes the connection.
     *
     * @param session current session
     */
    @OnClose
    public void onClose(Session session) {
        try {
            session.getBasicRemote().sendText("Connection is close");
        } catch (IOException ex) {
            LOGGER.error("Error on onClose: {}", ex.getMessage());
        }
    }
}

