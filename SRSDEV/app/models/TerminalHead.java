package models;


import java.util.ArrayList;
import java.util.List;

import play.Logger;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;


@Entity 
public class TerminalHead extends Model {
	

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
	 
	 
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,TerminalHead> find = new Finder<Long,TerminalHead>(Long.class, TerminalHead.class); 
	    
	 	public static List<TerminalHead> all() {
	 		Logger.info("@M TerminalHead -->> all() -->> ");
		  return find.all();
		}

		public static void create(TerminalHead terminalHead) {
			Logger.info("@M TerminalHead -->> create(terminalHead) -->> ");
			
			terminalHead.save();
			terminalHead.status = "ACTIVE";
			terminalHead.save();
			
			Logger.info("@M TerminalHead -->> create(terminalHead) <<--");
		}

		public static void delete(Long id) {
			Logger.info("@M TerminalHead -->> delete("+id+") -->> ");
		  
			TerminalHead terminalHead = TerminalHead.find.byId(id);
			terminalHead.status="DELETED";
			terminalHead.update();
			
		  Logger.info("@M TerminalHead -->> delete("+id+") <<--");
		}
		
		public static List<TerminalHead> getTerminalHeadList(String[] ids) {
			Logger.info("@M TerminalHead -->> getTerminalHeadList() -->>");
	 		List<TerminalHead> terminalHeadList = new ArrayList<TerminalHead>();
	 		
	 		  for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			TerminalHead terminalHead = TerminalHead.find.byId(id);
	 		
	 			terminalHeadList.add(terminalHead);
	 		    }
	 		 Logger.info("@M TerminalHead -->> getTerminalHeadList() <<--");
			  return terminalHeadList;
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
	    public static Page<TerminalHead> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M TerminalHead -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->>");
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

		public static TerminalHead findbyName(String thead) {
			Logger.info("@M TerminalHead -->> findbyName("+thead+") -->>");
			 return 
			            ( find.where()
			                .eq("name", thead)).findUnique();
			
		}
}

