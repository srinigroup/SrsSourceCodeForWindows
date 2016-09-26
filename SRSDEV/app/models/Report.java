package models;

import com.avaje.ebean.Ebean;

import play.db.ebean.*;
import java.util.List;

public class Report extends Model{
	
	private static final long serialVersionUID = 1L;
	
	public static List getSalesReport(String startDate , Long storeId){
		
		String sql = "select * from daily_sales_reconciliation where  store_id = :status";
		//String sql = "select * from daily_sales_reconciliation where reporting_business_date = :name and store_id = :status";
		 
		 com.avaje.ebean.SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
		// sqlQuery.setParameter("name", startDate);
		 sqlQuery.setParameter("status", storeId);
		 
		 // execute the query returning a List of MapBean objects
		 List<com.avaje.ebean.SqlRow> list = sqlQuery.findList();
		 return list;
		 
	}

}
