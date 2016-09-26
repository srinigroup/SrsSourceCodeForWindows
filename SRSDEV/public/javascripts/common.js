
	 <!-- Date Picker : start -->
	 $(function() {
		 $( ".datepicker1" ).datepicker({
			dateFormat: "dd/mm/yy"
			
		 });
		  $('.ui-datepicker').css('z-index', 99999999999999);
	 });
  	<!-- Date Picker : End -->


   
    
	<!-- diable ENTER key in-side form fields : start -->
	
	function stopRKey(evt) { 
	  var evt = (evt) ? evt : ((event) ? event : null); 
	  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null); 
	  if ((evt.keyCode == 13) && (node.type=="text"))  {return false;} 
	} 
	
	document.onkeypress = stopRKey; 
	<!-- diable ENTER key in-side form fields : end -->
	
	
	
	function handleQueryResponse(response) {
       
        // checking filter sales/media/invoices ..etc
        var filter = document.getElementById("byType").value;
       // alert(filter);
       
       
        if(filter == "sales" || filter == "media"){  // for sales and media options 
        
         var dataTable = new google.visualization.DataTable();
         
          // determine the number of rows and columns.
          var numRows = response.length;
          var numCols = response[0].length;

          // in this case the first column is of type 'string'.
          dataTable.addColumn('string', response[0][0]); //first,second columns are String's
			 dataTable.addColumn('string', response[0][1]);
			 
          // all other columns are of type 'number'.
          for (var i = 2; i < numCols; i++)   //Making remaining columns as Numbers 
            dataTable.addColumn('number', response[0][i]);           

          // now add the rows.
          for (var i = 1; i < numRows; i++)
            dataTable.addRow(response[i]);      
            
           
            
            // grouping the data in static way
            
         /*   var cols = [{'column': 2, 'aggregation': google.visualization.data.sum, 'type': 'number'},
	            {'column': 3, 'aggregation': google.visualization.data.sum, 'type': 'number'},
	            {'column': 4, 'aggregation': google.visualization.data.sum, 'type': 'number'},
	            {'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'},
	            {'column': 6, 'aggregation': google.visualization.data.sum, 'type': 'number'},
	            {'column': 7, 'aggregation': google.visualization.data.sum, 'type': 'number'}]; */
	       
	        // grouping the data in dynamic  way   
	        
	         var cols = new Array(numCols-2);
	         
	         for(var i=2;i<numCols;i++){
	         
	         cols[i-2] = {'column': i, 'aggregation': google.visualization.data.sum, 'type': 'number'};
	         
	         }   
		
		
			 
			 //sortBY value,either by store or date from select box
			 
            var sort = document.getElementById("sortBy").value;
           
	
           if(sort == 'STORE'){
            	// [0] gives store name
            	 grouped_tableData = google.visualization.data.group(dataTable, [0], cols);
				 grouped_data = google.visualization.data.group(dataTable, [0], cols);
            
            }else{
            	// [1] gives Date
            	 grouped_tableData = google.visualization.data.group(dataTable, [1], cols);
				 grouped_data = google.visualization.data.group(dataTable, [1], cols);
            } 
      
      
      // to calculate sum at bottom of Reports Page   <!-- sum logic starts -->
    	
    	function getSum(data, column) {
		    var total = 0;
		    for (i = 0; i < data.getNumberOfRows(); i++)
		      total = total + data.getValue(i, column);
		    return total;
		  }
		  
  		var sumArraysize = grouped_tableData.getNumberOfColumns(); // prepare an array to display sums at bottom of Reports Page
  		var sumAtBottom = new Array(sumArraysize);
  		sumAtBottom[0] = "Total";
  		for(var sumIndex=1 ; sumIndex < sumArraysize ; ++sumIndex){
  			//alert(sumIndex+ ": "+ getSum(dataTable,(sumIndex+1)) );
  			sumAtBottom[sumIndex] =  getSum(dataTable,(sumIndex+1)) ; 
  		}
  		
  		grouped_tableData.addRow(sumAtBottom); // add a row at the bottom to display sum   <!-- sum logic ends -->
    	//grouped_tableData.addRow(['Total', getSum(dataTable,2), getSum(dataTable,3) ,getSum(dataTable,4),  getSum(dataTable,5), getSum(dataTable,6),  getSum(dataTable,7), getSum(dataTable,8),  getSum(dataTable,9)]);
             
     barsVisualization = new google.visualization.ColumnChart(document.getElementById('visualizationbar'));
     tablevisualization = new google.visualization.Table(document.getElementById('visualizationtable'));
      
      
      // number format,with special chracter
      
    /*   var formatter = new google.visualization.NumberFormat(
      {prefix: '$', negativeColor: 'red', negativeParens: true});
 	 formatter.format(grouped_data,1); // Apply formatter to second column
 	 formatter.format(grouped_tableData,1); // Apply formatter to all column */
 	 
 	 var formatter = new google.visualization.NumberFormat("decimalSymbol");
 	// alert(numCols);
 	  for (var i = 1; i < numCols-1; i++){   // apply for all columns
 	  formatter.format(grouped_data,i);
 	  formatter.format(grouped_tableData,i);
 	  }
    
    barsVisualization.draw(grouped_data,
           {title:"Sales Heads for Store(s)",
           is3D:true,
            width:1000, height:400,
			legend:'AUTO',
			//isStacked: true,
            vAxis: {title: "Stores"},
            hAxis: {title: "Sales"}}
      );
    
    
      tablevisualization.draw(grouped_tableData, {showRowNumber: true, 'cssClassNames':{headerCell: 'googleHeaderCell'} });
      
      // events R&D start
      
      google.visualization.events.addListener(barsVisualization, 'select', function () { selectHandler(barsVisualization); } );

		function selectHandler(chart1) {
		
			
			//alert("hi");
			
		
    	// draw dynamic graph for select event
    	var selection = chart1.getSelection();
    	var selectedItem = selection[0];
			var true_selected =  grouped_tableData.getValue(selectedItem.row,selectedItem.column);
			var label = grouped_tableData.getColumnLabel(selectedItem.column);
			var columnType = grouped_tableData.getColumnType(selectedItem.column);
			
			var dataTableSub = new google.visualization.DataTable();
				
				 dataTableSub.addColumn('string', 'Gopi');
				 dataTableSub.addColumn(columnType, label);
				 dataTableSub.addRow(['Type',true_selected]);
		 var chartSub = new google.visualization.ColumnChart(document.getElementById('chartpopUP'));
					chartSub.draw(dataTableSub,{title:"Sales Heads for Store(s)",
           										is3D:true,
           										colors: ['indigo'],
									            width:500, height:300,
												legend:'AUTO',
									            }
								); 
					
					//show modal
					
					$('#myModalReportClick').modal('show');
					
	
		 }//events R&D end
     }else if( filter == "invoices"){ // for invoices option
		
		 var dataTable = new google.visualization.DataTable();
		 
			 // determine the number of rows and columns.
          	var numRows = response.length;
          	var numCols = response[0].length;
          	
          		// hard coding column types
          
          	dataTable.addColumn('string', response[0][0]);
          	dataTable.addColumn('string', response[0][1]);
          	dataTable.addColumn('string', response[0][2]);
          	dataTable.addColumn('string', response[0][3]);
          	dataTable.addColumn('string', response[0][4]);
          	dataTable.addColumn('number', response[0][5]);
          	
          	 // now add the rows.
          	for (var i = 1; i < numRows; i++)
            dataTable.addRow(response[i]);  
            
          	
         
	        console.log("columns : "+cols);    
	       var sort = document.getElementById("sortBy").value;
	       console.log("sort by : "+sort);
          		
          if(sort == 'STORE'){
            	// grouping the data in dynamic  way   
            	//alert("inside store");
	         	 cols = [
           		
	           
	            {'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'}];     
            	
          	 	grouped_tableData = google.visualization.data.group(dataTable, [0],cols);
				grouped_data = google.visualization.data.group(dataTable, [0],cols);
		 }
		 else if(sort == 'SUPPLIER') {
		 //alert("inside supplier");
		  		var cols = [
	            
	            {'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'}];   
				 
				 grouped_tableData = google.visualization.data.group(dataTable, [0], cols);
				 grouped_data = google.visualization.data.group(dataTable, [0], cols);
		 }
		 else if(sort == 'DATE') {
		
				 var cols = [
	            
	            {'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'}];
	           
				 grouped_tableData = google.visualization.data.group(dataTable, [0], cols);
				 grouped_data = google.visualization.data.group(dataTable, [0], cols);
				 
		}
		else {
		//alert("inside payterms");
				var cols = [
	           
	            {'column': 5, 'aggregation': google.visualization.data.sum, 'type': 'number'}];
		
				 grouped_tableData = google.visualization.data.group(dataTable, [0], cols);
				 grouped_data = google.visualization.data.group(dataTable, [0], cols);
		
		}
              	 console.log("grouped data : "+grouped_data);
              // to calculate sum at bottom of Reports Page   <!-- sum logic starts -->
    	
    		function getSum(data, column) {
		    var total = 0;
		    for (i = 0; i < data.getNumberOfRows(); i++)
		      total = total + data.getValue(i, column);
		    return total;
		  	}
  			//  here we have only one numeric column 
  			dataTable.addRow(['Total', null ,null,  null, null,getSum(dataTable,5)]); // add a row at the bottom to display sum   <!-- sum logic ends -->
             
        	var formatter = new google.visualization.NumberFormat("decimalSymbol");
		 	formatter.format(grouped_data,1); // format only specific column
			formatter.format(dataTable,1);
		 	
            barsVisualization = new google.visualization.ColumnChart(document.getElementById('visualizationbar'));
     		tablevisualization = new google.visualization.Table(document.getElementById('visualizationtable'));
     		
     		barsVisualization.draw(grouped_data,
           {title:"Sales Heads for Store(s)",
           is3D:true,
            width:1000, height:400,
			legend:'AUTO',
			//isStacked: true,
            vAxis: {title: "Stores"},
            hAxis: {title: "Sales"}}
      		);
      		
      		 tablevisualization.draw(dataTable, {showRowNumber: true, 'cssClassNames':{headerCell: 'googleHeaderCell'} });
		
		}else if( filter == "ReconciliationReport"){ // for ReconciliationReport option
		
			var dataTable = new google.visualization.DataTable();
			
			 // determine the number of rows and columns.
          	var numRows = response.length;
          	var numCols = response[0].length;
          	
          		// hard coding column types
          
          	dataTable.addColumn('string', response[0][0]);
          	dataTable.addColumn('string', response[0][1]);
          	/* dataTable.addColumn('number', response[0][2]);
          	dataTable.addColumn('number', response[0][3]);
          	dataTable.addColumn('number', response[0][4]);
          	dataTable.addColumn('number', response[0][5]);
          	dataTable.addColumn('number', response[0][6]);
          	dataTable.addColumn('number', response[0][7]);
          	dataTable.addColumn('number', response[0][8]);
          	dataTable.addColumn('number', response[0][9]);
          	dataTable.addColumn('number', response[0][10]);
          	dataTable.addColumn('number', response[0][11]);
          	dataTable.addColumn('number', response[0][12]);
          	dataTable.addColumn('number', response[0][13]);
          	dataTable.addColumn('number', response[0][14]);*/
          	// all other columns are of type 'number'.
          for (var i = 2; i < numCols; i++)   //Making remaining columns as Numbers 
            dataTable.addColumn('number', response[0][i]);
          	
          	 // now add the rows.
          	for (var i = 1; i < numRows; i++)
            dataTable.addRow(response[i]); 
            
            
             // grouping the data in dynamic  way   
	        
	         var cols = new Array(numCols-2);
	         
	         for(var i=2;i<numCols;i++){
	         
	         cols[i-2] = {'column': i, 'aggregation': google.visualization.data.sum, 'type': 'number'};
	         
	         }   
		
		
			 
			 //sortBY value,either by store or date from select box
			 
            var sort = document.getElementById("sortBy").value;
           
	
           if(sort == 'STORE'){
            	// [0] gives store name
            	 grouped_tableData = google.visualization.data.group(dataTable, [0], cols);
				 grouped_data = google.visualization.data.group(dataTable, [0], cols);
            
            }else{
            	// [1] gives Date
            	 grouped_tableData = google.visualization.data.group(dataTable, [1], cols);
				 grouped_data = google.visualization.data.group(dataTable, [1], cols);
            } 
            
            // to calculate sum at bottom of Reports Page   <!-- sum logic starts -->
    	
    	function getSum(data, column) {
		    var total = 0;
		    for (i = 0; i < data.getNumberOfRows(); i++)
		      total = total + data.getValue(i, column);
		    return total;
		  }
		  
  		var sumArraysize = grouped_tableData.getNumberOfColumns(); // prepare an array to display sums at bottom of Reports Page
  		var sumAtBottom = new Array(sumArraysize);
  		sumAtBottom[0] = "Total";
  		for(var sumIndex=1 ; sumIndex < sumArraysize ; ++sumIndex){
  			//alert(sumIndex+ ": "+ getSum(dataTable,(sumIndex+1)) );
  			sumAtBottom[sumIndex] =  getSum(dataTable,(sumIndex+1)) ; 
  		}
  		
  		grouped_tableData.addRow(sumAtBottom); // add a row at the bottom to display sum   <!-- sum logic ends -->
  		
             barsVisualization = new google.visualization.ColumnChart(document.getElementById('visualizationbar'));
     		tablevisualization = new google.visualization.Table(document.getElementById('visualizationtable'));
     		
     		 var formatter = new google.visualization.NumberFormat("decimalSymbol");
 			// alert(numCols);
		 	  for (var i = 1; i < numCols-1; i++){   // apply for all columns
		 	  formatter.format(grouped_data,i);
		 	  formatter.format(grouped_tableData,i);
		 	  }
     		
     		barsVisualization.draw(grouped_data,
           {title:"Sales Heads for Store(s)",
           is3D:true,
            width:1000, height:400,
			legend:'AUTO',
			//isStacked: true,
            vAxis: {title: "Stores"},
            hAxis: {title: "Sales"}}
      		);
      		
      		 tablevisualization.draw(grouped_tableData, {showRowNumber: true, 'cssClassNames':{headerCell: 'googleHeaderCell'} });
		
		
		
		}// end ReconciliationReport filter option
		 
		// Management Reconcilation
		else if( filter == "ManagementStoreReconciliationReport"){ // for ManagementStoreReconciliationReport option
		
			var dataTable = new google.visualization.DataTable();
			
			 // determine the number of rows and columns.
          	var numRows = response.length;
          	var numCols = response[0].length;
          	//console.log("total rows : "+numRows);
          		// hard coding column types
          
          	dataTable.addColumn('string', response[0][0]);
          	dataTable.addColumn('string', response[0][1]);          	
          /*dataTable.addColumn('string', response[0][2]);
            dataTable.addColumn('number', response[0][3]);
          	dataTable.addColumn('number', response[0][4]);
          	dataTable.addColumn('number', response[0][5]);
          	dataTable.addColumn('number', response[0][6]);
          	dataTable.addColumn('number', response[0][7]);
          	dataTable.addColumn('number', response[0][8]);
          	dataTable.addColumn('number', response[0][9]);
          	dataTable.addColumn('number', response[0][10]);
          	dataTable.addColumn('number', response[0][11]);
          	dataTable.addColumn('number', response[0][12]);
          	dataTable.addColumn('number', response[0][13]);
          	dataTable.addColumn('number', response[0][14]);*/
          	// all other columns are of type 'number'.
          	
       		 for (var i = 2; i < numCols; i++)   //Making remaining columns as Numbers 
       		 {
       		 	if(i<numCols-1)
          	 		dataTable.addColumn('number', response[0][i]);
          	 	else
          	 		dataTable.addColumn('string', response[0][i]);
          	}
          	 // now add the rows.
          	for (var i = 1; i < numRows; i++)
            	dataTable.addRow(response[i]); 
			 
			//sortBY value,either by store or date from select box
            var sort = document.getElementById("sortBy").value;
           
           	if(sort == 'STORE'){
            	// grouping the data in dynamic  way   
	         	var cols = new Array(numCols-2);
	         	
	         	for(var i=2;i<numCols-1;i++){
	        	 	cols[i-2] = {'column': i, 'aggregation': google.visualization.data.sum, 'type': 'number'};
	         	}   
            	// [0] gives store name
             	grouped_tableData = google.visualization.data.group(dataTable, [0,9], cols);
			 	grouped_data = google.visualization.data.group(dataTable, [0,9], cols);
            
            }else{
            
           		// grouping the data in dynamic  way   
	        	var cols = new Array(numCols-2);
	         
	        	for(var i=2;i<numCols-1;i++){
	         		cols[i-2] = {'column': i, 'aggregation': google.visualization.data.sum, 'type': 'number'};
	        	}   
            	// [2] gives Date
            	grouped_tableData = google.visualization.data.group(dataTable, [1], cols);
				grouped_data = google.visualization.data.group(dataTable, [1], cols);
            } 
            
            function getSum(data, column) {
		    	var total = 0;
		    	for (i = 0; i < data.getNumberOfRows(); i++)
		      		total = total + data.getValue(i, column);
		    	return total;
		  	}
		  
		  	var sumArraysize = grouped_tableData.getNumberOfColumns(); // prepare an array to display sums at bottom of Reports Page
  		  	var sumAtBottom = new Array(sumArraysize);
  		  	// console.log("sum at bottm : "+sumArraysize);
  		  	//console.log("sum at bottm : "+sumAtBottom);
  		  	sumAtBottom[0] = "Total";
  		  
            barsVisualization = new google.visualization.ColumnChart(document.getElementById('visualizationbar'));
     		tablevisualization = new google.visualization.Table(document.getElementById('visualizationtable'));
     		
     	  	var formatter = new google.visualization.NumberFormat("decimalSymbol");
 			// alert(numCols);
 			if(sort == 'STORE'){
 			  	//console.log("sum at bottm : "+sumArraysize);
 			   	for(var sumIndex=2; sumIndex < sumArraysize; ++sumIndex){
  			     	//alert(sumIndex+ ": "+ getSum(dataTable,(sumIndex+1)) );
  			     	sumAtBottom[sumIndex] =  getSum(dataTable,(sumIndex)) ; 
  		       	}
  		
  		     	grouped_tableData.addRow(sumAtBottom); // add a row at the bottom to display sum   <!-- sum logic ends -->
		 	 	for (var i =1; i < numCols-1; i++){   // apply for all columns
		 	     	formatter.format(grouped_data,i);
		 	 	 	formatter.format(grouped_tableData,i);
		 	  	}
     		}
     		else {
     			for(var sumIndex=1; sumIndex < sumArraysize ; ++sumIndex){
  					//alert(sumIndex+ ": "+ getSum(dataTable,(sumIndex+1)) );
  					sumAtBottom[sumIndex] =  getSum(dataTable,(sumIndex+1)) ; 
  		  		}
  		
  		  		grouped_tableData.addRow(sumAtBottom); // add a row at the bottom to display sum   <!-- sum logic ends -->
  		  		//console.log("num of columns in else : "+numCols);
     		 	for (var i = 1; i < numCols-2; i++){   // apply for all columns
		 	  		formatter.format(grouped_data,i);
		 	  		formatter.format(grouped_tableData,i);
		 	  	}
     		}
     	
     		barsVisualization.draw(grouped_data,
	           	{title:"Sales Heads for Store(s)",
	           	is3D:true,
	            width:1000, height:400,
				legend:'AUTO',
				//isStacked: true,
	            vAxis: {title: "Stores"},
	            hAxis: {title: "Sales"}}
      		);
      		
      		tablevisualization.draw(grouped_tableData, {showRowNumber: true, 'cssClassNames':{headerCell: 'googleHeaderCell'} });
		
		}// end ReconciliationReport filter option
     
    } // end of function handleQueryResponse(response)
    
    
    
    
    
  
 
 
 var actualMediaCollect = 0;
 
 var settleMediaCollect = 0;
 
	var backCount = 0; 
	
	 
