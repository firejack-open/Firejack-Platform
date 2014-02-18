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