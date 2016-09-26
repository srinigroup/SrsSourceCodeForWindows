package controllers;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

import com.avaje.ebean.Ebean;

import models.AccountHolder;
import models.Address;
import models.Company;
import models.ContactInfo;
import models.DailyReconciliation;
import models.DailySalesReconciliation;
import models.Employee;
import models.ExpenseHead;
import models.MediaTender;
import models.PaymentTender;
import models.Payout;
import models.Shift;
import models.ShiftMediaCollected;
import models.ShiftPaymentTender;
import models.ShiftSale;
import models.ShiftVariance;
import models.Store;
import models.SalesHead;
import models.Supplier;
import models.SupplierMapping;
import models.Terminal;
import models.TerminalHead;
import models.Timesheet;
import models.TotalSalesHead;
import play.data.DynamicForm;
import play.data.DynamicForm.Dynamic;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.shifts.*;
import static play.data.Form.*;
import play.*;
import play.mvc.Controller.*;

@Security.Authenticated(Secured.class)
public class Shifts extends Controller {
	
	/**
     * This result directly redirect to  home.
     */
    public static Result GO_HOME = redirect(
     
      routes.Shifts.list(0, "status", "desc", "","VIEW")
    );
    
	public static Result showBlank(Long sid,String thead,String pageAction) { 
		
		flash("pageAction", pageAction);
		
		Logger.info("@C Shifts -->> showBlank("+sid+","+thead+") -->>");
		//Form<Shift> shiftForm = form(Shift.class);
		 Store store = Store.find.byId(sid);
		 DailySalesReconciliation dsr = DailySalesReconciliation.getDailySalesReconciliation(sid);
		
		 //checking weather DSR is closed while adding a new shift
		 if(dsr!= null){
		 if(!(dsr.status.equals("SUBMITTED"))){
			 
		 
		 Form<DailySalesReconciliation> dsrForm = form(DailySalesReconciliation.class).fill(dsr);
		 
		 Terminal terminal = Terminal.getTerminalbyTerminalHead(dsr, thead);
		 Employee emp = Employee.findByEmail(session("email"));
		 Logger.debug("@C Shifts -->> showBlank("+sid+","+thead+") -->> emp name: "+emp.firstName);
		 Shift shift =null;
		 if(terminal!=null){
			 Logger.debug("@C Shifts -->> showBlank("+sid+","+thead+") -->> inside if cond..Terminal is not null");
			 shift = Shift.getActiveShift(terminal);
			 Long shiftEmpid = Shift.getShiftEmployeebyTerminal(terminal,emp.id);
		
			 if(shift==null){
				 Logger.debug("@C Shifts -->> showBlank("+sid+","+thead+") -->> inside if cond..Terminal is not null..if shift is null");
				 Timesheet timesheet = new Timesheet(emp.id,sid,"STORE","StoreKeeper","OPEN");
				 timesheet.save();
				 shift = new Shift(timesheet,"OPEN");
				 shift.terminal = terminal;
				 shift = setDeftaultValues( shift,  store);
				 shift.save();
				 List<Shift> shiftList = terminal.shifts;
				 shiftList.add(shift);
				 terminal.shifts = shiftList;
				 terminal.save();
				 List<Terminal>terminalList = dsr.terminals;
				
				 //terminalList.add(terminal);
				 dsr.terminals = terminalList;
				 dsr.save();
				 Logger.debug("@C Shifts -->> showBlank("+sid+","+thead+") -->> end of if condition");
			 }
			 else if(shift!=null && (shiftEmpid!=null&&shiftEmpid!=emp.id )){
				
				 String msg = "Currently Shift is used by another User \n Please Select another Terminal";
				
				 return ok(error.render(msg)); 
			 }
		 }
		 else{
			 Logger.debug("@C Shifts -->> showBlank("+sid+","+thead+") -->> inside else condition");
			 TerminalHead term_head = TerminalHead.findbyName(thead);
			 terminal = new Terminal(term_head,"OPEN");
			 terminal.dailySalesReconciliation=dsr;
			 terminal.save();
			 Timesheet timesheet = new Timesheet(emp.id,sid,"STORE","StoreKeeper","OPEN");
			 timesheet.save();
			 shift = new Shift(timesheet,"OPEN");
			 
			 shift.terminal = terminal;
			 shift = setDeftaultValues( shift,  store);
			 shift.save();	
		        	 
			 List<Shift>shiftList = terminal.shifts;
			 shiftList.add(shift);
			 terminal.shifts = shiftList;
			 terminal.save();
			 List<Terminal>terminalList = dsr.terminals;
			 terminalList.add(terminal);
			 dsr.terminals = terminalList;
			 dsr.save();
			 Logger.debug("@C Shifts -->> showBlank("+sid+","+thead+") -->> end of  else condition");
		 }
		 
		
		 flash("action", "shift");
		 Logger.info("@C Shifts -->> showBlank("+sid+","+thead+") <<--");
		return ok(show.render(dsrForm,dsr,store,shift)); 
		 }
	}
		 //if DSR is submitted..,Showing a Warning
		 flash("action", "shift");
		 
		 flash("ShiftErr", "Reconciliation is Closed for the Day..!!!");
		 return GO_HOME;
		 
		}
	
