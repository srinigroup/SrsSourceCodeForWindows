package controllers;

import static play.data.Form.*;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.avaje.ebean.Ebean;

import models.DailySalesReconciliation;
import models.Employee;
import models.Role;
import models.SalesHead;
import models.Shift;
import models.Store;
import models.Terminal;
import models.Timesheet;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.timesheets.*;

@Security.Authenticated(Secured.class)
public class Timesheets extends Controller {

	private static final Form<Timesheet> timesheetForm = form(Timesheet.class);

	public static Result GO_HOME = redirect(
	// routes.Timesheets.list()
	routes.Timesheets.list(0, "status", "asc", "", "VIEW"));

	public static Result GO_HOMEForsk = redirect(
	// routes.Timesheets.listForsk()
	routes.Timesheets.listForsk(0, "status", "asc", "", "VIEW"));
	
	/*public static Result GO_HOMEForHeadOffice = redirect(
	// routes.Timesheets.listForHeadOffice()
	routes.Timesheets.listForHeadOffice(0, "status", "asc", "", "VIEW"));
	*/
	

	/***
	 * author Gopi
	 * 
	 */

	public static Result list(int page, String sortBy, String order,
			String filter, String pageAction) {
		Logger.info("@C Timesheets -->> list(" + page + "," + sortBy + ","
				+ order + "," + filter + "," + pageAction + ") -->>");
		flash("pageAction", pageAction);

		flash("action", "timesheet");
		// check for role
		String email = session("email");
		Logger.debug("@C Timesheets -->> list(" + page + "," + sortBy + ","
				+ order + "," + filter + "," + pageAction
				+ ") -->>Employee email: " + email);

		String role = session("role");
		Logger.debug("@C Timesheets -->> list(" + page + "," + sortBy + ","
				+ order + "," + filter + ") -->>Role : " + role);

		Employee emp = null;
		Long empid = 0L;
		Long storeid = 0L;
		if (email != null) {
			emp = Employee.findByEmail(email);
			empid = emp.id;
			storeid = emp.store.id;

		}

		Store store = Store.find.byId(storeid);
		Logger.debug("@C Timesheets -->> list(" + page + "," + sortBy + ","
				+ order + "," + filter + ") -->>Store : " + store.name);
		String colName = null;
		Long value = 0L;

		if (role.contains("admin")) {

			colName = "firmid";
			value = storeid;

		} else if (role.contains("sm")) {
			colName = "firmid";
			value = storeid;

		} else if (role.contains("sk")) {

			colName = "empid";
			value = empid;
		}

		// return list based on filter criteria checking

		if (filter == "") {

			return ok(list.render(Timesheet.page(page, 10, colName, value,
					sortBy, order, filter), sortBy, order, filter, store,
					pageAction));
		} else {

			// calling page() of Timesheet for filter criteria in timesheet list
			// page
			return ok(list.render(
					Timesheet.page(page, 10, colName, value, filter), sortBy,
					order, filter, store, pageAction));
		}

	}
	
	public static Result listForHeadOffice(int page, String sortBy, String order,
			String filter, String pageAction) {
		Logger.info("@C Timesheets -->> listForHeadOffice(" + page + "," + sortBy + ","
				+ order + "," + filter + "," + pageAction + ") -->>");
		flash("pageAction", pageAction);

		flash("action", "Head Officetimesheet");
		// check for role
		String email = session("email");
		Logger.debug("@C Timesheets -->> listForHeadOffice(" + page + "," + sortBy + ","
				+ order + "," + filter + "," + pageAction
				+ ") -->>Employee email: " + email);

		String role = session("role");
		Logger.debug("@C Timesheets -->> listForHeadOffice(" + page + "," + sortBy + ","
				+ order + "," + filter + ") -->>Role : " + role);

		Employee emp = null;
		Long empid = 0L;
		Long storeid = 0L;
		if (email != null) {
			emp = Employee.findByEmail(email);
			empid = emp.id;
			storeid = emp.store.id;

		}

		Store store = Store.find.byId(1L);
		Logger.debug("@C Timesheets -->> list(" + page + "," + sortBy + ","
				+ order + "," + filter + ") -->>Store : " + store.name);
		String colName = null;
		Long value = 0L;

		if (role.contains("admin")) {

			colName = "firmid";
			value = storeid;

		} else if (role.contains("sm")) {
			colName = "firmid";
			value = storeid;

		} else if (role.contains("sa")) {
			colName = "firmid";
			value = storeid;

		} else if (role.contains("")) {
			colName = "firmid";
			value = storeid;

		} else if (role.contains("sk")) {

			colName = "empid";
			value = empid;
		}

		// return list based on filter criteria checking

		if (filter == "") {

			return ok(listForHeadOffice.render(Timesheet.page(page, 10, colName, value,
					sortBy, order, filter), sortBy, order, filter, store,
					pageAction));
		} else {

			// calling page() of Timesheet for filter criteria in timesheet list
			// page
			return ok(listForHeadOffice.render(
					Timesheet.page(page, 10, colName, value, filter), sortBy,
					order, filter, store, pageAction));
		}

	}

