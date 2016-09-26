package controllers;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static play.data.Form.form;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import models.Company;
import models.Employee;
import models.Invoice;
import models.Store;
import models.Supplier;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.invoiceInventory.*;

@Security.Authenticated(Secured.class)
public class InvoiceInventory extends Controller {

	// Starting Folder or Root Folder path, instead of using entire path every
	// where use this variable

	// private static String basePath =
	// Play.application().path().getAbsolutePath(); // for play
	// private static String basePath = System.getenv("CATALINA_HOME")
	// +"//webapps//SRSFiles"; // for tomcat ,within webapps
	private static String basePath = System.getenv("INVOICE_HOME"); // for
																	// tomcat
																	// other
																	// than
																	// webapps
																	// folder,D
																	// Drive

	// show upload page
	public static Result showUploadPage() {

		Logger.info("@C InvoiceInventory -->> showUploadPage() -->> ");
		String email = session("email");
		Employee employee = Employee.findByEmail(email);
		Store store = employee.store;

		// list used by store manager to find uploaded Invoices By Invoice Type
		List<String> typeList = new ArrayList<String>();
		typeList.add("CashPaid");
		typeList.add("Others");
		typeList.add("Statements");
		typeList.add("Fuel");

		Logger.info("@C InvoiceInventory -->> showUploadPage() <<-- ");
		return ok(showUploadPage.render(store.id, typeList));
	}

	// show upload page for Head Office People Invoice Upload
	public static Result showUploadPageForHeadOffice() {

		Logger.info("@C InvoiceInventory -->> showUploadPageForHeadOffice() -->> ");
		return ok(showUploadPageForHeadOffice.render());
	}

	// show payment page for Head Office
	public static Result showPaymentPage(String selectedCompany,
			String selectedStore, String selectedCategory,
			String selectedPaymentTerm) {
		System.out.println("Selected Company in show payment page : "
				+ selectedCompany);
		System.out.println("Selected Payment Term in show payment page : "
				+ selectedPaymentTerm);
		/*
		 * String selectedCompany="noCompany"; String selectedStore="noStore";
		 */
		Logger.info("@C InvoiceInventory -->> showPaymentpage() -->> ");
		return ok(showPaymentPage.render(selectedCompany, selectedStore,
				selectedCategory, selectedPaymentTerm));

	}

	// gives list of Invoice for Payment page
	public static Result getInvoicesForPayment() {

		Logger.info("@C InvoiceInventory -->> getInvoicesForPayment() -->> ");
		String invoiceCategory = "";
		String paymentTerms = "Others";
		List<Invoice> invoiceList = new ArrayList<Invoice>();

		Long cid = Long.parseLong(form().bindFromRequest().get("companyId"));
		String storeId = form().bindFromRequest().get("storeId");
		invoiceCategory = form().bindFromRequest().get("invoiceCategory");

		
		/*if(storeId.equals("")){
			storeId="1";
		}*/
		Logger.info("-------------------------------->"+storeId);

		Long refInvoiceId = 0L; // reference Invoice id to get same filter
								// criteria type invoices while search by
								// supplier
		String filter = ""; // used to assign value to search box with default
							// value
		/*if(invoiceCategory.equals("")){
			invoiceCategory="OTHER";
		}*/

		if (storeId.equals("")) { // if he selects only company,present all
									// invoices of that company

			invoiceList = Invoice.page("PROCESSED", cid);
		} else { // if he selects a store then gives List of Invoices of that
					// store based on invoice category and Payment terms

			Long sid = Long.parseLong(storeId);
			if (invoiceCategory.equals("Fuel")) { // gives Fuel Invoices of that
													// Store
				paymentTerms = "Fuel";
				invoiceList = Invoice.page(paymentTerms, sid, "PROCESSED");
			} else {
				paymentTerms = form().bindFromRequest().get("paymentTerms");
				if (paymentTerms.equals("ALL")) {
					invoiceList = Invoice.pageNEFuel("Fuel", sid, "PROCESSED"); // gives
																				// all
																				// Invoices
																				// of
																				// that
																				// store
																				// except
																				// fuel
				} else {
					invoiceList = Invoice.page(paymentTerms, sid, "PROCESSED"); // gives
																				// list
																				// Invoices
																				// of
																				// that
																				// Store
																				// based
																				// on
																				// payment
																				// term
				}
			}

		}

		// assign one Invoice id value to refInvoiceId ,if invoice list is not
		// empty
		if (invoiceList.size() >= 1) {

			refInvoiceId = invoiceList.get(0).id;
		}

		Logger.info("@C InvoiceInventory -->> getInvoicesForPayment(invoiceList, "
				+ paymentTerms + ") <<--");
		System.out.println("Selected Payment Term------------  : " + storeId);
		if(storeId.equals("") && invoiceCategory.equals("")){
			storeId="HEAD OFFICE";
			invoiceCategory="OTHER";
			
		}
		return ok(invoiceListPageForPaymentPeople.render(invoiceList,
				paymentTerms, refInvoiceId, filter, String.valueOf(cid),
				storeId, invoiceCategory, paymentTerms, invoiceCategory));
	}

