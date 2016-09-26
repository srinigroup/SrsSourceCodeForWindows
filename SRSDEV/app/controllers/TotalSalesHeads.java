package controllers;
import java.util.List;
import play.Logger;
import com.avaje.ebean.Ebean;
import java.util.Date;
import models.SalesHead;
import models.TotalSalesHead;
import views.html.*;
import views.html.totalsalesheads.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)  
public class TotalSalesHeads extends Controller {

	
    
	private static final Form<TotalSalesHead> totalSalesheadForm = form(TotalSalesHead.class); 
	
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     // routes.Companies.list()
      routes.TotalSalesHeads.list(0, "name", "asc", "")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C TotalSalesHeads -->> showBlank() -->> ");
		  flash("action", "totalsaleshead");
		return ok(show.render(totalSalesheadForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id) {
	  Logger.info("@C TotalSalesHeads -->> edit("+id+") -->> ");
	  TotalSalesHead totalSalesHead = TotalSalesHead.find.byId(id);
	  Form<TotalSalesHead> totalSalesHeadForm = form(TotalSalesHead.class).fill(totalSalesHead);
      
	  flash("action", "totalsaleshead");
	  Logger.info("@C TotalSalesHeads -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, totalSalesHeadForm)
      );
  }
  
  
  public static Result update(Long id) {
	  Logger.info("@C TotalSalesHeads -->> update("+id+") -->> ");
	  TotalSalesHead totalSalesHead = TotalSalesHead.find.byId(id);
	  Form<TotalSalesHead> totalSalesHeadForm = form(TotalSalesHead.class).bindFromRequest();
	 
      if(totalSalesHeadForm.hasErrors()) {
    	  Logger.error("@C TotalSalesHeads -->> update("+id+") -->> totalSalesHeadForm has some errors");
          return badRequest(editForm.render(id, totalSalesHeadForm));
      }
      totalSalesHead.created_date=new Date();
      totalSalesHead.save();
      totalSalesHeadForm.get().update(id);
      flash("action", "totalsaleshead");
      flash("success", totalSalesHeadForm.get().name + " has been updated");
      Logger.info("@C TotalSalesHeads -->> update("+id+") <<--");
      return GO_HOME;

	  }
  
  
  public static Result save() {
	  Logger.info("@C TotalSalesHeads -->> save() -->> ");
	  Form<TotalSalesHead> totalSalesHeadForm = form(TotalSalesHead.class).bindFromRequest();
	 
	  flash("logout", "You've been logged out");
      if(totalSalesHeadForm.hasErrors()) {
    	  Logger.error("@C TotalSalesHeads -->> save() -->> totalSalesHeadForm has some errors");
          return badRequest(show.render(totalSalesHeadForm));
      }
     
 
      TotalSalesHead.create(totalSalesHeadForm.get());
   
      flash("action", "totalsaleshead");
      flash("success", "Store " + totalSalesHeadForm.get().name + " has been saved");
      Logger.info("@C TotalSalesHeads -->> save() <<--");
      return GO_HOME; 

	  }
 
   
  
  public static List<TotalSalesHead> findByName(String term) { 
	  Logger.info("@C TotalSalesHeads -->> findByName("+term+") -->> ");
	  for (TotalSalesHead candidate : TotalSalesHead.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  TotalSalesHead.create(candidate);  
			  } 
		  } 
	  Logger.info("@C TotalSalesHeads -->> findByName("+term+") <<--");
    return TotalSalesHead.all();
    }
  /*
  public static Long getTotalSalesHeadId(String term) { 
	  Long id;
	  for (TotalSalesHead candidate : TotalSalesHead.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			    id = candidate.id;
			  } 
		  } 
    return id;
    }
  */
  public static Result delete(Long id) {
	  Logger.info("@C TotalSalesHeads -->> delete("+id+") -->> ");
	  TotalSalesHead totalSalesHead = TotalSalesHead.find.byId(id);
	  	flash("success", "TotalSalesHead "+totalSalesHead.name+" has been deleted");
	  Ebean.createSqlUpdate(
	            "DELETE FROM store_total_sales_head WHERE total_sales_head_id =:id"
	        ).setParameter("id", id).execute();
	
	  TotalSalesHead.delete(id);
	    flash("action", "totalsaleshead");
	    Logger.info("@C TotalSalesHeads -->> delete("+id+") <<--");
	  	return GO_HOME; 
	  }
 
  /**
   * Display the paginated list of computers.
   *
   * @param page Current page number (starts from 0)
   * @param sortBy Column to be sorted
   * @param order Sort order (either asc or desc)
   * @param filter Filter applied on computer names
   */
  public static Result list(int page, String sortBy, String order, String filter) {
	  Logger.info("@C TotalSalesHeads -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "totalsaleshead");
	  return ok(list.render(
			  TotalSalesHead.page(page, 10, sortBy, order, filter),
              sortBy, order, filter
          )); 
  }

  
  
}