	/**
	 * 
	 * @author Gopi
	 * 
	 */

	public static Result showBlank(Long storeId) {
		flash("action", "timesheet");
		Logger.info("@C Timesheets -->> showBlank(" + storeId + ") -->>");
		Store store = Store.find.byId(storeId);
		Logger.debug("@C Timesheets -->> showBlank(" + storeId
				+ ") -->> store : " + store.name);
		Logger.info("@C Timesheets -->> showBlank(" + storeId + ") <<--");
		return ok(show.render(timesheetForm, store));
	}

	/**
	 * @author Gopi
	 * 
	 */

	public static Result save(Long storeId) {
		System.out.println("inside save method===========");
		flash("action", "timesheet");
		Logger.info("@C Timesheets -->> save(" + storeId + ") -->>");
		Store store = Store.find.byId(storeId);

		Long empId = Long.parseLong(form().bindFromRequest().get("empid"));
		String startDate = form().bindFromRequest().get("date");
		String endDate = form().bindFromRequest().get("endDate");
		String leaveType = form().bindFromRequest().get("leaveType");
		String status = form().bindFromRequest().get("status");

		// get store and job title from employee
		Employee employee = Employee.find.byId(empId);
		System.out.println("Selected Employee Id is ========" + employee.id);
		String jobTitle = employee.designation;
		String firmType = "STORE";

		// check if apply leave or not
		if (!leaveType.equals("None")) { // if he selected one leave type

			Timesheet.create(empId, startDate, endDate, leaveType, "0", "0",
					"0", "0", "0:0", jobTitle, firmType, storeId, status);
		} else {
			String startHours = form().bindFromRequest().get("startTimeHour");
			String startMins = form().bindFromRequest().get("startTimeMins");
			String endHours = form().bindFromRequest().get("endTimeHour");
			String endMins = form().bindFromRequest().get("endTimeMins");
			String duration = form().bindFromRequest().get("duration");

			Timesheet.create(empId, startDate, endDate, leaveType, startHours,
					startMins, endHours, endMins, duration, jobTitle, firmType,
					storeId, status);
			System.out.println("employeee saved is=========" + employee);

		}

		flash("success", "Timesheet for Employee " + employee
				+ " has been saved");
		System.out.println("Timesheet saved=========");
		Logger.info("@C Timesheets -->> save(" + storeId + ") <<--");
		return redirect(routes.Timesheets.showBlank(storeId));

	}

	public static Result edit(Long tid, Long sid, String action) {

		flash("pageAction", action);

		Logger.info("@C Timesheets -->> edit(" + tid + "," + sid + ") -->>");
		Store store = Store.find.byId(sid);
		Timesheet timesheet = Timesheet.find.byId(tid);

		Form<Timesheet> timeSheetForm = form(Timesheet.class).fill(timesheet);

		flash("action", "timesheet");
		Logger.info("@C Timesheets -->> edit(" + tid + "," + sid + ") <<--");
		return ok(editForm.render(timeSheetForm, timesheet, store));
	}

