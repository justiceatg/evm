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
        loadElections(100, 0);
    }
    ;
}

function loadElections(max, index) {
    // /getAllElections/{session}/{max}/{index}
    var url = '/rest/api/getAllElections/' + sessionId + '/' + max + '/' + index;
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
                html += '                   <a class="btn btn-default btn-xs" href="add_candidate.html?electionId=' + unescape(item.id) + '"> Candidate</a>';
                html += '                   <a class="btn btn-default btn-xs" href="add_election_officer.html?electionId=' + unescape(item.id) + '"> Election Officer</a>';
                html += '                   <a class="btn btn-default btn-xs" href="add_voter.html?electionId=' + unescape(item.id) + '"> Voter</a>';
                html += '                   <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#editModal" onclick="loadSelectedItemInfo(' + unescape(item.id) + ');">Edit</button>';
                html += '                   <button type="button" class="btn btn-default btn-xs" data-toggle="modal" data-target="#deleteModal" onclick="setSelectedItemId(' + unescape(item.id) + ');">Delete</button>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.electionType) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     Start Date: ' + unescape(item.startDate);
                html += '                     <br>';
                html += '                     End Date: ' + unescape(item.endDate);
                html += '                </p>'
                html += '            <small><i class="fa fa-clock-o"></i> Constituency: ' + unescape(item.constituency.name) + ' </small>';
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#electionsHolderId').html(html);
        },
        error: function (data, status) {
            if (data.data.status === 400) {
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


function loadSelectedItemInfo(itemId) {
    console.log(itemId);
    // /getElection/{session}/{election}
    var html = '';
    var url = '/rest/api/getElection/' + sessionId + '/' + itemId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data);
            html += '       <div class="form-group">';
            html += '               <label for="constituencyId" class="col-sm-2 control-label">Constituency</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="hidden" class="form-control" id="id" value="' + unescape(data.id) + '">';
            html += '                       <select class="form-control" id="constituencyId"></select>';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="startDate" class="col-sm-2 control-label">Start Date</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="startDate" value="' + unescape(data.startDate) + '" onfocus="(this.type = \'date\')"">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="endDate" class="col-sm-2 control-label">End Date</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <input type="text" class="form-control" id="endDate" value="' + unescape(data.endDate) + '" onfocus="(this.type = \'date\')"">';
            html += '               </div>';
            html += '       </div>';
            html += '       <div class="form-group">';
            html += '               <label for="electionType" class="col-sm-2 control-label">Election Type</label>';
            html += '               <div class="col-sm-10">';
            html += '                       <select class="form-control " id="electionType">';
            html += '                               <option value="">Select election type...</option>';
            html += '                               <option value="GENERAL">General</option>';
            html += '                               <option value="BY_ELECTION">By-Election</option>';
            html += '                               <option value="RUNOFF">Runoff</option>';
            html += '                       </select>'
            html += '               </div>';
            html += '       </div>';

            $('#editForm').html(html);


            loadConstituency(100, 0, unescape(data.constituency.id));
            document.getElementById('electionType').value = unescape(data.electionType);
        },
        error: function (data, status) {
            if (data.status === 400) {
                toastr["error"]("Unable to load election details! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load election details! ", "Error!")

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
    var election = document.getElementById("id").value;
    var constituency = document.getElementById("constituencyId").value;
    var startDate = document.getElementById("startDate").value;
    var endDate = document.getElementById("endDate").value;
    var electionType = document.getElementById("electionType").value;

    var encodeElectionId = encodeURIComponent(election);
    var encodeConstituency = encodeURIComponent(constituency);
    var encodeStartDate = encodeURIComponent(startDate);
    var encodedEndDate = encodeURIComponent(endDate);
    var encodeElectionType = encodeURIComponent(electionType);

    if (encodeElectionId === '' || encodeConstituency === '' || encodeStartDate === '' || encodedEndDate === '' || encodeElectionType === '') {

        toastr["error"]("Make sure you have provided the fields in the form! ", "Error!")

        toastr.options = {
            "debug": false,
            "newestOnTop": false,
            "positionClass": "toast-bottom-right",
            "closeButton": true,
            "progressBar": true
        }

    } else
        // /updateElection/{session}/{election}/{constituency}/{startDate}/{endDate}/{electionType}
        var url = '/rest/api/updateElection/'
                + sessionId + '/'
                + encodeElectionId + '/'
                + encodeConstituency + '/'
                + encodeStartDate + '/'
                + encodedEndDate + '/'
                + encodeElectionType;

    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {

            toastr["success"]("Election  was updated! ", "Success!")

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

            toastr["error"]("Election was not updated! ", "Success!")

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
    // /deleteElection/{session}/{election}
    var url = '/rest/api/deleteElection/' + sessionId + '/' + itemToDeleteId;
    console.log(url);
    $.ajax({
        type: "POST",
        url: url,
        data: param = "",
        dataType: 'json',
        success: function (data, status) {
            console.log(data.status);
            toastr["success"]("Election  was deleted successfully! ", "Success!")

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
            if (data.status === 400) {
                console.log(data.status);
                toastr["error"]("Unable to delete election because it is linked! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else if (data.status === 200) {
                console.log(data.status);
                toastr["success"]("Election  was deleted successfully! ", "Success!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }

                window.location = "all_elections.html";
            } else {
                console.log(data.status);
                toastr["error"]("Unable to delete election! ", "Error!")

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

function loadConstituency(max, index, constituencyId) {
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

            console.log("ConstituencyId" + constituencyId);
            document.getElementById('constituencyId').value = constituencyId;

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