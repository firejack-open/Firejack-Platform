/*
 * Firejack Platform - Copyright (c) 2012 Firejack Technologies
 *
 * This source code is the product of the Firejack Technologies
 * Core Technologies Team (Benjamin A. Miller, Oleg Marshalenko, and Timur
 * Asanov) and licensed only under valid, executed license agreements
 * between Firejack Technologies and its customers. Modification and / or
 * re-distribution of this source code is allowed only within the terms
 * of an executed license agreement.
 *
 * Any modification of this code voids any and all warranties and indemnifications
 * for the component in question and may interfere with upgrade path. Firejack Technologies
 * encourages you to extend the core framework and / or request modifications. You may
 * also submit and assign contributions to Firejack Technologies for consideration
 * as improvements or inclusions to the platform to restore modification
 * warranties and indemnifications upon official re-distributed in patch or release form.
 */

String.prototype.startsWith = function(str) {
    return (this.match("^"+str)==str)
};
String.prototype.trim = function(){
    return (this.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, ""))
};


//utils functions
SQGetIntValue = function(sourceId) {
    var source =  Ext.getCmp(sourceId);
    try {
        return parseInt(source.getValue());
    } catch (err) {
        return 0;
    }
};
SQCopyValue = function(sourceId, destinationId) {
    var value = SQGetIntValue(sourceId);
    var destination = Ext.getCmp(destinationId);
    destination.setValue(value);
};

SQSetAsRequired = function(compId, defaultValue) {
    var comp =  Ext.getCmp(compId);
    comp.allowBlank = false;
    if (defaultValue) {
        comp.setValue(defaultValue);
    }
};
SQSetAsOptional = function(compId, defaultValue) {
    var comp =  Ext.getCmp(compId);
    comp.allowBlank = true;
    if (defaultValue) {
        comp.setValue(defaultValue);
    }
};

var sqCopyObjectValue = function(sourceId, destinationId) {
    var source =  Ext.getCmp(sourceId);
    var destination =  Ext.getCmp(destinationId);
    try {
        destination.setValue(source.getValue());
    } catch (err) {
        destination.setValue(null);
    }
};
var sqUnmaskForm = function(formCmp) {
    if (OPF.isNotEmpty(formCmp) && OPF.isNotEmpty(formCmp.getEl()) && formCmp.getEl().isMasked()) {
        formCmp.getEl().unmask();
    }
};
var sqUnmaskAndResetForm = function(formCmp) {
    if (OPF.isNotEmpty(formCmp)) {
        sqUnmaskForm(formCmp);
        formCmp.getForm().reset();
    }
};

/////////String utils\\\\\\\\\\\
var sqTrim = function(s) {
    return OPF.isEmpty(s) ? s : s.replace(/^\s*/, "").replace(/\s*$/, "");
};

var cutting = function(s, length) {
    if (OPF.isNotEmpty(s)) {
        if (s.length > length) {
            s = s.substring(0, length - 3) + '...';
        }
    } 
    return s;
};

var reverse = function(array) {
    if (array == null) {
        return;
    }
    var i = 0;
    var j = array.length - 1;
    var tmp;
    while (j > i) {
        tmp = array[j];
        array[j] = array[i];
        array[i] = tmp;
        j--;
        i++;
    }
    return array;
};

var URL_CHECK_REGEXP = new RegExp().compile("^(http|https)://([\\w-]+\\.)+[\\w-]+(:\\d{1,5})?(/[\\w- ./?%&=]*)?$");
function urlCheck(url) {
    return URL_CHECK_REGEXP.test(url);
}

//////////Date utils\\\\\\\\\\\\
var cloneDate = function(sourceDate) {
    if (isEmpty(sourceDate)) {
        return null;
    }
    var date = new Date();
    date.setTime(sourceDate.getTime());
    return date;
};
var sqDateFromHidden = function(hiddenFieldId) {
    if (isEmpty(hiddenFieldId)) {
        return null;
    }
    return sqStrToDate(Ext.getCmp(hiddenFieldId).getValue());
};

var sqStrToDate = function(sDate) {
    var milliseconds = Date.parse(sDate);
    var date = new Date();
    date.setTime(milliseconds);
    return date;
};

var sqMillisecondToDate = function(milliseconds) {
    var date = new Date();
    date.setTime(milliseconds);
    return date;
};

