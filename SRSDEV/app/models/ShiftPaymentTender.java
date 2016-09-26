package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import controllers.Application;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ShiftPaymentTender extends Model{
	
	private static final long serialVersionUID = 1L;

	@Id  
	  public Long id;

	@OneToOne
	  public PaymentTender paymentTender;
	
	@Constraints.Required
	  public Double amount = 0.00;
	
	@ManyToOne(cascade = CascadeType.ALL)
	public Shift shift;

	public static Double getShiftPaymentsTotalAmount(Shift shift){
		
		Double result = 0.00;
		
		for(ShiftPaymentTender shiftPaymentTender : shift.shiftPayments){
			
			if(! shiftPaymentTender.paymentTender.name.equals(Application.propertiesMap.get("PAYMENT_TENDER_CASHOUT"))){
				
				String amount =  Shift.getShiftPaymentTenderAmount(shift.shiftPayments, shiftPaymentTender.paymentTender.name);
				result = result + Double.parseDouble(amount);
			}
			
		}
		return result;
		
	}
}
