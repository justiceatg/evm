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
                html += '                   <button class="btn btn-default btn-xs">Edit</button>';
                html += '                   <button class="btn btn-default btn-xs">Delete</button>';
                html += '                   <a class="btn btn-default btn-xs" href="add_candidate.html?electionId=' + unescape(item.id) + '"> Candidate</a>';
                html += '                   <a class="btn btn-default btn-xs" href="add_election_officer.html?electionId=' + unescape(item.id) + '"> Election Officer</a>';
                html += '                   <a class="btn btn-default btn-xs" href="add_voter.html?electionId=' + unescape(item.id) + '"> Voter</a>';
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