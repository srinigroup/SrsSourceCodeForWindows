package models;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Page;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.Logger;

@Entity 
public class MediaTender extends Model {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	@Constraints.Required
	  public String name;
	
	// to hold status ACTIVE||DELETED
	  public String status;
	
	  public String category;
	 
	 
	  /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,MediaTender> find = new Finder<Long,MediaTender>(Long.class, MediaTender.class); 
	    
	 	public static List<MediaTender> all() {
	 		Logger.info("@M MediaTender -->> all() -->> ");
		  return find.all();
		}

		public static void create(MediaTender mediaTender) {
			Logger.info("@M MediaTender -->> create(mediaTender) -->> ");
			mediaTender.save();
			mediaTender.status = "ACTIVE";
			mediaTender.save();
			Logger.info("@M MediaTender -->> create(mediaTender) <<--");
		}

		public static void delete(Long id) {
			Logger.info("@M MediaTender -->> delete("+id+") -->> ");
		  
			MediaTender mediaTender = MediaTender.find.byId(id);
			mediaTender.status="DELETED";
			mediaTender.update();
			
		  Logger.info("@M MediaTender -->> delete("+id+") <<--");
		}
		
		public static List<MediaTender> getMediaTenderList(String[] ids) {
			Logger.info("@M MediaTender -->> getMediaTenderList() -->> ");
	 		List<MediaTender> mediaTenderList = new ArrayList<MediaTender>();
	 		
	 		  for (int i=0;i<ids.length;i++){
	 			 Long.getLong(ids[i]);
	 			long id = Long.valueOf(ids[i]).longValue() ;
	 			MediaTender mediaTender = MediaTender.find.byId(id);
	 		  
	 		    	mediaTenderList.add(mediaTender);
	 		    }
	 		 Logger.info("@M MediaTender -->> getMediaTenderList() <<--");
			  return mediaTenderList;
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
	    public static Page<MediaTender> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M MediaTender -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	            	.eq("status", "ACTIVE")
	                .ilike("name", filter + "%")
	                .orderBy(sortBy + " " + order)  	                
	                .findPagingList(pageSize))	                
	                .setFetchAhead(true)
	                .getPage(page);
	    }
	    
	    public static List<MediaTender> getMediaTenderByStore(Long storeid) {
	    	Logger.info("@M AccountHolder -->> getAccountHolderbyStore("+storeid+") -->> ");
	        return 
	            ( find.where()
	                .orderBy("name")  
	                .findList());
	    }
	    
		public String toString() {  
			  return id+": "+name;
		  }
		 public static MediaTender getMediaName(Long salesHeadId){
				
				
				return 
		 	            ( find.where()
		 	                .eq("name", salesHeadId)
		 	                ).findUnique();
		 	                
		 	   
			}
}

