package models;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import controllers.Application;
import play.*;
import play.data.DynamicForm;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;


/**
 * @author shravan
 *
 */
@Entity
public class Terminal  extends Model{
	
	
	private static final long serialVersionUID = 1L;

	@Id  
	public Long id;
		
		
	 @OneToMany(cascade = CascadeType.ALL)
	  public List<Shift> shifts = new ArrayList<Shift>();
	  
	
	 @OneToMany(cascade = CascadeType.ALL)
	  public List<TotalSettlementSale> totalSettlementSales = new ArrayList<TotalSettlementSale>();
	 
	  @OneToOne
	  public TerminalHead terminalHead ;
	 
	  public Double gstCollected;	  
	  
	  @ManyToOne
	  public DailySalesReconciliation dailySalesReconciliation;
	
	  public Double terminalVariance;
	  
	  public String varReason;
	  //SUBMITTED|SAVED|HOLIDAY-CLOSED|OPEN
	  @Constraints.Required	
	  public String status;
	  
	  public Terminal(TerminalHead terminalHead, String status) {
		  Logger.info("@M Terminal -->> 2-param constructor");
		  this.terminalHead = terminalHead;
		  this.status = status;
			
		}
	  public Terminal(List<Shift> shifts,
			  Double gstCollected,Double variance,List<TotalSettlementSale> totalSettlementSales,String status)
	  {
		  Logger.info("@M Terminal -->> Terminal(shifts,gstCollected,variance,totalSettlementSales,status)");
		this.shifts= shifts;
		this.gstCollected = gstCollected;
		this.terminalVariance = variance;
		this.totalSettlementSales = totalSettlementSales;
		this.status = status;
	  }
	  
	 

	/**
	     * Create a new Terminal.
	     */
	    public static Terminal create(List<Shift> shifts,
	    		Double gstCollected,Double variance,List<TotalSettlementSale> totalSettlementSales,String status)
	    {
	    	 Logger.info("@M Terminal -->> create(shifts,gstCollected,variance,totalSettlementSales,status) -->>");
	    	Terminal terminal = new Terminal(shifts, gstCollected,variance,totalSettlementSales,status);
	    	terminal.save();
	    	 Logger.info("@M Terminal -->> create(shifts,gstCollected,variance,totalSettlementSales,status) <<--");
	        return terminal;
	    }
	    
	    /**
	     * Generic query helper for entity Company with id Long
	     */
	    public static Finder<Long,Terminal> find = new Finder<Long,Terminal>(Long.class, Terminal.class); 
	    
	    public static String getTotalSettlementSales(DailySalesReconciliation dsr,String term,String saleType) {
	    	
	    	Logger.info("@M Terminal -->> getTotalSettlementSales(dsr id "+dsr.id+","+term+","+saleType+") -->>");
			  
			  String result = null;
			  Terminal terminal =  getTerminalbyTerminalHead(dsr,term);
			  result =  getTerminalSettlementSale(terminal, saleType);
			  Logger.debug("@M Terminal -->> getTotalSettlementSales(dsr id "+dsr.id+","+term+","+saleType+") -->> TotalSettlementSales "+result);
			  Logger.info("@M Terminal -->> getTotalSettlementSales(dsr id "+dsr.id+","+term+","+saleType+") <<--");
		       return result;
			  
			}
	    
	    
	    
	    public static String getTerminalSettlementSale(Terminal terminal,String term) {
	    	
	    	Logger.info("@M Terminal -->> getTerminalSettlementSale(terminal id "+terminal.id+","+term+") -->>");
			  
			  String result = null;
			  
			  for(TotalSettlementSale candidate: terminal.totalSettlementSales) {
		           
	    		  if(candidate.totalSalesHead.name.toLowerCase().contains(term.toLowerCase())){ 
	    			 result = candidate.amount.toString();
	  			  } 
		        }
			  Logger.debug("@M Terminal -->> getTerminalSettlementSale(terminal id "+terminal.id+","+term+") -->> TerminalSettlementSale "+result);
			  Logger.info("@M Terminal -->> getTerminalSettlementSale(terminal id "+terminal.id+","+term+") <<--");
			  
			  return result;
			  
			}
		 	  
