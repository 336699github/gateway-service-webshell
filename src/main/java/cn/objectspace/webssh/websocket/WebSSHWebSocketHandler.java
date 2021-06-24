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
package cn.objectspace.webssh.websocket;

import cn.objectspace.webssh.constant.ConstantPool;
import cn.objectspace.webssh.service.WebSSHService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class WebSSHWebSocketHandler implements WebSocketHandler{
    @Autowired
    private WebSSHService webSSHService;
    private Logger logger = LoggerFactory.getLogger(WebSSHWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        logger.info("User:{},connected to web shell server", webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY));
        webSSHService.initConnection(webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage) {
            logger.info("User:{},send message:{}", webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY), webSocketMessage.toString());
            webSSHService.recvHandle(((TextMessage) webSocketMessage).getPayload(), webSocketSession);
        } else if (webSocketMessage instanceof BinaryMessage) {

        } else if (webSocketMessage instanceof PongMessage) {

        } else {
            logger.error("Unexpected WebSocket message type: {}",webSocketMessage);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.error(" WebSocket message transport error");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("User:{} terminated web shell connection", String.valueOf(webSocketSession.getAttributes().get(ConstantPool.USER_UUID_KEY)));
        webSSHService.close(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