	public static Result edit(Long id,Long sid,String action) {
		//Form<Shift> shiftForm = form(Shift.class);
		System.out.println("inside edit method in shifts..........................");
		flash("pageAction", action);
		
		Logger.info("@C Shifts -->> edit("+id+","+sid+") -->>");
		 Store store = Store.find.byId(sid);
		 Logger.debug("@C Shifts -->> edit("+id+","+sid+") -->> store : "+store.name);
		 Shift shift = Shift.find.byId(id);
		 
		 DailySalesReconciliation dsr = shift.terminal.dailySalesReconciliation;
		 
		 Form<DailySalesReconciliation> dsrForm = form(DailySalesReconciliation.class).fill(dsr);
		 
		 flash("action", "shift");
		 Logger.info("@C Shifts -->> edit("+id+","+sid+") <<--");
			return ok(editForm.render(dsrForm,dsr,store,shift)); 
	}
	
	public static Result delete(Long id) {
		Logger.info("@C Shifts -->> edit("+id+") -->>");
		Shift.delete(id);
		  
		  	 flash("action", "shift");
		  Logger.info("@C Shifts -->> edit("+id+") <<--");
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
	  public static Result list(int page,String sortBy,String order,String filter,String pageAction) {
		  Logger.info("@C Shifts -->> list("+page+","+sortBy+","+order+","+filter+") -->>");
		  
		  flash("pageAction", pageAction);
		  flash("action", "shift");
		  //check for role
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
		 
		  return ok(list.render(
	              Shift.page(page,  10,storeid , empid, sortBy, order, filter),
	              sortBy, order, filter,store,pageAction
	          )); 
	  }
	  
	  
	  // Supplier list for specific store: action method
	  
	  public static Result storeSuppliersList(int page,String sortBy,String order,String filter) {
		  
		  Logger.info("@C Shifts -->> storeSuppliersList("+page+","+sortBy+","+order+","+filter+") -->>");
		  flash("action", "supplierPerStore");
		  String email= session("email");
		  Employee emp=Employee.findByEmail(email);
		  Store store=emp.store;
		  
		  List<SupplierMapping> list=new ArrayList<SupplierMapping>();
		  
		  if(filter==null ||( filter!= null && filter.trim().length()==0)){
			  list=store.supplierMapping;
		  }else{
			  
			  for(SupplierMapping supplierMapping:store.supplierMapping){
				  
				  if(supplierMapping.supplier.name.toLowerCase().startsWith(filter.toLowerCase())){
					  list.add(supplierMapping);
				  }
			  }
		  }
		  
		  // sort the above list
		  if(list.size() > 0){
			  
			  list.stream().sorted((object1, object2) -> object1.supplier.name.compareTo(object2.supplier.name));
		  }
		 
		  return ok(supplierList.render(
				  list,
	              sortBy, order, filter
	          )); 
	  }
	  
	  public static Result save(Long shiftid,Long storeid,String pageAction) {
		  Logger.info("@C Shifts -->> save("+shiftid+","+storeid+") -->>");

		  flash("pageAction", pageAction);
		 flash("action", "shift");
		Store store = Store.find.byId(storeid);
		Shift shift = Shift.find.byId(shiftid);
		
	//	Form<ShiftVariance> shiftVarianceForm = form(ShiftVariance.class).bindFromRequest();
		
	//	shiftVarianceForm.get().save();
		DynamicForm shiftform=Form.form().bindFromRequest();
		shift.shiftVariance = Double.parseDouble(shiftform.get("shiftVariance"));
		shift.varReason = shiftform.get("varReason");
		System.out.println("flow stopped///////////");
		shift = saveShiftSales(shiftform,store,shift);
		//shift =saveAccountShiftSalesInAccounts(shiftform,store,shift);//added by sirisha for shift account sales
		shift = saveMediaCollects(shiftform,store,shift);
		shift = saveShiftPayout(shiftform,store,shift);
		shift = savePaymentAmounts(shiftform,store,shift);
		shift.shiftVariance = Double.parseDouble(Shift.getTotalVariance(shift)); // added By Gopi after Demo with Santhosh
		shift.shiftEndDateTime = new Date();
		System.out.println("inside save method in shifts..........................");

		Terminal terminal =shift.terminal;
		
		if(terminal.terminalVariance!=null){
			
			terminal.terminalVariance = terminal.terminalVariance+shift.shiftVariance;
		}
		else{
			terminal.terminalVariance = shift.shiftVariance;
		}
		terminal.save();
		shift.save();
		DailySalesReconciliation dsr = DailySalesReconciliation.getDailySalesReconciliation(storeid);
		validateShiftSales(shift);
		 Logger.info("@C Shifts -->> save("+shiftid+","+storeid+") <<--");
		return ok(summary.render(dsr,store,shift)); 
	  }
	  
	  
	  public static Result update(Long shiftid,Long storeid,String pageAction) {
		  System.out.println("inside update method");
		  Logger.info("@C Shifts -->> update("+shiftid+","+storeid+") -->>");
		  
		  flash("pageAction", pageAction);
			 flash("action", "shift");
			Store store = Store.find.byId(storeid);
			Shift shift = Shift.find.byId(shiftid);
			ShiftVariance shiftVariance= null;
			
			DynamicForm shiftform=Form.form().bindFromRequest();
			
			shift.varReason = shiftform.get("varReason");				
			for(ShiftSale shiftSale: shift.shiftSales){
				
	    			 //System.out.println("inside shift sale update");
		    		 String formAmount = (shiftform.get(shiftSale.salesHead.name));
		    		 shiftSale.amount = Double.parseDouble(formAmount);
		    		 shiftSale.update(shiftSale.id);
					 System.out.println(" shift sale update");

	    	}
			
			for(ShiftMediaCollected shiftMediaCollected: shift.mediaCollects){
	    		
	    		 String mediaName = shiftMediaCollected.mediaTender.name;
	    		 String formAmount = (shiftform.get(mediaName));
	    		
	    		 shiftMediaCollected.amount = Double.parseDouble(formAmount);
	    		 formAmount = (shiftform.get("settle_"+mediaName));
	    		
	    		 shiftMediaCollected.settleAmount = Double.parseDouble(formAmount);
	    		 
	    		 shiftMediaCollected.update(shiftMediaCollected.id);
	        }
			
			shift = saveShiftPayout(shiftform,store,shift);
			for(ShiftPaymentTender shiftPaymentTender: shift.shiftPayments){
				
	    		 String formAmount = (shiftform.get(shiftPaymentTender.paymentTender.name));
	    		
	    		 shiftPaymentTender.amount = Double.parseDouble(formAmount);
	    		 
	    		 shiftPaymentTender.update(shiftPaymentTender.id);
	        }
			shift.status = "SAVED";
			shift.shiftVariance = Double.parseDouble(Shift.getTotalVariance(shift)); // added By Gopi after Demo with Santhosh
			shift.update(shift.id);	
			
			Terminal terminal =shift.terminal;
			if(terminal.terminalVariance!=null){
				
				terminal.terminalVariance = terminal.terminalVariance+shift.shiftVariance;
			}
			else{
				terminal.terminalVariance = shift.shiftVariance;
			}
			terminal.update(terminal.id);
			validateShiftSales(shift);
			shift.update(shift.id);
			DailySalesReconciliation dsr = DailySalesReconciliation.getDailySalesReconciliation(storeid);
			
			Logger.info("@C Shifts -->> update("+shiftid+","+storeid+") <<--");
			return ok(summary.render(dsr,store,shift)); 
		  }
	 
	  public static Shift setDeftaultValues(Shift shift, Store store){
		  
		  Logger.info("@C Shifts -->> setDeftaultValues(shift id: "+ shift.id +", store id:"+store.id+") -->>");
		  
			 List<ShiftSale> shiftSaleList = new ArrayList<ShiftSale>();
			 
			 List<ShiftMediaCollected> mediaCollectList = new ArrayList<ShiftMediaCollected>();
			 
			 List<ShiftPaymentTender> paymentTenderList = new ArrayList<ShiftPaymentTender>();
			 
				for(SalesHead salesHead: store.salesHeads){
					
					
					ShiftSale shiftSale = new ShiftSale();
					shiftSale.salesHead = salesHead;
					shiftSale.amount = 0.00;
					shiftSale.shift = shift;
					shiftSale.save();
					shiftSaleList.add(shiftSale);
		        	}
			
				for(MediaTender mediaTender: store.mediaTenderHeads){
					
					ShiftMediaCollected shiftMediaCollected = new ShiftMediaCollected();
					shiftMediaCollected.mediaTender = mediaTender;
					shiftMediaCollected.amount = 0.00;
					shiftMediaCollected.shift = shift; 
					shiftMediaCollected.save();
					mediaCollectList.add(shiftMediaCollected);
					
					
		        	}
				
				for(PaymentTender paymentTender: store.paymentTenders){
					
					ShiftPaymentTender shiftpaymentTender= new ShiftPaymentTender();
					shiftpaymentTender.paymentTender = paymentTender;
					shiftpaymentTender.amount = 0.00;
					shiftpaymentTender.shift = shift; 
					shiftpaymentTender.save();
					paymentTenderList.add(shiftpaymentTender);
					
					
		        	}
				shift.shiftSales = shiftSaleList;
				shift.mediaCollects = mediaCollectList;
				 Logger.info("@C Shifts -->> setDeftaultValues(shift id: "+shift.id+", store id:"+store.id+") <<--");
				return shift;
	  }
	  
	  public static Shift saveShiftSales(DynamicForm form,Store store,Shift shift) {
		  
		  Logger.info("@C Shifts -->> saveShiftSales(form,store,shift) -->>");
	    	
	         DynamicForm salesform=form;
	    	
	    	 // final Map<String, String[]> values = request().body().asFormUrlEncoded();
	    	
	    	System.out.println("saleform===="+salesform.get());
			
	    	 for(ShiftSale shiftSale :shift.shiftSales) {

		 	   	 String saleHeadName=shiftSale.salesHead.name;
	    		 String formAmount = (salesform.get(saleHeadName));
	    		 shiftSale.amount = Double.parseDouble(formAmount);
	    		 //System.out.println(" sales head name===="+saleHeadName);
		 	     //System.out.println("forAmount===="+formAmount);
		    	 
		    	 shiftSale.shift = shift;
		    	 shiftSale. update(shiftSale.id);
		 	     System.out.println("SHIFT SALE UPDATED====");
	    		 
	    		}
	    	 Logger.info("@C Shifts -->> saveShiftSales(form,store,shift) <<--");
	    	 return shift;
	    }
	
	  
	  public static Shift savePaymentAmounts(DynamicForm form,Store store,Shift shift) {
		  
		  Logger.info("@C Shifts -->> savePaymentAmounts(form,store,shift) -->>");
	    	
	         DynamicForm paymentTenderForm=form;
	    	
	    	 for(ShiftPaymentTender shiftPaymentTender :shift.shiftPayments) {
	    		 
	    		 String formAmount = paymentTenderForm.get(shiftPaymentTender.paymentTender.name);
	    		 shiftPaymentTender.amount = Double.parseDouble(formAmount);
	    		 shiftPaymentTender.shift = shift;
	    		 shiftPaymentTender. update(shiftPaymentTender.id);
	    	 }
	    	 Logger.info("@C Shifts -->> savePaymentAmounts(form,store,shift) <<--");
	    	 return shift;
	    }
	  
	  public static Shift saveMediaCollects(DynamicForm form,Store store,Shift shift) {
		  
		  Logger.info("@C Shifts -->> saveMediaCollects(form,store,shift) -->>");
	    	
		  DynamicForm mediaCollectForm=form;

	    	 for(ShiftMediaCollected shiftMediaCollected :shift.mediaCollects) {
	    		
	    		 String mediaName = shiftMediaCollected.mediaTender.name;
	    		 String formAmount = (mediaCollectForm.get(mediaName));
	    		 shiftMediaCollected.amount = Double.parseDouble(formAmount);
	    		 formAmount = (mediaCollectForm.get("settle_"+mediaName));
	    		 shiftMediaCollected.settleAmount = Double.parseDouble(formAmount);
	    		 
	    		 shiftMediaCollected.update(shiftMediaCollected.id);
	    	 }
	        Logger.info("@C Shifts -->> saveMediaCollects(form,store,shift) <<--");
	    	 return shift;
		
	       
	    }
	  
	 
	  public static Shift saveShiftPayout(DynamicForm form,Store store,Shift shift) {
		  
		  Logger.info("@C Shifts -->> saveShiftPayout(form,store,shift) -->>");
		   
	        Double temp;
	   		String fName;
	   		String var_pay;
	   		play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
	      
	         for(Payout payout: shift.payouts){
	        	 
	        	  var_pay = payout.id.toString()+"_Amt";
	        	  System.out.println("payout amount:"+var_pay);
	        	   
	        	  var_pay= form().bindFromRequest().get(var_pay);
	        	  System.out.println("payout amount from form:"+var_pay);
	        	  if(var_pay!=null){
	        	 
	        		  temp  = Double.parseDouble(var_pay);
	        		  System.out.println("parsed amount:"+temp);
	        		  payout.invoiceAmt = temp;

	        		  var_pay = payout.id.toString()+"_Exp";
			        	 
	        		  var_pay= form().bindFromRequest().get(var_pay);
			        	        	 	        	 
	        		  payout.category  = var_pay;
			        	 
	        		  //reason
	        		  var_pay = payout.id.toString()+"_reason";
			        	 
	        		  var_pay= form().bindFromRequest().get(var_pay);
			        	        	 	        	 
	        		  payout.reason  = var_pay;
			        	 
			          var_pay = payout.id.toString()+"_File";
			          if(body.getFile(var_pay)!=null ){
			        	  //empty String is passed to upload() , to diff b/w invoices and employee documents
			        	  payout.invoice = Application.upload(payout.supplier.name, var_pay, body,"");
			          }
		   	    	payout.update(payout.id);
	        	  }	
	          }
	         Logger.info("@C Shifts -->> saveShiftPayout(form,store,shift) <<--");
	   	     return shift;
	   	
	  }

	  /*
	   * Submit() used in summary page of shift
	   * 
	   * author: Gopi
	   * 
	   *
	   */
	  
	  public static Result submit(Long shiftid) {
		  
		  Logger.info("@C Shifts -->> submit("+shiftid+") -->>");
		  
		  Shift shift = Shift.find.byId(shiftid);
		  shift.shiftEndDateTime = new Date();
		  shift.status="SUBMITTED";
		  shift.varReason = Form.form().bindFromRequest().get("shiftVarReason");
		  shift.save();
		
		  // assigning shift cash amount after payout  to DR close cash
		  
		  Double shiftCash=Shift.getShiftMediaCollected(shift.mediaCollects,"CASH").settleAmount; //get shift media CASH
		  Double shiftCheque=Shift.getShiftMediaCollected(shift.mediaCollects,"CHEQUE").settleAmount; //get shift media CHEQUE
		 // Double shiftTotalPyouts=Double.parseDouble(Shift.getTotalPayoutsbyType(shift, "REGISTERPAYOUT")); // get total payouts for shift
		  shift.terminal.dailySalesReconciliation.dr.close_cash=DailyReconciliation.getDRCASHINSAFE( shift.terminal.dailySalesReconciliation.dr.id);
		  shift.terminal.dailySalesReconciliation.dr.close_cheque=DailyReconciliation.getDRCHEQUESINSAFE( shift.terminal.dailySalesReconciliation.dr.id);
		  shift.terminal.dailySalesReconciliation.dr.update(); // update DR
		  
			//Logic to find duration in Timesheet and status change
					  
					  
					  Date shiftStartTime=shift.shiftStartDateTime;
					  Date shiftEndTime=shift.shiftEndDateTime;
					  
					  Logger.debug("@C Shifts -->> submit("+shiftid+") -->> shiftStartTime "+shiftStartTime);
					  Logger.debug("@C Shifts -->> submit("+shiftid+") -->> shiftEndTime "+shiftEndTime);
					  
					  Calendar cal1 =Calendar.getInstance();
					  cal1.setTime(shiftStartTime);
					  Calendar cal2 =Calendar.getInstance();
					  cal2.setTime(shiftEndTime);
					  
					  long startTime=cal1.getTimeInMillis();
					  long endTime=cal2.getTimeInMillis();
					  long diff=endTime-startTime;
					  
					  
				        long diffMinutes = diff / (60 * 1000) % 60;
					
				        long diffHours = diff / (60 * 60 * 1000);
					
				       
						String duration = diffHours+" : "+ diffMinutes;
						 Logger.debug("@C Shifts -->> submit("+shiftid+") -->> shift duration "+duration);
					  Timesheet timeSheet=shift.timesheet;
					  
					  timeSheet.startTimeHour = new Integer(cal1.get(Calendar.HOUR_OF_DAY)).toString();
					  timeSheet.startTimeMins = new Integer(cal1.get(Calendar.MINUTE)).toString();
					  timeSheet.endTimeHour = new Integer(cal2.get(Calendar.HOUR_OF_DAY)).toString();
					  timeSheet.endTimeMins = new Integer(cal2.get(Calendar.MINUTE)).toString();
					  timeSheet.endDate = shiftEndTime;
							  
					  timeSheet.duration=duration;
					  timeSheet.leaveType = "None";
					  timeSheet.status="SUBMITTED";
					  timeSheet.save();
							  
					  Logger.info("@C Shifts -->> submit("+shiftid+") <<--");
		  
					  return redirect(
							  routes.Shifts.list(0, "status", "desc", "","MODIFY")
					        );
		  
	  }

		   
		   public static void validateShiftSales(Shift shift) {
			   
			   Logger.info("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->>");
			   
			   String payoutAmt=Shift.getTotalPayoutsbyType(shift, "REGISTERPAYOUT");
			   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> payoutAmt "+payoutAmt);
			   String cashAmt=Shift.getShiftMediaCollectedAmount(shift.mediaCollects,"CASH");//getting actual media collects to compare with Payout..,
			   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> cashAmt "+cashAmt);
			   
			   //if(Double.parseDouble(payoutAmt)>Double.parseDouble(cashAmt)){
				   Double temp=Double.parseDouble(payoutAmt)-Double.parseDouble(cashAmt);
				   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> inside 1st if condition");
				   temp=Math.round( temp * 100.0 ) / 100.0;
				   
				  // flash("payoutDiff", "-Difference amount : "+temp);
				   flash("payoutErr", "Please Verify the REGISTERPAYOUT Amount(s) -Difference amount : "+temp);
			   //}
			   String totalSaleAmt=Shift.getTotalNetTakings(shift,"REGISTERPAYOUT");
			   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> totalSaleAmt "+totalSaleAmt);
			  
			   String mediaCollects=Shift.getNetPOSTakings(shift);
			   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> mediaCollects "+mediaCollects);
			   
			   if(Double.parseDouble(totalSaleAmt)!=Double.parseDouble(mediaCollects)){
				   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> inside 2nd if condition");
				   Double temp1=Double.parseDouble(totalSaleAmt)-Double.parseDouble(mediaCollects);
				  
				   
				   temp1=Math.round( temp1 * 100.0 ) / 100.0;
				   
				   //flash("mediaDiff", "-Difference amount s : "+temp);
				  
				   if((temp1<-1 )|| (temp1>1 )){
					   Logger.debug("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") -->> inside nested if condition");
				   flash("mediaErr", "Please Verify the Sales/Media Amount(s) -Difference amount s : "+temp1);
				   }
			   }
			 
			   Logger.info("@C Shifts -->> validateShiftSales(shift id: "+shift.id+") <<--");
			   
		   }
		   
		   
		// checking for open status used in @dispaly of shift list page
		   
		   public static boolean getOPENStatus(){
			   
			  Employee emp=Employee.findByEmail(session("email"));
			  
			   List<Shift> shiftListForEmployee=Shift.page(emp.id,"SUBMITTED");// in model we are checking not equal condition on status
				  if(shiftListForEmployee.size()>=1){
					  return true;
				  }
				  else{
					  return false;
				  }
		   }
	  
		   
		   
		 //showOptions method used in Application.java
			  
			  public static Result showOptions(String page,String action) {
				  	
				  	
				  	flash("pageAction",action);
				  	
				  
				  if(action.equals("ALL")){
				  		
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
				  		return ok(manage_shift.render(page,store)); 
				  	}
				  	
				  	else{
				  		
				  		
				  		return redirect(
				 				 routes.Shifts.list(0, "status", "asc", "",action)
					            );
				 		
				  	}
				  	
				  }
	  
}
