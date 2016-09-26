package controllers;


import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static play.data.Form.form;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import models.Address;
import models.BankDeposit;
import models.Company;
import models.ContactInfo;
import models.DailyReconciliation;
import models.DailySalesReconciliation;
import models.Employee;
import models.ExpenseHead;
import models.Invoice;
import models.MediaTender;
import models.SalesHead;
import models.Shift;
import models.ShiftSale;
import models.Payout;
import models.Payroll;
import models.Store;
import models.Supplier;
import models.Terminal;
import models.TerminalHead;
import models.TotalSalesHead;
import models.TotalSettlementSale;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.*;
import views.html.dr.*;
import play.*;


@Security.Authenticated(Secured.class)
public class DailyReconciliations extends Controller {
	
private static final Form<DailyReconciliation> dailyReconForm = form(DailyReconciliation.class); 
	
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.DailyReconciliations.list(0, "status", "asc", "","VIEW")
    );
	  
public static Result showBlank(Long storeId){
	
	Logger.info("@C DailyReconciliations -->> showBlank("+storeId+") -->> ");
		
		Store store = Store.find.byId(storeId);
		Logger.debug("@C DailyReconciliations -->> showBlank("+storeId+") -->> store : "+store.name);
		DailyReconciliation dr = DailyReconciliation.getDailyReconciliation(storeId);
		
		//adding DSR to DR
		if(dr.dsr==null){
		DailySalesReconciliation dsrecon = DailySalesReconciliation.getDailySalesReconciliation(store.id);

		dr.dsr = dsrecon;
		 dr.save();
		}
		
		
		 Form<DailyReconciliation> dsrForm = form(DailyReconciliation.class).fill(dr);
		 flash("action", "DailyReconciliation");
		
		 Logger.info("@C DailyReconciliations -->> showBlank("+storeId+") <<--");
		return ok(show.render(dsrForm,store)); 
		}
	
	public static Result edit(Long id,String action) {
		
		flash("pageAction", action);
		
		Logger.info("@C DailyReconciliations -->> edit("+id+") -->> ");
		
		DailyReconciliation dsr = DailyReconciliation.find.byId(id);
		Form<DailyReconciliation> dsrForm = null;
		if(dsr!=null){
			dsrForm= form(DailyReconciliation.class).fill(dsr);
		}
		else{
			dsrForm = dailyReconForm;
		}
		Store store = dsr.store;
		Logger.debug("@C DailyReconciliations -->> edit("+id+") -->> store "+store.name);
	  
		flash("action", "DailyReconciliation");
		 
		
		Logger.info("@C DailyReconciliations -->> edit("+id+") <<--");
		return ok(editForm.render(id,dsrForm,dsr,store)); 
	}
	
	
	public static Result delete(Long id) {
		
		Logger.info("@C DailyReconciliations -->> delete("+id+") -->> ");
			DailyReconciliation.delete(id);
			  
			  	 flash("action", "DailyReconciliation");
		Logger.info("@C DailyReconciliations -->> delete("+id+") <<-- ");
			  	return GO_HOME; 
			  }
	
	
