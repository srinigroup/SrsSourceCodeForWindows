package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import play.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonBackReference;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity 
public class Store extends Model{
	
	 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;
	  
	@Constraints.Required
	 public String name;
	
		
	public String description;
	
	// to hold status ACTIVE||DELETED
	public String status;
	
	 @OneToOne 
	 @JsonBackReference
	 public Address address;
	 
	 @OneToOne 
	 public ContactInfo contactInfo;
	 
	 @ManyToMany(cascade = CascadeType.REMOVE)
	 public List<Employee> employees = new ArrayList<Employee>(); 
	 
	
	   
	 
	  @ManyToOne
	  public Company company;
	  
	  @ManyToMany
	  public List<SalesHead> salesHeads = new ArrayList<SalesHead>();
	  
	  @ManyToMany
	  public List<MediaTender> mediaTenderHeads = new ArrayList<MediaTender>();
	  
	  @ManyToMany
	  public List<PaymentTender> paymentTenders = new ArrayList<PaymentTender>();
	  
	  @ManyToMany
	  public List<ExpenseHead> expenseHeads = new ArrayList<ExpenseHead>();
	  
	  @ManyToMany
	  public List<TotalSalesHead> totalSalesHeads = new ArrayList<TotalSalesHead>();
	  
	  @ManyToMany
	  public List<TerminalHead> terminalHeads = new ArrayList<TerminalHead>();
	  @ManyToMany
	  public List<Supplier> suppliers = new ArrayList<Supplier>();
	  
	  //new filed for SupplierMapping object
		 @OneToMany(cascade = CascadeType.ALL)
		  public List<SupplierMapping> supplierMapping = new ArrayList<SupplierMapping>(); 
	  
	 
	  public Double cashInSafe=0.00;
	  
	 
	  public Double chequesInSafe=0.00;
	  
	    public Store(String name, String description, SalesHead salesHead,TerminalHead terminalHead,TotalSalesHead totalSalesHead, PaymentTender paymentTender) {
	        Logger.info("@M Store -->> Store("+name+","+description+",salesHead,terminalHead,totalSalesHead,paymentTender) -->> Constructor ");
	    	this.name = name;
	        this.description = description;
	        this.terminalHeads.add(terminalHead);
	        this.salesHeads.add(salesHead);
	        this.totalSalesHeads.add(totalSalesHead);
	        this.paymentTenders.add(paymentTender);
	    }
	    
	  public Store(String name, String description,  Address address,ContactInfo contactInfo,Employee emp,Company company,TerminalHead terminalHead,TotalSalesHead totalSalesHead, PaymentTender paymentTender) {
		  Logger.info("@M Store -->> Store("+name+","+description+",address,contactInfo,employee,company,terminalHead,totalSalesHead,paymentTender) -->> Constructor ");  
		  this.name = name;
	        this.description = description;
	        this.address = address;
	        this.contactInfo = contactInfo;
	        this.company = company;
	        this.employees.add(emp);
	        this.terminalHeads.add(terminalHead);
	        this.totalSalesHeads.add(totalSalesHead);
	        this.paymentTenders.add(paymentTender);
	        
	    }
	  
	  public Store(String name, String description,ContactInfo contactInfo, Address address,Company company,List<SalesHead> salesHeads,
			  							List<MediaTender> mediaTenderHeads,List<ExpenseHead> expenseHeads,List<TerminalHead> terminalHeads,List<TotalSalesHead> totalSalesHead,List<PaymentTender> paymentTenderList) {
		  Logger.info("@M Store -->> Store("+name+","+description+",address,contactInfo,company,salesHeadsList,mediaTenderHeadsList,expenseHeadsList,terminalHeadsList,totalSalesHeadList,paymentTenderList) -->> Constructor ");
	        this.name = name;
	        this.description = description;
	        this.address = address;
	        this.contactInfo = contactInfo;
	        this.company = company;
	        this.salesHeads = salesHeads;
	        this.mediaTenderHeads = mediaTenderHeads;
	        this.expenseHeads = expenseHeads;
	        this.terminalHeads= terminalHeads;
	        this.totalSalesHeads = totalSalesHead;
	        this.paymentTenders = paymentTenderList;
	    }
	  