	// show upload Invoice page for sk date select page
	public static Result dateSelectPageForSK(Long sid) {

		Logger.info("@C InvoiceInventory------------ -->> showUploadedInvoicesForSK(sid :"
				+ sid + ") -->> ");
		List<Invoice> invoiceList = new ArrayList<Invoice>();

		// by default give today's date invoice list
		Date date = new Date();
		invoiceList = Invoice.page(sid, Application.getDate(date));

		return ok(dateSelectForInvoiceViewForSK.render(sid, invoiceList, date,
				date, null,null));
	}

	// show upload Invoice page for sk date select page
	public static Result dateSelectPageForHeadOffice() {

		Logger.info("@C InvoiceInventory -->> dateSelectPageForHeadOffice() -->> ");

		List<Invoice> invoiceList = new ArrayList<Invoice>();
		Date d=new Date();
		return ok(dateSelectForInvoiceViewForHeadOffice.render(invoiceList,
				null, Application.getDate(d), Application.getDate(d), null,null));
	}

	// display Uploaded Invoice for SK
	public static Result displayUploadedForSK(Long sid) {

		Logger.info("@C InvoiceInventory -->> displayUploadedForSK(sid :" + sid
				+ ") -->> ");

		String startDate = form().bindFromRequest().get("invoiceStartDate");
		String endDate = form().bindFromRequest().get("invoiceEndDate");

		List<Invoice> invoiceList = new ArrayList<Invoice>();

		// if start Date and end date both are equal
		if (startDate.equals(endDate)) {

			invoiceList = Invoice.page(sid, Application.getDate(startDate));

		} else { // if start date and end date are different

			invoiceList = Invoice.page(sid, Application.getDate(startDate),
					Application.getDate(endDate));
		}

		return ok(dateSelectForInvoiceViewForSK.render(sid, invoiceList,
				Application.getDate(startDate), Application.getDate(endDate),
				null,null));

	}

	public static Result getInvoicesBySupplierName() {

		Logger.info("@C InvoiceInventory -->> getInvoicesBySupplierName() -->> ");

		String supplierName = form().bindFromRequest().get("s");
		String invoiceStatus = form().bindFromRequest().get("invoice_status");
		String sid = form().bindFromRequest().get("store_id");
		Long sid1 = Long.valueOf(sid);
		Logger.info("@C InvoiceInventory------------ -->> getInvoicesBySupplierName(storeId,ConvertedStoreId :"
				+ sid + "," + sid1 + "," + invoiceStatus + ") -->> ");
		List<Invoice> invoiceList=new ArrayList<Invoice>();
		Date date = new Date();
		// Store.findStoreIds(sid1);
		Logger.info("@C InvoiceInventory------------ -->> getInvoicesBySupplierName(supplier typed in search box is :"
				+ supplierName);
		invoiceList = Invoice.getInvoiceListBySupplierName(
				supplierName, sid1, invoiceStatus);
		return ok(dateSelectForInvoiceViewForSK.render(sid1, invoiceList,
				Application.getDate(date), Application.getDate(date),
				supplierName,invoiceStatus));
	}

	public static Result getInvoicesBySupplierNameInHeadOffice() {

		Logger.info("@C InvoiceInventory -->> getInvoicesBySupplierName() -->> ");

		String supplierName = form().bindFromRequest().get("s");
		String sid = form().bindFromRequest().get("store_id");
		Long sid1 = Long.valueOf(sid);
		String invoiceStatus = form().bindFromRequest().get("invoice_status");

		Logger.info("@C InvoiceInventory------------ -->> getInvoicesBySupplierName(sid :"
				+ sid + "," + sid1 + ") -->> ");
		Date date = new Date();
		// Store.findStoreIds(sid1);
		System.out.println("supplier typed in search box is :" + supplierName);
		List<Invoice> invoiceList = Invoice.getInvoiceListBySupplierName(
				supplierName, sid1, "PAID");
		return ok(dateSelectForInvoiceViewForHeadOffice.render(invoiceList,
				sid1, Application.getDate(date), Application.getDate(date),
				supplierName,invoiceStatus));
	}