var sqStrToInt = function(sNumber) {
    var number;
    try {
        number = parseInt(sNumber);
    } catch (e) {
        number = 0;
    }
    return number;
};

var sqTranslatedDate = function(date, time) {
    if (isEmpty(date)) {
        return null;
    }
    if (isNotEmpty(time) && Ext.isString(time) && sqTrim(time) != '') {
        var sDate = sqFormattedDate(date) + ' ' + time;
        return sqStrToDate(sDate);
    } else {
        return date;
    }
};

var sqGetTime = function(date) {
    if (isEmpty(date) || !Ext.isDate(date)) {
        return null;
    }
    return sqFormattedDate(date, 'h:i A');
};

var sqFormattedDate = function() {
    if (arguments.length == 0) {
        return (new Date()).format('m/d/Y');
    } else if (arguments.length <= 2) {
        if (Ext.isDate(arguments[0])) {
            var dateFormat = arguments.length == 2 && Ext.isString(arguments[1]) ? arguments[1] : 'm/d/Y';
//            return arguments[0].format(dateFormat);
            return Ext.Date.format(arguments[0], dateFormat);
        }
    }
    return null;
};

var sqFormatDateInMillis = function(dateInMillis, format) {
    if (isEmpty(dateInMillis)) {
        return '';
    }
    return sqFormattedDate(new Date(dateInMillis), format);
};

var sqFormatDateForJerseyParser = function(date) {
    if (!Ext.isDate(date)) { // can pass a string in format 'm/d/Y'
        date = new Date(date);
    }
    return date.format("Y-m-d\\TH:i:s.000O"); // the format that Jersey Java parser will understand (it's a PHP format that ExtJS uses to format the date)
};
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

SqParseTreeEntityId = function(id) {
    var re = new RegExp(/xnode-(\w+)-(\d+)/g);
    return re.exec(id);
};

SqGetIdFromTreeEntityId = function(compositeId) {
    var m = SqParseTreeEntityId(compositeId);
    if (m != null && m.length == 3) {
        return m[2];
    }
    return null;
};

SqGetTypeFromTreeEntityId = function(compositeId) {
    var m = SqParseTreeEntityId(compositeId);
    if (m != null && m.length == 3) {
        return m[1];
    }
    return null;
};

//SqReloadTreeNode = function(tree, nodeId) {
////    var reloadNode = function(nodeId) {
////        var node = tree.getNodeById(nodeId);
////        node.reload();
////        if (node.hasChildNodes()) {
////            node.expand();
////        } else {
////            node.collapse();
////        }
////    };
//
//    var node = tree.getNodeById(nodeId);
//    node.reload();
//    if (node.hasChildNodes()) {
//        node.expand();
//    } else {
//        node.collapse();
//    }
////    var parentNode = node.parentNode;
////    parentNode.reload();
////    tree.on('insert', reloadNode(nodeId));
//};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

var sqSetValue = function(id, value) {
    if (isNotEmpty(id)) {
        var field = Ext.getCmp(id);
        field.setValue(value);
    }
};
var sqValueToNull = function(id) {
    sqSetValue(id, null);
};
var sqSetDisabled = function(cmpId, disabled) {
    if (isNotEmpty(cmpId) && isNotEmpty(disabled)) {
        var cmp;
        if (isNotEmpty(cmp = Ext.getCmp(cmpId))) {
            cmp.setDisabled(disabled);
            return true;
        }
    }
    return false;
};
var sqSetBatchEnabled = function(enabled, cmpIds) {
    if (isNotEmpty(enabled) && Ext.isArray(cmpIds)) {
        var disabled = enabled != true;//ensure that enabled is boolean
        for (i = 0; i < cmpIds.length; i++) {
            sqSetDisabled(cmpIds[i], disabled);
        }
    }
};

var sqIndexOf = function(array, item) {
    for (var i = 0; i < array.length; i++) {
        if (array[i] == item) {
            return i;
        }
    }
    return -1;
};

var sqSetFormFieldVisibility = function(field, visible) {
    if (isNotEmpty(field) && isNotEmpty(visible)) {
        if (visible) {
            field.enable();
        } else {
            field.disable();
        }
        field.show();
        var labelEl = field.getEl();
        if (labelEl) {
            field.getEl().up('.x-form-item').setDisplayed(visible);
        }
    }
};

