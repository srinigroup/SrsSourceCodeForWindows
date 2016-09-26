package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.Page;

import controllers.Application;
import controllers.SalesHeads;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;



/**
 * @author shravan
 *
 */
@Entity
public class Shift  extends Model{
	
	
	private static final long serialVersionUID = 1L;

	  @Id  
	  public Long id;
	
	  
	  @Formats.DateTime(pattern="dd/MM/yyyy hh:mm:ss")
      public Date shiftStartDateTime = new Date();
	
	  
	  @Formats.DateTime(pattern="dd/MM/yyyy hh:mm:ss")
	  public Date shiftEndDateTime;
		
	  @OneToMany(cascade = CascadeType.ALL)
	  public List<ShiftSale> shiftSales = new ArrayList<ShiftSale>();
	  
	  @OneToMany(cascade = CascadeType.ALL)
	  public List<ShiftMediaCollected> mediaCollects = new ArrayList<ShiftMediaCollected>();
	  
	  @OneToMany(cascade = CascadeType.ALL)
	  public List<ShiftPaymentTender> shiftPayments = new ArrayList<ShiftPaymentTender>();
	  
	 	  
	  @OneToMany(cascade = CascadeType.ALL)
	 
	  public List<Payout> payouts = new ArrayList<Payout>();
	  
	 /* @OneToMany(cascade = CascadeType.ALL)
	  public List<SalesHead> salesheads=new ArrayList<SalesHead>();
	*/	 
	 
	  @OneToOne  
	  public Timesheet timesheet;
	  
	  @Constraints.Required	
	  public String status;
	  
	  @ManyToOne
	  public Terminal terminal;
	  
	  public Double shiftVariance ;
	  
	  public String varReason;
	  
	  public Shift(Timesheet timesheet,String status)
	  {
		Logger.info("@M Shift -->> constructor Shift(timesheet,"+status+")");	
		this.timesheet = timesheet;
		this.status = status;
		this.shiftVariance = 0.00;

	  }
	  public Shift(List<ShiftSale> shiftSales,
				List<ShiftMediaCollected> mediaCollects,List<Payout> payouts,Double shiftVariance,Timesheet timesheet,String status)
	  {
		  Logger.info("@M Shift -->> constructor Shift(shiftSales,mediaCollects,payouts,shiftVariance,timesheet,status)");
		this.shiftSales = shiftSales;
		this.mediaCollects = mediaCollects;
		this.payouts = payouts;
		this.shiftVariance = shiftVariance;
		this.timesheet = timesheet;
		this.status = status;

	  }
	  
	    /**
	     * Create a new Shift.
	     */
	    public static Shift create(List<ShiftSale> shiftSales,
				List<ShiftMediaCollected> mediaCollects,List<Payout> payouts,Double shiftVariance,Timesheet timesheet,String status) {
	    	 Logger.info("@M Shift -->>create(shiftSales,mediaCollects,payouts,shiftVariance,timesheet,status) -->>");
	    	Shift shift = new Shift(shiftSales, mediaCollects,payouts,shiftVariance,timesheet,status);
	    	shift.save();	
	    	Logger.info("@M Shift -->>create(shiftSales,mediaCollects,payouts,shiftVariance,timesheet,status) <<--");
	        return shift;
	    }
	    
	    /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,Shift> find = new Finder<Long,Shift>(Long.class, Shift.class); 
	    
	 	public static List<Shift> all() {
	 		Logger.info("@M Shift -->> all() -->>");
		  return find.all();
		}

		public static void create(Shift shift) {
			Logger.info("@M Shift -->> create(shift) -->>");
			shift.save();
			Logger.info("@M Shift -->> create(shift) <<--");
		}
		
