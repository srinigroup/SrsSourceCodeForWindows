package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonBackReference;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.Logger;

@Entity 
public class Supplier extends Model{
	
	 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;
	  
	@Constraints.Required
	  public String name;
	
	
	@Constraints.Required
	public String abn;
	
	public String description;
	
	// to hold status ACTIVE||DELETED
	public String status;
	
	 @OneToOne 
	 @JsonBackReference
	 public Address address;
	 
	 @OneToOne 
	 public ContactInfo contactInfo;
	 
	//new filed for SupplierMapping object
	/*  @OneToMany(cascade = CascadeType.ALL)
	  public List<SupplierMapping> supplierMapping = new ArrayList<SupplierMapping>();  */
	 
	 
	  // constructor including abn
	  public Supplier(String name, String description, String abn, Address address,ContactInfo contactInfo) {
		  Logger.info("@M Supplier -->> Supplier("+name+","+description+","+abn+",address,contactInfo) -->>constructor");
	        this.name = name;
	        this.description = description;
	        this.abn=abn;
	        this.address = address;
	        this.contactInfo = contactInfo;
	       
	        
	    }
	 
	  //constructor without abn
	  public Supplier(String name, String description, Address address,ContactInfo contactInfo) {
		  Logger.info("@M Supplier -->> Supplier("+name+","+description+","+abn+",address,contactInfo) -->>constructor");
	        this.name = name;
	        this.description = description;
	        this.address = address;
	        this.contactInfo = contactInfo;
	       
	        
	    }
	  
	 
	  /**
	     * Create a new Store.
	     */
	  //added one extra param abn to this create method
	    public static Supplier create(String name, String description,String abn,ContactInfo contactInfo,Address address) {
	    	 Logger.info("@M Supplier -->> create("+name+","+description+","+abn+",address,contactInfo) -->>");
	    	Supplier supplier = new Supplier(name, description,abn,address,contactInfo);
	    	supplier.save();
	    	supplier.status = "ACTIVE";
	    	supplier.save();
	    	Logger.info("@M Supplier -->> create("+name+","+description+","+abn+",address,contactInfo) <<--");
	        return supplier;
	    }

	    
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,Supplier> find = new Finder<Long,Supplier>(Long.class, Supplier.class); 
	    
	 	public static List<Supplier> all() {
	 		 Logger.info("@M Supplier -->> all() -->>");
		  return find.all();
		}

		public static void create(Supplier supplier) {
			Logger.info("@M Supplier -->> create(supplier) -->>");
			supplier.save();
			Logger.info("@M Supplier -->> create(supplier) <<--");
		}

		public static Supplier create(String name, String description, Address address,ContactInfo contactInfo,Employee emp,Long id) {
			Logger.info("@M Supplier -->> create("+name+","+description+",address,contactInfo,emp,"+id+") -->> ");
			Supplier supplier = new Supplier(name,description, address,contactInfo);
			supplier.save();
			supplier.saveManyToManyAssociations("employees");
			Logger.info("@M Supplier -->> create("+name+","+description+",address,contactInfo,emp,"+id+") <<--");
		        return supplier;
		}
		
	public static List<Supplier> getSuppliersList(String[] ids) {
		
		Logger.info("@M Supplier -->> getSuppliersList("+ids+") -->> ");
	 		
	 		List<Supplier> suppliersList = new ArrayList<Supplier>();
	 	
	 		  for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			Supplier supplier = Supplier.find.byId(id);
	 		 
	 		    	suppliersList.add(supplier);
	 		    }
	 		 Logger.info("@M Supplier -->> getSuppliersList("+ids+") <<--");
			  return suppliersList;
			}
		public static void delete(Long id) {
			Logger.info("@M Supplier -->> delete("+id+") -->> ");
		  
			Supplier supplier = Supplier.find.byId(id);
			supplier.status="DELETED";
			supplier.update();
			
		  Logger.info("@M Supplier -->> delete("+id+") <<--");
		}
			  
	
	    
	    /**
	     * Return a page of Supplier
	     *
	     * @param page Page to display
	     * @param pageSize Number of computers per page
	     * @param sortBy Product property used for sorting
	     * @param order Sort order (either or asc or desc)
	     * @param filter Filter applied on the name column
	     */
	    public static Page<Supplier> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M Supplier -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                .orderBy(sortBy + " " + order)  
	                .fetch("contactInfo")
	                 .fetch("address")
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	    
	 
	    /**
	     * Return a page of Supplier
	     *
	     * @param page Page to display
	     * @param pageSize Number of computers per page
	     * @param sortBy Product property used for sorting
	     * @param order Sort order (either or asc or desc)
	     * @param filter Filter applied on the name column
	     */
	    public static Page<Supplier> getSuppliersList(int page, int pageSize, String sortBy, String order, String filter) {
	    	
	    	Logger.info("@M Supplier -->> getSuppliersList("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                .orderBy(sortBy + " " + order)  
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	    
	    public static List<Supplier> getSuppliersList(String filter) {
	    	
	    	Logger.info("@M Supplier -->> getSuppliersList("+filter+") -->> ");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                 .findList());
	    }
	    public static ExpressionList<Supplier> findWithSupplierName(String supplierName){
	    	Logger.info("@M Supplier -->> findWithSupplierName(Supplier Name :"+supplierName+") -->> ");
				
				return find.where().eq("status", "ACTIVE")
						.ilike("name", supplierName + "%");
				
			}
	    
	   
	 public String toString() {  
		  return name;
	  } 
}
