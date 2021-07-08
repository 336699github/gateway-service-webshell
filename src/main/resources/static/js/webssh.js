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

function WSSHClient() {
};

WSSHClient.prototype._generateEndpoint = function () {
    /*
    if (window.location.protocol == 'https:') {
        var protocol = 'wss://';
    } else {
        var protocol = 'ws://';
    }
    var endpoint = protocol + '127.0.0.1:4242/webshellws';
    */
    var endpoint = 'wss://localhost:8443/gateway/knoxsso-sandbox/webshellws'
    //var endpoint = 'wss://localhost:8443/gateway/homepage/webshellws'

    return endpoint;
};

WSSHClient.prototype.connect = function (options) {
    var endpoint = this._generateEndpoint();
    console.log('generated endpoint:' + endpoint);

    if (window.WebSocket) {
        //if has websocket support
        // When new WebSocket(url) is created, it starts connecting immediately
        this._connection = new WebSocket(endpoint);
    }else {
        options.onError('WebSocket Not Supported');
        return;
    }

    this._connection.onopen = function () {
        console.log('websocket opened');
        options.onConnect();
    };

    this._connection.onmessage = function (evt) {
        var data = evt.data.toString();
        //data = base64.decode(data);
        options.onData(data);
    };


    this._connection.onclose = function (evt) {
        options.onClose();
    };
};

WSSHClient.prototype.send = function (data) {
    this._connection.send(JSON.stringify(data));
};

WSSHClient.prototype.sendInitData = function (options) {
    console.log('send initializing data through websocket'+JSON.stringify(options));
    this._connection.send(JSON.stringify(options));
}

WSSHClient.prototype.sendClientData = function (data) {
    this._connection.send(JSON.stringify({"operate": "command", "command": data}))
}

var client = new WSSHClient();
