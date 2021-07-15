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
package org.apache.knox.webshell.service;

import org.springframework.web.socket.WebSocketSession;

public interface WebShellService {
    /**
     * handle data received from client
     */
    void initConnection(WebSocketSession session);

    /**
     * handle data received from client
     */
    void recvHandle(String buffer, WebSocketSession session) throws Exception;

    /**
     * helper method for recvHandle, send data to frontend through websocket
     */
    void sendMessageToClient(WebSocketSession session, byte[] buffer) throws Exception;

    void closeConnection(WebSocketSession session) throws Exception;
}
