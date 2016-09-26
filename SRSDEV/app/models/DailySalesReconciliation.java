package models;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.OrderBy;
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

public class DailySalesReconciliation extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	public Long id;
	
//	@Constraints.Required	
	@Formats.DateTime(pattern="dd/MM/yyyy")
    public Date todayDate = new Date();	
	
	
	//@Constraints.Required	
	@Formats.DateTime(pattern="dd/MM/yyyy")
    public Date reportingBusinessDate;
	
	 @OneToMany(cascade = CascadeType.ALL)
	 public List<Terminal> terminals = new ArrayList<Terminal>();
	 
	 // new one added by Gopi on 01-04-2015,for assigning close cash amount to dr from shift
	 @OneToOne(mappedBy="dsr")
	 public DailyReconciliation dr;
		 
	 @OneToOne 
	 public Store store;
	 	 
	 
	  //SUBMITTED|SAVED|HOLIDAY-CLOSED|OPEN
	  public String status;
	  
	 
	  public Double cashInSafe =0.00;
	  
	 
	  public Double chequesInSafe =0.00;
	  
	  
	  public DailySalesReconciliation(Date todayDate,
			  Date reportingBusinessDate,List<Terminal> terminals,Store store)
	  {
		Logger.info("@M DailySalesReconciliation -->> 4-param constructor ");
		 this.todayDate= new Date();
		this.reportingBusinessDate = reportingBusinessDate;
		this.terminals = terminals;
		this.store = store;
		
		

	  }
	  
	  public DailySalesReconciliation(Date business_date, Store store) {
		  Logger.info("@M DailySalesReconciliation -->> 2-param constructor ");
		  this.todayDate= new Date();
		  this.reportingBusinessDate = business_date;
		  this.store = store;
		  this.status = "OPEN";
	}

	public static void create(DailySalesReconciliation dsr, Store store) {
		Logger.info("@M DailySalesReconciliation -->> create("+dsr.id+","+store.id+") -->>");
		  dsr.store = store;
		  dsr.save();
		  Logger.info("@M DailySalesReconciliation -->> create("+dsr.id+","+store.id+") <<--");
		}

	 
	  
	  public static Map<String,String> getTotalSalesMap(DailySalesReconciliation dsr,Store store,String term) {
		  
		  Logger.info("@M DailySalesReconciliation -->> getTotalSalesMap("+dsr.id+","+store.id+","+term+") -->>");
		  String result = null;
		  
		  Terminal candidate =  Terminal.getTerminalbyTerminalHead(dsr,term);
		  LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
		  if(candidate!=null){
		  for(TotalSalesHead totalSalesHead:store.totalSalesHeads){
			  
			  result = Terminal.getSaleAmount(totalSalesHead.name,candidate.shifts);
			  options.put(totalSalesHead.name, result);
			 			  
		  } 
		  }
		  Logger.info("@M DailySalesReconciliation -->> getTotalSalesMap("+dsr.id+","+store.id+","+term+") <<--");
		  return options;
		  
		}
	 	
	  	/**
	     * Create a new Terminal.
	     */
	    public static DailySalesReconciliation create(Date todayDate,
				  Date reportingBusinessDate,List<Terminal> terminals,Store store)
	    {
	    	Logger.info("@M DailySalesReconciliation -->> create(todayDate,reportingBusinessDate,terminals,store) -->>");
	    	DailySalesReconciliation dsr = new DailySalesReconciliation(todayDate, 
	    			reportingBusinessDate,terminals,store);
	    	dsr.save();
	    	Logger.info("@M DailySalesReconciliation -->> create(todayDate,reportingBusinessDate,terminals,store) <<--");
	        return dsr;
	    }
	    
	    /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,DailySalesReconciliation> find = new Finder<Long,DailySalesReconciliation>(Long.class, DailySalesReconciliation.class); 
	    
	 	public static List<DailySalesReconciliation> all() {
	 		Logger.info("@M DailySalesReconciliation -->> all() -->>");
		  return find.all();
		}

		public static void create(DailySalesReconciliation dsr) {
			Logger.info("@M DailySalesReconciliation -->> create("+dsr.id+") -->>");
			dsr.save();
			Logger.info("@M DailySalesReconciliation -->> create("+dsr.id+") <<--");
		}
		
		public static void delete(Long id) {
			Logger.info("@M DailySalesReconciliation -->> delete("+id+") -->>");
			  find.ref(id).delete();
			  Logger.info("@M DailySalesReconciliation -->> delete("+id+") <<--");
			}
				  	    
	    
	    public static Map<String,String> options() {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> options() -->>");
	    	
	    	/* 
	    	 Long lastId = getLastId();
	         int oldCount = DailySalesReconciliation.find.all().size();

	         DailySalesReconciliation existingDSR = Ebean.find(DailySalesReconciliation.class, lastId);
	        */
	        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
	        for(DailySalesReconciliation c: DailySalesReconciliation.find.findList()) {
	            options.put(c.id.toString(), c.status);
	        }
	        Logger.info("@M DailySalesReconciliation -->> options() <<--");
	        return options;
	    }
	    
	    public static DailySalesReconciliation getDailySalesReconciliation(Long storeid) {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->>");
	    	Store store = Store.find.byId(storeid);  	
	    	Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> store "+store.name);
	    	DailySalesReconciliation dsr = null;
	    	List<DailySalesReconciliation> dsrList = DailySalesReconciliation.find.where()
	    										.eq("store.id",storeid)
	    										.orderBy("reportingBusinessDate desc").findList();
	    										
	    	if(dsrList!=null && dsrList.size()>0){
	    		
	    		dsr = dsrList.get(0);
	    	}
	    	if(dsr==null){
	    		
	    		Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> inside if condition");
	    		DailyReconciliation dr=DailyReconciliation.getDailyReconciliation(storeid);
	    		dsr =dr.dsr;
	    		
	    	}
	    	else if(dsr.status.equals("SUBMITTED"))
	    	{
	    	
	    		Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> inside else-if condition");
	    		Date curr_date = dsr.reportingBusinessDate;
	    		
	    		Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> curr_date "+curr_date);
	    		Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> created date "+dsr.todayDate);
	    		
				
	    		/*if (Application.getDate(curr_date).compareTo(Application.getDate(new Date()))!=0) {
					Date nextdate = Application.getNextDate(curr_date,1);
					Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> inside if condition - next date "+nextdate);
					
					//checking  DR is SUBMITTED or not,While opening a new DSR in a same day
					
					Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> dr.status "+dr.status);
					Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> dr.dsr.id "+dr.dsr.id);
					Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> dsr.id "+dsr.id);
					
				}*/
	    		DailyReconciliation dr=DailyReconciliation.getDailyReconciliation(storeid);
	    	
	    		if(dr.dsr.id==dsr.id && dr.status.equals("OPEN")){
					Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> dr.status.equals "+dr.status);
					//need to be revised
					dsr=null;
					
				}
	    		else{
	    		
	    			dsr = dr.dsr;
	    		}
	    	}
	    	
	    	
	    	//Logger.debug("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") -->> returning todays date: "+dsr.todayDate);
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getDailySalesReconciliation("+storeid+") <<--");
	    	return dsr;
		}
	    
	    public static String getLastReportingBusinessDate(Long storeid) {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") -->>");
	    		
	    	String date = null;
	    	final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	    		    	
	    	DailySalesReconciliation dsr = null;
	     	List<DailySalesReconciliation> dsrList = DailySalesReconciliation.find.where()
	    										.eq("store.id",storeid)
	    										.eq("status","SUBMITTED")
	    										.orderBy("reportingBusinessDate desc").findList();
	    	
				if(dsrList!=null && dsrList.size()>0){
				
				dsr = dsrList.get(0);
				}	
				
				if(dsr!=null){   		
	    		  
				try {
					Logger.debug("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") -->> todays date:" +dsr.todayDate);
					Logger.debug("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") -->> reportingBusinessDatee:" +dsr.reportingBusinessDate);
					
					//String temp1 = format.format(dsr.reportingBusinessDate);
					//String temp2 = format.format(dsr.todayDate);
				
					 date = format.format(dsr.reportingBusinessDate);
				/*	if (!(temp1.equals(temp2))) {
			    	 date = format.format(dsr.reportingBusinessDate);
					}
					*/
					} catch( Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	    	}
	    	
				Logger.info("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") <<--");
	    	return date;
		}
	    //written for lastReporting Date view in Reconcilation reports
	    public static String getLastReportingBusinessDateForView(Long storeid,String startDateString,String endDateString) {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") -->>");
	    	
	    	String date = null;
	    	final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	    	Date endDate=null;
	    	if(endDateString.trim().equals("")){
	    		endDate=Application.getNextDate(startDateString, 1);
	    	}else{
	    		endDate=Application.getDate(endDateString);
	    	}
	    	//Here DailySalesReconsilation changed to DailyReconcilation to show reporting date upto dr status submitted only.	    	
	    	DailyReconciliation dsr = null;
	     	List<DailyReconciliation> dsrList = (DailyReconciliation.find.where()
	    										.eq("store.id",storeid)
	    										.eq("status","SUBMITTED")
	    										.between("reportingBusinessDate", Application.getDate(startDateString), endDate))
		    									.orderBy("reportingBusinessDate desc").findList();
	    										
	    	
				if(dsrList!=null && dsrList.size()>0){
				
				dsr = dsrList.get(0);
				}	
				
				if(dsr!=null){   		
	    		  
				try {
					Logger.debug("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") -->> todays date:" +dsr.createDate);
					Logger.debug("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") -->> reportingBusinessDatee:" +dsr.reportingBusinessDate);
					
					//String temp1 = format.format(dsr.reportingBusinessDate);
					//String temp2 = format.format(dsr.todayDate);
				
					 date = format.format(dsr.reportingBusinessDate);
				/*	if (!(temp1.equals(temp2))) {
			    	 date = format.format(dsr.reportingBusinessDate);
					}
					*/
					} catch( Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
	    	}
	    	
				Logger.info("@M DailySalesReconciliation -->> getLastReportingBusinessDate("+storeid+") <<--");
				System.out.println("last date================  "+date);
	    	return date;
		}
	    
	    
	    //end
	    
	    
	    public static Double getVarianceAmount(DailySalesReconciliation dsr) {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getVarianceAmount("+dsr.id+") -->>");
			  
	    	Double result = 0.00;
	    	if(dsr.terminals!=null){
			  for(Terminal candidate: dsr.terminals) {
				  
	    		 
	    			  for(Shift shift:candidate.shifts){
	    			 result = result +shift.shiftVariance;
	    			  }
	  			   
		        }
			  Logger.debug("@M DailySalesReconciliation -->> getVarianceAmount("+dsr.id+") -->> VarianceAmount :"+result);
			  result=Math.round( result * 100.0 ) / 100.0;
	        }
	    	Logger.info("@M DailySalesReconciliation -->> getVarianceAmount("+dsr.id+") <<--");
		       return result;
			  
			}
	    
	    
	    public static Double getTotalSalesAmount(DailySalesReconciliation dsr) {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getTotalSalesAmount("+dsr.id+") -->>");
			  
	    	Double result = 0.00;
	    	if(dsr.terminals!=null){
			  for(Terminal candidate: dsr.terminals) {
				  Logger.debug("@M DailySalesReconciliation -->> getTotalSalesAmount("+dsr.id+") -->> Terminal id :"+candidate.id + "termilas size: "+dsr.terminals.size());
	    		 String amount = Terminal.getSaleAmount("TOTALSALES",candidate.shifts);
	    			 result = result + Double.valueOf(amount);
	  			   
		        }
			  Logger.debug("@M DailySalesReconciliation -->> getTotalSalesAmount("+dsr.id+") -->> TotalSalesAmount :"+result);
			  result=Math.round( result * 100.0 ) / 100.0;
	    	}
	    	Logger.info("@M DailySalesReconciliation -->> getTotalSalesAmount("+dsr.id+") <<--");
		       return result;
			  
			}
	    
	    
	    // Gopi
	    
	    public static Double getTotalMediaCollected(DailySalesReconciliation dsr) {
	    	
	    	Logger.info("@M DailySalesReconciliation -->> getTotalMediaCollected("+dsr.id+") -->>");
	    	
	    	Double result = 0.00;
	    	if(dsr.terminals!=null){
			  for(Terminal candidate: dsr.terminals) {
				  
		          for(Shift shift : candidate.shifts){
		        	  	
		        	  String amount = Shift.getTotalMedialCollected(shift);
		        	  result = result + Double.parseDouble(amount);
		          }
		           
		        }
			  result=Math.round( result * 100.0 ) / 100.0;
	    	}
	    	Logger.info("@M DailySalesReconciliation -->> getTotalMediaCollected("+dsr.id+") <<--");
		       return result;
			  
			}
	    
	    public static Double getTotalPayouts(DailySalesReconciliation dsr) {
			  
	    	Logger.info("@M DailySalesReconciliation -->> getTotalPayouts("+dsr.id+") -->>");
	    	Double result = 0.00;
	    	if(dsr.terminals!=null){
			  for(Terminal candidate: dsr.terminals) {
				  
		          for(Shift shift: candidate.shifts){
		        	  
		        	  String amount = Shift.getTotalPayoutsbyType(shift,"REGISTERPAYOUT");
	    			 result = result + Double.parseDouble(amount);
		          }
		         
		        }
			  result=Math.round( result * 100.0 ) / 100.0;
	    	}
	    	Logger.info("@M DailySalesReconciliation -->> getTotalPayouts("+dsr.id+") <<--");
		       return result;
			  
			}
	    
	    public static Double getOtherPayouts(DailySalesReconciliation dsr) {
			  
	    	Logger.info("@M DailySalesReconciliation -->> getOtherPayouts("+dsr.id+") -->>");
	    	Double result = 0.00;
	    	if(dsr.terminals!=null){
			  for(Terminal candidate: dsr.terminals) {
				  
		          for(Shift shift: candidate.shifts){
		        	  
		        	  Double amount = ShiftPaymentTender.getShiftPaymentsTotalAmount(shift);
	    			 result = result + amount;
		          }
		         
		        }
			  result=Math.round( result * 100.0 ) / 100.0;
	    	}
	    	Logger.info("@M DailySalesReconciliation -->> getOtherPayouts("+dsr.id+") <<--");
		       return result;
			  
		}
	    
	    public static Double getTotalCashOuts(DailySalesReconciliation dsr) {
			  
	    	Logger.info("@M DailySalesReconciliation -->> getTotalCashOuts("+dsr.id+") -->>");
	    	Double result = 0.00;
	    	if(dsr.terminals!=null){
			  for(Terminal candidate: dsr.terminals) {
				  
		          for(Shift shift: candidate.shifts){
		        	  
		        	  String amount = Shift.getShiftPaymentTenderAmount(shift.shiftPayments, Application.propertiesMap.get("PAYMENT_TENDER_CASHOUT"));
	    			 result = result + Double.parseDouble(amount);
		          }
		         
		        }
			  result=Math.round( result * 100.0 ) / 100.0;
	    	}
	    	Logger.info("@M DailySalesReconciliation -->> getTotalCashOuts("+dsr.id+") <<--");
		       return result;
			  
		}
	    
	    /*
	     * author Gopi
	     * methods used for getting cash in safe,cheques in safe at Dsr list page
	     * 
	     */
	    
		 public static Double getCashReported(DailySalesReconciliation dsr) {
					  
			 Logger.info("@M DailySalesReconciliation -->> getCashReported("+dsr.id+") -->>"); 	
			    	Double result = 0.00;
			    	if(dsr.terminals!=null){
					  for(Terminal candidate: dsr.terminals) {
						 
						  
						  for(Shift shift:candidate.shifts){
							   
							  String amount=Shift.getShiftMediaCollectedSettleAmount(shift.mediaCollects, "CASH");
							  //String payouts=Shift.getTotalPayoutsbyType(shift, "REGISTERPAYOUT");
							  //above code is commented as cash amount excludes payout amount.
							  
							  result=result+Double.parseDouble(amount); //-Double.parseDouble(payouts);
						  }
						  
				        }
					  Logger.debug("@M DailySalesReconciliation -->> getCashReported("+dsr.id+") -->> CashReported "+result);
					  result=Math.round( result * 100.0 ) / 100.0;
			    	}
			    	Logger.info("@M DailySalesReconciliation -->> getCashReported("+dsr.id+") <<--"); 	
				       return result;
					  
					}
	   
		 	public static Double getChequesReported(DailySalesReconciliation dsr) {
			  
		 		Logger.info("@M DailySalesReconciliation -->> getChequesReported("+dsr.id+") -->>");
		    	Double result = 0.00;
		    	if(dsr.terminals!=null){
				  for(Terminal candidate: dsr.terminals) {
					 
			          
					  for(Shift shift:candidate.shifts){
						
						  String amount=Shift.getShiftMediaCollectedSettleAmount(shift.mediaCollects, "CHEQUE");
						  result=result+Double.parseDouble(amount);
					  }
		  			   
			        }
				  Logger.debug("@M DailySalesReconciliation -->> getChequesReported("+dsr.id+") -->>ChequesReported "+result);
				  result=Math.round( result * 100.0 ) / 100.0;
		    	}
		    	Logger.info("@M DailySalesReconciliation -->> getChequesReported("+dsr.id+") <<--");
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
   
	     
	     */
	     
	    public static Page<DailySalesReconciliation> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	filter="";
	    	Logger.info("@M DailySalesReconciliation -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
	        return 
	            ( find.where()
	                .ilike("status", "%" + filter + "%")
	                .orderBy(sortBy + " " + order)  	                
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	    
	    // page() method is used for get DSR's for a specific store
	    public static List<DailySalesReconciliation> page(Long storeId,String status) {
	    	
	    	return 
		             find.where()
		             	.ilike("status",status)
		                .eq("store.id", storeId)
		                .findList();
	    	
	    }
	    public static Page<DailySalesReconciliation> page(int page, int pageSize,Long storeId,String sortBy, String order,String filter) {
	       
	    	
	    	Logger.info("@M DailySalesReconciliation -->> page("+page+","+pageSize+","+storeId+","+sortBy+","+order+","+filter+") -->>");
	    	
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
	    
	    //page method used in Reports Controller ,for particular store
	    public static List<DailySalesReconciliation> page(Long storeId,String startDateString , String endDateString) {
	    	//System.out.println("inside page() store id : "+storeId);
	    	Date endDate=null;
	    	if(endDateString.trim().equals("")){
	    		endDate=Application.getNextDate(startDateString, 1);
	    	}else{
	    		endDate=Application.getDate(endDateString);
	    	}
	    	 
	    	List<DailySalesReconciliation> list = find.where()
	    										  .eq("store.id", storeId)
	    										//  .lt("reportingBusinessDate",Application.getNextDate(startDate, 1))
	    										 // .ge("reportingBusinessDate",Application.getDate(startDate))
	    										  	.ge("reportingBusinessDate", Application.getDate(startDateString))
	    										  	.le("reportingBusinessDate", endDate)
	    										  .findList();
	    	//System.out.println("DSR Reports : for a single store "+list);
	    	return list;
	    }
	    
	    //page method used in Reports Controller  ,for all stores
	    public static List<DailySalesReconciliation> page(String storeId,String startDateString , String endDateString) {
	    	//System.out.println("inside page() store id : "+storeId);
	    	Date endDate=null;
	    	if(endDateString.trim().equals("")){
	    		endDate=Application.getNextDate(startDateString,1);
	    	}else{
	    		endDate=Application.getDate(endDateString);
	    	}
	    	 
	    	List<DailySalesReconciliation> list = find.where()
	    										  //.eq("store.id", storeId)
	    										//  .lt("reportingBusinessDate",Application.getNextDate(startDate, 1))
	    										 // .ge("reportingBusinessDate",Application.getDate(startDate))
	    										  	.ge("reportingBusinessDate", Application.getDate(startDateString))
	    										  	.le("reportingBusinessDate", endDate)
	    										  	.findList();
	    	//System.out.println("DSR Reports : for all store "+list);
	    	return list;
	    	
	    	
	    }
	    
	    
	    public String toString() {  
			  return id+": "+reportingBusinessDate+": "+status;
		  }
	    
	    public static Long getLastId() {
	    	Logger.info("@M DailySalesReconciliation -->> getLastId() -->>");
	        return (Long) DailySalesReconciliation.find.orderBy("id desc").findIds().get(0);
	    }
	    
	    
}