public static Result update(Long id) {
		
	Logger.info("@C DailyReconciliations -->> update("+id+") -->> ");
		
		DailyReconciliation dr = DailyReconciliation.find.byId(id);
		Store store=dr.store;
		
		
		
		/*  
		  dsr.status = form().bindFromRequest().get("status");
		  saveTotalSettlementSales(dsr);*/
		
		DynamicForm drForm=Form.form().bindFromRequest();
		
		  Form<BankDeposit> bankDepositForm = form(BankDeposit.class).bindFromRequest();
		bankDepositForm.get().update(dr.bankDeposit.id);
		  
	    dr=saveSAFEPayout(drForm,store,dr);
	    dr=savePayroll(drForm,dr);
	    dr=saveDailyReportFile(drForm,dr);
	    
	    dr.status=form().bindFromRequest().get("status");
	    
	    // updating close cash and close cheques
	    
		DailySalesReconciliation dsr=dr.dsr;
		if(dr.status.equals("SUBMITTED")){
		    dr.close_cash=DailyReconciliation.getCashInSafe(dr.dsr, dr); //DailySalesReconciliation.getCashReported(dsr);
		    dr.close_cash= Math.round( dr.close_cash * 100.0 ) / 100.0; // rounding
		    dr.close_cheque=DailyReconciliation.getChequesInSafe(dr.dsr, dr); //DailySalesReconciliation.getChequesReported(dsr);
		    dr.close_cheque= Math.round( dr.close_cheque * 100.0 ) / 100.0; // rounding
		    dr.save();
		    //safe cash in store
		    store.cashInSafe=Math.round( (dr.close_cash) * 100.0 ) / 100.0;
		    store.chequesInSafe=Math.round( (dr.close_cheque) * 100.0 ) / 100.0;
		    store.save();
		}
		 dr.update(id);
		  
		  	 flash("action", "DailyReconciliation");
		  	 
		  	Logger.info("@C DailyReconciliations -->> update("+id+") <<--");
		  	return redirect(
		  			routes.DailyReconciliations.list(0, "status", "asc", "","MODIFY")
	      		  );
		  }
	 public static Result save(Long storeid) {
	 	 
		 Logger.info("@C DailyReconciliations -->> save("+storeid+") -->> ");
		 flash("action", "DailyReconciliation");
		 
		  Form<DailyReconciliation> dsrForm = form(DailyReconciliation.class).bindFromRequest();
		//  long storeid = Long.valueOf(form().bindFromRequest().get("store.id")).longValue() ;
		 
	      Store store = Store.find.byId(storeid);
	      Logger.debug("@C DailyReconciliations -->> save("+storeid+") -->>  store : "+store);
	      DailyReconciliation dr=DailyReconciliation.getDailyReconciliation(storeid);
	    
	      dr.status= form().bindFromRequest().get("status");
	      dr.save();
	     
	      Logger.info("@C DailyReconciliations -->> save("+storeid+") <<--");
	      return GO_HOME; 

		  }
	 
	 /**
	   * Display the paginated list of computers.
	   *
	   * @param page Current page number (starts from 0)
	   * @param sortBy Column to be sorted
	   * @param order Sort order (either asc or desc)
	   * @param filter Filter applied on computer names
	   */
	  public static Result list(int page, String sortBy, String order, String filter,String pageAction) {
		  
		  Logger.info("@C DailyReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
		  flash("pageAction", pageAction);
		  flash("action", "DailyReconciliation");
		  
		  String email= session("email");
		  Logger.debug("@C DailyReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->> Email :"+email);
			 Employee emp = null;
			 Long empid = 0L;
			 Long storeid = 0L;
			 if(email!=null){
	          emp = Employee.findByEmail(email);
	          empid = emp.id;
	          storeid = emp.store.id;
	         
	          }
			 Store store = Store.find.byId(storeid);
			 System.out.println("filter is :"+filter);
			 return ok(list.render(
		              DailyReconciliation.page(page,  10,storeid , sortBy, order, filter),
		              sortBy, order, filter,store, pageAction
		          )); 
	  }
	  
 public static Result listByDate(int page, String sortBy, String order, String filter,String pageAction) {
		  
		  Logger.info("@C DailyReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
		  flash("pageAction", pageAction);
		  flash("action", "DailyReconciliation");
		  
		  String email= session("email");
		  Logger.debug("@C DailyReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->> Email :"+email);
			 Employee emp = null;
			 Long empid = 0L;
			 Long storeid = 0L;
			 if(email!=null){
	          emp = Employee.findByEmail(email);
	          empid = emp.id;
	          storeid = emp.store.id;
	         
	          }
			 Store store = Store.find.byId(storeid);
			 System.out.println("filter is :"+filter);
			 return ok(list.render(
		              DailyReconciliation.pageByDate(page,  10,storeid , sortBy, order, filter),
		              sortBy, order, "",store, pageAction
		          )); 
	  }
 //Gopi
	  
	  public static DailyReconciliation saveSAFEPayout(DynamicForm form,Store store,DailyReconciliation dr){
		  
		  Logger.info("@C DailyReconciliations -->> saveSAFEPayout(form,store,DailyReconciliation) -->> ");
		//   final Map<String, String[]> values = request().body().asFormUrlEncoded();
	        //   
	        
	        Double temp;
	   		String fName;
	   		String var_pay;
	   		//String var_payArray[];
	   		play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
	     
	         for(Payout payout: dr.payouts){
	        	 
	        	  var_pay = payout.id.toString()+"_Amt";
	        	  
	        	  var_pay= form().bindFromRequest().get(var_pay);
	        	 
	        	 temp  = Double.parseDouble(var_pay);
	        	 payout.invoiceAmt = temp;
	        	 
	        	 var_pay = payout.id.toString()+"_Exp";
	        	   
	        	  var_pay= form().bindFromRequest().get(var_pay);
	        	         	 	        	 
	        	 payout.category  = var_pay;
	        	 
	        	//reason
	        	 var_pay = payout.id.toString()+"_reason";
	        	 
	        	  var_pay= form().bindFromRequest().get(var_pay);
	        	        	 	        	 
	        	 payout.reason  = var_pay;
	        	 
	        	 var_pay = payout.id.toString()+"_File";
	        	    
	        	//  var_pay= form().bindFromRequest().get(var_pay);
	        	    	
	        	 
	        	// Logger.debug("@C DailyReconciliations -->> saveSAFEPayout(form,store,DailyReconciliation) -->> Invoice amount: "+payout.invoiceAmt+" category :"+payout.category+" File: "+var_pay);
	        	 
	        	 if(body.getFile(var_pay)!=null ){
	        	        	 	        	 
	        	//empty String is passed to upload() , to diff b/w invoices and employee documents
	        	 payout.invoice = Application.upload(payout.supplier.name, var_pay, body,"");
	        	 }
	        	   
		   	    	payout.update(payout.id);
		   	    	
	         }
	         Logger.info("@C DailyReconciliations -->> saveSAFEPayout(form,store,DailyReconciliation) <<--"); 
	   	      return dr;
		  
	  }
	  
	  public static DailyReconciliation savePayroll(DynamicForm form,DailyReconciliation dr){
		  
		  Logger.info("@C DailyReconciliations -->> savePayroll(form,DailyReconciliation) -->> ");
		  
		  form=Form.form().bindFromRequest();
		  for(Payroll payroll : dr.payroll){
			  
			  String key="payAmt_"+payroll.id;
			 payroll.payAmt=Double.parseDouble(form.get(key));
			  payroll.update(payroll.id);
			  
		  }
		  
		  Logger.info("@C DailyReconciliations -->> savePayroll(form,DailyReconciliation) <<--");
		  return dr;
		  
	  }

	  // save DailyReport File
	  public static DailyReconciliation saveDailyReportFile(DynamicForm form,DailyReconciliation dr){
		  
		  Logger.info("@C DailyReconciliations -->> saveDailyReportFile(requestBody,DailyReconciliation) -->> ");
		  
		  	//final String  basePath = Play.application().path().getAbsolutePath(); // for play
			//final String  basePath = System.getenv("CATALINA_HOME") +"//webapps//SRSFiles"; // for tomcat ,within webapps
			final String  basePath = System.getenv("INVOICE_HOME"); // for tomcat other than webapps folder,D Drive
				
		  Store store = dr.store; // for store name
		  StringBuffer fileNameString = new StringBuffer(); // to save file path in DB
		  play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData(); // get Form Body
		  String fName =dr.id+"_dailyReportFile"; // make input file tag name
		 
		  FilePart upFile = body.getFile(fName);
		  
		  if (upFile != null) {
	        	
	           String fileName = upFile.getFilename();
	           String contentType = upFile.getContentType(); 
	           File file = upFile.getFile();
	           
	           fileName = StringUtils.substringAfterLast(fileName, ".");
	          
	           // path to Upload Files
	           	File ftemp= new File(basePath +"//Daily Sales Analysis Reports//"+store.name); //for play
				//File ftemp= new File(basePath +"//Daily Sales Analysis Reports//"+store.name); // for tomcat
				fileNameString.append("Daily Sales Analysis Reports/"+store.name+"/");
				ftemp.mkdirs();
				
				// to hava Date in file name
				Calendar cal = Calendar.getInstance();
				cal.setTime(dr.reportingBusinessDate);
	          
	           File f1 = new File(ftemp.getAbsolutePath() +"//"+cal.get(Calendar.DATE)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.YEAR)+"_"+fName+"."+fileName);
	           file.setWritable(true);
	           file.setReadable(true);
	           
	           try{ 
	        	   Files.copy(file.toPath(), f1.toPath(), REPLACE_EXISTING);
	           }
	           catch(Exception e){
	        	  
	        	   e.printStackTrace();
	           }
	           
	           file.renameTo(f1);
	           fileNameString.append(f1.getName());
	           
	           dr.dailyReportFile = fileNameString.toString();
	           dr.save();
		  }
		  Logger.info("@C DailyReconciliations -->> saveDailyReportFile(requestBody,DailyReconciliation) <<--");
		  return dr;
	  }
	  

	  public static void saveTotalSettlementSales(DailyReconciliation dsr) {
		  
		  Logger.info("@C DailyReconciliations -->> saveTotalSettlementSales(DailyReconciliation id:"+dsr.id+") -->> ");
	    	
			 String email= session("email");
			 Logger.debug("@C DailyReconciliations -->> saveTotalSettlementSales(DailyReconciliation id:"+dsr.id+") -->>Email  "+email);
			 Employee emp = null;
			 Long empid = 0L;
			 Long storeid = 0L;
			 if(email!=null){
	          emp = Employee.findByEmail(email);
	          empid = emp.id;
	          storeid = emp.store.id;
	          
	          }
			 Store store = Store.find.byId(storeid);
			 
	       
	         dsr.save();
	         Logger.info("@C DailyReconciliations -->> saveTotalSettlementSales(DailyReconciliation id:"+dsr.id+") <<--");
		
	       
	    }
	  
	  public static Result summary(Long empId,String weekDate){
		  Logger.info("@C DailyReconciliations -->> summary("+empId+","+weekDate+") -->> ");
		  Logger.debug("@C DailyReconciliations -->> summary("+empId+","+weekDate+") -->> weekDate "+weekDate);
			 
			 String weekStartDate=weekDate.replaceAll("-", "/");
			
			 Logger.debug("@C DailyReconciliations -->> summary("+empId+","+weekDate+") -->> weekStartDate "+weekStartDate);
			 Date date=Application.getDate(weekStartDate);
			  Logger.info("@C DailyReconciliations -->> summary("+empId+","+weekDate+") <<--");
				  
			 return ok(summary.render(date,empId));
		 }
	  
	  // checking for open status ,used @display of DR list page
	  
	  public static boolean getOPENStatus(Long storeId){
		  
		  List<DailyReconciliation> dsrListForStore=DailyReconciliation.page(storeId,"OPEN");
		  if(dsrListForStore.size()>=1){
			  return true;
		  }
		  else{
			  return false;
		  }
	  }
	  
	  /**
	   * drViewOptions() ,used to show DR View in HeadOffice section to display List of DR's in Particular Range
	   * 
	   */
	  
	  public static Result drViewOptions(int page, String sortBy, String order, String filter,String pageAction) {
		  
		  Logger.info("@C DailyReconciliations -->> drViewOptions("+page+","+sortBy+","+order+","+filter+","+pageAction+") -->> ");
		  
		  flash("pageAction", pageAction);
		  
		  flash("action", "DailyReconciliationView");
		  
		  String startDate = form().bindFromRequest().get("drStartDate");
		  String endDate = form().bindFromRequest().get("drEndDate");
		  String formStore=form().bindFromRequest().get("storeId");
		  
		 
		  Long storeId = null;
		  
		  // for the first request fields are null ,so for fresh call display list of DR's of todays'date irrespective of store
		  if(formStore!=null){
			  
		   storeId = Long.parseLong(formStore);
		   
		  }
		  else{
			  // here 20 is max for number of stores, For initial call we get all stores reconciliations of today's date if they are done
			  return ok(drViewOptions.render(
		              DailyReconciliation.page(page,  25, sortBy, order),
		              sortBy, order, filter, pageAction, startDate, endDate,storeId
		          ));
		  }
		  
		  
		  // for the further requests other than first request we have form fields
		  // 31 here is to display list of one month max
		  if(startDate.equals(endDate)){ // if start date and end date both are same
			  
			  return ok(drViewOptions.render(
					  DailyReconciliation.page(page,  31, sortBy, order, filter,startDate,storeId),
					  sortBy, order, filter,pageAction,startDate,endDate,storeId
		          ));
			  
		  }else{ // if start date and end date both are different
			  
			  return ok(drViewOptions.render(
					  DailyReconciliation.page(page,  31, sortBy, order, filter,startDate,endDate,storeId),
					  sortBy, order, filter,pageAction,startDate,endDate,storeId
		          ));
			  
		  }
		  
	  }
		
	  
//showOptions method used in Application.java
	  
	  public static Result showOptions(String page,String action) {
		  
		  flash("pageAction",action);
		  
		  String email= session("email");
	  		Employee emp = null;
			 Long empid = 0L;
			 Long storeid = 0L;
			 if(email!=null){
	          emp = Employee.findByEmail(email);
	          empid = emp.id;
	          storeid = emp.store.id;
	       
	          }
			 Store store = Store.find.byId(storeid);
		 	
		 	if(action.equals("ADD")){
		 		
		 		  return redirect(
			            		routes.DailyReconciliations.showBlank(storeid)
			            );
		 	}
		 	else if(action.equals("ALL")){
		 		
		 		return ok(manage_dr.render(page,store)); 
		 	}
		 	
		 	else{
		 		
		 		
		 		return redirect(
						 routes.DailyReconciliations.list(0, "status", "asc", "",action)
			            );
				
		 	}
		  }
	 
}