function goBack() {
	//alert("hi");
	backCount=backCount-1;
    window.history.go(backCount);
}

function isRolesSelected(){
	
	var list=document.getElementById("roles[]");
	
	var result=list.value;
	
	if(result.trim().length == 0){
	alert("Select at least one role !!");
	
	return false;
	}
	
	

}

 
function myFunction(action) {
	//alert(action);
    
    if(action=="DailySalesReconciliation" || action=="DailyReconciliation"){
    location.href = "#acc4";
    }
    else if(action=="shift"){
    location.href = "#acc4";
    }
    else{
    location.href = "#acc1";
    }
}


$(document).ready(function() {
	
	// BootsTrap Modal As Alert Box ,it will convert normal alert into Model 
	
	  //var proxied = window.alert;
  		window.alert = function() {
		    $("#myModalForJSAlertBox .modal-body").text(arguments[0]);
		    $("#myModalForJSAlertBox").modal('show');
  		};
  		
	// 2nd datepicker based on 1st datepicker, where two dates are required in our application
	
		$(".startDatePicker").datepicker({
	        dateFormat: "dd/mm/yy",
	        onSelect: function (date) {
	            var date2 = $('.startDatePicker').datepicker('getDate');
	            date2.setDate(date2.getDate() + 0);
	            //console.log("date is :"+date);
	            //console.log("date2 is :"+date2);
	            $('.endDatePicker').datepicker('setDate', date2);
	            //console.log("date2 before adding is :"+date2);
	            
	            //sets minDate to dt1 date + 1
	            $('.endDatePicker').datepicker('option', 'minDate', date2);
	            //console.log("date2 is after adding :"+date2);
	            
	        }
	    });
	    $('.endDatePicker').datepicker({
	        dateFormat: "dd/mm/yy",
	        onClose: function () {
	            var dt1 = $('.startDatePicker').datepicker('getDate');
	            var dt2 = $('.endDatePicker').datepicker('getDate');
	            //check to prevent a user from entering a date below date of dt1
	            if (dt2 <= dt1) {
	                var minDate = $('.endDatePicker').datepicker('option', 'minDate');
	                $('.endDatePicker').datepicker('setDate', minDate);
	            }
	        }
	    });
	  
	 // Password change option , validations checking
	 
	 $("input[type=password]").keyup(function(){
		    var ucase = new RegExp("[A-Z]+");
			var lcase = new RegExp("[a-z]+");
			var num = new RegExp("[0-9]+");
			
			if($("#password1").val().length >= 8){
				$("#8char").removeClass("icon-remove");
				$("#8char").addClass("icon-ok");
				$("#8char").css("color","#00A41E");
			}else{
				$("#8char").removeClass("icon-ok");
				$("#8char").addClass("icon-remove");
				$("#8char").css("color","#FF0004");
			}
			
			if(ucase.test($("#password1").val())){
				$("#ucase").removeClass("icon-remove");
				$("#ucase").addClass("icon-ok");
				$("#ucase").css("color","#00A41E");
			}else{
				$("#ucase").removeClass("icon-ok");
				$("#ucase").addClass("icon-remove");
				$("#ucase").css("color","#FF0004");
			}
			
			if(lcase.test($("#password1").val())){
				$("#lcase").removeClass("icon-remove");
				$("#lcase").addClass("icon-ok");
				$("#lcase").css("color","#00A41E");
			}else{
				$("#lcase").removeClass("icon-ok");
				$("#lcase").addClass("icon-remove");
				$("#lcase").css("color","#FF0004");
			}
			
			if(num.test($("#password1").val())){
				$("#num").removeClass("icon-remove");
				$("#num").addClass("icon-ok");
				$("#num").css("color","#00A41E");
			}else{
				$("#num").removeClass("icon-ok");
				$("#num").addClass("icon-remove");
				$("#num").css("color","#FF0004");
			}
			
			if($("#password1").val() == $("#password2").val() && $("#password1").val() != '' ){
				$("#pwmatch").removeClass("icon-remove");
				$("#pwmatch").addClass("icon-ok");
				$("#pwmatch").css("color","#00A41E");
			}else{
				$("#pwmatch").removeClass("icon-ok");
				$("#pwmatch").addClass("icon-remove");
				$("#pwmatch").css("color","#FF0004");
			}
			
			//enable submit button based on validations
			if( $('#8char').is(".icon.icon-ok") && $('#ucase').is(".icon.icon-ok") && $('#lcase').is(".icon.icon-ok") && $('#num').is(".icon.icon-ok") && $('#pwmatch').is(".icon.icon-ok") ){
				$("#password_modal_sub_btn").removeAttr("disabled", "disabled");
			}else{
				$("#password_modal_sub_btn").attr("disabled", "disabled");
			}
			
		});
	  
	
	// on page load if check box is selected for leave type in timesheet
	
	if($("#applyLeaveCheckBox").prop('checked') == true){
	
            $('#leaveType_field').css('display','block'); // display leave type list box
			$('#leaveType').prop('required',true);
			$('#leaveType option:first').val("");
			
			$('#timesheetStartTime_field').css('display','none'); // hide time fields,duration
			$('#timesheetEndTime_field').css('display','none');
			$('#duration_field').css('display','none');
			
			$('#endTimeHour').prop('required',false); // disable required attribute if leave selected
			$('#endTimeMins').prop('required',false);
			$('#startTimeMins').prop('required',false);
			$('#startTimeHour').prop('required',false);
			
        }else{
        
        	$('#leaveType_field').css('display','none'); // hide leave type list box
			$('#leaveType').prop('required',false);
			$('#leaveType option:first').val("None");
			
			$('#timesheetStartTime_field').css('display','block'); // diaplay time fields,duration
			$('#timesheetEndTime_field').css('display','block');
			$('#duration_field').css('display','block');
			
			$('#endTimeHour').prop('required',true); // enable required attribute if leave not selected
			$('#endTimeMins').prop('required',true);
			$('#startTimeMins').prop('required',true);
			$('#startTimeHour').prop('required',true);
        
        }
	
	// leave type checkbox, onchange disable or enable dropdown of leave type options
	
	$('#applyLeaveCheckBox').change(function () {
	
    	if($("#applyLeaveCheckBox").prop('checked') == true){
    	
    		$('#leaveType_field').css('display','block'); // display leave type list box
			$('#leaveType').prop('required',true);
			$('#leaveType option:first').val("");
			
			$('#timesheetStartTime_field').css('display','none'); // hide time fields,duration
			$('#timesheetEndTime_field').css('display','none');
			$('#duration_field').css('display','none');
			
			$('#endTimeHour').prop('required',false); // disable required attribute if leave selected
			$('#endTimeMins').prop('required',false);
			$('#startTimeMins').prop('required',false);
			$('#startTimeHour').prop('required',false);
			
		}else{
			
			$('#leaveType_field').css('display','none'); // hide leave type list box
			$('#leaveType').prop('required',false);
			$('#leaveType option:first').val("None");
			
			
			//un selected the previous selected value
			$('#leaveType option:selected').removeAttr( "selected" );
			
			$('#timesheetStartTime_field').css('display','block'); // diaplay time fields,duration
			$('#timesheetEndTime_field').css('display','block');
			$('#duration_field').css('display','block');
			
			$('#endTimeHour').prop('required',true); // enable required attribute if leave not selected
			$('#endTimeMins').prop('required',true);
			$('#startTimeMins').prop('required',true);
			$('#startTimeHour').prop('required',true);
		}
 	});
 	
 	//method for Head office timesheet
 	
 	$('#applyLeaveCheckBoxHO').change(function () {
	
    	if($("#applyLeaveCheckBoxHO").prop('checked') == true){
    	
    		$('#leaveType_field').css('display','block'); // display leave type list box
			$('#leaveType').prop('required',true);
			$('#leaveTypeHO option:first').val("");
			
			$('#timesheetStartTime_field').css('display','none'); // hide time fields,duration
			$('#timesheetEndTime_field').css('display','none');
			$('#duration_field').css('display','none');
			$('#activity_field').css('display','none');
			$('#addButton_field').css('display','none');
			$('#headOfficeTimeSheetTable_field').css('display','none');
			
			$('#endTimeHour').prop('required',false); // disable required attribute if leave selected
			$('#endTimeMins').prop('required',false);
			$('#startTimeMins').prop('required',false);
			$('#startTimeHour').prop('required',false);
			$('#hOTextarea').prop('required',false);
			
		}else{
			
			$('#leaveType_field').css('display','none'); // hide leave type list box
			$('#leaveType').prop('required',false);
			$('#leaveTypeHO option:first').val("None");
			
			//un selected the previous selected value
			$('#leaveType option:selected').removeAttr( "selected" );
			$('#headOfficeTimeSheetTable_field').css('display','none');
			
			$('#timesheetStartTime_field').css('display','block'); // diaplay time fields,duration
			$('#timesheetEndTime_field').css('display','block');
			$('#duration_field').css('display','block');
			$('#activity_field').css('display','block');
			$('#addButton_field').css('display','block');
			$('#headOfficeTimeSheetTable_field').css('display','none');
			
			$('#endTimeHour').prop('required',true); // enable required attribute if leave not selected
			$('#endTimeMins').prop('required',true);
			$('#startTimeMins').prop('required',true);
			$('#startTimeHour').prop('required',true);
			$('#hOTextarea').prop('required',true);
		}
 	});
	
	// bind datepicker to onblur at Timesheet 
	
	 var elem= $("input:text[name='date'],[name='endDate']");
		 if (elem.hasClass('hasDatepicker')) {
		    var onClose = elem.datepicker('option','onClose');
		    elem.datepicker('option','onClose', function() {
		        durationFromStartEnd();
		        if (onClose) { onClose(elem.val()); }
		    });
		} else {
		    elem.bind('blur',durationFromStartEnd);
		}
	
	
	//display Daily Reports File Upload only when status is Submitted
	$('#drStatusId').on('change', function (e) {
	    var optionSelected = $("option:selected", this);
	    var valueSelected = this.value;
	    if(this.value === "SUBMITTED"){ // display file upload at DR only when the status is submitted
	    
	    	$('#dailyReportFile_field').css('display', 'block');
	    	//$("#dailyReportFile_field input").prop('required',true);
	    }else{
	    	$('#dailyReportFile_field').css('display', 'none');
	    	$("#dailyReportFile_field input").prop('required',false);
	    }
	});
	
	// Popover Pending Invoice Button in showDownload page of Invoice Inventory
	
	 $('[data-toggle="popover"]').popover({ html : true });  
	
	//In FnacyBox Datepicker is not coming like regularly.,For this Retain the below script
	
	$(document).on('click', '.datepickerFancyBox', function(){ 
	    if (!$(this).hasClass('hasDatepicker')) { 
	        $(this).datepicker({dateFormat: "dd/mm/yy"}); $(this).datepicker('show'); 
	    }
	}); 
	
	// icon lock change events
	
	$('.collapse').on('shown.bs.collapse', function(){
		$(this).parent().find(".icon-lock").removeClass("icon-lock").addClass("icon-unlock");
		}).on('hidden.bs.collapse', function(){
			$(this).parent().find(".icon-unlock").removeClass("icon-unlock").addClass("icon-lock");
	});
	
	

	
    // hide Approve/reject options in Timesheet for sk edit page select box
    
    			 
				//alert("hi");
			 	$("#statusForsk option[value='REJECTED']").hide();
			  	$("#statusForsk option[value='APPROVED']").hide();
			  	
			  	//if emp timesheet rejected,after emp changes duration for re-submission ,and forget to select status other than REJECTED
			  	 $('#durationForSK').blur(function() {
			  	 	var res = $('#statusForsk').val();
			  	 	if(res == 'REJECTED'){
			  	 		alert("Changing status From REJECTED to SUBMITTED..!");
			  	 		
			  	 		 $('#statusForsk').focus();
			  	 		  $('#statusForsk').val("SUBMITTED");
			  	 			
			  	 		}
    				});
    
    // calculating shift variance in shift page,auto populating that field
    
     actualMediaCollect = parseFloat($('input.actual_hidden').val());
     settleMediaCollect = parseFloat($('input.settle_hidden').val());
    $('input.actual').focus(function() {
      
    	var temp = this.value.trim()
    	
    	 if(!isNaN(temp)){
       actualMediaCollect = (actualMediaCollect-temp);
       }
       
    });
    $('input.actual').blur(function() {
  
    this.value=this.value.trim()
    
    if(!isNaN(this.value)){
   
   	 if (this.value === '') {
            $(this).val('0.00');
        }
        else{
        var temp = parseFloat(this.value.trim())
        	 actualMediaCollect = (actualMediaCollect+temp);
        	
        }
        
          $('input#shiftVariance').val((settleMediaCollect - actualMediaCollect ).toFixed(2));
      // alert("actualMediaCollect blur: " +actualMediaCollect);
    }
    else{
     alert("Please enter valid Number");
     this.focus();
    return false;
    }
       
    });
     $('input.settle').focus(function() {
    
    	var temp = this.value.trim()
    	
    	 if(!isNaN(temp)){
    
     	temp = parseFloat(this.value)
        settleMediaCollect = settleMediaCollect - temp;
        }
    });
    
    $('input.settle').blur(function() {
    
     this.value=this.value.trim()
    
    if(!isNaN(this.value)){
   
   	 if (this.value === '') {
            $(this).val('0.00');
        }
        else{
        var temp = parseFloat(this.value.trim())
        	  settleMediaCollect = settleMediaCollect + temp;
        	
        }
        
           $('input#shiftVariance').val((settleMediaCollect - actualMediaCollect).toFixed(2));
        // alert("settleMediaCollect blur: " +settleMediaCollect);
    }
    else{
     alert("Please enter valid Number");
     this.focus();
    return false;
    }
    
    }); 
    
  
  // null check for media diff Reason
  
    $('input.varReason').blur(function() {
     if (this.value === '') {
		    		$(this).val('default');
		        }
    }); 
   
    // check for NaN and null in sales heads , variance, payout(reg,safe) ,bank amount, payment tender,payroll
    
    $("input.saleshead, input.drRegPayout, input.shiftRegPayout, input.bankChequeAmt, input.bankCashAmt, input.shiftVariance, input.paymentTender, input.payAmt").blur(function() {
    	
    	this.value=this.value.trim()
    
    	if(!isNaN(this.value)){
    		if (this.value === '') {
		    		$(this).val('0.00');
		    }
    	}
    	else{
    	
    		alert("Please enter valid Number");
     		this.focus();
    		return false;
    	}
    
     });
    
    //hiding sortBy select box values based on category selected value in reportsHome.scala.html
    $('#byType').change(function() {
 		 if(this.value === 'invoices'){
 			document.getElementById('sortBy').innerHTML=document.getElementById('sortBySelectTagForSupp').innerHTML;
		 }
		 else if(this.value === 'sales' || this.value === 'media' || this.value === 'ReconciliationReport'){
		 	document.getElementById('sortBy').innerHTML=document.getElementById('sortBySelectTagForSales').innerHTML;
		 }
	});
    
});
jQuery(function($) {
     $('#boxLinks li a').click(function() {
    // alert("hi  ***");
     backCount--;
     //alert("backCount  ***"+backCount);
    })
   
});
$( document ).ready(function() {

$("#ajaxform").submit(function(e)
		{
		//alert("ajax call javascript");
		 var postData = $(this).serializeArray();
		 var formURL = $(this).attr("action");
		 console.log("form url"+formURL);
		 console.log("Post data"+postData);
		$.ajax(
    	{
   		
   		url : formURL,
		type: "POST",
        data : postData,
        success:function(data, textStatus, jqXHR) 
        {
           //alert(data);
           handleQueryResponse(data);
        },
        error: function(jqXHR, textStatus, errorThrown) 
        {
            alert("Please Select Date...!");
                  
        }
         });
        e.preventDefault(); //STOP default action
    	
		});

 $('#tab1').addClass("active");
 
  
 
  $("#tab1,#tab2,#tab3,#tab4,#tab5,#tab6,#tab7,#tab8,#tab9,#tab10").click(function(event){
   
   //  alert("clicked: "+event.target.nodeName)
     $('#acc4').css("height", 'auto');  		
 	 $('#acc4').css("background-color", '#fff');
 	 $('#acc4 h2').css("font-size", '1.6em');
 	  $('#acc4 h2').css("color", '#333');
 	  $('#boxLinks li a').removeClass("active");
 	  $(event.target).addClass("active");
 	 	 
  });
  $("#acc4,#acc4 p a").click(function(event){
   
   //  alert("clicked: "+event.target.nodeName)
     $('#acc4').css("height", 'auto');  		
 	 $('#acc4').css("background-color", '#fff');
 	 $('#acc4 h2').css("font-size", '1.6em');
 	  $('#acc4 h2').css("color", '#333');
 	
 	 	
 	 	
  });
  $("#acc1,#acc2,#acc3,#acc5").click(function(event){
   
    // alert("clicked: "+event.target.nodeName)
     $('#acc4').css("height", '2em');  		
 	 $('#acc4').css("background-color", '#333');
 	  $('#acc4 h2').css("font-size", '1em');
 	  $('#acc4 h2').css("color", '#ddd');
  });

  });  
  	
  	// verfiy password while changing password
  	
  	function verifyCurrentPassword(empid){
  	
  		var currentPassword = document.getElementById('currentPassword').value;
  		
  		// make ajax call to verify password
  		 myJsRoutes.controllers.Application.verifyCurrentPassword(empid,currentPassword).ajax({
    		success : function(data) {
    			
    			if(data == "SUCCESS"){
    				$("#currentPasswordMatch").removeClass("icon-remove");
					$("#currentPasswordMatch").addClass("icon-ok");
					$("#currentPasswordMatch").css("color","#00A41E");
					
					$("#password1").removeAttr("disabled", "disabled");
					$("#password2").removeAttr("disabled", "disabled");
					$("#currentPassword").attr("disabled", "disabled");
    			}else{
    				$("#currentPasswordMatch").removeClass("icon-ok");
					$("#currentPasswordMatch").addClass("icon-remove");
					$("#currentPasswordMatch").css("color","#FF0004");
    			}
    		}
		});
  	}
  	
  	// verifying Payout Requirments whhile submit shift i.e Payout must had either invoice File or reason for that payout 
  	
  	function verifyPayoutReqmnts(){
  	
  		var flag = false;
  		var payoutCount = $("#payTabForm > tbody > tr").length; // get no of payouts
  		if(payoutCount == 0){
  			flag = true;
  		}else{
  		
  			$('#payTabForm > tbody  > tr').each(function(){
  			
  					var item = $(this);
  					//alert(item.find('td:nth-child(5)').children().length); // 5th td is file
  					var file = item.find('input:file').val();
  					var reason = item.find('textarea').val();
  				 	//alert("payouts present count : "+(file == '')+"@@"+reason);
  				 	if( (file != '') || (reason != '')){
  				 		
  				 		flag = true;
  				 		
  				 	}else if(item.find('td:nth-child(5)').children().length == 2){ //if preview Invoice Button available
  				 	
  				 		flag = true;
  				 		
  				 	}else{
  				 	
  				 		alert("You must provide Either Invoice or Reason for Payout");
  				 		flag = false;
  				 	}
  			 	}
  			 );
  		}
  		
  		return flag;
  	}
  	
  	
  	// Check for Unique mail and Names in HeadOffice section ., Otherwise Give Alert
  	
  	function checkForUnique(param,type,mode){
  	
  		//alert("inside unique : "+param.value+": "+type+": "+mode);
  		
  		// make an AJAX call to find out it is Matched any Existing Data
  		
  		 myJsRoutes.controllers.Application.checkForUnique(param.value,type,mode).ajax({
    		success : function(data) {
    			
    			if(data == ""){
    				// do nothing if empty String
    			}else{
    			
    				alert(data);
    				param.focus();
    			}
    		}
		});
  	}
  
  	// display Selected File names in Invoice Inventory while uploading Files 
  	
  	 updateList = function() {
		  var input = document.getElementById('invoiceFiles');
		  var output = document.getElementById('fileList');
			
			// display block for span
			document.getElementById('displaySelected').style.display = "block";
			//alert(input.value);
		  output.innerHTML = '<ul>';
		  for (var i = 0; i < input.files.length; ++i) {
		    output.innerHTML += '<li>' + input.files.item(i).name + '</li>';
		  }
		  output.innerHTML += '</ul>';
	}
	
	function invoiceformValidation(){
	var file = document.getElementById("invoiceFiles");
	var flag =true;
	
	for (var i=0; i<file.files.length; i++) {
    //var ext = file.files[i].name.substr(-3);
    var fileName = file.files[i].name;
	var fileExt = fileName.split(".");
	var fileExtLength = fileExt.length;
	//alert("file array length is : "+fileExt.length);
	var fileType = fileExt[fileExtLength-1];
	//console.log("fileName is : "+fileName);
	console.log("file type is : "+fileType);
		if(fileName == "")
			$('#invoiceFiles').prop('required',true);
		else{
			if(!(fileType == 'jpeg' || fileType == 'pdf' || fileType == 'JPEG' || fileType == 'PDF')){
		     	alert("Only pdf and jpeg are allowed to upload");
				flag = false;
		    }
		}
	}
	return flag;
}
	
	
		// display Selected File Form details in Head Office forms while uploading Files 
  	
  	  function HoFormUpdateList(element) {
		var file = element;
		var flag =true;
	
		for (var i=0; i<file.files.length; i++) {
	    //var ext = file.files[i].name.substr(-3);
	    var fileName = file.files[i].name;
		var fileExt = fileName.split(".");
		var fileExtLength = fileExt.length;
		//alert("file array length is : "+fileExt.length);
		var HoFileType = fileExt[fileExtLength-1];
		//console.log("fileName is : "+fileName);
		console.log("file type is : "+HoFileType);

			if(HoFileType == 'pdf' || HoFileType == 'docx' || HoFileType == 'doc' || HoFileType == 'xlsx' || HoFileType == 'xls' ||
			HoFileType == 'PDF' || HoFileType == 'DOCX' || HoFileType == 'DOX' || HoFileType == 'XLSX' || HoFileType == 'XLS'){
				var selectedCategory=document.getElementById('formType').value;
			  	var input = document.getElementById('hoFormFiles');
			  	var output = document.getElementById('fileList');
			}
			else {
			var output = document.getElementById('fileList');
			//alert(output);
				flag = false;
			}
		}
		//console.log("flag is : "+flag);
		if(flag == false){
		
			alert("only pdf and jpeg file types allowed");
			element.value="";
			document.getElementById('displaySelected').style.display = "none";
			output.innerHTML = "";
		}
		else{
		
			// display block for span
			document.getElementById('displaySelected').style.display = "block";
			//alert(input.value);
			output.innerHTML = '<ul>';
			for (var i=0; i<file.files.length; i++) {
			   	output.innerHTML += '<li Style="font-size:16px;">'+"File Name :" + file.files.item(i).name + '</li>'+
			    	'<li Style="font-size:16px;">'+"Form Type :"+selectedCategory+ '</li>';
			}
			output.innerHTML += '</ul>';
		}
	}
	
	// Open FancyBOx for Invoice Inventory Processing System list page,when clicks on one link
	
	function OpenFancyBox(invoiceId){
		//alert(invoiceId);
		
		$(".fancyboxPDF").fancybox({
			openEffect: 'elastic',
			closeEffect: 'elastic',
			width:1200,
			height:1000,
			autoSize: true,
			type: 'iframe',
			loop : false,
			helpers : { 
			  overlay : {closeClick: false}, // disables close when outside clcik 
			  title : {  // put title on the top
                type: 'inside',
                position : 'top'
            	}
			},
			beforeShow: function () {
	            if (this.title) {
	                // New line
	                this.title += '<br />';
	                
	                // Add Form to Title
	                this.title += '<form action="/InvoiceInventory/list/move/'+invoiceId+'" method="post"><div class="table-responsive"><table style="background-color: white;border-color: #fff;" class="table table-bordered"><tr><td><div class="clearfix" id="category_field"><label for="category">Invoice Category</label><div class="input"><select id="invoiceCategoryAtFancyBox" name="category" onchange="disableSupplierListAtFancyBox()" required><option class="blank" value="">-- Choose --</option><option value="Fuel">Fuel</option><option value="Others">Others</option></select> <span class="help-inline"></span></div></div></td><td><div class="clearfix" id="invoiceDate_field"><label for="invoiceDate">Invoice Date</label><div class="input"><input type="text" class="datepickerFancyBox" name="invoiceDate" required><span class="help-inline"></span></div></div></td><td><div class="clearfix" id="paymentTerms_field"><label for="paymentTerms">Payment Terms</label><div class="input"><select id="paymentTermsFancyBox" name="paymentTerms" required><option class="blank" value="">-- Choose --</option><option value="CashPaid">CashPaid</option><option value="DD">DD</option><option value="FortNightly">FortNightly</option><option value="Weekly">Weekly</option><option value="Monthly">Monthly</option></select> <span class="help-inline"></span></div></div></td><td><div class="clearfix" id="supplier_field"><label for="supplier">Supplier</label><div class="input"><select id="supplierListAtFancyBox" name="supplier" required onfocus="selectBoxOptionsInFancyBox()"><option class="blank" value="">-- Choose a Supplier--</option></select> <span class="help-inline"></span></div></div></td><td><input type="submit" value="Done" class="btn btn-success">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/InvoiceInventory/delete/'+invoiceId+'" class="btn btn-danger" onclick="return confirm('+" 'Are You sure want to Delete ?' "+')">Delete</a></td></tr></table></div></form>';
	                
	               }
        	},
			iframe: {
				preload: false // fixes issue with iframe and IE
			}
		});	
	}	  

	// open fancyBox for Just View Uploaded Invoices at Store level and for Head Office people to view Processed Invoices
	
	function OpenFancyBoxForJustView(invoiceId){
		
		$(".fancyboxPDF").fancybox({
			openEffect: 'elastic',
			closeEffect: 'elastic',
			width:1200,
			height:1000,
			autoSize: true,
			type: 'iframe',
			loop : false,
			helpers : { 
			  overlay : {closeClick: false}, // disables close when outside clcik 
			},
			iframe: {
				preload: false // fixes issue with iframe and IE
			}
		});	
	}
	
	// open FancyBox for Head Office Invoices having company Names 
	
	function OpenFancyBoxForHeadOffice(invoiceId){
	
		$(".fancyboxPDF").fancybox({
			openEffect: 'elastic',
			closeEffect: 'elastic',
			width:1200,
			height:1000,
			autoSize: true,
			type: 'iframe',
			loop : false,
			helpers : { 
			  overlay : {closeClick: false}, // disables close when outside clcik 
			  title : {  // put title on the top
                type: 'inside',
                position : 'top'
            	}
			},
			beforeShow: function () {
	            if (this.title) {
	                // New line
	                this.title += '<br />';
	                
	                // Add Form to Title
	                this.title += '<form action="/InvoiceInventory/headOfficeList/headOfficeMove/'+invoiceId+'" method="post"><div class="table-responsive"><table style="background-color: white;border-color: #fff;" class="table table-bordered"><td><div class="clearfix" id="company_field"><label for="company">Company</label><div class="input"><select id="companyListAtFancyBox" name="company" required onfocus="selectBoxOptionsInFancyBoxForCompany()"><option class="blank" value="">-- Choose a Company--</option></select> <span class="help-inline"></span></div></div></td><td><div class="clearfix" id="invoiceDate_field"><label for="invoiceDate">Invoice Date</label><div class="input"><input type="text" class="datepickerFancyBox" name="invoiceDate" required><span class="help-inline"></span></div></div></td><td><div class="clearfix" id="invoiceCategory_field"><label for="invoiceCategory">Invoice Category</label><div class="input"><select name="invoiceCategory" required><option class="blank" value="">-- Choose --</option><option value="Electricity">Electricity</option><option value="Telephone">Telephone</option><option value="Insurance">Insurance</option><option value="Council Rates">Council Rates</option><option value="Others">Others</option></select> <span class="help-inline"></span></div></div></td><td><input type="submit" value="Done" class="btn btn-success">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="/InvoiceInventory/delete/'+invoiceId+'" class="btn btn-danger" onclick="return confirm('+" 'Are You sure want to Delete ?' "+')">Delete</a></td></tr></table></div></form>';
	                
	               }
        	},
			iframe: {
				preload: false // fixes issue with iframe and IE
			}
		});
	
	}
	
	// open Fnacy for payment done option
	
	function OpenFancyBoxForPaymentDone(invoiceId,paymentTerms,filter,selectedCategory){
	console.log("filter value :"+filter);
	console.log("selectedCategory :"+selectedCategory);
	if(selectedCategory == ""){
	selectedCategory="OTHER";
	}
	
		var comp=$('#paymentSelectedCompany').val();
	   	var category=$('#paymentSelectedInvoiceType').val();
	
		$(".fancyboxPDF").fancybox({
			openEffect: 'elastic',
			closeEffect: 'elastic',
			width:1200,
			height:1000,
			autoSize: true,
			type: 'iframe',
			loop : false,
			helpers : { 
			  overlay : {closeClick: false}, // disables close when outside clcik 
			  title : {  // put title on the top
                type: 'inside',
                position : 'top'
            	}
			},
			beforeShow: function () {
	            if (this.title) {
	                // New line
	                this.title += '<br />';
	                
	                // Add Form to Title
	                this.title += '<form action="/InvoiceInventory/paymentList/paymentMove/'+invoiceId+'/'+paymentTerms+'/'+comp+'/'+selectedCategory+'/'+filter+'" method="post"><div class="table-responsive"><table style="background-color: white;border-color: #fff;" class="table table-bordered"><td><center><input type="submit" value="Payment Done" onclick="return confirm('+" 'Are You sure .. Payment Done ..?' "+')" class="btn btn-success">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" onclick="$.fancybox.close();" value="Cancel" class="btn btn-danger"/></center></tr></table></div></form>';
	                
	               }
        	},
			iframe: {
				preload: false // fixes issue with iframe and IE
			}
		});
	
	}
	
	// event methods 
	
    function getPayoutSuppliers(payoutType){
     
    
     strKey = document.getElementById("paysupplierid").value;
   
  myJsRoutes.controllers.Suppliers.getSuppliers(strKey,"").ajax({
    success : function(data) {
    	
    	//alert(data[0].id +", "+data[0].name);
    	 parsePayoutSupplierData(data,payoutType);
    }
	});
  }
  
  function parsePayoutSupplierData(data,payoutType){
  		$('#paytabList tbody').empty();
  		
  		
		if ( data != null ){
		
			  
				 $('#suppliersid').empty();
					for( i=0;i<data.length;i++) {
						
						$('#suppliersid').append($('<option>').text(data[i].name+"-"+data[i].abn).attr('value', data[i].id));
					
						
					}
		}
  }
  
  function delRow(pid)
  {
  alert("payout");
  var current = window.event.srcElement;
    	  //alert(current.parentElement.tagName);
    //here we will delete the line
    while ( (current = current.parentElement)  && current.tagName.toLowerCase() !="tr");        
         current.parentElement.removeChild(current);
    	 
 	 deletePayout(pid);
   
  }
  
   function deletePayout(pid){
   
    //alert("inside deletePayout");
    myJsRoutes.controllers.Application.deletePayout(pid).ajax({
    success : function(data) {
    	
    	//alert("Deleted Payout "+pid);
    	
    	 
    }
	});
   
   }
  
  
    
   function delPayrollRow(pid)
  {
 // alert("delPayrollRow "+pid);
  var current = window.event.srcElement;
    	  //alert(current.parentElement.tagName);
    //here we will delete the line
    while ( (current = current.parentElement)  && current.tagName.toLowerCase() !="tr");        
         current.parentElement.removeChild(current);
    	 
 	 deletePayroll(pid);
   
  }
  
   
   function deletePayroll(pid){
   
  //  alert("inside deletePayroll payroll id: "+pid);
    myJsRoutes.controllers.Application.deletePayroll(pid).ajax({
    success : function(data) {
    	
    	//alert("Deleted Payroll after ajax call "+pid);
    	
    	 
    }
	});
   
   }
   
   
   function addPayrollbyEmployee(){
   
   }
   
  function addSuppDetails(payoutType,strKey)
	{
	supp_data = $('#suppliersid option:selected').attr('value');
	 myJsRoutes.controllers.Application.getPayout(payoutType,strKey,supp_data).ajax({
    success : function(data) {
      val = $('#suppliersid option:selected').text(); 
  	  res = val.split("-");
    //	alert(data);
     newRowContent = "<tr><td>"+res[0]+" </td><td>"+res[1]+"</td><td><input type=\"text\" name=\"" +data + "_Amt\"  onblur=\"payAmtNullCheck(this)\" id=\"" +data + "_Amt\" value=\"0.00\"></td><td><select name=\"" +data + "_Exp\" id=\"" +data + "_Exp\"></select> </td><td><input type=\"file\" name=\"" +data + "_File\" id=\"" +data + "_File\"></td><td><textarea name=\"" +data + "_reason\" id=\"" +data + "_reason\" cols=\"30\" rows=\"4\"></textarea></td><td><INPUT TYPE=\"Button\" CLASS=\"btn btn-primary\" onClick=\"delRow('"+data+"')\" VALUE=\"Delete Row\"></td></tr>";
		// newRowContent = "<tr><td>"+res[0]+" </td><td>"+res[1]+"</td></tr>";
		 $("#payTabForm tbody").append(newRowContent);
		 var sel_id = data+"_Exp";
		 addSelect(sel_id)
    }
	});
			
	}
	
	function addPayroll(drId)
	{
		
		var empId=document.getElementById("empId").value;
		
		var weekStartDate = document.getElementsByName("startdate")[0].value;
		 weekStartDate = weekStartDate.replace(/[&\/\\#,+()$~%.'":*?<>{}]/g,'-');
		
		 myJsRoutes.controllers.Application.addPayroll(empId,weekStartDate,drId).ajax({
    	success : function(data) {
    	
    //	alert(data);
    if(data!="null"){
      res = data.split(":");
      payrollId=res[0];
       var weekStartDate1 = weekStartDate.replace('-','/');
       var weekStartDate2 = weekStartDate1.replace('-','/');
     // alert("inside add payroll method: "+payrollId);
     newRowContent = "<tr><td>"+res[1]+" </td><td>"+weekStartDate2+"</td><td>"+res[2]+"</td><td>"+res[3]+"</td><td>"+res[4]+":"+res[5]+"</td><td>"+res[6]+"</td><td>"+res[7]+"</td><td><input type=\"text\" name=\"payAmt_" +payrollId+" \"  value=\"0.00\" onblur=\"payAmtNullCheck(this)\" class=\"payAmt\" id=\"payAmt_" +payrollId+" \"></td><td><INPUT TYPE=\"Button\" CLASS=\"btn btn-success\" onClick=\"viewTimesheet('"+empId+"','"+weekStartDate+"')\" VALUE=\"View Summary\"></td><td><INPUT TYPE=\"Button\" CLASS=\"btn btn-primary\" onClick=\"delPayrollRow('"+payrollId+"')\" VALUE=\"Delete\"></td></tr>";
		// newRowContent = "<tr><td>"+res[0]+" </td><td>"+res[1]+"</td></tr>";
		 $("#payWageForm tbody:last").append(newRowContent);
		}
		else{
			alert("Payroll for Employee  has already been created!!! ");
		}
		
    }
	});
		
	
	}
	
	
		function addAccountSelect(id){
	
	//	alert(id);
		var first = document.getElementById('accHolderId');
		var options = first.innerHTML;
		//alert(first.innerHTML);
		
		var second = document.getElementById(id);
		var options = second.innerHTML + options;
		
		second.innerHTML = options;
 		//alert(second.innerHTML);
	}	
	
	var extendedId=0;
	
	/*function addAccountHolder(accHolderId,salesHeadId,mediaId)
	{
	 extendedId=extendedId+1;
	 //alert("inside account holder");
	 var accHolder=document.getElementById("accHolderId").value;
	 var salesHead=document.getElementById("salesHeadId").value;
	 var mediaTender=document.getElementById("mediaId").value;
	 //alert(mediaTender);
	  myJsRoutes.controllers.Application.addAccountHolder(accHolder,salesHead,mediaTender).ajax({
 		 type:'POST',
    	success : function(data)    
    	 {
    	alert(data);
    	 if(data!="null"){
    	 	alert("data is not null");
      		var res = data.split(":");
      		accountHolderIdId=res[0];
      		console.log("===="+accountHolderIdId);
      		alert("inside addAccountHolder method: "+accountHolderIdId);
      		 newRowContent = "<tr><td>"+res[0]+"</td><td>"+res[2]+"</td><td><input type=\"text\" name=\"account_" +accountHolderIdId+" \"  value=\"0.00\" onblur=\"accSalesHead(this)\" class=\"account\" id=\"account_" +accountHolderIdId+" \"</td><td>"+res[4]+"</td></tr>";
      		$("#accountHolderForm tbody").append(newRowContent);
      		
      	
		 }
		else{
			alert("Already added!!! ");
		}
		
    }
	});
 }*/
 
 function accSalesHead(aid){
	
		aid.value=aid.value.trim()
    
    	if(!isNaN(aid.value)){
    		if (aid.value === '') {
		    		$(aid).val('0.00');
		    }
    	}
    	else{
    	
    		alert("Please enter valid Number");
     		aid.focus();
    		return false;
    	}
	}	
	
	
	function payAmtNullCheck(pid){
	
		pid.value=pid.value.trim()
    
    	if(!isNaN(pid.value)){
    		if (pid.value === '') {
		    		$(pid).val('0.00');
		    }
    	}
    	else{
    	
    		alert("Please enter valid Number");
     		pid.focus();
    		return false;
    	}
	}	
	
	
	
	
	function addSelect(id){
	
	//	alert(id);
		var first = document.getElementById('expId');
		var options = first.innerHTML;
		//alert(first.innerHTML);
		
		var second = document.getElementById(id);
		var options = second.innerHTML + options;
		
		second.innerHTML = options;
	//alert(second.innerHTML);
	}	
	
	// to fill list box options in fancybox supplier list
	
	function selectBoxOptionsInFancyBox(){
		
		var first = document.getElementById('supplierListByStoreForInvoice');
		var options = first.innerHTML;
		//alert(first.innerHTML);
		
		var second = document.getElementById('supplierListAtFancyBox');
		second.innerHTML = options;
	
	
	}
	
	// to fill list box options in fancybox Company list
	
	function selectBoxOptionsInFancyBoxForCompany(){
	
		var first = document.getElementById('companyListForInvoice');
		var options = first.innerHTML;
		//alert(first.innerHTML);
		
		var second = document.getElementById('companyListAtFancyBox');
		second.innerHTML = options;
	
	}
	
	// to disable Supplier List box & Payment Term  at fancy Box when select Invoice Category as "Fuel"
	
	function disableSupplierListAtFancyBox(){
		
		var res = document.getElementById('invoiceCategoryAtFancyBox');
		if(res.options[res.selectedIndex].value == 'Fuel'){
			
			document.getElementById('supplierListAtFancyBox').disabled=true;
			document.getElementById('paymentTermsFancyBox').disabled=true;
		}else{
			
			document.getElementById('supplierListAtFancyBox').disabled=false;
			document.getElementById('paymentTermsFancyBox').disabled=false;
		}
	}
	
	// enable category selection option at Invoice payment page
	
	function enableCategoryAtpaymentPage(){
		
		$("#invoiceCategoryAtPaymentPage option[value='']").attr("selected","selected");
	   	$("#paymentTermsAtpaymentPage option[value='ALL']").attr("selected","selected");
	   	$("#paymentTermsAtpaymentPage_field").css('display','none');
		var res = document.getElementById('invoiceStoreList');
		if(res.options[res.selectedIndex].value == ''){
		console.log("inside if");
			
			$('#invoiceCategoryAtPaymentPage_field').css('display','none');
			$('#invoiceCategoryAtPaymentPage').prop('required',false);
			
			$('#paymentTermsAtpaymentPage_field').css('display','none');
			$('#paymentTermsAtpaymentPage').prop('required',false);
		}else{
					console.log("inside else");
			
			$('#invoiceCategoryAtPaymentPage_field').css('display','block');
			$('#invoiceCategoryAtPaymentPage').prop('required',true);
		}
	}
	
	
	
	
	// to disable  Payment Term  at Invoice payment page when select Invoice Category as "Fuel"
	
	function disablePaymentTermsAtpaymentPage(){
		
		var res = document.getElementById('invoiceCategoryAtPaymentPage');
		if(res.options[res.selectedIndex].value == 'Fuel'){
				//alert("iiii");
				console.log(res.options[res.selectedIndex].value);
			$('#paymentTermsAtpaymentPage_field').css('display','none');
			$('#paymentTermsAtpaymentPage').prop('required',false);
		}else{
			
			$('#paymentTermsAtpaymentPage_field').css('display','block');
			$('#paymentTermsAtpaymentPage').prop('required',true);
			
		}
	}
	
	// populate store options in Payment Invoice page based on selected Comapny
	
	function populateStoreOptionsInInvoicePayments(){
	   $("#invoiceCategoryAtPaymentPage option[value='']").attr("selected","selected");
	   $("#invoiceCategoryAtPaymentPage_field").css('display','none');
	   $("#paymentTermsAtpaymentPage option[value='ALL']").attr("selected","selected");
	   $("#paymentTermsAtpaymentPage_field").css('display','none');
		var companyId = document.getElementById('companyId').value;
		//alert(companyId);
		myJsRoutes.controllers.Application.storesByCompany(companyId).ajax({
   			 success : function(data) {
    			var ids = data[0];
    			var names = data[1];
    			$('#invoiceStoreList').empty();
    			$('#invoiceStoreList').append($('<option>').text("--Choose a Store--").attr('class', "blank").attr('value',""));
    			for( i=0;i<ids.length;i++) {
						
						$('#invoiceStoreList').append($('<option>').text(names[i]).attr('value', ids[i]));
				}
    		}
		});
		
	}
	
	// calculate duration of timesheet based on start and End time
	
	function durationFromStartEnd(){
	
		var startHours = document.getElementById('startTimeHour');
		var startMins = document.getElementById('startTimeMins');
		var endHours = document.getElementById('endTimeHour');
		var endMins = document.getElementById('endTimeMins');
		var startDate = document.getElementsByName('date')[0];
		var endDate = document.getElementsByName('endDate')[0];
		
		
		
		if( (!startHours.value == "" ) && (!startMins.value == "" ) && (!endHours.value == "" ) &&  (!endMins.value == "" ) ){ // if every thing is not null
			
			if( (startDate.value != null) && (endDate.value != null) ){
			
				var sdate = startDate.value.split("/");
				var edate = endDate.value.split("/");
				
				var sd = new Date(sdate[2], sdate[1] - 1, sdate[0], startHours.value, startMins.value, 00, 00);
				var ed = new Date(edate[2], edate[1] - 1, edate[0], endHours.value, endMins.value, 00, 00);
				
				
				// check if end time is Greate than start time or not
				if(sd.getTime() > ed.getTime()){ // if start Time greater than end time
					
					
					if(sd.getDate() > ed.getDate() ){ // if start date is greater than end date
					
						alert("End Date should be Greater Than Start Date");
						document.getElementsByName('endDate')[0].focus();
						return false;
					}
					
					if(sd.getDate() == ed.getDate()){ // if both dates are equal
						
						if(startHours.value > endHours.value ){ // if both dates are equal check for hours
						
							alert("End time should be Greater Than Start Time");
							document.getElementById('endTimeHour').focus();
							return false;
					
						}
						if(startHours.value == endHours.value ){ // if both hours are equal check for mins
						
							if(startMins.value > endMins.value){
							
								alert("End time should be Greater Than Start Time");
								document.getElementById('endTimeMins').focus();
								return false;
							}
						}
					}
					return false; // if sd.getTime() > ed.getTime()
										
				}else{ // if time is perfect , calculate duration
					
					//console.log("sd: "+sd);
					//console.log("ed: "+ed.getTime());
					var duration = ed.getTime() -sd.getTime();
					var milliseconds = parseInt((duration%1000)/100)
            			, seconds = parseInt((duration/1000)%60)
            			, minutes = parseInt((duration/(1000*60))%60)
            			, hours = parseInt((duration/(1000*60*60)));

        			hours = (hours < 10) ? "0" + hours : hours;
        			minutes = (minutes < 10) ? "0" + minutes : minutes;
        			
        			//console.log("duration is: "+ hours+":"+minutes);
 					
 					//duration for Sm and sk roles 
					if(document.getElementById('durationForSK') != null){ // check for Sk timesheet ,sk duration have id  "durationForSK"
						//alert("sk");
						document.getElementById('durationForSK').value = hours+":"+minutes ;
					}else{													// check for SM timesheet , sm have id "duration"
						//alert("sm");
						document.getElementById('duration').value = hours+":"+minutes ;
					}
					
				} // else
			} //if(),date fields null check
			
		} // if(), time fileds null check
	} // function
	
	
	
	
	// calculate duration of timesheet based on start and End time
	
	function durationHeadOfficeFromStartEnd(){
	
		var startHours = document.getElementById('startTimeHour');
		var startMins = document.getElementById('startTimeMins');
		var endHours = document.getElementById('endTimeHour');
		var endMins = document.getElementById('endTimeMins');
		var startDate = document.getElementsByName('date')[0];
		var endDate = document.getElementsByName('date')[0];
		var date=document.getElementById("hoDate").value;
		var enddate=document.getElementById("hoEndDate").value;
		
		//console.log("startDate : "+date);
		//console.log("endDate : "+enddate);
		
		//if(date == "" || date.length == 0){
			// alert("Please select date");
			//document.getElementById("hoDateErrorLabel").style.display="block";
			//document.getElementById("hoDate").focus();
		//}//else
		//{
		
		
		if( (!startHours.value == "" ) && (!startMins.value == "" ) && (!endHours.value == "" ) &&  (!endMins.value == "" ) ){ // if every thing is not null
			
			if( (startDate.value != null) && (endDate.value != null) ){
			
				var sdate = startDate.value.split("/");
				var edate = endDate.value.split("/");
				
				var sd = new Date(sdate[2], sdate[1] - 1, sdate[0], startHours.value, startMins.value, 00, 00);
				var ed = new Date(edate[2], edate[1] - 1, edate[0], endHours.value, endMins.value, 00, 00);
				
				//console.log("sated id: "+sdate);
				//console.log("edate id: "+edate);
				
				//console.log("sd id: "+sd);
				//console.log("ed id: "+ed);
				
				
				// check if end time is Greate than start time or not
				if(sd.getTime() > ed.getTime()){ // if start Time greater than end time
					
					
					if(sd.getDate() > ed.getDate() ){ // if start date is greater than end date
					
						alert("End Date should be Greater Than Start Date");
						document.getElementsByName('endDate')[0].focus();
						return false;
					}
					
					if(sd.getDate() == ed.getDate()){ // if both dates are equal
						
						if(startHours.value > endHours.value ){ // if both dates are equal check for hours
						
							alert("End time should be Greater Than Start Time");
							document.getElementById('endTimeHour').focus();
							return false;
					
						}
						if(startHours.value == endHours.value ){ // if both hours are equal check for mins
						
							if(startMins.value > endMins.value){
							
								alert("End time should be Greater Than Start Time");
								document.getElementById('endTimeMins').focus();
								return false;
							}
						}
					}
					return false; // if sd.getTime() > ed.getTime()
										
				}else{ // if time is perfect , calculate duration
					
					//console.log("sd: "+sd);
					//console.log("ed: "+ed.getTime());
					var duration = ed.getTime() -sd.getTime();
					var milliseconds = parseInt((duration%1000)/100)
            			, seconds = parseInt((duration/1000)%60)
            			, minutes = parseInt((duration/(1000*60))%60)
            			, hours = parseInt((duration/(1000*60*60)));

        			hours = (hours < 10) ? "0" + hours : hours;
        			minutes = (minutes < 10) ? "0" + minutes : minutes;
        			
        			//console.log("duration is: "+ hours+":"+minutes);
 					
 					//duration for Sm and sk roles 
					if(document.getElementById('durationForSK') != null){ // check for Sk timesheet ,sk duration have id  "durationForSK"
						//alert("sk");
						document.getElementById('durationForSK').value = hours+":"+minutes ;
					}else{													// check for SM timesheet , sm have id "duration"
						//alert("sm");
						document.getElementById('duration').value = hours+":"+minutes ;
					}
					
				} // else
			} //if(),date fields null check
			
		} // if(), time fileds null check
		//}
	} // function
	
	
	// check for Duplicate Time sheet, while adding a new Timesheet
	
	function checkTimesheetExists(){
	
		
		var startHours = document.getElementById('startTimeHour').value;
		var startMins = document.getElementById('startTimeMins').value;
		var endHours = document.getElementById('endTimeHour').value;
		var endMins = document.getElementById('endTimeMins').value;
		var date = document.getElementsByName("date")[0].value;
		var endDate = document.getElementsByName("endDate")[0].value;
		var empId = document.getElementById('empid').value;
		var storeId =document.getElementsByName("firmid")[0].value;
		var leaveType = document.getElementById('leaveType').value;
		
		var flag = false ;
		
		if(leaveType == 'None'){
		
			//make an Ajax call
			 myJsRoutes.controllers.Application.checkTimesheetExists(storeId,empId,date,endDate,leaveType,startHours,startMins,endHours,endMins).ajax({
	   			 success : function(data) {
	    		
	    		//alert(data);
	    		//return false;
	    		//alert("timesheet data==="+data);
	    			if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
	    				alert("Already One Timesheet is There for this Employee for this date : "+date+" . Modify The Existing one or Choose Different Time or Date");
	    				flag = false;
	    			}else if(data == -1){
	    				alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates..");
	    			}
	    			else{
	    				//alert("Else");
	    				document.getElementById('timesheetForm').submit(); // submit the form here
	    				flag = true;
	    			}
	    		}
			});
		
		}else{
			
			//make an Ajax call
			 myJsRoutes.controllers.Application.checkTimesheetExistsForLeave(storeId,empId,date,endDate).ajax({
	   			 success : function(data) {
	    		
	    		//alert(data);
	    		//return false;
	    		
	    			if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
	    				alert("Already One Timesheet is There for this Employee for this date : "+date+" or Range . Modify The Existing one or Choose Different Time or Date");
	    				flag = false;
	    			}else if(data == -1){
	    				alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates..");
	    			}
	    			else{
	    				//alert("Else");
	    				document.getElementById('timesheetForm').submit(); // submit the form here
	    				flag = true;
	    			}
	    		}
			});
		}
		
		
		return flag;
	}
	
	
	// check existing timesheets in edit page
	function checkTimesheetExistsEditPage(tid){ // in edit compare timesheets other than this tid
		
		var startHours = document.getElementById('startTimeHour').value;
		var startMins = document.getElementById('startTimeMins').value;
		var endHours = document.getElementById('endTimeHour').value;
		var endMins = document.getElementById('endTimeMins').value;
		var date = document.getElementsByName("date")[0].value;
		var endDate = document.getElementsByName("endDate")[0].value;
		var empId = document.getElementById('empid').value;
		var storeId =document.getElementsByName("firmid")[0].value;
		var leaveType = document.getElementById('leaveType').value;
		
		var flag = false ;
		
		if(leaveType == 'None'){
		
			//make an Ajax call
			 myJsRoutes.controllers.Application.checkTimesheetExistsEditPage(storeId,empId,date,endDate,leaveType,startHours,startMins,endHours,endMins,tid).ajax({
	   			 success : function(data) {
	    		
	    		//alert(data);
	    		//return false;
	    		
	    			if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
	    				alert("Already One Timesheet is There for this Employee for this date : "+date+" . Modify The Existing one or Choose Different Time or Date");
	    				flag = false;
	    			}else if(data == -1){
	    				alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates..");
	    			}
	    			else{
	    				//alert("Else");
	    				document.getElementById('timesheetForm').submit(); // submit the form here
	    				flag = true;
	    			}
	    		}
			});
		
		}else{
			
			//make an Ajax call
			 myJsRoutes.controllers.Application.checkTimesheetExistsForLeaveEditPage(storeId,empId,date,endDate,tid).ajax({
	   			 success : function(data) {
	    		
	    		//alert(data);
	    		//return false;
	    		
	    			if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
	    				alert("Already One Timesheet is There for this Employee for this date : "+date+" or Range . Modify The Existing one or Choose Different Time or Date");
	    				flag = false;
	    			}else if(data == -1){
	    				alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates..");
	    			}
	    			else{
	    				//alert("Else");
	    				document.getElementById('timesheetForm').submit(); // submit the form here
	    				flag = true;
	    			}
	    		}
			});
		}
		
		
		return flag;
	}
	
	
	
	function addSelectForSupplier(id1,id2){
	
		//alert(id1+":"+id2);
		var first = document.getElementById('paymentTerms');
		//alert(first)
		var options = first.innerHTML;
		//alert(first.innerHTML);
		
		var second = document.getElementById(id1);
		var options = second.innerHTML + options;
		
		second.innerHTML = options;
	//alert(second.innerHTML);
	
	//	alert(id);
		var first = document.getElementById('paymentMode');
		var options = first.innerHTML;
		//alert(first.innerHTML);
		
		var second = document.getElementById(id2);
		var options = second.innerHTML + options;
		
		second.innerHTML = options;
	//alert(second.innerHTML);
	
	}	
  
  function getSuppliers(){
  var strKey = document.getElementById("supplierid").value;
  myJsRoutes.controllers.Suppliers.getSuppliers(strKey,"HeadOffice").ajax({
    success : function(data) {
    	
    	//alert(data[0].id +", "+data[0].name);
    	 parseSupplierData(data);
    }
	});
  }
  
  function parseSupplierData(data){
  $('#similarSuppliersid').empty();
		//	alert(data.length);			
			if ( data != null ){
			for( i=0;i<data.length;i++) {
				$('#similarSuppliersid').append($('<option>').text(data[i].name+"-"+data[i].description).attr('value', data[i].id));
			
				}
			}
  
  }
  
  function similarSupplierChange()
	{
           
      
   var   supp_data = $('#similarSuppliersid option:selected').attr('value');
   var   val = $('#similarSuppliersid option:selected').text(); 
   
   if (validate(supp_data,$("input[name='supplierslist[]']"))){
 
	
  		 var newRowContent = "<tr><td><input type=\"checkbox\" name='supplierslist[]' id=\"" + supp_data + "\" value=\"" + supp_data + "\">" + val + "</td><td>Payment Terms<select name=\"" +supp_data + "_paymentTerms\" id=\"" +supp_data + "_paymentTerms\"></select></td><td>Payment Mode<select name=\"" +supp_data + "_paymentMode\" id=\"" +supp_data + "_paymentMode\"></select></td></tr>";

			$("#cblist tbody").append(newRowContent);
			
			var terms = supp_data+"_paymentTerms";
			var mode = supp_data+"_paymentMode";
		 	addSelectForSupplier(terms,mode);
	 }
	 else{
	 	alert("Supplier "+val+ "already selected!!");
	 	
	 }
	}
	
	
	 function similarSupplierChange1(storeId)
	{
           
       var  supp_data = $('#similarSuppliersid option:selected').attr('value');
       var  val = $('#similarSuppliersid option:selected').text();
       val =val.split("-");
      if (validateForSuppliers(supp_data)){
      
	 myJsRoutes.controllers.Application.getSupplierMapping(storeId,supp_data).ajax({
    success : function(data) {
  
  	  res = data.split("-");
    	//alert(res[0]+":"+res[1]+":"+res[2]);
     var newRowContent = "<tr><td>"+res[1]+"</td><td>"+res[2]+"</td><td><select name=\"" +res[0]+ "_paymentTerms\" id=\"" +res[0]+ "_paymentTerms\" ></select></td><td><select name=\"" +res[0]+ "_paymentMode\" id=\"" +res[0]+ "_paymentMode\"></select></td><td><INPUT TYPE=\"Button\" id=\"b_"+supp_data+"\" CLASS=\"btn btn-primary\" onClick=\"delSupplierMapping('"+res[0]+"')\" VALUE=\"Delete\"></td></tr>";
		
		$("#cblist tbody").append(newRowContent);
		 var terms = res[0]+"_paymentTerms";
			var mode = res[0]+"_paymentMode";
		 	addSelectForSupplier(terms,mode);
    		}
		});
	}
      else{
	 	alert("Supplier "+val[0]+ " already selected!!");
	 }
	 
	}
	
	
	
	 function delSupplierMapping(pid)
  {
 // alert("delPayrollRow "+pid);
  var current = window.event.srcElement;
    	  //alert(current.parentElement.tagName);
    //here we will delete the line
    while ( (current = current.parentElement)  && current.tagName.toLowerCase() !="tr");        
         current.parentElement.removeChild(current);
    	 
 	 delSupplierMappingRow(pid);
   
  }
  
   
   function delSupplierMappingRow(pid){
   
  //  alert("inside deletePayroll payroll id: "+pid);
    myJsRoutes.controllers.Application.deleteSupplierMapping(pid).ajax({
    success : function(data) {
    	
    	//alert("Deleted Payroll after ajax call "+pid);
    	
    	 
    }
	});
   
   }
  
					

 function validate(id,inputdata) {
 //alert("Inside Validate function" ); 
 
 //alert(inputdata.length)
 

 for (counter=0; counter < inputdata.length; counter++)
 	 {
 	 	//alert("inputdata"+inputdata[counter].value+"compared to id"+id); 
        	if (inputdata[counter].value== id) {
       			return false;
       		}
      		
     }
     
 	return true;
   }