	// display Uploaded Invoice for Head Office
	public static Result displayUploadedForHeadOffice() {

		Logger.info("@C InvoiceInventory -->> () -->> ");

		Long sid = Long.parseLong(form().bindFromRequest().get("storeId"));

		String startDate = form().bindFromRequest().get("invoiceStartDate");
		String endDate = form().bindFromRequest().get("invoiceEndDate");
		String supplierName = form().bindFromRequest().get("s");
		String invoiceStatus = form().bindFromRequest().get("invoice_status");
		Logger.info("@C InvoiceInventory -->> displayUploadedForHeadOffice() -->>supplier_name,invoice_status -->> "
				+ supplierName + "," + invoiceStatus);

		List<Invoice> invoiceList = new ArrayList<Invoice>();
		System.out.println("startDate :"+startDate);
		System.out.println("endDate :"+endDate);
		System.out.println("supplierName :" +supplierName);
		System.out.println("store id :"+sid);
		System.out.println("invoiceStatus :"+invoiceStatus);
		
		if(supplierName.equals("")){
			System.out.println("====================== :" +"u did not search by supplier");
			if (startDate.equals(endDate)) {

				invoiceList = Invoice.page(sid, Application.getDate(startDate),Application.getDate(startDate),invoiceStatus);

			} else { // if start date and end date are different

				invoiceList = Invoice.page(sid, Application.getDate(startDate),
						Application.getDate(endDate),invoiceStatus);
			}
			
		}
		else{
			System.out.println("====================== :" +supplierName);
			invoiceList= Invoice.getInvoiceListBySupplierName(
					supplierName, sid, invoiceStatus);

		}

		/*if ((supplierName.equals("") && invoiceStatus.equals("")) || (supplierName.equals("") || invoiceStatus.equals(""))) {
			Logger.info("@C InvoiceInventory -->> @@@@@@@@() -->>supplier_name,invoice_status -->> "+supplierName+","+invoiceStatus);

			// if start Date and end date both are equal
			if (startDate.equals(endDate)) {

				invoiceList = Invoice.page(sid, Application.getDate(startDate));

			} else { // if start date and end date are different

				invoiceList = Invoice.page(sid, Application.getDate(startDate),
						Application.getDate(endDate));
			}
		}
		else{
			if((!supplierName.equals("") || !(invoiceStatus.equals("")))){
			Date date = new Date();
			System.out.println("DATE IS :"+date);
			startDate=Application.getDateString(date);
			endDate=Application.getDateString(date);
			Logger.info("@C InvoiceInventory -->>inside else-->> "+supplierName+","+invoiceStatus);
			invoiceList = Invoice.getInvoiceListBySupplierName(
					supplierName, sid, invoiceStatus);
			}

		}
*/
		return ok(dateSelectForInvoiceViewForHeadOffice.render(invoiceList,
				sid, Application.getDate(startDate),
				Application.getDate(endDate), null,invoiceStatus));

	}

	// show download or Processing page
	public static Result showDownloadPage() {

		Logger.info("@C InvoiceInventory -->> showDownloadPage() -->> ");

		// by default send dummy values
		Long storeId = 1L;
		String invoiceType = "";
		List<Invoice> invoiceList = new ArrayList<Invoice>();

		return ok(showDownloadPage.render(invoiceList, storeId, invoiceType));
	}

	// save or upload Invoices From Store Keeper side
	public static Result uploadInvoices(Long sid) {

		Logger.info("@C InvoiceInventory -->> uploadInvoices(store id: " + sid
				+ ") -->> ");

		play.mvc.Http.MultipartFormData body = request().body()
				.asMultipartFormData();

		// get InvoiceType from select Box
		String invoiceType = body.asFormUrlEncoded().get("invoiceType")[0];
		Logger.info("@C InvoiceInventoryinvoiceType-->> " + invoiceType);

		Store store = Store.find.byId(sid);

		// path to Upload Files
		// File ftemp= new File(basePath
		// +"//public//InvoiceInventory//"+store.name+"//SHOP//"+invoiceType+"//");
		// for play
		File ftemp = new File(basePath + "//InvoiceInventory//" + store.name
				+ "//SHOP//" + invoiceType + "//"); // for tomcat
		ftemp.mkdirs();

		List<FilePart> fileList = body.getFiles();
		for (FilePart p : fileList) {

			Long lastId = (Long) Invoice.find.findIds().get(
					Invoice.find.findIds().size() - 1);
			String modifiedFileName = Application.getModifiedFileName(
					p.getFilename(), sid, lastId + 1); // form the name with
														// store id and date

			File f1 = new File(ftemp.getAbsolutePath() + "//"
					+ modifiedFileName);
			File file = p.getFile();

			file.setWritable(true);
			file.setReadable(true);
			f1.setWritable(true);
			f1.setReadable(true);

			try {
				Files.copy(file.toPath(), f1.toPath(), REPLACE_EXISTING);
				String filePathString = "InvoiceInventory/" + store.name
						+ "/SHOP/" + invoiceType + "/" + modifiedFileName;
				// update for File Replace & Create new Record For File Copy
				Invoice.checkFileExists(store.id, invoiceType,
						modifiedFileName, "UPLOADED", filePathString);

				Logger.debug("@C InvoiceInventory -->> uploadInvoices(store id: "
						+ sid + ") -->> File path " + filePathString);

				// set flash to indicate successful Upload done
				flash("success", fileList.size()
						+ " File(s) Has been Successfully Uploaded");
			} catch (Exception e) {

				Logger.error(
						"@C InvoiceInventory -->> uploadInvoices(store id: "
								+ sid + ") -->> ", e);
				e.printStackTrace();
			}
		}

		Logger.info("@C InvoiceInventory -->> uploadInvoices(store id: " + sid
				+ ") <<-- Redirecting to Upload page");
		// return to upload page
		return redirect(routes.InvoiceInventory.showUploadPage());
	}

	// save or upload Invoices from Head Office side

