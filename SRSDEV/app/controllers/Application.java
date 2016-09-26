package controllers;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.nio.file.StandardCopyOption.*;
import java.text.DateFormatSymbols;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData.FilePart;
import play.api.libs.json.JsArray;
import play.api.mvc.MultipartFormData;
import play.data.*;
import scala.util.parsing.json.JSONArray;
import static play.data.Form.*;
import models.*;
import views.html.*;
import views.html.companies.show;

public class Application extends Controller {

	// filed that contains properties file elements
	public static Map<String, String> propertiesMap = new HashMap<String, String>();

	// static block
	static {

		Properties propObj = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("./public/test.properties"); // for play
			 //input = new FileInputStream(System.getenv("CATALINA_HOME")
					 //+"/webapps/SRSFiles/PropFiles/test.properties"); //for tomcat
			 propObj.load(input);

			for (final String name : propObj.stringPropertyNames()) {

				propertiesMap.put(name, propObj.getProperty(name));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// -- Authentication

	public static class Login {

		public String email;
		public String password;

		public String validate() {
			Logger.info("@C Application -->> inside nested Inner class Login -->> validate() -->>");
			if (User.authenticate(email, password) == null) {
				Logger.debug("@C Application -->> inside nested Inner class Login -->> validate() -->> inside if condition");
				return "Invalid user or password";
			}
			Logger.info("@C Application -->> inside nested Inner class Login -->> validate() <<--");

			return null;
		}

	}

	/**
	 * Login page.
	 */
	public static Result login() {
		Logger.info("@C Application -->> login() -->>");
		return ok(login.render(form(Login.class)));
	}

	/**
	 * Handle login form submission.
	 */
	public static Result authenticate() {
		Logger.info("@C Application -->> authenticate() -->>");
		Form<Login> loginForm = form(Login.class).bindFromRequest();
		StringBuilder rString = new StringBuilder();

		if (loginForm.hasErrors()) {
			Logger.error("@C Application -->> authenticate() -->> Loginform has some errors");
			return badRequest(login.render(loginForm));
		} else {
			User user = User.findByEmail(loginForm.get().email);
			if (user != null) {
				session("email", user.email);

				session("name", user.name);
				boolean employeeStatus = setSessionContext();
				if (employeeStatus == false) { // checking weather any Employee
												// is there for this User Email,
												// otherwise Redirect to Login
												// page
					flash("employeeStatus",
							"With  \" "
									+ user.email
									+ " \"  no Employee is present..Make sure User must be an Employee..!");
					return badRequest(login.render(loginForm));
				}
				for (Role each : user.roles) {
					rString.append(each.name).append(" ");
				}
				session("role", rString.toString());
				Logger.debug("@C Application -->> authenticate() -->> session role "
						+ rString.toString());

				return redirect(routes.Application.index());
			}
		}
		Logger.info("@C Application -->> authenticate() <<--");
		return badRequest(login.render(loginForm));
	}

	/*
	 * public static Result authenticate() {
	 * 
	 * Form<Login> loginForm = form(Login.class).bindFromRequest();
	 * 
	 * session("email", "shravanbele@gmail.com");
	 * 
	 * session("name", "shravan");
	 * 
	 * session("role", "sk");
	 * 
	 * 
	 * if(rString.toString().contains("sk")){ return redirect(
	 * routes.Shifts.list(0, "status", "desc", "") ); } return redirect(
	 * routes.DailySalesReconciliations.list(0, "status", "desc", "") ); }
	 * 
	 * 
	 * }
	 */

	// session method

	public static boolean setSessionContext() {
		Logger.info("@C Application -->> setSessionContext() -->>");
		String email = session("email");

		Employee emp = null;
		Long empid = 0L;
		Long storeid = 0L;

		if (email != null) {
			emp = Employee.findByEmail(email);
		}

		// checking weather Any Employee is present with this User email
		if (emp == null) {

			return false; // indicating there is no Employee with this User
							// Email
		} else {
			empid = emp.id;
			session("empid", empid.toString());
			if (emp.store != null) { // check whether employee is mapped for for
										// any store or not
				storeid = emp.store.id;
				session("storeid", storeid.toString());
			}

			return true; // indicate there is an Employee with this Email,
		}
	}

	/**
	 * Logout and clean the session.
	 */
	public static Result logout() {
		Logger.info("@C Application -->> logout() -->>");
		session().clear();
		flash("success", "You've been logged out");
		Logger.info("@C Application -->> logout() <<--");
		return redirect(routes.Application.login());
	}

	/**
	 * Display the dashboard.
	 */

	public static Result index() {
		Logger.info("@C Application -->> index() -->>");

		String email = session("email");
		String role = session("role");
		if (email == null || role == null) {
			return redirect(routes.Application.login());
		} else if (role.contains("admin")) { // render to Admin page
			return ok(home_adm.render());
		} else if (role.contains("ac")) { // render to Accountant page
			return ok(home_ac.render());
		} else if (role.contains("su")) { // render to Super user page
			return ok(home_su.render());
		} else if (role.contains("sa")) { // render to Super Admin page
			return ok(home.render());
		} else if (role.contains("mgb")) { // render to Management page
			return ok(home_management.render());
		} else if (role.contains("HOE")) { // render to Management page
			return ok(home_headOffice.render());
		} else if (role.contains("HeadOffice Accountant")) { // render to
																// Management
																// page
			return ok(home_headOffice_accountant.render());
		}
		
		else { // for role sk and sm
			return ok(home_store.render());
		}

	}

	// show options in home and manage html files
	public static Result showOptions(String page, String action) {
		Logger.info("@C Application -->> showOptions(" + page + "," + action
				+ ") -->>");

		Result result = ok(home.render());
		if (page.equals("STORE")) {

			result = Stores.showOptions(page, action);
		} else if (page.equals("COMPANY")) {

			result = Companies.showOptions(page, action);
		} else if (page.equals("STAFF")) {

			result = Employees.showOptions(page, action);
		} else if (page.equals("USER")) {

			result = Users.showOptions(page, action);
		} else if (page.equals("SALES HEAD")) {

			result = SalesHeads.showOptions(page, action);
		} else if (page.equals("MEDIA TENDER")) {

			result = MediaTenders.showOptions(page, action);
		} else if (page.equals("PAYMENT TENDER")) {
			result = PaymentTenders.showOptions(page, action);
		} else if (page.equals("EXPENSE HEAD")) {

			result = ExpenseHeads.showOptions(page, action);
		} else if (page.equals("TERMINAL HEAD")) {

			result = TerminalHeads.showOptions(page, action);
		} else if (page.equals("SUPPLIER")) {

			result = Suppliers.showOptions(page, action);
		} else if (page.equals("ACCOUNT HOLDER")) {

			result = AccountHolders.showOptions(page, action);
		} else if (page.equals("TIMESHEET")) {

			result = Timesheets.showOptions(page, action);
		} else if (page.equals("ADD TIMESHEET")) {

			result = Timesheets.showOptionsForHeadOffice(page, action);
		} else if (page.equals("EMP TIMESHEET")) {

			result = Timesheets.showOptionsForEMP(page, action);
		} else if (page.equals("SHIFT")) {

			result = Shifts.showOptions(page, action);
		} else if (page.equals("SALES RECONCILIATION")) {

			result = DailySalesReconciliations.showOptions(page, action);
		} else if (page.equals("DAILY RECONCILIATION")) {

			result = DailyReconciliations.showOptions(page, action);
		}
		return result;

	}

	public static Date getNextDate(Date curDate, int numofDays) {
		Logger.info("@C Application -->> getNextDate(Date " + curDate + ","
				+ numofDays + ") -->>");
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {

			calendar.setTime(curDate);
			calendar.add(Calendar.DAY_OF_YEAR, numofDays);
			date = format.parse(format.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.debug("@C Application -->> getNextDate(Date " + curDate + ","
				+ numofDays + ") -->> NextDate " + date);
		Logger.info("@C Application -->> getNextDate(Date " + curDate + ","
				+ numofDays + ") <<--");
		return date;
	}

	public static Date getNextDate(String curDate, int numofDays) {
		Logger.info("@C Application -->> getNextDate(String " + curDate + ","
				+ numofDays + ") -->>");
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		Date date = null;
		if (numofDays > 6) {

		}
		try {

			calendar.setTime(getDate(curDate));
			calendar.add(Calendar.DAY_OF_YEAR, numofDays);
			date = format.parse(format.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.debug("@C Application -->> getNextDate(String " + curDate + ","
				+ numofDays + ") -->> NextDate " + date);
		Logger.info("@C Application -->> getNextDate(String " + curDate + ","
				+ numofDays + ") <<--");

		return date;
	}

	public static Date getDate() {
		Logger.info("@C Application -->> getDate() -->>");
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {

			calendar.setTime(new Date());
			date = format.parse(format.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.debug("@C Application -->> getDate() -->> Date : " + date);
		Logger.info("@C Application -->> getDate() <<--");
		return date;
	}

	public static Date getDate(Date varDate) {
		Logger.info("@C Application -->> getDate(Date " + varDate + ") -->>");
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		Date date = null;
		try {

			calendar.setTime(varDate);
			date = format.parse(format.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.debug("@C Application -->> getDate(Date " + varDate
				+ ") -->> Date : " + date);
		Logger.info("@C Application -->> getDate(Date " + varDate + ") <<--");
		return date;
	}

	public static String getDateString(Date varDate) {
		Logger.info("@C Application -->> getDateString(Date " + varDate
				+ ") -->>");
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		final Calendar calendar = Calendar.getInstance();
		String date = null;
		try {

			calendar.setTime(varDate);
			date = format.format(calendar.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.debug("@C Application -->> getDateString(Date " + varDate
				+ ") -->> DateString :" + date);
		Logger.info("@C Application -->> getDateString(Date " + varDate
				+ ") <<--");
		return date;
	}

	public static Date getDate(String varDate) {
		Logger.info("@C Application -->> getDate(String " + varDate + ") -->>");
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		try {

			date = format.parse(varDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.debug("@C Application -->> getDate(String " + varDate
				+ ") -->> Date : " + date);
		Logger.info("@C Application -->> getDate(String " + varDate + ") <<--");
		return date;
	}

	// get Day from Date
	public static String getDayFromDate(Date date) {

		String day = "";

		if (date != null) {

			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			final Calendar calendar = Calendar.getInstance();
			try {
				calendar.setTime(date);
				day = Application.getDayOfWeek(calendar
						.get(Calendar.DAY_OF_WEEK));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return day;
	}

	// get Day of week

	public static String getDayOfWeek(int value) {
		String day = "";
		switch (value) {
		case 1:
			day = "SUNDAY";
			break;
		case 2:
			day = "MONDAY";
			break;
		case 3:
			day = "TUESDAY";
			break;
		case 4:
			day = "WEDNESDAY";
			break;
		case 5:
			day = "THURSDAY";
			break;
		case 6:
			day = "FRIDAY";
			break;
		case 7:
			day = "SATURDAY";
			break;
		}
		return day;

	}

	public static void upload(Employee emp, play.mvc.Http.MultipartFormData body) {
		Logger.info("@C Application -->> upload(emp,body) -->>");
		String empProfile = null;
		String sid = emp.id.toString();
		// flag is passed to upload() , to diff b/w invoices and employee
		// documents
		String flag = "Employees";
		empProfile = Application.upload(sid, "tfndf", body, flag);
		if (empProfile != null) {
			emp.tfndf = empProfile;
		}
		empProfile = Application.upload(sid, "saf", body, flag);
		if (empProfile != null) {
			emp.saf = empProfile;
		}
		empProfile = Application.upload(sid, "resume", body, flag);
		if (empProfile != null) {
			emp.resume = empProfile;
		}
		empProfile = Application.upload(sid, "payroll", body, flag);
		if (empProfile != null) {
			emp.payroll = empProfile;
		}
		emp.save();
		Logger.info("@C Application -->> upload(emp,body) <<--");
	}

	public static String upload(String id, String fName,
			play.mvc.Http.MultipartFormData body, String flag) {

		Logger.info("@C Application -->> upload(" + id + "," + fName + ","
				+ "body) -->>");
		final String basePath = System.getenv("INVOICE_HOME");

		FilePart upFile = body.getFile(fName);

		// for store name
		Employee emp = Employee.findByEmail(session("email"));

		// for Month as a folder to save invoices

		int monthNum = Calendar.getInstance().getTime().getMonth();
		String monthString = Application.getMonthForInt(monthNum);

		StringBuffer fileNameString = new StringBuffer();

		if (upFile != null) {
			Logger.debug("@C Application -->> upload(" + id + "," + fName + ","
					+ "body) -->> upFile" + upFile.getFilename());

			String fileName = upFile.getFilename();
			String contentType = upFile.getContentType();
			File file = upFile.getFile();

			Logger.debug("@C Application -->> upload(" + id + "," + fName + ","
					+ "body) -->> src fileName" + fileName);

			fileName = StringUtils.substringAfterLast(fileName, ".");
			Logger.debug("@C Application -->> upload(" + id + "," + fName + ","
					+ "body) -->> dest filetype" + fileName);
			Logger.debug("@C Application -->> upload(" + id + "," + fName + ","
					+ "body) -->> contentType" + contentType);
			Logger.debug("@C Application -->> upload(" + id + "," + fName + ","
					+ "body) -->> src file path: "
					+ Play.application().path().getAbsolutePath());

			// File ftemp= new File(Play.application().path().getAbsolutePath()
			// +"//public//"+emp.store.name+"//"+monthString+"//invoices//" );
			File ftemp = null;
			if (flag == "") {
				// System.out.println("Application :"+emp.store.name);
				fileNameString.append("Payouts/" + emp.store.name + "/"
						+ monthString + "/invoices/");
				ftemp = new File(basePath + "Payouts//" + emp.store.name + "/"
						+ monthString + "/invoices/");
				/*
				 * ftemp = new File(System.getenv("CATALINA_HOME") +
				 * "//webapps//SRSFiles//" + emp.store.name + "//" + monthString
				 * + "//invoices//");
				 */

			} else {
				// need to be revised
				fileNameString.append(flag + "/Employee_" + id + "/");
				ftemp = new File(basePath + "Employee Forms//" + flag
						+ "// Employee_" + id + "//");
				/*
				 * ftemp = new File(System.getenv("CATALINA_HOME") +
				 * "//webapps//SRSFiles//" + flag + "// Employee_" + id + "//");
				 */

			}
			ftemp.mkdirs();
			File f1 = new File(ftemp.getAbsolutePath() + "//" + id + "_"
					+ fName + "." + fileName);
			file.setWritable(true);
			file.setReadable(true);

			try {
				Files.copy(file.toPath(), f1.toPath(), REPLACE_EXISTING);
			} catch (Exception e) {
				Logger.error("@C Application -->> upload(" + id + "," + fName
						+ "," + "body) -->>", e);

				e.printStackTrace();
			}
			file.renameTo(f1);
			fileNameString.append(f1.getName());
			Logger.debug("@C Application -->> upload(" + id + "," + fName + ","
					+ "body) -->> dest path: " + f1.getPath());

			flash("success	", "Uploaded file");
		}
		Logger.info("@C Application -->> upload(" + id + "," + fName + ","
				+ "body) <<--");
		return fileNameString.toString();
	}

	// method used to get Month name based on month number
	public static String getMonthForInt(int num) {
		String month = "wrong";
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		if (num >= 0 && num <= 11) {
			month = months[num];
		}
		return month;
	}

	// -- Javascript routing
	public static Result getPayout(String payoutType, Long shiftId, Long suppid) {
		Logger.info("@C Application -->> getPayout(" + payoutType + ","
				+ shiftId + "," + suppid + ") -->> ajax call method");
		Payout payout = Payout.getPayout(payoutType, shiftId, suppid);
		String result = payout.id.toString();

		Logger.debug("@C Application -->> getPayout(" + payoutType + ","
				+ shiftId + "," + suppid + ") -->> payout id result" + result);
		Logger.info("@C Application -->> getPayout(" + payoutType + ","
				+ shiftId + "," + suppid + ") <<--");
		return ok(Json.toJson(result));
	}

	/*
	 * public static Result getAccountSalesHead(Long accId,Long shiftId,Long
	 * salesId,Long mediaId){
	 * //System.out.println("Inside getAccountSalesHead()============");
	 * Logger.info
	 * ("@C Application -->> getAccountSalesHead("+shiftId+","+salesId
	 * +") -->> ajax call method"); String result=null;
	 * 
	 * ShiftAccount shiftAccount =
	 * ShiftAccount.getAccountHolderName(accId,shiftId, salesId,mediaId);
	 * SalesHead salesHeadId = SalesHead.find.byId(salesId); MediaTender
	 * media=MediaTender.find.byId(mediaId); AccountHolder
	 * accName=AccountHolder.find.byId(accId);
	 * 
	 * if(salesHeadId.name.equals("ACCOUNT SALES")){
	 * result=shiftAccount.id+","+accName
	 * .accountHolder+","+salesHeadId.name+","+"-";
	 * //System.out.println("RESULT++++++++++++++"+result); } else{
	 * result=shiftAccount
	 * .id+","+accName.accountHolder+","+salesHeadId.name+","+media.name;
	 * System.out.println("RESULT++++++++++++++"+result); }
	 * 
	 * Logger.debug("@C Application -->> getAccountSalesHead("+shiftId+","+salesId
	 * +") -->> payout id result"+result);
	 * Logger.info("@C Application -->> getAccountSalesHead("
	 * +shiftId+","+salesId+") <<--"); return ok(Json.toJson(result)); }
	 */public static Result deletePayout(Long pId) {
		Logger.info("@C Application -->> deletePayout(" + pId
				+ ") -->> ajax call method");

		Ebean.createSqlUpdate("DELETE FROM payout WHERE id =:id")
				.setParameter("id", pId).execute();
		Logger.info("@C Application -->> deletePayout(" + pId + ") <<-- ");
		return ok(Json.toJson("success"));
	}

	public static Result deletePayroll(Long pId) {

		Logger.info("@C Application -->> deletePayroll(" + pId
				+ ") -->> ajax call method");
		Ebean.createSqlUpdate("DELETE FROM payroll WHERE id =:id")
				.setParameter("id", pId).execute();
		Logger.info("@C Application -->> deletePayroll(" + pId + ") <<--");
		return ok(Json.toJson("success"));
	}

	public static String getTimesheetDetails(Long empId, String weekDate) {

		Logger.info("@C Application -->> getTimesheetDetails(" + empId
				+ ", String " + weekDate + ") -->> ajax call method");

		String duration = Timesheet.getTotalTimesheetHours(empId,
				getDate(weekDate));
		Logger.debug("@C Application -->> getTimesheetDetails(" + empId
				+ ", String " + weekDate + ") -->> Hours : " + duration);
		String salRate = Employee.find.byId(empId).sal;
		Logger.debug("@C Application -->> getTimesheetDetails(" + empId
				+ ", String " + weekDate + ") -->> Sal Rate : " + salRate);
		Double amount = Timesheet.getTotalTimesheetAmount(empId,
				Application.getDate(weekDate));
		Logger.debug("@C Application -->> getTimesheetDetails(" + empId
				+ ", String " + weekDate + ") -->> Amount : " + amount);
		// Double padiAmount=Payroll.getAmountPaidbyWeek(empId,
		// getDate(weekDate),drId);
		String result = null;

		result = duration + ":" + amount.toString();// +":"+padiAmount;
		Logger.info("@C Application -->> getTimesheetDetails(" + empId
				+ ", String " + weekDate + ") <<--");
		return result;
	}

	public static Result addPayroll(Long empId, String weekDate, Long drId) {

		Logger.info("@C Application -->> addPayroll(" + empId + ", String "
				+ weekDate + "," + drId + ") -->> ajax call method");
		String weekStartDate = weekDate.replaceAll("-", "/");
		Logger.debug("@C Application -->> addPayroll(" + empId + ", String "
				+ weekDate + "," + drId + ") -->> weekStartDate : "
				+ weekStartDate);
		Payroll payroll = Payroll.getPayroll(empId, weekStartDate, drId);
		String date = getDateString(Application.getNextDate(weekStartDate, 6));

		Double paidAmt = Payroll.getAmountPaidbyWeek(empId,
				getDate(weekStartDate), drId);
		Logger.debug("@C Application -->> addPayroll(" + empId + ", String "
				+ weekDate + "," + drId + ") -->> paidAmt : " + paidAmt);
		String result = "null";

		if (payroll != null) {
			Employee emp = Employee.find.byId(empId);
			result = payroll.id + ":" + emp.toString() + ":" + date + ":"
					+ emp.sal + ":" + getTimesheetDetails(empId, weekStartDate)
					+ ":" + paidAmt;
			Logger.debug("@C Application -->> addPayroll(" + empId
					+ ", String " + weekDate + "," + drId + ") -->> result : "
					+ result);
		}
		Logger.info("@C Application -->> addPayroll(" + empId + ", String "
				+ weekDate + "," + drId + ") <<--");
		return ok(Json.toJson(result));

	}

	// add supplier Mapping, ajax call method

	public static Result getSupplierMapping(Long storeId, Long suppid) {

		Logger.info("@C Application -->> getSupplierMapping(" + storeId + ","
				+ suppid + ") -->> ajax call method");
		SupplierMapping supplierMapping = SupplierMapping.getSupplierMapping(
				storeId, suppid);

		Supplier supplier = Supplier.find.byId(suppid);
		String result = supplierMapping.id + "-" + supplier.name + "-"
				+ supplier.abn;
		System.out.println("Application : Supplier Name " + supplier.name + ":"
				+ supplierMapping.id);

		Logger.debug("@C Application -->> getSupplierMapping(" + storeId + ","
				+ suppid + ") -->> supplierMapping id result" + result);
		Logger.info("@C Application -->> getSupplierMapping(" + storeId + ","
				+ suppid + ") <<--");
		return ok(Json.toJson(result));
	}

	// deleting supplier mapping ,ajax call
	public static Result deleteSupplierMapping(Long pId) {
		Logger.info("@C Application -->> deleteSupplierMapping(" + pId
				+ ") -->> ajax call method");
		// Payout.delete(pId);
		Ebean.createSqlUpdate("DELETE FROM supplier_mapping WHERE id =:id")
				.setParameter("id", pId).execute();
		Logger.info("@C Application -->> deleteSupplierMapping(" + pId
				+ ") <<-- ");
		return ok(Json.toJson("success"));
	}

	// deleting account salesHead mapping ,ajax call

	// Ajax call method to Check for Duplicate Name/Email
	public static Result checkForUnique(String param, String type, String mode) {

		String result = "";

		if (type.equals("STORE")) { // for store, to check name and email
			if (mode.equals("NAME")) { // check name
				List<Store> storeList = Store.find.all();
				for (Store store : storeList) {
					if (store.name.equalsIgnoreCase(param.trim())) {
						result = "with this name already one Store Exists..,Please Enter different Name";
					}
				}
			} else if (mode.equals("EMAIL")) { // check email
				List<Store> storeList = Store.find.all();
				for (Store store : storeList) {
					if (store.contactInfo.email.equalsIgnoreCase(param.trim())) {
						result = "Already one Store is Registered with this Email..,Please Enter different Email";
					}
				}
			}
		} else if (type.equals("COMPANY")) { // for company, to check name and
												// email
			if (mode.equals("NAME")) { // check name
				List<Company> companyList = Company.find.all();
				for (Company company : companyList) {
					if (company.name.equalsIgnoreCase(param.trim())) {
						result = "with this name already one Company Exists..,Please Enter different Name";
					}
				}
			} else if (mode.equals("EMAIL")) { // check email
				List<Company> companyList = Company.find.all();
				for (Company company : companyList) {
					if (company.contactInfo.email
							.equalsIgnoreCase(param.trim())) {
						result = "Already one Company is Registered with this Email..,Please Enter different Email";
					}
				}
			}
		} else if (type.equals("EMPLOYEE")) { // for employee, to check email
			if (mode.equals("EMAIL")) { // check email
				List<Employee> employeeList = Employee.find.all();
				for (Employee employee : employeeList) {
					if (employee.contactInfo.email.equalsIgnoreCase(param
							.trim())) {
						result = "Already one Employee is Registered with this Email..,Please Enter different Email";
					}
				}
			}
		} else if (type.equals("USER")) { // for user, to check email
			if (mode.equals("EMAIL")) { // check email
				List<User> userList = User.find.all();
				for (User user : userList) {
					if (user.email.equalsIgnoreCase(param.trim())) {
						result = "Already one User is Registered with this Email..,Please Enter different Email";
					}
				}
			}
		} else if (type.equals("SALESHEAD")) { // for saleshead, to check name
			if (mode.equals("NAME")) { // check name
				List<SalesHead> headList = SalesHead.find.all();
				for (SalesHead head : headList) {
					if (head.name.equalsIgnoreCase(param.trim())) {
						result = "Already one SalesHead is Registered with this Name..,Please Enter different Name";
					}
				}
			}
		} else if (type.equals("MEDIATENDER")) { // for MediaTender, to check
													// name
			if (mode.equals("NAME")) { // check name
				List<MediaTender> headList = MediaTender.find.all();
				for (MediaTender head : headList) {
					if (head.name.equalsIgnoreCase(param.trim())) {
						result = "Already one MediaTender is Registered with this Name..,Please Enter different Name";
					}
				}
			}
		} else if (type.equals("PAYMENTTENDER")) { // for PaymentTender, to
													// check name
			if (mode.equals("NAME")) { // check name
				List<PaymentTender> headList = PaymentTender.find.all();
				for (PaymentTender head : headList) {
					if (head.name.equalsIgnoreCase(param.trim())) {
						result = "Already one PaymentTender is Registered with this Name..,Please Enter different Name";
					}
				}
			}
		} else if (type.equals("EXPENSEHEAD")) { // for EXPENSEHEAD, to check
													// name
			if (mode.equals("NAME")) { // check name
				List<ExpenseHead> headList = ExpenseHead.find.all();
				for (ExpenseHead head : headList) {
					if (head.name.equalsIgnoreCase(param.trim())) {
						result = "Already one ExpenseHead is Registered with this Name..,Please Enter different Name";
					}
				}
			}
		} else if (type.equals("TERMINALHEAD")) { // for TERMINALHEAD, to check
													// name
			if (mode.equals("NAME")) { // check name
				List<TerminalHead> headList = TerminalHead.find.all();
				for (TerminalHead head : headList) {
					if (head.name.equalsIgnoreCase(param.trim())) {
						result = "Already one TERMINALHEAD is Registered with this Name..,Please Enter different Name";
					}
				}
			}
		} else if (type.equals("SUPPLIER")) { // for SUPPLIER, to check name
			if (mode.equals("NAME")) { // check name
				List<Supplier> supplierList = Supplier.find.all();
				for (Supplier supplier : supplierList) {
					if (supplier.name.equalsIgnoreCase(param.trim())) {
						result = "Already one SUPPLIER is Registered with this Name..,Please Enter different Name";
					}
				}
			}
		}

		return ok(Json.toJson(result));
	}

	public static Result checkTimesheetExists(Long sId, Long empId,
			String date, String endDate, String leaveType, String startHours,
			String startMins, String endHour, String endMins) {

		Logger.info("@C Application -->> checkTimesheetExists() -->> ");

		int count = 0;

		// form two calendar objects with the new start date and end date
		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		Calendar newStartCal = Calendar.getInstance();
		newStartCal.setTime(newStart);
		newStartCal.set(Calendar.HOUR, Integer.parseInt(startHours));
		newStartCal.set(Calendar.MINUTE, Integer.parseInt(startMins));

		Calendar newEndCal = Calendar.getInstance();
		newEndCal.setTime(newEnd);
		newEndCal.set(Calendar.HOUR, Integer.parseInt(endHour));
		newEndCal.set(Calendar.MINUTE, Integer.parseInt(endMins));

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkTimesheetExists() -->> Last Wednesday...."
				+ lastWednesday);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkTimesheetExists() -->> if wednesday of week is greater than todays date "
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			// get list of existing timesheets in the range of start date and
			// end date
			List<Timesheet> timesheetList = Timesheet.checkTimesheetExists(sId,
					empId, date, endDate, startHours, startMins, endHour,
					endMins);

			for (Timesheet timesheet : timesheetList) {
				if ((timesheet.startTimeHour != null)
						&& (timesheet.endTimeHour != null)) { // check for null

					// form to Calendar objects with retrieved timesheet

					Calendar tempStart = Calendar.getInstance();
					tempStart.setTime(timesheet.date);
					tempStart.set(Calendar.HOUR,
							Integer.parseInt(timesheet.startTimeHour));
					tempStart.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.startTimeMins));

					Calendar tempEnd = Calendar.getInstance();
					tempEnd.setTime(timesheet.endDate);
					tempEnd.set(Calendar.HOUR,
							Integer.parseInt(timesheet.endTimeHour));
					tempEnd.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.endTimeMins));

					// check for leave timesheet is there or not while adding a
					// new
					if (timesheet.leaveType.equals("AL")
							|| timesheet.leaveType.equals("SL")
							|| timesheet.leaveType.equals("PH")) {

						Date timesheetStart = timesheet.date;
						Date timesheetEnd = timesheet.endDate;

						if (newStart.compareTo(timesheetStart) >= 0
								&& newEnd.compareTo(timesheetEnd) <= 0) {

							count = count + 1;

						} else if (newStart.compareTo(timesheetStart) <= 0
								&& newEnd.compareTo(timesheetEnd) >= 0) {

							count = count + 1;

						}
					} else if ((newStartCal.compareTo(tempStart) == 0)
							&& (newEndCal.compareTo(tempEnd) == 0)) { // if the
																		// new
																		// date
																		// is
																		// exactly
																		// same
																		// as
																		// existing
																		// dates
																		// and
																		// times

						count = count + 1;
					} else if ((newStartCal.after(tempStart) && newStartCal
							.before(tempEnd))
							|| (newEndCal.after(tempStart) && newEndCal
									.before(tempEnd))) { // if new dates are in
															// the range of
															// existing dates

						count = count + 1;
					} else if ((newStartCal.compareTo(tempStart) >= 0 && newStartCal
							.compareTo(tempEnd) <= 0)
							|| (newEndCal.compareTo(tempStart) >= 0 && newEndCal
									.compareTo(tempEnd) <= 0)) { // if new dates
																	// are in
																	// the range
																	// of
																	// existing
																	// dates..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					} else if (newStartCal.before(tempStart)
							&& newEndCal.after(tempEnd)) { // start is less than
															// existing start
															// date and greater
															// than existing end
															// date

						count = count + 1;
					} else if (newStartCal.compareTo(tempStart) <= 0
							&& newEndCal.compareTo(tempEnd) >= 0) { // start is
																	// less than
																	// existing
																	// start
																	// date and
																	// greater
																	// than
																	// existing
																	// end
																	// date..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					}
				}

			}

		}

		Logger.info("@C Application -->> checkTimesheetExists() <<--");
		Logger.info("@C Application -->> checkTimesheetExists() <<--" + count);
		return ok(Json.toJson(count));
	}

	public static int checkTimesheetExistsOrNot(Long sId, Long empId,
			String date, String endDate, String leaveType, String startHours,
			String startMins, String endHour, String endMins) {

		Logger.info("@C Application -->> checkTimesheetExists() -->> ");

		int count = 0;

		// form two calendar objects with the new start date and end date
		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		Calendar newStartCal = Calendar.getInstance();
		newStartCal.setTime(newStart);
		newStartCal.set(Calendar.HOUR, Integer.parseInt(startHours));
		newStartCal.set(Calendar.MINUTE, Integer.parseInt(startMins));

		Calendar newEndCal = Calendar.getInstance();
		newEndCal.setTime(newEnd);
		newEndCal.set(Calendar.HOUR, Integer.parseInt(endHour));
		newEndCal.set(Calendar.MINUTE, Integer.parseInt(endMins));

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkTimesheetExists() -->> Last Wednesday...."
				+ lastWednesday);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkTimesheetExists() -->> if wednesday of week is greater than todays date "
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			// get list of existing timesheets in the range of start date and
			// end date
			List<Timesheet> timesheetList = Timesheet.checkTimesheetExists(sId,
					empId, date, endDate, startHours, startMins, endHour,
					endMins);

			for (Timesheet timesheet : timesheetList) {
				if ((timesheet.startTimeHour != null)
						&& (timesheet.endTimeHour != null)) { // check for null

					// form to Calendar objects with retrieved timesheet

					Calendar tempStart = Calendar.getInstance();
					tempStart.setTime(timesheet.date);
					tempStart.set(Calendar.HOUR,
							Integer.parseInt(timesheet.startTimeHour));
					tempStart.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.startTimeMins));

					Calendar tempEnd = Calendar.getInstance();
					tempEnd.setTime(timesheet.endDate);
					tempEnd.set(Calendar.HOUR,
							Integer.parseInt(timesheet.endTimeHour));
					tempEnd.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.endTimeMins));

					// check for leave timesheet is there or not while adding a
					// new
					if (timesheet.leaveType.equals("AL")
							|| timesheet.leaveType.equals("SL")
							|| timesheet.leaveType.equals("PH")) {

						Date timesheetStart = timesheet.date;
						Date timesheetEnd = timesheet.endDate;

						if (newStart.compareTo(timesheetStart) >= 0
								&& newEnd.compareTo(timesheetEnd) <= 0) {

							count = count + 1;

						} else if (newStart.compareTo(timesheetStart) <= 0
								&& newEnd.compareTo(timesheetEnd) >= 0) {

							count = count + 1;

						}
					} else if ((newStartCal.compareTo(tempStart) == 0)
							&& (newEndCal.compareTo(tempEnd) == 0)) { // if the
																		// new
																		// date
																		// is
																		// exactly
																		// same
																		// as
																		// existing
																		// dates
																		// and
																		// times

						count = count + 1;
					} else if ((newStartCal.after(tempStart) && newStartCal
							.before(tempEnd))
							|| (newEndCal.after(tempStart) && newEndCal
									.before(tempEnd))) { // if new dates are in
															// the range of
															// existing dates

						count = count + 1;
					} else if ((newStartCal.compareTo(tempStart) >= 0 && newStartCal
							.compareTo(tempEnd) <= 0)
							|| (newEndCal.compareTo(tempStart) >= 0 && newEndCal
									.compareTo(tempEnd) <= 0)) { // if new dates
																	// are in
																	// the range
																	// of
																	// existing
																	// dates..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					} else if (newStartCal.before(tempStart)
							&& newEndCal.after(tempEnd)) { // start is less than
															// existing start
															// date and greater
															// than existing end
															// date

						count = count + 1;
					} else if (newStartCal.compareTo(tempStart) <= 0
							&& newEndCal.compareTo(tempEnd) >= 0) { // start is
																	// less than
																	// existing
																	// start
																	// date and
																	// greater
																	// than
																	// existing
																	// end
																	// date..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					}
				}

			}

		}

		Logger.info("@C Application -->> checkTimesheetExists() <<--" + count);
		System.out.println("count is" + count);
		return count;
	}

	// check for Existing Timesheet while adding a new Timesheet for leave
	public static Result checkTimesheetExistsForLeave(Long sId, Long empId,
			String date, String endDate) {

		Logger.info("@C Application -->> checkTimesheetExistsForLeave() -->> ");
		int count = 0;

		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkTimesheetExistsForLeave() -->> Last Wednesday...."
				+ lastWednesday);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkTimesheetExistsForLeave() -->> if wednesday of week is greater than todays date"
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			List<Timesheet> timesheetList = Timesheet
					.checkTimesheetExistsForLeave(sId, empId, date, endDate);

			for (Timesheet timesheet : timesheetList) {

				Date timesheetStart = timesheet.date;
				Date timesheetEnd = timesheet.endDate;

				if (newStart.compareTo(timesheetStart) >= 0
						&& newEnd.compareTo(timesheetEnd) <= 0) {

					count = count + 1;

				} else if (newStart.compareTo(timesheetStart) <= 0
						&& newEnd.compareTo(timesheetEnd) >= 0) {

					count = count + 1;

				}
			}

		}

		Logger.info("@C Application -->> checkTimesheetExistsForLeave() <<--");
		return ok(Json.toJson(count));
	}

	// check for Existing Head Office Timesheet while adding a new Timesheet for
	// leave
	public static Result checkHeadOfficeTimesheetExistsForLeave(Long sId,
			Long empId, String date, String endDate) {

		Logger.info("@C Application -->> checkTimesheetExistsForLeave() -->> ");
		int count = 0;

		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkHeadOfficeTimesheetExistsForLeave() -->> Last Wednesday...."
				+ lastWednesday);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkHeadOfficeTimesheetExistsForLeave() -->> if wednesday of week is greater than todays date"
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			List<Timesheet> timesheetList = Timesheet
					.checkHeadOfficeTimesheetExistsForLeave(sId, empId, date,
							endDate);

			for (Timesheet timesheet : timesheetList) {

				Date timesheetStart = timesheet.date;
				Date timesheetEnd = timesheet.endDate;

				if (newStart.compareTo(timesheetStart) >= 0
						&& newEnd.compareTo(timesheetEnd) <= 0) {

					count = count + 1;

				} else if (newStart.compareTo(timesheetStart) <= 0
						&& newEnd.compareTo(timesheetEnd) >= 0) {

					count = count + 1;

				}
			}

		}

		Logger.info("@C Application -->> checkHeadOfficeTimesheetExistsForLeave() <<--");
		Logger.info("@C Application -->> checkHeadOfficeTimesheetExistsForLeave() <<--"
				+ count);

		return ok(Json.toJson(count));
	}

	// check duplicate timesheets in edit page submission
	public static Result checkTimesheetExistsEditPage(Long sId, Long empId,
			String date, String endDate, String leaveType, String startHours,
			String startMins, String endHour, String endMins, Long tid) {

		Logger.info("@C Application -->> checkTimesheetExistsEditPage() -->> ");

		int count = 0;

		// form two calendar objects with the new start date and end date
		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		Calendar newStartCal = Calendar.getInstance();
		newStartCal.setTime(newStart);
		newStartCal.set(Calendar.HOUR, Integer.parseInt(startHours));
		newStartCal.set(Calendar.MINUTE, Integer.parseInt(startMins));

		Calendar newEndCal = Calendar.getInstance();
		newEndCal.setTime(newEnd);
		newEndCal.set(Calendar.HOUR, Integer.parseInt(endHour));
		newEndCal.set(Calendar.MINUTE, Integer.parseInt(endMins));

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkTimesheetExistsEditPage() -->> Last Wednesday...."
				+ lastWednesday);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkTimesheetExistsEditPage() -->> if wednesday of week is greater than todays date"
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			// get list of existing timesheets in the range of start date and
			// end date
			List<Timesheet> timesheetList = Timesheet.checkTimesheetExists(sId,
					empId, date, endDate, startHours, startMins, endHour,
					endMins);

			for (Timesheet timesheet : timesheetList) {

				if ((timesheet.startTimeHour != null)
						&& (timesheet.endTimeHour != null)
						&& !(timesheet.id.equals(tid))) { // check for null
					// form to Calendar objects with retrieved timesheet

					Calendar tempStart = Calendar.getInstance();
					tempStart.setTime(timesheet.date);
					tempStart.set(Calendar.HOUR,
							Integer.parseInt(timesheet.startTimeHour));
					tempStart.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.startTimeMins));

					Calendar tempEnd = Calendar.getInstance();
					tempEnd.setTime(timesheet.endDate);
					tempEnd.set(Calendar.HOUR,
							Integer.parseInt(timesheet.endTimeHour));
					tempEnd.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.endTimeMins));

