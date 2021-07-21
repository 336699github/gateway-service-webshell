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
package org.apache.knox.webshell.service.impl;

import org.apache.knox.webshell.constant.ConstantPool;
import org.apache.knox.webshell.pojo.WebShellData;
import org.apache.knox.webshell.service.WebShellService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebShellServiceImpl implements WebShellService {
    private static final Map<String, Object> connectMap = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(WebShellServiceImpl.class);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void initConnection(WebSocketSession webSocketSession){
        // map a webSocketSession to a connectInfo instance
        ConnectInfo connectInfo = new ConnectInfo();
        connectMap.put(webSocketSession.getId(), connectInfo);

    }

    @Override
    public void recvHandle(String buffer, WebSocketSession webSocketSession) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        WebShellData webShellData;
        // convert received json string to WebShellData
        webShellData = objectMapper.readValue(buffer, WebShellData.class);
        //logger.info("successfully parsed json data received from client");

        if (ConstantPool.OPERATION_CONNECT.equals(webShellData.getOperation())) {
            logger.info("received connection request, connecting into target host ...");
            // start asynchronous execution
            final WebShellData finalWebShellData = webShellData;
            executorService.execute(new Runnable() {
                @Override
                public void run(){
                    connectToHost(finalWebShellData, webSocketSession);
                }
            });
        } else if (ConstantPool.OPERATION_COMMAND.equals(webShellData.getOperation())) {
            ConnectInfo connectInfo = (ConnectInfo) connectMap.get(webSocketSession.getId());
            transToHost(connectInfo.getOutputStream(), webShellData.getCommand());
        } else {
            throw new UnsupportedOpException(String.format("Operation %s not supported",webShellData.getOperation()));
        }
    }

    //send raw output back to client (to be parsed and displayed by xterm.js)
    @Override
    public void sendMessageToClient(WebSocketSession session, byte[] buffer) {
        try {
            session.sendMessage(new TextMessage(buffer));
        } catch (Exception e) {
            logger.error("error sending websocket message to client");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection(WebSocketSession webSocketSession){
        if (!connectMap.containsKey(webSocketSession.getId())) return;
        try{
            ConnectInfo connectInfo = (ConnectInfo) connectMap.get(webSocketSession.getId());
            connectInfo.disconnect();
            connectMap.remove(webSocketSession.getId());
            webSocketSession.close();
        }catch(Exception e){
            logger.error("Error closing connection", e);
        }
    }

    /**
     * transmit command to target host
     */
    private void transToHost (OutputStream outputStream, String command) throws Exception{
        outputStream.write(command.getBytes());
        outputStream.flush();
    }

    /**
     * connect to target host, to be run asynchronously
     */
    private void connectToHost(WebShellData webShellData, WebSocketSession webSocketSession){
        try {
            ConnectInfo connectInfo = (ConnectInfo) connectMap.get(webSocketSession.getId());
            assert (connectInfo != null);
            connectInfo.connect();
            InputStream in = connectInfo.getInputStream();
            OutputStream out = connectInfo.getOutputStream();
            switchToUserShell(in,out,webShellData);
            byte[] buffer = new byte[1024];
            int i;
            //thread blocks until data comes in
            while ((i = in.read(buffer)) != -1) {
                sendMessageToClient(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }
        } catch (Exception e){
            sendMessageToClient(webSocketSession, e.getMessage().getBytes());
            closeConnection(webSocketSession);
        }
    }

    private boolean switchSuccessful(InputStream in, WebShellData webShellData) throws Exception{
        InputStreamReader reader = new InputStreamReader( in );
        BufferedReader bufferedReader = new BufferedReader( reader );
        /*
        exec sudo -u {username} bash
        [{username}@{hostname} knox]$ exec sudo -u {username} bash
        cd $HOME
         */
        for (int i=0; i<3; i++) {
            logger.info(bufferedReader.readLine());
        }
        String line = bufferedReader.readLine();
        String targetRegex = String.format("\\[%s@[\\w-]+\\s%s\\]\\$ cd \\$HOME",webShellData.getUsername(), "knox");
        return line.matches(targetRegex);
    }

    // switch from knox user shell to target user's shell
    private void switchToUserShell(InputStream in, OutputStream out, WebShellData webShellData) throws Exception {
        logger.info("start user switching... ");
        transToHost(out, String.format("exec sudo -u %s bash\ncd $HOME\n", webShellData.getUsername()));
        if (!switchSuccessful(in, webShellData)){
            throw new UnknownUserException("Unknown user!");
        };
    }
}