		public static void delete(Long id) {
			Logger.info("@M Shift -->> delete("+id+") -->>");
			  find.ref(id).delete();
			  Logger.info("@M Shift -->> delete("+id+") <<--");
			}
				  	    
	    
	    public static Map<String,String> options() {
	    	Logger.info("@M Shift -->> options() -->>");
	        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
	        for(Shift c: Shift.find.findList()) {
	            options.put(c.id.toString(), c.status);
	        }
	        Logger.info("@M Shift -->> options() <<--");
	        return options;
	    }
	    
	    public static String getTotalSales(Shift shift) {
	    	
	    	Logger.info("@M Shift -->> getTotalSales(shift id:"+shift.id+") -->>");
			  
	    	Double result = 0.00;
			  
				 for(ShiftSale shiftSale : shift.shiftSales){
					 String temp=shiftSale.salesHead.name;
					 if(!(temp.equals(Application.propertiesMap.get("salesHaed_AC_SALES"))||temp.equals(Application.propertiesMap.get("salesHaed_AC_RECV"))||temp.equals(Application.propertiesMap.get("salesHaed_Fuel_InLtr")) || temp.equals(Application.propertiesMap.get("salesHaed_ShopSales_Excl_GST")) )){
						
     				result = result + shiftSale.amount;
					 }
     				
     			 }
				 result=Math.round( result * 100.0 ) / 100.0;
				 Logger.info("@M Shift -->> getTotalSales(shift"+shift.id+") <<--");
				 return result.toString();
			  
			}
	    
	    
	    public static String getTotalVariance(Shift shift) {
	    	Logger.info("@M Shift -->> getTotalVariance(shift id:"+shift.id+") -->>");
			  
	    	Double result = 0.00;
			
	    	Double totalMedia = Double.parseDouble(Shift.getTotalMedialCollected(shift));
	    	Double totalSales = (Double.parseDouble(Shift.getTotalSales(shift))+Shift.getShiftSale(shift.shiftSales,Application.propertiesMap.get("salesHaed_AC_RECV")).amount-Shift.getShiftSale(shift.shiftSales,Application.propertiesMap.get("salesHaed_AC_SALES")).amount);
	    	Double payouts = Double.parseDouble(Shift.getTotalPayoutsbyType(shift,"REGISTERPAYOUT"));
	    	
	    	// Total Payment Tender's amount except CashOut , only cashOut subtracting and Remaining Payment Tenders are Summing
	    	Double paymentTenderTotal = 0.00;
	    	for(PaymentTender paymentTender : shift.terminal.dailySalesReconciliation.store.paymentTenders){ 
	    		
	    		if(! paymentTender.name.equals(Application.propertiesMap.get("PAYMENT_TENDER_CASHOUT"))){  // except CashOut add all the payment Tenders
	    			
	    			paymentTenderTotal = paymentTenderTotal+ Double.parseDouble(Shift.getShiftPaymentTenderAmount(shift.shiftPayments, paymentTender.name));
	    		}
	    	}
	    	
	    	Double cashOut = Double.parseDouble(Shift.getShiftPaymentTenderAmount(shift.shiftPayments, Application.propertiesMap.get("PAYMENT_TENDER_CASHOUT")));
	    	result =  totalSales -( totalMedia + payouts + paymentTenderTotal - cashOut);
	    	
			  result= Math.round( result * 100.0 ) / 100.0;
			  
			  Logger.info("@M Shift -->> getTotalVariance(shift id:"+shift.id+") <<--");
			  return result.toString();
			  
			}
	    
