var sessionId;
var sessionDataObject;
var index = 0;
var max = 6;

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
        loadCandidates(max, index);
    }
    ;
}

function next() {
    index = index + max;
    var html = '';
    // /getAllCandidates/{session}/{max}/{index}
    var url = '/rest/api/getAllCandidates/' + sessionId + '/' + max + '/' + index;
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            if (data.length === 0) {
                toastr["warning"]("Reached the end of the list! ", "Info!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
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
                    html += '                     Candidate Number: ' + unescape(item.candidateNumber);
                    html += '                     <br>';
                    html += '                     National Id: ' + unescape(item.nationalId);
                    html += '                     <br>';
                    if (item.winner === true) {
                        html += '                     Winner';
                    }
                    html += '                </p>'
                    html += '            <small><i class="fa fa-clock-o"></i> Constituency: ' + unescape(item.election.constituency.name) + ' </small>';
                    html += '           </div>';
                    html += '       </div>';
                    html += '   </div>';
                });
                $('#candidatesHolderId').html(html);
            }
        },
        error: function (data, status) {
            if (data.data.status === 400) {
                toastr["error"]("Unable to load the candidate list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the candidate list! ", "Error!")

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

function previous() {
    index = index - max;
    if (index < 0) {
        toastr["warning"]("Reached the beginning of the list! ", "Info!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }
    } else {
        loadCandidates(max, index);
    }
}

function loadCandidates(max, index) {
    // /getAllCandidates/{session}/{max}/{index}
    var url = '/rest/api/getAllCandidates/' + sessionId + '/' + max + '/' + index;
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
                html += '                     Candidate Number: ' + unescape(item.candidateNumber);
                html += '                     <br>';
                html += '                     National Id: ' + unescape(item.nationalId);
                html += '                     <br>';
                if (item.winner === true) {
                    html += '                     Winner';
                }
                html += '                </p>'
                html += '            <small><i class="fa fa-clock-o"></i> Constituency: ' + unescape(item.election.constituency.name) + ' </small>';
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#candidatesHolderId').html(html);
        },
        error: function (data, status) {
            if (data.data.status === 400) {
                toastr["error"]("Unable to load the candidate list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the candidate list! ", "Error!")

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
    // /getCandidate/{session}/{candidate}
    var html = '';
    var url = '/rest/api/getCandidate/' + sessionId + '/' + itemId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            html += '       <div class="form-group">';
            html += '               <label for="candidateNumber" class="col-sm-2 control-label">Candidate Number</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="hidden" class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <input type="text" class="form-control" id="candidateNumber" value="' + unescape(data.candidateNumber) + '">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="fullnames" class="col-sm-2 control-label">Fullnames</label>';
            html += '               <div class="col-sm-10">';
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
            html += '               <label for="electionId" class="col-sm-2 control-label">Election</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="hidden" class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <select class="form-control" id="electionId"></select>';
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

            loadElections(100, 0, unescape(data.election.id));
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load candidate details! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load candidate details! ", "Error!")

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
    var candidate = document.getElementById("id").value;
    var candidateNumber = document.getElementById("candidateNumber").value;
    var fullnames = document.getElementById("fullnames").value;
    var nationalId = document.getElementById("nationalId").value;
    var election = document.getElementById("electionId").value;
    var address = document.getElementById("address").value;
    var cellphone = document.getElementById("cellphone").value;
    var postalAddress = document.getElementById("postalAddress").value;
    var emailAddress = document.getElementById("emailAddress").value;

    var encodeCandidate = encodeURIComponent(candidate);
    var encodedCandidateNumber = encodeURIComponent(candidateNumber);
    var encodedNationalId = encodeURIComponent(nationalId);
    var encodeElection = encodeURIComponent(election);
    var encodeFullnames = encodeURIComponent(fullnames);
    var encodeAddress = encodeURIComponent(address);
    var encodedCellphone = encodeURIComponent(cellphone);
    var encodePostalAddress = encodeURIComponent(postalAddress);
    var encodeEmailAddress = encodeURIComponent(emailAddress);

    if (encodeCandidate === '' || encodedCandidateNumber === '' || encodedNationalId === '' || encodeElection === '' || encodeFullnames === '' || encodeAddress === '' || encodedCellphone === '' || encodePostalAddress === '' || encodeEmailAddress === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateCandidate/{session}/{candidate}/{candidateNumber}/{fullnames}/{nationalId}/{election}/{address}/{cellphone}/{postalAddress}/{emailAddress}
        var url = '/rest/api/updateCandidate/'
                + sessionId + '/'
                + encodeCandidate + '/'
                + encodedCandidateNumber + '/'
                + encodeFullnames + '/'
                + encodedNationalId + '/'
                + encodeElection + '/'
                + encodeAddress + '/'
                + encodedCellphone + '/'
                + encodePostalAddress + '/'
                + encodeEmailAddress;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Candidate  was updated! ", "Success!")

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

            toastr["error"]("Candidate was not updated! ", "Success!")

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
    // /deleteCandidate/{session}/{candidate}
    var url = '/rest/api/deleteCandidate/' + sessionId + '/' + itemToDeleteId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data.status);
            toastr["success"]("Candidate  was deleted successfully! ", "Success!")

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
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete candidate because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("Candidate  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_candidates.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete candidate! ", "Error!")

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

function loadElections(max, index, electionId) {
    // /getAllElections/{session}/{max}/{index}
    var url = '/rest/api/getAllElections/' + sessionId + '/' + max + '/' + index;
    var html = '';
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            html += '<option value="">Select election...</option>';
            $.each(data, function (index, item) {
                html += '<option value="' + unescape(item.id) + '">' + unescape(item.electionType) + '</option>';
            });
            $('#electionId').html(html);

            console.log("electionId" + electionId);
            document.getElementById('electionId').value = electionId;

        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load the election list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the election list! ", "Error!")

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

function searchFunction() {
    var searchText = document.getElementById("searchText").value;

    var encodeSearchText = encodeURIComponent(searchText);

    if (encodeSearchText === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else {

        // /rest/api/searchCandidates/{searchText}
        var html = '';
        var url = '/rest/api/searchCandidates/' + encodeSearchText;
        $.ajax({
            type: "POST",
            url: url,
            data: param = "",
            dataType: 'json',
            success: function (data, status) {
                if (data.length === 0) {
                    toastr["warning"]("No record found matcing your search text! ", "Info!")

                    toastr.options = {
                        "debug": false,
                        "newestOnTop": false,
                        "positionClass": "toast-bottom-right",
                        "closeButton": true,
                        "progressBar": true
                    }
                } else {
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
                        html += '                     Candidate Number: ' + unescape(item.candidateNumber);
                        html += '                     <br>';
                        html += '                     National Id: ' + unescape(item.nationalId);
                        html += '                     <br>';
                        if (item.winner === true) {
                            html += '                     Winner';
                        }
                        html += '                </p>'
                        html += '            <small><i class="fa fa-clock-o"></i> Constituency: ' + unescape(item.election.constituency.name) + ' </small>';
                        html += '           </div>';
                        html += '       </div>';
                        html += '   </div>';
                    });
                    $('#candidatesHolderId').html(html);
                }
            },
            error: function (data, status) {
                if (data.status === 400) {
                    toastr["error"]("No candidate found matching that id! ", "Error!")

                    toastr.options = {
                        "debug": false,
                        "newestOnTop": false,
                        "positionClass": "toast-bottom-right",
                        "closeButton": true,
                        "progressBar": true
                    }
                } else {
                    toastr["error"]("No candidate found matching that id! ", "Error!")

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
}