package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.format.Formats;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class HoForm extends Model {

	private static final long serialVersionUID = 1L;
	@Id
	public Long id;

	public String fileName;

	public String filePath;

	public String formType;
	@Formats.DateTime(pattern = "dd/MM/yyyy hh:mm:ss")
	public Date uploadedDate;

	public HoForm(String fileName, Date uploadedDate, String formType,
			String filePath) {
		this.fileName = fileName;
		this.uploadedDate = uploadedDate;
		this.formType = formType;
		this.filePath = filePath;
	}

	public static Finder<Long, HoForm> find = new Finder<Long, HoForm>(
			Long.class, HoForm.class);

	public static void create(String fileName, Date uploadedDate,
			String formType, String filePath) {

		HoForm hoForm = new HoForm(fileName, uploadedDate, formType, filePath);
		hoForm.save();
	}

	public static List<HoForm> getAllHeadOfficeForms(){
		return(find.all()
				);
	}
}
