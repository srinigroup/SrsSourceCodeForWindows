package controllers;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static play.data.Form.form;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.Employee;
import models.Invoice;
import models.MonthlyReport;
import models.Store;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.invoiceInventory.dateSelectForInvoiceViewForSK;
import views.html.monthlyReports.*;
import views.html.*;

@Security.Authenticated(Secured.class)
public class MonthlyReports extends Controller {

	//Starting Folder or Root Folder path, instead of using entire path every where use this variable
	
		//private static String  basePath = Play.application().path().getAbsolutePath(); // for play
		//private static String  basePath = System.getenv("CATALINA_HOME") +"//webapps//SRSFiles"; // for tomcat ,within webapps
		private static String  basePath = System.getenv("INVOICE_HOME"); // for tomcat other than webapps folder,D Drive
		
		public static Result showUploadPage() {
			
			Logger.info("@C MonthlyReports -->> showUploadPage() -->> ");
			String email = session("email");
			Employee employee = Employee.findByEmail(email);
			Store store = employee.store;
			
			Logger.info("@C MonthlyReports -->> showUploadPage() <<-- ");
			return ok(showUploadPage.render(store.id)); 
		}
		
		public static Result uploadReports(Long sid){
			
			Logger.info("@C MonthlyReports -->> uploadReports(store id: "+sid+") -->> ");
			
			play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
			
			// get ReportType from select Box
			String reportType = body.asFormUrlEncoded().get("reportType")[0];
			int month = Integer.parseInt(body.asFormUrlEncoded().get("reportingMonth")[0]);
			int year = Integer.parseInt(body.asFormUrlEncoded().get("reportingYear")[0]);
			String monthFromInt = Application.getMonthForInt(month);
			
			Store store = Store.find.byId(sid);
			
			// path to Upload Files
			//File ftemp= new File(basePath +"//public//InvoiceInventory//"+store.name+"//SHOP//"+invoiceType+"//"); for play
			File ftemp= new File(basePath +"//Monthly Reports//"+store.name+"//"+reportType+"//"); // for tomcat
			ftemp.mkdirs();
			
	        List<FilePart> fileList = body.getFiles();
	        for(FilePart p: fileList){
	        	
	        	Long lastId ; // to make unique file name,append record id to file name. This lastId is required
	        	
	        	if(MonthlyReport.find.findIds().size() == 0){ // for first record list gives size zero, if we reduce -1 from it we will get AIOOB Exception
	        		
	        		lastId =0l ; // we are adding +1 to lastId to obtain new id in file name formation
	        	}else{
	        		
	        		lastId = (Long)MonthlyReport.find.findIds().get(MonthlyReport.find.findIds().size()-1);
	        	}
	        	
	        	String modifiedFileName = MonthlyReport.getModifiedFileName(p.getFilename(), sid, lastId+1 , monthFromInt, reportType, year); // form the name with store id and date
	        	
	        	File f1 = new File(ftemp.getAbsolutePath() +"//"+modifiedFileName);
	        	File  file= p.getFile();
	        	
	        	file.setWritable(true);
	        	file.setReadable(true);
	        	f1.setWritable(true);
	        	f1.setReadable(true);
	        	
	        	try{ 
	     		   Files.copy(file.toPath(), f1.toPath(), REPLACE_EXISTING);
	     		  String filePathString ="Monthly Reports/"+store.name+"/"+reportType+"/"+modifiedFileName;
	     		  //save the record in DB
	     		 MonthlyReport.create(modifiedFileName,reportType,store.id,filePathString,monthFromInt);
	     		  
	     		 Logger.debug("@C MonthlyReports -->> uploadReports(store id: "+sid+") -->> File path "+filePathString);
	     		 
	     		// set flash to indicate successful Upload done 
	          	flash("success", fileList.size()+" File(s) Has been Successfully Uploaded");
	              }
	            catch(Exception e){
	            	
	            	Logger.error("@C MonthlyReports -->> uploadReports(store id: "+sid+") -->> ",e);
	         	   e.printStackTrace();
	              }
	        }
	        
	        Logger.info("@C MonthlyReports -->> uploadReports(store id: "+sid+") <<-- Redirecting to Upload page");
			// return to upload page
	        return redirect(
		            routes.MonthlyReports.showUploadPage()
		        );
		}
		
		// to show upload Reports page for sk. Month select page rendering  
		public static Result showUploadedReports(Long sid){
			
			Logger.info("@C MonthlyReports -->> showUploadedReports(sid :"+sid+") -->> ");
			List<MonthlyReport> reportList = new ArrayList<MonthlyReport>();
			Integer year = null;
			
			// by default give current month Reports 
			int month = Calendar.getInstance().get(Calendar.MONTH);
			reportList = MonthlyReport.page(sid, Application.getMonthForInt(month));
			
			return ok(showUploadedReports.render(sid,reportList,month,year));
		}
		
		// display Uploaded Invoice for SK
		public static Result displayUploaded(Long sid){
			
			Logger.info("@C MonthlyReports -->> displayUploaded(sid :"+sid+") -->> ");
			
			int month = Integer.parseInt(form().bindFromRequest().get("reportingMonth"));
			int year = Integer.parseInt(form().bindFromRequest().get("reportingYear"));
			
			List<MonthlyReport> reportList = new ArrayList<MonthlyReport>();
			reportList = MonthlyReport.page(sid, Application.getMonthForInt(month),year);
			
			return ok(showUploadedReports.render(sid,reportList,month,year));
			
		}
		
		// to display select page to get  uploaded reports to head office
		public static Result viewUploadedReports(){
			
			Logger.info("@C MonthlyReports -->> viewUploadedReports() -->> ");
			
			Long sid = null;
			Integer year = null;
			List<MonthlyReport> reportList = new ArrayList<MonthlyReport>();
			int month = Calendar.getInstance().get(Calendar.MONTH);
					
			Logger.info("@C MonthlyReports -->> viewUploadedReports() <<--");
			return ok(viewUploadedReports.render(sid,reportList,month,year));
		}
		
		public static Result getUploadedReports(){
			
			Logger.info("@C MonthlyReports -->> getUploadedReports() -->> ");
			
			Long sid = Long.parseLong(form().bindFromRequest().get("storeId"));
			int month = Integer.parseInt(form().bindFromRequest().get("reportingMonth"));
			int year = Integer.parseInt(form().bindFromRequest().get("reportingYear"));
			List<MonthlyReport> reportList = new ArrayList<MonthlyReport>();
			reportList = MonthlyReport.page(sid, Application.getMonthForInt(month), year);
			
			Logger.info("@C MonthlyReports -->> getUploadedReports() <<--");
			return ok(viewUploadedReports.render(sid,reportList,month,year));
		}
		
		
}