	  public Store(String name, String description,ContactInfo contactInfo, Address address,TerminalHead terminalHead,TotalSalesHead totalSalesHead) {
		  Logger.info("@M Store -->> Store("+name+","+description+",contactInfo,address,terminalHead,totalSalesHead) -->> Constructor ");
		  	this.name = name;
	        this.description = description;
	        this.address = address;
	        this.contactInfo = contactInfo;
	        this.terminalHeads.add(terminalHead);
	        this.totalSalesHeads.add(totalSalesHead);
	        
	    }
	  /**
	     * Create a new Store.
	     */
	    public static Store create(String name, String description,ContactInfo contactInfo,Address address,Company company,List<SalesHead> salesHeads,
	    		List<MediaTender> mediaTenderHeads,List<ExpenseHead> expenseHeads,List<TerminalHead> terminalHeads,List<TotalSalesHead> totalSalesHeads,List<PaymentTender> paymentTenderList) {
	    	Logger.info("@M Store -->> create("+name+","+description+",contactInfo,Address,company,salesHeadsList,mediaTenderHeadsList,expenseHeadsList,suppliersList,terminalHeadsList,totalSalesHeadList,paymentTenderList) -->> ");
	    	Store store = new Store(name, description,contactInfo,address,company,salesHeads,mediaTenderHeads,expenseHeads, terminalHeads, totalSalesHeads, paymentTenderList);
	    	store.save();
	    	store.status="ACTIVE";
	    	store.save();
	    	Logger.info("@M Store -->> create("+name+","+description+",contactInfo,Address,company,salesHeadsList,mediaTenderHeadsList,expenseHeadsList,suppliersList,terminalHeadsList,totalSalesHeadList,paymentTenderList) <<--");
	    	
	        return store;
	    }

	    /**
	     * Create a new Store.
	     */
	    public static Store create(String name, String description, ContactInfo contactInfo,Address address,TerminalHead terminalHead,TotalSalesHead totalSalesHead) {
	    	 Logger.info("@M Store -->> create("+name+","+description+",contactInfo,address,terminalHead,totalSalesHead) -->> ");
	    	Store store = new Store(name, description,contactInfo,address,terminalHead, totalSalesHead);
	    	store.save();
	    	Logger.info("@M Store -->> create("+name+","+description+",contactInfo,address,terminalHead,totalSalesHead) <<--");
	        return store;
	    }
	    
	    public static Store create(String name, String description, SalesHead salesHead,TerminalHead terminalHead,TotalSalesHead totalSalesHead, PaymentTender paymentTender) {
	    	Logger.info("@M Store -->> create("+name+","+description+",salesHead,terminalHead,totalSalesHead,paymentTender) -->> ");
	    	Store store = new Store(name, description, salesHead,terminalHead, totalSalesHead, paymentTender);
	    	store.save();
	    	Logger.info("@M Store -->> create("+name+","+description+",salesHead,terminalHead,totalSalesHead,paymentTender) <<--");
	        return store;
	    
	    }

	    /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,Store> find = new Finder<Long,Store>(Long.class, Store.class); 
	    
	 	public static List<Store> all() {
	 		Logger.info("@M Store -->> all() -->> ");
		  return find.all();
		}

		public static void create(Store store) {
			Logger.info("@M Store -->> create(store) -->> ");
			store.save();
			Logger.info("@M Store -->> create(store) <<--");
		}

		public static Store create(String name, String description, Address address,ContactInfo contactInfo,Employee emp,Long id,TerminalHead terminalHead,TotalSalesHead totalSalesHead,PaymentTender paymentTender) {
			 Logger.info("@M Store -->> create("+name+","+description+",address,contactInfo,employee,"+id+",terminalHead,totalSalesHead,paymentTender) -->> ");
			Store store = new Store(name,description, address,contactInfo,emp,Company.find.ref(id),terminalHead, totalSalesHead, paymentTender);
			store.save();
			store.saveManyToManyAssociations("employees");
			Logger.info("@M Store -->> create("+name+","+description+",address,contactInfo,employee,"+id+",terminalHead,totalSalesHead,paymentTender) <<--");
		        return store;
		}
		public static void delete(Long id) {
			Logger.info("@M Store -->> delete("+id+") -->> ");
		  
			Store store = Store.find.byId(id);
			store.status = "DELETED";
			store.update();
			
		  Logger.info("@M Store -->> delete("+id+") <<--");
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
	    public static Page<Store> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M Store -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                .orderBy(sortBy + " " + order)  
	                .fetch("company")
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
	    
	    public static Map<String,String> options() {
	    	Logger.info("@M Store -->> options() -->> ");
	        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
	        for(Store c: Store.find.where().eq("status", "ACTIVE").orderBy("name").findList()) {
	            options.put(c.id.toString(), c.name);
	        }
	        Logger.info("@M Store -->> options() <<--");
	        return options;
	    }
	 public String toString() {  
		  return name;
	  } 
	 public static Store findByStore(String name){
			for(Store holder :Store.all()){
				if(holder.name.equals(name)){
					return holder;
				}
			}
			return null;
		}
	
	 public static ExpressionList<Store> findWithStore(String sortBy,int page,int pageSize,String order,String storeId){
		 			
			return find.where().eq("status", "ACTIVE")
					.eq("id", storeId + "%");
		}
		
}