	public static Result uploadInvoicesByHeadOffice() throws Exception {

		Logger.info("@C InvoiceInventory -->> uploadInvoicesByHeadOffice() -->> ");

		play.mvc.Http.MultipartFormData body = request().body()
				.asMultipartFormData();

		// get InvoiceType from select Box
		String invoiceType = body.asFormUrlEncoded().get("invoiceType")[0];

		Store store = Store.find.byId(Long.parseLong(body.asFormUrlEncoded()
				.get("storeId")[0]));
		Long sid = store.id;

		// path to Upload Files
		// File ftemp= new File(basePath
		// +"//public//InvoiceInventory//"+store.name+"//SHOP//"+invoiceType+"//");
		// for play
		File ftemp = new File(basePath + "//InvoiceInventory//" + store.name
				+ "//SHOP//" + invoiceType + "//"); // for tomcat
		ftemp.mkdirs();

		List<FilePart> fileList = body.getFiles();
		for (FilePart p : fileList) {

			Long lastId = (Long) Invoice.find.findIds().get(
					Invoice.find.findIds().size() - 1);
			String modifiedFileName = Application.getModifiedFileName(
					p.getFilename(), sid, lastId + 1); // form the name with
														// store id and date

			File f1 = new File(ftemp.getAbsolutePath() + "//"
					+ modifiedFileName);
			File file = p.getFile();

			file.setWritable(true);
			file.setReadable(true);
			f1.setWritable(true);
			f1.setReadable(true);

			try {
				Files.copy(file.toPath(), f1.toPath(), REPLACE_EXISTING);
				String filePathString = "InvoiceInventory/" + store.name
						+ "/SHOP/" + invoiceType + "/" + modifiedFileName;
				// update for File Replace & Create new Record For File Copy
				Invoice.checkFileExists(store.id, invoiceType,
						modifiedFileName, "UPLOADED", filePathString);

				Logger.debug("@C InvoiceInventory -->> uploadInvoicesByHeadOffice() -->> File Path "
						+ filePathString);

				// set flash to indicate successful Upload done
				flash("success", fileList.size()
						+ " File(s) Has been Successfully Uploaded");
			} catch (Exception e) {
				Logger.error(
						"@C InvoiceInventory -->> uploadInvoicesByHeadOffice() -->> catch block",
						e);
				e.printStackTrace();
			}
		}
		Logger.info("@C InvoiceInventory -->> uploadInvoicesByHeadOffice() <<-- Redirecting to Upload Page for Head Office");
		// return to upload page
		return redirect(routes.InvoiceInventory.showUploadPageForHeadOffice());
	}

	// show invoices for processing for Admin module

	public static Result getInvoices() {

		Logger.info("@C InvoiceInventory -->> getInvoices() -->> ");

		// get store id from select box
		Long storeId = Long.parseLong(form().bindFromRequest().get("storeId"));

		// get invoiceType from select box
		String invoiceType = form().bindFromRequest().get("invoiceType");

		List<Invoice> invoiceList = Invoice.page(storeId, invoiceType,
				"UPLOADED");
		Logger.info("@C InvoiceInventory -->> getInvoices() <<-- store id: "
				+ storeId + " Invoice Type: " + invoiceType
				+ " invoiceList size: " + invoiceList.size());

		return ok(showDownloadPage.render(invoiceList, storeId, invoiceType));
	}

	// to just display Invoices ,Not for Processing
	public static Result getInvoicesByStoreAndType(Long sid, String invoiceType) {

		Logger.info("@C InvoiceInventory -->> getInvoicesByStoreAndType(" + sid
				+ "," + invoiceType + ") -->> ");

		List<Invoice> invoiceList = Invoice.page(sid, invoiceType, "UPLOADED");

		Logger.info("@C InvoiceInventory -->> getInvoicesByStoreAndType(" + sid
				+ "," + invoiceType + ") <<-- Retrived Invoice List size "
				+ invoiceList);
		return ok(invoiceListPageForJustView.render(invoiceList));
	}

