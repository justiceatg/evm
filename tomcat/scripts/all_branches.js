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
        loadBranches(100, 0);
    }
    ;
}

function loadBranches(max, index) {
    // /getAllBranches/{max}/{index}
    var html = '';
    var url = '/rest/api/getAllBranches/' + max + '/' + index;
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            $.each(data, function (index, item) {

                html += '<div class="col-md-4">';
                html += '       <div class="panel panel-filled ">';
                html += '           <div class="panel-body">';
                html += '                <div class="btn-group pull-right m-b-md">';
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.id) + ');">Edit</button>';
                html += '                <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.id) + ');">Delete</button>';
                html += '                   <a class="btn btn-default btn-xs" href="add_service.html?branchId=' + unescape(item.id) + '">Add Service</a>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.displayName) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     Longitude: ' + unescape(item.longitude);
                html += '                     <br>';
                html += '                     Lattitude: ' + unescape(item.lattitude);
                html += '                     <br>';
                if (item.city === null) {
                    html += '                     City: -';
                } else {
                    html += '                     City: ' + unescape(item.city);
                }
                html += '                     <br>';
                if (item.country === null) {
                    html += '                     Country: -';
                } else {
                    html += '                     Country: ' + unescape(item.country);
                }
                html += '                </p>'
                html += '            <small><i class="fa fa-clock-o"></i> Last QDC update: ' + unescape(item.lastQdcUpdate) + ' </small>';
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#branchesHolderId').html(html);
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

function loadSelectedItemInfo(itemId) {
    // /getBranch/{session}/{branchId}
    var html = '';
    var url = '/rest/api/getBranch/' + sessionId + '/' + itemId;
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
            html += '               <label for="longitude" class="col-sm-2 control-label">Longitude</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="longitude" value="' + unescape(data.longitude) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="lattitude" class="col-sm-2 control-label">Lattitude</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="lattitude" value="' + unescape(data.lattitude) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="city" class="col-sm-2 control-label">City</label>';
            html += '               <div class="col-sm-10">';
            if (data.city === null) {
                html += '                       <input type="text" class="form-control" id="city" value="">';
            } else {
                html += '                       <input type="text" class="form-control" id="city" value="' + unescape(data.city) + '">';
            }
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="country" class="col-sm-2 control-label">Country</label>';
            html += '               <div class="col-sm-10">';
            if (data.country === null) {
                html += '                       <input type="text" class="form-control" id="country" value="">';
            } else {
                html += '                       <input type="text" class="form-control" id="country" value="' + unescape(data.country) + '">';
            }
            html += '               </div>';
            html += '       </div>';

            $('#editForm').html(html);
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load branch details! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load branch details! ", "Error!")

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
    var branchId = document.getElementById("id").value;
    var name = document.getElementById("name").value;
    var longitude = document.getElementById("longitude").value;
    var lattitude = document.getElementById("lattitude").value;
    var city = document.getElementById("city").value;
    var country = document.getElementById("country").value;

    var encodeBranchId = encodeURIComponent(branchId);
    var encodeName = encodeURIComponent(name);
    var encodeLongitude = encodeURIComponent(longitude);
    var encodedLattitude = encodeURIComponent(lattitude);
    var encodeCity = '';
    if (encodeURIComponent(city) === '') {
        encodeCity = 'null';
    } else {
        encodeCity = encodeURIComponent(city);
    }
    var encodeCountry = '';
    if (encodeURIComponent(country) === '') {
        encodeCountry = 'null';
    } else {
        encodeCountry = encodeURIComponent(country);
    }



    if (encodeName === '' || encodeLongitude === '' || encodedLattitude === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateBranch/{session}/{branchId}/{name}/{longitude}/{lattitude}/{city}/{country}
        var url = '/rest/api/updateBranch/'
                + sessionId + '/'
                + encodeBranchId + '/'
                + encodeName + '/'
                + encodeLongitude + '/'
                + encodedLattitude + '/'
                + encodeCity + '/'
                + encodeCountry;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Branch  was updated! ", "Success!")

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

            toastr["error"]("Branch was not updated! ", "Success!")

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
    // /deleteBranch/{session}/{branchId}
    var url = '/rest/api/deleteBranch/' + sessionId + '/' + itemToDeleteId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data.status);
            toastr["success"]("Branch  was deleted successfully! ", "Success!")

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
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete branch because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("Branch  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_branches.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete branch! ", "Error!")

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