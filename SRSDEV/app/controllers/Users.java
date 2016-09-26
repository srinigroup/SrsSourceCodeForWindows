package controllers;
import java.util.List;



import java.util.Map;

import com.avaje.ebean.Ebean;

import play.Logger;
import models.Role;
import models.SalesHead;
import models.User;
import models.Employee;
import views.html.*;
import views.html.users.*;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)
public class Users extends Controller {

	
    
	private static final Form<User> userForm = form(User.class); 
	
	
	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
     
      routes.Users.list(0, "name", "asc", "","VIEW")
    );
	  
	public static Result showBlank(){ 
		Logger.info("@C Users -->> showBlank() -->> ");
		 flash("action", "user");
		return ok(register.render(userForm)); 
		}

 
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id,String action) {
	  
	  flash("pageAction", action);
	  
	  Logger.info("@C Users -->> edit("+id+") -->> ");
	  User user = User.find.byId(id);
	  Form<User> userForm = form(User.class).fill(user);
      
	  flash("action", "user");
	  Logger.info("@C Users -->> edit("+id+") <<--");
      return ok(
          editForm.render(id, userForm,user)
      );
  }
  
  
  public static Result update(Long id) {
	 
	  Logger.info("@C Users -->> update("+id+") -->> "); 
	  User user = User.find.byId(id);
	
	  
   /*   if(userForm.hasErrors()) {
    	 
    	  Logger.error("@C Users -->> update("+id+") -->> userForm has errors"); 
          return badRequest(editForm.render(id, userForm, user));
      } */
      final Map<String, String[]> values = request().body().asFormUrlEncoded();
	  String messages[] = values.get("roles[]");
	
		  List<Role> rolesList = Role.getRolesList(messages);
		  user.roles=rolesList;
		  
		  user.email = form().bindFromRequest().get("email");
		  user.name = form().bindFromRequest().get("name");
		  
		  user.update(id);
      flash("action", "user");
      flash("success", user.name + " has been updated");
      Logger.info("@C Users -->> update("+id+") <<--"); 
     
      return redirect(
      		routes.Users.edit(id,"MODIFY")
    		  );

	  }
  
  
  public static Result register() {
	  Logger.info("@C Users -->> register() -->> "); 
	//  Form<User> userForm = form(User.class).bindFromRequest();
	
	  final Map<String, String[]> values = request().body().asFormUrlEncoded();
	  String messages[] = values.get("roles[]");
	 
		  List<Role> rolesList = Role.getRolesList(messages);
	
    /* if(userForm.hasErrors()) {
    	
          return badRequest(register.render(userForm));
      }
     
     */
		 
      User.create(form().bindFromRequest().get("name"),form().bindFromRequest().get("email"),form().bindFromRequest().get("password"),rolesList);
   
      flash("action", "user");
      flash("success", "User " + form().bindFromRequest().get("name") + " has been saved");
      Logger.info("@C Users -->> register() <<--"); 
      return redirect(
        		routes.Users.showBlank()
      		  ); 

	  }
 
   
  
  public static List<User> findByName(String term) { 
	  Logger.info("@C Users -->> findByName("+term+") -->> "); 
	  for (User candidate : User.findAll()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  User.create(candidate);  
			  } 
		  } 
	  Logger.info("@C Users -->> findByName("+term+") <<--");
    return User.findAll();
    }
  
  public static Result delete(Long id) {
	  Logger.info("@C Users -->> delete("+id+") -->> ");
	  User user = User.find.byId(id);
	  
	  	flash("success", "User "+user.name+" has been deleted");
	  Ebean.createSqlUpdate(
	            "DELETE FROM account_role WHERE account_id =:id"
	        ).setParameter("id", id).execute();
	  user.delete(id);
	  flash("action", "user");
	  Logger.info("@C Users -->> delete("+id+") <<--");
	  return redirect(
	  			routes.Application.showOptions("USER","DELETE")
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
	  Logger.info("@C Users -->> list("+page+","+sortBy+","+order+","+filter+") -->> ");
	  flash("action", "user");
	  flash("pageAction", pageAction);
	  return ok(list.render(
              User.page(page, 10, sortBy, order, filter),
              sortBy, order, filter, pageAction
          )); 
  }

  
//showOptions method used in Application.java
  
  public static Result showOptions(String page,String action) {
	  	
	  	
	  	flash("pageAction",action);
	  	
	  	if(action.equals("ADD")){
	  		
	  		 return redirect(
		            		routes.Users.showBlank()
		            );
	  	}
	  	else if(action.equals("ALL")){
	  		
	  		return ok(manage1.render(page)); 
	  	}
	  	
	  	else{
	  		
	  		
	  		return redirect(
	 				 routes.Users.list(0, "name", "asc", "",action)
		            );
	 		
	  	}
	  	
	  }

  
  // to change the password
  
  public static Result changePassword(Long id) {
		 
	  Logger.info("@C Users -->> changePassword("+id+") -->> "); 
	  Employee employee = Employee.find.byId(id);
	
		  String password1  = form().bindFromRequest().get("password1");
		  String password2  = form().bindFromRequest().get("password2");
		  
		  User user = User.findByEmail(employee.contactInfo.email);
		  
		  if(user != null){
			  if( (password1 != null) && (password2 != null) ){
				  if(password1.trim().equals(password2.trim())){
					 user.password = password2;
					 user.update();
					 flash("changePasswordSuccess", " Password changed Successfully, Please login with new password");
				  }else{
					  flash("changePasswordFailure", " Password change Falied , Password and Confirm Password must be same");
				  }
			  }else{
				  flash("changePasswordFailure", " Password change Falied , Password and Confirm Password should not be empty");
			  }
			  
		  }else{
			  flash("changePasswordFailure", " Password change Falied , With this Employee we could not find any User account");
		  }
		
      Logger.info("@C Users -->> changePassword("+id+") <<--"); 
     
      return redirect(
      		routes.Application.logout()
    		  );

	  }
  
}