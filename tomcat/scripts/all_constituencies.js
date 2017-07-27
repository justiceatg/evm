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
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.id) + ');">Edit</button>';
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.id) + ');">Delete</button>';
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
            html += '               <label for="name" class="col-sm-2 control-label">Name</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="hidden" class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <input type="text" class="form-control" id="name" value="' + unescape(data.name) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="userRole" class="col-sm-2 control-label">Constituency Type</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <select class="form-control" id="constituencyType">';
            html += '                               <option value="">Select constituency type...</option>';
            html += '                               <option value="WARD">Ward</option>';
            html += '                               <option value="DISTRICT">District</option>';
            html += '                               <option value="COUNTY">County</option>';
            html += '                               <option value="PROVINCE">Province</option>';
            html += '                               <option value="STATE">State</option>';
            html += '                               <option value="CITY">City</option>';
            html += '                               <option value="MUNICIPALITY">Munucipality</option>';
            html += '                               <option value="COUNTRY">Country</option>';
            html += '                               ';
            html += '                       </select>';
            html += '               </div>';
            html += '       </div>';

            $('#editForm').html(html);

            document.getElementById('constituencyType').value = unescape(data.constituencyType);
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
    var id = document.getElementById("id").value;
    var name = document.getElementById("name").value;
    var constituencyType = document.getElementById("constituencyType").value;

    var encodeId = encodeURIComponent(id);
    var encodeName = encodeURIComponent(name);
    var encodedConstituencyType = encodeURIComponent(constituencyType);

    if (encodeId === '' || encodeName === '' || encodedConstituencyType === '') {

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
                + encodeId + '/'
                + encodeName + '/'
                + encodedConstituencyType;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Constituency  was updated! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }

            window.location = "all_constituencies.html";
        },
        error: function (data, status) {

            toastr["error"]("Constituency was not updated! ", "Success!")

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
            toastr["success"]("Constituency  was deleted successfully! ", "Success!")

            toastr.options = {
                "debug": false,
                "newestOnTop": false,
                "positionClass": "toast-bottom-right",
                "closeButton": true,
                "progressBar": true
            }

            window.location = "all_constituencies.html";
        },
        error: function (data, status) {
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete constituency because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("Constituency  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_constituencies.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete constituency! ", "Error!")

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