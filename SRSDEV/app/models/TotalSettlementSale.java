/**
 * 
 */
package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints;
import play.db.ebean.Model;


/**
 * @author shravan
 *
 */
@Entity
public class TotalSettlementSale extends Model {

	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;

	@ManyToOne
	  public TotalSalesHead totalSalesHead;
	
	@Constraints.Required
	  public Double amount;
	
	@ManyToOne
	public Terminal terminal;
	

	  /**
	     * Generic query helper for entity ShiftSale with id Long
	     */
	    public static Finder<Long,TotalSettlementSale> find = new Finder<Long,TotalSettlementSale>(Long.class, TotalSettlementSale.class); 
	    
	    
	    
	 	public static List<TotalSettlementSale> all() {
		  return find.all();
		}

		
		public static void delete(Long id) {
		  find.ref(id).delete();
		}
		
		
		public String toString() {  
			 return "TotalSettlementSale("+ totalSalesHead.name +":" + amount + ")";
		  } 
	
}