	    public static String getTotalMedialCollected(Shift shift) {
	    	Logger.info("@M Shift -->> getTotalMedialCollected(shift id:"+shift.id+") -->>");
			  
	    	Double result = 0.00;
			  
				 for(ShiftMediaCollected shiftMediaCollect : shift.mediaCollects){
   				result = result + shiftMediaCollect.settleAmount;
   			 }
				 result=Math.round( result * 100.0 ) / 100.0;
				 Logger.debug("@M Shift -->> getTotalMedialCollected(shift id:"+shift.id+") -->> TotalMedialCollected : "+result);
				 Logger.info("@M Shift -->> getTotalMedialCollected(shift id:"+shift.id+") <<--");
			  return result.toString();
			  
			}
	    public static String getTotalActualMedialCollected(Shift shift) {
	    	
	    	Logger.info("@M Shift -->> getTotalActualMedialCollected(shift id:"+shift.id+") -->>");
			  
	    	Double result = 0.00;
			  
				 for(ShiftMediaCollected shiftMediaCollect : shift.mediaCollects){
   				result = result + shiftMediaCollect.amount;
   			 }
				 result=Math.round( result * 100.0 ) / 100.0;
				 Logger.debug("@M Shift -->> getTotalActualMedialCollected(shift id:"+shift.id+") -->> TotalActualMedialCollected : "+result);
				 Logger.info("@M Shift -->> getTotalActualMedialCollected(shift id:"+shift.id+") <<--");
			  return result.toString();
			  
			}
		  
		  public static ShiftSale getShiftSale(List<ShiftSale> shiftSales,String term) {
			  Logger.info("@M Shift -->> getShiftSale(shiftSalesList,+"+term+") -->>");
			  
			  ShiftSale result = null;
			  
			  for(ShiftSale candidate: shiftSales) {
		           
	    		  if(candidate.salesHead.name.toLowerCase().equals(term.toLowerCase())){ 
	    			 result = candidate;
	    			
	  			  } 
		        }
			  Logger.info("@M Shift -->> getShiftSale(shiftSalesList,+"+term+") <<--");
			  return result;
			  
			}
		  

		  
		  public static String getShiftSaleAmount(List<ShiftSale> shiftSales,String term) {
			  
			  Logger.info("@M Shift -->> getShiftSaleAmount(shiftSalesList,+"+term+") -->>");
			  
			  String result = "0";
			  
			  ShiftSale temp = getShiftSale(shiftSales,term);
			  if(temp!=null){
				  result = temp.amount.toString();
				  Logger.debug("@M Shift -->> getShiftSaleAmount(shiftSalesList,+"+term+") -->> ShiftSaleAmount: "+temp.id+" : "+result);
			  }
			  
			  Logger.info("@M Shift -->> getShiftSaleAmount(shiftSalesList,+"+term+") <<--");
			  return result;
			  
			}
		 
		  public static ShiftPaymentTender getShiftPaymentTender(List<ShiftPaymentTender> shiftPayments,String term) {
			  Logger.info("@M Shift -->> getShiftPaymentTender(shiftPayments,"+term+") -->>");
			  
			  ShiftPaymentTender result = null;
			  
			  for(ShiftPaymentTender candidate: shiftPayments) {
		           
	    		  if(candidate.paymentTender.name.toLowerCase().equals(term.toLowerCase())){ 
	    			 result = candidate;
	    			
	  			  } 
		        }
			  Logger.info("@M Shift -->> getShiftPaymentTender(shiftPayments,"+term+") <<--");
			  return result;
			  
			}
		  
