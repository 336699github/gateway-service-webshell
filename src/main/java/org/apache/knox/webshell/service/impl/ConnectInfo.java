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

import org.apache.knox.webshell.pojo.WebShellData;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.springframework.web.socket.WebSocketSession;

/**
* data structure to store a connection session
*/
public class ConnectInfo {
    //private String Id;
    //private WebSocketSession webSocketSession;
    private final JSch jsch;
    private Channel channel;
    private Session jschSession;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Logger logger;

    public ConnectInfo (WebSocketSession webSocketSession){
        jsch = new JSch();
        //this.Id = webSocketSession.getId();
        //this.webSocketSession = webSocketSession;
        logger = LoggerFactory.getLogger(ConnectInfo.class);
    }

    public void connect(WebShellData webShellData) throws Exception{
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            jschSession = jsch.getSession(webShellData.getKnoxUsername(), webShellData.getHost(), webShellData.getPort());
            jschSession.setConfig(config);
            jschSession.setPassword(webShellData.getKnoxPassword());
            jschSession.connect(30000);
            channel = jschSession.openChannel("shell");
            inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            channel.connect(3000);
            logger.info("successfully connected to target host using jsch");
    }
/*
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public void setWebSocketSession(WebSocketSession webSocketSession) {
        this.webSocketSession = webSocketSession;
    }

 */

    public InputStream getInputStream()  {
        return this.inputStream;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    public void disconnect(){
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (channel != null) {
                channel.disconnect();
                jschSession.disconnect();
            }
            //if (webSocketSession != null) webSocketSession.close();
        } catch (Exception e) {
            logger.error("error disconnecting connectInfo");
        }
    }
}
