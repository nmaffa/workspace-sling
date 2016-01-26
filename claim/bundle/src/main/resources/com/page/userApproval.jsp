<%--

  userApproval component.

  Page component used for approving new users.

--%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="jquerysamples" />
<html>
<head>
<meta charset="UTF-8">
<title>Approve Users for GSAM Funds</title>
<style>
#signup .indent label.error {
  margin-left: 0;
}
#signup label.error {
  font-size: 0.8em;
  color: #F00;
  font-weight: bold;
  display: block;
  margin-left: 215px;
}
#signup  input.error, #signup select.error  {
  background: #FFA9B8;
  border: 1px solid red;
}
</style>
<script>
$(document).ready(function() {


       var aDataSet = [];

    $('#dynamic').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="resultTable"></table>' );

    $('#resultTable').dataTable( {
          "aaData": aDataSet,
          "aoColumns": [
              { "sTitle": "Email" },
              { "sTitle": "First Name" },
              { "sTitle": "Last Name", "sClass": "center" },
              { "sTitle": "Approve?", "sClass": "dt-center", "sType": "html" },
              { "sTitle": "UUID"/*, "bVisible": false*/ }
          ]
    } );

    $('body').hide().fadeIn(500);

    //Get Prospect data
    //This method populates the data grid with data retrieved from the Adobe CQ JCR
	function getProspectData() {
        var failure = function(err) {
              alert("Unable to retrive data "+err);
          };

        //Get the query filter value from drop down control
        //var filter=   $('#prospectQuery').val() ; 

        //Apply query
        var url = location.pathname.replace(".html", "/_jcr_content.query.json") /*+ "?filter="+ filter*/;

        $.ajax(url, {
            dataType: "text",
            success: function(rawData, status, xhr) {
                var data;
                try {
                    data = $.parseJSON(rawData);


                    //Set the fields in the forum
                    var myXML = data.xml;

                    var loopIndex = 0; 

                    //Reference the data grid, clear it, and add new records
                    //queried from the Adobe CQ JCR
                    var oTable = $('#resultTable').dataTable();
                     oTable.fnClearTable(true);


                     //Loop through this function for each Prospect element
                     //in the returned XML
                     $(myXML).find('prospect').each(function(){

                        var $field = $(this);
                        var email = $field.find('email').text();
                        var fName = $field.find('fname').text();
                        var lName = $field.find('lname').text();
                         //var isApproved = $field.find('isApproved').text();
                         //var uuid = $field.find('uuid').text();
                         var jcrPath = $field.find('jcrpath').text();

                        var isApproved = "<input id=chk-" + jcrPath + " type=checkbox>";

                        //Set the new data 
                        oTable.fnAddData( [
                            email,
                            fName,
                            lName,
                            isApproved,
                        	jcrPath,]
                        );

						loopIndex++;

                        });

                } catch(err) {
                    failure(err);
                }
            },
            error: function(xhr, status, err) {
                failure(err);
            } 
        });

	}

	getProspectData();

	function approveProspects() {

		var failure = function(err) {
              alert("Unable to persist data " + err);
          };

        //Get the query filter value from drop down control
        //var filter=   $('#prospectQuery').val() ; 

		var usersApproved = [];

		$('input[type=checkbox]').each(function() {
			if (this.checked){
                var checkboxIdSplit = $(this).attr('id').split("chk-");
                if (checkboxIdSplit.length > 1){
					usersApproved.push(checkboxIdSplit[1]);
                }
			}
		});

        //Apply persistence
        var url = location.pathname.replace(".html", "/_jcr_content.persist.json") + "?paths=" + usersApproved.toString();

        $.ajax({
            url : url,
            //type: "POST",
            //dataType: "text",
            //data: usersApproved.toString(),
            success: function(data, textStatus, xhr)
    		{

    		},
    		error: function (xhr, textStatus, errorThrown)
    		{
                failure(errorThrown);
    		}

        });

    }

    //When Approve Prospects button clicked, approve all checked prospects and reload page                                                
    $('#approveButton').click(function() {
        approveProspects();
    	location.reload();
    });

}); // end ready
</script>
</head>
<body>
    <div class="wrapper">
        <div class="header">
            <p class="logo">New User Approval Page</p>
        </div>
        <div id="container">
            <form name="prospectdata" id="prospectdata">
                <h1>Prospects</h1>
                <div id="dynamic"></div>
                <div class="spacer"></div>
                <div>
                    <input type="button" value="Approve Prospects"  name="approveButton" id="approveButton">
                </div>
            </form>
        </div>
    </div>
</body>
</html>