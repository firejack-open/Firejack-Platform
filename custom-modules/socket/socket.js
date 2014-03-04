/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Created by IntelliJ IDEA.
 * User: mjr
 * Date: 6/10/11
 * Time: 1:26 PM
 */

function getFlexApp(appName) {
    if (navigator.appName.indexOf("Microsoft") != -1) {
        return window[appName];
    } else {
        return document[appName];
    }
}

function onConnected() {
    var token = readCookie('opf.authentication.token');
    if (token) {
        getFlexApp('swfsocket').send('{"token":"' + token + '"}');
    }
}

function onDisconnected() {
}

function onMessage(data) {
    var response = eval('(' + data + ')');
    var progressBar = FJK.openflame.clds.SocketProgressBarDialog.init();
    var status = response.status;
    if (status != 'ERROR') {
        var vo = response.data;
        progressBar.showStatus(vo.percent, vo.title);
        if (status == 'COMPLETED') {
            response.responseText = data;
            ajaxSuccessFunction(response);
        }
    } else {
        progressBar.showErrorMessage(response.message);
        response.responseText = data
        ajaxFailureFunction(response);
    }
}