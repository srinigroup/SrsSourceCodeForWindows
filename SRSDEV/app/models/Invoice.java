package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;

import controllers.Application;
import controllers.Stores;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Invoice extends Model {

	private static final long serialVersionUID = 1L;
	@Id
	public Long id;

	public String fileName;

	public String invoiceType;

	public String filePath;

	public Long storeId;

	public Long companyId;

	public Long supplierId;

	@Formats.DateTime(pattern = "dd/MM/yyyy hh:mm:ss")
	public Date uploadedDate;

	@Formats.DateTime(pattern = "dd/MM/yyyy hh:mm:ss")
	public Date processedDate;

	@Formats.DateTime(pattern = "dd/MM/yyyy hh:mm:ss")
	public Date invoiceDate;

	public String paymentMode;

	public String status;

	// constructor
	public Invoice(String fileName, Date uploadedDate, String invoiceType,
			Long storeId, String filePath, String status) {

		this.fileName = fileName;
		this.uploadedDate = uploadedDate;
		this.invoiceType = invoiceType;
		this.storeId = storeId;
		this.filePath = filePath;
		this.status = status;
	}

	/**
	 * Generic query helper for entity Invoice with id Long
	 */
	public static Finder<Long, Invoice> find = new Finder<Long, Invoice>(
			Long.class, Invoice.class);

	public static List<Invoice> all() {
		Logger.info("@M Invoice -->> all() -->>");
		return find.all();
	}

	// Create new Invoice
	public static void create(String fileName, Date uploadedDate,
			String invoiceType, Long storeId, String filePath, String status) {

		Invoice invoice = new Invoice(fileName, uploadedDate, invoiceType,
				storeId, filePath, status);
		invoice.save();

	}

	// update the corresponding Invoice in DB if any File is Replaced ,While
	// Uploading with same name
	public static void updateForFileReplace(Long id, String fileName,
			Date uploadedDate, String invoiceType, Long storeId,
			String filePath, String status) {

		Invoice invoice = Invoice.find.byId(id);

		// update the existing Invoice with new Details
		invoice.fileName = fileName;
		invoice.uploadedDate = uploadedDate;
		invoice.invoiceType = invoiceType;
		invoice.storeId = storeId;
		invoice.filePath = filePath;
		invoice.status = status;
		invoice.update();

	}

	// Updating status while Moving file,When done Process by admin module
	public static void updateStatus(Long id, Date processedDate,
			Date invoiceDate, String paymentTerms, String status,
			String filePath, Long cid) {

		Invoice invoice = Invoice.find.byId(id);

		// changing the status of invoice in DB & update Invoice
		invoice.processedDate = new Date();
		invoice.invoiceDate = Application.getDate(invoiceDate);
		invoice.paymentMode = paymentTerms;
		invoice.status = status;
		invoice.filePath = filePath;
		invoice.companyId = cid;
		invoice.update();
	}

	// Updating status while Moving file,When done Process by admin module
	public static void updateStatus(Long id, Date processedDate,
			Date invoiceDate, String paymentTerms, String status,
			String filePath) {

		Invoice invoice = Invoice.find.byId(id);

		// changing the status of invoice in DB & update Invoice
		invoice.processedDate = new Date();
		invoice.invoiceDate = Application.getDate(invoiceDate);
		invoice.paymentMode = paymentTerms;
		invoice.status = status;
		invoice.filePath = filePath;
		invoice.update();

	}

	public static void updateStatusForSupplierInvoice(Long id,
			Date processedDate, Date invoiceDate, String paymentTerms,
			String status, String filePath, Long supplierId) {

		Invoice invoice = Invoice.find.byId(id);

		// changing the status of invoice in DB & update Invoice
		invoice.processedDate = new Date();
		invoice.invoiceDate = Application.getDate(invoiceDate);
		invoice.paymentMode = paymentTerms;
		invoice.status = status;
		invoice.filePath = filePath;
		invoice.supplierId = supplierId;
		invoice.update();

	}

	// change the status and filepath
	public static void updateStatus(Long id, String status, String filePath) {

		Invoice invoice = Invoice.find.byId(id);

		invoice.status = status;
		invoice.filePath = filePath;
		invoice.update();

	}

	public static void delete(Long id) {
		Logger.info("@M Invoice -->> delete(" + id + ") -->>");
		find.ref(id).delete();
		Logger.info("@M Invoice -->> delete(" + id + ") <<--");
	}

	// Get list of Invoices based on Store and invoice type
	public static List<Invoice> page(Long storeId, String invoiceType,
			String status) {

		return (find.where().eq("storeId", storeId)
				.eq("invoiceType", invoiceType).eq("status", status))
				.findList();
	}

	// Get list of Invoices based on company and payment terms
	public static List<Invoice> page(Long cid, Long storeId,
			String paymentTerms, String status) {

		return (find.where().eq("companyId", cid).eq("storeId", storeId)
				.eq("paymentMode", paymentTerms).eq("status", status))
				.findList();
	}

	// Get list of Invoices based on company and payment terms and supplier ,
	// supplier search criteria
	public static List<Invoice> page(Long cid, Long storeId,
			String paymentTerms, String status, Long supplierId) {

		return (find.where().eq("companyId", cid).eq("supplierId", supplierId)
				.eq("storeId", storeId).eq("paymentMode", paymentTerms).eq(
				"status", status)).findList();
	}

	// Get list of Invoices based on store and payment terms
	public static List<Invoice> page(String paymentTerms, Long storeId,
			String status) {

		return (find.where().eq("storeId", storeId)
				.eq("paymentMode", paymentTerms).eq("status", status)).orderBy(
				"paymentMode asc").findList();
	}

	// Get list of Invoices based on store and payment terms and supplier .,
	// Supplier search criteria
	public static List<Invoice> page(String paymentTerms, Long storeId,
			String status, Long supplierId) {

		return (find.where().eq("storeId", storeId)
				.eq("supplierId", supplierId).eq("paymentMode", paymentTerms)
				.eq("status", status)).orderBy("paymentMode asc").findList();
	}

	// Get list of Invoices based on store and other than Fuel and all payment
	// terms
	public static List<Invoice> pageNEFuel(String paymentTerms, Long storeId,
			String status) {

		return (find.where().eq("storeId", storeId)
				.ne("paymentMode", paymentTerms).eq("status", status)).orderBy(
				"paymentMode asc").findList();
	}

	// Get list of Invoices based on store and other than Fuel and all payment
	// terms and supplier id . supplier search criteria
	public static List<Invoice> pageNEFuel(String paymentTerms, Long storeId,
			String status, Long supplierId) {

		return (find.where().eq("storeId", storeId)
				.eq("supplierId", supplierId).ne("paymentMode", paymentTerms)
				.eq("status", status)).orderBy("paymentMode asc").findList();
	}

	// get Parent Directory name of a given file path
	public static String getParentDirectory(String filePath) {

		File file = new File(System.getenv("INVOICE_HOME") + filePath);
		String parentFolder = file.getParentFile().getName();
		return parentFolder;
	}

	// checking whether file is Existing while Uploading , File copy will do
	// Replace But we are only take care of creating new record for that Replace
	// File,If not Exists Create new Record
	public static void checkFileExists(Long sid, String invoiceType,
			String fileName, String status, String filePathString) {

		Invoice invoice = (find.where().eq("storeId", sid)
				.eq("invoiceType", invoiceType).eq("fileName", fileName).eq(
				"status", status)).findUnique();

		// check whether Invoice exits or not,If exists Update otherwise Create
		// New One
		if (invoice != null) {
			Invoice.updateForFileReplace(invoice.id, fileName, new Date(),
					invoiceType, sid, filePathString, "UPLOADED");
		} else {
			Invoice.create(fileName, new Date(), invoiceType, sid,
					filePathString, "UPLOADED");
		}
	}

	// used to find pending Invoices For single Store
	public static int pendingInvoicesByStore(Long sid) {

		int count = 0;
		String[] invoiceType = { "CashPaid", "Others", "Statements", "Fuel" };
		for (String type : invoiceType) {
			int countByStoreAndType = Invoice.pendingInvoicesByStoreAndType(
					sid, type);
			count = count + countByStoreAndType;
		}

		return count;
	}

	// used to find pending Invoices For All Stores
	public static int pendingInvoicesByAllStores() {

		int count = 0;
		List<Store> storeList = Stores.getStoresList();
		for (Store store : storeList) {

			int countByStore = Invoice.pendingInvoicesByStore(store.id);
			count = count + countByStore;
		}

		return count;
	}

	// used to find pending Invoices for Store and invoiceType
	public static int pendingInvoicesByStoreAndType(Long sid, String invoiceType) {

		int count = 0;

		List<Invoice> invoiceList = Invoice.page(sid, invoiceType, "UPLOADED");
		count = count + invoiceList.size();

		return count;
	}

	// get Processed invoices
	public static int processedInvoicesByStore(Long sid) {

		int count = 0;

		List<Invoice> invoiceList = Invoice.page(sid, "PROCESSED");
		count = count + invoiceList.size();

		return count;
	}

	public static int processedInvoicesByAllStores() {

		int count = 0;
		List<Store> storeList = Stores.getStoresList();
		for (Store store : storeList) {
			if (store.id != 1) { // store id 1 related Company Invoices,hence we
									// are Not counting it
				int countByStore = Invoice.processedInvoicesByStore(store.id);
				count = count + countByStore;
			}

		}

		return count;
	}

	// Get list of Invoices based on Store and invoice type
	public static List<Invoice> page(Long storeId, String status) {

		return (find.where().eq("storeId", storeId).eq("status", status))
				.findList();
	}

	// get Invoice by store id and Uploaded Date
	public static List<Invoice> page(Long storeId, Date date) {

		return (find.where().eq("storeId", storeId)).ge("uploadedDate", date)
				.lt("uploadedDate", Application.getNextDate(date, 1))
				.orderBy("uploadedDate asc").findList();
	}

	// get Invoice by store id and Uploaded Date between Range of Dates
	public static List<Invoice> page(Long storeId, Date date, Date endDate) {

		return (find.where().eq("storeId", storeId)).ge("uploadedDate", date)
				.lt("uploadedDate", Application.getNextDate(endDate, 1))
				.orderBy("uploadedDate asc").findList();
	}

	// get Processed invoices By company
	public static int processedInvoicesByCompany(Long cid) {

		int count = 0;

		List<Invoice> invoiceList = Invoice.page("PROCESSED", cid);
		count = count + invoiceList.size();

		return count;
	}

	public static int processedInvoicesByAllCompanies() {

		int count = 0;
		List<Company> companyList = Company.all();
		for (Company company : companyList) {

			int countByCompany = Invoice.processedInvoicesByCompany(company.id);
			count = count + countByCompany;
		}

		return count;
	}

	// Get list of Invoices based on Store and invoice type
	public static List<Invoice> page(String status, Long cid) {

		return (find.where().eq("companyId", cid).eq("status", status))
				.findList();
	}

	public static List<Invoice> getInvoiceListBySupplierName(
			String supplierName, Long sid,String invoiceStatus) {
		ExpressionList<Supplier> l = Supplier
				.findWithSupplierName(supplierName);
		List<Object> sd = new ArrayList<Object>();
		sd = l.findIds();
		Logger.info("@C Invoice------------ -->> getInvoiceListBySupplierName(Total Suppliers List Size :"
				+ sd.size() + ")");
		Logger.info("@C Invoice------------ -->> getInvoiceListBySupplierName(ID'S OF Suppliers in Database ::"
				+ l.findIds() + ")");
		List<Invoice> sl = Invoice.getTotalSuppliersList(sd, sid,invoiceStatus);

		return sl;

	}

	public static List<Invoice> getTotalSuppliersList(List<Object> sd, Long sid,String invoiceStatus) {
		List<Invoice> ids = new ArrayList<Invoice>();
		List<Invoice> allIds = new ArrayList<Invoice>();
		System.out.println("Invoice status=============="+invoiceStatus);
		for (int i = 0; i < sd.size(); i++) {
			if(invoiceStatus.equals("ALL")){
				ids = find.where().eq("supplier_id", sd.get(i)).eq("store_id", sid)
						.orderBy("uploaded_date desc").findList();
				
			}
			else{
			ids = find.where().eq("supplier_id", sd.get(i)).eq("store_id", sid).eq("status", invoiceStatus)
					.orderBy("uploaded_date desc").findList();
			}
			Logger.info("@C Invoice------------ -->> getTotalSuppliersList(ids list size in for loop :"
					+ ids.size() + ")");
			allIds.addAll(ids);
			Logger.info("@C Invoice------------ -->> getTotalSuppliersList(all Ids List Size in for loop :"
					+ allIds.size() + ")");

		}
		Logger.info("@C Invoice------------ -->> getTotalSuppliersList(all Ids List Size in out of for loop :"
				+ allIds.size() + ")");

		return allIds;
	}
	public static List<Invoice> page(Long storeId, Date date, Date endDate,
			String invoiceStatus) {
		List<Invoice> l = new ArrayList<Invoice>();
		if (invoiceStatus.equals("ALL")) {
			System.out.println("inside all invoice status");
			l = (find.where().eq("storeId", storeId)).ge("uploadedDate", date)
					.lt("uploadedDate", Application.getNextDate(endDate, 1))
					.orderBy("uploadedDate asc").findList();
		} else {
			System.out.println("inside invoice status other  than all");

			l = (find.where().eq("storeId", storeId)).ge("uploadedDate", date)
					.lt("uploadedDate", Application.getNextDate(endDate, 1))
					.eq("status", invoiceStatus).orderBy("uploadedDate asc")
					.findList();
		}

		return l;
	}
}
