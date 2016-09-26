package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.avaje.ebean.Page;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.*;

/**
 * 
 * class used for Supplier Payment Terms and Payment Mode
 * @author Gopi
 *
 */
 @Entity 
public class SupplierMapping extends Model {
	
	private static final long serialVersionUID = 1L;
	
		// Fields
	
		@Id  
		public Long id;
		
		@Constraints.Required
		 public String paymentTerms;
		
		@Constraints.Required
		  public String paymentMode;
		
		@OneToOne
		public Supplier supplier;
		
		@ManyToOne(cascade=CascadeType.ALL)
		public Store store;
		
		
		public static Finder<Long,SupplierMapping> find = new Finder<Long,SupplierMapping>(Long.class, SupplierMapping.class);
		
		// default Constructor
		public SupplierMapping(){}
		
		// 2-param Constructor
		public SupplierMapping(String paymentTerms , String paymentMode){
			
			 Logger.info("@M SupplierMapping -->> SupplierMapping("+paymentTerms+","+paymentMode+") -->>constructor");
			 
			 this.paymentMode=paymentMode;
			 this.paymentTerms=paymentTerms;
			 
		}
		
		public static void create(String paymentTerms , String paymentMode , Supplier supplier , Store store) {
			
			Logger.info("@M SupplierMapping -->> create("+paymentTerms+","+paymentMode+",supplier,store) -->>");
			
			SupplierMapping supplierMapping=new SupplierMapping(paymentTerms,paymentMode);
			supplierMapping.supplier=supplier;
			supplierMapping.store=store;
			supplierMapping.save();
			
			Logger.info("@M SupplierMapping -->> create("+paymentTerms+","+paymentMode+"supplier,store) <<--");
		}
		
		
		// method used for ajax call in edit page of stores,To add supplier mappings
		public static SupplierMapping getSupplierMapping(Long storeId,Long suppid){
			
			
			  
			  Logger.info("@M SupplierMapping -->> getSupplierMapping("+storeId+","+suppid+") -->>");
			  
			  List<SupplierMapping> supplierMappings = new ArrayList<SupplierMapping>();
			
			  SupplierMapping supplierMapping = new SupplierMapping();
			 
			  
			  Supplier supplier = Supplier.find.byId(suppid);
			  
			  supplierMapping.supplier=supplier;
			  
			  Logger.debug("@M SupplierMapping -->> getSupplierMapping("+storeId+","+suppid+") -->> supplier :"+supplier.name);
			 
			  Store store = Store.find.byId(storeId);
			  supplierMapping.store = store;
			  supplierMapping.save();
			  supplierMappings = store.supplierMapping;
			  supplierMappings.add(supplierMapping);
			  store.supplierMapping = supplierMappings;
			  store.save();
			  
			  Logger.info("@M SupplierMapping -->> getSupplierMapping("+storeId+","+suppid+") <<--");
			  return supplierMapping;
		  }
		
		// page method used in getSupplier() in Suppliers Controller , to get List of SupplierMappings for a particular store with filter strkey value
		 public static List<SupplierMapping> getSupplierMappingList( String filter, Long storeId) {
		    	
		    	Logger.info("@M SupplierMapping -->> getSupplierMappingList("+filter+","+storeId+") -->> ");
		        return 
		            ( find.where()
		                .ilike("supplier.name", filter + "%")
		                .eq("store.id", storeId)
		                .findList());
		                
		    }
		
		
	
}
