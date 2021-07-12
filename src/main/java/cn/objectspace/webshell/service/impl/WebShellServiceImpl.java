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
package cn.objectspace.webshell.service.impl;

import cn.objectspace.webshell.constant.ConstantPool;
import cn.objectspace.webshell.pojo.WebShellData;
import cn.objectspace.webshell.service.WebShellService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class WebShellServiceImpl implements WebShellService {
    //map to store ssh connection information for each uuid
    private static final Map<String, Object> connectMap = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(WebShellServiceImpl.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void initConnection(WebSocketSession webSocketSession) {
        // map a webSocketSession to a connectInfo
        ConnectInfo connectInfo = new ConnectInfo();
        connectMap.put(webSocketSession.getId(), connectInfo);
    }

    @Override
    public void recvHandle(String buffer, WebSocketSession webSocketSession) {
        ObjectMapper objectMapper = new ObjectMapper();
        WebShellData webShellData = null;
        try {
            // convert received json string to WebShellData (used for ssh into host machine)
            webShellData = objectMapper.readValue(buffer, WebShellData.class);
            logger.info("successfully parsed json data received from client");
        } catch (IOException e) {
            logger.error("Json conversion error:{}", e.getMessage());
            return;
        }
        if (ConstantPool.OPERATION_CONNECT.equals(webShellData.getOperation())) {
            logger.info("received connection request,now ssh into target host");
            ConnectInfo connectInfo = (ConnectInfo) connectMap.get(webSocketSession.getId());
            // start asynchronous execution
            final WebShellData finalWebSSHData = webShellData;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectToHost(connectInfo, finalWebSSHData, webSocketSession);
                    } catch (JSchException | IOException | SUDOException e  ) {
                        logger.error("Error connecting into target host:{}", e.getMessage());
                        close(webSocketSession);
                    }
                }
            });
        } else if (ConstantPool.OPERATION_COMMAND.equals(webShellData.getOperation())) {
            String command = webShellData.getCommand();
            ConnectInfo connectInfo = (ConnectInfo) connectMap.get(webSocketSession.getId());
            if (connectInfo != null) {
                try {
                    transToHost(connectInfo.getOutputStream(), command);
                } catch (IOException e) {
                    logger.error("Error connecting with target host:{}", e.getMessage());
                }
            }
        } else {
            logger.error("Operation not supported");
            close(webSocketSession);
        }
    }

    //send raw output back to client (to be parsed and displayed by xterm.js)
    @Override
    public void sendMessageToClient(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    @Override
    public void close(WebSocketSession session) {
        String userId = String.valueOf(session.getAttributes().get(ConstantPool.USER_UUID_KEY));
        ConnectInfo sshConnectInfo = (ConnectInfo) connectMap.get(userId);
        if (sshConnectInfo != null) {
            if (sshConnectInfo.getChannel() != null) sshConnectInfo.getChannel().disconnect();
            connectMap.remove(userId);
        }
    }

    /**
     * connect to target host
     */
    private void connectToHost(ConnectInfo connectInfo, WebShellData webShellData, WebSocketSession webSocketSession) throws JSchException, IOException {
        connectInfo.connect(webShellData);

        try {
            InputStream inputStream = connectInfo.getInputStream();
            switchToUserShell(inputStream, webShellData);
            byte[] buffer = new byte[1024];
            int i = 0;
            //thread blocks until data comes in
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessageToClient(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {
            connectInfo.disconnect();
        }

    }

    private void switchToUserShell(InputStream inputStream, WebShellData webShellData) throws SUDOException{
        inputStream

    }
    /**
     * transmit command to target host
     */
    private void transToHost(OutputStream outputStream, String command) throws IOException {
            outputStream.write(command.getBytes());
            outputStream.flush();
    }
}