	       public static Terminal getTerminalbyTerminalHead(DailySalesReconciliation dsr,String term) {
	    	   
	    	   Logger.info("@M Terminal -->> getTerminalbyTerminalHead(dsr id "+dsr.id+","+term+") -->>");
			  
			  Terminal result = null;
			  
			 result = getTerminalbyTerminalHead(dsr.terminals,term);
			  
			 Logger.info("@M Terminal -->> getTerminalbyTerminalHead(dsr id "+dsr.id+","+term+") <<--");
			  return result;
			  
			}
		  
	       public static List<Shift> getShiftsbyTerminalHead(DailySalesReconciliation dsr,String term) {
	    	   
	    	   Logger.info("@M Terminal -->> getShiftsbyTerminalHead(dsr id "+dsr.id+","+term+") -->>");
				  
				  Terminal result = null;
				  List<Shift> shifts =  new ArrayList<Shift>();
				  
				 result = getTerminalbyTerminalHead(dsr.terminals,term);
				  if(result!=null)
				  {
					  shifts =  result.shifts;
				  }
				  
				  Logger.info("@M Terminal -->> getShiftsbyTerminalHead(dsr id "+dsr.id+","+term+") <<--");
				  return shifts;
				  
				}
	       
	       public static Terminal getTerminalbyTerminalHead(List<Terminal> terminals,String term) {
	 		  
	    	   Logger.info("@M Terminal -->> getShiftsbyTerminalHead(terminalsList,"+term+") -->>");
	 		  Terminal result = null;
	 		  if(terminals.size()>0){
	 		  for(Terminal candidate: terminals) {
	 			
	     		  if(candidate.terminalHead.name.toLowerCase().contains(term.toLowerCase())){ 
	     			 result = candidate;
	   			  } 
	 	        }
	 		  }
	 		  Logger.info("@M Terminal -->> getShiftsbyTerminalHead(terminalsList,"+term+") <<--");
	 		  return result;
	 		  
	 		}
	    
	    public static String getSaleAmount(String sale,List<Shift> shifts){
	    	 Logger.info("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->>");
			  String result ;
			   switch (sale) {
				  
				      case "TOTALSALES":		  
				    	  Logger.debug("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->> case : TOTALSALES");
				    	  result = Terminal.getTotalSalesAmount(shifts);
			              break;
			 
			          case "ACCOUNT SALES":
			        	  Logger.debug("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->> case : ACCOUNT SALES ");
			        	  result = Terminal.getAccoutSalesAmount(shifts,sale);
			              break;
			              
			          case "ACCOUNT RECV":
			        	  Logger.debug("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->> case : ACCOUNT RECV");
			        	  result = Terminal.getAccoutSalesAmount(shifts,sale);
			              break;
			              
			          case "EFPOS":
			        	  Logger.debug("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->> case : EFPOS");
			        	  result = Terminal.getEFPOSAmount(shifts,sale);
			              break;
			              
			          case "SAFE_FINAL_DROP":
			        	  Logger.debug("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->> case : SAFE_FINAL_DROP");
			        	  result = Terminal.getSafeFinalDropAmount(shifts,sale);
			              break;
			  
			          default:
			        	  Logger.debug("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") -->> case : default case");
			              result  = Terminal.getMediaAmount(shifts,sale);
			              break;
			  
			          }
			   Logger.info("@M Terminal -->> getSaleAmount("+sale+",shiftsList"+") <<--");
			   return result;
		  }
		
		  public static String getMediaAmount(List<Shift> shifts, String sale) {
			  
			  Logger.info("@M Terminal -->> getMediaAmount(shiftsList ,"+sale+") -->>");
			  
			  Double result = 0.00;
			  result = getMediaAmountLong(shifts,sale);
			  result=Math.round( result * 100.0 ) / 100.0;
			  Logger.debug("@M Terminal -->> getMediaAmount(shiftsList ,"+sale+") -->>MediaAmount "+result);
			  Logger.info("@M Terminal -->> getMediaAmount(shiftsList ,"+sale+") <<--");
			  return result.toString();
			  
			}
		  
		  public static Double getMediaAmountLong(List<Shift> shifts, String mediaType) {
			  
			  Logger.info("@M Terminal -->> getMediaAmountLong(shiftsList ,"+mediaType+") -->>");
			  Double result = 0.00;
			  ShiftMediaCollected shiftMediaCollected ;
			 
	    		  if(shifts!=null){
	    			  
	    			 for(Shift shift : shifts){
	    				 
	    				 shiftMediaCollected = Shift.getShiftMediaCollected(shift.mediaCollects,mediaType);
	    				 if(shiftMediaCollected!=null){
	        				result = result + shiftMediaCollected.settleAmount;
	    				 }
	    			 }
	  			  } 
	    		  Logger.debug("@M Terminal -->> getMediaAmountLong(shiftsList ,"+mediaType+") -->> MediaAmountLong :"+result);
	    		 
	    		  
	    		 result=Math.round( result * 100.0 ) / 100.0;
	    		 Logger.info("@M Terminal -->> getMediaAmountLong(shiftsList ,"+mediaType+") <<--");
			  return result;
			  
			}
		  
