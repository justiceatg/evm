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
        loadUsers(100, 0);
    }
    ;
}

function loadUsers(max, index) {
    // /getAllUsers/{session}/{max}/{index}
    var html = '';
    var url = '/rest/api/getAllUsers/' + sessionId + '/' + max + '/' + index;
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
//                html += '                       <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.username) + ');">Edit</button>';
//                html += '                       <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.username) + ');">Delete</button>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.fullnames) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     User Role: ' + unescape(item.userRole);
                html += '                     <br>';
                html += '                     Username: ' + unescape(item.username);
                html += '                     <br>';
                if (item.emailAddress === null) {
                    html += '                     Email: -';
                } else {
                    html += '                     Email: ' + unescape(item.emailAddress);
                }

                html += '                </p>'
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#usersHolderId').html(html);
        },
        error: function (data, status) {
            if (data.data.status === 400) {
                toastr["error"]("Unable to load the user list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the user list! ", "Error!")

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
    // /getUser/{session}/{username}
    var html = '';
    var url = '/rest/api/getUser/' + sessionId + '/' + itemId;
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
    var username = document.getElementById("username").value;
    var fullNames = document.getElementById("fullNames").value;
    var address = document.getElementById("address").value;
    var cellphone = document.getElementById("cellphone").value;
    var emailAddress = document.getElementById("emailAddress").value;
    var idType = document.getElementById("idType").value;
    var postalAddress = document.getElementById("postalAddress").value;
    var userRole = document.getElementById("userRole").value;

    var encodeUsername = encodeURIComponent(username);
    var encodeFullNames = encodeURIComponent(fullNames);
    var encodeAddress = encodeURIComponent(address);
    var encodedCellphone = encodeURIComponent(cellphone);
    var encodeEmailAddress = encodeURIComponent(emailAddress);
    var encodeIdType = encodeURIComponent(idType);
    var encodePostalAddress = encodeURIComponent(postalAddress);
    var encodedUserRole = encodeURIComponent(userRole);

    if (encodeFullNames === '' || encodeAddress === '' || encodedCellphone === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateUser/{session}/{username}/{fullNames}/{address}/{cellphone}/{emailAddress}/{idType}/{postalAddress}/{userRole}
        var url = '/rest/api/updateUser/'
                + sessionId + '/'
                + encodeUsername + '/'
                + encodeFullNames + '/'
                + encodeAddress + '/'
                + encodedCellphone;

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
    // /deleteUser/{session}/{username}
    var url = '/rest/api/deleteUser/' + sessionId + '/' + itemToDeleteId;
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