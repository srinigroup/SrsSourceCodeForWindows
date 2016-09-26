package controllers;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.Address;
import models.Company;
import models.ContactInfo;
import models.Store;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.companies.*;
import static play.data.Form.*;
import play.Logger;

@Security.Authenticated(Secured.class)  
public class Companies extends Controller {

	
    
	private static final Form<Company> companyForm = form(Company.class); 
	private static final Form<ContactInfo> contactForm = form(ContactInfo.class); 
	private static final Form<Address> addressForm = form(Address.class); 
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.Companies.list(0, "name", "asc", "","VIEW")
    );

	public static Result showBlank(){ 
		Logger.info("@C Companies -->> showBlank() -->>");
		flash("action", "company");
		return ok(show.render(companyForm,contactForm,addressForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  
	 public static Result edit(Long id,String action) {
		 
	  Logger.info("@C Companies -->> edit("+id+") -->>");
	  flash("pageAction", action);
	  
	  Company company = Company.find.byId(id);
      Form<Company> companyForm = form(Company.class).fill(company);
      Form<ContactInfo> contactForm = form(ContactInfo.class).fill(
    		  ContactInfo.find.byId(company.contactInfo.id));
      Form<Address> addressForm  = form(Address.class).fill(
    		  Address.find.byId(company.address.id));
      flash("action", "company");
      Logger.info("@C Companies -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, companyForm,contactForm,addressForm,company)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C Companies -->> update("+id+") -->>"); 
	  Company company = Company.find.byId(id);
	  Form<Company> companyForm = form(Company.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
      if(companyForm.hasErrors()) {
    	  Logger.error("@C Companies -->> update("+id+") -->> companyForm has some errors");
          return badRequest(editForm.render(id, companyForm, contactForm,addressForm,company));
      }
      contactForm.get().update(company.contactInfo.id);
      addressForm.get().update(company.address.id);
      companyForm.get().update(id);
      flash("action", "company");
      flash("success", companyForm.get().name + " has been updated");
      Logger.info("@C Companies -->> update("+id+") -->>"); 
      return redirect(
        		routes.Companies.edit(id,"MODIFY")
      		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C Companies -->> save() -->>");
	  Form<Company> companyForm = form(Company.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
	  flash("logout", "You've been logged out");
	  
      if(companyForm.hasErrors()||contactForm.hasErrors()||addressForm.hasErrors()) {
    	 
    	  Logger.error("@C Companies -->> save() -->> Form has some errors");
    	  
          return badRequest(show.render(companyForm,contactForm,addressForm));
      }
      contactForm.get().save();
      addressForm.get().save();
      ContactInfo contact = ContactInfo.findByEmail(form().bindFromRequest().get("email")) ;
     
      
      Address address = Address.findByAddress(form().bindFromRequest().get("street"), form().bindFromRequest().get("postalCode"));
     
    /*  Company company = companyForm.get();
      company.contactInfo = contact;
      company.address = address;
      */
    Company.create(form().bindFromRequest().get("name"),form().bindFromRequest().get("description"),form().bindFromRequest().get("director"), contact, address);
   
   //  Company company = Company.create(form().bindFromRequest().get("name"),form().bindFromRequest().get("description"),form().bindFromRequest().get("email"));
  //    Company company = Company.create(comany,contact,address);
      flash("success", "Company " + companyForm.get().name + " has been saved");
      flash("action", "company");
      Logger.info("@C Companies -->> save() <<--");
      return redirect(
      		routes.Companies.showBlank()
    		  ); 

	  }
 
 
 
   
  
  public static List<Company> findByName(String term) { 
	  Logger.info("@C Companies -->> findByName("+term+") -->>");
	  for (Company candidate : Company.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  Company.create(candidate);  
			  } 
		  } 
	  Logger.info("@C Companies -->> findByName("+term+") <<--");
    return Company.all();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C Companies -->> delete("+id+") -->>");
	 
	  Company company = Company.find.byId(id);
	  List<Store> storeList = company.stores;
	  
	  // based on list size we will come to know any stores are mapped to this company
	  if(storeList.size() == 0){
		  
		  Company.delete(id);
		  flash("success", "Company "+company.name+" has been deleted");
	  }else{
		  
		  flash("success", "Company "+company.name+" Not deleted, Some Stores are associated with this company");
	  }
	 
	  	flash("action", "company");
	  	Logger.info("@C Companies -->> delete("+id+") <<--");
	  	return redirect(
	  			routes.Application.showOptions("COMPANY","DELETE")
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
	  Logger.info("@C Companies -->> list("+page+","+sortBy+","+order+","+filter+","+pageAction+") -->>");
	  flash("action", "company");
	  flash("pageAction", pageAction);
	  return ok(list.render(
              Company.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }
  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  Logger.info("@C Companies -->> showOptions("+page+","+action+") -->>");
	  
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.Companies.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		return redirect(
	 				 routes.Companies.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }

 
}