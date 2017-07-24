var sessionId;
var sessionDataObject;
var branchId = '';

function initForm() {
    if (sessionStorage.length > 0) {
        sessionDataObject = JSON.parse(sessionStorage.sessionId);
        sessionId = sessionDataObject.id;
    };

    if (sessionId === null) {
        window.location = "index.html";
    };
    
    branchId = getQueryParameter("branchId");

    if (sessionId != null) {
        loadLoggedInUser();
        loadGroups(100, 0);
        loadBranches(100, 0);
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

function addServiceFunction() {
    var id = document.getElementById("id").value;
    var name = document.getElementById("name").value;
    var queueType = document.getElementById("queueType").value;
    var group = document.getElementById("group").value;
//    var branch = document.getElementById("branch").value;

    var encodeId = encodeURIComponent(id);
    var encodeName = encodeURIComponent(name);
    var encodedQueueType = encodeURIComponent(queueType);
    var encodeGroup = '';
    if(encodeURIComponent(group) == ''){
        encodeGroup = 'null';
    }else{
        encodeGroup = encodeURIComponent(group);
    } 
//    var encodedBranch = encodeURIComponent(branch);
    


    if (encodeId == '' || encodeName == '' || encodedQueueType == '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /addService/{session}/{id}/{name}/{queueType}/{groupId}/{branchName}
        var url = '/rest/api/addService/'
        + sessionId + '/'
        + encodeId + '/'
        + encodeName + '/'
        + encodedQueueType + '/'
        + encodeGroup + '/'
        + branchId;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Service  was added to the database! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }
        
            window.location = "all_services.html";
        },
        error: function (data, status) {

            toastr["error"]("Service was not added to the database! ", "Success!")

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

function loadGroups(max, index) {
    // /getAllServices/{max}/{index}
    var url = '/rest/api/getAllServices/' + max + '/' + index;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            
            $('#group').typeahead({
                source: data
            });
        
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load the group list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the group list! ", "Error!")

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

function loadBranches(max, index) {
    // /getAllBranches/{max}/{index}
    var url = '/rest/api/getAllBranches/' + max + '/' + index;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            
            $('#branch').typeahead({
                source: data
            });
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load the branch list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the branch list! ", "Error!")

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
