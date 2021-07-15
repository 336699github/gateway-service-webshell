/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.knox.webshell.websocket;

import org.apache.knox.webshell.service.WebShellService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class WebShellWebSocketHandler implements WebSocketHandler{
    @Autowired
    private WebShellService webShellService;
    private Logger logger = LoggerFactory.getLogger(WebShellWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info("WebSocket session:{},connected to web shell server", webSocketSession.getId());
        webShellService.initConnection(webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        try {
            if (webSocketMessage instanceof TextMessage) {
                webShellService.recvHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
            } else {
                throw new UnsupportedMsgTypeException("Unsupported WebSocket message type");
            }
        } catch (Exception e) {
            // display error message to browser terminal, disconnect and release resources
            webShellService.sendMessageToClient(webSocketSession, e.getMessage().getBytes());
            webShellService.closeConnection(webSocketSession);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable e) throws Exception {
        logger.error(" WebSocket message transport error:{}",e.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        webShellService.closeConnection(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
