package controllers;

import static play.data.Form.form;
import models.AccountHolder;
import models.Address;
import models.ContactInfo;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.manage1;
import views.html.accountholders.*;
public class AccountHolders extends Controller {
	
	private static final Form<AccountHolder> accountHolderForm = form(AccountHolder.class); 
	private static final Form<ContactInfo> contactForm = form(ContactInfo.class); 
	private static final Form<Address> addressForm = form(Address.class);
	
    

	
	
		public static Result showBlank(){
			return ok(show.render(accountHolderForm,contactForm,addressForm));
		}
	
		public static Result save() {
		  Logger.info("@C AccountHolder -->> save() -->>"); 
		  Form<AccountHolder> accountHolderForm = form(AccountHolder.class).bindFromRequest();
		  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
		  Form<Address> addressForm = form(Address.class).bindFromRequest();
		  
	      if(accountHolderForm.hasErrors()||contactForm.hasErrors()||addressForm.hasErrors()) {
	    	  Logger.error("@C Suppliers -->> save() -->> from has some errors");
	          return badRequest(show.render(accountHolderForm,contactForm,addressForm));
	      }
	      contactForm.get().save();
	      addressForm.get().save();
	      ContactInfo contact = ContactInfo.findByEmail(form().bindFromRequest().get("email")) ;
	      Address address = Address.findByAddress(form().bindFromRequest().get("street"), form().bindFromRequest().get("postalCode"));
	     // Store store=Store.findByStore(form().bindFromRequest().get("store.id"));

	      AccountHolder account = accountHolderForm.get();
	      account.contactInfo=contact;
	      account.address=address;
	      
	      AccountHolder.create(account);
	     //AccountHolder.create(form().bindFromRequest().get("accountHolder"),store, contact, address);
	   
	     flash("action", "account");
	      flash("success", "account " + accountHolderForm.get().accountHolder + " has been saved");
	      Logger.info("@C AccountHolder -->> save() <<--");
	      return redirect(
	        		routes.AccountHolders.showBlank()
	      		  );

		  }
	
		public static Result edit(Long id,String action) {
		 
		  Logger.info("@C AccountHolders -->> edit("+id+") -->> ");
		  flash("pageAction", action);
		  
		  AccountHolder account = AccountHolder.find.byId(id);
	      Form<AccountHolder> accountHolderForm = form(AccountHolder.class).fill(account);
	     
	     
	      Form<ContactInfo> contactForm = form(ContactInfo.class).fill(
	    		  ContactInfo.find.byId(account.contactInfo.id));
	      Form<Address> addressForm  = form(Address.class).fill(
	    		  Address.find.byId(account.address.id));
	      	      flash("action", "employee");
	      Logger.info("@C AccountHolders -->> edit("+id+") <<--");
	      return ok(
	          editForm.render(id, accountHolderForm,contactForm,addressForm,account)
	      );
	  }
		
		public static Result update(Long id) {
			  Logger.info("@C Employees -->> update("+id+") -->> "); 
			  AccountHolder account = AccountHolder.find.byId(id);
			  Form<AccountHolder> accountHolderForm = form(AccountHolder.class).bindFromRequest();
			  Form<ContactInfo> contactForm = form(ContactInfo.class).bindFromRequest();
			  Form<Address> addressForm = form(Address.class).bindFromRequest();
		      if(accountHolderForm.hasErrors()) {
		    	  Logger.error("@C Employees -->> update("+id+") -->> employeeForm has some errors"); 
		          return badRequest(editForm.render(id, accountHolderForm, contactForm,addressForm,account));
		      }
		      contactForm.get().update(account.contactInfo.id);
		      addressForm.get().update(account.address.id);
		      accountHolderForm.get().update(id);
		      flash("action", "employee");
		      flash("success", accountHolderForm.get().accountHolder + " has been updated");
		      Logger.info("@C AccountHolders -->> update("+id+") <<--"); 
		      return redirect(
		        		routes.AccountHolders.edit(id,"MODIFY")
		      		  );

			  }
				
		 	  public static Result delete(Long id) {
			  Logger.info("@C AccountHolders -->> delete("+id+") -->> "); 
			  AccountHolder account = AccountHolder.find.byId(id);
			  	flash("success", "AccountHolder "+account.accountHolder+"  has been deleted");
			  flash("action", "account");
			  AccountHolder.delete(id);
			  Logger.info("@C AccountHolder -->> delete("+id+") <<--"); 
			  
			  return redirect(
			  			routes.Application.showOptions("ACCOUNT HOLDER","DELETE")
			      		  );
			  }

		 
		 	
	
			public static Result list(int page, String sortBy, String order, String filter,String pageAction) {
			Logger.info("@C AccountHolder -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
			flash("action", "account");
			flash("pageAction", pageAction);
			return ok(list.render(
	              AccountHolder.page(page, 10, sortBy, order, filter),
	              sortBy, order, filter, pageAction
	          )); 
			}


	
	
	
	
	public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.AccountHolders.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		return redirect(
					 routes.AccountHolders.list(0, "account_holder", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
  

	
}
