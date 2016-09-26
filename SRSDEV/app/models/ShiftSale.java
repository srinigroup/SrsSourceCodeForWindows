package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.*;

/**
 * @author shravan
 *
 */
@Entity
public class ShiftSale extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;

	 @OneToOne
	  public SalesHead salesHead;
	
	@Constraints.Required
	  public Double amount;
	
	@ManyToOne(cascade = CascadeType.ALL)
	  public Shift shift;
	

	  /**
	     * Generic query helper for entity ShiftSale with id Long
	     */
	    public static Finder<Long,ShiftSale> find = new Finder<Long,ShiftSale>(Long.class, ShiftSale.class); 
	    
	    
	    
	 	public static List<ShiftSale> all() {
	 		Logger.info("@M ShiftSale -->> all() -->>");
		  return find.all();
		}

		
		public static void delete(Long id) {
			Logger.info("@M ShiftSale -->> delete("+id+") -->>");
		    find.ref(id).delete();
		    Logger.info("@M ShiftSale -->> delete("+id+") <<--");
		}
		
		
		public String toString() {  
			 return "ShiftSale("+ salesHead.name +":" + amount + ")";
		  } 
		
	}
