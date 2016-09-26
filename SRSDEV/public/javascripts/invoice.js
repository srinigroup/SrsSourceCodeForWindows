
	$('#paymentSelectedCompany').ready(function(){
		var comp=$('#paymentSelectedCompany').val();
		var category=$('#paymentSelectedInvoiceType').val();
	
	 	if(comp!="noCompany"){
	 		$("#companyId option[value='" + comp + "']").attr("selected","selected");
	 	 	populateStoresInInvoicePayments(comp);
	 	 	setTimeout(function(){
    		var store=$('#paymentSelectedStore').val(); 
    		   			
				if(store!="noStore"){
			 	
			 		$("#invoiceStoreList option[value='" + store + "']").attr("selected","selected");
					if(category!="noCategory"){
						$('#invoiceCategoryAtPaymentPage_field').css('display','block');
		 				$("#invoiceCategoryAtPaymentPage option[value='" + category + "']").attr("selected","selected");
			 			if(category == 'Other'){
			 				var paymentTerm=$('#paymentSelectedPaymentTerm').val();
							if(paymentTerm!="noPaymentTerm"){
								$('#paymentTermsAtpaymentPage_field').css('display','block');
			 					$("#paymentTermsAtpaymentPage option[value='" + paymentTerm + "']").attr("selected","selected");
			 				}
						}
			 	   	}
				}
		 	  
			}, 500);
		}
	});	
	

	function populateStoresInInvoicePayments(companyId){
	
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
	