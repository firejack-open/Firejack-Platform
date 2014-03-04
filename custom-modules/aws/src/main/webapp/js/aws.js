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

$(document).ready(function () {
    var $installForm = $("#installForm");
    var $error = $("#error");

//    $("#installBtn").click(function () {
//        $installForm.submit();
//    });

    $installForm.submit(function (e) {
        $error.hide();
        e.preventDefault();

        $.ajax({
            url: "install",
            method: 'POST',
            dataType: 'json',
            data: JSON.stringify($("#installForm").serializeObject()),
            contentType: 'application/json',
            type: 'application/json',

            success: function (data) {
                var obj = { file: data.keyFileName };
                window.location.href = 'install/downloadKey?' + $.param(obj);

                $installForm.hide();
                $("#installBtn").hide();
                $("#successMsg").show();
            },
            error: function (data, status, er) {
                $error.text(data.responseJSON.error).show();
            }
        });
    });

});

$(document).ajaxStart(function() {
    $("body").addClass("loading");
});
$(document).ajaxStop(function() {
    $("body").removeClass("loading");
});

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};