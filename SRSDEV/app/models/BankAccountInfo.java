package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity 
public class BankAccountInfo extends Model {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	  
	@Constraints.Required
	  public String accountName;  
	
	@Constraints.Required
	  public String accountNumber;
	 
	@Constraints.Required
	  public String accountType;
	  
	  @Constraints.Required
	  public String bankName; 

	  @Constraints.Required
	  public String bsbNumber; 
	  

	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,BankAccountInfo> find = new Finder<Long,BankAccountInfo>(Long.class, BankAccountInfo.class); 
	    
	    
	    
	 	public static List<BankAccountInfo> all() {
	 		Logger.info("@M BankAccountInfo -->> all() -->> "); 
		  return find.all();
		}

		
		public static void delete(Long id) {
			Logger.info("@M BankAccountInfo -->> delete("+id+") -->> "); 
		  find.ref(id).delete();
		  Logger.info("@M BankAccountInfo -->> delete("+id+") <<--");
		}
		
		public static BankAccountInfo findByAccountNumber(String accountNumber) {
			Logger.info("@M BankAccountInfo -->> findByAccountNumber("+accountNumber+") -->> ");
		
	       
			  for (BankAccountInfo candidate : BankAccountInfo.all()) { 
				 
			       
				  if (candidate.accountNumber.equals(accountNumber)) {  
					
					  return candidate;  
					  }  
				  } 
			  Logger.info("@M BankAccountInfo -->> findByAccountNumber("+accountNumber+") <<--");
			  return null; 
			  }
		public String toString() {  
			 return "BankAccountInfo("+ accountName +":" + accountNumber + ")";
		  } 
	  
	 
}