	public static Result move(Long id) {

		Logger.info("@C InvoiceInventory -->> move(Invoice id :" + id
				+ ") -->> ");

		Invoice invoice = Invoice.find.byId(id);
		StringBuffer filePathString = new StringBuffer(); // to hold destination
															// file path

		// read form elements
		String invoiceCategory = form().bindFromRequest().get("category"); // to
																			// identify
																			// this
																			// invoice
																			// is
																			// Fuel
																			// Invoice
																			// or
																			// other
		String paymentTerms = form().bindFromRequest().get("paymentTerms");
		String invoiceDate = form().bindFromRequest().get("invoiceDate");

		if (paymentTerms == null) { // for fuel Invoices we disabled payment
									// Terms.hence null will come if we read
									// it.Heance instead of Null i am placing
									// "Fuel" in DB record

			paymentTerms = "Fuel";
		}

		// get Month from the Invoice Date
		Calendar cal = Calendar.getInstance();
		cal.setTime(Application.getDate(invoiceDate));
		int month = cal.get(Calendar.MONTH);
		String monthString = Application.getMonthForInt(month);

		// get Store from Invoice
		Store store = Store.find.byId(invoice.storeId);

		// Moving the File From Shop Folder to HeadOffice Folder
		// destination path
		// File destPath=new File(basePath
		// +"//public//InvoiceInventory//"+store.name+"//HEAD OFFICE//"+monthString+"//"+paymentTerms+"//");//
		// for play
		File destPath = null;
		Supplier supplier = null;
		if (invoiceCategory.equals("Fuel")) { // if it is Fuel category Place in
												// Fuel folder irrespective of
												// Payment Terms

			Logger.debug("@C InvoiceInventory -->> move(Invoice id :" + id
					+ ") -->> invoice Category : " + invoiceCategory);
			destPath = new File(basePath + "//InvoiceInventory//" + store.name
					+ "//HEAD OFFICE//" + monthString + "//" + invoiceCategory
					+ "//"); // for tomcat
			filePathString.append("InvoiceInventory/" + store.name
					+ "/HEAD OFFICE/" + monthString + "/" + invoiceCategory
					+ "/");
		} else { // if other ,place it in Supplier folder inside Payment Terms
					// folder

			Logger.debug("@C InvoiceInventory -->> move(Invoice id :" + id
					+ ") -->> invoice Category : " + invoiceCategory);
			String supplierId = form().bindFromRequest().get("supplier"); // Retrieve
																			// Supplier
																			// id
																			// from
																			// fancyBox

			if (supplierId != null) { // supplierId null check
				supplier = Supplier.find.byId(Long.parseLong(supplierId));
			}

			if (invoice.invoiceType.equals("CashPaid")) { // if invoice type is
															// CashPaid put it
															// in PAID folder
															// directly while
															// moving

				destPath = new File(basePath + "//InvoiceInventory//"
						+ store.name + "//HEAD OFFICE//" + monthString + "//"
						+ paymentTerms + "//" + supplier.name.trim()
						+ "//PAID//"); // for tomcat
				filePathString.append("InvoiceInventory/" + store.name
						+ "/HEAD OFFICE/" + monthString + "/" + paymentTerms
						+ "/" + supplier.name.trim() + "/PAID/");

			} else {
				destPath = new File(basePath + "//InvoiceInventory//"
						+ store.name + "//HEAD OFFICE//" + monthString + "//"
						+ paymentTerms + "//" + supplier.name.trim() + "//"); // for
																				// tomcat
				filePathString.append("InvoiceInventory/" + store.name
						+ "/HEAD OFFICE/" + monthString + "/" + paymentTerms
						+ "/" + supplier.name.trim() + "/");

			}
		}
		destPath.mkdirs();
		File destPathFile = new File(destPath.getAbsolutePath() + "//"
				+ invoice.fileName);
		File srcPathFile = new File(basePath + invoice.filePath);

		filePathString.append(invoice.fileName);

		destPathFile.setWritable(true);
		destPathFile.setReadable(true);
		srcPathFile.setWritable(true);
		srcPathFile.setReadable(true);

		String status; // set status as PAID if invoice type is CashPaid
						// otherwise PROCESSED
		if (invoice.invoiceType.equals("CashPaid")) {
			status = "PAID";
		} else {
			status = "PROCESSED";
		}

		try {
			Files.move(srcPathFile.toPath(), destPathFile.toPath(), ATOMIC_MOVE);

			// check whether invoice belongs to Fuel or Others
			if (supplier != null) {

				Invoice.updateStatusForSupplierInvoice(id, new Date(),
						Application.getDate(invoiceDate), paymentTerms, status,
						filePathString.toString(), supplier.id);
			} else {

				Invoice.updateStatus(id, new Date(),
						Application.getDate(invoiceDate), paymentTerms, status,
						filePathString.toString());
			}

			Logger.debug("@C InvoiceInventory -->> move(Invoice id :" + id
					+ ") -->> source Path: " + srcPathFile.toPath());
			Logger.debug("@C InvoiceInventory -->> move(Invoice id :" + id
					+ ") -->> Destination Path: " + destPathFile.toPath());

			// set flash to indicate successful Upload done
			flash("success", invoice.fileName
					+ " Has been Successfully Processed..!");
		} catch (Exception e) {

			Logger.error("@C InvoiceInventory -->> move(Invoice id :" + id
					+ ") -->> in Catch block ", e);
			// set flash to indicate successful Upload done
			flash("success",
					invoice.fileName
							+ " Has Not Moved Successfully..! Some Problem Occured Internally");
			e.printStackTrace();
		}

		Logger.info("@C InvoiceInventory -->> move(Invoice id :"
				+ id
				+ ") <<-- Redirecting to Remaining List with same selection Criteria ");

		// Return back to Invoice List of page of same select Criteria,Remaining
		// Uploaded Invoices.., If we directly render list method the url of
		// FancyBox Form submission is not changing,that's why i am Redirecting
		return redirect(routes.InvoiceInventory.getInvoicesAfterMove(store.id,
				invoice.invoiceType));
	}

	// Move the Head Office Invoices based on Company