					// check for leave timesheet is there or not while adding a
					// new
					if (timesheet.leaveType.equals("AL")
							|| timesheet.leaveType.equals("SL")
							|| timesheet.leaveType.equals("PH")) {

						Date timesheetStart = timesheet.date;
						Date timesheetEnd = timesheet.endDate;

						if (newStart.compareTo(timesheetStart) >= 0
								&& newEnd.compareTo(timesheetEnd) <= 0) {

							count = count + 1;

						} else if (newStart.compareTo(timesheetStart) <= 0
								&& newEnd.compareTo(timesheetEnd) >= 0) {

							count = count + 1;

						}
					} else if ((newStartCal.compareTo(tempStart) == 0)
							&& (newEndCal.compareTo(tempEnd) == 0)) { // if the
																		// new
																		// date
																		// is
																		// exactly
																		// same
																		// as
																		// existing
																		// dates
																		// and
																		// times

						count = count + 1;
					} else if ((newStartCal.after(tempStart) && newStartCal
							.before(tempEnd))
							|| (newEndCal.after(tempStart) && newEndCal
									.before(tempEnd))) { // if new dates are in
															// the range of
															// existing dates

						count = count + 1;
					} else if ((newStartCal.compareTo(tempStart) >= 0 && newStartCal
							.compareTo(tempEnd) <= 0)
							|| (newEndCal.compareTo(tempStart) >= 0 && newEndCal
									.compareTo(tempEnd) <= 0)) { // if new dates
																	// are in
																	// the range
																	// of
																	// existing
																	// dates..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					} else if (newStartCal.before(tempStart)
							&& newEndCal.after(tempEnd)) { // start is less than
															// existing start
															// date and greater
															// than existing end
															// date

						count = count + 1;
					} else if (newStartCal.compareTo(tempStart) <= 0
							&& newEndCal.compareTo(tempEnd) >= 0) { // start is
																	// less than
																	// existing
																	// start
																	// date and
																	// greater
																	// than
																	// existing
																	// end
																	// date..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					}
				}

			}

		}

		Logger.info("@C Application -->> checkTimesheetExistsEditPage() <<--");
		return ok(Json.toJson(count));
	}

	public static Result checkHeadOfficeTimesheetExists(Long sId, Long empId,
			String date, String endDate, String leaveType, String startHours,
			String startMins, String endHour, String endMins) {

		Logger.info("@C Application -->> checkTimesheetExists() -->> ");

		int count = 0;

		// form two calendar objects with the new start date and end date
		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		Calendar newStartCal = Calendar.getInstance();
		newStartCal.setTime(newStart);
		newStartCal.set(Calendar.HOUR, Integer.parseInt(startHours));
		newStartCal.set(Calendar.MINUTE, Integer.parseInt(startMins));

		Calendar newEndCal = Calendar.getInstance();
		newEndCal.setTime(newEnd);
		newEndCal.set(Calendar.HOUR, Integer.parseInt(endHour));
		newEndCal.set(Calendar.MINUTE, Integer.parseInt(endMins));

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkTimesheetExists() -->> Last Wednesday...."
				+ lastWednesday);
		Logger.info("store id inside checkHeadOfficeTimeSheetExistOrNot" + sId);
		Logger.info("employee id inside checkHeadOfficeTimeSheetExistOrNot"
				+ empId);
		Logger.info("start date inside checkHeadOfficeTimeSheetExistOrNot"
				+ date);
		Logger.info("endDate inside checkHeadOfficeTimeSheetExistOrNot"
				+ endDate);
		Logger.info("leaveType inside checkHeadOfficeTimeSheetExistOrNot"
				+ leaveType);
		Logger.info("startHours inside checkHeadOfficeTimeSheetExistOrNot"
				+ startHours);
		Logger.info("startMins inside checkHeadOfficeTimeSheetExistOrNot"
				+ startMins);
		Logger.info("endHours inside checkHeadOfficeTimeSheetExistOrNot"
				+ endHour);
		Logger.info("endMins inside checkHeadOfficeTimeSheetExistOrNot"
				+ endMins);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkTimesheetExists() -->> if wednesday of week is greater than todays date "
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			// get list of existing timesheets in the range of start date and
			// end date
			List<Timesheet> timesheetList = Timesheet
					.checkHeadOfficeTimesheetExists(sId, empId, date, endDate,
							startHours, startMins, endHour, endMins);

			for (Timesheet timesheet : timesheetList) {
				if ((timesheet.startTimeHour != null)
						&& (timesheet.endTimeHour != null)) { // check for null

					// form to Calendar objects with retrieved timesheet

					Calendar tempStart = Calendar.getInstance();
					tempStart.setTime(timesheet.date);
					tempStart.set(Calendar.HOUR,
							Integer.parseInt(timesheet.startTimeHour));
					tempStart.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.startTimeMins));

					Calendar tempEnd = Calendar.getInstance();
					tempEnd.setTime(timesheet.endDate);
					tempEnd.set(Calendar.HOUR,
							Integer.parseInt(timesheet.endTimeHour));
					tempEnd.set(Calendar.MINUTE,
							Integer.parseInt(timesheet.endTimeMins));

					// check for leave timesheet is there or not while adding a
					// new
					if (timesheet.leaveType.equals("AL")
							|| timesheet.leaveType.equals("SL")
							|| timesheet.leaveType.equals("PH")) {

						Date timesheetStart = timesheet.date;
						Date timesheetEnd = timesheet.endDate;

						if (newStart.compareTo(timesheetStart) >= 0
								&& newEnd.compareTo(timesheetEnd) <= 0) {

							count = count + 1;

						} else if (newStart.compareTo(timesheetStart) <= 0
								&& newEnd.compareTo(timesheetEnd) >= 0) {

							count = count + 1;

						}
					} else if ((newStartCal.compareTo(tempStart) == 0)
							&& (newEndCal.compareTo(tempEnd) == 0)) { // if the
																		// new
																		// date
																		// is
																		// exactly
																		// same
																		// as
																		// existing
																		// dates
																		// and
																		// times

						count = count + 1;
					} else if ((newStartCal.after(tempStart) && newStartCal
							.before(tempEnd))
							|| (newEndCal.after(tempStart) && newEndCal
									.before(tempEnd))) { // if new dates are in
															// the range of
															// existing dates

						count = count + 1;
					} else if ((newStartCal.compareTo(tempStart) > 0 && newStartCal
							.compareTo(tempEnd) < 0)
							|| (newEndCal.compareTo(tempStart) > 0 && newEndCal
									.compareTo(tempEnd) < 0)) { // if new dates
																// are in
																// the range
																// of
																// existing
																// dates..,
																// and check
																// equal
																// condition
																// also
																// also
						count = count + 1;
					} else if (newStartCal.before(tempStart)
							&& newEndCal.after(tempEnd)) { // start is less than
															// existing start
															// date and greater
															// than existing end
															// date

						count = count + 1;
					} else if (newStartCal.compareTo(tempStart) <= 0
							&& newEndCal.compareTo(tempEnd) >= 0) { // start is
																	// less than
																	// existing
																	// start
																	// date and
																	// greater
																	// than
																	// existing
																	// end
																	// date..,
																	// and check
																	// equal
																	// condition
																	// also

						count = count + 1;
					}
				}

			}

		}

		Logger.info("@C Application -->> checkTimesheetExists() <<--");
		Logger.info("@C Application -->> checkTimesheetExists() <<--" + count);
		return ok(Json.toJson(count));
	}

	// check for Existing Timesheet while adding a new Timesheet for leave
	public static Result checkTimesheetExistsForLeaveEditPage(Long sId,
			Long empId, String date, String endDate, Long tid) {

		Logger.info("@C Application -->> checkTimesheetExistsForLeaveEditPage() -->> ");
		int count = 0;

		Date newStart = Application.getDate(date);
		Date newEnd = Application.getDate(endDate);

		// to get wednesday date of a week from today date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -(cal.get(Calendar.DAY_OF_WEEK) - 4));
		Date lastWednesday = Application.getDate(cal.getTime());

		Logger.debug("@C Application -->> checkTimesheetExistsForLeaveEditPage() -->> Last Wednesday...."
				+ lastWednesday);

		if (lastWednesday.compareTo(Application.getDate(new Date())) >= 0) {

			lastWednesday = Application.getNextDate(lastWednesday, -7);
			Logger.debug("@C Application -->> checkTimesheetExistsForLeaveEditPage() -->> if wednesday of week is greater than todays date"
					+ lastWednesday);
		}

		// if user tries to add a timesheet of last previous processed week ,
		// Then give alert
		if (newStart.compareTo(lastWednesday) <= 0
				|| newEnd.compareTo(lastWednesday) <= 0) {

			count = -1;
		} else {

			List<Timesheet> timesheetList = Timesheet
					.checkTimesheetExistsForLeave(sId, empId, date, endDate);

			for (Timesheet timesheet : timesheetList) {

				if (!timesheet.id.equals(tid)) { // for edit page checking

					Date timesheetStart = timesheet.date;
					Date timesheetEnd = timesheet.endDate;

					if (newStart.compareTo(timesheetStart) >= 0
							&& newEnd.compareTo(timesheetEnd) <= 0) {

						count = count + 1;

					} else if (newStart.compareTo(timesheetStart) <= 0
							&& newEnd.compareTo(timesheetEnd) >= 0) {

						count = count + 1;

					}
				}

			}

		}

		Logger.info("@C Application -->> checkTimesheetExistsForLeaveEditPage() <<--");
		return ok(Json.toJson(count));
	}

	// to populate stores based on company selected in Invoice payment page

	public static Result storesByCompany(Long companyId) {

		Company company = Company.find.byId(companyId);
		List<Store> storeListByCompany = Store.find.where()
				.eq("company.id", company.id).eq("status", "ACTIVE").findList();
		String[] ids = new String[storeListByCompany.size()];
		String[] names = new String[storeListByCompany.size()];
		for (int i = 0; i < storeListByCompany.size(); i++) {
			ids[i] = storeListByCompany.get(i).id.toString();
			names[i] = storeListByCompany.get(i).name;
		}
		String[][] total = { ids, names };
		return ok(Json.toJson(total));
	}

	/**
	 * Method used to append an Id and Date at last of existing file name
	 */

	public static String getModifiedFileName(String fileNameWithExtension,
			Long id) {

		Logger.info("@C Application -->> getModifiedFileName("
				+ fileNameWithExtension + "," + id + ") -->> ");
		// form the destination file name having some id
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HHmmss");
		String strDate = sdf.format(Calendar.getInstance().getTime());

		String modifiedFileName;
		String beforeDot = StringUtils.substringBeforeLast(
				fileNameWithExtension, ".");
		String afterDot = StringUtils.substringAfterLast(fileNameWithExtension,
				".");
		modifiedFileName = beforeDot + "_" + id + "_" + strDate + "."
				+ afterDot;
		Logger.info("@C Application -->> getModifiedFileName("
				+ fileNameWithExtension + "," + id + ") <<--");
		return modifiedFileName;
	}

	/**
	 * Method used to append an Id and and store id at last of existing file
	 * name
	 */
	public static String getModifiedFileName(String fileNameWithExtension,
			Long sid, Long id) {

		Logger.info("@C Application -->> getModifiedFileName("
				+ fileNameWithExtension + "," + sid + "," + id + ") -->> ");
		String modifiedFileName;
		String beforeDot = StringUtils.substringBeforeLast(
				fileNameWithExtension, ".");
		String afterDot = StringUtils.substringAfterLast(fileNameWithExtension,
				".");

		// checking whether user upload a file with extension or not
		if (afterDot.equals("")) {
			modifiedFileName = beforeDot + "_" + sid + "_" + id;
		} else {
			modifiedFileName = beforeDot + "_" + sid + "_" + id + "."
					+ afterDot;
		}

		Logger.info("@C Application -->> getModifiedFileName("
				+ fileNameWithExtension + "," + sid + "," + id + ") <<--");
		return modifiedFileName;
	}

	/**
	 * Verify password , while changing password
	 * 
	 * @return JSON string SUCCESS to represent verified and FALIURE to
	 *         represent NOt Verified
	 */
	public static Result verifyCurrentPassword(Long empId, String currPwd) {

		Logger.info("@C Application -->> verifyCurrentPassword(" + empId
				+ ") -->> ");

		String result = "FAILURE";
		if (empId != null) {
			Employee employee = Employee.find.byId(empId);
			if (employee != null) {
				User user = User.findByEmail(employee.contactInfo.email);
				Logger.debug("@C Application -->> verifyCurrentPassword("
						+ empId + ") -->> User :" + user.email + " : "
						+ user.id);
				if (user != null && currPwd != null) {
					if (user.password.trim().equals(currPwd.trim())) {
						result = "SUCCESS";
					}
				}
			}
		}
		Logger.info("@C Application -->> verifyCurrentPassword(" + empId
				+ ") <<-- Returning : " + result);

		return ok(Json.toJson(result));
	}

	public static Result getTimeSheetSave(Long empId, String date,
			String startTimeHour, String startTimeMin, String endTimeHour,
			String endTimeMin, String duration, String textarea,
			String leaveType) {

		Timesheet tm = null;
		Employee employee = Employee.find.byId(empId);
		System.out.println("Selected Employee Id is ========" + employee.id);
		String jobTitle = employee.designation;
		String firmType = "HEAD OFFICE";
		Long storeId = 1L;
		String status = "SUBMITTED";

		if (leaveType.trim().equals("None")) {
			tm = Timesheet.addTimeSheet(empId, date, date, leaveType,
					startTimeHour, startTimeMin, endTimeHour, endTimeMin,
					duration, textarea, jobTitle, firmType, storeId, status);
		} else {
			tm = Timesheet.addTimeSheet(empId, date, date, leaveType, "0", "0",
					"0", "0", "0:0", textarea, jobTitle, firmType, storeId,
					status);

		}
		return ok(Json.toJson(tm));
	}

	public static Result javascriptRoutes() {
		response().setContentType("text/javascript");
		Logger.info("@C Application -->> javascriptRoutes() -->> javascriptRoutes");
		return ok(Routes.javascriptRouter("myJsRoutes",
				routes.javascript.Suppliers.getSuppliers(),
				routes.javascript.Application.getPayout(),
				routes.javascript.Application.deletePayout(),
				routes.javascript.Application.addPayroll(),
				routes.javascript.Application.deletePayroll(),
				routes.javascript.Application.getTimeSheetSave(),
				routes.javascript.Application.getSupplierMapping(),
				routes.javascript.Application.deleteSupplierMapping(),
				routes.javascript.Reports.getReport(),
				routes.javascript.Application.checkForUnique(),
				routes.javascript.Application.checkTimesheetExists(),
				routes.javascript.Application.checkHeadOfficeTimesheetExists(),
				routes.javascript.Application.checkTimesheetExistsForLeave(),
				routes.javascript.Application
						.checkHeadOfficeTimesheetExistsForLeave(),
				routes.javascript.Application.checkTimesheetExistsEditPage(),
				routes.javascript.Application
						.checkTimesheetExistsForLeaveEditPage(),
				routes.javascript.Application.storesByCompany(),
				routes.javascript.Application.verifyCurrentPassword()));
	}

}