		  public static String getEFPOSAmount(List<Shift> shifts, String sale) {
			  
			  Logger.info("@M Terminal -->> getEFPOSAmount(shiftsList ,"+sale+") -->>");
			  Double result = 0.00;
			  
			  result = getMediaAmountLong(shifts,"EFPOS1");
			  result = result + getMediaAmountLong(shifts,"EFPOS2");
			  result=Math.round( result * 100.0 ) / 100.0;
			  Logger.debug("@M Terminal -->> getEFPOSAmount(shiftsList ,"+sale+") -->> EFPOSAmount "+result);
			  Logger.info("@M Terminal -->> getEFPOSAmount(shiftsList ,"+sale+") <<--");
			  return result.toString();
			  
		  }
		  
		  public static String getSafeFinalDropAmount(List<Shift> shifts, String sale) {
			  Logger.info("@M Terminal -->> getSafeFinalDropAmount(shiftsList ,"+sale+") -->>");
			  Double result = 0.00;
			  result = getMediaAmountLong(shifts,"CASH");
			  result = result + getMediaAmountLong(shifts,"CHEQUE");
			  result=Math.round( result * 100.0 ) / 100.0;
			  Logger.debug("@M Terminal -->> getSafeFinalDropAmount(shiftsList ,"+sale+") -->> SafeFinalDropAmount "+result);
			  Logger.info("@M Terminal -->> getSafeFinalDropAmount(shiftsList ,"+sale+") <<--");
			  return result.toString();
		  }
	    
	    public static String getTotalSalesAmount(List<Shift> shifts) {
	    	
	    	 Logger.info("@M Terminal -->> getTotalSalesAmount(shiftsList) -->>");
			  
	    	Double result = 0.00;
			
				  
	    		  if(shifts!=null){
	    			  
	    			 for(Shift shift : shifts){
	    				   				 
	    				 for(ShiftSale shiftSale : shift.shiftSales){
	    					
	    					 if(!(shiftSale.salesHead.name.equals(Application.propertiesMap.get("salesHaed_AC_SALES")) || shiftSale.salesHead.name.equals(Application.propertiesMap.get("salesHaed_AC_RECV"))|| shiftSale.salesHead.name.equals(Application.propertiesMap.get("salesHaed_Fuel_InLtr"))|| shiftSale.salesHead.name.equals(Application.propertiesMap.get("salesHaed_ShopSales_Excl_GST")))){
	    						
	    						 result = result + shiftSale.amount;
	    					 }
	        			 }
	    			 }
	  			  } 
	    		  result=Math.round( result * 100.0 ) / 100.0;
	    		  Logger.debug("@M Terminal -->> getTotalSalesAmount(shiftsList) -->>TotalSalesAmount "+result);
	    		  Logger.info("@M Terminal -->> getTotalSalesAmount(shiftsList) <<--");
			  return result.toString();
			  
			}
	    
	    
	    
		  
		  
		  public static String getAccoutSalesAmount(List<Shift> shifts, String sale) {
			  Logger.info("@M Terminal -->> getAccoutSalesAmount(shiftsList,"+sale+") -->>");
			  Double result = 0.00;
			  ShiftSale shiftSale ;
				  
	    		  if(shifts!=null){
	    			  
	    			 for(Shift shift : shifts){
	    				 				 
	    				 shiftSale = Shift.getShiftSale(shift.shiftSales,sale);
	    				 if(shiftSale!=null){
	        				result = result + shiftSale.amount;
	    				 }
	    			 }
	  			  } 
	    		  result=Math.round( result * 100.0 ) / 100.0;
	    		  Logger.debug("@M Terminal -->> getAccoutSalesAmount(shiftsList,"+sale+") -->>AccoutSalesAmount "+result);
	    		  Logger.info("@M Terminal -->> getAccoutSalesAmount(shiftsList,"+sale+") <<--");
			  return result.toString();
			  
			}
	 	public static List<Terminal> all() {
	 		Logger.info("@M Terminal -->> all() -->>");
		  return find.all();
		}

