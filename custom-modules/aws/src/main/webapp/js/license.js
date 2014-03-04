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
    var $licenseForm = $("#licenseForm");
    var $error = $("#error");
    $( "#datepicker" ).datepicker();

//    $("#installBtn").click(function () {
//        $error.hide();
//        $licenseForm.submit();
//    });


    $licenseForm.submit(function (e) {
        e.preventDefault();
        $error.hide();
        var $datepicker = $("#datepicker");
        var $date = $datepicker.val();
        var $milliseconds  = Date.parse($date);
        $datepicker.val($milliseconds);

        $.ajax({
            url: "license/download",
            method: 'POST',
            dataType: 'json',
            data: JSON.stringify($("#licenseForm").serializeObject()),
            contentType: 'application/json',
            type: 'application/json',

            success: function (data) {
                if (data.keyFileName) {
                    var obj = { file: data.keyFileName };
                    window.location.href = 'install/downloadKey?' + $.param(obj);

                    $licenseForm.hide();
                    $("#installBtn").hide();
                    $("#successMsg").html(data.status);
                    $("#successMsg").show();
                } else {
                    $error.html(data.status);
                    $error.show();
                }
                $datepicker.val($date);
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