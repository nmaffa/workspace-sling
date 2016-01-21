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


       var aDataSet = [
                       ['','','',''],
                       ['','','','']
                   ];

    $('#dynamic').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="resultTable"></table>' );

    $('#resultTable').dataTable( {
          "aaData": aDataSet,
          "aoColumns": [
              { "sTitle": "Email" },
              { "sTitle": "First Name" },
              { "sTitle": "Last Name", "sClass": "center" },
              { "sTitle": "Approve?", "sClass": "center", "sType": "html" }
          ]
    } );

    $('body').hide().fadeIn(2000);


    //Will need to use similar function for posting data
/*$('#submit').click(function() {
    var failure = function(err) {
      //  $(".main").unmask();
        alert("Unable to retrive data "+err);

    };


    //Get the user-defined values to persist in the database
    var emailVal= $('#emailInput').val() ; 
    var fNameVal= $('#fNameInput').val() ; 
    var lNameVal= $('#lNameInput').val() ; 
    var isApprovedVal= $('#isApprovedInput').val() ; 

//var url = location.pathname.replace(".html", "/_jcr_content.persist.json") + "?email="+ emailVal +"&fname="+fNameVal +"lname="+lNameVal +"&isApproved="+isApprovedVal;

    //$(".main").mask("Saving Data...");

    $.ajax(url, {
        dataType: "text",
        success: function(rawData, status, xhr) {
            var data;
            try {
                data = $.parseJSON(rawData);


                //Set the fields in the forum
                //$('#custId').val(data.pk); 

            } catch(err) {
                failure(err);
            }
        },
        error: function(xhr, status, err) {
            failure(err);
        } 
    });
});*/


//Get customer data -- called when the submitget button is clicked
//this method populates the data grid with data retrieved from the //Adobe CQ JCR
$('#submitget').click(function() {
    var failure = function(err) {
          alert("Unable to retrive data "+err);
      };

    //Get the query filter value from drop down control
    //var filter=   $('#prospectQuery').val() ; 

    //alert("Button was pressed");
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
                    var isApproved = $field.find('isApproved').text(); 

                     isApproved = "<input type=checkbox>";

                    //Set the new data 
                    oTable.fnAddData( [
                        email,
                        fName,
                        lName,
                        isApproved,]
                    );

                    });

            } catch(err) {
                failure(err);
            }
        },
        error: function(xhr, status, err) {
            failure(err);
        } 
    });
  });

}); // end ready
</script>

</head>
<body>
<div class="wrapper">
    <div class="header">
        <p class="logo">New User Approval Page</p>
    </div>
    <!-- <div class="content">
    <div class="main">
    <h1>CQ Data Persist Example</h1>

    <form name="signup" id="signup">
     <table>
    <tr>
    <td>
    <label for="first">First Name:</label>
    </td>
     <td>
    <input type="first" id="first" name="first" value="" />
    </td>
    </tr>
    <tr>
    <td>
    <label for="last">Last Name:</label>
    </td>
     <td>
    <input type="last" id="last" name="last" value="" />
    </td>
    </tr>
     <tr>
    <td>
    <label for="address">Address:</label>
    </td>
     <td>
    <input type="address" id="address" name="address" value="" />
    </td>
    </tr>
     <tr>
    <td>
   <label for="description">Description:</label>
    </td>
    <td>
    <select id="description"  >
            <option>Active Customer</option>
            <option>Past Customer</option>  
        </select>
    </td>
    </tr>
     <tr>
    <td>
    <label for="custId">Customer Id:</label>
    </td>
     <td>
    <input type="custId" id="custId" name="custId" value="" readonly="readonly"/>
    </td>
    </tr>

</table>
            <div>
                <input type="button" value="Add Customer!"  name="submit" id="submit" value="Submit">
            </div>
        </form> 
        </div>
    </div> -->

    <div id="container">
     <form name="prospectdata" id="prospectdata">

    <h1>Prospects</h1>
<!--   <div>
     <select id="prospectQuery"  >
            <option>All Prospects</option>
            <option>Active Customer</option>
            <option>Past Customer</option>  
        </select>
    </div> -->
    <div id="dynamic"></div>
    <div class="spacer"></div>

   <div>
      <input type="button" value="Get Prospects"  name="submitget" id="submitget">
    </div>
   </form>

</div>
</div>
</body>
</html>