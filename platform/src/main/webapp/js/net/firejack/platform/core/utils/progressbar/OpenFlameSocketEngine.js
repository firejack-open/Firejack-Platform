/**
 * Created by IntelliJ IDEA.
 * User: mjr
 * Date: 6/10/11
 * Time: 1:26 PM
 */

var ajaxSuccessFunction;
var ajaxFailureFunction;
var isInterrupted = false;

var AJAX_STRATEGY = 'ajax';
var FLASH_STRATEGY = 'flash';

var PROGRESS_BAR_STRATEGY = AJAX_STRATEGY;

function consoleLog(message) {
    if ((Ext.isGecko || Ext.isChrome) && OPF.Cfg.DEBUG_MODE) {
        console.log(message);
    }
}

function onConnected() {
    PROGRESS_BAR_STRATEGY = FLASH_STRATEGY;
    consoleLog('onConnected ' + new Date());
}

function onDisconnected() {
    PROGRESS_BAR_STRATEGY = AJAX_STRATEGY;
    consoleLog('onDisconnected ' + new Date());
}

function onError(error) {
    consoleLog('onError: ' + error);
}

function onMessage(data) {
    var responseData = eval('(' + data + ')');
    consoleLog('onMessage: ' + data);
    performProgressBar(responseData);
}

function loadProgress(isBaseUrl, forceLoadProgress) {
    if (!isInterrupted || forceLoadProgress) {
        isInterrupted = false;
        Ext.Ajax.request({
            url: OPF.Cfg.restUrl('progress/status', isBaseUrl),
            method: 'GET',

            success: function(response, action) {
                consoleLog('Load Progress success: ' + response.responseText);

                var responseData = Ext.decode(response.responseText);
                var logs = responseData.data;

                var isNext = !isInterrupted;
                Ext.each(logs, function(log) {
                    isNext &= performProgressBar(log);
                });

                if (isNext) {
                    new Ext.util.DelayedTask( function() {
                        loadProgress(isBaseUrl, false)
                    }).delay(500);
                } else {
                    consoleLog('Close progress bar');

                    var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
                    progressBar.close();
                }
            },

            failure: function(response) {
                consoleLog('Load Progress failure: ' + response);
                OPF.Msg.setAlert(false, response.message);

                var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
                progressBar.close();

                ajaxFailureFunction(response);
            }
        });
    } else {
        isInterrupted = false;
    }
}

function interruptProgress() {
    isInterrupted = true;
}

function performProgressBar(responseData) {
    var result;
    var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
    if (responseData.data && responseData.data[0].dtoName == 'net.firejack.platform.web.mina.bean.Status') {
        if (responseData.data[0].type == 'ERROR') {
            progressBar.showErrorMessage(responseData.message);
            ajaxFailureFunction({
                message: responseData.message,
                success: false
            });
            result = false;
        } else {
            if (responseData.data[0].type == 'STARTED') {
                progressBar.setLogVisible(responseData.data[0].showLogs);
            }
            progressBar.showStatus(responseData.data[0].percent, responseData.data[0].title, responseData.data[0].logLevel);
            result = true;
        }
    } else {
        var response = {
            responseText: '(' + Ext.JSON.encode(responseData) + ')'
        };

        consoleLog('Execute ajax function: ' + responseData.success);

        if (responseData.success) {
            progressBar.showCompletedStatus(responseData.message, null);
            ajaxSuccessFunction(response);
        } else {
            progressBar.showErrorMessage(responseData.message);
            ajaxFailureFunction(response);
        }
        result = false;
    }
    return result;
}

