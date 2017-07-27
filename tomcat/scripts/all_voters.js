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
        loadVoters(100, 0);
    }
    ;
}

function loadVoters(max, index) {
    // /getAllVoters/{session}/{max}/{index}
    var url = '/rest/api/getAllVoters/' + sessionId + '/' + max + '/' + index;
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
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.id) + ');">Edit</button>';
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.id) + ');">Delete</button>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.fullnames) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     National Id: ' + unescape(item.nationalId);
                html += '                </p>'
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#votersHolderId').html(html);
        },
        error: function (data, status) {
            if (data.data.status === 400) {
                toastr["error"]("Unable to load the voter list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the voter list! ", "Error!")

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
    // /getVoter/{session}/{voter}
    var html = '';
    var url = '/rest/api/getVoter/' + sessionId + '/' + itemId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            html += '       <div class="form-group">';
            html += '               <label for="fullnames" class="col-sm-2 control-label">Fullnames</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="hidden" class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <input type="text" class="form-control" id="fullnames" value="' + unescape(data.fullnames) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="nationalId" class="col-sm-2 control-label">National Id</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="nationalId" value="' + unescape(data.nationalId) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="address" class="col-sm-2 control-label">Address</label>';
            html += '               <div class="col-sm-10">';
            if (data.address === null) {
                html += '                       <input type="text" class="form-control" id="address" placeholder="Address">';
            } else {
                html += '                       <input type="text" class="form-control" id="address" value="' + unescape(data.address) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="cellphone" class="col-sm-2 control-label">Cellphone</label>';
            html += '               <div class="col-sm-10">';
            if (data.cellphone === null) {
                html += '                       <input type="text" class="form-control" id="cellphone" placeholder="Cellphone">';
            } else {
                html += '                       <input type="text" class="form-control" id="cellphone" value="' + unescape(data.cellphone) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="postalAddress" class="col-sm-2 control-label">Postal Address</label>';
            html += '               <div class="col-sm-10">';
            if (data.postalAddress === null) {
                html += '                       <input type="text" class="form-control" id="postalAddress" placeholder="Postal Address">';
            } else {
                html += '                       <input type="text" class="form-control" id="postalAddress" value="' + unescape(data.postalAddress) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="emailAddress" class="col-sm-2 control-label">Email Address</label>';
            html += '               <div class="col-sm-10">';
            if (data.emailAddress === null) {
                html += '                       <input type="text" class="form-control" id="emailAddress" placeholder="Email Address">';
            } else {
                html += '                       <input type="text" class="form-control" id="emailAddress" value="' + unescape(data.emailAddress) + '">';
            }
            html += '               </div>';
            html += '       </div>';

            $('#editForm').html(html);
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load voter details! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load voter details! ", "Error!")

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
    var voterId = document.getElementById("id").value;
    var fullnames = document.getElementById("fullnames").value;
    var address = document.getElementById("address").value;
    var cellphone = document.getElementById("cellphone").value;
    var postalAddress = document.getElementById("postalAddress").value;
    var emailAddress = document.getElementById("emailAddress").value;
    var nationalId = document.getElementById("nationalId").value;

    var encodeVoterId = encodeURIComponent(voterId);
    var encodeFullnames = encodeURIComponent(fullnames);
    var encodeAddress = encodeURIComponent(address);
    var encodedCellphone = encodeURIComponent(cellphone);
    var encodePostalAddress = encodeURIComponent(postalAddress);
    var encodeEmailAddress = encodeURIComponent(emailAddress);
    var encodedNationalId = encodeURIComponent(nationalId);

    if (encodeVoterId === '' || encodedNationalId === '' || encodeFullnames === '' || encodeAddress === '' || encodedCellphone === '' || encodePostalAddress === '' || encodeEmailAddress === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateVoter/{session}/{voter}/{fullnames}/{address}/{cellphone}/{postalAddress}/{emailAddress}/{nationalId}
        var url = '/rest/api/updateVoter/'
                + sessionId + '/'
                + encodeVoterId + '/'
                + encodeFullnames + '/'
                + encodeAddress + '/'
                + encodedCellphone + '/'
                + encodePostalAddress + '/'
                + encodeEmailAddress + '/'
                + encodedNationalId;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Voter  was updated! ", "Success!")

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

            toastr["error"]("Voter was not updated! ", "Success!")

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
    // /deleteVoter/{session}/{voter}
    var url = '/rest/api/deleteVoter/' + sessionId + '/' + itemToDeleteId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data.status);
            toastr["success"]("Voter was deleted successfully! ", "Success!")

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
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete voter because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("Voter  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_voters.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete voter! ", "Error!")

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