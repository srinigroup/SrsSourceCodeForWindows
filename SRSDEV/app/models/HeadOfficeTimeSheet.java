package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import controllers.Application;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class HeadOfficeTimeSheet extends Model {
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;

	@Constraints.Required	
	@Formats.DateTime(pattern="dd/MM/yyyy")
    public Date date = new Date();
	
	@Constraints.Required	
	@Formats.DateTime(pattern="dd/MM/yyyy")
    public Date endDate = new Date();
	
	//@Constraints.Required
	  public String duration;
	
	@Constraints.Required
	  public String startTimeHour;
	
	@Constraints.Required
	  public String startTimeMins;
	
	@Constraints.Required
	  public String endTimeHour;
	
	@Constraints.Required
	  public String endTimeMins;
	
	@Constraints.Required
	  public String leaveType;
	
	@Constraints.Required
	  public Long empid;
	
	@Constraints.Required
	  public String jobTitle;
	@Constraints.Required	
	  public String activity="Store Work";
	
	 public HeadOfficeTimeSheet(Long empId,String startDate,String startTimeHour,String startTimeMins,String endTimeHour,String endTimeMins,String activity){
		 this.empid=empId;
		 this.date=Application.getDate(startDate);
		 this.startTimeHour=startTimeHour;
		 this.startTimeMins=startTimeMins;
		 this.endTimeHour=endTimeHour;
		 this.endTimeMins=endTimeMins;
		 this.activity=activity;
		 
	 }
	 public static HeadOfficeTimeSheet savetimeSheet(Long empId, String date,
				String startTimeHour, String startTimeMin, String endTimeHour,
				String endTimeMin,String activity){
		 HeadOfficeTimeSheet tm=new HeadOfficeTimeSheet(empId, date, startTimeHour, startTimeMin, endTimeHour, endTimeMin, activity);
			tm.save();
			
			return tm;
		}

}
