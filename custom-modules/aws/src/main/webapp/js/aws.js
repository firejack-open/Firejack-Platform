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