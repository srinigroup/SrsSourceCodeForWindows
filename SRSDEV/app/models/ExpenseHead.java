package models;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import play.Logger;

import java.util.Map;

import play.data.format.Formats;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.data.validation.Constraints;
import play.db.ebean.Model;



@Entity 
public class ExpenseHead extends Model {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	@Constraints.Required
	  public String name;
	
	
	  public String category;
	  
	// to hold status ACTIVE||DELETED
	public String status;
	 
	  @Formats.DateTime(pattern="dd/MM/yyyy")
	    public Date created_date;
	
	  
	  @Formats.DateTime(pattern="dd/MM/yyyy")
	    public Date modified_date;
	
	 	 
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Model.Finder<Long,ExpenseHead> find = new Model.Finder<Long,ExpenseHead>(Long.class, ExpenseHead.class); 
	    
	 	public static List<ExpenseHead> all() {
	 		Logger.info("@M ExpenseHead -->> all() -->> "); 
		  return find.all();
		}

		public static void create(ExpenseHead expensehead) {
			Logger.info("@M ExpenseHead -->> create(expensehead) -->> ");
			
			expensehead.created_date=new Date();
			expensehead.save();
			expensehead.status = "ACTIVE";
			expensehead.save();
			
			Logger.info("@M ExpenseHead -->> create(expensehead) <<--");
		}

		public static void delete(Long id) {
			Logger.info("@M ExpenseHead -->> delete("+id+") -->> ");
		  
			ExpenseHead expenseHead = ExpenseHead.find.byId(id);
			expenseHead.status= "DELETED";
			expenseHead.update();
			
		  Logger.info("@M ExpenseHead -->> delete("+id+") <<--");
		}
		public static List<ExpenseHead> getExpenseHeadList(String[] ids) {
			Logger.info("@M ExpenseHead -->> getExpenseHeadList() -->> ");
	 		List<ExpenseHead> expenseHeadList = new ArrayList<ExpenseHead>();
	 		
	 		  for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			ExpenseHead expenseHead = ExpenseHead.find.byId(id);
	 		 
	 		    	expenseHeadList.add(expenseHead);
	 		    }
	 		 Logger.info("@M ExpenseHead -->> getExpenseHeadList() <<--");
			  return expenseHeadList;
			}
		
		 public static Map<String,String> options() {
			 Logger.info("@M ExpenseHead -->> options() -->> "); 
		        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
		        for(ExpenseHead c: ExpenseHead.find.where().eq("status", "ACTIVE").orderBy("name").findList()) {
		            options.put(c.id.toString(), c.name);
		        }
		        Logger.info("@M ExpenseHead -->> options() <<--");
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
	    public static Page<ExpenseHead> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M ExpenseHead -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
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
			  return id+": "+name;
		  }
}

