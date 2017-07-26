
var UserRoleLoggedIn;

function loadLoggedInUser() {
    var url = '/rest/api/getLoggedOnUser/' + sessionId;
    console.log(sessionId);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        contentType: "application/json",
        success: function (data, status) {

            $('#fullNamesHolderId').html(data.fullnames);
            $('#usernameHolderId').html(data.userRole);
            
            var html = '';
            html += '<img src="images/img.jpg" alt="">';
            html += '                    ' + data.fullnames; 
            html += ' <span class=" fa fa-angle-down"></span>';
            
            $('#namesHolderId').html(html);

            UserRoleLoggedIn = data.userRole;

            sessionStorage.setItem("userRole", data);
            sessionStorage.setItem("userRole", JSON.stringify(data));

        },
        error: function (data, status) {

            
        }
    });
}

function logOutUser(){
    var url = '/rest/api/logout/' + sessionId;
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        contentType: "application/json",
        success: function (data, status) {
            sessionStorage.clear();
            window.location="index.html";
        },
        error: function (data, status) {
            sessionStorage.clear();
            window.location="index.html";
        }
    });
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