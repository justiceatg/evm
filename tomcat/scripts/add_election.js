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
        loadConstituency(100, 0);
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

function addElectionFunction() {

    var constituency = document.getElementById("constituencyId").value;
    var startDate = document.getElementById("startDate").value;
    var endDate = document.getElementById("endDate").value;
    var electionType = document.getElementById("electionType").value;

    var encodeConstituency = encodeURIComponent(constituency);
    var encodedStartDate = encodeURIComponent(startDate);
    var encodeEndDate = encodeURIComponent(endDate);
    var encodeElectionType = encodeURIComponent(electionType);

    if (encodeConstituency === '' || encodedStartDate === '' || encodeEndDate === '' || encodeElectionType === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /addElection/{session}/{constituency}/{startDate}/{endDate}/{electionType}
        var url = '/rest/api/addElection/'
                + sessionId + '/'
                + encodeConstituency + '/'
                + encodedStartDate + '/'
                + encodeEndDate + '/'
                + encodeElectionType;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Election  was added to the database! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }

            window.location = "all_elections.html";
        },
        error: function (data, status) {

            toastr["error"]("Election was not added to the database! ", "Error!")

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

function loadConstituency(max, index) {
    // /getAllConstituencies/{session}/{max}/{index}
    var url = '/rest/api/getAllConstituencies/' + sessionId + '/' + max + '/' + index;
    var html = '';
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            html += '<option value="">Select constituency...</option>';
            $.each(data, function (index, item) {
                html += '<option value="' + unescape(item.id) + '">' + unescape(item.name) + '</option>';
            });
            $('#constituencyId').html(html);

        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load the constituency list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the constituency list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            }
        }
    });
}