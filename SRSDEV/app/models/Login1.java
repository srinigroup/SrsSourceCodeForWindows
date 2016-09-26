package models;

import java.util.List;

import javax.persistence.*; 


import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@SuppressWarnings("serial")
@Entity 
public class Login1 extends Model { 

	
	@Id 
	@Constraints.Required
    public String email;
	
	@Constraints.Required
    public String password;
    
    public static List<Login1> all() {
  	  return find.all();
  	}

  	public static void create(Login1 login) {
  		login.save();
  	}

  	// change it to static method later
  	public void delete(String email) {
  	  find.ref(email).delete();
  	}
  	
  	 /**
       * Generic query helper for entity Product with id Long
       */
      public static Finder<String,Login1> find = new Finder<String,Login1>(String.class, Login1.class); 
      

}
