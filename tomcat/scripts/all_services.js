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
        loadServices(100, 0);
    }
    ;
}

function loadServices(max, index) {
    // /getAllServices/{max}/{index}
    var html = '';
    var url = '/rest/api/getAllServices/' + max + '/' + index;
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
                html += '                       <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.id) + ');">Edit</button>';
                html += '                       <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.id) + ');">Delete</button>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.name) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     Queue Type: ' + unescape(item.queueType);
                html += '                     <br>';
                html += '                     Branch: ' + unescape(item.branch);
                html += '                </p>'
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#servicesHolderId').html(html);
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
    // /getService/{session}/{serviceId}
    var html = '';
    var url = '/rest/api/getService/' + sessionId + '/' + itemId;
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);

            html += '       <div class="form-group">';
            html += '               <label for="qdcId" class="col-sm-2 control-label">Qdc id</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="hidden" class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <input type="text" class="form-control" id="qdcId" value="' + unescape(data.qdcId) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="name" class="col-sm-2 control-label">Name</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="name" value="' + unescape(data.name) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="queueType" class="col-sm-2 control-label">Queue Type</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <select class="form-control " id="queueType">'
            html += '                               <option value="">Select queue type...</option>'
            html += '                               <option value="GROUP">Group</option>'
            html += '                               <option value="NORMAL">Normal</option>'
            html += '                               <option value="TRANSFER">Transfer</option>'
            html += '                               <option value="APPOINTMENT">Appointment</option>'
            html += '                       </select>'
            html += '               </div>';
            html += '       </div>';
            
            $('#editForm').html(html);

            document.getElementById('queueType').value = unescape(data.queueType);
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
    var serviceId = document.getElementById("id").value;
    var qdcId = document.getElementById("qdcId").value;
    var name = document.getElementById("name").value;
    var queueType = document.getElementById("queueType").value;

    var encodeServiceId = encodeURIComponent(serviceId);
    var encodeQdcId = encodeURIComponent(qdcId);
    var encodeName = encodeURIComponent(name);
    var encodedQueueType = encodeURIComponent(queueType);

    if (encodeQdcId === '' || encodeName === '' || encodedQueueType === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateService/{session}/{serviceId}/{qdcId}/{name}/{queueType}
        var url = '/rest/api/updateService/'
                + sessionId + '/'
                + encodeServiceId + '/'
                + encodeQdcId + '/'
                + encodeName + '/'
                + encodedQueueType;

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
    // /deleteService/{session}/{serviceId}
    var url = '/rest/api/deleteService/' + sessionId + '/' + itemToDeleteId;
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