	public static Result headOfficeMove(Long id) {

		Logger.info("@C InvoiceInventory -->> headOfficeMove(Invoice id :" + id
				+ ") -->> ");

		Invoice invoice = Invoice.find.byId(id);

		StringBuffer filePathString = new StringBuffer(); // to hold destination
															// file path

		// get Store from Invoice
		Store store = Store.find.byId(invoice.storeId);

		// read form elements
		String invoiceCategory = form().bindFromRequest()
				.get("invoiceCategory");
		Long companyId = Long
				.parseLong(form().bindFromRequest().get("company"));
		String invoiceDate = form().bindFromRequest().get("invoiceDate");

		Company company = Company.find.byId(companyId);

		// get Month from the Invoice Date
		Calendar cal = Calendar.getInstance();
		cal.setTime(Application.getDate(invoiceDate));
		int month = cal.get(Calendar.MONTH);
		String monthString = Application.getMonthForInt(month);

		File destPath = new File(basePath + "//InvoiceInventory//" + store.name
				+ "//" + company.name + "//" + monthString + "//"
				+ invoiceCategory + "//"); // for tomcat
		destPath.mkdirs();
		File destPathFile = new File(destPath.getAbsolutePath() + "//"
				+ invoice.fileName);
		File srcPathFile = new File(basePath + invoice.filePath);

		destPathFile.setWritable(true);
		destPathFile.setReadable(true);
		srcPathFile.setWritable(true);
		srcPathFile.setReadable(true);

		filePathString.append("InvoiceInventory/" + store.name + "/"
				+ company.name + "/" + monthString + "/" + invoiceCategory
				+ "/");
		filePathString.append(invoice.fileName);

		String status; // set status as PAID if invoice type is CashPaid
						// otherwise PROCESSED
		if (invoice.invoiceType.equals("CashPaid")) {
			status = "PAID";
		} else {
			status = "PROCESSED";
		}

		try {
			Files.move(srcPathFile.toPath(), destPathFile.toPath(), ATOMIC_MOVE);

			Invoice.updateStatus(id, new Date(),
					Application.getDate(invoiceDate), "Others", status,
					filePathString.toString(), companyId);

			Logger.debug("@C InvoiceInventory -->> headOfficeMove(Invoice id :"
					+ id + ") -->> source Path: " + srcPathFile.toPath());
			Logger.debug("@C InvoiceInventory -->> headOfficeMove(Invoice id :"
					+ id + ") -->> Destination Path: " + destPathFile.toPath());

			// set flash to indicate successful Upload done
			flash("success", invoice.fileName
					+ " Has been Successfully Processed..!");
		} catch (Exception e) {

			Logger.error("@C InvoiceInventory -->> headOfficeMove(Invoice id :"
					+ id + ") -->> in Catch block ", e);
			// set flash to indicate successful Upload done
			flash("success",
					invoice.fileName
							+ " Has Not Moved Successfully..! Some Problem Occured Internally");
			e.printStackTrace();
		}

		Logger.info("@C InvoiceInventory -->> headOfficeMove(Invoice id :" + id
				+ ") <<--");

		// Return back to Invoice List of page of same select Criteria,Remaining
		// Uploaded Invoices.., If we directly render list method the url of
		// FancyBox Form submission is not changing,that's why i am Redirecting
		return redirect(routes.InvoiceInventory.getInvoicesAfterMove(store.id,
				invoice.invoiceType));
	}

	// move Invoices to PAID folder from Company folder in Head Office(1) store
	// after Payment

	public static Result paymentMove(Long id, String paymentTerms,
			Long companyId, String filter, String selectedCategory) {
		String selectedCtegoryForOtherStatements = "Fuel";
		Logger.info("@C InvoiceInventory --==================>> paymentMove(Invoice id :" + id
				+ "," + selectedCategory + ") -->> ");

		Invoice invoice = Invoice.find.byId(id);

		StringBuffer filePathString = new StringBuffer(); // to hold destination
															// file path

		// make the destination path from source path with extra folder PAID
		File file = new File(invoice.filePath);
		String filePath = file.getPath();
		String path = filePath.substring(0,
				filePath.lastIndexOf(File.separator));

		File destPath = new File(basePath + path + "//PAID//");
		destPath.mkdirs();
		File destPathFile = new File(destPath.getAbsolutePath() + "//"
				+ invoice.fileName);
		File srcPathFile = new File(basePath + invoice.filePath);

		destPathFile.setWritable(true);
		destPathFile.setReadable(true);
		srcPathFile.setWritable(true);
		srcPathFile.setReadable(true);

		filePathString.append(path + "/PAID/");
		filePathString.append(invoice.fileName);

		/*
		 * System.out.println("source : "+srcPathFile.getAbsolutePath());
		 * System.out.println("dest : "+destPathFile.getAbsolutePath());
		 * System.out.println("file path : "+filePathString);
		 */

		try {
			Files.move(srcPathFile.toPath(), destPathFile.toPath(), ATOMIC_MOVE);

			Invoice.updateStatus(id, "PAID", filePathString.toString());

			Logger.debug("@C InvoiceInventory -->> paymentMove(Invoice id :"
					+ id + ") -->> source Path: " + srcPathFile.toPath());
			Logger.debug("@C InvoiceInventory -->> paymentMove(Invoice id :"
					+ id + ") -->> Destination Path: " + destPathFile.toPath());

			// set flash to indicate successful Upload done
			flash("success", invoice.fileName
					+ " Has been Successfully Paid..!");
		} catch (Exception e) {

			Logger.error("@C InvoiceInventory -->> paymentMove(Invoice id :"
					+ id + ") -->> in Catch block ", e);
			// set flash to indicate successful Upload done
			flash("success",
					invoice.fileName
							+ " Has Not Moved Successfully..! Some Problem Occured Internally");
			e.printStackTrace();
		}

		Logger.info("@C InvoiceInventory -->> paymentMove(Invoice id :" + id
				+ ") <<--");
		Logger.info("@C InvoiceInventory -->> paymentMove(selectedCtegoryForOtherStatements :"
				+ selectedCtegoryForOtherStatements + ") <<--");
		if (selectedCategory.equals("Others")
				|| selectedCategory.equals("Statements")
				|| selectedCategory.equals("Other")) {
			selectedCtegoryForOtherStatements = "Other";
			Logger.info("@C InvoiceInventory -->> paymentMove(selectedCtegoryForOtherStatements in if :"
					+ selectedCtegoryForOtherStatements + ") <<--");
			Logger.info("@C InvoiceInventory------------------------------>"+selectedCtegoryForOtherStatements);
		}

		return redirect(routes.InvoiceInventory.getInvoicesAfterPaymentDone(id,
				paymentTerms, companyId, filter,
				selectedCtegoryForOtherStatements));
	}

