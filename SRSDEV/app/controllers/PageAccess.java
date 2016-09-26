package controllers;

import models.Shift;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Security;

/**
 * 
 * @author Rootc pc-1
 * 
 * Class used for Giving privileges on different actions SUBMITTED||SAVED||OPEN for Diff  roles sm||sk
 *
 */

@Security.Authenticated(Secured.class)
public class PageAccess extends Controller{
	
	
	//method that decide to disable/enable the specific action button in a page
	public static  boolean getAccess(String page , String role , Shift shift , String action){
		
		Logger.info("@C PageAccess -->> getAccess("+page+","+role+","+shift.id+","+action+") -->>");
		
		//if shift status is OPEN || SAVED
		if(!(shift.status.equals("SUBMITTED"))){
			Logger.debug("@C PageAccess -->> getAccess("+page+","+role+","+shift.id+","+action+") -->>status : Other Than SUBMITTED");
			
			return true;
		}else{
			//if shift status is SUBMITTED.,,Checking for Role 
			Logger.debug("@C PageAccess -->> getAccess("+page+","+role+","+shift.id+","+action+") -->>status : SUBMITTED");
			if(role.contains("sm") && !(shift.terminal.dailySalesReconciliation.status.equals("SUBMITTED"))){
				Logger.debug("@C PageAccess -->> getAccess("+page+","+role+","+shift.id+","+action+") -->>status : SUBMITTED,But role contains sm");
				
				
				return true;
			}
			else{
				
				return false;
			}
		}
		
		
	}

}
