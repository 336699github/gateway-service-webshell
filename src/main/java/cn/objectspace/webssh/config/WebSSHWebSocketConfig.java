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
package cn.objectspace.webssh.config;

import cn.objectspace.webssh.interceptor.WebSocketInterceptor;
import cn.objectspace.webssh.websocket.WebSSHWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSSHWebSocketConfig implements WebSocketConfigurer{
    @Autowired
    WebSSHWebSocketHandler webSSHWebSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        // register WebSocketHandler for websocket connection endpoint ending with /webshellws
        webSocketHandlerRegistry.addHandler(webSSHWebSocketHandler, "/webshellws")
                .addInterceptors(new WebSocketInterceptor())
                //allow websocket connection request to come from all ip addresses
                .setAllowedOrigins("*");
    }
}
