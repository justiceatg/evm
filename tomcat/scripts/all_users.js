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
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.id) + ');">Edit</button>';
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.id) + ');">Delete</button>';
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
    // /getUser/{session}/{userId}
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
            html += '                       <input type="hidden" disabled class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <input type="text" disabled class="form-control" id="username" value="' + unescape(data.username) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="fullNames" class="col-sm-2 control-label">FullNames</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="fullNames" value="' + unescape(data.fullnames) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="email" class="col-sm-2 control-label">National Id</label>';
            html += '               <div class="col-sm-10">';
            if (data.nationalId === null) {
                html += '                       <input type="text" class="form-control" id="nationalId" placeholder="National Id">';
            } else {
                html += '                       <input type="text" class="form-control" id="nationalId" value="' + unescape(data.nationalId) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="email" class="col-sm-2 control-label">Email</label>';
            html += '               <div class="col-sm-10">';
            if (data.emailAddress === null) {
                html += '                       <input type="text" class="form-control" id="email" placeholder="Email">';
            } else {
                html += '                       <input type="text" class="form-control" id="email" value="' + unescape(data.emailAddress) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="email" class="col-sm-2 control-label">Address</label>';
            html += '               <div class="col-sm-10">';
            if (data.address === null) {
                html += '                       <input type="text" class="form-control" id="address" placeholder="Address">';
            } else {
                html += '                       <input type="text" class="form-control" id="address" value="' + unescape(data.address) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="email" class="col-sm-2 control-label">Cellphone</label>';
            html += '               <div class="col-sm-10">';
            if (data.cellphone === null) {
                html += '                       <input type="text" class="form-control" id="cellphone" placeholder="Cellphone">';
            } else {
                html += '                       <input type="text" class="form-control" id="cellphone" value="' + unescape(data.cellphone) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="email" class="col-sm-2 control-label">Postal Address</label>';
            html += '               <div class="col-sm-10">';
            if (data.postalAddress === null) {
                html += '                       <input type="text" class="form-control" id="postalAddress" placeholder="Postal Address">';
            } else {
                html += '                       <input type="text" class="form-control" id="postalAddress" value="' + unescape(data.postalAddress) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="userRole" class="col-sm-2 control-label">User Role</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <select class="form-control " id="userRole">';
            html += '                               <option value="">Select user type...</option>';
            html += '                               <option value="ADMINISTRATOR">Administrator</option>';
            html += '                               <option value="ELECTION_OFFICER">Election Officer</option>';
            html += '                       </select>';
            html += '               </div>';
            html += '       </div>';

            $('#editForm').html(html);

            document.getElementById('userRole').value = unescape(data.userRole);
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load user details! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load user details! ", "Error!")

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
    var userId = document.getElementById("id").value;
    var fullNames = document.getElementById("fullNames").value;
    var address = document.getElementById("address").value;
    var cellphone = document.getElementById("cellphone").value;
    var emailAddress = document.getElementById("email").value;
    var nationalId = document.getElementById("nationalId").value;
    var postalAddress = document.getElementById("postalAddress").value;
    var userRole = document.getElementById("userRole").value;

    var encodeUserId = encodeURIComponent(userId);
    var encodeFullNames = encodeURIComponent(fullNames);
    var encodedUserRole = encodeURIComponent(userRole);
    var encodeAddress = '';
    if (encodeURIComponent(address) === '') {
        encodeAddress = 'null';
    } else {
        encodeAddress = encodeURIComponent(address);
    }
    var encodeCellphone = '';
    if (encodeURIComponent(cellphone) === '') {
        encodeCellphone = 'null';
    } else {
        encodeCellphone = encodeURIComponent(cellphone);
    }
    var encodeEmailAddress = '';
    if (encodeURIComponent(emailAddress) === '') {
        encodeEmailAddress = 'null';
    } else {
        encodeEmailAddress = encodeURIComponent(emailAddress);
    }
    var encodePostalAddress = '';
    if (encodeURIComponent(postalAddress) === '') {
        encodePostalAddress = 'null';
    } else {
        encodePostalAddress = encodeURIComponent(postalAddress);
    }
    var encodeIdNumber = '';
    if (encodeURIComponent(nationalId) === '') {
        encodeIdNumber = 'null';
    } else {
        encodeIdNumber = encodeURIComponent(nationalId);
    }

    if (encodeUserId === '' || encodeFullNames === '' || encodedUserRole === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateUser/{session}/{userId}/{fullNames}/{address}/{cellphone}/{emailAddress}/{nationalId}/{postalAddress}/{userRole}
        var url = '/rest/api/updateUser/'
                + sessionId + '/'
                + encodeUserId + '/'
                + encodeFullNames + '/'
                + encodeAddress + '/'
                + encodeCellphone + '/'
                + encodeEmailAddress + '/'
                + encodeIdNumber + '/'
                + encodePostalAddress + '/'
                + encodedUserRole;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("User  was updated! ", "Success!")

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

            toastr["error"]("User was not updated! ", "Success!")

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
    // /deleteUser/{session}/{userId}
    var url = '/rest/api/deleteUser/' + sessionId + '/' + itemToDeleteId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data.status);
            toastr["success"]("User  was deleted successfully! ", "Success!")

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
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete user because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("User  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_users.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete user! ", "Error!")

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