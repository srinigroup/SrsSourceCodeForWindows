package controllers;


import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Address;
import models.Company;
import models.ContactInfo;
import models.DailyReconciliation;
import models.DailySalesReconciliation;
import models.Employee;
import models.ExpenseHead;
import models.MediaTender;
import models.SalesHead;
import models.Shift;
import models.ShiftSale;
import models.Store;
import models.Supplier;
import models.Terminal;
import models.TerminalHead;
import models.TotalSalesHead;
import models.TotalSettlementSale;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.dsr.*;
import play.*;

@Security.Authenticated(Secured.class)
public class DailySalesReconciliations extends Controller {
	
private static final Form<DailySalesReconciliation> dailySaleReconForm = form(DailySalesReconciliation.class); 
	
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.DailySalesReconciliations.list(0, "status", "asc", "","VIEW")
    );
	  
	public static Result showBlank(Long storeId){
		
		Logger.info("@C DailySalesReconciliations -->> showBlank("+storeId+") -->>");
		
		Store store = Store.find.byId(storeId);
		
		
		
		 DailySalesReconciliation dsr = DailySalesReconciliation.getDailySalesReconciliation(storeId);
		 
		 //flash message for DSR new creation,when DR status is open
		 if(dsr==null){
			 
			 flash("DSRErr","Please SUBMIT DailyReconciliation first.,While adding a new DailySalesReconciliation ..!!!");
			 return GO_HOME; 
		 }
		
		 //opening a DR when adding a new DSR
		/* DailyReconciliation dr=DailyReconciliation.getDailyReconciliation(storeId);
		
		 //checking wheather there is  DSR for DR
		// if(dr.dsr==null){
		 dr.dsr=dsr;
		 dsr.save();
		 dr.save();
		// }
		 */
		 
		 Form<DailySalesReconciliation> dsrForm = form(DailySalesReconciliation.class).fill(dsr);
		 flash("action", "DailySalesReconciliation");
		 Logger.info("@C DailySalesReconciliations -->> showBlank("+storeId+") <<--");
		return ok(show.render(dsrForm,dsr,store)); 
		}
	
	public static Result edit(Long id,String action) {
		
		System.out.println("inside Dialysalesreconsilations edit................");
		flash("pageAction", action);
		
		Logger.info("@C DailySalesReconciliations -->> edit("+id+") -->>");
		
		DailySalesReconciliation dsr = DailySalesReconciliation.find.byId(id);
		Form<DailySalesReconciliation> dsrForm = null;
		if(dsr!=null){
			Logger.debug("@C DailySalesReconciliations -->> edit("+id+") -->> inside if condition");
			dsrForm= form(DailySalesReconciliation.class).fill(dsr);
		}
		else{
			Logger.debug("@C DailySalesReconciliations -->> edit("+id+") -->> inside else condition");
			dsrForm = dailySaleReconForm;
		}
		Store store = dsr.store;
		Logger.debug("@C DailySalesReconciliations -->> edit("+id+") -->> store : "+store.name);
	   
		flash("action", "DailySalesReconciliation");
		
		Logger.info("@C DailySalesReconciliations -->> edit("+id+") <<--");
		 
		return ok(editForm.render(id,dsrForm,dsr,store)); 
	}
	public static Result delete(Long id) {
		
		Logger.info("@C DailySalesReconciliations -->> delete("+id+") -->>");
		Shift.delete(id);
		  
		  	 flash("action", "DailySalesReconciliation");
		  	Logger.info("@C DailySalesReconciliations -->> delete("+id+") <<--");
		  	return GO_HOME; 
		  }
	
	public static Result update(Long id) {
		
		Logger.info("@C DailySalesReconciliations -->> update("+id+") -->>");
		
		DailySalesReconciliation dsr = DailySalesReconciliation.find.byId(id);
		//Form<DailySalesReconciliation> dsrForm = form(DailySalesReconciliation.class).bindFromRequest();
		
		  dsr.status = form().bindFromRequest().get("status");
		//  saveTotalSettlementSales(dsr);
		  dsr.update(id);
		  
		  	 flash("action", "DailySalesReconciliation");
		  	Logger.info("@C DailySalesReconciliations -->> update("+id+") <<--");
		  	return redirect(
		  			routes.DailySalesReconciliations.list(0, "status", "asc", "","MODIFY")
	      		  );
		  }
	 public static Result save(Long storeid) {
		 
		 Logger.info("@C DailySalesReconciliations -->> save("+storeid+") -->>");
	 	 
		  Form<DailySalesReconciliation> dsrForm = form(DailySalesReconciliation.class).bindFromRequest();
		//  long storeid = Long.valueOf(form().bindFromRequest().get("store.id")).longValue() ;
	      Store store = Store.find.byId(storeid);
		 
	      if(dsrForm.hasErrors()) {
	    	 
	          return badRequest(show.render(dsrForm,dsrForm.get(),store));
	      }
			     
	    
	      DailySalesReconciliation.create(dsrForm.get(),store);
	    
	      Logger.info("@C DailySalesReconciliations -->> save("+storeid+") <<--");	    
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
		  Logger.info("@C DailySalesReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->>");
		  flash("pageAction", pageAction);
		  flash("action", "DailySalesReconciliation");
		  String email= session("email");
		  Logger.debug("@C DailySalesReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->> email :"+email);
			 Employee emp = null;
			 Long empid = 0L;
			 Long storeid = 0L;
			 if(email!=null){
	          emp = Employee.findByEmail(email);
	          empid = emp.id;
	          storeid = emp.store.id;
	         
	          }
			 Store store = Store.find.byId(storeid);
			 Logger.debug("@C DailySalesReconciliations -->> list("+page+","+sortBy+","+order+","+filter+") -->> store :"+store.name);
			
			 return ok(list.render(
		              DailySalesReconciliation.page(page,  10,storeid , sortBy, order, filter),
		              sortBy, order, filter,store, pageAction
		          )); 

		  
		  
	  }

	  public static void saveTotalSettlementSales(DailySalesReconciliation dsr) {
		  
		  Logger.info("@C DailySalesReconciliations -->> saveTotalSettlementSales("+dsr.id+") -->>");
	    	
			 String email= session("email");
			 Logger.debug("@C DailySalesReconciliations -->> saveTotalSettlementSales("+dsr.id+") -->> Email : "+email);
			 Employee emp = null;
			 Long empid = 0L;
			 Long storeid = 0L;
			 if(email!=null){
	          emp = Employee.findByEmail(email);
	          empid = emp.id;
	          storeid = emp.store.id;
	          
	          }
			 Store store = Store.find.byId(storeid);
			 Logger.debug("@C DailySalesReconciliations -->> saveTotalSettlementSales("+dsr.id+") -->> Store : "+store.name);
			 
	     
	         
	    	 
	    	 List<Terminal> terminalList = new ArrayList<Terminal>();
	    	 if(dsr.terminals.size()>0){
	         List<TotalSettlementSale> totalSettlementSaleList = new ArrayList<TotalSettlementSale>();
	         for(TerminalHead terminalHead : store.terminalHeads) {
	        	
			  		Terminal terminal = Terminal.getTerminalbyTerminalHead(dsr.terminals, terminalHead.name);
			  		
     	
			
		    	 for(TotalSalesHead totalSaleshead :store.totalSalesHeads) {
		    		
		    		 TotalSettlementSale totalSettlementSale = new TotalSettlementSale();
		    		 totalSettlementSale.totalSalesHead = totalSaleshead;
		    		 totalSettlementSale.terminal = terminal;
		    		 totalSettlementSale.totalSalesHead.save();
		    		 
		    		 String temp_name = terminalHead.name+"_"+totalSaleshead.name;
		    		
		    		 String tempLong = form().bindFromRequest().get(temp_name);
		    		 
		    		 totalSettlementSale.amount = Double.parseDouble(tempLong) ;
		    		 
		    		 totalSettlementSale.save();
		    		 
		    		
		    		 totalSettlementSaleList.add(totalSettlementSale);
		    	 }
			  
		    	 terminal.totalSettlementSales = totalSettlementSaleList;
		    	 Logger.debug("@C DailySalesReconciliations -->> saveTotalSettlementSales("+dsr.id+") -->> TotalSettlementSales : "+terminal.totalSettlementSales);
		     	
		    	 terminal.save();
		    	 terminalList.add(terminal);
	         }
	    	
	         dsr.terminals= terminalList;
	    	 }
	         dsr.save();
		
	         Logger.info("@C DailySalesReconciliations -->> saveTotalSettlementSales("+dsr.id+") <<--");
	    }
	  
	  // checking for open status 
	  
	  public static boolean getOPENStatus(Long storeId){
		  
		  List<DailySalesReconciliation> dsrListForStore=DailySalesReconciliation.page(storeId,"OPEN");
		  if(dsrListForStore.size()>=1){
			  return true;
		  }
		  else{
			  return false;
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
			            		routes.DailySalesReconciliations.showBlank(storeid)
			            );
		 	}
		 	else if(action.equals("ALL")){
		 		
		 		return ok(manage_dsr.render(page,store)); 
		 	}
		 	
		 	else{
		 		
		 		
		 		return redirect(
						 routes.DailySalesReconciliations.list(0, "status", "asc", "",action)
			            );
				
		 	}
		  }
}
