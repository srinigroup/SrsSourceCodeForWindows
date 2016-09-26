package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.Page;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import play.Logger;

@Entity 
public class Company extends Model{
	
	private static final long serialVersionUID = 1L;
	  @Id       
	  public Long id; 
	  
	  @Constraints.Required
	  public String name;
	  
	  @Constraints.Required
	  public String director;
	  
	  public String description;
	  
	  // to hold status ACTIVE||DELETED
	  public String status;
	
	 @OneToOne 
	 public Address address;
	 
	 @OneToOne 
	  public ContactInfo contactInfo;
	 

	 @ManyToMany(cascade = CascadeType.REMOVE)
	 public List<Employee> employees = new ArrayList<Employee>(); 
	
	 
	 @OneToMany      	 
	 public List<Store> stores= new ArrayList<Store>();  
	 
	 public Company() {
	       
	    }
	 
	 
	 public Company(String name, String description, String director, Address address,ContactInfo contactInfo,Employee emp,Store store) {
		 Logger.info("@M Company -->> Company("+name+","+description+","+director+",address,contactInfo,emp,store) -->> Constructor");
		 	this.name = name;
	        this.description = description;
	        this.director = director;
	        this.address = address;
	        this.contactInfo = contactInfo;
	       
	        this.employees.add(emp);
	        this.stores.add(store);
	    }
	 
	 public Company(String name, String description, String director, ContactInfo contactInfo) {
		 Logger.info("@M Company -->> Company("+name+","+description+","+director+",contactInfo) -->> constructor");
		 this.name = name;
	        this.description = description;
	        this.contactInfo = contactInfo;
	        this.director = director;
	    }
	
	 public Company(String name, String description, String director, ContactInfo contactInfo,Address address) {
		 Logger.info("@M Company -->> Company("+name+","+description+","+director+",contactInfo,address) -->> constructor");
	        this.name = name;
	        this.description = description;
	        this.director = director;
	        this.address = address;
	        this.contactInfo = contactInfo;
	       
	    }
	 	/**
	     * Create a new project.
	     */
	    public static Company create(String name, String description, String director,String email) {
	    	Logger.info("@M Company -->> create("+name+","+description+","+director+","+email+") -->>");
	    	Company company = new Company(name, description, director, ContactInfo.findByEmail(email));
	    	company.save();
	    	Logger.info("@M Company -->> create("+name+","+description+","+director+","+email+") <<--");
	        return company;
	    }
	    
	    /**
	     * Create a new project.
	     */
	    public static Company create(String name, String description,String director, ContactInfo contactInfo,Address address) {
	    	Logger.info("@M Company -->> create("+name+","+description+","+director+",contactInfo,address) -->>");
	    	Company company = new Company(name, description,director, contactInfo,address);
	    	company.save();
	    	company.status ="ACTIVE";
	    	company.save();
	    	Logger.info("@M Company -->> create("+name+","+description+","+director+",contactInfo,address) <<--");
	        return company;
	    }
	 	public static List<Company> all() {
	 		Logger.info("@M Company -->> all() -->>");
		  return find.all();
		}

		
		
		public static void create(Company company) {
			Logger.info("@M Company -->> create(company) -->>");
			company.save();
			Logger.info("@M Company -->> create(company) <<--");
		}
		
		
		  
		
		public static Company create(String name, String description,String director, Address address,ContactInfo contactInfo,Employee emp,Long id) {
			Logger.info("@M Company -->> create("+name+","+description+","+director+",address,contactInfo,emp,"+id+") -->>");
			Company company = new Company(name, description,director,address,contactInfo,emp,Store.find.ref(id));
			company.save();
			company.saveManyToManyAssociations("employees");
			Logger.info("@M Company -->> create("+name+","+description+","+director+",address,contactInfo,emp,"+id+") <<--");
		        return company;
		}
		public static void delete(Long id) {
			Logger.info("@M Company -->> delete("+id+") -->>");
			
		  Company company = Company.find.byId(id);
		  company.status="DELETED";
		  company.update();
		  
		  Logger.info("@M Company -->> delete("+id+") <<--");
		}
		
		 /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,Company> find = new Finder<Long,Company>(Long.class, Company.class); 
	    
	    public static Map<String,String> options() {
	    	Logger.info("@M Company -->> options() -->>");
	        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
	        for(Company c: Company.find.where().eq("status", "ACTIVE").orderBy("name").findList()) {
	            options.put(c.id.toString(), c.name);
	        }
	        Logger.info("@M Company -->> options() <<--");
	        return options;
	    }
	    
	    /**
	     * Return a page of computer
	     *
	     * @param page Page to display
	     * @param pageSize Number of computers per page
	     * @param sortBy Product property used for sorting
	     * @param order Sort order (either or asc or desc)
	     * @param filter Filter applied on the name column
	     */
	    public static Page<Company> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M Company -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                .orderBy(sortBy + " " + order)  
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	 public String toString() {  
		  return name;
	  } 
}