	// Redirect method used in move()
	public static Result getInvoicesAfterMove(Long storeId, String invoiceType) {

		Logger.info("@C InvoiceInventory -->> getInvoicesAfterMove(store id :"
				+ storeId + "," + invoiceType + ") -->> ");

		List<Invoice> invoiceList = Invoice.page(storeId, invoiceType,
				"UPLOADED");
		return ok(showDownloadPage.render(invoiceList, storeId, invoiceType));
	}

	// redirect method to get Invoices of same select Criteria while doing
	// payment, paymentTerms parameter used here to get the same selected
	// criteria invoice list

	public static Result getInvoicesAfterPaymentDone(Long id,
			String paymentTerms, Long companyId, String filter,
			String selectedCategory) {

		Logger.info("@C InvoiceInventory -->> getInvoicesAfterPaymentDone(Invoice id :"
				+ id + ") -->> ");
		Logger.info("@C InvoiceInventory  getInvoicesAfterPaymentDoneselected CategoryAtFirst------------------:"
				+ selectedCategory + ") -->> ");

		Invoice invoice = Invoice.find.byId(id);
		List<Invoice> outInvoiceList = new ArrayList<Invoice>();

		// from client side we are sending dummy string(EMPTY) value if current
		// filter is ("")
		if (filter.equals("EMPTY")) {

			filter = ""; // if filter is EMPTY then assign ""., From client side
							// we are sending dummy value if filter is ""
		}

		// if filter is empty string
		if (filter.trim().equals("")) {

			if (invoice.storeId == 1) { // if it is head office store,get
										// invoices related to that company with
										// PROCESSED ,payment terms
				if (invoice.companyId != null) {
					outInvoiceList = Invoice.page(invoice.companyId,
							invoice.storeId, invoice.paymentMode, "PROCESSED");
				}
			} else { // give Invoices related to a store
				if (paymentTerms.equals("ALL")) {
					outInvoiceList = Invoice.pageNEFuel("Fuel",
							invoice.storeId, "PROCESSED");
				} else {
					outInvoiceList = Invoice.page(invoice.paymentMode,
							invoice.storeId, "PROCESSED");
				}
			}

		} else {

			List<Supplier> supplierList = Supplier.getSuppliersList(filter
					.trim());
			Logger.debug("@C InvoiceInventory -->> getInvoicesAfterPaymentDone(Invoice id :"
					+ id
					+ ", filter :"
					+ filter
					+ ", paymentTerms: "
					+ paymentTerms
					+ ") -->> supplierList size : "
					+ supplierList.size());

			for (Supplier supplier : supplierList) {

				List<Invoice> invoiceList = new ArrayList<Invoice>();

				if (invoice.storeId == 1) { // if it is head office store,get
											// invoices related to that company
											// with PROCESSED ,payment terms
					if (invoice.companyId != null) {
						invoiceList = Invoice.page(invoice.companyId,
								invoice.storeId, invoice.paymentMode,
								"PROCESSED", supplier.id);
					}
				} else { // give Invoices related to a store
					if (paymentTerms.equals("ALL")) {
						invoiceList = Invoice.pageNEFuel("Fuel",
								invoice.storeId, "PROCESSED", supplier.id);
					} else {
						invoiceList = Invoice.page(invoice.paymentMode,
								invoice.storeId, "PROCESSED", supplier.id);
					}
				}

				outInvoiceList.addAll(invoiceList);
			}

		}

		Logger.info("@C InvoiceInventory -->> getInvoicesAfterPaymentDone(Invoice id :"
				+ id + ") <<--");
		Logger.info("@C InvoiceInventory&&&& getInvoicesAfterPaymentDoneselected CategoryAtLast:"
				+ selectedCategory + ") -->> ");

		return ok(invoiceListPageForPaymentPeople.render(outInvoiceList,
				paymentTerms, id, filter, String.valueOf(companyId),
				String.valueOf(invoice.storeId), invoice.invoiceType,
				paymentTerms, selectedCategory));
	}

