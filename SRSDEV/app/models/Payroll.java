package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import controllers.Application;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.*;

/**
 * @author shravan
 *
 */
@Entity 
public class Payroll extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;
	
	@Constraints.Required	
	@Formats.DateTime(pattern="MM/dd/yyyy")
    public Date date = new Date();
	
	@Formats.DateTime(pattern="MM/dd/yyyy")
    public Date from_date;
	
	@Formats.DateTime(pattern="MM/dd/yyyy")
    public Date to_date;
	
	 @ManyToOne()
	 public DailyReconciliation dailyReconciliation;
	
	
	
	@Constraints.Required
	  public Double payAmt=0.00;
	
	@Constraints.Required
	public long empid;
	
	public Payroll(Date date,Date fromDate,Date toDate,Double payAmt,Long empId){
		
		Logger.info("@M Payroll -->> constructor Payroll("+date+","+fromDate+","+toDate+","+payAmt+","+empId+") -->>");
		
		this.date=date;
		this.from_date=fromDate;
		this.to_date=toDate;
		this.payAmt=payAmt;
		this.empid=empId;
		
		
		
	}
	

	
	/**
     * Generic query helper for entity Payroll with id Long
     */
    public static Finder<Long,Payroll> find = new Finder<Long,Payroll>(Long.class, Payroll.class); 
    
    
    
 	public static List<Payroll> all() {
 		Logger.info("@M Payroll -->> all() -->>");
	  return find.all();
	}

	
	public static void delete(Long id) {
		Logger.info("@M Payroll -->> delete("+id+") -->>");
	  find.ref(id).delete();
	  Logger.info("@M Payroll -->> delete("+id+") <<--");
	}
	
	
	public String toString() {  
		 return "Payroll("+ empid +":" + payAmt +":" + date + ")";
	  }


	public static String getPayrollAmount(Payroll payroll) {
		
		Logger.info("@M Payroll -->> getPayrollAmount(payroll) -->>");
		Double result = 0.00;		
	           
   		  
   			 result = payroll.payAmt;
 			
   			result=Math.round( result * 100.0 ) / 100.0;
   			Logger.info("@M Payroll -->> getPayrollAmount(payroll) <<--"); 
		  return result.toString();
	} 
	
	public static String getTotalPayrollAmount(List<Payroll> payrollList) {
		Logger.info("@M Payroll -->> getTotalPayrollAmount(payrollList) -->>");
		Double result = 0.00;		
	      for(Payroll payroll: payrollList){
	    	 
	    	  if(payroll != null){
	    		
   			 result = result + payroll.payAmt;
	    	  }
	      }
	      result=Math.round( result * 100.0 ) / 100.0;
	      Logger.debug("@M Payroll -->> getTotalPayrollAmount(payrollList) -->> TotalPayrollAmount "+result);
	      Logger.info("@M Payroll -->> getTotalPayrollAmount(payrollList) <<--");
		  return result.toString();
	} 
	
	//Gopi
	
	public static Payroll getPayroll(Long empId,String weekDate,Long drId){
		
		Logger.info("@M Payroll -->> getPayroll("+empId+", String  "+weekDate+", "+drId+") -->>");
		
		
		
		List<Payroll> payrolls = new ArrayList<Payroll>();
		
		Date weekStartDate=Application.getDate(weekDate);
		Payroll payroll=getPayroll( empId, weekStartDate, drId);
		
		
		if(payroll==null){
			Logger.debug("@M Payroll -->> getPayroll("+empId+", String "+weekDate+","+drId+") -->> inside if condition");
		DailyReconciliation dailyReconciliation = DailyReconciliation.find.byId(drId);
		payroll=new Payroll(new Date(),weekStartDate,Application.getNextDate(weekStartDate, 6),0.00,empId);
		payroll.dailyReconciliation=dailyReconciliation;
		payroll.save();
		payrolls=dailyReconciliation.payroll;
		
		payrolls.add(payroll);
		dailyReconciliation.payroll=payrolls;
		dailyReconciliation.save();
		
		}
		else{
			Logger.debug("@M Payroll -->> getPayroll("+empId+", String "+weekDate+","+drId+") -->> inside else condition");
			payroll=null;
			
		}
	
		Logger.info("@M Payroll -->> getPayroll("+empId+", String "+weekDate+","+drId+") <<--");
		
		return payroll;
	}
	
	public static Payroll getPayroll(Long empId,Date weekDate,Long drId){
		
		Logger.info("@M Payroll -->> getPayroll("+empId+", Date "+weekDate+","+drId+") -->>");
			
			return 
	 	            ( find.where()
	 	                .eq("empid", empId)
	 	                .eq("from_date", weekDate)
	 	                .eq("dailyReconciliation.id", drId)
	 	                ).findUnique();
	 	                
	 	   
		}
	public static Double getAmountPaidbyWeek(Long empId,Date weekDate,Long drId){
		
		Logger.info("@M Payroll -->> getAmountPaidbyWeek("+empId+", Date "+weekDate+","+drId+") -->>");
		
		List<Payroll> payrollList=getAmountPaidbyWeekList(empId,weekDate,drId);
		Double amount=0.00;
		if( payrollList.size()>0){
			for(Payroll payroll:payrollList){
				
				if(payroll!=null){
					amount=amount+payroll.payAmt;
					
				}
			}
			Logger.debug("@M Payroll -->> getAmountPaidbyWeek("+empId+", Date "+weekDate+","+drId+") -->> AmountPaidbyWeek "+amount);
		}
		amount=Math.round( amount * 100.0 ) / 100.0;
		Logger.info("@M Payroll -->> getAmountPaidbyWeek("+empId+", Date "+weekDate+","+drId+") <<--");
		return amount;
	}
		
	public static List<Payroll> getAmountPaidbyWeekList(Long empId,Date weekDate,Long drId){
		
		Logger.info("@M Payroll -->> getAmountPaidbyWeekList("+empId+", Date "+weekDate+","+drId+") -->>");
		return 
 	            ( find.where()
 	                .eq("empid", empId)
 	                .eq("from_date", weekDate)
 	                .ne("dailyReconciliation.id", drId)
 	                ).findList();
 	                
 	   
	}
}