		public static void create(Terminal terminal) {
			Logger.info("@M Terminal -->> create(terminal) -->>");
			terminal.save();
			Logger.info("@M Terminal -->> create(terminal) <<--");
		}
		
		public static void delete(Long id) {
			Logger.info("@M Terminal -->> delete("+id+") -->>");
			  find.ref(id).delete();
			  Logger.info("@M Terminal -->> delete("+id+") <<--");
			}
				  	    
	    
	    public static Map<String,String> options() {
	    	Logger.info("@M Terminal -->> options() -->>");
	        LinkedHashMap<String,String> options = new LinkedHashMap<String,String>();
	        for(Terminal c: Terminal.find.findList()) {
	            options.put(c.id.toString(), c.status);
	        }
	        Logger.info("@M Terminal -->> options() <<--");
	        return options;
	    }
	    
	   public static Double getPayoutsByTerminal(Terminal terminal){
		   
		   Logger.info("@M Terminal -->> getPayoutsByTerminal() -->>");
		   
		   Double result=0.00;
		   for(Shift shift:terminal.shifts){
			   
			   String payout=Shift.getTotalPayoutsbyType(shift,"REGISTERPAYOUT");
			   result=result+Double.parseDouble(payout);
		   }
		   result=Math.round( result * 100.0 ) / 100.0;
		   Logger.info("@M Terminal -->> getPayoutsByTerminal() <<--");
		   return result;
	   }
	   
	   public static Double getPaymentTendersByTerminal(Terminal terminal,String term) {
		   
		   Logger.info("@M Terminal -->> getPaymentTendersByTerminal() -->>");
		   
		   Double result=0.00;
		   for(Shift shift:terminal.shifts){
			   
			   String paymentTenderAmount=Shift.getShiftPaymentTenderAmount(shift.shiftPayments,term);
			   result=result+Double.parseDouble(paymentTenderAmount);
		   }
		   result=Math.round( result * 100.0 ) / 100.0;
		   Logger.info("@M Terminal -->> getPaymentTendersByTerminal() <<--");
		   return result;
		   
	   }
	   
	   
	   public static Double getTotalShiftVariance(List<Shift> shifts) {
		   Logger.info("@M Terminal -->> getTotalShiftVariance(shiftsList) -->>");
			  
			  Double result = 0.00;
			
			 
	    		  if(shifts!=null){
	    			  
	    			 for(Shift shift : shifts){
	    				 	 
	    				
	        				result = result + shift.shiftVariance;
	    				
	    			 }
	  			  } 
		      
	    		  result=Math.round( result * 100.0 ) / 100.0;
	    		  Logger.debug("@M Terminal -->> getTotalShiftVariance(shiftsList) -->> TotalShiftVariance "+result);
	    		  Logger.info("@M Terminal -->> getTotalShiftVariance(shiftsList) <<--");
			  return result;
			  
			}
	   
	   /**
	    * methods used in terminal summary page 
	    * @author: Gopi
	    */
	   
	    
	    public static Double getTotalNetTakings( Terminal terminal){
	    	Logger.info("@M Terminal -->> getTotalNetTakings(terminal) -->>");
	    	Double amount=0.0;
	    	for(Shift shift:terminal.shifts){
	    		
	    		String result=Shift.getTotalNetTakings(shift, "REGISTERPAYOUT");
	    		amount=amount+Double.parseDouble(result);
	    	}
	    	amount=Math.round( amount * 100.0 ) / 100.0;
	    	Logger.debug("@M Terminal -->> getTotalNetTakings(terminal) -->> TotalNetTakings "+amount);
	    	Logger.info("@M Terminal -->> getTotalNetTakings(terminal) <<--");
	    	return amount;
	    }
	    
	    public static Double getNetPOSTakings(Terminal terminal){
	    	Logger.info("@M Terminal -->> getNetPOSTakings(terminal) -->>");
	    	
	    	Double amount=0.0;
	    	for(Shift shift:terminal.shifts){
	    		
	    		String result=Shift.getNetPOSTakings(shift);
	    		amount=amount+Double.parseDouble(result);
	    	}
	    	amount=Math.round( amount * 100.0 ) / 100.0;
	    	Logger.debug("@M Terminal -->> getNetPOSTakings(terminal) -->>NetPOSTakings "+amount);
	    	Logger.info("@M Terminal -->> getNetPOSTakings(terminal) <<--");
	    	return amount;
	    	
	    }

}
