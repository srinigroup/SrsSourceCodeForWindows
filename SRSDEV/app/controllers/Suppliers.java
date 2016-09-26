package controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.Address;
import models.ContactInfo;
import models.MediaTender;
import models.Store;
import models.Supplier;
import models.SupplierMapping;
import play.Routes;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.suppliers.*;
import static play.data.Form.*;
import play.Logger;

@Security.Authenticated(Secured.class)
public class Suppliers extends Controller {

	
    
	private static final Form<Supplier> supplierForm = form(Supplier.class); 
	private static final Form<ContactInfo> contactForm = form(ContactInfo.class); 
	private static final Form<Address> addressForm = form(Address.class); 
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.Suppliers.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C Suppliers -->> showBlank() -->>");
		 flash("action", "supplier");
		return ok(show.render(supplierForm,contactForm,addressForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C Suppliers -->> edit("+id+") -->>");
	  Supplier supplier = Supplier.find.byId(id);
      Form<Supplier> supplierForm = form(Supplier.class).fill(supplier);
      Form<ContactInfo> contactForm = form(ContactInfo.class).fill(
    		  ContactInfo.find.byId(supplier.contactInfo.id));
      Form<Address> addressForm  = form(Address.class).fill(
    		  Address.find.byId(supplier.address.id));
      flash("action", "supplier");
      Logger.info("@C Suppliers -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, supplierForm,contactForm,addressForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C Suppliers -->> update("+id+") -->>");	 
	  Supplier supplier = Supplier.find.byId(id);
	  Form<Supplier> supplierForm = form(Supplier.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
      if(supplierForm.hasErrors()) {
    	  Logger.error("@C Suppliers -->> update("+id+") -->> supplierForm has some errors");
          return badRequest(editForm.render(id, supplierForm, contactForm,addressForm));
      }
      contactForm.get().update(supplier.contactInfo.id);
      addressForm.get().update(supplier.address.id);
      supplierForm.get().update(id);
      flash("action", "supplier");
      flash("success", supplierForm.get().name + " has been updated");
      Logger.info("@C Suppliers -->> update("+id+") <<--");	 
      return redirect(
      		routes.Suppliers.edit(id,"MODIFY")
    		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C Suppliers -->> save() -->>"); 
	  Form<Supplier> supplierForm = form(Supplier.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
	  flash("logout", "You've been logged out");
      if(supplierForm.hasErrors()||contactForm.hasErrors()||addressForm.hasErrors()) {
    	  Logger.error("@C Suppliers -->> save() -->> from has some errors");
          return badRequest(show.render(supplierForm,contactForm,addressForm));
      }
      contactForm.get().save();
      addressForm.get().save();
      ContactInfo contact = ContactInfo.findByEmail(form().bindFromRequest().get("email")) ;
     
      
      Address address = Address.findByAddress(form().bindFromRequest().get("street"), form().bindFromRequest().get("postalCode"));
      
 
     Supplier.create(form().bindFromRequest().get("name"),form().bindFromRequest().get("description"), form().bindFromRequest().get("abn"),contact, address);
   
     flash("action", "supplier");
      flash("success", "Supplier " + supplierForm.get().name + " has been saved");
      Logger.info("@C Suppliers -->> save() <<--");
      return redirect(
        		routes.Suppliers.showBlank()
      		  );

	  }
 
   
  
  public static List<Supplier> findByName(String term) { 
	  Logger.info("@C Suppliers -->> findByName("+term+") -->>");
	  for (Supplier candidate : Supplier.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  Supplier.create(candidate);  
			  } 
		  } 
	  Logger.info("@C Suppliers -->> findByName("+term+") <<--");
    return Supplier.all();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C Suppliers -->> delete("+id+") -->>");
	  Supplier supplier = Supplier.find.byId(id);
	  	
	  int count =  Ebean.createSqlQuery(
	            "SELECT * FROM supplier_mapping WHERE supplier_id =:id"
	        ).setParameter("id", id).findList().size();
	
	// decide this head is mapped to any store or not
	if(count == 0){
		Ebean.createSqlUpdate(
	            "DELETE FROM supplier_mapping WHERE supplier_id =:id"
	        ).setParameter("id", id).execute();
	
		Supplier.delete(id);
		flash("success", "Supplier "+supplier.name+" has been deleted");
	}else{
		
		flash("success", "Supplier "+supplier.name+" Not deleted, This Supplier is mapped with some Store");
	}
	  
	  	 flash("action", "supplier");
	  	Logger.info("@C Suppliers -->> delete("+id+") <<--");
	  	return redirect(
	  			routes.Application.showOptions("SUPPLIER","DELETE")
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
	  
	  Logger.info("@C Suppliers -->> list("+page+","+sortBy+","+order+","+filter+") -->>");
	  flash("action", "supplier");
	  return ok(list.render(
              Supplier.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

  public static Result list() {
	  Logger.info("@C Suppliers -->> list() -->>");
	  flash("action", "supplier");
	  return ok(sample1.render(supplierForm)); 
  }
  
  public static Result getSuppliers(String searchKey , String headOffice) {
	  Logger.info("@C Suppliers -->> getSuppliers("+searchKey+") -->> Ajax call method");
	  
	  if(headOffice.equals("HeadOffice")){
	  // Gives list of all suppliers irrespective store, for store level configuration
		  
		  Page<Supplier> result = Supplier.getSuppliersList(0,10,"name", "asc",searchKey);
		  return ok(Json.toJson(result.getList()));
	  }else{
		  
		// Gives list of suppliers belonging to particular store., for payout suppliers
		  
		  //get store from session
		  Application.setSessionContext();
		  Long storeId = Long.parseLong(session("storeid"));
		 
		  List<Supplier> supplierListForStore = new ArrayList<Supplier>();
		  List<SupplierMapping> supplierMappingListForStore = SupplierMapping.getSupplierMappingList(searchKey, storeId);
		  for(SupplierMapping suppliermapping : supplierMappingListForStore){
			  
			  supplierListForStore.add(suppliermapping.supplier);
			  
		  }
		 
		  return ok(Json.toJson(supplierListForStore));
		  
	  }
	 
	}
 
  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.Suppliers.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.Suppliers.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
  
  
}