	public static Result update(Long tid, Long sid) {

		Logger.info("@C Timesheets -->> update(" + tid + "," + sid + ") -->>");

		Store store = Store.find.byId(sid);
		Timesheet timesheet = Timesheet.find.byId(tid);

		Long empId = Long.parseLong(form().bindFromRequest().get("empid"));
		String startDate = form().bindFromRequest().get("date");
		String endDate = form().bindFromRequest().get("endDate");
		String leaveType = form().bindFromRequest().get("leaveType");
		String status = form().bindFromRequest().get("status");

		// get store and job title from employee
		Employee employee = Employee.find.byId(empId);
		String jobTitle = employee.designation;
		String firmType = "STORE";

		// check if apply leave or not
		if (!leaveType.equals("None")) { // if he selected one leave type

			Timesheet.update(empId, startDate, endDate, leaveType, "0", "0",
					"0", "0", "0:0", jobTitle, firmType, sid, status, tid);
		} else {
			String startHours = form().bindFromRequest().get("startTimeHour");
			String startMins = form().bindFromRequest().get("startTimeMins");
			String endHours = form().bindFromRequest().get("endTimeHour");
			String endMins = form().bindFromRequest().get("endTimeMins");
			String duration = form().bindFromRequest().get("duration");

			Timesheet.update(empId, startDate, endDate, leaveType, startHours,
					startMins, endHours, endMins, duration, jobTitle, firmType,
					sid, status, tid);

		}

		flash("action", "timesheet");
		flash("success", "Timesheet for " + employee + " has been updated");
		Logger.info("@C Timesheets -->> update(" + tid + "," + sid + ") <<--");
		return redirect(routes.Timesheets.edit(tid, sid, "MODIFY"));

	}

	public static Result delete(Long id) {
		Logger.info("@C Timesheets -->> delete(" + id + ") -->>");

		flash("action", "timesheet");

		// flash("success", "Timesheet "+timesheet.date+" has been deleted");
		Ebean.createSqlUpdate("DELETE FROM timesheet WHERE id =:id")
				.setParameter("id", id).execute();

		flash("action", "timesheet");
		Logger.info("@C Timesheets -->> delete(" + id + ") <<--");
		return redirect(routes.Application.showOptions("TIMESHEET", "DELETE"));
	}

	// Timesheets listForsk() method for list of timesheets for role sk

	public static Result listForsk(int page, String sortBy, String order,
			String filter, String pageAction) {

		flash("action", "timesheet_sk");
		flash("pageAction", pageAction);

		String email = session("email");
		String role = session("role");
		Employee emp = null;
		Long empid = 0L;
		Long storeid = 0L;
		if (email != null) {

			emp = Employee.findByEmail(email);
			empid = emp.id;
			storeid = emp.store.id;

		}
		Store store = Store.find.byId(storeid);

		// return list based on filter criteria checking

		if (filter == "") {

			return ok(listForsk.render(Timesheet.page(page, 10, empid, storeid,
					sortBy, order, filter), sortBy, order, filter, store,
					pageAction));
		} else {

			// calling page() of Timesheet for filter criteria in timesheet list
			// page
			return ok(listForsk.render(
					Timesheet.page(page, 10, empid, storeid, filter), sortBy,
					order, filter, store, pageAction));
		}

	}

	// Timesheets showBlankForsk() method for showForsk page ,while adding a new
	// Timesheet for sk

	public static Result showBlankForsk(Long storeId) {
		flash("action", "timesheet_sk");
		Logger.info("@C Timesheets -->> showBlankForsk(" + storeId + ") -->>");
		Store store = Store.find.byId(storeId);
		String email = session("email");
		Employee emp = Employee.findByEmail(email);
		Logger.info("@C Timesheets -->> showBlankForsk(" + storeId + ") <<--");
		return ok(showForsk.render(timesheetForm, store, emp));
	}

	// Timesheets saveForsk() method for saving a new Timesheet for role sk