Ext.override(Ext.form.Basic, {

    doAction: function(action, options) {
        if (!(/Page-UID=/g.test(this.url))) {
            this.url += /\?/g.test(this.url) ? '&' : '?';
            this.url += 'Page-UID=' + OPF.Cfg.PAGE_UID;
        }
        //below is code from original class
        if (Ext.isString(action)) {
            action = Ext.ClassManager.instantiateByAlias('formaction.' + action, Ext.apply({}, options, {form: this}));
        }
        if (this.fireEvent('beforeaction', this, action) !== false) {
            action.options = options;//this line is only customization
            this.beforeAction(action);
            Ext.defer(action.run, 100, action);
        }
        return this;
    },

    beforeAction : function(action){
        // Call HtmlEditor's syncValue before actions
        if (OPF.isNotEmpty(this.items)) {
            for (var i = 0; i < this.items.length; i++) {
                for (var j = 0; j < this.items[i].length; j++) {
                    var f = this.items[i][j];
                    if (f.isFormField && f.syncValue) {
                        f.syncValue();
                    }
                }
            }
        }
        var options = action.options;
        if (options) {
            if(options.waitMsg){
                if(this.waitMsgTarget === true){
                    this.el.mask(options.waitMsg, 'x-mask-loading');
                }else if(this.waitMsgTarget){
                    this.waitMsgTarget = Ext.get(this.waitMsgTarget);
                    this.waitMsgTarget.mask(options.waitMsg, 'x-mask-loading');
                }else{
                    Ext.MessageBox.wait(options.waitMsg, options.waitTitle || this.waitTitle);
                }
            }

            if(options.success) {
                ajaxSuccessFunction = function(response) {
                    var action = {
                        response: response
                    };
                    options.success(this, action);
                };
            }
            if(options.failure) {
                ajaxFailureFunction = function(response) {
                    var action = {
                        response: response
                    };
                    options.failure(this, action);
                };
            }
            if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                var isBaseUrl = action.form.url.startsWith(OPF.Cfg.BASE_URL);
                var isOPFUrl = action.form.url.startsWith(OPF.Cfg.OPF_CONSOLE_URL);
                loadProgress(isBaseUrl && !isOPFUrl, true);
            }
        }
    },

    afterAction : function(action, success){
        this.activeAction = null;
        var options = action.options;
        if(options.waitMsg){
            if(this.waitMsgTarget === true){
                this.el.unmask();
            }else if(this.waitMsgTarget){
                this.waitMsgTarget.unmask();
            }else{
                Ext.MessageBox.updateProgress(1);
                Ext.MessageBox.hide();
            }
        }

        var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();

        var responseData = Ext.decode(action.response.responseText);
        if (responseData.data && responseData.data[0].dtoName == 'net.firejack.platform.web.mina.bean.Status') {
            if (responseData.data[0].type == 'ERROR') {
                if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                    interruptProgress();
                }
                progressBar.showErrorMessage(responseData.message);
            } else {
                if (responseData.data[0].type == 'STARTED') {
                    progressBar.setLogVisible(responseData.data[0].showLogs);
                }
                progressBar.showStatus(responseData.data[0].percent, responseData.data[0].title, responseData.data[0].logLevel);
            }
        } else {
            if (success) {
                if (options.reset) {
                    this.reset();
                }
                progressBar.showCompletedStatus(responseData.message, 'UPLOAD');
                Ext.callback(options.success, options.scope, [this, action]);
                this.fireEvent('actioncomplete', this, action);
            } else {
                progressBar.showErrorMessage(responseData.message);
                Ext.callback(options.failure, options.scope, [this, action]);
                this.fireEvent('actionfailed', this, action);
            }
            if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                interruptProgress();
            }
        }
    }
});

Ext.override(Ext.data.Connection, {

    onComplete : function(request) {
        var me = this,
            options = request.options,
            result,
            success,
            response;

        try {
            result = me.parseStatus(request.xhr.status);
        } catch (e) {
            // in some browsers we can't access the status if the readyState is not 4, so the request has failed
            result = {
                success : false,
                isException : false
            };
        }
        success = result.success;

        if (success) {
            response = me.createResponse(request);

            me.fireEvent('requestcomplete', me, response, options);

            var responseData = eval('(' + OPF.ifBlank(response.responseText, '{}') + ')');
            var progressData;
            if (OPF.isNotEmpty(responseData.data)) {
                progressData = responseData.data[0];
            }
            if (OPF.isNotEmpty(progressData) && progressData.dtoName == 'net.firejack.platform.web.mina.bean.Status') {
                var progressBar = OPF.core.utils.progressbar.ProgressBarDialog.get();
                if (progressData.type == 'ERROR') {
                    progressBar.showErrorMessage(responseData.message);
                } else {
                    if (responseData.data[0].type == 'STARTED') {
                        progressBar.setLogVisible(responseData.data[0].showLogs);
                    }
                    progressBar.showStatus(progressData.percent, progressData.title, progressData.logLevel);
                    if (PROGRESS_BAR_STRATEGY == AJAX_STRATEGY) {
                        var isBaseUrl = options.url.startsWith(OPF.Cfg.BASE_URL);
                        var isOPFUrl = options.url.startsWith(OPF.Cfg.OPF_CONSOLE_URL);
                        loadProgress(isBaseUrl && !isOPFUrl, true);
                    }
                }
                if (options.success) {
                    ajaxSuccessFunction = options.success;
                }
                if (options.failure) {
                    ajaxFailureFunction = options.failure;
                }
            } else {
                Ext.callback(options.success, options.scope, [response, options]);
            }
        } else {
            if (result.isException || request.aborted || request.timedout) {
                response = me.createException(request);
            } else {
                response = me.createResponse(request);
            }
            me.fireEvent('requestexception', me, response, options);
            Ext.callback(options.failure, options.scope, [response, options]);
        }
        Ext.callback(options.callback, options.scope, [options, success, response]);
        delete me.requests[request.id];
        return response;
    }

});