		  public static String getShiftPaymentTenderAmount(List<ShiftPaymentTender> shiftPayments,String term) {
			  
			  Logger.info("@M Shift -->> getShiftPaymentTenderAmount(shiftPayments,"+term+") -->>");
			  
			  String result = "0";
			  
			  ShiftPaymentTender temp = getShiftPaymentTender(shiftPayments,term);
			  if(temp!=null){
				  result = temp.amount.toString();
			  }
			  Logger.debug("@M Shift -->> getShiftPaymentTenderAmount(shiftPayments,"+term+") -->> ShiftPaymentAmount: "+result);
			  Logger.info("@M Shift -->> getShiftPaymentTenderAmount(shiftPayments,"+term+") <<--");
			  return result;
			  
			}
		  
		  
	  public static String getTotalPayoutsbyType(Shift shift,String type) {
		  
		  Logger.info("@M Shift -->> getTotalPayoutsbyType(shift id:"+shift.id+","+type+") -->>");
			  
			  String result = null;			  
				
     			result =  Payout.getTotalPayouts(shift.payouts,type);
     			Logger.debug("@M Shift -->> getTotalPayoutsbyType(shift id:"+shift.id+","+type+") -->> TotalPayoutsbyType "+type+" is "+result);
     			Logger.info("@M Shift -->> getTotalPayoutsbyType(shift id:"+shift.id+","+type+") <<--");
			  return result;
			  
			}

		  
		
		  
		  public static ShiftMediaCollected getShiftMediaCollected(List<ShiftMediaCollected> shiftMediaCollectList,String mediaType) {
			  
			  Logger.info("@M Shift -->> getShiftMediaCollected(shiftMediaCollectList,"+mediaType+") -->>");
			  ShiftMediaCollected result = null;
			  
			  for(ShiftMediaCollected candidate: shiftMediaCollectList) {
		           
	    		  if(candidate.mediaTender.name.toLowerCase().equals(mediaType.toLowerCase())){ 
	    			 result = candidate;
	    			 
	  			  } 
		        }
			  
			  Logger.info("@M Shift -->> getShiftMediaCollected(shiftMediaCollectList,"+mediaType+") <<--");
			  return result;
			  
			}
		  
	 

		  public static String getShiftMediaCollectedAmount(List<ShiftMediaCollected> shiftMediaCollectList,String mediaType) {
			  
			  Logger.info("@M Shift -->> getShiftMediaCollectedAmount(shiftMediaCollectList,"+mediaType+") -->>");
			  
			  String result = "0";
			  
			  ShiftMediaCollected temp = getShiftMediaCollected(shiftMediaCollectList,mediaType);
			  if(temp!=null){
				  result = temp.amount.toString();
				  Logger.debug("@M Shift -->> getShiftMediaCollected(shiftMediaCollectList,"+mediaType+") -->>inside if condition  ");
			  }
			  
			  Logger.info("@M Shift -->> getShiftMediaCollectedAmount(shiftMediaCollectList,"+mediaType+") <<--");
			  return result;
			  
			}
		  
