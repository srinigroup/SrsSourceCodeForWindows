package controllers;
import java.util.List;

import play.Logger;

import com.avaje.ebean.Ebean;

import models.MediaTender;
import models.PaymentTender;
import views.html.*;
import views.html.paymentTenders.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)  
public class PaymentTenders extends Controller {

	
    
	private static final Form<PaymentTender> paymentTenderForm = form(PaymentTender.class); 
	
	
	/**
     * This result directly redirect to  home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.PaymentTenders.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C PaymentTenders -->> showBlank() -->> ");
		 flash("action", "paymentTender");
		return ok(show.render(paymentTenderForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C PaymentTenders -->> edit("+id+") -->> ");
	  PaymentTender paymentTender = PaymentTender.find.byId(id);
	  Form<PaymentTender> paymentTenderForm = form(PaymentTender.class).fill(paymentTender);
      
	  flash("action", "paymentTender");
	  Logger.info("@C PaymentTenders -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, paymentTenderForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C PaymentTenders -->> update("+id+") -->> "); 
	  PaymentTender paymentTender = PaymentTender.find.byId(id);
	  Form<PaymentTender> paymentTenderForm = form(PaymentTender.class).bindFromRequest();
	 
      if(paymentTenderForm.hasErrors()) {
    	  Logger.error("@C PaymentTenders -->> update("+id+") -->> paymentTenderForm has some errors");
          return badRequest(editForm.render(id, paymentTenderForm));
      }
      
      paymentTenderForm.get().update(id);
      flash("action", "paymentTender");
      flash("success", paymentTenderForm.get().name + " has been updated");
      Logger.info("@C PaymentTenders -->> update("+id+") <<--");
      return redirect(
      		routes.PaymentTenders.edit(id,"MODIFY")
    		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C PaymentTenders -->> save() -->> "); 
	  Form<PaymentTender> paymentTenderForm = form(PaymentTender.class).bindFromRequest();
	 
	  flash("logout", "You've been logged out");
      if(paymentTenderForm.hasErrors()) {
    	  Logger.error("@C PaymentTenders -->> save() -->> mediaTenderForm has some errors"); 
          return badRequest(show.render(paymentTenderForm));
      }
     
 
      PaymentTender.create(paymentTenderForm.get());
   
      flash("action", "paymentTender");
      flash("success", "PaymentTender " + paymentTenderForm.get().name + " has been saved");
      Logger.info("@C PaymentTenders -->> save() <<--"); 
      return redirect(
        		routes.PaymentTenders.showBlank()
      		  );

	  }
 
  public static Result delete(Long id) {
	  Logger.info("@C PaymentTenders -->> delete("+id+") -->> "); 
	  PaymentTender paymentTender = PaymentTender.find.byId(id);
	  	
	  int count =  Ebean.createSqlQuery(
	            "SELECT * FROM store_payment_tender WHERE payment_tender_id =:id"
	        ).setParameter("id", id).findList().size();
	
	// decide this head is mapped to any store or not
	if(count == 0){
		Ebean.createSqlUpdate(
	            "DELETE FROM store_payment_tender WHERE payment_tender_id =:id"
	        ).setParameter("id", id).execute();
	
		PaymentTender.delete(id);
		 flash("success", "PaymentTender "+paymentTender.name+" has been deleted");
	}else{
		
		flash("success", "PaymentTender "+paymentTender.name+" Not deleted, This PaymentTender is mapped with some Store");
	}
	 
	  	 flash("action", "paymentTender");
	  	Logger.info("@C PaymentTenders -->> delete("+id+") <<--"); 
	  	 return redirect(
		  			routes.Application.showOptions("PAYMENT TENDER","DELETE")
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
	  
	  Logger.info("@C PaymentTenders -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "paymentTender");
	  return ok(list.render(
			  PaymentTender.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }
  
  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.PaymentTenders.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.PaymentTenders.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
}