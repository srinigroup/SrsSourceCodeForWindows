package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.reports.*;


@Security.Authenticated(Secured.class)
public class Reports extends Controller {
	
	// GST amount as per AUS government
	private static final int GST_AUS = 11;
	
	//Reports Home page action method
	public static Result getReportHome(){
		flash("action", "report");
		return ok(reportsHome.render());
	}
	
	public static Result getReportHomeForStore(){
		flash("action", "report");
		Long sid = Long.parseLong(session("storeid")); // Retrieve store id from session
		return ok(reportsHomeForStore.render(sid));
	}
	
	
	public static Result getReport(){
		
		
		String startDate=form().bindFromRequest().get("reportStartDate");
		String endDate=form().bindFromRequest().get("reportEndDate");
		String storeName=form().bindFromRequest().get("reportStore");
		String byType=form().bindFromRequest().get("byType");
		String sortBy=form().bindFromRequest().get("sortBy");
		Long storeId=null;
		
		if(!storeName.equals("ALL")){
			storeId=Long.parseLong(storeName);
		}
		
		//List result=Report.getSalesReport(form().bindFromRequest().get("reportStartDate"),Long.parseLong(form().bindFromRequest().get("reportStore")));
		List<DailySalesReconciliation> result=null;
		if(storeId!=null){
			result = DailySalesReconciliation.page(storeId,startDate,endDate);
		}else{
			result = DailySalesReconciliation.page(storeName,startDate,endDate);
		}
		
		List<List> resultVOList=new ArrayList<List>();

		
		//checking either sales or media
	if(byType.equals("sales")){
		
		//add columns
	
		List columnList = new ArrayList();
		
			columnList.add("Store Name");
			columnList.add("Date");
			columnList.add("Total Sales");
			columnList.add("GST Shop Sales");
			columnList.add("GST Free Sales");
			
			for(SalesHead salesHaed : SalesHeads.getSalesHeadsList()){
			if(!(salesHaed.name.equals("ACCOUNT RECV"))){
				columnList.add(salesHaed.name);
				}
			}
			
			columnList.add("Variance");
			columnList.add("Media Variance");
			resultVOList.add(columnList);
		//add rows
		for(DailySalesReconciliation dsr : result){
			if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY
		
			List recordVO=new ArrayList();
			
			recordVO.add(dsr.store.name);
			recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
			Double totalSalesAmount=DailySalesReconciliation.getTotalSalesAmount(dsr);
			recordVO.add(totalSalesAmount);
			recordVO.add(getGSTShopSales(dsr));
			recordVO.add(getGSTFreeSales(dsr));
			for(SalesHead saleHead: SalesHeads.getSalesHeadsList()){
				Double amount=0.00;
				if(!(saleHead.name.equals("ACCOUNT RECV"))){
				
					for(Terminal terminal : dsr.terminals){
					
						for(Shift shift : terminal.shifts){
						
							for(ShiftSale shiftSale : shift.shiftSales){
							
								if((shiftSale.salesHead.name.equals(saleHead.name) && !(shiftSale.salesHead.name.equals("ACCOUNT RECV")))){
								
									amount=amount+shiftSale.amount;
								}
							}
						}
					}
			
					recordVO.add(amount);
				}
			}
			recordVO.add(DailySalesReconciliation.getVarianceAmount(dsr));
			Double mediaDifferenceAmount=0.00;
			for(Terminal terminal : dsr.terminals){
				
				for(Shift shift : terminal.shifts){
					
					mediaDifferenceAmount=shift.getShiftMediaDifference(shift);
				}
			}
			recordVO.add(mediaDifferenceAmount);
		resultVOList.add(recordVO);
		
			}//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY
		}
				
	} 
	else if(byType.equals("media")){
			// add columns
			
			List columnList = new ArrayList();
			
			columnList.add("Store Name");
			columnList.add("Date");
			columnList.add("Total Sales");
			for(MediaTender mediaTender : MediaTenders.getMediaTendersList()){
					columnList.add(mediaTender.name);
			}
			resultVOList.add(columnList);
			//Add rows
			for(DailySalesReconciliation dsr : result){
					List recordVO=new ArrayList();
					if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY

					recordVO.add(dsr.store.name);
					recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
					Double totalSalesAmount=DailySalesReconciliation.getTotalSalesAmount(dsr);
					recordVO.add(totalSalesAmount);
					for(MediaTender mediaTender : MediaTenders.getMediaTendersList()){
						Double amount=0.00;
							for(Terminal terminal : dsr.terminals){
								for(Shift shift : terminal.shifts){
									for(ShiftMediaCollected shiftMediaCollected : shift.mediaCollects){
										if(shiftMediaCollected.mediaTender.name.equals(mediaTender.name)){
										amount=amount+shiftMediaCollected.settleAmount;
										}
										
									}
								}
							}
							recordVO.add(amount);
					}
						resultVOList.add(recordVO);
					}//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY
			}
					
		}
		
		else if(byType.equals("invoices")){
			
			if(sortBy.equals("SUPPLIER")){
				
				// add columns
				List columnList = new ArrayList();
				
				columnList.add("Supplier Name");
				columnList.add("Date");
				columnList.add("Store Name");
				columnList.add("Payment Terms");
				columnList.add("Payout Type");
				columnList.add("Invoice Amount");
				
				
				
				resultVOList.add(columnList);
				
				// add rows
				for(DailySalesReconciliation dsr : result){
					if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY

					for(Terminal terminal : dsr.terminals){
						for(Shift shift : terminal.shifts){
							for(Payout payout : shift.payouts){
								List recordVO=new ArrayList();
								
								recordVO.add(payout.supplier.name);
								recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
								recordVO.add(dsr.store.name);
								for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
									if(supplierMapping.supplier.id == payout.supplier.id){
										recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
									} 
								}
								recordVO.add(payout.payoutType);
								recordVO.add(payout.invoiceAmt);
								
								resultVOList.add(recordVO);
							}
						}
					}
					for(Payout payout : dsr.dr.payouts){
						List recordVO=new ArrayList(); 
						
						recordVO.add(payout.supplier.name);
						recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
						recordVO.add(dsr.store.name);
						for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
							if(supplierMapping.supplier.id == payout.supplier.id){
								recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
							}
						}
						recordVO.add(payout.payoutType);
						recordVO.add(payout.invoiceAmt);
						
						resultVOList.add(recordVO);
						
					}
					
				}//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY
				}
			}
				
			else if(sortBy.equals("PAYTERMS")){
				
				// add columns
				List columnList = new ArrayList();
				
				columnList.add("Payment Terms");
				columnList.add("Date");
				columnList.add("Supplier Name");
				columnList.add("Store Name");
				columnList.add("Payout Type");
				columnList.add("Invoice Amount");
				
				
				resultVOList.add(columnList);
				
				// add rows
				for(DailySalesReconciliation dsr : result){
					
					
					for(Terminal terminal : dsr.terminals){
						for(Shift shift : terminal.shifts){
							for(Payout payout : shift.payouts){
								List recordVO=new ArrayList();
								for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
									if(supplierMapping.supplier.id == payout.supplier.id){
										recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
									}
								}
								
								recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
								recordVO.add(payout.supplier.name);
								recordVO.add(dsr.store.name);
								recordVO.add(payout.payoutType);
								recordVO.add(payout.invoiceAmt);
								resultVOList.add(recordVO);
							}
						}
					}
					for(Payout payout : dsr.dr.payouts){
						List recordVO=new ArrayList();
						
						for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
							if(supplierMapping.supplier.id == payout.supplier.id){
								recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
							}
						}
						recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
						recordVO.add(payout.supplier.name);
						recordVO.add(dsr.store.name);
						recordVO.add(payout.payoutType);
						recordVO.add(payout.invoiceAmt);
						resultVOList.add(recordVO);
						
					}
					
				}
				
				
			}else if(sortBy.equals("STORE")){
				
				// add columns
				List columnList = new ArrayList();
				
				columnList.add("Store Name");
				columnList.add("Date");
				columnList.add("Supplier Name");
				columnList.add("Payment Terms");
				columnList.add("Payout Type");
				columnList.add("Invoice Amount");

				
				resultVOList.add(columnList);
				
				// add rows
				for(DailySalesReconciliation dsr : result){
					
					
					for(Terminal terminal : dsr.terminals){
						for(Shift shift : terminal.shifts){
							for(Payout payout : shift.payouts){
								List recordVO=new ArrayList();
								
								recordVO.add(dsr.store.name);
								recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
								recordVO.add(payout.supplier.name);
								
								for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
									if(supplierMapping.supplier.id == payout.supplier.id){
										recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
									}
								}
								recordVO.add(payout.payoutType);
								recordVO.add(payout.invoiceAmt);

								resultVOList.add(recordVO);
							}
						}
					}
					for(Payout payout : dsr.dr.payouts){
						List recordVO=new ArrayList();
						
						recordVO.add(dsr.store.name);
						recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
						recordVO.add(payout.supplier.name);
						
						for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
							if(supplierMapping.supplier.id == payout.supplier.id){
								recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
							}
						}
						recordVO.add(payout.payoutType);
						recordVO.add(payout.invoiceAmt);

						resultVOList.add(recordVO);
					}
					
				}
				
				
			}else{ //here you sortBY Date
				
				// add columns
				List columnList = new ArrayList();
				
				columnList.add("Date");
				columnList.add("Supplier Name");
				columnList.add("Payment Terms");
				columnList.add("Store Name");
				columnList.add("Payout Type");
				columnList.add("Invoice Amount");

				resultVOList.add(columnList);
				
				// add rows
				for(DailySalesReconciliation dsr : result){
					
					
					for(Terminal terminal : dsr.terminals){
						for(Shift shift : terminal.shifts){
							for(Payout payout : shift.payouts){
								List recordVO=new ArrayList();
								
								recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
								recordVO.add(payout.supplier.name);
								for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
									if(supplierMapping.supplier.id == payout.supplier.id){
										recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
									}
								}
								recordVO.add(dsr.store.name);
								recordVO.add(payout.payoutType);
								recordVO.add(payout.invoiceAmt);

								resultVOList.add(recordVO);
							}
						}
					}
					for(Payout payout : dsr.dr.payouts){
						List recordVO=new ArrayList();
						
						
						recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
						recordVO.add(payout.supplier.name);
						
						for(SupplierMapping supplierMapping : dsr.store.supplierMapping){
							if(supplierMapping.supplier.id == payout.supplier.id){
								recordVO.add(Application.propertiesMap.get(supplierMapping.paymentTerms));
							}
						}
						recordVO.add(dsr.store.name);
						recordVO.add(payout.payoutType);
						recordVO.add(payout.invoiceAmt);

						resultVOList.add(recordVO);
						
					}
				}
			}
			
			
			
		
		
		}
		else if(byType.equals("ReconciliationReport")){
			
			//add columns
			
			List columnList = new ArrayList();

				columnList.add("Store Name");
				columnList.add("Date");
				columnList.add("Today Cash");
				columnList.add("Account Receivables");
				columnList.add("Today Cheque");
				columnList.add("Total Sales");
				columnList.add("Total Media");
				columnList.add("Register payouts");
				columnList.add("Safe Payouts");
				columnList.add("Other Payouts");
				columnList.add("Pay Wages");
				columnList.add("Cash Deposits");
				columnList.add("Cheque Deposits");
				columnList.add("Safe Cash");
				columnList.add("Safe Cheques");
				// columnList.add("Variance");
				
						
			resultVOList.add(columnList);
			
			
			//add rows
			for(DailySalesReconciliation dsr : result){
				
			
				List recordVO=new ArrayList();
				if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY 

				
				recordVO.add(dsr.store.name);
				recordVO.add(Application.getDateString(dsr.reportingBusinessDate));
				recordVO.add(DailySalesReconciliation.getCashReported(dsr));
				for(SalesHead saleHead: SalesHeads.getSalesHeadsList()){
					Double amount=0.00;
					if((saleHead.name.equals("ACCOUNT RECV"))){
					
						for(Terminal terminal : dsr.terminals){
						
							for(Shift shift : terminal.shifts){
							
								for(ShiftSale shiftSale : shift.shiftSales){
								
									if((shiftSale.salesHead.name.equals("ACCOUNT RECV"))){
									
										amount=amount+shiftSale.amount;
										
									}
								}
							}
						}
						System.out.println("Account Rece amount is "+amount);
						recordVO.add(amount);
					}
				}
				recordVO.add(DailySalesReconciliation.getChequesReported(dsr));
				Double totalSalesAmount=DailySalesReconciliation.getTotalSalesAmount(dsr);
				recordVO.add(totalSalesAmount);
				recordVO.add(DailySalesReconciliation.getTotalMediaCollected(dsr));
				recordVO.add(DailySalesReconciliation.getTotalPayouts(dsr));
				Double payoutAmount=0.00;
				for(Payout payout : dsr.dr.payouts){
					
					payoutAmount=payoutAmount+payout.invoiceAmt;
				}
				recordVO.add(payoutAmount);
				recordVO.add(DailySalesReconciliation.getOtherPayouts(dsr));
				Double payrollAmount=0.00;
				for(Payroll payroll : dsr.dr.payroll){
					
					payrollAmount=payrollAmount+payroll.payAmt;
				}
				recordVO.add(payrollAmount);
				recordVO.add(dsr.dr.bankDeposit.cashAmt);
				recordVO.add(dsr.dr.bankDeposit.chequeAmt);
				recordVO.add(dsr.dr.close_cash);
				recordVO.add(dsr.dr.close_cheque);
				//recordVO.add(DailySalesReconciliation.getVarianceAmount(dsr));
				
				resultVOList.add(recordVO);
				//System.out.println("Each Recoed List :"+recordVO);		
				
				}//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY
			}
			
			
		}
		//System.out.println("Reports List :"+resultVOList);		
		return ok(Json.toJson(resultVOList));
	}
	
	
	/**
	 * To calculate GST Shop Sales  
	 * @author Gopi
	 * @param dsr
	 * @return GST Shop Sales Count
	 */
	public static Double getGSTShopSales(DailySalesReconciliation dsr){
		
		Logger.info("@C Reports getGSTShopSales(dsr) -->> ");
		
		Double result = 0.00;
		
		Double GST = calculateGST(dsr);
		result = GST * GST_AUS; // GST Shop sales = GST*11
		
		Logger.info("@C Reports getGSTShopSales(dsr) <<--");
		
		return result;
	}
	
	
	/**
	 * To calculate GST Free Sales  
	 * @author Gopi
	 * @param dsr
	 * @return GST Free Sales Count
	 */
	public static Double getGSTFreeSales(DailySalesReconciliation dsr){
		
		Logger.info("@C Reports getGSTFreeSales(dsr) -->> ");
		
		Double result = 0.00;
		
		Double shopSales_Incl_GST = 0.00;
		
		for(Terminal terminal : dsr.terminals){
			
			for(Shift shift : terminal.shifts){
				
				Double incl_gst = Double.parseDouble(Shift.getShiftSaleAmount(shift.shiftSales, Application.propertiesMap.get("salesHaed_ShopSales_Incl_GST")));
				shopSales_Incl_GST = shopSales_Incl_GST + incl_gst;
				
			}
			
		}
		
		result = shopSales_Incl_GST - Reports.getGSTShopSales(dsr); // GST Free sales = Shop sales including GST - GST Shop sales
		
		Logger.info("@C Reports getGSTFreeSales(dsr) <<--");
		
		return result;
	}
	
	/**
	 * To calculate GST
	 * @author Gopi
	 * @param dsr
	 * @return GST 
	 */
	public static Double calculateGST(DailySalesReconciliation dsr){
		
		Logger.info("@C Reports calculateGST(dsr) -->> ");
		
		Double result = 0.00;
		
		Double shopSales_Incl_GST = 0.00;
		Double shopSales_Excl_GST = 0.00;
		
		for(Terminal terminal : dsr.terminals){
			
			for(Shift shift : terminal.shifts){
				
				Double excl_gst = Double.parseDouble(Shift.getShiftSaleAmount(shift.shiftSales, Application.propertiesMap.get("salesHaed_ShopSales_Excl_GST")));
				Double incl_gst = Double.parseDouble(Shift.getShiftSaleAmount(shift.shiftSales, Application.propertiesMap.get("salesHaed_ShopSales_Incl_GST")));
				shopSales_Incl_GST = shopSales_Incl_GST + incl_gst;
				shopSales_Excl_GST = shopSales_Excl_GST + excl_gst;
			}
			
		}
		
		result = shopSales_Incl_GST - shopSales_Excl_GST; // GST = shop sales including GST - shop sales Excluding GST
		
		Logger.info("@C Reports calculateGST(dsr) <<--");
		
		return result;
	}
}
