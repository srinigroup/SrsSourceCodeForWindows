package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.DailyReconciliation;
import models.DailySalesReconciliation;
import models.MediaTender;
import models.Payout;
import models.Payroll;
import models.SalesHead;
import models.Shift;
import models.ShiftMediaCollected;
import models.ShiftSale;
import models.Store;
import models.Terminal;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.managementreports.*;
import views.html.reports.reportsHomeForStore;

public class ManagementReports extends Controller {
	public static Result getReportsHome() {
		flash("action", "Management Report");
		return ok(reportsHomePageForManagement.render());
	}

	public static Result getReportHomeForStore() {
		flash("action", "report");
		Long sid = Long.parseLong(session("storeid")); // Retrieve store id from
														// session
		return ok(reportsHomeForStore.render(sid));
	}

	public static Result getManagementReport() {

		String startDate = form().bindFromRequest().get("reportStartDate");
		String endDate = form().bindFromRequest().get("reportEndDate");
		String storeName = form().bindFromRequest().get("reportStore");
		String byType = form().bindFromRequest().get("byType");
		String sortBy = form().bindFromRequest().get("sortBy");
		Long storeId = null;

		if (!storeName.equals("ALL")) {
			storeId = Long.parseLong(storeName);
		}

		// List
		// result=Report.getSalesReport(form().bindFromRequest().get("reportStartDate"),Long.parseLong(form().bindFromRequest().get("reportStore")));
		List<DailySalesReconciliation> result = null;
		if (storeId != null) {
			result = DailySalesReconciliation.page(storeId, startDate, endDate);
		} else {
			result = DailySalesReconciliation.page(storeName, startDate,
					endDate);
		}

		List<List> resultVOList = new ArrayList<List>();
		List<List> resultVOListName = new ArrayList<List>();

		// checking either sales or media
		if (byType.equals("sales")) {

			// add columns

			List columnList = new ArrayList();

			columnList.add("Store Name");
			columnList.add("Date");
			columnList.add("Total Sales");
			for (SalesHead salesHaed : SalesHeads.getSalesHeadsList()) {
				if (!((salesHaed.name.equals("ACCOUNT RECV"))
						|| (salesHaed.name.equals("ACCOUNT SALES"))
						|| (salesHaed.name.equals("Shop Sales (Excl GST)")) || (salesHaed.name
							.equals("Total  Shift Sales")))) {
					columnList.add(salesHaed.name);
				}
			}

			// columnList.add("Variance");
			resultVOList.add(columnList);

			// add rows
			for (DailySalesReconciliation dsr : result) {

				List recordVO = new ArrayList();
				
				if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY

				recordVO.add(dsr.store.name);
				recordVO.add(Application
						.getDateString(dsr.reportingBusinessDate));
				Double totalSalesAmount = DailySalesReconciliation
						.getTotalSalesAmount(dsr);
				recordVO.add(totalSalesAmount);
				for (SalesHead saleHead : SalesHeads.getSalesHeadsList()) {
					Double amount = 0.00;
					if (!((saleHead.name.equals("ACCOUNT RECV"))
							|| (saleHead.name.equals("ACCOUNT SALES"))
							|| (saleHead.name.equals("Shop Sales (Excl GST)")) || (saleHead.name
								.equals("Total  Shift Sales")))) {

						for (Terminal terminal : dsr.terminals) {

							for (Shift shift : terminal.shifts) {

								for (ShiftSale shiftSale : shift.shiftSales) {

									if ((shiftSale.salesHead.name
											.equals(saleHead.name) && (!((shiftSale.salesHead.name
											.equals("ACCOUNT RECV"))
											|| (shiftSale.salesHead.name
													.equals("ACCOUNT SALES"))
											|| (shiftSale.salesHead.name
													.equals("Shop Sales (Excl GST)")) || (shiftSale.salesHead.name
												.equals("Total  Shift Sales")))))) {

										amount = amount + shiftSale.amount;
									}
								}
							}
						}

						recordVO.add(amount);
					}
				}
				// recordVO.add(DailySalesReconciliation.getVarianceAmount(dsr));
				resultVOList.add(recordVO);
			}
			}

		} else if (byType.equals("media")) {
			// add columns

			List columnList = new ArrayList();

			columnList.add("Store Name");
			columnList.add("Date");
			columnList.add("Total Sales");
			columnList.add("Total Media");
			for (MediaTender mediaTender : MediaTenders.getMediaTendersList()) {
				columnList.add(mediaTender.name);
			}
			for (SalesHead salesHaed : SalesHeads.getSalesHeadsList()) {
				if ((salesHaed.name.equals("ACCOUNT RECV"))
						|| (salesHaed.name.equals("ACCOUNT SALES"))) {
					columnList.add(salesHaed.name);
				}
			}
			resultVOList.add(columnList);
			// Add rows
			for (DailySalesReconciliation dsr : result) {
				List recordVO = new ArrayList();
				
				if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY

				recordVO.add(dsr.store.name);
				recordVO.add(Application
						.getDateString(dsr.reportingBusinessDate));
				Double totalSalesAmount = DailySalesReconciliation
						.getTotalSalesAmount(dsr);
				recordVO.add(totalSalesAmount);
				Double totalMediaAmount = DailySalesReconciliation
						.getTotalMediaCollected(dsr);
				recordVO.add(totalMediaAmount);
				for (MediaTender mediaTender : MediaTenders
						.getMediaTendersList()) {
					Double amount = 0.00;
					for (Terminal terminal : dsr.terminals) {
						for (Shift shift : terminal.shifts) {
							for (ShiftMediaCollected shiftMediaCollected : shift.mediaCollects) {
								if (shiftMediaCollected.mediaTender.name
										.equals(mediaTender.name)) {
									amount = amount
											+ shiftMediaCollected.settleAmount;
								}

							}
						}
					}
					recordVO.add(amount);
				}
				for (SalesHead saleHead : SalesHeads.getSalesHeadsList()) {
					Double amount = 0.00;
					if ((saleHead.name.equals("ACCOUNT RECV"))
							|| (saleHead.name.equals("ACCOUNT SALES"))) {

						for (Terminal terminal : dsr.terminals) {

							for (Shift shift : terminal.shifts) {

								for (ShiftSale shiftSale : shift.shiftSales) {

									if ((shiftSale.salesHead.name
											.equals(saleHead.name) && ((shiftSale.salesHead.name
											.equals("ACCOUNT RECV")) || (shiftSale.salesHead.name
											.equals("ACCOUNT SALES"))))) {

										amount = amount + shiftSale.amount;
									}
								}
							}
						}

						recordVO.add(amount);
					}
				}

				resultVOList.add(recordVO);
			}
			}

		}

		else if (byType.equals("ManagementStoreReconciliationReport")) {

			// add columns

			List columnList = new ArrayList();

			columnList.add("Store Name");
			columnList.add("Date");
			columnList.add("Total Sales");
			columnList.add("Total Media");
			columnList.add("Cash Deposits");
			columnList.add("Cheque Deposits");
			columnList.add("Safe Cash");
			columnList.add("Safe Cheques");
			columnList.add("Variance");
			columnList.add("Last Reporting Date");

			resultVOList.add(columnList);

			// add rows
			for (DailySalesReconciliation dsr : result) {

				List recordVO = new ArrayList();
				if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY

				recordVO.add(dsr.store.name);
				recordVO.add(Application
						.getDateString(dsr.reportingBusinessDate));
				System.out.println("reporting businee date : "
						+ dsr.reportingBusinessDate);
				System.out.println("reporting businee date : "
						+ Application.getDateString(dsr.reportingBusinessDate));
				Long storeid = null;
				Double totalSalesAmount = DailySalesReconciliation
						.getTotalSalesAmount(dsr);
				recordVO.add(totalSalesAmount);
				Double totalMediaAmount = DailySalesReconciliation
						.getTotalMediaCollected(dsr);
				recordVO.add(totalMediaAmount);
				recordVO.add(dsr.dr.bankDeposit.cashAmt);
				recordVO.add(dsr.dr.bankDeposit.chequeAmt);
				String lastReportingDate = DailySalesReconciliation
						.getLastReportingBusinessDateForView(dsr.store.id,
								startDate, endDate);

				Double safeCash=dsr.getCashReported(dsr);
				//recordVO.add(safeCash);
				
				recordVO.add(dsr.dr.close_cash);
				recordVO.add(dsr.dr.close_cheque);
				recordVO.add(DailySalesReconciliation.getVarianceAmount(dsr));
				
				recordVO.add(lastReportingDate);

				// System.out.println("+++++++++++++"+lastReportingDate);
				// System.out.println("+++++++++++++"+recordVO.size());

				resultVOList.add(recordVO);
			}

			}

		} else if (byType.equals("StoreReconciliationReport")) {

			// add columns

			List columnList = new ArrayList();

			columnList.add("Store Name");
			columnList.add("Last Reporting Date");

			resultVOList.add(columnList);

			// add rows
			for (DailySalesReconciliation dsr : result) {

				List recordVO = new ArrayList();
				

				recordVO.add(dsr.store.name);
				String lastReportingDate = DailySalesReconciliation
						.getLastReportingBusinessDateForView(dsr.store.id,
								startDate, endDate);
				if(dsr.dr.status.equals("SUBMITTED")){//IF CONDITION ADDED FOR REPORTS VIEW UPTO DAILYRECONCILATION STATUS SUBMITTED DAYS ONLY

				recordVO.add(lastReportingDate);
				}
				System.out.println("reporting businee date : "+dsr.dr.status);
				// System.out.println("reporting businee date : "+Application.getDateString(dsr.reportingBusinessDate));

				resultVOList.add(recordVO);
			

			}
		}

		return ok(Json.toJson(resultVOList));

	}

}
