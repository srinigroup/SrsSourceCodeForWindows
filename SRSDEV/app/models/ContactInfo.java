package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import play.Logger;

@Entity
public class ContactInfo extends Model {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	  
	@Constraints.Required
	  public String phone1;  
	
	  public String phone2;
	  
	  @Constraints.Required
	  public String email; 
	  
	//  public String email2;

	  public String facsimile; 
	  

	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,ContactInfo> find = new Finder<Long,ContactInfo>(Long.class, ContactInfo.class); 
	    
	    
	    
	 	public static List<ContactInfo> all() {
	 		Logger.info("@M ContactInfo -->> all() -->>"); 
		  return find.all();
		}

		
		public static void delete(Long id) {
			Logger.info("@M ContactInfo -->> delete("+id+") -->>"); 
		  find.ref(id).delete();
		  Logger.info("@M ContactInfo -->> delete("+id+") <<--"); 
		}
		
		public static ContactInfo findByEmail(String email) {
			
			Logger.info("@M ContactInfo -->> findByEmail("+email+") -->>"); 
	       
			  for (ContactInfo candidate : ContactInfo.all()) { 
				   
				  if (candidate.email.equals(email)) {  
					 
					  return candidate;  
					  }  
				  } 
			  Logger.info("@M ContactInfo -->> findByEmail("+email+") <<--"); 
			  return null; 
			  }
		public String toString() {  
			 return "ContactInfo("+ phone1 +":" + email + ")";
		  } 
	  
	 
}