package controllers;
import java.util.List;









import com.avaje.ebean.Ebean;

import play.Logger;
import models.MediaTender;
import models.TerminalHead;
import views.html.*;
import views.html.terminalheads.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)  
public class TerminalHeads extends Controller {

	
    
	private static final Form<TerminalHead> terminalHeadForm = form(TerminalHead.class); 
	
	
	/**
     * This result directly redirect to  home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.TerminalHeads.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C TerminalHeads -->> showBlank() -->> ");
		 flash("action", "terminalhead");
		return ok(show.render(terminalHeadForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C TerminalHeads -->> edit("+id+") -->> ");
	  TerminalHead terminalHead = TerminalHead.find.byId(id);
	  Form<TerminalHead> terminalHeadForm = form(TerminalHead.class).fill(terminalHead);
      
	  flash("action", "terminalhead");
	  Logger.info("@C TerminalHeads -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, terminalHeadForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C TerminalHeads -->> update("+id+") -->> "); 
	  TerminalHead terminalHead = TerminalHead.find.byId(id);
	  Form<TerminalHead> terminalHeadForm = form(TerminalHead.class).bindFromRequest();
	 
      if(terminalHeadForm.hasErrors()) {
    	  Logger.error("@C TerminalHeads -->> update("+id+") -->> terminalHeadForm has some errors"); 
          return badRequest(editForm.render(id, terminalHeadForm));
      }
      
      terminalHeadForm.get().update(id);
      flash("action", "terminalhead");
      flash("success", terminalHeadForm.get().name + " has been updated");
      Logger.info("@C TerminalHeads -->> update("+id+") <<--"); 
      return redirect(
      		routes.TerminalHeads.edit(id,"MODIFY")
    		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C TerminalHeads -->> save() -->> ");
	  Form<TerminalHead> terminalHeadForm = form(TerminalHead.class).bindFromRequest();
	 
	  flash("logout", "You've been logged out");
      if(terminalHeadForm.hasErrors()) {
    	  Logger.error("@C TerminalHeads -->> save() -->> terminalHeadForm has some errors");
          return badRequest(show.render(terminalHeadForm));
      }
     
 
      TerminalHead.create(terminalHeadForm.get());
   
      flash("action", "terminalhead");
      flash("success", "TerminalHead " + terminalHeadForm.get().name + " has been saved");
      Logger.info("@C TerminalHeads -->> save() <<--");
      return redirect(
        		routes.TerminalHeads.showBlank()
      		  ); 

	  }
 
   
  
  public static List<TerminalHead> findByName(String term) { 
	  Logger.info("@C TerminalHeads -->> findByName("+term+") -->> "); 
	  for (TerminalHead candidate : TerminalHead.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  TerminalHead.create(candidate);  
			  } 
		  } 
	  Logger.info("@C TerminalHeads -->> findByName("+term+") <<--"); 
    return TerminalHead.all();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C TerminalHeads -->> delete("+id+") -->> ");
	  TerminalHead terminalHead = TerminalHead.find.byId(id);
	  
	  int count =  Ebean.createSqlQuery(
	            "SELECT * FROM store_terminal_head WHERE terminal_head_id =:id"
	        ).setParameter("id", id).findList().size();
	
	// decide this head is mapped to any store or not
	if(count == 0){
		
		Ebean.createSqlUpdate(
	            "DELETE FROM store_terminal_head WHERE terminal_head_id =:id"
	        ).setParameter("id", id).execute();
	
		TerminalHead.delete(id);
		flash("success", "TerminalHead "+terminalHead.name+" has been deleted");
	}else{
		
		flash("success", "TerminalHead "+terminalHead.name+" Not deleted, This TerminalHead is mapped with some Store");
	}
	  
	  	 flash("action", "terminalhead");
	  	Logger.info("@C TerminalHeads -->> delete("+id+") <<--");
	  	return redirect(
	  			routes.Application.showOptions("TERMINAL HEAD","DELETE")
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
	  
	  Logger.info("@C TerminalHeads -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "terminalhead");
	  return ok(list.render(
			  TerminalHead.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.TerminalHeads.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.TerminalHeads.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
  
  
}