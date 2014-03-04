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

Ext.data.Connection.securedCallback = function(options, success, response) {
    if (response.status == 403 || response.status == 406) {
        var message = response.status == 403 ?
            'Guest user tries to perform secured action</br>' +
                'or Your session has timed out.</br>' +
                ' Please, login to the system.' :
            'You are no longer logged in to the system.</br>' +
                'Please, provide your username and password to resume work.';
        Ext.MessageBox.show({
            title:'Warning',
            msg: message,
            closable: false,
            buttons: Ext.MessageBox.OK,
            fn: function() {
                document.location = OPF.Cfg.OPF_CONSOLE_URL + '/console/logout';
            },
            animEl: 'elId',
            icon: Ext.MessageBox.WARNING
        });
    } else if (response.status == 401) {
        Ext.Msg.show({
            title:'Warning',
            msg: 'Not enough permissions.',
            closable: false,
            buttons: Ext.Msg.OK,
            fn: function() {
            },
            animEl: 'elId',
            icon: Ext.MessageBox.WARNING
        });
    } else {
        if (OPF.isNotEmpty(options.clientCallback)) {
            Ext.callback(options.clientCallback, this, arguments);
        }
    }
};

Ext.override(Ext.data.Connection, {

    request: function(options) {
        options = options || {};

        ////////////////The only added lines////////////////
        if (OPF.isNotEmpty(options.callback)) {
            options.clientCallback = options.callback;
        }
        options.callback = Ext.data.Connection.securedCallback;
        ////////////////////////////////////////////////////

        var me = this,
            scope = options.scope || window,
            username = options.username || me.username,
            password = options.password || me.password || '',
            async,
            requestOptions,
            request,
            headers,
            xhr;

        if (me.fireEvent('beforerequest', me, options) !== false) {

            requestOptions = me.setOptions(options, scope);

            if (this.isFormUpload(options) === true) {
                this.upload(options.form, requestOptions.url, requestOptions.data, options);
                return null;
            }

            // if autoabort is set, cancel the current transactions
            if (options.autoAbort === true || me.autoAbort) {
                me.abort();
            }

            // create a connection object

            if ((options.cors === true || me.cors === true) && Ext.isIE && Ext.ieVersion >= 8) {
                xhr = new XDomainRequest();
            } else {
                xhr = this.getXhrInstance();
            }

            async = options.async !== false ? (options.async || me.async) : false;

            // open the request
            if (username) {
                xhr.open(requestOptions.method, requestOptions.url, async, username, password);
            } else {
                xhr.open(requestOptions.method, requestOptions.url, async);
            }

            if (options.withCredentials === true || me.withCredentials === true) {
                xhr.withCredentials = true;
            }

            headers = me.setupHeaders(xhr, options, requestOptions.data, requestOptions.params);

            // create the transaction object
            request = {
                id: ++Ext.data.Connection.requestId,
                xhr: xhr,
                headers: headers,
                options: options,
                async: async,
                timeout: setTimeout(function() {
                    request.timedout = true;
                    me.abort(request);
                }, options.timeout || me.timeout)
            };
            me.requests[request.id] = request;
            me.latestId = request.id;
            // bind our statechange listener
            if (async) {
                xhr.onreadystatechange = Ext.Function.bind(me.onStateChange, me, [request]);
            }

            if ((options.cors === true || me.cors === true) && Ext.isIE && Ext.ieVersion >= 8) {
                xhr.onload = function() {
                    me.onComplete(request);
                }
            }

            // start the request!
            xhr.send(requestOptions.data);
            if (!async) {
                return this.onComplete(request);
            }
            return request;
        } else {
            Ext.callback(options.callback, options.scope, [options, undefined, undefined]);
            return null;
        }
    }

});