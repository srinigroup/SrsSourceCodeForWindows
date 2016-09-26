 package models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.avaje.ebean.Page;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import play.*;


@Entity 
public class BankDeposit extends Model {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	
	@Formats.DateTime(pattern="MM/dd/yyyy")
	 public Date createDate;
	
	@Constraints.Required
	  public Double cashAmt =0.00;
	
	@Constraints.Required
	  public Double chequeAmt =0.00;
	
	
	 
	 public BankDeposit(){
		
		 cashAmt=0.00;
		 chequeAmt=0.00;
	 }
	 public BankDeposit(Date date,Double cashAmt,Double chequeAmt){
		 
		 this.createDate=date;
		 this.chequeAmt=cashAmt;
		 this.chequeAmt=chequeAmt;
	 }
	 
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,BankDeposit> find = new Finder<Long,BankDeposit>(Long.class, BankDeposit.class); 
	    
	 	public static List<BankDeposit> all() {
	 		Logger.info("@M BankDeposit -->> all() -->>");
		  return find.all();
		}

		public static void create(BankDeposit bankDeposit) {
			Logger.info("@M BankDeposit -->> create(bankDeposit) -->>");
			
			bankDeposit.save();
			Logger.info("@M BankDeposit -->> create(bankDeposit) <<--");
		}

		public static void delete(Long id) {
			Logger.info("@M BankDeposit -->> delete("+id+") -->>");
		  find.ref(id).delete();
		  Logger.info("@M BankDeposit -->> delete("+id+") <<--");
		}
		
		public static List<BankDeposit> getBankDepositList(String[] ids) {
			Logger.info("@M BankDeposit -->> getBankDepositList("+ids+") -->>");
	 		List<BankDeposit> bankDepositList = new ArrayList<BankDeposit>();
	 		
	 		  for (int i=0;i<ids.length;i++){
	 			 Logger.debug("@M BankDeposit -->> getBankDepositList("+ids+") -->> inside for loop");
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			BankDeposit bankDeposit = BankDeposit.find.byId(id);
	 		  
	 			bankDepositList.add(bankDeposit);
	 		    }
	 		 Logger.info("@M BankDeposit -->> getBankDepositList("+ids+") <<--");
			  return bankDepositList;
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
	    public static Page<BankDeposit> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M BankDeposit -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
	        return 
	            ( find.where()
	                .ilike("name", "%" + filter + "%")
	                .orderBy(sortBy + " " + order)  	                
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
		public String toString() {  
			  return id+" :cashAmt "+cashAmt+" :chequeAmt "+chequeAmt;
		  }
}

