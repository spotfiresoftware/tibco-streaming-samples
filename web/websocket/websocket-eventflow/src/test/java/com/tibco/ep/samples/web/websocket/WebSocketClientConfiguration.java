/* *****************************************************************************
 Copyright (c) 2020-2023 Cloud Software Group, Inc.

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

import jakarta.websocket.ClientEndpointConfig;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * WebSocket client configurator, using the name of system user as the username to pass authentication check of the default-realm,
 * no password needed
 */
public class WebSocketClientConfiguration extends ClientEndpointConfig.Configurator {

    @Override
    public void beforeRequest(Map<String, List<String>> headers) {

        final String username = System.getProperty("user.name");
        final String AUTHENTICATION_TOKEN_FOR_NODE_A = "Basic " + Base64.getEncoder().encodeToString((username + ":").getBytes());
        headers.put(HttpHeaders.AUTHORIZATION, Collections.singletonList(AUTHENTICATION_TOKEN_FOR_NODE_A));
    }
}
