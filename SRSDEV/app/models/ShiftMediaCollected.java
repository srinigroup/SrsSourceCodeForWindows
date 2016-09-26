package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * @author shravan
 *
 */
@Entity
public class ShiftMediaCollected extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;

	@OneToOne
	  public MediaTender mediaTender;
	
	@Constraints.Required
	  public Double amount = 0.00;
	
	@Constraints.Required
	  public Double settleAmount = 0.00;
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Shift shift;
	
	/**
     * Generic query helper for entity ShiftMediaCollected with id Long
     */
    public static Finder<Long,ShiftMediaCollected> find = new Finder<Long,ShiftMediaCollected>(Long.class, ShiftMediaCollected.class); 
    
    public ShiftMediaCollected(){
    	
    	this.amount = 0.00;
    	this.settleAmount = 0.00;
    }
    
 	public static List<ShiftMediaCollected> all() {
 		Logger.info("@M ShiftMediaCollected -->> all() -->>");
	  return find.all();
	}

	
	public static void delete(Long id) {
		Logger.info("@M ShiftMediaCollected -->> delete("+id+") -->>");
	  find.ref(id).delete();
	  Logger.info("@M ShiftMediaCollected -->> delete("+id+") <<--");
	}
	
	
	public String toString() {  
		 return "ShiftSale("+ mediaTender.name +":" + amount + ")";
	  } 

}