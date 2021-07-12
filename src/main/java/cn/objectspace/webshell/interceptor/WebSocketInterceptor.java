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
package cn.objectspace.webshell.interceptor;

import cn.objectspace.webshell.constant.ConstantPool;
import cn.objectspace.webshell.websocket.WebShellWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

public class WebSocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        Logger logger = LoggerFactory.getLogger(WebShellWebSocketHandler.class);
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            // ServletServerHttpRequest request = (ServletServerHttpRequest) serverHttpRequest;
            // generate a uuid for each websocket connection
            String uuid = UUID.randomUUID().toString().replace("-","");
            //put uuid in websocket session, map can be retrieved in WebSocketHandler via session.getAttributes()
            map.put(ConstantPool.USER_UUID_KEY, uuid);
            // logger.info("User:{}, at before handshake interceptor", uuid);
            return true;
        } else {
            return false;
        }

        //return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