var sqSetComboValue = function(comboId, comboValue) {
    if (isNotEmpty(comboValue)) {
        var combo = Ext.getCmp(comboId);
        if (isNotEmpty(combo)) {
            combo.store.load();
            combo.setValue(comboValue);
            return true;
        }
    }
    return false;
};

var sqInitializeTabbedFormPanel = function(tabbedPanelId, activeTabIndex) {
    if (isNotEmpty(tabbedPanelId)) {
        var tabbedPanel = Ext.getCmp(tabbedPanelId);
        if (isNotEmpty(tabbedPanel)) {
            var tabItems = tabbedPanel.items;
            if (isNotEmpty(tabItems) && tabItems.length > 0) {
                Ext.each(tabItems, function(tabItem, index){
                    tabbedPanel.setActiveTab(tabItem);
                });
                var activeItemIndex = isEmpty(activeTabIndex) ||
                        activeTabIndex < 0 || activeTabIndex >= tabItems.length ?
                        0 : activeTabIndex;
                var activeTabItem = tabbedPanel.getComponent(activeItemIndex);
                tabbedPanel.setActiveTab(activeTabItem);
            }
        }
    }
};

//var sqSimpleAjaxRequest = function(requestUrl, jsonData) {
//    jsonData = isEmpty(jsonData) ? '[]' : jsonData;
//    Ext.Ajax.request({
//        url: requestUrl,
//        method: 'POST',
//        jsonData: jsonData,
//
//        success:function(response, action) {
//            var msg = Ext.decode(response.responseText).object;
//            Ext.MessageBox.alert('Info', msg);
//        },
//
//        failure:function(resp) {
//            sqShowFailureResponseStatus(resp);
//        }
//    });
//};

var sqAjaxGETRequest = function(requestUrl, successAction, failureAction) {
    Ext.Ajax.request({
        url: requestUrl,
        method: 'GET',

        success:function(response, action) {
            if (isNotEmpty(successAction)) {
                successAction(response);
            }
        },

        failure:function(response) {
            if (isNotEmpty(failureAction)) {
                failureAction(response);
            }
            sqShowFailureResponseStatus(response);
        }
    });
};

var sqAjaxPOSTRequest = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest(requestUrl, jsonData, 'POST', successAction, failureAction)
};

var sqAjaxPOSTRequest2 = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest2(requestUrl, jsonData, 'POST', successAction, failureAction)
};

var sqAjaxPUTRequest = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest(requestUrl, jsonData, 'PUT', successAction, failureAction)    
};

var sqAjaxDELETERequest = function(requestUrl, jsonData, successAction, failureAction) {
    sqAjaxRequest(requestUrl, jsonData, 'DELETE', successAction, failureAction)
};

var sqAjaxRequest = function(requestUrl, jsonData, method, successAction, failureAction) {
    jsonData = isEmpty(jsonData) ? '[]' : jsonData;
    Ext.Ajax.request({
        url: requestUrl,
        method: method,
        jsonData: jsonData,

        success:function(response, action) {
            if (isNotEmpty(successAction)) {
                successAction(response);
            }
        },

        failure:function(response) {
            if (isNotEmpty(failureAction)) {
                failureAction(response);
            }
            sqShowFailureResponseStatus(response);
        }
    });
};

var sqAjaxRequest2 = function(requestUrl, jsonData, method, successAction, failureAction) {
    jsonData = isEmpty(jsonData) ? '[]' : jsonData;
    Ext.Ajax.request({
        url: requestUrl,
        method: method,
        jsonData: jsonData,

        success:function(response, action) {
            if (isNotEmpty(successAction)) {
                successAction(response);
            }
        },

        failure:function(response) {
            if (isNotEmpty(failureAction)) {
                failureAction(response);
            }
            sqShowFailureResponseStatus2(response);
        }
    });
};

