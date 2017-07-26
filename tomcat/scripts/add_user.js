var sessionId;
var sessionDataObject;

function initForm() {
    if (sessionStorage.length > 0) {
        sessionDataObject = JSON.parse(sessionStorage.sessionId);
        sessionId = sessionDataObject.id;
    }
    ;

    if (sessionId === null) {
        window.location = "index.html";
    }
    ;


    if (sessionId != null) {
        loadLoggedInUser();
    }
    ;

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

function addUserFunction() {
    var userRole = document.getElementById("userRole").value;
    var fullNames = document.getElementById("fullnames").value;
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
//    var cpassword = document.getElementById("cpassword").value;
    var email = document.getElementById("email").value;

    var encodeUserRole = encodeURIComponent(userRole);
    var encodeFullNames = encodeURIComponent(fullNames);
    var encodedUsername = encodeURIComponent(username);
    var encodedPassword = encodeURIComponent(password);
//    var encodedCPassword = encodeURIComponent(cpassword);
    var encodeEmail = '';
    if (encodeURIComponent(email) === '') {
        encodeEmail = 'null';
    } else {
        encodeEmail = encodeURIComponent(email);
    }

    if (encodeUserRole === '' || encodeFullNames === '' || encodedUsername === '' || encodedPassword === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } 
//    else if (encodedPassword !== encodedCPassword) {
//
//        toastr["error"]("Make sure your passwords match! ", "Error!")
//
//        toastr.options = {
//            "debug": false,
//            "newestOnTop": false,
//            "positionClass": "toast-bottom-right",
//            "closeButton": true,
//            "progressBar": true
//        }
//
//    } 
    else
        // /registerUser/{session}/{fullNames}/{username}/{password}/{userRole}/{email}
        var url = '/rest/api/registerUser/'
                + sessionId + '/'
                + encodeFullNames + '/'
                + encodedUsername + '/'
                + encodedPassword + '/'
                + encodeUserRole + '/'
                + encodeEmail;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("User  was added to the database! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }

            window.location = "all_users.html";
        },
        error: function (data, status) {

            toastr["error"]("User was not added to the database! ", "Success!")

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