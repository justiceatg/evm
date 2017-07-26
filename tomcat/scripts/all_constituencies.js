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

    if (sessionId != null) {
        loadLoggedInUser();
        loadConstituencies(100, 0);
    }
    ;
}

function loadConstituencies(max, index) {
    // /getAllConstituencies/{session}/{max}/{index}
    var url = '/rest/api/getAllConstituencies/' + sessionId + '/' + max + '/' + index;
    var html = '';
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            $.each(data, function (index, item) {

                html += '<div class="col-md-4">';
                html += '       <div class="panel panel-filled ">';
                html += '           <div class="panel-body">';
                html += '                <div class="btn-group pull-right m-b-md">';
                html += '                   <button class="btn btn-default btn-xs">Edit</button>';
                html += '                   <button class="btn btn-default btn-xs">Delete</button>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.name) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     Constituency Type: ' + unescape(item.constituencyType);
                html += '                </p>'
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#constituenciesHolderId').html(html);
        },
        error: function (data, status) {
            if (data.data.status === 400) {
                toastr["error"]("Unable to load the service list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the service list! ", "Error!")

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


function loadSelectedItemInfo(itemId) {
    console.log(itemId);
    // /getConstituency/{session}/{constituency}
    var html = '';
    var url = '/rest/api/getConstituency/' + sessionId + '/' + itemId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);

            html += '       <div class="form-group">';
            html += '               <label for="username" class="col-sm-2 control-label">Username</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" disabled class="form-control" id="username" value="' + unescape(data.username) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="fullNames" class="col-sm-2 control-label">FullNames</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="fullNames" value="' + unescape(data.fullNames) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="email" class="col-sm-2 control-label">Email</label>';
            html += '               <div class="col-sm-10">';
            if (data.email === null) {
                html += '                       <input type="text" class="form-control" id="email" placeholder="Email">';
            } else {
                html += '                       <input type="text" class="form-control" id="email" value="' + unescape(data.email) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="userRole" class="col-sm-2 control-label">User Role</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <select class="form-control " id="userRole">'
            html += '                               <option value="">Select user type...</option>'
            html += '                               <option value="ADMINISTRATOR">Administrator</option>'
            html += '                               <option value="BRANCH">Branch Manager</option>'
            html += '                               <option value="PERF_MANAGER">Perf Manager</option>'
            html += '                       </select>'
            html += '               </div>';
            html += '       </div>';

            $('#editForm').html(html);

            document.getElementById('userRole').value = unescape(data.userRole);
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load service details! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load service details! ", "Error!")

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

function updateFunction() {
    var constituency = document.getElementById("constituency").value;
    var name = document.getElementById("name").value;
    var constituencyType = document.getElementById("constituencyType").value;

    var encodeServiceId = encodeURIComponent(constituency);
    var encodeName = encodeURIComponent(name);
    var encodedConstituencyType = encodeURIComponent(constituencyType);

    if (encodeQdcId === '' || encodeName === '' || encodedConstituencyType === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateConstituency/{session}/{constituency}/{name}/{constituencyType}
        var url = '/rest/api/updateConstituency/'
                + sessionId + '/'
                + encodeServiceId + '/'
                + encodeName + '/'
                + encodedConstituencyType;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Service  was updated! ", "Success!")

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

            toastr["error"]("Service was not updated! ", "Success!")

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

var itemToDeleteId = '';
function setSelectedItemId(itemId) {
    itemToDeleteId = itemId;
}

function deleteSelectedItem() {
    // /deleteConstituency/{session}/{constituency}
    var url = '/rest/api/deleteConstituency/' + sessionId + '/' + itemToDeleteId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data.status);
            toastr["success"]("Service  was deleted successfully! ", "Success!")

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
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete service because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("Service  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_services.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete service! ", "Error!")

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