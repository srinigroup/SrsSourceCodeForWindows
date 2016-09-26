package controllers;
import java.util.List;
import java.util.Date;

import com.avaje.ebean.Ebean;

import models.ExpenseHead;
import models.MediaTender;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.expenseheads.*;
import static play.data.Form.*;
import play.Logger;

@Security.Authenticated(Secured.class)  
public class ExpenseHeads extends Controller {

	
    
	private static final Form<ExpenseHead> expenseHeadForm = form(ExpenseHead.class); 
	
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.ExpenseHeads.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C ExpenseHeads -->> showBlank() -->>");
		flash("action", "expensehead");
		return ok(show.render(expenseHeadForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C ExpenseHeads -->> edit("+id+") -->>");
	  ExpenseHead expenseHead = ExpenseHead.find.byId(id);
	  Form<ExpenseHead> expenseHeadForm = form(ExpenseHead.class).fill(expenseHead);
      
	  flash("action", "expensehead");
	  Logger.info("@C ExpenseHeads -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, expenseHeadForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C ExpenseHeads -->> update("+id+") -->>"); 
	  ExpenseHead expenseHead = ExpenseHead.find.byId(id);
	  Form<ExpenseHead> expenseHeadForm = form(ExpenseHead.class).bindFromRequest();
	 
      if(expenseHeadForm.hasErrors()) {
    	  Logger.error("@C ExpenseHeads -->> update("+id+") -->> expenseHeadForm has some errors");
          return badRequest(editForm.render(id, expenseHeadForm));
      }
      expenseHead.created_date=new Date();
      expenseHead.save();
      expenseHeadForm.get().update(id);
      flash("action", "expensehead");
      flash("success", expenseHeadForm.get().name + " has been updated");
      Logger.info("@C ExpenseHeads -->> update("+id+") <<--");
      return redirect(
      		routes.ExpenseHeads.edit(id,"MODIFY")
    		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C ExpenseHeads -->> save() -->>"); 
	  Form<ExpenseHead> expenseHeadForm = form(ExpenseHead.class).bindFromRequest();
	 
	  flash("action", "expensehead");
      if(expenseHeadForm.hasErrors()) {
    	  Logger.error("@C ExpenseHeads -->> save() -->> expenseHeadForm has some errors"); 
          return badRequest(show.render(expenseHeadForm));
      }
     
 
      ExpenseHead.create(expenseHeadForm.get());
   
  
      flash("success", "ExpenseHead " + expenseHeadForm.get().name + " has been saved");
      Logger.info("@C ExpenseHeads -->> save() <<--"); 
      return redirect(
        		routes.ExpenseHeads.showBlank()
      		  ); 

	  }
 
   
  
  public static List<ExpenseHead> findByName(String term) { 
	  Logger.info("@C ExpenseHeads -->> findByName("+term+") -->>"); 
	  for (ExpenseHead candidate : ExpenseHead.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  ExpenseHead.create(candidate);  
			  } 
		  } 
	  Logger.info("@C ExpenseHeads -->> findByName("+term+") <<--"); 
    return ExpenseHead.all();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C ExpenseHeads -->> delete("+id+") -->>");
	  ExpenseHead expenseHead = ExpenseHead.find.byId(id);
	  
	  int count =  Ebean.createSqlQuery(
	            "SELECT * FROM store_expense_head WHERE expense_head_id =:id"
	        ).setParameter("id", id).findList().size();
	
	// decide this head is mapped to any store or not
	if(count == 0){
		Ebean.createSqlUpdate(
	            "DELETE FROM store_expense_head WHERE expense_head_id =:id"
	        ).setParameter("id", id).execute();
	
		 ExpenseHead.delete(id);
		flash("success", "ExpenseHead "+expenseHead.name+" has been deleted");
	}else{
		
		flash("success", "ExpenseHead "+expenseHead.name+" Not deleted, This ExpenseHead is mapped with some Store");
	}
	   	
	  	flash("action", "expensehead");
	  	 Logger.info("@C ExpenseHeads -->> delete("+id+") <<--");
	  	return redirect(
	  			routes.Application.showOptions("EXPENSE HEAD","DELETE")
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
	  
	  Logger.info("@C ExpenseHeads -->> list("+page+","+sortBy+","+order+","+filter+") -->>");
	  flash("action", "expensehead");
	  return ok(list.render(
              ExpenseHead.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

  
  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.ExpenseHeads.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.ExpenseHeads.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
  
  
}