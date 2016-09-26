package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.text.StrTokenizer;

import com.avaje.ebean.Page;

import controllers.Application;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

/**
 * @author shravan
 *
 */
@Entity
public class Timesheet extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Constraints.Required
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date date = new Date();

	@Constraints.Required
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date endDate = new Date();

	// @Constraints.Required
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
	public Long firmid;

	@Constraints.Required
	public String firmType;
	
	//activity added for Head office timesheet 
	@Constraints.Required
	public String activity;

	@Constraints.Required
	public String jobTitle;

	@Constraints.Required
	public String status;

	public Timesheet(Long empid, Long firmid, String firmType, String jobTitle,
			String status) {
		Logger.info("@M Timesheet -->> constructor  (" + empid + "," + firmid
				+ "," + firmType + "," + jobTitle + "," + status + ")");
		this.date = new Date();
		this.empid = empid;
		this.firmid = firmid;
		this.firmType = firmType;
		this.jobTitle = jobTitle;
		this.status = status;
	}

	public Timesheet(Long empId, String startDate, String endDate,
			String leaveType, String startHours, String startMins,
			String endHours, String endMins, String duration, String jobTitle,
			String firmType, Long storeId, String status) {

		this.empid = empId;
		this.date = Application.getDate(startDate);
		this.endDate = Application.getDate(endDate);
		this.leaveType = leaveType;
		this.startTimeHour = startHours;
		this.startTimeMins = startMins;
		this.endTimeHour = endHours;
		this.endTimeMins = endMins;
		this.duration = duration;
		this.jobTitle = jobTitle;
		this.firmType = firmType;
		this.firmid = storeId;
		this.status = status;

	}
	
	//Constructor added for Head Office Timesheet
	public Timesheet(Long empId, String startDate, String endDate,
			String leaveType, String startTimeHour, String startTimeMins,
			String endTimeHour, String endTimeMins, String duration,
			String activity, String jobTitle, String firmType, Long storeId,
			String status) {
		this.empid = empId;
		this.date = Application.getDate(startDate);
		this.endDate = Application.getDate(endDate);
		this.leaveType = leaveType;
		this.startTimeHour = startTimeHour;
		this.startTimeMins = startTimeMins;
		this.endTimeHour = endTimeHour;
		this.endTimeMins = endTimeMins;
		this.duration = duration;
		this.activity = activity;
		this.jobTitle = jobTitle;
		this.firmType = firmType;
		this.firmid = storeId;
		this.status = status;

	}

	public Timesheet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Generic query helper for entity Timesheet with id Long
	 */
	public static Finder<Long, Timesheet> find = new Finder<Long, Timesheet>(
			Long.class, Timesheet.class);

	public static void create(Timesheet timesheet) {
		Logger.info("@M Timesheet -->> create() -->>");
		timesheet.save();

		Logger.info("@M Timesheet -->> create() <<--");
	}

	public static void create(Long empId, String startDate, String endDate,
			String leaveType, String startHours, String startMins,
			String endHours, String endMins, String duration, String jobTitle,
			String firmType, Long storeId, String status) {
		Logger.info("@M Timesheet -->> create(" + empId + "," + startDate + ","
				+ endDate + "," + leaveType + "," + jobTitle + "," + firmType
				+ "," + storeId + ") -->>");

		Timesheet timesheet = new Timesheet(empId, startDate, endDate,
				leaveType, startHours, startMins, endHours, endMins, duration,
				jobTitle, firmType, storeId, status);
		timesheet.save();

		Logger.info("@M Timesheet -->> create(" + empId + "," + startDate + ","
				+ endDate + "," + leaveType + "," + jobTitle + "," + firmType
				+ "," + storeId + ") <<--");
	}

	public static void update(Long empId, String startDate, String endDate,
			String leaveType, String startHours, String startMins,
			String endHours, String endMins, String duration, String jobTitle,
			String firmType, Long storeId, String status, Long tid) {
		Logger.info("@M Timesheet -->> update(" + empId + "," + startDate + ","
				+ endDate + "," + leaveType + "," + jobTitle + "," + firmType
				+ "," + storeId + ") -->>");

		Timesheet timesheet = Timesheet.find.byId(tid);

		timesheet.empid = empId;
		timesheet.date = Application.getDate(startDate);
		timesheet.endDate = Application.getDate(endDate);
		timesheet.leaveType = leaveType;
		timesheet.startTimeHour = startHours;
		timesheet.startTimeMins = startMins;
		timesheet.endTimeHour = endHours;
		timesheet.endTimeMins = endMins;
		timesheet.duration = duration;
		timesheet.jobTitle = jobTitle;
		timesheet.firmType = firmType;
		timesheet.firmid = storeId;
		timesheet.status = status;

		timesheet.save();

		Logger.info("@M Timesheet -->> update(" + empId + "," + startDate + ","
				+ endDate + "," + leaveType + "," + jobTitle + "," + firmType
				+ "," + storeId + ") <<--");
	}

	public static List<Timesheet> all() {
		Logger.info("@M Timesheet -->> all() -->>");
		return find.all();

	}

	public static void delete(Long id) {
		Logger.info("@M Timesheet -->> delete(" + id + ") -->>");
		find.ref(id).delete();
		Logger.info("@M Timesheet -->> delete(" + id + ") <<--");
	}

	public static Employee getEmployee(Long id) {
		Logger.info("@M Timesheet -->> getEmployee(" + id + ") -->>");
		Employee employee = Employee.find.byId(id);
		Logger.debug("@M Timesheet -->> getEmployee(" + id
				+ ") -->>.,Retrived  Employee based on Timesheet Id "
				+ employee.id);
		return employee;
	}

	public static String getEmployee(Shift shift) {
		Logger.info("@M Timesheet -->> getEmployee(shift) -->>");
		String result = "";
		if (shift != null) {
			Employee employee = Employee.find.byId(shift.timesheet.empid);
			Logger.debug("@M Timesheet -->> getEmployee(shift) -->>.,Retrived  Employee   "
					+ employee.firstName);
			result = employee.toString();
		}
		return result;
	}

	/***
	 * Author: Gopi
	 * 
	 */

	public static Page<Timesheet> page(int page, int pageSize, String colName,
			Long value, String sortBy, String order, String filter) {
		Logger.info("@M Timesheet -->> page() -->>");
		return (find.where().ilike("status", "%" + filter + "%")
		// .gt("timesheet.date",lastWeek)
				.eq(colName, value).orderBy(sortBy + " " + order)
				.findPagingList(pageSize)).setFetchAhead(false).getPage(page);
	}

	// additional method used for filter criteria ,else condition inside list()
	// of Timesheets
	public static Page<Timesheet> page(int page, int pageSize, String colName,
			Long value, String filter) {
		Logger.info("@M Timesheet -->> page() -->>");

		return (find
				.where()
				// need to be Revised
				.eq(colName, value)
				.lt("date", Application.getNextDate(filter, 1))
				.ge("date", Application.getDate(filter))
				.findPagingList(pageSize)).setFetchAhead(false).getPage(page);
	}

	// additional method used for filter criteria ,else condition inside list()
	// of Timesheets in Timesheet for SK
	public static Page<Timesheet> page(int page, int pageSize, Long colName,
			Long value, String filter) {
		Logger.info("@M Timesheet -->> page() -->>");

		return (find
				.where()
				// need to be Revised
				.eq("empid", colName).eq("firmid", value)
				.lt("date", Application.getNextDate(filter, 1))
				.ge("date", Application.getDate(filter))
				.findPagingList(pageSize)).setFetchAhead(false).getPage(page);
	}

	// page () for listForsk() method ,to display list of timesheets for
	// StoreKeeper

	public static Page<Timesheet> page(int page, int pageSize, Long empId,
			Long storeId, String sortBy, String order, String filter) {
		Logger.info("@M Timesheet -->> page(listForsk()) -->>");
		return (find.where().ilike("status", "%" + filter + "%")
				.eq("firmid", storeId).eq("empid", empId)
				.orderBy(sortBy + " " + order).findPagingList(pageSize))
				.setFetchAhead(false).getPage(page);

	}

	/**
	 * author Gopi method used for ajax timesheet calculation
	 * 
	 */
	public static Long getTimesheetHours(Long empId, Date startDate) {
		Logger.info("@M Timesheet -->> getTimesheetHours(" + empId + ","
				+ startDate.getDate() + ") -->>");

		Date endDate = Application.getNextDate(startDate, 6);

		Logger.debug("@M Timesheet -->> getTimesheetHours(" + empId + ","
				+ startDate + ") -->> Start Date: " + startDate);
		Logger.debug("@M Timesheet -->> getTimesheetHours(" + empId + ","
				+ startDate + ") -->> End Date: " + endDate);

		Long duration = 0L;
		List<Timesheet> timesheetList = getTimesheetbyEmployee(empId,
				startDate, endDate);
		Logger.debug("@M Timesheet -->> getTimesheetHours(" + empId + ","
				+ startDate + ") -->> Timesheet list size : "
				+ timesheetList.size());

		if (timesheetList != null && timesheetList.size() > 0) {

			for (Timesheet timesheet : timesheetList) {

				if (timesheet.duration != null
						&& timesheet.duration.contains(":")) {
					String hoursMins[] = timesheet.duration.split(":");
					if (hoursMins != null) {

						duration = duration
								+ Long.parseLong(hoursMins[0].trim());

					}
				} else {
					String hoursMins = timesheet.duration;
					duration = duration + Long.parseLong(hoursMins);
				}
			}
		}
		Logger.info("@M Timesheet -->> getTimesheetHours(" + empId + ","
				+ startDate + ") <<--");
		return duration;

	}

	//method used for for ajax head office timesheet calculation
	public static Double getTotalTimesheetAmount(Long empId, Date startDate) {

		Logger.info("@M Timesheet -->> getTotalTimesheetAmount(" + empId + ","
				+ startDate + ") -->>");

		Double totalAmt = 0.00;
		String totalDuration = getTotalTimesheetHours(empId,
				Application.getDate(startDate));
		Logger.info("@M Timesheet -->> getTotalTimesheetAmount(" + empId + ","
				+ startDate + ") -->>Total Timesheet Hours :" + totalDuration);
		Double sal = Double.parseDouble(Employee.find.byId(empId).sal);
		Logger.info("@M Timesheet -->> getTotalTimesheetAmount(" + empId + ","
				+ startDate + ") -->>Sal Rate for Emp :" + sal);
		String[] hoursNmins = totalDuration.split(":");
		System.out.println(hoursNmins[0]);
		System.out.println(hoursNmins[1]);
		totalAmt = (Long.parseLong(hoursNmins[0]) * sal)
				+ (((Float.parseFloat(hoursNmins[1]) / 60)) * sal);
		totalAmt = Math.round(totalAmt * 100.0) / 100.0;
		// totalAmt=hours*sal;
		Logger.info("@M Timesheet -->> getTotalTimesheetAmount(" + empId + ","
				+ startDate + ") -->>Total amount :" + totalAmt);
		Logger.info("@M Timesheet -->> getTotalTimesheetAmount(" + empId + ","
				+ startDate + ") <<--");
		return totalAmt;
	}

	public static List<Timesheet> getTimesheetbyEmployee(Long empId,
			Date startDate, Date endDate) {

		Logger.info("@M Timesheet -->> getTimesheetbyEmployee(" + empId + ","
				+ startDate + "," + endDate + ") -->> Gives list of Timesheets");
		return (find.where().eq("empid", empId).eq("status", "APPROVED")
				.ge("date", startDate).le("date", endDate).orderBy("date asc")
				.findList());

	}

	// method used to find Timesheets in Admin module

	public static List<Timesheet> getTimesheetByEmployeeAndDate(Long sid,
			Long empId, Date startDate) {

		Logger.info("@M Timesheet -->> getTimesheetsStoreDuration(" + sid + ","
				+ empId + "," + startDate + ") -->> Gives list of Timesheets");
		return (find.where().eq("firmid", sid).eq("empid", empId)
				.eq("status", "APPROVED").ge("date", startDate)
				.lt("date", Application.getNextDate(startDate, 1))
				.orderBy("date asc").findList());

	}

	// method used to find Head office Timesheets in Admin module
	public static List<Timesheet> getHeadOfficeTimesheetByEmployee(Long empId,
			Date startDate, Date endDate) {

		Logger.info("@M Timesheet -->> getHeadOfficeTimesheetByEmployee("
				+ empId + "," + startDate + ") -->> Gives list of Timesheets");
		return (find.where().eq("empid", empId).eq("status", "SUBMITTED")
				.ge("date", startDate).le("date", endDate).orderBy("date asc")
				.findList());

	}

	// method used to find Head office Timesheets in Admin module

	public static List<Timesheet> getHeadOfficeTimesheetByEmployeeAndDate(
			Long empId, Date startDate, int i) {
		
		List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
		Timesheet timesheet = new Timesheet();
		Logger.info("@M Timesheet -->> getTimesheetsStoreDuration(" + empId
				+ "," + startDate + ") -->> Gives list of Timesheets");
		
		timeSheetList = find.where().eq("empid", empId)
				.eq("status", "SUBMITTED")
				.ge("date", Application.getNextDate(startDate, i - 1))
				.lt("date", Application.getNextDate(startDate, i))
				.orderBy("date asc").findList();
		
		Logger.info("@M Timesheet -->> getHeadOfficeTimesheetByEmployeeAndDate Timesheet list size"
				+ timeSheetList.size());

		if (timeSheetList.size() == 0) {
			timesheet.empid = -1l;
			timeSheetList.add(timesheet);
		}
		return timeSheetList;

	}

	// get TotalTimesheet Hours for Admin module Timesheet view

	public static String getTotalTimesheetHours(Long empId, Date startDate) {
		Logger.info("@M Timesheet -->> getTotalTimesheetHours(" + empId + ","
				+ startDate.getDate() + ") -->>");

		Date endDate = Application.getNextDate(startDate, 6);

		List<Timesheet> timesheetList = getTimesheetbyEmployee(empId,
				startDate, endDate);

		int hours = 0;
		int mins = 0;
		if (timesheetList != null && timesheetList.size() > 0) {

			for (Timesheet timesheet : timesheetList) {

				if (timesheet.duration != null
						&& timesheet.duration.contains(":")) {
					String hoursMins[] = timesheet.duration.split(":");
					if (hoursMins != null) {

						hours = hours + Integer.parseInt(hoursMins[0].trim());
						mins = mins + Integer.parseInt(hoursMins[1].trim());
					}
				} else {
					if (timesheet.duration != null) {
						hours = hours
								+ Integer.parseInt(timesheet.duration.trim());
					}

				}
			}
		}

		// add the minutes to hours if minutes are greater than 60
		while (mins >= 60) {
			hours = hours + 1;
			mins = mins - 60;
		}

		Logger.info("@M Timesheet -->> getTotalTimesheetHours(" + empId + ","
				+ startDate.getDate() + ") <<--");
		return hours + ":" + mins;
	}


	public static String getTotalHeadOfficeTimesheetHours(Long empId,
			Date startDate, Date endDate) {
		Logger.info("@M Timesheet -->> getTotalTimesheetHours(" + empId + ","
				+ startDate + " : " + endDate + ") -->>");

		// Date endDate = Application.getNextDate(startDate, days);

		List<Timesheet> timesheetList = getHeadOfficeTimesheetByEmployee(empId,
				startDate, endDate);

		int hours = 0;
		int mins = 0;
		if (timesheetList != null && timesheetList.size() > 0) {

			for (Timesheet timesheet : timesheetList) {

				if (timesheet.duration != null
						&& timesheet.duration.contains(":")) {
					String hoursMins[] = timesheet.duration.split(":");
					if (hoursMins != null) {

						hours = hours + Integer.parseInt(hoursMins[0].trim());
						mins = mins + Integer.parseInt(hoursMins[1].trim());
					}
				} else {
					if (timesheet.duration != null) {
						hours = hours
								+ Integer.parseInt(timesheet.duration.trim());
					}

				}
			}
		}

		// add the minutes to hours if minutes are greater than 60
		while (mins >= 60) {
			hours = hours + 1;
			mins = mins - 60;
		}

		Logger.info("@M Timesheet -->> getTotalTimesheetHours(" + empId + ","
				+ startDate.getDate() + ") <<--");
		//Logger.info("@M Timesheet -->> getTotalTimesheetHours(" + hours + ","
			//	+ mins + ") <<--");
		return hours + ":" + mins;
	}

	public static String getDurationForMultipleTimesheets(Date date,
			List<Timesheet> timesheets) {
		Logger.info("@M Timesheet -->> getDurationForMultipleTimesheets("
				+ date + ",TimesheetList) -->>");

		Long durationHours = 0L;
		Long durationMins = 0L;

		for (Timesheet timesheet : timesheets) {

			if (date.getDate() == timesheet.date.getDate()) {

				String[] hours = timesheet.duration.split(":");

				durationHours = durationHours + Long.parseLong(hours[0].trim());
				durationMins = durationMins + Long.parseLong(hours[1].trim());

			}

		}

		// add the minutes to hours if minutes are greater than 60
		while (durationMins >= 60) {
			durationHours = durationHours + 1;
			durationMins = durationMins - 60;
		}

		Logger.info("@M Timesheet -->> getDurationForMultipleTimesheets("
				+ date + ",TimesheetList) <<--");
		return durationHours + ":" + durationMins;

	}

	public static List<Timesheet> checkTimesheetExists(Long sid, Long empId,
			String date, String endDate, String sh, String sm, String eh,
			String em) {

		return (find.where().eq("status", "APPROVED").eq("firmid", sid)
				.eq("empid", empId).ge("date", Application.getDate(date))
				.lt("endDate", Application.getNextDate(endDate, 1))
				.orderBy("date asc").findList());

	}

	public static List<Timesheet> checkHeadOfficeTimesheetExists(Long sid,
			Long empId, String date, String endDate, String sh, String sm,
			String eh, String em) {

		return (find.where().eq("status", "SUBMITTED").eq("firmid", sid)
				.eq("empid", empId).ge("date", Application.getDate(date))
				.lt("endDate", Application.getNextDate(endDate, 1))
				.orderBy("date asc").findList());

	}

	public static List<Timesheet> checkTimesheetExistsForLeave(Long sid,
			Long empId, String date, String endDate) {

		return (find.where().eq("status", "APPROVED").eq("firmid", sid)
				.eq("empid", empId).ge("date", Application.getDate(date))
				.lt("endDate", Application.getNextDate(endDate, 1))
				.orderBy("date asc").findList());

	}

	public static List<Timesheet> checkHeadOfficeTimesheetExistsForLeave(
			Long sid, Long empId, String date, String endDate) {

		return (find.where().eq("status", "SUBMITTED").eq("firmid", sid)
				.eq("empid", empId).ge("date", Application.getDate(date))
				.lt("endDate", Application.getNextDate(endDate, 1))
				.orderBy("date asc").findList());

	}

	// method to get Employees Info to show head office
	public static List<Employee> getEmployeesInfoByTimesheet(Long sid,
			Date weekStartDate) {

		List<Employee> employees = new ArrayList<Employee>();
		Set<Long> ids = new TreeSet<Long>();

		List<Timesheet> timesheetList = find.where().eq("firmid", sid)
				.eq("status", "APPROVED").ge("date", weekStartDate)
				.lt("date", Application.getNextDate(weekStartDate, 7))
				.orderBy("date asc").findList();

		for (Timesheet timesheet : timesheetList) {

			ids.add(timesheet.empid);
		}
		// eliminate duplicate employee id's
		for (Long id : ids) {

			Employee employee = Employee.find.byId(id);
			employees.add(employee);
		}

		return employees;
	}

	// method to get Employees Info to show head office
	public static List<Employee> getHeadOfficeEmployeesInfoByTimesheet(
			Long empId, Date weekStartDate, Integer days) {

		Logger.info("No of days :" + days);

		List<Employee> employees = new ArrayList<Employee>();
		Set<Long> ids = new TreeSet<Long>();

		List<Timesheet> timesheetList = find.where().eq("empid", empId)
				.eq("status", "SUBMITTED").ge("date", weekStartDate)
				.lt("date", Application.getNextDate(weekStartDate, days))
				.orderBy("date asc").findList();

		Logger.info("Timesheet list size() :" + timesheetList.size());

		for (Timesheet timesheet : timesheetList) {

			ids.add(timesheet.empid);
		}
		// eliminate duplicate employee id's
		for (Long id : ids) {

			Employee employee = Employee.find.byId(id);
			employees.add(employee);
		}

		return employees;
	}

	public String toString() {
		return "Timesheet(" + id + ":" + date + ":" + empid + ":" + duration
				+ ":" + firmid + ":" + jobTitle + ")";
	}

	public static Timesheet addTimeSheet(Long empId, String date,
			String endDate, String leaveType, String startTimeHour,
			String startTimeMin, String endTimeHour, String endTimeMin,
			String duration, String activity, String jobTitle, String firmType,
			Long storeId, String status) {

		Timesheet tm = new Timesheet(empId, date, endDate, leaveType,
				startTimeHour, startTimeMin, endTimeHour, endTimeMin, duration,
				activity, jobTitle, firmType, storeId, status);
		// tm.save();

		return tm;
	}

	public static Timesheet savetimeSheet(Long empId, String date,
			String endDate, String leaveType, String startTimeHour,
			String startTimeMin, String endTimeHour, String endTimeMin,
			String duration, String activity, String jobTitle, String firmType,
			Long storeId, String status) {

		Timesheet tm = new Timesheet(empId, date, endDate, leaveType,
				startTimeHour, startTimeMin, endTimeHour, endTimeMin, duration,
				activity, jobTitle, firmType, storeId, status);
		tm.save();

		return tm;
	}

	public static List<Timesheet> getHeadOfficeEmployeeTimeSheet(Long storeId) {
		List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
		timeSheetList = find.where().eq("firmid", storeId)
				.eq("status", "SUBMITTED").findList();

		return timeSheetList;
	}

}