package models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.Logger;

@Entity 
public class SalesHead extends Model {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	@Constraints.Required
	  public String name;
	
	
	  public String category;
	  
	// to hold status ACTIVE||DELETED
	  public String status;
	  
	  @Formats.DateTime(pattern="dd/MM/yyyy hh:mm:ss")
	    public Date created_date=new Date();
	
	  
	  @Formats.DateTime(pattern="dd/MM/yyyy")
	    public Date modified_date;
	  
	 // @ManyToOne(cascade = CascadeType.ALL)
	  public AccountHolder accid;
	  
	  
	  
	
	 	 
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,SalesHead> find = new Finder<Long,SalesHead>(Long.class, SalesHead.class); 
	    
	 	public static List<SalesHead> all() {
	 		Logger.info("@M SalesHead -->> all() -->>"); 
		  return find.all();
		}
	 	
	 	public static List<SalesHead> getSalesHeadList(String[] ids) {
	 		Logger.info("@M SalesHead -->> getSalesHeadList() -->>");
	 		List<SalesHead> salesHeadList = new ArrayList<SalesHead>();
	 		for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			 SalesHead salesHead = SalesHead.find.byId(id);
	 	
	 			 	salesHead.save();
	 		    	salesHeadList.add(salesHead);
	 		    }
	 		 Logger.info("@M SalesHead -->> getSalesHeadList() <<--");
			  return salesHeadList;
			}
	 	
		public static void create(SalesHead saleshead) {
			Logger.info("@M SalesHead -->> create(saleshead) -->>"); 
			saleshead.created_date=new Date();
			saleshead.save();
			saleshead.status = "ACTIVE";
			saleshead.save();
			Logger.info("@M SalesHead -->> create(saleshead) <<--"); 
		}

		public static void delete(Long id) {
			Logger.info("@M SalesHead -->> delete("+id+") -->>");

			SalesHead salesHead = SalesHead.find.byId(id);
			salesHead.status = "DELETED";
			salesHead.update();
			
		  Logger.info("@M SalesHead -->> delete("+id+") <<--");
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
	    public static Page<SalesHead> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M SalesHead -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
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
		/* public static SalesHead getSalesHeadName(long salesHeadId){
				
				
				return 
		 	            ( find.where()
		 	                .eq("name", salesHeadId)
		 	                ).findUnique();
		 	                
		 	   
			}
*/
		/* public static SalesHead getAccountHolderName(Long accId,Long shiftId,Long salesId,Long mediaId){
			  
			  Logger.info("@M Payout -->> getAccountHolderName("+shiftId+","+salesId+") -->>");
			  
			  List<ShiftSale> shiftSales = new ArrayList<ShiftSale>();
			  List<SalesHead> salesHeads=new ArrayList<SalesHead>();
			
			  ShiftSale shiftSale = new ShiftSale();
			  SalesHead salesHead=new SalesHead();
			 
			  
			  SalesHead salesHeadId = SalesHead.find.byId(salesId); 
			  MediaTender media=MediaTender.find.byId(mediaId);
			  AccountHolder accHolSale=AccountHolder.find.byId(accId);
			  
			  System.out.println("media is========="+media);
			  System.out.println("salesHead name========="+salesHeadId.name);
			  System.out.println("Account Holder name========="+accHolSale.accountHolder);
			  
			  shiftSale.salesHead.accid=accHolSale;
		
			  
			  Logger.debug("@M Payout -->> getAccountHolderName("+shiftId+","+salesId+","+mediaId+") -->> account holder :"+salesHeadId.name,accHolSale.accountHolder);
			 	
			  if(salesHeadId.name.equals("ACCOUNT RECV") || salesHeadId.name.equals("ACCOUNT SALES")){
				  Logger.debug("@M Payout -->> getAccountHolderName("+shiftId+","+salesId+") -->> inside if condition i.e SALESHEAD is: ACCOUNT RECV");
				  shiftSale.salesHead.name=salesHeadId.name;
			      System.out.println("salesHead ========="+salesHeadId.name);
			      shiftSale.salesHead.category=salesHeadId.category;
			      shiftSale.salesHead.created_date=shiftSale.salesHead.created_date;
			      shiftSale.salesHead.status=salesHeadId.status;
				  Shift shift=Shift.find.byId(shiftId);
			      System.out.println("shift ========="+shift.id);
			      shiftSale.salesHead.shift=shift;
				  System.out.println("shift id2 ========="+shiftSale.salesHead.shift.id);
				  shiftSale.save();
				  System.out.println("saleshead not null:"+shiftSale.salesHead);
			      System.out.println("shift saved");
				  System.out.println("saleshead not null:"+shiftSale.salesHead);
				  //salesHeads = shift.salesheads;
				  System.out.println("saleshead not null:"+shiftSale.salesHead);
				  System.out.println("SALESHEAD SAVED");
				  System.out.println("saleshead not null:"+shiftSale.salesHead);
				  if(shiftSale.salesHead.equals("null")){
					  System.out.println("saleshead null");
				  }
				  salesHeads.add(salesHead);
			      System.out.println("SALESHEAD ADDED");
				  //shift.salesheads = salesHeads;
				 // shift.save();
			      //salesHeads = shift.salesheads;
			      //salesHeads.add(salesHead);
				 // shift.salesheads = salesHeads;
				  shift.save();
				  System.out.println(salesHeads);


			  }
			  return salesHead;
		  }
		 
		 public static List<SalesHead> getSalesHeadList(List<SalesHead> salesHeadList,String salesHeadType) {
			  
			  Logger.info("@M Payout -->> getPayoutList(salesHeadList,"+salesHeadType+") -->>");
			  
			  List<SalesHead> salesheads = new ArrayList<SalesHead>();
			  Logger.debug("@M SalesHead -->> getPayoutList(salesHeadList,"+salesHeadType+") -->> listSize :"+salesheads.size());
			  if(salesHeadList!=null && salesHeadList.size()>0){
				  
				  for(SalesHead salesHead: salesHeadList) {
					  if(salesHead.name.equals("ACCOUNT RECV") || salesHead.name.equals("ACCOUNT SALES") ){
						  if(salesHead.name.toLowerCase().contains(salesHead.name.toLowerCase())){ 
						   salesheads.add(salesHead) ;
	    			 
						  } 
					  }
		        }
			  }
			  
			  Logger.info("@M Payout -->> getPayoutList(payputList,"+salesHeadType+") <<--");
			  return salesheads;
			  
		}*/


}

