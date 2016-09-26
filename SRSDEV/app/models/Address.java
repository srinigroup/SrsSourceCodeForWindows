package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import play.Logger;

@Entity 
@Table(name="address")
public class Address extends Model {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;
	  
	 @OneToOne(mappedBy="address")
	public Store store;
	  
	 
	  public Company company;
	  
	  @Constraints.Required
	  public String street; 
	  
	  public String number; 
	  
	  @Constraints.Required
	  public String postalCode; 
	  
	 
	  public String city;  
	  
	  public String state;
	  
	  
	  public String country;
	  
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,Address> find = new Finder<Long,Address>(Long.class, Address.class); 
	    
	    
	 	public static List<Address> all() {
	 		Logger.info("@M Address -->> all() -->>"); 
		  return find.all();
		}

		public static void create(Address address) {
			Logger.info("@M Address -->> create(address) -->>"); 
			address.save();
			Logger.info("@M Address -->> create(address) <<--"); 
		}

		public static void delete(Long id) {
			Logger.info("@M Address -->> delete("+id+") -->>"); 
		  find.ref(id).delete();
		  Logger.info("@M Address -->> delete("+id+") <<--"); 
		}
		
		  /**
	     * Retrieve Address for given street
	     */
	    public static List<Address> findInvolving(String street) {
	    	Logger.info("@M Address -->> findInvolving("+street+") -->>");
	        return find.where()
	            .eq("street", street)
	            .findList();
	    }
	    
	    public static Address findByAddress(String street, String postalCode) {
	    	Logger.info("@M Address -->> findByAddress("+street+","+postalCode+") -->>");
	    	
			  for (Address candidate : findInvolving(street)) { 
				  if (candidate.street.equals(street) && candidate.postalCode.equals(postalCode)) {  
					 		 
					  return candidate;  
					  }  
				  } 
			  Logger.info("@M Address -->> findByAddress("+street+","+postalCode+") <<--");
			  return null; 
			  }
		
	    
		public String toString() {  
			 return "Address("+ street +":" + postalCode + ":" + city +":"+state+":" + country +")";
		  } 
	  
}
