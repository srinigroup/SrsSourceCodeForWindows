package models;
import java.util.List;

import javax.persistence.*; 

import com.avaje.ebean.Page;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity 
public class Product extends Model { 
  
  private static final long serialVersionUID = 1L;
  @Id       
  public Long id; 
  
  @Constraints.Required
  public Long ean;
  
  @Constraints.Required
  public String name;
  public String description; 
   
  public static List<Product> all() {
	  return find.all();
	}

	public static void create(Product product) {
		product.save();
	}

	public static void delete(Long id) {
	  find.ref(id).delete();
	}
	
	 /**
     * Generic query helper for entity Product with id Long
     */
    public static Finder<Long,Product> find = new Finder<Long,Product>(Long.class, Product.class); 
    
	
	 /**
     * Return a page of computer
     *
     * @param page Page to display
     * @param pageSize Number of computers per page
     * @param sortBy Product property used for sorting
     * @param order Sort order (either or asc or desc)
     * @param filter Filter applied on the name column
     */
    public static Page<Product> page(int page, int pageSize, String sortBy, String order, String filter) {
        return 
            find.where()
                .ilike("name", "%" + filter + "%")
                .orderBy(sortBy + " " + order)                
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }
  public String toString() {  
	  return name;
  } 
}