	// to delete the Unwanted Invoice
	public static Result delete(Long id) {

		Logger.info("@C InvoiceInventory -->> delete(Invoice id :" + id
				+ ") -->> ");

		Invoice invoice = Invoice.find.byId(id);
		Store store = Store.find.byId(invoice.storeId);

		// File file = new File(basePath
		// +"//public//InvoiceInventory//"+store.name+"//SHOP//"+invoice.invoiceType+"//");
		// // for play
		File file = new File(basePath + "//InvoiceInventory//" + store.name
				+ "//SHOP//" + invoice.invoiceType + "//"); // for tomcat
		File srcPathFile = new File(file.getAbsolutePath() + "//"
				+ invoice.fileName);
		try {
			boolean flag = srcPathFile.delete();
			if (flag == true) {
				Invoice.delete(id);
				// set flash to indicate successful Delete done
				flash("success", invoice.fileName
						+ " Has been Successfully Deleted..!");
			} else {
				// set flash to indicate Delete operation Failed
				flash("success", invoice.fileName
						+ " Not Deleted..! Some Problem Occurred..");
			}
		} catch (Exception e) {
			// set flash to indicate Delete operation Failed,due to Exception
			flash("success", invoice.fileName
					+ " Not Deleted..! Some Exception Occurred..");
			Logger.error("@C InvoiceInventory -->> delete(Invoice id :" + id
					+ ") -->> Cactch Block ", e);
			e.printStackTrace();
		}

		Logger.info("@C InvoiceInventory -->> delete(Invoice id :" + id
				+ ") <<--");
		// Return back to Invoice List of page of same select Criteria,Remaining
		// Uploaded Invoices..
		return redirect(routes.InvoiceInventory.getInvoicesAfterMove(store.id,
				invoice.invoiceType));
	}

	// to filter Invoices about to be paid based on Supplier name

	public static Result filterPaymentInvoices(String companyId,
			String selectedCategoryForEnable, String filter, Long refInvoiceId,
			String paymentTerms) {

		Logger.info("@C InvoiceInventory ----------------------->> filterPaymentInvoices(Invoice id :"
				+ refInvoiceId
				+ ", filter :"
				+ filter
				+ ", paymentTerms: "
				+ paymentTerms + ") -->> ");

		Invoice invoice = Invoice.find.byId(refInvoiceId);
		List<Invoice> outInvoiceList = new ArrayList<Invoice>();

		// if filter is empty string
		if (filter.trim().equals("")) {

			if (invoice.storeId == 1) { // if it is head office store,get
										// invoices related to that company with
										// PROCESSED ,payment terms
				if (invoice.companyId != null) {
					outInvoiceList = Invoice.page(invoice.companyId,
							invoice.storeId, invoice.paymentMode, "PROCESSED");
				}
			} else { // give Invoices related to a store
				if (paymentTerms.equals("ALL")) {
					outInvoiceList = Invoice.pageNEFuel("Fuel",
							invoice.storeId, "PROCESSED");
				} else {
					outInvoiceList = Invoice.page(invoice.paymentMode,
							invoice.storeId, "PROCESSED");
				}
			}

		} else { // filter search box is not empty

			List<Supplier> supplierList = Supplier.getSuppliersList(filter
					.trim());
			Logger.debug("@C InvoiceInventory -->> filterPaymentInvoices(Invoice id :"
					+ refInvoiceId
					+ ", filter :"
					+ filter
					+ ", paymentTerms: "
					+ paymentTerms
					+ ") -->> supplierList size : "
					+ supplierList.size());

			for (Supplier supplier : supplierList) {

				List<Invoice> invoiceList = new ArrayList<Invoice>();

				if (invoice.storeId == 1) { // if it is head office store,get
											// invoices related to that company
											// with PROCESSED ,payment terms
					if (invoice.companyId != null) {
						invoiceList = Invoice.page(invoice.companyId,
								invoice.storeId, invoice.paymentMode,
								"PROCESSED", supplier.id);
					}
				} else { // give Invoices related to a store
					if (paymentTerms.equals("ALL")) {
						invoiceList = Invoice.pageNEFuel("Fuel",
								invoice.storeId, "PROCESSED", supplier.id);
					} else {
						invoiceList = Invoice.page(invoice.paymentMode,
								invoice.storeId, "PROCESSED", supplier.id);
					}
				}

				outInvoiceList.addAll(invoiceList);
			}
		}
		Logger.info("@C InvoiceInventory -->> filterPaymentInvoices(Invoice id :"
				+ refInvoiceId
				+ ", filter :"
				+ filter
				+ ", paymentTerms: "
				+ paymentTerms + ") <<--");
		return ok(invoiceListPageForPaymentPeople.render(outInvoiceList,
				paymentTerms, refInvoiceId, filter, String.valueOf(companyId),
				String.valueOf(invoice.storeId), invoice.invoiceType,
				paymentTerms, selectedCategoryForEnable));
	}
}
