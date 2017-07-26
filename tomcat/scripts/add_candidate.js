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

function addCandidateFunction() {
    var candidateNumber = document.getElementById("candidateNumber").value;
    var fullnames = document.getElementById("fullnames").value;
    var nationalId = document.getElementById("nationalId").value;

    var encodeCandidateNumber = encodeURIComponent(candidateNumber);
    var encodedFullnames = encodeURIComponent(fullnames);
    var encodedNationalId = encodeURIComponent(nationalId);
    var encodedElectionId = encodeURIComponent(electionId);

    if (encodeCandidateNumber === '' || encodedFullnames === '' || encodedNationalId === '' || encodedElectionId === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /addCandidate/{session}/{candidateNumber}/{fullnames}/{nationalId}/{election}
        var url = '/rest/api/addCandidate/'
                + sessionId + '/'
                + encodeCandidateNumber + '/'
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

            toastr["success"]("Candidate  was added to the database! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }

            window.location = "all_candidates.html";
        },
        error: function (data, status) {

            toastr["error"]("Candidate was not added to the database! ", "Success!")

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