var sqShowFailureResponseStatus = function(resp) {
    if (isNotEmpty(resp)) {
        var msg;
        var responseStatus = Ext.decode(resp.responseText);
        if (isNotEmpty(responseStatus)) {
            if (isNotEmpty(responseStatus.errors)) {
                if (Ext.isArray(responseStatus.errors)) {
                    msg = 'Error' + (responseStatus.errors.length == 1 ? '' : 's') + ':\n';
                    for (var i = 0; i < responseStatus.errors.length; i++) {
                        msg += responseStatus.errors[i].msg + '\n';
                    }
                } else {
                    msg = 'Error:\n' + responseStatus.errors.msg;
                }
            } else {
                msg = responseStatus.toString();
            }
        } else {
            msg = 'Error occurred.';
        }
        Ext.MessageBox.alert('Failure', msg);
    }
};

var sqShowFailureResponseStatus2 = function(resp) {
    if (isNotEmpty(resp)) {
        var msg;
        //var responseStatus = Ext.decode(resp.responseText);
        var responseStatusWrapper = Ext.decode(resp.responseText);
        if (isNotEmpty(responseStatusWrapper)) {
            var responseStatus = responseStatusWrapper.responseStatus;
            if (isNotEmpty(responseStatus)) {
                if (isNotEmpty(responseStatus.errors)) {
                    if (Ext.isArray(responseStatus.errors)) {
                        msg = 'Error' + (responseStatus.errors.length == 1 ? '' : 's') + ':\n';
                        for (var i = 0; i < responseStatus.errors.length; i++) {
                            msg += responseStatus.errors[i].msg + '\n';
                        }
                    } else {
                        msg = 'Error:\n' + responseStatus.errors.msg;
                    }
                } else {
                    msg = responseStatus.toString();
                }
            } else {
                msg = 'Error occurred.';
            }
        }
        Ext.MessageBox.alert('Failure', msg);
    }
};

var sqDialogCancelAction = function(editor, customAction) {
    Ext.MessageBox.confirm("Cancel Action", "All changes will be lost, are you sure?",
        function(btn) {
            if ( btn[0] == 'y' ) {
                if (customAction != undefined &&
                        customAction != null) {
                    customAction();
                }
                editor.form.getForm().reset();
                editor.contentManager.showGrid();
            }
        }
    );
};

SQCopyMapCoords = function(latId, lngId, leadId, decayId, mapId) {
    var latValue =  Ext.getCmp(latId);
    if (latValue.getValue() == null) {
        latValue.setValue(SQMapDefault.lat);
    }
    var lngValue =  Ext.getCmp(lngId);
    if (lngValue.getValue() == null) {
        lngValue.setValue(SQMapDefault.lng);
    }
    var leadValue =  Ext.getCmp(leadId);
    var decayValue =  Ext.getCmp(decayId);
    var mapControl =  Ext.getCmp(mapId);
    mapControl.setLatLng(parseFloat(latValue.getValue()), parseFloat(lngValue.getValue()));
};

function capitaliseFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function isNotEmpty(obj) {
    return obj != undefined && obj != null;
}

function isEmpty(obj) {
    return obj == undefined || obj == null;
}

function isBlank(str) {
    return isEmpty(str) || str.length == 0
}

function isNotBlank(str) {
    return isNotEmpty(str) && str.length > 0;
}

function ifBlank(str, defaultValue) {
    if (isNotBlank(str)) {
        return str;
    }
    return defaultValue;
}

function ifEmpty(value, defaultValue) {
    if (isNotEmpty(value)) {
        return value;
    }
    return defaultValue;
}

function getJsonOfStore(store){
    var data = new Array();
    var records = store.getRange();
    for (var i = 0; i < records.length; i++) {
        data.push(records[i].data);
    }
    return data;
}

function shortenTextToDotSeparator(text, maxLen) {
    var subLookup = text;
    if (text.length > maxLen) {
        var names = text.split('.');
        var result = '';
        for (var i = names.length - 1; i > -1 ; i--) {
            var name = names[i];
            var subName = (i == names.length - 1) ? name : name + '.' + result;
            if (subName.length < maxLen) {
                result = subName;
            } else {
                break;
            }
        }
        if (result != text) {
            subLookup = '...' + result;
        }
    }
    return subLookup;
}

//Deprecated
function calculateLookup(path, name) {
    var path = ifBlank(path, '');
    var name = ifBlank(name, '');
    var normalizeName = name.replace(/[\s!"#$%&'()*+,-./:;<=>?@\[\\\]^_`{|}~]+/g, '-');
    var lookup = (isNotBlank(path) ? path + '.' : '') + normalizeName;
    return lookup.toLowerCase();
}