package models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.Id;
import play.Logger;
import com.avaje.ebean.Page;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;


@Entity 
public class TotalSalesHead extends Model {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	@Constraints.Required
	  public String name;
	
	
	  public String category;
	 
	  @Formats.DateTime(pattern="dd/MM/yy")
	    public Date created_date;
	
	  
	  @Formats.DateTime(pattern="dd/MM/yy")
	    public Date modified_date;
	
	 	 
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,TotalSalesHead> find = new Finder<Long,TotalSalesHead>(Long.class, TotalSalesHead.class); 
	    
	 	public static List<TotalSalesHead> all() {
	 		Logger.info("@M TotalSalesHead -->> all() -->> ");
		  return find.all();
		}
	 	
	 	public static List<TotalSalesHead> getTotalSalesHeadList(String[] ids) {
	 		Logger.info("@M TotalSalesHead -->> getTotalSalesHeadList() -->> ");
	 		List<TotalSalesHead> totalSalesHeadList = new ArrayList<TotalSalesHead>();
	 	
	 
	 		  for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			TotalSalesHead totalSalesHead = TotalSalesHead.find.byId(id);
	 	
	 			totalSalesHead.save();
	 			 	totalSalesHeadList.add(totalSalesHead);
	 		    }
	 		 Logger.info("@M TotalSalesHead -->> getTotalSalesHeadList() <<--");
			  return totalSalesHeadList;
			}
	 	
		public static void create(TotalSalesHead totalSalesHead) {
			Logger.info("@M TotalSalesHead -->> create(totalSalesHead) -->> ");
			totalSalesHead.created_date=new Date();
			totalSalesHead.save();
			Logger.info("@M TotalSalesHead -->> create(totalSalesHead) <<--");
		}

		public static void delete(Long id) {
			Logger.info("@M TotalSalesHead -->> delete("+id+") -->> ");
		  find.ref(id).delete();
		  Logger.info("@M TotalSalesHead -->> delete("+id+") <<--");
		}
		
		 /**
	     * Return a page of TotalSalesHead
	     *
	     * @param page Page to display
	     * @param pageSize Number of computers per page
	     * @param sortBy Product property used for sorting
	     * @param order Sort order (either or asc or desc)
	     * @param filter Filter applied on the name column
	     */
	    public static Page<TotalSalesHead> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M TotalSalesHead -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	                .ilike("name", "%" + filter + "%")
	                .orderBy(sortBy + " " + order)  	                
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }
		public String toString() {  
			  return id+": "+name;
		  }
}

