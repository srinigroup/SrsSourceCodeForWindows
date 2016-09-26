package controllers;

import static play.data.Form.form;
import models.DailySalesReconciliation;
import models.Shift;
import models.Store;
import models.Terminal;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.terminals.*;

import play.*;

@Security.Authenticated(Secured.class)
public class Terminals extends Controller {
	
	public static Result showBlank(Long id){ 
		Form<Shift> shiftForm = form(Shift.class);
		 Store store = Store.find.byId(id);
		 return ok(); 
			//	return ok(show.render(shiftForm,store)); 
		}

public static Result delete(Long id) {
		
		Shift.delete(id);
		  
		  	 flash("action", "DailySalesReconciliation");
		  	return ok();
		  }
	
	public static Result update(Long id) {
		
		
		  
		  	 flash("action", "DailySalesReconciliation");
		  	return ok();
		  }
	 public static Result save() {
	 	 
		  
		 return ok();
		  }
	 
	 
	 //terminal summary
	 public static Result summary(Long id,String thead){
		 
		 Logger.info("@C Terminals -->> summary("+id+","+thead+") -->>");
		 
		 
		 DailySalesReconciliation dsr = DailySalesReconciliation.find.byId(id);
		 Store store = dsr.store;
			Terminal terminal=Terminal.getTerminalbyTerminalHead(dsr, thead);
			 Logger.info("@C Terminals -->> summary("+id+","+thead+") <<--");
			return ok(summary.render(dsr,store,terminal));  
		 
	 }
}
