<%--

  prospectApproval component.

  Page component used for approving prospects.

--%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:includeClientLib categories="datatables_samples" />
<html>
<head>
<meta charset="UTF-8">
<title>Approve Prospects for GSAM Funds</title>
<script>
$(document).ready(function() {

    var aDataSet = [];

    //Div used to store data table
    $('#dynamic').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="resultTable"></table>' );

    //Table of Prospect data
    $('#resultTable').dataTable( {
          "aaData": aDataSet,
          "aoColumns": [
              { "sTitle": "Email"},
              { "sTitle": "First Name" },
              { "sTitle": "Last Name", "sClass": "center" },
              { "sTitle": "Approve?", "sClass": "dt-center", "sType": "html" }
          ]
    } );


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
                    var resultXml = data.xml;

                    var loopIndex = 0; 

                    //Reference the data grid, clear it, and add new records
                    //queried from the Adobe CQ JCR
                    var oTable = $('#resultTable').dataTable();
                    oTable.fnClearTable(true);


                     //Loop through this function for each Prospect element
                     //in the returned XML
                     $(resultXml).find('prospect').each(function(){

                        var $field = $(this);
                        var email = $field.find('email').text();
                        var fName = $field.find('fname').text();
                        var lName = $field.find('lname').text();
                        var jcrPath = $field.find('jcrpath').text();

                        //JCR Path of prospect is saved as part of the ID for each checkbox, to make iterating through
                        //IDs of approved prospects easier for persisting later
                        var approveCheckbox = "<input id=chk-" + jcrPath + " type=checkbox>";

                        //Set the new data 
                        oTable.fnAddData( [
                            email,
                            fName,
                            lName,
                            approveCheckbox,]
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

	function approveProspects() {

		var failure = function(err) {
              alert("Unable to persist data " + err);
          };

		var prospectsApproved = [];

        //For each checked box, add the ID (JCR Path) of the prospect to prospectsApproved array
		$('input[type=checkbox]').each(function() {
			if (this.checked){
                var checkboxIdSplit = $(this).attr('id').split("chk-");
                if (checkboxIdSplit.length > 1){
					prospectsApproved.push(checkboxIdSplit[1]);
                }
			}
		});

        //If there are checked prospects, apply persistence
        if (prospectsApproved.length > 0){   

            //Query parameter 'paths' holds a string of all the JCR Paths of approved prospects delimited with a comma ','
            var url = location.pathname.replace(".html", "/_jcr_content.persist.json") + "?paths=" + prospectsApproved.toString();

            $.ajax(url, {
                dataType: "text",
                //data: prospectsApproved.toString(),
                success: function(data, textStatus, xhr)
                {
                    //If request successful, reload the page
					window.location.reload(true);
                },
                error: function (xhr, textStatus, errorThrown)
                {
                    console.log(xhr);
                    console.log(textStatus);
                    console.log(errorThrown);
                    failure(errorThrown);
                }

            });
        }

    } //End method declarations

  	//Load page with prospect data                                                    
    getProspectData();

    //When Approve Prospects button clicked, approve all checked prospects and reload page                                                
    $('#approveButton').click(function() {
        approveProspects();
    });

}); // end ready
</script>
</head>
<body>
    <div class="wrapper">
        <div class="header">
            <p class="logo">Prospect Approval Page</p>
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