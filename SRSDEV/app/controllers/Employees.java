package controllers;

import java.io.File;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

import play.Logger;
import models.Address;
import models.BankAccountInfo;
import models.ContactInfo;
import models.Employee;
import models.Store;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.*;
import views.html.employees.*;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)  
public class Employees extends Controller {

	
    
	private static final Form<Employee> employeeForm = form(Employee.class); 
	private static final Form<ContactInfo> contactForm = form(ContactInfo.class); 
	private static final Form<BankAccountInfo> bankAccountForm = form(BankAccountInfo.class); 
	private static final Form<Address> addressForm = form(Address.class); 
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.Employees.list(0, "first_name", "asc", "","VIEW")
    );

	public static Result showBlank(){ 
		Logger.info("@C Employees -->> showBlank() -->> ");
		flash("action", "employee");
		return ok(show.render(employeeForm,contactForm,addressForm,bankAccountForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  
	 public static Result edit(Long id,String action) {
		 
	  Logger.info("@C Employees -->> edit("+id+") -->> ");
	  flash("pageAction", action);
	  
	  Employee employee = Employee.find.byId(id);
      Form<Employee> employeeForm = form(Employee.class).fill(employee);
     
     
      Form<ContactInfo> contactForm = form(ContactInfo.class).fill(
    		  ContactInfo.find.byId(employee.contactInfo.id));
      Form<Address> addressForm  = form(Address.class).fill(
    		  Address.find.byId(employee.address.id));
      Form<BankAccountInfo> bankAccountForm = form(BankAccountInfo.class).fill(
    		  BankAccountInfo.find.byId(employee.bankAccountInfo.id));
      flash("action", "employee");
      Logger.info("@C Employees -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, employeeForm,contactForm,addressForm,bankAccountForm,employee)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C Employees -->> update("+id+") -->> "); 
	  Employee employee = Employee.find.byId(id);
	  Form<Employee> employeeForm = form(Employee.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
	  Form<BankAccountInfo> bankAccountForm = form(BankAccountInfo.class).bindFromRequest();
      if(employeeForm.hasErrors()) {
    	  Logger.error("@C Employees -->> update("+id+") -->> employeeForm has some errors"); 
          return badRequest(editForm.render(id, employeeForm, contactForm,addressForm,bankAccountForm,employee));
      }
      contactForm.get().update(employee.contactInfo.id);
      addressForm.get().update(employee.address.id);
      bankAccountForm.get().update(employee.bankAccountInfo.id);
      play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
    
      Application.upload(employee, body);
      employeeForm.get().update(id);
      flash("action", "employee");
      flash("success", employeeForm.get().firstName + " has been updated");
      Logger.info("@C Employees -->> update("+id+") <<--"); 
      return redirect(
        		routes.Employees.edit(id,"MODIFY")
      		  );

	  }
  
 
  public static Result save() {
	  Logger.info("@C Employees -->> save() -->> ");  
	  Form<Employee> employeeForm = form(Employee.class).bindFromRequest();
	  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
	  Form<Address> addressForm = form(Address.class).bindFromRequest();
	  Form<BankAccountInfo> bankAccountForm = form(BankAccountInfo.class).bindFromRequest(); 
	 
	
      if(employeeForm.hasErrors()||contactForm.hasErrors()||addressForm.hasErrors()) {
    	 
    	  Logger.error("@C Employees -->> save() -->> Form has some errors");  
    	  
          return badRequest(show.render(employeeForm,contactForm,addressForm,bankAccountForm));
      }
    
      play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
     
      contactForm.get().save();
      addressForm.get().save();
      bankAccountForm.get().save();
      ContactInfo contact = ContactInfo.findByEmail(form().bindFromRequest().get("email")) ;
     
      
      Address address = Address.findByAddress(form().bindFromRequest().get("street"), form().bindFromRequest().get("postalCode"));
      BankAccountInfo bankAccount = BankAccountInfo.findByAccountNumber(form().bindFromRequest().get("accountNumber"));
     
      Employee employee = employeeForm.get();
      employee.contactInfo = contact;
      employee.address = address;
      employee.bankAccountInfo = bankAccount;
    
      Employee.create(employee);
      Application.upload(employee, body);
   
      //  Company company = Company.create(form().bindFromRequest().get("name"),form().bindFromRequest().get("description"),form().bindFromRequest().get("email"));
  
      //    Company company = Company.create(comany,contact,address);
      flash("action", "employee");
      flash("success", "Employee " + employeeForm.get().firstName + " has been Saved");
      Logger.info("@C Employees -->> save() <<--");  
      
      return redirect(
      		routes.Employees.showBlank()
    		  );

	  }
 
 
 
   
  
   
  public static Result delete(Long id) {
	  Logger.info("@C Employees -->> delete("+id+") -->> "); 
	  Employee employee = Employee.find.byId(id);
	  	flash("success", "Employee "+employee.firstName+" "+ employee.lastName+" has been deleted");
	  flash("action", "employee");
	  Employee.delete(id);
	  Logger.info("@C Employees -->> delete("+id+") <<--"); 
	  
	  return redirect(
	  			routes.Application.showOptions("STAFF","DELETE")
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
	  Logger.info("@C Employees -->> list--->("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "employee");
	  flash("pageAction", pageAction);
	  return ok(list.render(
              Employee.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

  public static Result listForFilterByStore(int page, String sortBy, String order, String filter,String pageAction) {
	  Logger.info("@C Employees -->> list=========("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "employee");
	  String storeId=form().bindFromRequest().get("sn");
	  Store storeName=Store.find.byId(Long.parseLong(storeId));
	  //System.out.println("STORE ID :"+storeId);
	  List<Employee> l= Employee.page1(page, 10, sortBy, order, storeId);
	  flash("pageAction", pageAction);
	  return ok(listByStoreName.render(
             l,sortBy, order, filter, pageAction,storeName
          )); 
  }
  // to display profile Info of any Employee, Used in top Nav Bar
   public static Result myProfile(Long id){
	   Logger.info("@C Employees -->> myProfile("+id+") -->> "); 
	   
	   Employee employee  = Employee.find.byId(id);
	   Logger.info("@C Employees -->> myProfile("+id+") <<--"); 
	   return ok(profile.render(employee));
   }
  
//showOptions method used in Application.java
  
 public static Result showOptions(String page,String action) {
 	
 	
 	flash("pageAction",action);
 	
 	if(action.equals("ADD")){
 		
 		 return redirect(
	            		routes.Employees.showBlank()
	            );
 	}
 	else if(action.equals("ALL")){
 		
 		return ok(manage1.render(page)); 
 	}
 	
 	else{
 		
 		
 		return redirect(
				 routes.Employees.list(0, "first_name", "asc", "",action)
	            );
		
 	}
 	
 }
 
 public static List<Employee> getEmployeeList(){
	  
	   return Employee.page(0, 30, "first_name", "asc", "").getList();
	  
}

  
}