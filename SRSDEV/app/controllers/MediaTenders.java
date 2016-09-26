package controllers;
import java.util.List;

import play.Logger;

import com.avaje.ebean.Ebean;

import models.MediaTender;
import models.SalesHead;
import views.html.*;
import views.html.mediatenders.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)  
public class MediaTenders extends Controller {

	
    
	private static final Form<MediaTender> mediaTenderForm = form(MediaTender.class); 
	
	
	/**
     * This result directly redirect to  home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.MediaTenders.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C MediaTenders -->> showBlank() -->> ");
		 flash("action", "mediatender");
		return ok(show.render(mediaTenderForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C MediaTenders -->> edit("+id+") -->> ");
	  MediaTender mediaTender = MediaTender.find.byId(id);
	  Form<MediaTender> mediaTenderForm = form(MediaTender.class).fill(mediaTender);
      
	  flash("action", "mediatender");
	  Logger.info("@C MediaTenders -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, mediaTenderForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C MediaTenders -->> update("+id+") -->> "); 
	  MediaTender mediaTender = MediaTender.find.byId(id);
	  Form<MediaTender> mediaTenderForm = form(MediaTender.class).bindFromRequest();
	 
      if(mediaTenderForm.hasErrors()) {
    	  Logger.error("@C MediaTenders -->> update("+id+") -->> mediaTenderForm has some errors");
          return badRequest(editForm.render(id, mediaTenderForm));
      }
      
      mediaTenderForm.get().update(id);
      flash("action", "mediatender");
      flash("success", mediaTenderForm.get().name + " has been updated");
      Logger.info("@C MediaTenders -->> update("+id+") <<--");
      return redirect(
      		routes.MediaTenders.edit(id,"MODIFY")
    		  );

	  }
  
  
  public static Result save() {
	  Logger.info("@C MediaTenders -->> save() -->> "); 
	  Form<MediaTender> mediaTenderForm = form(MediaTender.class).bindFromRequest();
	 
	  flash("logout", "You've been logged out");
      if(mediaTenderForm.hasErrors()) {
    	  Logger.error("@C MediaTenders -->> save() -->> mediaTenderForm has some errors"); 
          return badRequest(show.render(mediaTenderForm));
      }
     
 
      MediaTender.create(mediaTenderForm.get());
   
      flash("action", "mediatender");
      flash("success", "MediaTender " + mediaTenderForm.get().name + " has been saved");
      Logger.info("@C MediaTenders -->> save() <<--"); 
      return redirect(
        		routes.MediaTenders.showBlank()
      		  );

	  }
 
   
  
  public static List<MediaTender> findByName(String term) { 
	  Logger.info("@C MediaTenders -->> findByName("+term+") -->> "); 
	  for (MediaTender candidate : MediaTender.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  MediaTender.create(candidate);  
			  } 
		  } 
	  Logger.info("@C MediaTenders -->> findByName("+term+") <<--");
    return MediaTender.all();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C MediaTenders -->> delete("+id+") -->> "); 
	  MediaTender mediatender = MediaTender.find.byId(id);
	  
	  int count =  Ebean.createSqlQuery(
	            "SELECT * FROM store_media_tender WHERE media_tender_id =:id"
	        ).setParameter("id", id).findList().size();
	
	// decide this head is mapped to any store or not
	if(count == 0){
		Ebean.createSqlUpdate(
	            "DELETE FROM store_media_tender WHERE media_tender_id =:id"
	        ).setParameter("id", id).execute();
	
		MediaTender.delete(id);
	    flash("success", "MediaTender "+mediatender.name+" has been deleted");
	}else{
		
		flash("success", "MediaTender "+mediatender.name+" Not deleted, This MediaTender is mapped with some Store");
	}
	  
	  	 flash("action", "mediatender");
	  	Logger.info("@C MediaTenders -->> delete("+id+") <<--"); 
	  	 return redirect(
		  			routes.Application.showOptions("MEDIA TENDER","DELETE")
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
	  
	  Logger.info("@C MediaTenders -->> list("+page+","+sortBy+","+order+","+filter+","+pageAction+") -->> ");
	  flash("action", "mediatender");
	  return ok(list.render(
              MediaTender.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

//getMediaTendersList() used in Reports page ,to display MediaTender names in select box
  
  public static List<MediaTender> getMediaTendersList(){
	  
	   return MediaTender.page(0, 20, "name", "asc", "").getList();
	  
  }
  
  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  Logger.info("@C MediaTenders -->> showOptions("+page+","+action+") -->> "); 
	  
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.MediaTenders.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.MediaTenders.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }
}