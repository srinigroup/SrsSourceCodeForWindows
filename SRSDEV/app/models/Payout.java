package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
public class Payout extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;
	
	@ManyToOne
	  public Supplier supplier;
	@ManyToOne
	  public Shift shift;
	@ManyToOne
	  public DailyReconciliation dailyReconciliation;
	
	@Constraints.Required
	  public Double invoiceAmt = 0.00;
	
		
	@Constraints.Required
	  public String payoutType;
	
	@Constraints.Required
	  public String category; //expense type/id

	
	public String invoice;
	
	public String reason;
	
		
	/**
     * Generic query helper for entity Payout with id Long
     */
    public static Finder<Long,Payout> find = new Finder<Long,Payout>(Long.class, Payout.class); 
    
    
    
 	public static List<Payout> all() {
 		Logger.info("@M Payout -->> all() -->>");
	  return find.all();
	}

	
	public static void delete(Long id) {
		Logger.info("@M Payout -->> delete("+id+") -->>");
	  find.ref(id).delete();
	  Logger.info("@M Payout -->> delete("+id+") <<--");
	}
	
	
	public String toString() {  
		 return "Payout("+ id +":" + invoiceAmt +":" + category + ")";
	  }



	public static String getPayoutAmount(Payout payout, String type) {
		Logger.info("@M Payout -->> getPayoutAmount(payout id:"+payout.id+","+type+") -->>");
		Double result = 0.00;		
	           
   		  if(payout.payoutType.toLowerCase().equals(type.toLowerCase())){ 
   			 result = payout.invoiceAmt;
   			result=Math.round( result * 100.0 ) / 100.0;
	        }
   		Logger.debug("@M Payout -->> getPayoutAmount(payout id:"+payout.id+","+type+") -->> PayoutAmount "+result);
   		Logger.info("@M Payout -->> getPayoutAmount(payout id:"+payout.id+","+type+") <<--");
		  return result.toString();
	} 
	
	public static String getTotalPayouts(List<Payout> payoutList, String type) {
		Logger.info("@M Payout -->> getTotalPayouts(payputList,"+type+") -->>");
		Double result = 0.00;		
	      for(Payout payout: payoutList){
   		  if(payout.payoutType.toLowerCase().equals(type.toLowerCase())){ 
   			 result = result +payout.invoiceAmt;
   			 result=Math.round( result * 100.0 ) / 100.0;
	        }
	      }
	      Logger.debug("@M Payout -->> getTotalPayouts(payputList,"+type+") -->> TotalPayouts "+result);
	      Logger.info("@M Payout -->> getTotalPayouts(payputList,"+type+") <<--");
		  return result.toString();
	} 
	
	  public static List<Payout> getPayoutList(List<Payout> payoutList,String payoutType) {
		  
		  Logger.info("@M Payout -->> getPayoutList(payputList,"+payoutType+") -->>");
		  
		  List<Payout> payouts = new ArrayList<Payout>();
		  Logger.debug("@M Payout -->> getPayoutList(payputList,"+payoutType+") -->> listSize :"+payouts.size());
		  if(payoutList!=null && payoutList.size()>0){
			  
		  for(Payout payout: payoutList) {
			  
    		  if(payout.payoutType.toLowerCase().contains(payoutType.toLowerCase())){ 
    			  payouts.add(payout) ;
    			 
  			  } 
	        }
		  }
		  Logger.info("@M Payout -->> getPayoutList(payputList,"+payoutType+") <<--");
		  return payouts;
		  
		}

	  public static Payout getPayout(String payoutType,Long mode,Long suppid){
		  
		  Logger.info("@M Payout -->> getPayout("+payoutType+","+mode+","+suppid+") -->>");
		  
		  List<Payout> payouts = new ArrayList<Payout>();
		
		  Payout payout = new Payout();
		 
		  
		  Supplier supplier = Supplier.find.byId(suppid); 
		  
		  Logger.debug("@M Payout -->> getPayout("+payoutType+","+mode+","+suppid+") -->> supplier :"+supplier.name);
		  payout.supplier= supplier;
		  
		 		 
		  if(payoutType.equals("REGISTERPAYOUT")){		
			  Logger.debug("@M Payout -->> getPayout("+payoutType+","+mode+","+suppid+") -->> inside if condition i.e payout is: REGISTERPAYOUT");
			  payout.payoutType = payoutType;	
			  Shift shift = Shift.find.byId(mode); 
			  payout.shift = shift;
			  payout.save();
			  payouts = shift.payouts;
			  payouts.add(payout);
			  shift.payouts = payouts;
			  shift.save();
		  }
		  else{
			  Logger.debug("@M Payout -->> getPayout("+payoutType+","+mode+","+suppid+") -->> inside else condition i.e payout is: SAFEPAYOUT");
			  payout.payoutType = "SAFEPAYOUT";	
			  DailyReconciliation dailyReconciliation = DailyReconciliation.find.byId(mode);
			  payout.dailyReconciliation = dailyReconciliation;
			  payout.save();
			  payouts = dailyReconciliation.payouts;
			  payouts.add(payout);
			  dailyReconciliation.payouts = payouts;
			  dailyReconciliation.save();
		  }
		  
		  
		  
		  Logger.info("@M Payout -->> getPayout("+payoutType+","+mode+","+suppid+") <<--");
		  return payout;
	  }
	  
	 
}
