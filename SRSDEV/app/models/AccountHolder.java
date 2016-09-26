package models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.avaje.ebean.Page;

import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class AccountHolder extends Model {
	
	private static final long serialVersionUID = 1L;

	@Id 
	  public Long id;
	
	@Constraints.Required
	  public String accountHolder;
	
	
	@ManyToOne
	  public Store store;
	
	@OneToOne 
	  public Address address;
	  
	  @OneToOne 
	  public ContactInfo contactInfo;
	  
		  
	  
	    public static Finder<Long,AccountHolder> find = new Finder<Long,AccountHolder>(Long.class, AccountHolder.class); 
	   

	  public AccountHolder(String accountHolder,Store store,ContactInfo contactInfo,Address address){
		  this.accountHolder=accountHolder;
		  this.store=store;
		  this.contactInfo=contactInfo;
		  this.address=address;
		  
	  }
	  
	  public static void create(AccountHolder account){
		  account.save();
		 
	  }
	  
	  
	  public static AccountHolder create(String accountHolder,Store store,ContactInfo contactInfo,Address address) {
	    	 Logger.info("@M Supplier -->> create("+accountHolder+",store,address,contactInfo) -->>");
	    	 AccountHolder account = new AccountHolder(accountHolder,store,contactInfo,address);
	    	account.save();
	    	Logger.info("@M Supplier -->> create("+accountHolder+",address,contactInfo) <<--");
	        return account;
	    }
		
	  public static Page<AccountHolder> page(int page, int pageSize, String sortBy, String order, String filter) {
	    	Logger.info("@M Employee -->> page("+page+","+pageSize+","+sortBy+","+order+","+filter+") -->> ");
	        return 
	            ( find.where()
	                .ilike("account_holder", filter + "%")
	                .orderBy(sortBy + " " + order)  
	                .findPagingList(pageSize))	                
	                .setFetchAhead(false)
	                .getPage(page);
	    }

	  public static void delete(Long id) {
			 Logger.info("@M AccountHolder -->> delete("+id+") -->> ");
		 
			 //find accountholder 
		  AccountHolder account = AccountHolder.find.byId(id);
		  
		   account.delete();
		  
		  Logger.info("@M AccountHolder -->> delete("+id+") <<--");
		}
	  
	  public static Map<String,String> options(Long storeid) {
			 Logger.info("@M AccountHolder -->> options("+storeid+") -->> ");
		        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
		        for(AccountHolder c: AccountHolder.find.where().eq("store.id", storeid).orderBy("account_holder").findList()) {
		            options.put(c.id.toString(), c.accountHolder);
		        }
		        Logger.info("@M AccountHolder -->> options("+storeid+") <<--");
		        return options;
		    }
		
	  public static List<AccountHolder> getAccountHolderByStore(Long storeid) {
	    	Logger.info("@M AccountHolder -->> getAccountHolderbyStore("+storeid+") -->> ");
	        return 
	            ( find.where()
	                .eq("store.id", storeid)
	                .orderBy("account_holder")  
	                .findList());
	    }
	  /*public static AccountHolder getDetails(Long accountHolderid,Long salesHeadId){
		  AccountHolder accountName=AccountHolder.getAccountName(accountHolderid,salesHeadId);
		  SalesHead salesHead=SalesHead.getSalesHeadName(salesHeadId);

		  return accountName;
		  
	  }
*/
	
	  public static AccountHolder getAccountName(Long accountHolderId,Long name){
			
				
				return 
		 	            ( find.where()
		 	                .eq("account_holder", accountHolderId)
		 	                ).findUnique();
		 	                
		 	   
			}
	 


}
