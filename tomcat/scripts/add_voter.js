var sessionId;
var sessionDataObject;
var electionId;

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
        electionId = getQueryParameter("electionId");
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

function addVoterFunction() {
    var fullnames = document.getElementById("fullnames").value;
    var nationalId = document.getElementById("nationalId").value;

    var encodedFullnames = encodeURIComponent(fullnames);
    var encodedNationalId = encodeURIComponent(nationalId);
    var encodedElectionId = encodeURIComponent(electionId);

    if (encodedFullnames === '' || encodedNationalId === '' || encodedElectionId === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /addVoter/{session}/{fullnames}/{nationalId}/{election}
        var url = '/rest/api/addVoter/'
                + sessionId + '/'
                + encodedFullnames + '/'
                + encodedNationalId + '/'
                + encodedElectionId;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Voter  was added to the database! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }

            window.location = "all_voters.html";
        },
        error: function (data, status) {

            toastr["error"]("Voter was not added to the database! ", "Success!")

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