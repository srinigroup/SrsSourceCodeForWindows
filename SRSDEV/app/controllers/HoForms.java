package controllers;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.HoForm;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.headofficeforms.*;


public class HoForms extends Controller {

	public static Result showHoFormUploadPage() {

		return ok(showHoFormUploadPage.render());
	}

	public static Result uploadHoFormsByHeadOffice() throws Exception {

		Logger.info("@C HoForms -->> uploadHoFormsByHeadOffice() -->> ");
		//final String basePath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 8.0\\webapps\\pdfs\\";

		final String basePath = System.getenv("INVOICE_HOME");
		Date date = new Date();
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
		String startDate = format.format(date);
		date = format.parse(startDate);

		play.mvc.Http.MultipartFormData body = request().body()
				.asMultipartFormData(); // get Form Body
										// in DB
		String formType = body.asFormUrlEncoded().get("formType")[0];// get form
																		// type
																		// from
																		// select
																		// box.
																		// //
																		// type

		try {
			// path to Upload Files
			File ftemp = new File(basePath + "HeadOfficeForms\\" + formType
					+ "");// for play & tomcat also
			ftemp.mkdirs();
			List<FilePart> fileList = body.getFiles();
			//System.out.println("file no============== is"+fileList.size());

	        for(FilePart p: fileList){
    		//FilePart upFile = body.getFile("hoFormFiles");// get the file details
    		String fileName = p.getFilename();// get the file name
    		System.out.println("file name is :" + ftemp + "," + fileName + ","
					+ ftemp.getAbsolutePath());
			String filePathString = "HeadOfficeForms\\" + formType + "\\"
					+ fileName;

			File f1 = new File(ftemp.getAbsolutePath() + "//" + fileName);
			File file = p.getFile();

			file.setWritable(true);
			file.setReadable(true);
			f1.setWritable(true);
			f1.setReadable(true);
			Files.copy(file.toPath(), f1.toPath(), REPLACE_EXISTING);
			HoForm.create(fileName, date, formType, filePathString);
			
	        }
	        flash("success", fileList.size() + " Files Has been Successfully Uploaded");

		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.info("@C HoForms -->> uploadHoFormsByHeadOffice() <<-- Redirecting to Upload Page for Head Office");
		return redirect(routes.HoForms.showHoFormUploadPage());// return to
																// upload page
	}

	public static Result getAllHeadOfficeForms() {

		Logger.info("@C HoForms -->> getAllHeadOfficeForms() -->> ");
		List<HoForm> hoFormList = new ArrayList<HoForm>();
		hoFormList = HoForm.getAllHeadOfficeForms();
		 System.out.println("hoFormList size is=======> :" +
		hoFormList.size());
		return ok(list.render(hoFormList));
	}

}