function validateForSuppliers(id) {
 	console.log("Inside validateForSuppliers function" ); 
 	id="b_"+id;
 	//alert(document.getElementById(id));
	//alert(id);
	var temp=document.getElementById(id);
        	if (temp!=null) {
       			return false;
       		}
   	return true;
   }
  
		
   function callTheJsonp(url)
	 {
		// the url of the script where we send the asynchronous call
				      
        //      alert(url);
		// create a new script element
		var script = document.createElement('script');
		// set the src attribute to that url
		script.setAttribute('src', url);
		// insert the script in out page
		 document.getElementsByTagName('head')[0].appendChild(script);
        //     alert("end of callTheJsonp");
	}

			// this function should parse responses.. you can do anything you need..
			// you can make it general so it would parse all the responses the page receives based on a response field
			function parseRequest(data)
			{
              //alert("parseRequest" + data);
              if ( data != null  ){
              		if(data.length>0){
						getSimilar(data);
						}
						else{
						alert("Please enter valid address");
						
						return
						}
					}
					
				try // try to output this to the javascript console
				{
					console.log(response);
				}
				catch(an_exception) // alert for the users that don't have a javascript console
				{
				//	alert('product id ' + response.item_id + ': quantity = ' + response.quantity + ' & price = ' + response.price);
				}
			}
                  
                  
                  
                  function getParsedAddressLine()
					{
						//location.href="http://api.addressify.com.au/address/autoComplete?api_key={430be3d0-2143-43a8-b96f-ddcbcc28b1b0}&term="+document.getElementById("addressid").value;
                      
                     // alert(document.getElementById("addresslineid").value);
                       var url = "http://api.addressify.com.au/address/addressLineAutoComplete?api_key={430be3d0-2143-43a8-b96f-ddcbcc28b1b0}&term="+document.getElementById('addresslineid').value+"&jsonp=parseRequest";
                       
                      callTheJsonp(url)  ; 
                   }
                  
				    function getParsedAddress()
					{
						
                    
                           var url = "http://api.addressify.com.au/address/autoComplete?api_key={430be3d0-2143-43a8-b96f-ddcbcc28b1b0}&term="+document.getElementById('addressid').value+"&jsonp=parseRequest";
                       
                      callTheJsonp(url)  ; 
						 
					}
  	function getSimilar(data)
					{
						$('#similarAddressesid').empty();
						
							if ( data != null ){
							$.each(data, function(i, value) {
								$('#similarAddressesid').append($('<option>').text(value).attr('value', value));
							});
							}
					}

					function similarAddressesChange()
					{
                      addLine = document.getElementById("similarAddressesid").value;
                       var res = addLine.split(",");
                  //  alert("split address value"+res[0]);
                      
                        document.getElementById("addressid").value= res[0];
						var num = res[0].split(" ");
						document.getElementById("number").value= num[0];
						
						 // alert("split num Length "+num.length);
						  
						temp = num[1];
						 for(i=2;i<num.length;i++){
						 temp = temp+" "+num[i];
											
						}
						document.getElementById("street").value= temp;
                      
               //  alert("splite address value2"+res[1]);
                 
                      res = res[1];
                      res = res.split(" ");
                      //alert("split res Length "+res.length);
                      if(res.length==5){
                      	document.getElementById("city").value = res[0]+" "+res[1]+" "+res[2];
                          
						  document.getElementById("state").value = res[3];
						  document.getElementById("postalCode").value =res[4];
						 
						 }
						 else if(res.length==4){
						 
						document.getElementById("city").value = res[0]+" "+res[1];
						  document.getElementById("state").value = res[2];
						  document.getElementById("postalCode").value =res[3];
						 }
						 else{
						  document.getElementById("city").value = res[0];
						  document.getElementById("state").value = res[1];
						  document.getElementById("postalCode").value =res[2];
						 }
						 document.getElementById("country").value ="AUS";
					}
					//making disable media type in shift add account holders tab
			function enableMediaType(){
					//alert("media");
					var res = document.getElementById('salesHeadId');
					if(res.options[res.selectedIndex].value == '2'){
						
						$('#mediaId').css('display','none');
						$('#mediaIdHead').css('display','none');
						$('#mediaIdHead').css('display','none');
						$('#mediaId').prop('required',false);
						
					}else{
						$('#mediaId').css('display','block');
						$('#mediaIdHead').css('display','block');
						$('#mediaId').prop('required',true);
						$("#mediaId").val("none");
						}
					}
	//Funtion wriiten for to prevent the submit button from double clicks in TimeSheet show page
	
				$(function()
					{
					  $('#timesheetForm').submit(function(){
					    $("input[type='submit']", this).val("Please Wait...")
					      .attr('disabled', 'disabled');
					      setTimeout('$("#btnSubmitInTimesheet").removeAttr("disabled")', 2500);
					         $("input[type='submit']", this).val("Submit")
					    return true;
					  });
					});	
					
	//Funtion wriiten for to prevent the submit button from double clicks in shift Add 
	//using form id				
					$(function()
					{
					  $('#shiftAddForm').submit(function(){
					    $("input[type='submit']", this)
					      .val("Please Wait...")
					      .attr('disabled', 'disabled');
			     		    return true;
					  });
					});	
					
	//Funtion wriiten for to prevent the submit button from double clicks in Shift submit page
					$(function()
					{
					 $('#shiftForm').submit(function(){
					    $("input[type='submit']", this)
					      .val("Please Wait...")
					      .attr('disabled', 'disabled');
					      setTimeout('$("#btnSubmit").removeAttr("disabled")', 1500);
					         $("input[type='submit']", this).val("Submit")
					    return true;
					  });
					});
	//Funtion wriiten for to prevent the submit button from double clicks in Shift Edit Page
					$(function()
					{
					  $('#shiftEditForm').submit(function(){
					    $("input[type='submit']", this)
					      .val("Please Wait...")
					     .attr('disabled', 'disabled');
					     setTimeout('$("#btnSubmit").removeAttr("disabled")', 1500);
					         $("input[type='submit']", this).val("Submit")
					    return true;
					  });
					});	
					
	//Funtion wriiten for to prevent the submit button from double clicks in Shift Summary Page		
	//using button id.
				
					$(function()
					{
					
					  $('#summaryButton').on('click',function()
					  {
					    $(this).val('Please wait ...')
					      .attr('disabled','disabled');
					       setTimeout('$("#btnSubmit").removeAttr("disabled")', 1000);
					        $("input[type='submit']", this).val("Submit")
					    $('#shiftSummaryForm').submit();
					  });
					  
					});
	//Funtion wriiten for to prevent the submit button from double clicks in DailyReconcilation edit page 
					$(function()
					{
					  $('#drButton').on('click',function()
					  {
						var status=document.getElementById("drStatusId").value;
						console.log("status :"+status);
						var filename = $('input[type=file]').val().split('\\').pop();
						console.log("file :"+filename);
						if(status == "SUBMITTED" && filename == ""){
							//alert("Please select file");
							$("#dailyReportFile_field input").prop('required',true);
						}
						else{
							$(this).val('Please wait...')
							.attr('disabled','disabled');
							$('#drEditForm').submit();
						}
					});
					  
					  
					});	
				
					$(function()
					{
					  $('#hOTSheetButton').on('click',function()
					  {
					    $(this).val('Please wait ...')
					      .attr('disabled','disabled');
					   	setTimeout('$("#hOTSheetButton").removeAttr("disabled")', 1000);
					   	 $(this).val('Add')
					   	
					   
					  });
					  
					});	
					
	var rowNum=0;
	
	//method for head office timesheet
	function addTimeSheetHours(empid){
		var emp=document.getElementById("empid").value;
		var date=document.getElementById("hoDate").value;
		//var date=$('.startDatePicker').datepicker('getDate');
		var startTimeHour=document.getElementById('startTimeHour').value;
		var startTimeMins=document.getElementById('startTimeMins').value;
		var endTimeHour=document.getElementById('endTimeHour').value;
		var endTimeMins=document.getElementById('endTimeMins').value;
		var textArea=document.getElementById('hOTextarea').value;
		var duration=document.getElementById('duration').value;
		var leaveCheckBox=document.getElementById('applyLeaveCheckBoxHO').checked;
		var leaveType=document.getElementById('leaveType').value;
		var storeId =1;
		var startHoursInTable;
		var startMinsInTable;
		var endHoursInTable;
		var endMinsInTable;
		var timeSheetPresent="no";
		//console.log("startTimeHour is : "+startTimeHour);
		//console.log("startTimeHour length is : "+startTimeHour.length);
		//console.log("Date length is : "+date.length);
		
		
		
		//form validation start
		if(date == "" || date.length == 0){
			// alert("Please select date");
			document.getElementById("hoDateErrorLabel").style.display="block";
			document.getElementById("startTimeHourErrorLabel").style.display="none";
			document.getElementById("startTimeMinsErrorLabel").style.display="none";
			document.getElementById("endTimeHourErrorLabel").style.display="none";
			document.getElementById("endTimeMinsErrorLabel").style.display="none";
			document.getElementById("hoActivityErrorLabel").style.display="none";
			document.getElementById("hoDate").focus();
		}
		else if(startTimeHour == "" || startTimeHour.length == 0){
			document.getElementById("startTimeHourErrorLabel").style.display="block";
			document.getElementById("hoDateErrorLabel").style.display="none";
			document.getElementById("startTimeMinsErrorLabel").style.display="none";
			document.getElementById("endTimeHourErrorLabel").style.display="none";
			document.getElementById("endTimeMinsErrorLabel").style.display="none";
			document.getElementById("hoActivityErrorLabel").style.display="none";
			document.getElementById("startTimeHour").focus();
		}
		else if(startTimeMins == "" || startTimeMins.length == 0){
			document.getElementById("startTimeMinsErrorLabel").style.display="block";
			document.getElementById("hoDateErrorLabel").style.display="none";
			document.getElementById("startTimeHourErrorLabel").style.display="none";
			document.getElementById("endTimeHourErrorLabel").style.display="none";
			document.getElementById("endTimeMinsErrorLabel").style.display="none";
			document.getElementById("hoActivityErrorLabel").style.display="none";
			document.getElementById("startTimeMins").focus();
		}
		else if(endTimeHour == "" || endTimeHour.length == 0){
			document.getElementById("endTimeHourErrorLabel").style.display="block";
			document.getElementById("hoDateErrorLabel").style.display="none";
			document.getElementById("startTimeHourErrorLabel").style.display="none";
			document.getElementById("startTimeMinsErrorLabel").style.display="none";
			document.getElementById("endTimeMinsErrorLabel").style.display="none";
			document.getElementById("hoActivityErrorLabel").style.display="none";
			document.getElementById("endTimeHour").focus();
		}
		else if(endTimeMins == "" || endTimeMins.length == 0){
			document.getElementById("endTimeMinsErrorLabel").style.display="block";
			document.getElementById("hoDateErrorLabel").style.display="none";
			document.getElementById("startTimeHourErrorLabel").style.display="none";
			document.getElementById("startTimeMinsErrorLabel").style.display="none";
			document.getElementById("endTimeHourErrorLabel").style.display="none";
			document.getElementById("hoActivityErrorLabel").style.display="none";
			document.getElementById("endTimeMins").focus();
		}
		else if(textArea == "" || textArea.length == 0){
			document.getElementById("hoActivityErrorLabel").style.display="block";
			document.getElementById("hoDateErrorLabel").style.display="none";
			document.getElementById("startTimeHourErrorLabel").style.display="none";
			document.getElementById("startTimeMinsErrorLabel").style.display="none";
			document.getElementById("endTimeHourErrorLabel").style.display="none";
			document.getElementById("endTimeMinsErrorLabel").style.display="none";
			document.getElementById("hOTextarea").focus();
		}
		//form validation end
		
		else{
		document.getElementById("hoActivityErrorLabel").style.display="none";
		
		var myTable = document.getElementById('headOfficeTimeSheetTable').tBodies[0];
		

         // first loop for each row	
		for (var r=0, n = myTable.rows.length; r < n; r++) {
            // this loop is getting each colomn/cells
			//console.log("inside outer for loop");
			for (var c = 0, m = myTable.rows[r].cells.length; c < m; c++) {
			console.log("inside inner for loop");
				if(myTable.rows[r].cells[c].childNodes[0].value){
				   	
					startHoursInTable = myTable.rows[r].cells[0].childNodes[0].value;
					startMinsInTable = myTable.rows[r].cells[1].childNodes[0].value;
					endHoursInTable = myTable.rows[r].cells[2].childNodes[0].value;
					endMinsInTable = myTable.rows[r].cells[3].childNodes[0].value;
					
					var startTime=startTimeHour.concat(startTimeMins);
					var endTime=endTimeHour.concat(endTimeMins);
					console.log("start time :"+startTime);
					console.log("end time :"+endTime);
					
					var startTimeInTable = startHoursInTable.concat(startMinsInTable);
					var endTimeInTable = endHoursInTable.concat(endMinsInTable);
					
					console.log("start time in table :"+startTimeInTable);
					console.log("end time in table :"+endTimeInTable);					
					if((startTime >= startTimeInTable && startTime < endTimeInTable) 
					|| (endTime > startTimeInTable && endTime <= endTimeInTable) 
					|| (startTime <= startTimeInTable && endTime >= endTimeInTable) ){
					
					console.log("Timesheet Hours Entered is Already Existing in the System ");
					timeSheetPresent="yes";
				}//if condition	
							 
					c = c+2;
			}
				
		}
        }
        
		
		if(startTimeHour == startHoursInTable
						 && startTimeMins == startMinsInTable 
						 && endTimeHour == endHoursInTable && endTimeMins == endMinsInTable){
			alert("This row is already inserted in table");
		}
		else if(timeSheetPresent == "yes"){
			alert("Timesheet Hours Entered is Already Existing in the System");
		}
		
		else{
		//if condition start for if leave type equals none
			if(leaveType == 'None'){
		
				//make an Ajax call
				myJsRoutes.controllers.Application.checkHeadOfficeTimesheetExists(storeId,emp,date,date,leaveType,startTimeHour,startTimeMins,endTimeHour,endTimeMins).ajax({
					success : function(data) {
						
					//console.log("RESULT IS : "+data);
						if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
							alert("Already One Timesheet is There for this Employee for this date : "+date+" . Modify The Existing one or Choose Different Time or Date");
								flag = false;
						}else if(data == -1){
			    		 	alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates....");
			    				flag = false;
				    	} else{
			    			$('#headOfficeTimeSheetTable_field').css('display','block');
							myJsRoutes.controllers.Application.getTimeSheetSave(emp,date,startTimeHour,startTimeMins,endTimeHour,endTimeMins,duration,textArea,leaveType).ajax({
								success : function(data) {
							    //console.log("data is: "+JSON.stringify(data));
								//var res = data.split(":");
								//alert("data is:"+data);
								//console.log("start time is : "+data.startTimeHour);
									if(leaveType == "None"){
										newRowContent = "<tr><td><input type=\"text\" name=\"" +rowNum + "_StartTimeHours\"   id=\"" +rowNum + "_StartTimeHours\" value=\""+data.startTimeHour+"\" readonly='readonly'></td><td><input type=\"text\" name=\"" +rowNum + "_StartTimeMins\"   id=\"" +rowNum + "_StartTimeMins\" value=\""+data.startTimeMins+"\" readonly='readonly'></td><td><input type=\"text\" name=\"" +rowNum + "_EndTimeHours\"   id=\"" +rowNum + "_EndTimeHours\" value=\""+data.endTimeHour+"\" readonly='readonly'></td><td><input type=\"text\" name=\"" +rowNum + "_EndTimeMins\"   id=\"" +rowNum + "_EndTimeMins\" value=\""+data.endTimeMins+"\" readonly='readonly'></td><td><input type=\"text\" name=\"" +rowNum + "_Duration\"   id=\"" +rowNum + "_Duration\" value=\""+data.duration+"\" readonly='readonly'></td><td><textarea name=\"" +rowNum + "_Activity\"   id=\"" +rowNum + "_Activity\" value=\""+data.activity+"\" readonly='readonly'>"+data.activity+"</textarea></td><td><INPUT TYPE=\"Button\" CLASS=\"btn btn-primary\" onClick=\"delHeadOfficeTimeSheetRow('"+data+"')\" VALUE=\"Delete Row\"></td></tr>";
										$("#headOfficeTimeSheetTable tbody").append(newRowContent);
										//newRowContent = "<tr><td>"+data.startTimeHour+"</td><td>"+data.startTimeMins+"</td><td>"+data.endTimeHour+"</td><td>"+data.endTimeMins+"</td><td>"+data.duration+"</td><td>"+data.activity+"</td><td><INPUT TYPE=\"Button\" CLASS=\"btn btn-primary\" onClick=\"delHeadOfficeTimeSheetRow('"+data+"')\" VALUE=\"Delete Row\"></td></tr>";
										rowNum++;
										document.getElementById("rowCountHidden").value=rowNum;
										//console.log("row count is :"+rowNum);
										
										$('select#startTimeHour option').removeAttr("selected");
										$('select#startTimeMins option').removeAttr("selected");
										$('select#endTimeHour option').removeAttr("selected");
										$('select#endTimeMins option').removeAttr("selected");
										$("#duration").val("");
										//$("#hoDate").val();
										$("#hOTextarea").val("");
										$('#hoDate').prop('required',false); // disable required attribute if leave selected
										$('#hoEndDate').prop('required',false);
										$('#startTimeHour').prop('required',false);
										$('#startTimeMins').prop('required',false);
										$('#endTimeHour').prop('required',false);
										$('#endTimeMins').prop('required',false);
										
									}
					 			}
							});
			    		}
			    		
		    		}
				});
				
			}	//if condition end 	for if leave type equals none
			else{
			
			//make an Ajax call
			 myJsRoutes.controllers.Application.checkTimesheetExistsForLeave(storeId,empId,date,endDate).ajax({
	   			 success : function(data) {
	    		
	    		alert(data);
	    		//return false;
	    		
	    			if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
	    				alert("Already One Timesheet is There for this Employee for this date : "+date+" or Range . Modify The Existing one or Choose Different Time or Date");
	    				flag = false;
	    			}else if(data == -1){
	    				alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates..");
	    			}
	    			else{
	    				//alert("Else");
	    				document.getElementById('timesheetForm').submit(); // submit the form here
	    				flag = true;
	    			}
	    		}
			});
		}
		
		
			
		}
		
		}
		
	}

	function delHeadOfficeTimeSheetRow(pid)
	{
	  	alert("Are you surely want to delete");
	  	var current = window.event.srcElement;
	 	//alert(current.parentElement.tagName);
	    //here we will delete the line
	    while ( (current = current.parentElement)  && current.tagName.toLowerCase() !="tr");        
			current.parentElement.removeChild(current);
	}
	
	/*function checkForHeadOfficeTimeSheet(){
			var contents = {},
		    duplicates = false;
		$("#headOfficeTimeSheetTable td").each(function() {
		    var tdContent = $(this).text();
		    if (contents[tdContent]) {
		        duplicates = true;
		        return false;
		    }
		    contents[tdContent] = true;
		});    
		if (duplicates)
		   alert("There were duplicates.");
		   }*/
	
	function getRowCount(){
	
	var rowCount=document.getElementById("headOfficeTimeSheetTable").rows.length;
	var leaveType=document.getElementById('leaveType').value;
	var leaveCheckBox=document.getElementById('applyLeaveCheckBoxHO').checked;
	document.getElementById("rowCountHidden").value=rowNum;
	
	//console.log("leave checkbox status :"+leaveCheckBox);
	//alert("leave type :"+leaveType);
	
	
	//console.log("rowCount is"+rowCount);
	
		if(leaveCheckBox == true && leaveType == 'None'){
			alert("Please Select Leave Type");
			return false;
		}
	
		 if(leaveType == 'None' && leaveCheckBox == false ){
			if(rowNum == 0){
				alert("Please click Add button before submitting the Timesheet");
					return false;	
			}
		}	
		else{
			//alert("inside alert....");
			var emp=document.getElementById("empid").value;
			var date=document.getElementById("hoDate").value;
			var storeId =1;
			var result=false;
			//make an Ajax call
			 myJsRoutes.controllers.Application.checkHeadOfficeTimesheetExistsForLeave(storeId,emp,date,date).ajax({
	   			 success : function(data) {
	    		
	    		//alert(data);
	    		//return false;
	    		
	    			if(data > 0){ // Ajax call returns count of Existing Timesheet for the same Time range for this employee
	    				alert("Already One Timesheet is There for this Employee for this date : "+date+" or Range . Modify The Existing one or Choose Different Time or Date");
	    				result = false;
	    			}else if(data == -1){
	    				alert("For the given Dates Weekly Timesheet is Already Processed..! Please choose different Dates..");
	    				result = false;
	    			}
	    			else{
	    				//alert("Else");
	    				document.getElementById('timesheetForm').submit(); // submit the form here
	    				result = true;
	    			}
	    		}
			});
			return result;
		}

	}
	
	function getHeadOfficeTimeSheetTableData(){
	//alert("inside getHeadOfficeTimeSheetTableData()");
	console.log("inside getHeadOfficeTimeSheetTableData()");
		// Get table object
		var myTable = document.getElementById('headOfficeTimeSheetTable').tBodies[0];
		console.log("after myTable ");
		console.log("myTable rows length is :"+myTable.rows.length);

         // first loop for each row	
        for (var r=0, n = myTable.rows.length; r < n; r++) {
            // this loop is getting each colomn/cells
			console.log("inside outer for loop");
			for (var c = 0, m = myTable.rows[r].cells.length; c < m; c++) {
				console.log("inside inner for loop");
			    if(myTable.rows[r].cells[c].childNodes[0].value){
				console.log("inside if condition");
				   	
					var startHours = myTable.rows[r].cells[0].childNodes[0].value;
					 //alert("start hours: "+startHours);
					 var startMins = myTable.rows[r].cells[1].childNodes[0].value;
					 var endHours = myTable.rows[r].cells[2].childNodes[0].value;
					 var endMins = myTable.rows[r].cells[3].childNodes[0].value;
					 console.log("startHours is: "+startHours);
 					 console.log("startMins is: "+startMins);
 					 console.log("endHours is: "+endHours);
 					 console.log("endMins is: "+endMins);
					 
					c = c+2;
			    }
				
            }
        }

	}	
	function showActivity(element){
		// alert(activityData);
		//$('modelBodyP')
		document.getElementById("modelBodyP").innerHTML=element.innerText;
		$('#myModal').modal("show");
	}
	
	function checkActivityTextLength(){
	//alert("inside checking");
	var activityLength=document.getElementById("hOTextarea").value.length;
	//alert("activity length :"+activityLength);
	var textLength = 250-activityLength;
	document.getElementById("activityTextLength").innerHTML="Remaining Characters are "+textLength;
	}
	
	function OpenFancyBoxForHeadOfficeFormsView(hoFormId){
		
	
		$(".fancyboxPDF").fancybox({
			openEffect: 'elastic',
			closeEffect: 'elastic',
			width:1200,
			height:1000,
			autoSize: true,
			type: 'iframe',
			loop : false,
			helpers : { 
			
			  overlay : {closeClick: false}, // disables close when outside clcik 
			},
			iframe: {
				preload: false // fixes issue with iframe and IE
			}
			
		});	
	}
