package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class PaymentTender extends Model{
	
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	@Constraints.Required
	  public String name;
	
	  public String category;
	  
	// to hold status ACTIVE||DELETED
	 public String status;
	  
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,PaymentTender> find = new Finder<Long,PaymentTender>(Long.class, PaymentTender.class); 
	    
	 	public static List<PaymentTender> all() {
		  return find.all();
		}

		public static void create(PaymentTender paymentTender) {
			
			paymentTender.save();
			paymentTender.status = "ACTIVE";
			paymentTender.save();
		}

		public static void delete(Long id) {
			
			PaymentTender paymentTender = PaymentTender.find.byId(id);
			paymentTender.status="DELETED";
			paymentTender.update();
			
		}
		
		public static List<PaymentTender> getPaymentTenderList(String[] ids) {
			Logger.info("@M PaymentTender -->> getPaymentTenderList() -->> ");
	 		List<PaymentTender> paymentTenderList = new ArrayList<PaymentTender>();
	 		
	 		  for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			PaymentTender paymentTender = PaymentTender.find.byId(id);
	 		  
	 			paymentTenderList.add(paymentTender);
	 		    }
	 		 Logger.info("@M PaymentTender -->> getPaymentTenderList() <<--");
			  return paymentTenderList;
			}

		
		 /**
	     * Return a page of computer
	     *
	     * @param page Page to display
	     * @param pageSize Number of computers per page
	     * @param sortBy Product property used for sorting
	     * @param order Sort order (either or asc or desc)
	     * @param filter Filter applied on the name column
	     */
	    public static Page<PaymentTender> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M PaymentTender -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                .orderBy(sortBy + " " + order)  	                
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
		public String toString() {  
			  return id+": "+name;
		  }

}
