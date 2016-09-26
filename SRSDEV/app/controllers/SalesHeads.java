package controllers;
import java.util.List;
import java.util.Map;
import java.util.Date;










import com.avaje.ebean.Ebean;

import play.Logger;
import models.SalesHead;
import models.Store;
import views.html.*;
import views.html.salesheads.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)  
public class SalesHeads extends Controller {

	
    
	private static final Form<SalesHead> salesheadForm = form(SalesHead.class); 
	
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.SalesHeads.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C SalesHeads -->> showBlank() -->> ");
		  flash("action", "saleshead");
		return ok(show.render(salesheadForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C SalesHeads -->> edit("+id+") -->> ");
	  SalesHead salesHead = SalesHead.find.byId(id);
	  Form<SalesHead> salesHeadForm = form(SalesHead.class).fill(salesHead);
      
	  flash("action", "saleshead");
	  Logger.info("@C SalesHeads -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, salesHeadForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C SalesHeads -->> update("+id+") -->> "); 
	  SalesHead salesHead = SalesHead.find.byId(id);
	  Form<SalesHead> salesHeadForm = form(SalesHead.class).bindFromRequest();
	 
      if(salesHeadForm.hasErrors()) {
    	  Logger.error("@C SalesHeads -->> update("+id+") -->> salesHeadForm has some errors "); 
          return badRequest(editForm.render(id, salesHeadForm));
      }
      
      salesHead.created_date=new Date();
      salesHead.save();
      salesHeadForm.get().update(id);
      flash("action", "saleshead");
      flash("success", salesHeadForm.get().name + " has been updated");
      Logger.info("@C SalesHeads -->> update("+id+") <<--");
      return redirect(
        		routes.SalesHeads.edit(id,"MODIFY")
      		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C SalesHeads -->> save() -->> ");
	  Form<SalesHead> salesHeadForm = form(SalesHead.class).bindFromRequest();
	 
	  flash("logout", "You've been logged out");
      if(salesHeadForm.hasErrors()) {
    	  Logger.error("@C SalesHeads -->> save() -->> salesHeadForm has some errors");
          return badRequest(show.render(salesHeadForm));
      }
     
      SalesHead.create(salesHeadForm.get());
   
      flash("action", "saleshead");
      flash("success", "Saleshead " + salesHeadForm.get().name + " has been saved");
      Logger.info("@C SalesHeads -->> save() <<--");
      return redirect(
      		routes.SalesHeads.showBlank()
    		  ); 

	  }
 
   
  
  public static List<SalesHead> findByName(String term) { 
	  Logger.info("@C SalesHeads -->> findByName("+term+") -->> ");
	  for (SalesHead candidate : SalesHead.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  SalesHead.create(candidate);  
			  } 
		  } 
	  Logger.info("@C SalesHeads -->> findByName("+term+") <<--");
    return SalesHead.all();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C SalesHeads -->> delete("+id+") -->> ");
	  
	  SalesHead salesHead = SalesHead.find.byId(id);
	 
	int count =  Ebean.createSqlQuery(
	            "SELECT * FROM store_sales_head WHERE sales_head_id =:id"
	        ).setParameter("id", id).findList().size();
	
	// decide this head is mapped to any store or not
	if(count == 0){
		Ebean.createSqlUpdate(
	            "DELETE FROM store_sales_head WHERE sales_head_id =:id"
	        ).setParameter("id", id).execute();
	
	    SalesHead.delete(id);
	    flash("success", "SalesHead "+salesHead.name+" has been deleted");
	}else{
		
		flash("success", "SalesHead "+salesHead.name+" Not deleted, This SalesHead is mapped with some Store");
	}
	    
	    flash("action", "saleshead");
	    Logger.info("@C SalesHeads -->> delete("+id+") <<--");
	    return redirect(
	  			routes.Application.showOptions("SALES HEAD","DELETE")
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
	  
	  Logger.info("@C SalesHeads -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "saleshead");
	  return ok(list.render(
              SalesHead.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

//getSalesHeadsList() used in Reports page ,to display SalesHead names in select box
  
  public static List<SalesHead> getSalesHeadsList(){
	  
	   return SalesHead.page(0, 20, "name", "asc", "").getList();
	  
  }
  
  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.SalesHeads.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.SalesHeads.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
  
}