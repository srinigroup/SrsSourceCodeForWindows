package controllers;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.avaje.ebean.Ebean;

import play.Logger;
import models.Address;
import models.Company;
import models.ContactInfo;
import models.DailyReconciliation;
import models.ExpenseHead;
import models.MediaTender;
import models.PaymentTender;
import models.Payout;
import models.Shift;
import models.Store;
import models.SalesHead;
import models.Supplier;
import models.SupplierMapping;
import models.TerminalHead;
import models.TotalSalesHead;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.stores.*;
import views.html.*;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)
public class Stores extends Controller {

	
    
	private static final Form<Store> storeForm = form(Store.class); 
	private static final Form<ContactInfo> contactForm = form(ContactInfo.class); 
	private static final Form<Address> addressForm = form(Address.class); 
	
	private static final int pageSize=15;
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.Stores.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C Stores -->> showBlank() -->> ");
		 flash("action", "store");
		return ok(show.render(Supplier.page(0, 10, "name", "asc", ""),SalesHead.page(0, pageSize, "name", "asc", ""),MediaTender.page(0, pageSize, "name", "asc", ""),ExpenseHead.page(0, pageSize, "name", "asc", ""),TerminalHead.page(0, 10, "name", "asc", ""),TotalSalesHead.page(0, 10, "name", "asc", ""),PaymentTender.page(0, 10, "name", "asc", ""),storeForm,contactForm,addressForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  Logger.info("@C Stores -->> edit("+id+") -->> ");
	  flash("pageAction", action);
	  
	  //checking for any DR of this store is OPEN status
	  if(DailyReconciliations.getOPENStatus(id)){
		  flash("DROpenStatusError", "You are not able to Edit Store while Reconciliations are in OPEN Status");
	  }
	  
	  Store store = Store.find.byId(id);
      Form<Store> storeForm = form(Store.class).fill(store);
      Form<ContactInfo> contactForm = form(ContactInfo.class).fill(
    		  ContactInfo.find.byId(store.contactInfo.id));
      Form<Address> addressForm  = form(Address.class).fill(
    		  Address.find.byId(store.address.id));
     // List<SalesHead> salesHeadList = store.salesHeads;
   
   //   List<MediaTender> mediaTenderList = store.mediaTenderHeads;
   //   List<ExpenseHead> expenseHeadList = store.expenseHeads;
   //   List<Supplier> suppliersList = store.suppliers;
      
      flash("action", "store");
      Logger.info("@C Stores -->> edit("+id+") <<--");
      return ok(
          editForm.render(id,storeForm,contactForm,addressForm,store)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C Stores -->> update("+id+") -->> ");
	  Store store = Store.find.byId(id);
	  Form<Store> storeForm = form(Store.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
      if(storeForm.hasErrors()) {
    	  Logger.error("@C Stores -->> update("+id+") -->> storeForm has some errors");
          return badRequest(editForm.render(id, storeForm, contactForm,addressForm,store));
      }
    
	  final Map<String, String[]> values = request().body().asFormUrlEncoded();
	  String messages[] = values.get("salesheadslist[]");
		
		  List<SalesHead> salesHeadList = SalesHead.getSalesHeadList(messages);
		  
		  // removing totalsaleshead list by passing dummy list i.e empty list
		/*  messages = values.get("totalsalesheadslist[]");
		
			  List<TotalSalesHead> totalSalesHeadList = TotalSalesHead.getTotalSalesHeadList(messages); */
		  
		  List<TotalSalesHead> totalSalesHeadList = new ArrayList<TotalSalesHead>();
		  
		  messages = values.get("mediatenderslist[]");
	
		  List<MediaTender> mediaTenderList = MediaTender.getMediaTenderList(messages);
		  
		  messages = values.get("paymentTenderslist[]");
		  List<PaymentTender> paymentTenderslist = null;
		  // check for null.for that store no payment Tenders then assign empty ArrayList
		  if(messages == null){
			  
			  paymentTenderslist = new ArrayList<PaymentTender>();
		  }else{
			  
			  paymentTenderslist = PaymentTender.getPaymentTenderList(messages);
		  }
		  
		  messages = values.get("expenseheadslist[]");
	
		  List<ExpenseHead> expenseHeadList = ExpenseHead.getExpenseHeadList(messages);
		  
		/*  messages = values.get("supplierslist[]");
		  List<Supplier> suppliersList = Supplier.getSuppliersList(messages);*/
		  
		  // saving Suppliermappings
		  
		  saveSupplierMapping(store);
	   
		  messages = values.get("terminalheadslist[]");
		  List<TerminalHead> terminalheadslist = TerminalHead.getTerminalHeadList(messages);
	     
	     
		  long companyid = Long.valueOf(form().bindFromRequest().get("company.id")).longValue() ;
	      Company company = Company.find.byId(companyid);
	  
	     
	      //Retrieving Supplier Payment Terms and Mode fields and saving it
		  
	  /*    	  for(Supplier supplier:suppliersList){
	      		  
	      		  String paymentTerms=form().bindFromRequest().get(supplier.id+"_paymentTerms");
	      		  String paymentMode=form().bindFromRequest().get(supplier.id+"_paymentMode");
	      		  
	      		  
	      	  } */
	           
	     
	 
	  
      contactForm.get().update(store.contactInfo.id);
      addressForm.get().update(store.address.id);
      storeForm.get().company = company;
      storeForm.get().salesHeads = salesHeadList;
      storeForm.get().totalSalesHeads = totalSalesHeadList;
	  storeForm.get().mediaTenderHeads = mediaTenderList;
	  storeForm.get().paymentTenders = paymentTenderslist;
	  storeForm.get().expenseHeads = expenseHeadList;
	 // storeForm.get().suppliers = suppliersList;
	  storeForm.get().terminalHeads = terminalheadslist;
      storeForm.get().update(id);
      flash("action", "store");
      flash("success", storeForm.get().name + " has been updated");
      Logger.info("@C Stores -->> update("+id+") <<--");
      return redirect(
      		routes.Stores.edit(id,"MODIFY")
    		  );
	  }
  
  
  public static Result save() {
	  Logger.info("@C Stores -->> save() -->> "); 
	  Form<Store> storeForm = form(Store.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
	  
	 
	  final Map<String, String[]> values = request().body().asFormUrlEncoded();
	  
	  flash("logout", "You've been logged out");
      if(storeForm.hasErrors()||contactForm.hasErrors()||addressForm.hasErrors()) {
    	  Logger.error("@C Stores -->> save() -->> Form has some errors");
          return badRequest(show.render(Supplier.page(0, 10, "name", "asc", ""),SalesHead.page(0, pageSize, "name", "asc", ""),MediaTender.page(0, pageSize, "name", "asc", ""),ExpenseHead.page(0, pageSize, "name", "asc", ""),TerminalHead.page(0, 10, "name", "asc", ""),TotalSalesHead.page(0, 10, "name", "asc", ""),PaymentTender.page(0, 10, "name", "asc", ""),storeForm,contactForm,addressForm));
      }
	 String messages[] = values.get("salesheadslist[]");
	
	  List<SalesHead> salesHeadList = SalesHead.getSalesHeadList(messages);
	  
	  
	  // removing totalsaleshead list by passing dummy list i.e empty list ., Because we are not using it. If remove it it may cause errors existing code where ever it had been used 
	 /*  messages = values.get("totalsalesheadslist[]");
		
		  List<TotalSalesHead> totalSalesHeadList = TotalSalesHead.getTotalSalesHeadList(messages); */
		  
		  
		  List<TotalSalesHead> totalSalesHeadList = new ArrayList<TotalSalesHead>();
	  
	  messages = values.get("mediatenderslist[]");

	  List<MediaTender> mediaTenderList = MediaTender.getMediaTenderList(messages);
	  
	  messages = values.get("expenseheadslist[]");

	  List<ExpenseHead> expenseHeadList = ExpenseHead.getExpenseHeadList(messages);
	  
	
	  messages = values.get("terminalheadslist[]");
	  List<TerminalHead> terminalheadslist = TerminalHead.getTerminalHeadList(messages);
	  
	  messages = values.get("paymentTenderslist[]");
	  List<PaymentTender> paymentTenderslist = null;
	  // check for null.for that store no payment Tenders then assign empty ArrayList
	  if(messages == null){
		  
		  paymentTenderslist = new ArrayList<PaymentTender>();
	  }else{
		  
		  paymentTenderslist = PaymentTender.getPaymentTenderList(messages);
	  }
	  
      contactForm.get().save();
      addressForm.get().save();
      ContactInfo contact = ContactInfo.findByEmail(form().bindFromRequest().get("email")) ;
      Address address = Address.findByAddress(form().bindFromRequest().get("street"), form().bindFromRequest().get("postalCode")); 
      long companyid = Long.valueOf(form().bindFromRequest().get("company.id")).longValue() ;
      Company company = Company.find.byId(companyid);
   
     // company.save();
      Store.create(form().bindFromRequest().get("name"),form().bindFromRequest().get("description"), contact, address,
    		 company,salesHeadList,mediaTenderList,expenseHeadList,terminalheadslist,totalSalesHeadList,paymentTenderslist);
      flash("action", "store");
      flash("success", "Store " + storeForm.get().name + " has been saved");
      Logger.info("@C Stores -->> save() <<--");
      
      return redirect(
        		routes.Stores.showBlank()
      		  );

	  }
 
   
  public static void saveSupplierMapping(Store store) {
	  
	  Logger.info("@C Stores -->> saveSupplierMapping(store) -->>");
    	
	  
   		String terms;
   		String mode;
   		
   		DynamicForm supplierMappingForm=Form.form().bindFromRequest();
   		
   	//	System.out.println("SupplierMapping : form "+supplierMappingForm);
      
         for(SupplierMapping supplierMapping: store.supplierMapping){
        	 
        	 terms = supplierMapping.id.toString()+"_paymentTerms";
        	   
        	 terms = form().bindFromRequest().get(terms);
        	 
        	 mode = supplierMapping.id.toString()+"_paymentMode"; 
        	 
        	 mode = form().bindFromRequest().get(mode);
        	 
        	
        	 supplierMapping.paymentTerms = terms;
        	// System.out.println("SupplierMapping : terms "+supplierMapping.paymentTerms);
        	 
        	 supplierMapping.paymentMode = mode;
        	        	 	        	 
        	// System.out.println("SupplierMapping : mode "+supplierMapping.paymentMode);
        	 supplierMapping.update(supplierMapping.id);
        	 store.update(store.id);
	   	    	
         }
         Logger.info("@C Stores -->> saveSupplierMapping(store) <<--");
   	     
  }
  
  public static List<Store> findByName(String term) { 
	  Logger.info("@C Stores -->> findByName("+term+") -->> ");
	  for (Store candidate : Store.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  Store.create(candidate);  
			  } 
		  } 
	  Logger.info("@C Stores -->> findByName("+term+") <<--");
    return Store.all();
    }
  public static Result delete(Long id) {
	  
	  Logger.info("@C Stores -->> delete("+id+") -->> ");
	 
	  Store store = Store.find.byId(id);
	   List<DailyReconciliation> drList =  DailyReconciliation.find.where().eq("store.id", id).findList();
	   
	   //if drList size is zero then mark this store as DELETED
	   if(drList.size() == 0){
		  
		   Ebean.createSqlUpdate(
		            "DELETE FROM store_expense_head WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		  Ebean.createSqlUpdate(
		            "DELETE FROM store_sales_head WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		  Ebean.createSqlUpdate(
		            "DELETE FROM store_terminal_head WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		  Ebean.createSqlUpdate(
		            "DELETE FROM store_total_sales_head WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		  Ebean.createSqlUpdate(
		            "DELETE FROM store_media_tender WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		  Ebean.createSqlUpdate(
		            "DELETE FROM store_payment_tender WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		  Ebean.createSqlUpdate(
		            "DELETE FROM supplier_mapping WHERE store_id =:id"
		        ).setParameter("id", id).execute();
		   
		   Store.delete(id);
		   
		   flash("success", "Store "+store.name+" has been deleted");
	   }else{
		   
		   flash("success", "Store "+store.name+" Not deleted. based on this store some Rconciliations are present");
	   }
	  	
	   flash("action", "store");
	  	Logger.info("@C Stores -->> delete("+id+") <<--");
	  	
	  	 return redirect(
	  			routes.Application.showOptions("STORE","DELETE")
	      		  );
	  	
	  	 
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
	 
	  flash("pageAction", pageAction);
	  Logger.info("@C Stores -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "store");
	  return ok(list.render(
              Store.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }
  
  //getStores() used in Reports page ,to display store names in select box
  
  public static List<Store> getStoresList(){
	  
	   return Store.page(0, 30, "name", "asc", "").getList();
	  
  }
  
  // showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
  	
  	
  	flash("pageAction",action);
  	
  	if(action.equals("ADD")){
  		
  		 return redirect(
	            		routes.Stores.showBlank()
	            );
  	}
  	else if(action.equals("ALL")){
  		
  		return ok(manage1.render(page)); 
  	}
  	
  	else{
  		
  		
  		return redirect(
 				 routes.Stores.list(0, "name", "asc", "",action)
	            );
 		
  	}
  	
  }
  
}