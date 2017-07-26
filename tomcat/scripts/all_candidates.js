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
        loadCandidates(100, 0);
    }
    ;
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
                html += '                   <button class="btn btn-default btn-xs">Edit</button>';
                html += '                   <button class="btn btn-default btn-xs">Delete</button>';
                html += '               </div>';
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.fullnames) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     Candidate Number: ' + unescape(item.candidateNumber);
                html += '                     <br>';
                html += '                     National Id: ' + unescape(item.nationalId);
                html += '                     <br>';
                if(item.winner === true){
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