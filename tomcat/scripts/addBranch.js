var sessionId;
var sessionDataObject;

function initForm() {
    if (sessionStorage.length > 0) {
        sessionDataObject = JSON.parse(sessionStorage.sessionId);
        sessionId = sessionDataObject.id;
    };

    if (sessionId === null) {
        window.location = "index.html";
    };

    if (sessionId != null) {
        loadLoggedInUser();
    };

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

function addBranchFunction() {

    var name = document.getElementById("name").value;
    var longitude = document.getElementById("longitude").value;
    var lattitude = document.getElementById("lattitude").value;
    var city = document.getElementById("city").value;
    var country = document.getElementById("country").value;

    var encodeName = encodeURIComponent(name);
    var encodedLongitude = encodeURIComponent(longitude);
    var encodeLattitude = encodeURIComponent(lattitude);
    
    var encodeCity = '';
    if(encodeURIComponent(city) == ''){
        encodeCity = 'null';
    }else{
        encodeCity = encodeURIComponent(city);
    }
    
    var encodeCountry = '';
    if(encodeURIComponent(country) == ''){
        encodeCountry = 'null';
    }else{
        encodeCountry = encodeURIComponent(country);
    }

    if (encodeName == '' || encodedLongitude == '' || encodeLattitude == '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /addBranch/{session}/{name}/{longitude}/{lattitude}/{city}/{country}
        var url = '/rest/api/addBranch/'
        + sessionId + '/'
        + encodeName + '/'
        + encodedLongitude + '/'
        + encodeLattitude + '/'
        + encodeCity + '/'
        + encodeCountry;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Branch  was added to the database! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }
        
            window.location = "all_branches.html";
        },
        error: function (data, status) {

            toastr["error"]("Branch was not added to the database! ", "Error!")

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