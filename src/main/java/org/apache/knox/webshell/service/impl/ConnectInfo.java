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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;

/**
* data structure to store a connection session
*/
public class ConnectInfo {
    private InputStream in;
    private OutputStream out;
    private Process process;
    private Logger logger;

    public ConnectInfo (){
        logger = LoggerFactory.getLogger(ConnectInfo.class);
    }

    public void connect() throws Exception{
        ProcessBuilder builder = new ProcessBuilder("bash", "-i");
        builder.redirectErrorStream(true); // so we can ignore the error stream
        process = builder.start();
        InputStream in = process.getInputStream();
        OutputStream out = process.getOutputStream();
    }

    public InputStream getInputStream()  {
        return this.in;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    public void disconnect(){
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (process != null) {
                process.destroy();
                logger.info("process exited with value {}", process.exitValue());
            }
        } catch (Exception e) {
            logger.error("error disconnecting connectInfo");
        }
    }
}