	public static Result saveForsk(Long storeId) {

		flash("action", "timesheet_sk");
		Logger.info("@C Timesheets -->> saveForsk(" + storeId + ") -->>");
		Store store = Store.find.byId(storeId);

		Long empId = Long.parseLong(form().bindFromRequest().get("empid"));
		String startDate = form().bindFromRequest().get("date");
		String endDate = form().bindFromRequest().get("endDate");
		String leaveType = form().bindFromRequest().get("leaveType");
		String status = form().bindFromRequest().get("status");

		// get store and job title from employee
		Employee employee = Employee.find.byId(empId);
		String jobTitle = employee.designation;
		String firmType = "STORE";

		// check if apply leave or not
		if (!leaveType.equals("None")) { // if he selected one leave type

			Timesheet.create(empId, startDate, endDate, leaveType, "0", "0",
					"0", "0", "0:0", jobTitle, firmType, storeId, status);
		} else {
			String startHours = form().bindFromRequest().get("startTimeHour");
			String startMins = form().bindFromRequest().get("startTimeMins");
			String endHours = form().bindFromRequest().get("endTimeHour");
			String endMins = form().bindFromRequest().get("endTimeMins");
			String duration = form().bindFromRequest().get("duration");

			Timesheet.create(empId, startDate, endDate, leaveType, startHours,
					startMins, endHours, endMins, duration, jobTitle, firmType,
					storeId, status);

		}

		flash("success", "Timesheet for Employee " + employee
				+ " has been saved");

		Logger.info("@C Timesheets -->> saveForsk(" + storeId + ") <<--");
		return redirect(routes.Timesheets.showBlankForsk(storeId));

	}

	// Timesheets editForsk() method for edit a Timesheet for role sk

	public static Result editForsk(Long tid, Long sid, String action) {

		flash("action", "timesheet_sk");
		flash("pageAction", action);

		Logger.info("@C Timesheets -->> editForsk(" + tid + "," + sid
				+ ") -->>");
		Store store = Store.find.byId(sid);
		Timesheet timesheet = Timesheet.find.byId(tid);
		Form<Timesheet> timeSheetForm = form(Timesheet.class).fill(timesheet);
		Employee emp = Employee.find.byId(timesheet.empid);

		Logger.info("@C Timesheets -->> editForsk(" + tid + "," + sid
				+ ") <<--");
		return ok(editFormForsk.render(timeSheetForm, timesheet, store, emp));
	}
	
	public static Result editFormForHeadOffice(Long tid, Long sid, String action) {

		flash("action", "timesheet_sk");
		flash("pageAction", action);

		Logger.info("@C Timesheets -->> editFormForHeadOffice(" + tid + "," + sid
				+ ") -->>");
		Store store = Store.find.byId(sid);
		Timesheet timesheet = Timesheet.find.byId(tid);
		Form<Timesheet> timeSheetForm = form(Timesheet.class).fill(timesheet);
		Employee emp = Employee.find.byId(timesheet.empid);

		Logger.info("@C Timesheets -->> editFormForHeadOffice(" + tid + "," + sid
				+ ") <<--");
		return ok(editFormForHeadOffice.render(timeSheetForm, timesheet, store,emp));
	}

	// Timesheets updateForsk() method for update a Timesheet for role sk

	public static Result updateForsk(Long tid, Long sid) {
		flash("action", "timesheet_sk");
		Logger.info("@C Timesheets -->> updateForsk(" + tid + "," + sid
				+ ") -->>");

		Store store = Store.find.byId(sid);
		Timesheet timesheet = Timesheet.find.byId(tid);

		Long empId = Long.parseLong(form().bindFromRequest().get("empid"));
		String startDate = form().bindFromRequest().get("date");
		String endDate = form().bindFromRequest().get("endDate");
		String leaveType = form().bindFromRequest().get("leaveType");
		String status = form().bindFromRequest().get("status");

		// get store and job title from employee
		Employee employee = Employee.find.byId(empId);
		String jobTitle = employee.designation;
		String firmType = "STORE";

		// check if apply leave or not
		if (!leaveType.equals("None")) { // if he selected one leave type

			Timesheet.update(empId, startDate, endDate, leaveType, "0", "0",
					"0", "0", "0:0", jobTitle, firmType, sid, status, tid);
		} else {
			String startHours = form().bindFromRequest().get("startTimeHour");
			String startMins = form().bindFromRequest().get("startTimeMins");
			String endHours = form().bindFromRequest().get("endTimeHour");
			String endMins = form().bindFromRequest().get("endTimeMins");
			String duration = form().bindFromRequest().get("duration");

			Timesheet.update(empId, startDate, endDate, leaveType, startHours,
					startMins, endHours, endMins, duration, jobTitle, firmType,
					sid, status, tid);

		}

		flash("action", "timesheet");
		flash("success", "Timesheet for " + employee + " has been updated");

		Logger.info("@C Timesheets -->> updateForsk(" + tid + "," + sid
				+ ") <<--");
		return redirect(routes.Timesheets.editForsk(tid, sid, "MODIFY"));

	}

