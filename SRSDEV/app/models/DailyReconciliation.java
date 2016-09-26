package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;

import controllers.Application;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.*;



/**
 * @author shravan
 *
 */
@Entity

public class DailyReconciliation extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	public Long id;
	
	//@Constraints.Required	
	@Formats.DateTime(pattern="dd/MM/yyyy")
    public Date createDate = new Date();	
	
	
	//@Constraints.Required	
	@Formats.DateTime(pattern="dd/MM/yyyy")
    public Date reportingBusinessDate;
	
	 public Double open_cash =0.00;
	 public Double close_cash =0.00;
	  
	 public Double open_cheque =0.00;
	 public Double close_cheque =0.00;
	 
	//SUBMITTED|SAVED|HOLIDAY-CLOSED|OPEN
	  @Constraints.Required	
	  public String status;
	  
	  //new Field to capture Daily Sales Analysis Report
	  @Constraints.Required	
	  public String dailyReportFile;
	
	  @OneToMany(cascade = CascadeType.ALL)
	 public List<Payroll> payroll = new ArrayList<Payroll>();
	  
	  @OneToMany(cascade = CascadeType.ALL)
	  public List<MiscExpense> miscExpenses = new ArrayList<MiscExpense>(); // new one added to capture other expenses from safe
	 
	 @OneToOne
	 public DailySalesReconciliation dsr;
	 
	 @OneToMany(cascade = CascadeType.ALL)	 
	  public List<Payout> payouts = new ArrayList<Payout>();
	 
	 //@OneToMany(cascade = CascadeType.ALL)	 
	// public List<SalesHead> salesheads=new ArrayList<SalesHead>();
	 
	 @OneToOne 
	 public BankDeposit bankDeposit;
	 
	 @OneToOne 
	 public Store store;
	  	 
	  
	    
	    /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,DailyReconciliation> find = new Finder<Long,DailyReconciliation>(Long.class, DailyReconciliation.class); 
	    
	 	public static List<DailyReconciliation> all() {
	 		Logger.info("@M DailyReconciliation -->> all() -->>");
		  return find.all();
		}

		public static void create(DailyReconciliation dr) {
			Logger.info("@M DailyReconciliation -->> create(DailyReconciliation id: "+dr.id+") -->>");
			dr.save();
			Logger.info("@M DailyReconciliation -->> create(DailyReconciliation id: "+dr.id+") <<--");
		}
		
		public static void delete(Long id) {
			Logger.info("@M DailyReconciliation -->> delete("+id+") -->>");
			  find.ref(id).delete();
			  Logger.info("@M DailyReconciliation -->> delete("+id+") <<--");
			}
		
		 
				  	    
		  public DailyReconciliation(Date business_date,Store store) {
			  
			  Logger.info("@M DailyReconciliation -->> 2-param constructor -->>");
			  this.createDate= new Date();
			 
			  this.reportingBusinessDate = business_date;
			  this.store = store;
			  this.status = "OPEN";
			 
			  this.payouts = new ArrayList<Payout>();
			  this.payroll = new ArrayList<Payroll>();
		}
		  
		  //Gives DR,after checking multiple conditions, whether it is already existed|| Previous one is closed or not || Corresponding DSR status check
	    public static DailyReconciliation getDailyReconciliation(Long storeid) {
	    	
	    	Logger.info("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") -->>");
	    	Store store = Store.find.byId(storeid);  	
	    	DailyReconciliation dr = null;
	    	List<DailyReconciliation> drList = DailyReconciliation.find.where()
	    										.eq("store.id",storeid)
	    										.orderBy("reportingBusinessDate desc").findList();
	    	if(drList!=null && drList.size()>0){
	    		
	    		dr = drList.get(0);
	    	}
	    	if(dr==null){
	    		
	    		Logger.debug("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") -->> inside if condition");
	    		
	    		dr = DailyReconciliation.getDR(new Date(), store); 
	    		
	    	}
	    	else if(dr.status.equals("SUBMITTED"))
	    	{
	    	
	    		Logger.debug("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") -->> inside esle-if condition");
	    		Date curr_date = dr.reportingBusinessDate;
	    		Logger.debug("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") -->> curr_date "+curr_date);
	    		Logger.debug("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") -->> todays date "+new Date());
	    		
			
				
				
				if (Application.getDate(curr_date).compareTo(Application.getDate(new Date()))!=0) {
					Date nextdate = Application.getNextDate(curr_date,1);
					Logger.debug("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") -->>inside if condition  next date "+nextdate);
		    		
		    		dr = DailyReconciliation.getDR(nextdate, store);
		    		
		    		
				}
	    		
	    		
	    	}
	    	
	    	Logger.info("@M DailyReconciliation -->> getDailyReconciliation("+storeid+") <<--");
	    	
	    	return dr;
		}
	    
	    //Gives Last Reporting Business Date
	    public static String getLastReportingBusinessDate(Long storeid) {
	    	
	    	Logger.info("@M DailyReconciliation -->> getLastReportingBusinessDate("+storeid+") -->>");
	    		
	    	String date = null;
	    	final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	    		    	
	    	DailyReconciliation dsr = null;
	     	List<DailyReconciliation> dsrList = DailyReconciliation.find.where()
	    										.eq("store.id",storeid)
	    										.eq("status","SUBMITTED")
	    										.orderBy("reportingBusinessDate desc").findList();
	    	
				if(dsrList!=null && dsrList.size()>0){
				
				dsr = dsrList.get(0);
				}	
				
				if(dsr!=null){   		
	    		  
				try {
					Logger.debug("@M DailyReconciliation -->> getLastReportingBusinessDate("+storeid+") -->> todays date: "+dsr.createDate);
					Logger.debug("@M DailyReconciliation -->> getLastReportingBusinessDate("+storeid+") -->> reportingBusinessDatee: "+dsr.reportingBusinessDate);
					
				//	String temp1 = format.format(dsr.reportingBusinessDate);
				//	String temp2 = format.format(dsr.createDate);
					
					date = format.format(dsr.reportingBusinessDate);
					
				/*	if (!(temp1.equals(temp2))) {
			    	 date = format.format(dsr.reportingBusinessDate);
					}*/
					} catch( Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    	
				Logger.info("@M DailyReconciliation -->> getLastReportingBusinessDate("+storeid+") <<--");
				System.out.println("last dr  date================  "+date);

	    	return date;
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
	    
	    public static Page<DailyReconciliation> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	filter="";
	    	Logger.info("@M DailyReconciliation -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
	        return 
	            ( find.where()
	                .ilike("status", "%" + filter + "%")
	                .orderBy(sortBy + " " + order)  	                
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	    
	    public static Page<DailyReconciliation> page(int page, int pageSize,Long storeId,String sortBy, String order,String filter) {
	      
	    	Logger.info("@M DailyReconciliation -->> page("+page+","+pageSize+","+storeId+","+sortBy+","+order+","+filter+") -->>");
	    	
	    	return 
	            ( find.where()
	                .ilike("status", "%" + filter + "%")
	               // .gt("timesheet.date",lastWeek)
	                .eq("store.id",storeId)
	                .orderBy(sortBy + " " + order)
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	    
	    public static Page<DailyReconciliation> pageByDate(int page, int pageSize,Long storeId,String sortBy, String order,String filter) {
		      
	    	Logger.info("@M DailyReconciliation -->> page("+page+","+pageSize+","+storeId+","+sortBy+","+order+","+filter+") -->>");
	    	
	    	return 
	            ( find.where()
	            		.eq("store_id", storeId)
	    				.lt("reporting_business_date", Application.getNextDate(filter, 1))
	    				.ge("reporting_business_date", Application.getDate(filter))
	    				.findPagingList(pageSize)).setFetchAhead(false).getPage(page);
	    }
	 // page() method is used for get DR's for a specific store
	    public static List<DailyReconciliation> page(Long storeId,String status) {
	    	
	    	return 
		             find.where()
		             	.ilike("status",status)
		                .eq("store.id", storeId)
		                .findList();
	    	
	    }
	    
	 // page() method is used for get DR's in DR View page in Head Office,,whose status is submitted,both startdate and enddate's given
	    public static Page<DailyReconciliation> page(int page, int pageSize, String sortBy, String order, String filter,String startDate,String endDate,Long storeId) {
	    	
	    	Logger.info("@M DailyReconciliation -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+","+startDate+","+endDate+","+storeId+") -->>");
	    	Date dateStartDate=Application.getDate(startDate);
	    	Date dateEndDate=Application.getDate(endDate);
	    	
	    			return 
	    		            ( find.where()
	    		                .ilike("status", "%" + filter + "%")
	    		               // .gt("timesheet.date",lastWeek)
	    		                .eq("store.id",storeId)
	    		                .ge("reportingBusinessDate", dateStartDate)
	    		                .le("reportingBusinessDate", Application.getNextDate(dateEndDate,1))
	    		                .orderBy(sortBy + " " + order)
	    		                .findPagingList(pageSize))	                
	    		                .setFetchAhead(false)
	    		                .getPage(page);
	    	
	    }
	    
	    // page() method is used for get DR's in DR View page in Head Office,,whose status is submitted,only single date: startDate
	    public static Page<DailyReconciliation> page(int page, int pageSize, String sortBy, String order, String filter,String startDate,Long storeId) {
	    	
	    	Logger.info("@M DailyReconciliation -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+","+startDate+","+storeId+") -->>");
	    	
	    	Date dateStartDate=Application.getDate(startDate);
	    			return 
	    		            ( find.where()
	    		                .ilike("status", "%" + filter + "%")
	    		               // .gt("timesheet.date",lastWeek)
	    		                .eq("store.id",storeId)
	    		                .ge("reportingBusinessDate", dateStartDate)
	    		                .lt("reportingBusinessDate", Application.getNextDate(dateStartDate, 1))
	    		                .orderBy(sortBy + " " + order)
	    		                .findPagingList(pageSize))	                
	    		                .setFetchAhead(false)
	    		                .getPage(page);
	    	
	    }
	    
	 // page() method is used for get DR's in DR View page in Head Office,,whose status is submitted, for first call,initial
	    public static Page<DailyReconciliation> page(int page, int pageSize,String sortBy, String order) {
	    	
	    	return ( find.where()
	                .ilike("status", "%" + "SUBMITTED" + "%")
	                .ge("reportingBusinessDate", Application.getDate())
	                .orderBy(sortBy + " " + order)
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);

	    }

	    
	    public String toString() {  
			  return id+": "+reportingBusinessDate+": "+status;
		  }
	    
	    //Gives last DR id 
	    public static Long getLastId(Long storeId) {
	    	
	    	Logger.info("@M DailyReconciliation -->> getLastId() -->>");
	    	
	    	Long lastDRId=null;
	    	
	    	try{
	    		lastDRId=(Long) DailyReconciliation.find.where()
        		.eq("store.id",storeId)
        		.orderBy("id desc").findIds().get(0);
	    	}catch(IndexOutOfBoundsException iobe){
	    		lastDRId=null;
	    	}
	    	
	         return lastDRId;
	    }

		public static void create(DailyReconciliation dailyReconciliation,
				Store store) {
			Logger.info("@M DailyReconciliation -->> create(dailyReconciliation,store) -->>");
			// TODO Auto-generated method stub
			
		}
		
		/**
		 *  @author Gopi
		 * 
		 * 	Methods used for Getting values in DR page Cash-In-Safe and Checks-In-Safe
		 * 
		 */
	    
		public static Double getCashInSafe(DailySalesReconciliation dsr, DailyReconciliation dr){
			
			Logger.info("@M DailyReconciliation -->> getCashInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->>");
			
			//assign getDRCASHINSAFE value to result variable
			Double result=DailyReconciliation.getDRCASHINSAFE(dr.id);
			
			Double safePayoutAmount=0.00;
			
			for(Payout payout:dr.payouts){
				
				safePayoutAmount=safePayoutAmount+Double.parseDouble(Payout.getPayoutAmount(payout, "SAFEPAYOUT"));
				
			}
			Logger.debug("@M DailyReconciliation -->> getCashInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->> safePayoutAmount  "+safePayoutAmount);
			//collect payRoll amount
			
			Double payrollAmount=Double.parseDouble(Payroll.getTotalPayrollAmount(dr.payroll));
			
			Logger.debug("@M DailyReconciliation -->> getCashInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->> payrollAmount  "+payrollAmount);
			
			
			if(dr.bankDeposit!=null){
				Double bankCash=dr.bankDeposit.cashAmt;
				Logger.debug("@M DailyReconciliation -->> getCashInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->> bankCash  "+bankCash);
			result=result-safePayoutAmount-payrollAmount-bankCash;
			
			}else{
				
				result=result-safePayoutAmount-payrollAmount;
				
			}
			 result=Math.round( result * 100.0 ) / 100.0;
			Logger.debug("@M DailyReconciliation -->> getCashInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->> CashInSafe  "+result);
			Logger.info("@M DailyReconciliation -->> getCashInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") <<--");
			System.out.println("safe cah====================== :"+result);
			 return result;
		}
		
		public static Double getChequesInSafe(DailySalesReconciliation dsr , DailyReconciliation dr){
				
			Logger.info("@M DailyReconciliation -->> getChequesInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->>");
					// assign getDRCHEQUESINSAFE  to result variable
					Double result=DailyReconciliation.getDRCHEQUESINSAFE(dr.id);
					
					//Double dsrChequesReported=DailySalesReconciliation.getChequesReported(dsr);
					
					//result=result+dsrChequesReported;
					if(dr.bankDeposit!=null){
					Double bankCheques=dr.bankDeposit.chequeAmt;
					
					Logger.debug("@M DailyReconciliation -->> getChequesInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->> bankCheques "+bankCheques);
					result=result-bankCheques;
					
					}
					 result=Math.round( result * 100.0 ) / 100.0;
					Logger.debug("@M DailyReconciliation -->> getChequesInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") -->> ChequesInSafe "+result);
			Logger.info("@M DailyReconciliation -->> getChequesInSafe(dsr id:"+dsr.id+", dr id: "+dr.id+") <<--");
					 return result;
				}
		
		
	   
		//used to get new DR, used in getDailyReconciliation(storeId)
		
		public static DailyReconciliation getDR(Date date,Store store){
			
			DailyReconciliation dr=new DailyReconciliation(date,store);
			
			
			
		
			//create BankDeposit Object for this DR
		BankDeposit bankDeposit=new BankDeposit(new Date(),0.00,0.00);
   		 bankDeposit.save();
   		 dr.bankDeposit=bankDeposit;

   		//Get last DR for previous DR close cash and cheque values
   		Long lastDRId=DailyReconciliation.getLastId(store.id);
   		
   		if(lastDRId!=null){
   		DailyReconciliation lastDR = DailyReconciliation.find.byId(lastDRId);
   		
   		//Assign last DR close-cash & close-cheque values to Today's DR 
   		dr.open_cash=Math.round( lastDR.close_cash * 100.0 ) / 100.0;
   		dr.close_cash=Math.round( lastDR.close_cash * 100.0 ) / 100.0;
   		
   		dr.open_cheque=Math.round( lastDR.close_cheque * 100.0 ) / 100.0;
   		dr.close_cheque=Math.round( lastDR.close_cheque * 100.0 ) / 100.0;
   		}else{
   			
   			dr.open_cash=0.00;
	    		dr.close_cash=0.00;
	    		
	    		dr.open_cheque=0.00;
	    		dr.close_cheque=0.00;
   			
   		}
   		// adding a new DSR for newly created DR
   		DailySalesReconciliation dsr = new DailySalesReconciliation(date,store);
		dsr.save();
   			
		dr.dsr = dsr;	
			dr.save();
			return dr;
			
			
		}
		
		
		
		// methods used to calculate close cash and cheques in DR.,used in Shifts submit() and DR update() methods
		
		public static Double getDRCASHINSAFE(Long id){
			
			DailyReconciliation dr = DailyReconciliation.find.byId(id);
			Double amount=dr.open_cash;
			Double shiftTotalCash=0.00;
			for(Terminal terminal : dr.dsr.terminals){
				for(Shift shift : terminal.shifts){
					if(shift.status.equals("SUBMITTED")){
						shiftTotalCash=shiftTotalCash+Shift.getShiftMediaCollected(shift.mediaCollects,"CASH").settleAmount;
					}
				}
			}
			amount=amount+shiftTotalCash;
			amount=Math.round( amount * 100.0 ) / 100.0;
			return amount;
			
		}
		
		public static Double getLastDRCASHINSAFE(Long id){
			
			DailyReconciliation dr = DailyReconciliation.find.byId(id);
			Double amount=dr.open_cash;
			Double shiftTotalCash=0.00;
			for(Terminal terminal : dr.dsr.terminals){
				for(Shift shift : terminal.shifts){
					if(shift.status.equals("SUBMITTED")){
						shiftTotalCash=Shift.getShiftMediaCollected(shift.mediaCollects,"CASH").settleAmount;
					}
				}
			}
			amount=amount+shiftTotalCash;
			amount=Math.round( amount * 100.0 ) / 100.0;
			return amount;
			
		}
		
			public static Double getDRCHEQUESINSAFE(Long id){
			
			DailyReconciliation dr = DailyReconciliation.find.byId(id);
			Double amount=dr.open_cheque;
			Double shiftTotalCheque=0.00;
			for(Terminal terminal : dr.dsr.terminals){
				for(Shift shift : terminal.shifts){
					if(shift.status.equals("SUBMITTED")){
						shiftTotalCheque=shiftTotalCheque+Shift.getShiftMediaCollected(shift.mediaCollects,"CHEQUE").settleAmount;
					}
				}
			}
			amount=amount+shiftTotalCheque;
			amount=Math.round( amount * 100.0 ) / 100.0;
			return amount;
			
		}
			
}