		  public static String getShiftMediaCollectedSettleAmount(List<ShiftMediaCollected> shiftMediaCollectList,String mediaType) {
			  
			  Logger.info("@M Shift -->> getShiftMediaCollectedSettleAmount(shiftMediaCollectList,"+mediaType+") -->>");
			  String result = "0";
			  
			  ShiftMediaCollected temp = getShiftMediaCollected(shiftMediaCollectList,mediaType);
			  if(temp!=null){
				  result = temp.settleAmount.toString();
				  Logger.debug("@M Shift -->> getShiftMediaCollectedSettleAmount(shiftMediaCollectList,"+mediaType+") -->>inside if condition  ");
			  }
			  
			  Logger.info("@M Shift -->> getShiftMediaCollectedSettleAmount(shiftMediaCollectList,"+mediaType+") <<--");
			  return result;
			  
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
		    public static Page<Shift> page(int page, int pageSize, String sortBy, String order, String filter) {
		    	Logger.info("@M Shift -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
		        return 
		            ( find.where()
		                .ilike("status", "%" + filter + "%")
		                .orderBy(sortBy + " " + order)
		                .fetch("terminal")
		                .findPagingList(pageSize))	                
		                .setFetchAhead(false)
		                .getPage(page);
		    }
		 
		    /*
		    List<Order> orderList = 
	   		Ebean.find(Order.class)
	     	.fetch("customer")
	     	.fetch("details")
	     	.where()
	       	.like("customer.name","rob%")
	       	.gt("orderDate",lastWeek)
	     	.orderBy("customer.id, id desc")
	     	.setMaxRows(50)
	     	.findList();
	   
	   		The idea is to search the expenses between the beginning and the end of the month, something like:
			--------------------------------------------------------------------------------------------------
			Datetime firstDayOfMonth= new Datetime().withDayOfMonth(1);
			Datetime lastDayOfMonth = new Datetime().dayOfMonth().withMaximumValue();
			return finder.where()
			    .between("purchaseDate", firstDayOfMonth, lastDayOfMonth).findList();
					     
		     */
		     
		    public static Page<Shift> page(int page, int pageSize,Long storeId,Long empId,String sortBy, String order,String filter) {
		    	Logger.info("@M Shift -->> page("+page+","+pageSize+","+storeId+","+empId+","+sortBy+","+order+","+filter+") -->>");
		    	 if(filter.equals("OPEN")){
		    		
		    		return 
				            ( find.where()
				                .ne("status", "SUBMITTED")
				               // .gt("timesheet.date",lastWeek)
				                .eq("timesheet.firmid",storeId)
				                .eq("timesheet.empid",empId)
				                .orderBy(sortBy + " " + order)
				                .fetch("terminal")
				        	    .fetch("timesheet")
				                .findPagingList(pageSize))	                
				                .setFetchAhead(false)
				                .getPage(page);
		    		
		    	}
		    	return 
		            ( find.where()
		                .ilike("status", "%" + filter + "%")
		               // .gt("timesheet.date",lastWeek)
		                .eq("timesheet.firmid",storeId)
		                .eq("timesheet.empid",empId)
		                .orderBy(sortBy + " " + order)
		                .fetch("terminal")
		        	    .fetch("timesheet")
		                .findPagingList(pageSize))	                
		                .setFetchAhead(false)
		                .getPage(page);
		    }
		    
		 // page() method is used for get Shift for employee
		    public static List<Shift> page(Long empId,String status) {
		    	
		    	return 
			             find.where()
			             	.ne("status",status)
			                .eq("timesheet.empid", empId)
			                .findList();
		    	
		    }

			public static  Shift getActiveShift(Terminal terminal) {
				  
				Logger.info("@M Shift -->> getActiveShift(Terminal) -->>");
				  Shift result = null;
				  
					 for(Shift shift : terminal.shifts){
	     				if(shift.status.equals("OPEN") || shift.status.equals("SAVED")){
	     					
	     						result = shift; 
	     					    					
	     				}
	     			 }
					 
					 Logger.info("@M Shift -->> getActiveShift(Terminal) <<--");
				  return result;
				  
				}
			
			
			public static  Long getShiftEmployeebyTerminal(Terminal terminal,Long empId) {
				
				Logger.info("@M Shift -->> getShiftEmployeebyTerminal(Terminal,"+empId+") -->>");
				  
				  Long result = null;
				  
					 for(Shift shift : terminal.shifts){
	     				if(shift.status.equals("OPEN") || shift.status.equals("SAVED")){
	     					return shift.timesheet.empid;
	     					
	     				}
	     			 }
					 
				Logger.info("@M Shift -->> getShiftEmployeebyTerminal(Terminal,"+empId+") <<--");
				  return result;
				  
				}
			/*
			 * Additonal method's used By summary page in Shift
			 * 
			 * author : Gopi
			 * 
			 */
			
			public static String getTotalNetTakings(Shift shift,String type) {
				
				Logger.info("@M Shift -->> getTotalNetTakings(shift id: "+shift.id+","+type+") -->>");
				  
				  String totalMediaCollected=Shift.getTotalMedialCollected(shift);
				  Logger.debug("@M Shift -->> getTotalNetTakings(shift id: "+shift.id+","+type+") -->> totalMediaCollected : "+totalMediaCollected);
				  String totalPayoutsByType=Shift.getTotalPayoutsbyType(shift,type);
				  Logger.debug("@M Shift -->> getTotalNetTakings(shift id: "+shift.id+","+type+") -->> totalPayoutsByType : "+totalPayoutsByType);
				  
				  Double paymentTenderTotal = 0.00;
			    	for(PaymentTender paymentTender : shift.terminal.dailySalesReconciliation.store.paymentTenders){ 
			    		
			    		if(! paymentTender.name.equals(Application.propertiesMap.get("PAYMENT_TENDER_CASHOUT"))){  // except CashOut add all the payment Tenders
			    			
			    			paymentTenderTotal = paymentTenderTotal+ Double.parseDouble(Shift.getShiftPaymentTenderAmount(shift.shiftPayments, paymentTender.name));
			    		}
			    	}
				  Double cashOut = Double.parseDouble(Shift.getShiftPaymentTenderAmount(shift.shiftPayments, Application.propertiesMap.get("PAYMENT_TENDER_CASHOUT")));
			    
				  Double l=Double.parseDouble(totalMediaCollected) + Double.parseDouble(totalPayoutsByType) + paymentTenderTotal - cashOut;
				   
				  l= Math.round( l * 100.0 ) / 100.0;
				  Logger.debug("@M Shift -->> getTotalNetTakings(shift id: "+shift.id+","+type+") -->> TotalNetTakings : "+l);
				  Logger.info("@M Shift -->> getTotalNetTakings(shift id: "+shift.id+","+type+") <<--");
				  
				  return l.toString();
				  
				}
			
			
			/*
			 * Additonal method's used By summary page in Shift
			 * 
			 * author : Gopi
			 * 
			 */
			
			public static String getNetPOSTakings(Shift shift) {
				
				Logger.info("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") -->>");
				  
				  String totalSales=Shift.getTotalSales(shift);
				  Logger.debug("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") -->> totalSales: "+totalSales);
				 String totalVariance=Shift.getTotalVariance(shift);
				  Logger.debug("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") -->> totalVariance: "+totalVariance);
				  Double accountSales=Shift.getShiftSale(shift.shiftSales,Application.propertiesMap.get("salesHaed_AC_SALES")).amount;
				  Logger.debug("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") -->> accountSales: "+accountSales);
				  Double accountRecv=Shift.getShiftSale(shift.shiftSales,Application.propertiesMap.get("salesHaed_AC_RECV")).amount;
				  Logger.debug("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") -->> accountRecv: "+accountRecv);
				  Double l=0.00;
				 
				 	l=Double.parseDouble(totalSales) -  Double.parseDouble(totalVariance);
				
				  l=l+accountRecv-accountSales;
				  
				 l= Math.round( l * 100.0 ) / 100.0;
				 Logger.debug("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") -->> NetPOSTakings: "+l);
				 Logger.info("@M Shift -->> getNetPOSTakings(shift id: "+shift.id+") <<--"+l);
				  return l.toString();
				  
				}
			
			// Media Difference Method ,Written By Gopi .After Demo with Santhosh

			public static Double getShiftMediaDifference(Shift shift){
				
				Double result = 0.00;
				Double actualAmt = 0.00;
				Double settleAmount =0.00;
				for(MediaTender media : MediaTender.all()){
					actualAmt = actualAmt + Double.parseDouble(Shift.getShiftMediaCollectedAmount(shift.mediaCollects,media.name));
					settleAmount = settleAmount + Double.parseDouble(Shift.getShiftMediaCollectedSettleAmount(shift.mediaCollects,media.name));
				}
				result = settleAmount - actualAmt;
				result= Math.round( result * 100.0 ) / 100.0;
				return result;
				
			}
			 
			/**
			 * In Time sheet Edit page,Employee selection option.,If Store Mangers are changed the employee of timesheet which is generated by System when Shift starts
			 * Those Timesheet Reflects in list of Shifts in SK ,because in list we get shifts based on Timesheet empid,timesheet firmid
			 * so,making Employee changing option of Edit Timesheet  READ ONLY for the SHIFT related Timesheets.
			 */
			
			public static Shift timesheetWithShift(Long tid){
				
				Logger.info("@M Shift -->> timesheetWithShift(timesheet id: "+tid+") -->>");
				
				 return 
				            ( find.where()
				                .eq("timesheet.id", tid))
				                .findUnique();
			}
			public static Shift getShiftId(Long id){
				  Shift sh=Shift.find.byId(id);
				  return sh;
				  
			  }
			 
}