	// showOptions method used in Application.java

	public static Result showOptions(String page, String action) {

		flash("pageAction", action);

		if (action.equals("ADD")) {
			Employee emp = Employee.findByEmail(session("email"));

			return redirect(routes.Timesheets.showBlank(emp.store.id));
		} else if (action.equals("ALL")) {

			return ok(manage1.render(page));
		}

		else {

			return redirect(routes.Timesheets.list(0, "status", "asc", "",
					action));

		}

	}

/*	public static Result showOptionsForHeadOffice(String page, String action) {

		flash("pageAction", action);

		if (action.equals("ADD")) {
			Employee emp = Employee.findByEmail(session("email"));

			return redirect(routes.Timesheets
					.showBlankForHeadOffice(emp.store.id));
		} else if (action.equals("ALL")) {

			return ok(manage1.render(page));
		}

		else {

			return redirect(routes.Timesheets.listForHeadOffice(0, "status", "asc", "",
					action));

		}

	}
*/
	// showOptions method used in Application.java

	public static Result showOptionsForEMP(String page, String action) {

		flash("pageAction", action);

		if (action.equals("ADD")) {
			Employee emp = Employee.findByEmail(session("email"));

			return redirect(routes.Timesheets.showBlankForsk(emp.store.id));
		} else if (action.equals("ALL")) {

			return ok(manage1.render(page));
		}

		else {

			return redirect(routes.Timesheets.listForsk(0, "status", "asc", "",
					action));

		}

	}
	
	// showOptions method used in Application.java for headoffice timesheet
	
	public static Result showOptionsForHeadOffice(String page, String action) {

		flash("pageAction", action);

		if (action.equals("ADD")) {
			Employee emp = Employee.findByEmail(session("email"));

			return redirect(routes.Timesheets.showBlankForHeadOffice(emp.store.id));
		} else if (action.equals("ALL")) {

			return ok(headOfficeManage1.render(page));
		}

		else {

			return redirect(routes.Timesheets.listForHeadOffice(0, "status", "asc", "",
					action));

		}

	}

	// method used to select Page in Admin module to Process Timesheets
	public static Result showSelectPage() {

		return ok(showSelectPage.render());
	}
	
	// method used to select Page in Admin module to Process Head Office Timesheets

	public static Result showSelectPageForHeadOfficeTimeSheet() {
		
		return ok(showSelectPageForHeadOfficeTimeSheet.render());
	}

	// method used to select Page in store module to Process Timesheets
	public static Result showSelectPageForSK(Long sid) {

		return ok(showSelectPageForSK.render(sid));
	}

	// used to get List of Timesheet's Based on the select Criteria in
	// showSelectPage

	public static Result getTimesheets() {

		Long storeId = Long.parseLong(form().bindFromRequest().get("storeId"));
		String weekDate = form().bindFromRequest().get("timesheetWeekDate");
		Date startDate = Application.getDate(weekDate);
		Date endDate = Application.getNextDate(startDate, 6);
		System.out
				.println("end Date: " + endDate + "start Date : " + startDate);
		// List<Timesheet> timesheetList =
		// Timesheet.getTimesheetByStoreDuration(storeId,Application.getDate(startDate),Application.getDate(endDate));
		return ok(timesheetListForAdmin.render(startDate, storeId));
	}
	
	// used to get List of Head Office Timesheet's Based on the select Criteria in showSelectPageForHeadOfficeTimesheet page.
	
