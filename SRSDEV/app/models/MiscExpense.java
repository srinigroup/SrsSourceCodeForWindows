package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import controllers.Application;
import play.Logger;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class MiscExpense extends Model{

	private static final long serialVersionUID = 1L;

	@Id  
	public Long id;
	
	@Constraints.Required
	  public Double expenseAmt=0.00;
	
	@Constraints.Required
	public String expenseType;
	
	@ManyToOne()
	 public DailyReconciliation dailyReconciliation;
	
	// constructor
	public MiscExpense(String expenseType, Double expenseAmount){
	
		this.expenseType=expenseType;
		this.expenseAmt=expenseAmount;
		
	}
	
	/**
     * Generic query helper for entity MiscExpense with id Long
     */
    public static Finder<Long,MiscExpense> find = new Finder<Long,MiscExpense>(Long.class, MiscExpense.class);
    
    public static List<MiscExpense> all() {
 		Logger.info("@M MiscExpense -->> all() -->>");
	  return find.all();
	}

	
	public static void delete(Long id) {
		Logger.info("@M MiscExpense -->> delete("+id+") -->>");
	  find.ref(id).delete();
	  Logger.info("@M MiscExpense -->> delete("+id+") <<--");
	}
	
	// to calculate MiscExpenses amount Total
	
	public static Double getTotalMiscExpensesAmount(List<MiscExpense> miscExpenses) {
		Logger.info("@M MiscExpense -->> getTotalMiscExpensesAmount(miscExpenses) -->>");
		Double result = 0.00;		
	      for(MiscExpense miscExpense: miscExpenses){
	    	  if(miscExpense != null){
	    		  result = result + miscExpense.expenseAmt;
	    	  }
	      }
	      result=Math.round( result * 100.0 ) / 100.0;
	      Logger.debug("@M MiscExpense -->> getTotalMiscExpensesAmount(miscExpenses) -->> TotalMiscExpenseAmount "+result);
	      Logger.info("@M MiscExpense -->> getTotalMiscExpensesAmount(miscExpenses) <<--");
		  return result;
	}
	
	public static MiscExpense getMiscExpense(String expType,Double expAmount,Long drId){ // get MiscExpense object 
		
		Logger.info("@M MiscExpense -->> getMiscExpense("+expType+", "+expAmount+","+drId+") -->>");
		
		List<MiscExpense> miscExpenses = new ArrayList<MiscExpense>();
		
		MiscExpense miscExpense = MiscExpense.getMiscExpenseUnique(expType, expAmount, drId); // find an object with same exp and amount
		
		if(miscExpense==null){ // if no object is found with same exp and amount ,create new one
			Logger.info("@M MiscExpense -->> getMiscExpense("+expType+", "+expAmount+","+drId+") -->> create new");
		DailyReconciliation dailyReconciliation = DailyReconciliation.find.byId(drId);
		miscExpense=new MiscExpense(expType,expAmount);
		miscExpense.dailyReconciliation=dailyReconciliation;
		miscExpense.save();
		miscExpenses=dailyReconciliation.miscExpenses;
		
		miscExpenses.add(miscExpense);
		dailyReconciliation.miscExpenses=miscExpenses;
		dailyReconciliation.save();
		
		}
		else{
			Logger.info("@M MiscExpense -->> getMiscExpense("+expType+", "+expAmount+","+drId+") -->> return null Indiacte already with same expense and amount one Orecord is there");
			miscExpense=null;
			
		}
	
		Logger.info("@M MiscExpense -->> getMiscExpense("+expType+", "+expAmount+","+drId+") <<--");
		
		return miscExpense;
		
		
	}
	
	public static MiscExpense getMiscExpenseUnique(String expType,Double expAmount,Long drId){
		
		Logger.info("@M MiscExpense -->> getMiscExpenseUnique("+expType+", "+expAmount+","+drId+") -->>");
		return 
 	            ( find.where()
 	                .eq("expenseType", expType)
 	                .eq("expenseAmt", expAmount)
 	                .eq("dailyReconciliation.id", drId)
 	                ).findUnique();
	}
	
	public String toString() {  
		 return "MiscExpense("+ expenseType +":" + expenseAmt + ")";
	  }
}