function loginValidate(){
	var email=document.getElementById("email").value;
	var password=document.getElementById("password").value;
	if(email != "" && password == ""){
	$('#emailError').css('display','none');
	$('#passwordError').css('display','block');
	$("#password").css("backgroundColor", "rgba(255, 0, 0, 0.11)");
	
		return false;
	}
	if(email == "" && password != ""){
	$('#emailError').css('display','block');
	$("#email").css("backgroundColor", "rgba(255, 0, 0, 0.11)");
	$('#passwordError').css('display','none');
		return false;
	}
	if(email == "" && password == ""){
	$('#emailError').css('display','block');
	$("#email").css("backgroundColor", "rgba(255, 0, 0, 0.11)");
	$('#passwordError').css('display','block');
	$("#password").css("backgroundColor", "rgba(255, 0, 0, 0.11)");
	
		return false;
	}
	else{
	return true;
	}

}	
	
$(document).ready(function () {
    $('#upload').on("click", function () {
    	var imgVal1 = $('#formType').val();
    	//alert(imgVal1);
        var imgVal = $('[type=file]').val();
        if (imgVal1 == '' || imgVal == '' ) {
            alert("Please select All Fields");
            return false;
        }
	    $(this).val('Please wait ...')
	      .attr('disabled','disabled');
	       setTimeout('$("#upload").removeAttr("disabled")', 1000);
	        $("input[type='submit']", this).val("Submit")
	    $('#shiftSummaryForm').submit();
	 
    });
});

$('#statusSelectedInInvoice').ready(function(){
		var invoiceStatus=$('#statusSelectedInInvoice').val();
		$("#invoice_status option[value='" + invoiceStatus + "']").attr("selected","selected");
	});
	
$('#statusSelectedInStore').ready(function(){
		var invoiceStatusInStore=$('#statusSelectedInStore').val();
		$("#invoice_statusInStore option[value='" + invoiceStatusInStore + "']").attr("selected","selected");
	});

	