	public static Result getHeadOfficeTimesheets() {

		Long empId = Long.parseLong(form().bindFromRequest().get("empId"));
		//String weekDate = form().bindFromRequest().get("timesheetWeekDate");
		String tmsStartDate = form().bindFromRequest().get("timesheetStartDate");
		String tmEndDate = form().bindFromRequest().get("timesheetEndDate");
		Date startDate = Application.getDate(tmsStartDate);
		Date endDate = Application.getDate(tmEndDate);
		Date endDateNext = Application.getNextDate(endDate, 1);
		Date stDate=Application.getDate(tmsStartDate);
	
		System.out
				.println("end Date: " + endDate + "start Date : " + startDate);
		Employee emp=Employee.find.byId(empId);
		
		int days = Days.daysBetween(new DateTime(startDate), new DateTime(endDateNext)).getDays(); // => 34
		
		Logger.info("@C Timesheets -->> getHeadOfficeTimesheets(" + startDate + "+" + endDateNext + "+" + days + ") <<--");

		return ok(timesheetListOfHeadofficeForAdmin.render(startDate,endDate, emp,empId,days-1,stDate,0));
	}

	//  Head office add Timesheet  view method
	public static Result showBlankForHeadOffice(Long storeId) {
		flash("action", "timesheet");
		Logger.info("@C Timesheets -->> showBlank(" + storeId + ") -->>");
		Store store = Store.find.byId(storeId);
		String email = session("email");
		Employee emp = Employee.findByEmail(email);
		Logger.debug("@C Timesheets -->> showBlank(" + storeId
				+ ") -->> store : " + store.name);
		Logger.info("@C Timesheets -->> showBlank(" + storeId + ") <<--");
		return ok(showForHeadOffice.render(timesheetForm, store, emp));
	}

	public static Result saveForHeadOfficeTimeSheet(Long storeId, Long empId) {

		String startDate = form().bindFromRequest().get("date");
		String endDate = form().bindFromRequest().get("endDate");
		String leaveType = form().bindFromRequest().get("leaveType");

		Employee employee = Employee.find.byId(empId);

		String jobTitle = employee.designation;
		String firmType = "HEAD OFFICE";
		storeId = 1L;
		String status = "SUBMITTED";

		Long t = Long.parseLong(form().bindFromRequest().get("rowCountHidden"));
		System.out.println("t value is: " + t);

		if (leaveType.equals("None")) {
			if(t.SIZE>0){

			for (int i = 0; i < t; i++) {
				
				String startTimeHour = i + "_StartTimeHours";
				startTimeHour = form().bindFromRequest().get(startTimeHour);

				String startTimeMins = i + "_StartTimeMins";
				startTimeMins = form().bindFromRequest().get(startTimeMins);

				String endTimeHours = i + "_EndTimeHours";
				endTimeHours = form().bindFromRequest().get(endTimeHours);

				String endTimeMins = i + "_EndTimeMins";
				endTimeMins = form().bindFromRequest().get(endTimeMins);

				String duration = i + "_Duration";
				duration = form().bindFromRequest().get(duration);

				String activity = i + "_Activity";
				activity = form().bindFromRequest().get(activity);

				System.out.println(startTimeHour);
				System.out.println(startTimeMins);
				System.out.println(endTimeHours);
				System.out.println(endTimeMins);
				System.out.println(duration);
				System.out.println(activity);
				if(startTimeHour != null){
				Timesheet tm = Timesheet.savetimeSheet(empId, startDate,
						endDate, leaveType, startTimeHour, startTimeMins,
						endTimeHours, endTimeMins, duration, activity,
						jobTitle, firmType, storeId, status);
				System.out.println("saved timesheet");
				}
			}
		 }
			
		} else {
			Timesheet tm = Timesheet.savetimeSheet(empId, startDate, endDate,
					leaveType, "0", "0", "0", "0", "0:0", null, jobTitle,
					firmType, storeId, status);
		}
		flash("success", "Timesheet for Employee " + employee
				+ " has been saved");

		return redirect(routes.Timesheets.showBlankForHeadOffice(storeId));	}
}
