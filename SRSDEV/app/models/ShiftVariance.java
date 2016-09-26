package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.*;

/**
 * @author shravan
 *
 */
@Entity 
public class ShiftVariance extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;

	@Constraints.Required
	  public Double cashAmt = 0.00;
	
	@Constraints.Required
	  public Double eftposAmt = 0.00;
	
	
	  public String cashVarReason = " ";
	
	
	  public String eftposVarReason = " ";
	
	public ShiftVariance(){
		
		this.cashAmt = 0.00;
		this.eftposAmt = 0.00;
		this.cashVarReason = "";
		this.eftposVarReason = "";
		
	}
	public ShiftVariance(Double cashAmt, Double eftposAmt, String cashVarReason, String eftposVarReason){
		
		this.cashAmt = cashAmt;
		this.eftposAmt = eftposAmt;
		this.cashVarReason = cashVarReason;
		this.eftposVarReason = eftposVarReason;
		
	}
	
	/**
     * Generic query helper for entity ShiftVariance with id Long
     * 
     * 
     */
    public static Finder<Long,ShiftVariance> find = new Finder<Long,ShiftVariance>(Long.class, ShiftVariance.class); 
    
    
    
 	public static List<ShiftVariance> all() {
 		Logger.info("@M ShiftVariance -->> all() -->> ");
	  return find.all();
	}

	
	public static void delete(Long id) {
		Logger.info("@M ShiftVariance -->> delete("+id+") -->> ");
	  find.ref(id).delete();
	  Logger.info("@M ShiftVariance -->> delete("+id+") <<-- ");
	}
	
	public String getTotalVariance() {  
		Logger.info("@M ShiftVariance -->> getTotalVariance() -->> ");
		Double result = cashAmt + eftposAmt;
		result=Math.round( result * 100.0 ) / 100.0;
		Logger.info("@M ShiftVariance -->> getTotalVariance() <<-- ");
		return String.valueOf(result);
		 
	  } 
	
	public String toString() {  
		 return "ShiftVariance("+ cashAmt +":" + cashVarReason +"||"+ eftposAmt +":" + eftposVarReason + ")";
	  } 


}