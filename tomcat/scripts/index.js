
function initLogin() {

}

function setCookie(cname, cvalue) {
    sessionStorage.setItem(cname, cvalue);
}
function getCookie(cname) {
    return sessionStorage.getItem(cname);
}

function getQueryParameter(parameterName) {
    var queryString = window.top.location.search.substring(1);
    var parameterName = parameterName + "=";
    if (queryString.length > 0) {
        begin = queryString.indexOf(parameterName);
        if (begin != -1) {
            begin += parameterName.length;
            end = queryString.indexOf("&", begin);
            if (end == -1) {
                end = queryString.length
            }
            return unescape(queryString.substring(begin, end));
        }
    }
    return null;
}


function sendLogin() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;

    if (username=='' || password=='') {
        toastr["error"]("Make sure you have provided both the username & password! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        } 
    }else{
        $.ajax({
            type: "POST",
            url: "/rest/api/login/" + username + '/' + password,
            data: param = "",
            dataType: 'json',
            success: function (data, status) {

                setCookie("sessionId", data);
                sessionStorage.setItem("sessionId", JSON.stringify(data));
                var mog = JSON.parse(sessionStorage.sessionId);
                console.log(status);
                console.log(mog.id);
                console.log(data);
                
                toastr["success"]("Login successful. Please wait... ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                } 

                if (data.user.userRole == "ADMINISTRATOR") {

                    window.location = "all_branches.html";
                };
             
            },
            error: function () {
                
                toastr["error"]("Please make sure your password & username are correct! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                } 

            }
        });

    }
   
}