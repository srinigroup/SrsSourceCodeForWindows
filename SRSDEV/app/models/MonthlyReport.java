package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class MonthlyReport extends Model{

	private static final long serialVersionUID = 1L;
	
	@Id 
	public Long id;
	
	public String fileName;
	
	public String filePath;
	
	@Formats.DateTime(pattern="dd/MM/yyyy hh:mm:ss")
    public Date uploadedDate;
	
	public Long storeId;
	
	 public String reportingMonth;
	 
	 public String reportType;
	 
	// constructor
	 public MonthlyReport(String fileName, String reportType, Long storeId, String filePath, String reportingMonth, Date uploadedDate){
			  
		 this.fileName = fileName;
		 this.uploadedDate = uploadedDate;
		 this.reportType = reportType;
		 this.storeId = storeId;
		 this.filePath = filePath;
		 this.reportingMonth = reportingMonth;
	}
	 
	 public static Finder<Long,MonthlyReport> find = new Finder<Long,MonthlyReport>(Long.class, MonthlyReport.class);
	 
	// Create new MonthyReport
	 public static void create(String fileName,String reportType, Long storeId, String filePath, String reportingMonth){
		 
		 Logger.info("@C MonthlyReport -->> create("+fileName+","+reportType+","+storeId+","+filePath+","+reportingMonth+") -->> ");
		 
		 MonthlyReport monthlyReport = new MonthlyReport(fileName,reportType,storeId,filePath,reportingMonth,new Date());
		 monthlyReport.save();
		 
		 Logger.info("@C MonthlyReport -->> create("+fileName+","+reportType+","+storeId+","+filePath+","+reportingMonth+") <<--");
		 		
	 }
	 
	 public static String getModifiedFileName(String fileNameWithExtension,Long sid,Long id, String month, String reportType, int year){
	 		
	 		Logger.info("@C MonthlyReport -->> getModifiedFileName("+fileNameWithExtension+","+sid+","+id+") -->> ");
	 		   	String modifiedFileName ;
	 		   	String beforeDot = StringUtils.substringBeforeLast(fileNameWithExtension, ".");
	 		   	String afterDot =   StringUtils.substringAfterLast(fileNameWithExtension, ".");    
	 		   	
	 		   	// checking whether user upload a file with extension or not
	 		   	if(afterDot.equals("")){
	 		   		modifiedFileName = year+"_"+month+"_"+reportType+"_"+sid+"_"+id;
	 		   	}else{
	 		   		modifiedFileName = year+"_"+month+"_"+reportType+"_"+sid+"_"+id+"."+afterDot;
	 		   	}
	 		   	
	 		   Logger.info("@C MonthlyReport -->> getModifiedFileName("+fileNameWithExtension+","+sid+","+id+") <<--");
	 	 		return modifiedFileName;
	 	 }
	
	 //Get list of Reports based on Store and month
	 	public static List<MonthlyReport> page(Long storeId, String month) {
	    	
	        return 
	            ( find.where()
	                .eq("storeId",storeId)
	                .eq("reportingMonth",month))
	                .findList();
	    }
	 	
	 	//Get list of Reports based on Store and month and year
	 	public static List<MonthlyReport> page(Long storeId, String month, int year) {
	    	
	        return 
	            ( find.where()
	            	.like("fileName", year+"%")
	                .eq("storeId",storeId)
	                .eq("reportingMonth",month))
	                .findList();
	    }
}
