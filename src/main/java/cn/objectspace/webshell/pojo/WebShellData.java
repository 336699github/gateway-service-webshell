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
package cn.objectspace.webshell.pojo;

/**
* data structure to store request coming from client (connect or command)
*/
public class WebShellData {
    //either connect or command
    private String operation;
    private String host;
    //default ssh port to be used to connect to target host
    private Integer port = 22;
    private String knoxUsername;
    private String knoxPassword;
    private String username;
    private String command = "";

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getKnoxUsername() {
        return knoxUsername;
    }

    public void setKnoxUsername(String knoxUsername) {
        this.knoxUsername = knoxUsername;
    }

    public String getKnoxPassword() {
        return knoxPassword;
    }

    public void setKnoxPassword(String knoxPassword) {
        this.knoxPassword = knoxPassword;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
