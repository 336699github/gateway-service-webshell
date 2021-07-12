<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  <!doctype html>
-->
<html>
<head>
    <title>WebSSH</title>
    <link rel="stylesheet" href="../css/xterm.css" />
</head>
<body>
<div id="terminal" style="width: 100%;height: 100%"></div>

<script src="../js/jquery-3.4.1.min.js"></script>
<script src="../js/xterm.js" charset="utf-8"></script>
<script src="../js/webssh.js" charset="utf-8"></script>
<script>
    openTerminal( {
        operate:'connect',
        host: 'sandbox.hortonworks.com',
        port: '2222',
        knoxUsername: 'knox_test',
        knoxPassword: 'knox_test',
        username: '${username}'
    });
    function openTerminal(options){
        var client = new WSSHClient();
        //console.log('created new client');
        var term = new Terminal({
            cols: 97,
            rows: 37,
            cursorBlink: true,
            cursorStyle: "block", //null | 'block' | 'underline' | 'bar'
            scrollback: 800,
            tabStopWidth: 8,
            screenKeys: true
        });

        term.on('data', function (data) {
            //callback when input via keyboard
            client.sendClientData(data);
        });
        term.open(document.getElementById('terminal'));
        term.write('Connecting ' + '${username}...\r\n');
        client.connect({
            onError: function (error) {
                term.write('Error: ' + error + '\r\n');
            },
            onOpen: function () {
                console.log('connected to websocket');
                client.sendInitData(options);
            },
            onClose: function () {
                term.write("\rconnection closed");
            },
            onData: function (data) {
                //call back when received input
                term.write(data);
            }
        });
    }
</script>
</body>
</html>