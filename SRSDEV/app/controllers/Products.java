package controllers;
import java.util.HashSet;
import java.util.List;
import java.util.Set;






import models.Product;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.products.*;
import static play.data.Form.*;

@Security.Authenticated(Secured.class)
public class Products extends Controller {

	/**
     * This result directly redirect to application home.
     */
    public static Result GO_HOME = redirect(
        routes.Products.list(0, "name", "asc", "")
    );
    
	private static final Form<Product> productForm = form(Product.class); 
	
	  
	public static Result showBlank(){ 
		
		flash("logout", "You've been logged out");
		return ok(show.render(productForm)); 
		}

  public static Result show(Long ean) {  
	  final Product product = Products.findByEan(ean);
	  if (product == null) {   
		  return notFound(String.format("Product %s does not exist.", ean)); 
		  }
	  Form<Product> filledForm = productForm.fill(product); 
	  return ok(show.render(filledForm));  
  }
  /**
   * Display the 'edit form' of a existing Computer.
   *
   * @param id Id of the computer to edit
   */
  public static Result edit(Long id) {
      Form<Product> productForm = form(Product.class).fill(
          Product.find.byId(id)
      );
      flash("logout", "You've been logged out");
      return ok(
          editForm.render(id, productForm)
      );
  }
  public static Result update(Long id) {
	 	 
	  Form<Product> productForm = form(Product.class).bindFromRequest();
      if(productForm.hasErrors()) {
          return badRequest(editForm.render(id, productForm));
      }
      productForm.get().update(id);
      flash("logout", "You've been logged out");
      flash("success", "Product " + productForm.get().name + " has been updated");
      return GO_HOME;

	  }
  public static Result save() {
	 	 
	  Form<Product> productForm = form(Product.class).bindFromRequest();
	  flash("logout", "You've been logged out");
      if(productForm.hasErrors()) {
          return badRequest(show.render(productForm));
      }
      productForm.get().save();
      
      flash("success", "Product " + productForm.get().name + " has been saved");
      return GO_HOME; 

	  }
 
  public static Product findByEan(Long ean) {
	  for (Product candidate : Product.all()) { 
		  if (candidate.ean.equals(ean)) {  
			  return candidate;  
			  }  
		  } 
	  return null; 
	  }
  
 
  
  public static List<Product> findByName(String term) { 
	  
	  for (Product candidate : Product.all()) {
		  if(candidate.name.toLowerCase().contains(term.toLowerCase())){ 
			  Product.create(candidate);  
			  } 
		  } 
    return Product.all();
    }
  public static Result delete(Long id) {
	  	Product.delete(id);
	  	flash("success", "Product has been deleted");
	  	flash("logout", "You've been logged out");
	  	return GO_HOME; 
	  }
 /*
  public static Result list() { 
		 return ok(list.render(Product.all()));
  }*/
  /**
   * Display the paginated list of computers.
   *
   * @param page Current page number (starts from 0)
   * @param sortBy Column to be sorted
   * @param order Sort order (either asc or desc)
   * @param filter Filter applied on computer names
   */
  public static Result list(int page, String sortBy, String order, String filter) {
	  flash("logout", "You've been logged out");
      return ok(
          list.render(
              Product.page(page, 10, sortBy, order, filter),
              sortBy, order, filter
          )
      );
  }
  

}