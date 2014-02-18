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