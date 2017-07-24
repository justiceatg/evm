var sessionId;
var sessionDataObject;
var html = '';

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
        loadResults(100, 0);
    };
}

function loadResults(max, index) {
    // /getAllResults/{max}/{index}
    var url = '/rest/api/getAllResults/' + max + '/' + index;
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
                html += '               <img alt="image" class="img-rounded image-lg" src="images/branch.png">';
                html += '                     <br>';
                html += '                     <small><i>Ticket Number: ' + unescape(item.ticketNumber) + '</i></small>';
                html += '                <h5 class="m-b-none"><a href="#"> ' + unescape(item.resultType) + ' </a></h5>';
                html += '                     <br>';
                html += '                <p>';
                html += '                     Queue Time: ' + unescape(item.queueTime);
                html += '                     <br>';
                html += '                     Serve Time: ' + unescape(item.serveTime);
                html += '                     <br>';
                html += '                     Complete Time: ' + unescape(item.completeTime);
                html += '                </p>'
                if(item.priority == true){
                    html += '            <small><i class="fa fa-clock-o"></i> Priority</small>';
                }else if(item.priority == false){
                    html += '            <small><i class="fa fa-clock-o"></i> Not Priority</small>';
                }
                html += '           </div>';
                html += '       </div>';
                html += '   </div>';
            });
            $('#resultsHolderId').html(html);
        },
        error: function (data, status) {
            if (data.data.status === 400) {
                toastr["error"]("Unable to load the result list! ", "Error!")

                toastr.options = {
                    "debug": false,
                    "newestOnTop": false,
                    "positionClass": "toast-bottom-right",
                    "closeButton": true,
                    "progressBar": true
                }
            } else {
                toastr["error"]("Unable to load the result list! ", "Error!")

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
