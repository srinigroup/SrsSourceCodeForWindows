package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import play.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.h2.index.PageIndex;
import org.springframework.beans.support.PagedListHolder;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.Application;
import controllers.PageAccess;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.mvc.BodyParser;

@Entity
public class Employee extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	@Constraints.Required
	public String firstName;

	@Constraints.Required
	public String lastName;

	public String shortName;

	@Constraints.Required
	public String gender;

	// add email id for an employee
	/*
	 * public String email;
	 */
	@Constraints.Required
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date dob;

	public String emergencyContactName;
	public String emergencyRelation;
	public String emergencyPhone;
	public String emergencyEmail;

	@ManyToOne
	public Company company;

	@ManyToOne
	public Store store;

	// Employement Commence Date
	@Constraints.Required
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date ecd;

	// Employment Status
	public String empStatus;

	// Approved Hourly Rate/Annual Salary
	@Constraints.Required
	public String sal;

	// Position with Company/Store
	public String designation;

	// Applying for Tax Free Threshold
	public String isTaxFree;

	public String tfndf;
	public String saf;
	public String resume;
	public String payroll;

	// to hold employee status.By default it is ACTIVE .if we delete any
	// employee then the status will be DELETED
	public String status;

	@OneToOne
	public Address address;

	@OneToOne
	public ContactInfo contactInfo;

	@OneToOne
	public BankAccountInfo bankAccountInfo;

	/**
	 * Generic query helper for entity Company with id Long
	 */
	public static Finder<Long, Employee> find = new Finder<Long, Employee>(
			Long.class, Employee.class);

	public static List<Employee> all() {
		Logger.info("@M Employee -->> all() -->> ");
		return find.all();
	}

	@BodyParser.Of(value = BodyParser.Text.class, maxLength = 10 * 1024)
	public static void create(Employee employee) {
		Logger.info("@M Employee -->> create(employee) -->> ");
		employee.save();
		employee.status = "ACTIVE";// by default employee status is ACTIVE
		employee.save();
		Logger.info("@M Employee -->> create(employee) <<--");

	}

	/**
	 * Retrieve a User from email.
	 */
	public static Employee findByEmail(String email) {
		Logger.info("@M Employee -->> findByEmail(" + email + ") -->> ");
		return find.where().eq("contactInfo.email", email).findUnique();
	}

	public static void delete(Long id) {
		Logger.info("@M Employee -->> delete(" + id + ") -->> ");

		// find employee and make status as DELETED
		Employee employee = Employee.find.byId(id);

		// delete user(account) record if this employee had a user credentials
		User user = User.findByEmail(employee.contactInfo.email);
		if (user != null) {
			user.delete();
		}

		employee.status = "DELETED";
		employee.update();

		Logger.info("@M Employee -->> delete(" + id + ") <<--");
	}

	public static Map<String, String> options(Long storeid) {
		Logger.info("@M Employee -->> options(" + storeid + ") -->> ");
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (Employee c : Employee.find.where().eq("store.id", storeid)
				.eq("status", "ACTIVE").orderBy("first_name").findList()) {
			options.put(c.id.toString(), c.firstName + " " + c.lastName);
		}
		Logger.info("@M Employee -->> options(" + storeid + ") <<--");
		return options;
	}

	/**
	 * Return a page of computer
	 *
	 * @param page
	 *            Page to display
	 * @param pageSize
	 *            Number of computers per page
	 * @param sortBy
	 *            Product property used for sorting
	 * @param order
	 *            Sort order (either or asc or desc)
	 * @param filter
	 *            Filter applied on the name column
	 */
	public static Page<Employee> page(int page, int pageSize, String sortBy,
			String order, String filter) {
		Logger.info("@M Employee -->> page(" + page + "," + pageSize + ","
				+ sortBy + "," + order + "," + filter + ") -->> ");
		return (find.where().eq("status", "ACTIVE")
				.ilike("first_name", filter + "%")
				.orderBy(sortBy + " " + order).findPagingList(pageSize))
				.setFetchAhead(false).getPage(page);
	}

	public static List<Employee> getEmployeesbyStore(Long storeid) {
		Logger.info("@M Employee -->> getEmployeesbyStore(" + storeid
				+ ") -->> ");
		return (find.where().eq("store.id", storeid).eq("status", "ACTIVE")
				.orderBy("first_name").findList());
	}

	public static String getEmployee(Long empId) {
		Logger.info("@M Employee -->> getEmployee(" + empId + ") -->> ");
		Employee employee = Employee.find.byId(empId);
		Logger.info("@M Employee -->> getEmployee(" + empId + ") <<--");

		if (employee != null) {

			return employee.toString();
		} else {
			return "";
		}

	}

	public String toString() {
		return firstName + "." + lastName;
	}

	public static List<Employee> getHeadOfficeEmployee(Long storeId) {
		List<Timesheet> timeSheetList = new ArrayList<Timesheet>();
		List<Employee> empList = new ArrayList<Employee>();
		Set<Long> empId = new TreeSet<Long>();
		Employee employee = new Employee();

		timeSheetList = Timesheet.getHeadOfficeEmployeeTimeSheet(storeId);

		for (Timesheet tm : timeSheetList) {
			empId.add(tm.empid);
		}

		for (Long eId : empId) {
			employee = Employee.find.byId(eId);
			empList.add(employee);
		}
		return empList;
	}

	public static List<Employee> page1(int page, int pageSize, String sortBy,
			String order, String storeId) {
		Logger.info("@M Employee -->> page(" + page + "," + pageSize + ","
				+ sortBy + "," + order + "," + storeId + ") -->> ");
		List<Employee> ids = new ArrayList<Employee>();

		ids = find.where().eq("status", "ACTIVE").eq("store_id", storeId).orderBy("first_name" + " " + "asc")
				.findList();

		System.out.println("ID'S OF STORES" + ids.size());
		//List l = Employee.page2(page, 10, sortBy, order, storeId, sd);
		//System.out.println("!!!!!!!" + l);

		return ids;

	}
	/*
	 * public static List page2(int page, int pageSize, String sortBy, String
	 * order, String filter, List<Object> sd) { List ids = new ArrayList(); List
	 * allIds = new ArrayList(); for (int i = 0; i < sd.size(); i++) {
	 * 
	 * 
	 * ids = find.where().eq("status", "ACTIVE").eq("store_id", sd.get(i))
	 * .findList();
	 * 
	 * System.out.println("lisz size in page2 :" + ids.size());
	 * System.out.println("lisz size in page2 :" +
	 * Arrays.toString(ids.toArray()));
	 * 
	 * System.out.println("allIds in for :" + allIds.size());
	 * allIds.addAll(ids); } System.out.println("idssize in page2 :" +
	 * ids.size()); System.out.println("All ids size in page2 :" +
	 * allIds.size());
	 * 
	 * return allIds;
	 * 
	 * }
	 */
}
