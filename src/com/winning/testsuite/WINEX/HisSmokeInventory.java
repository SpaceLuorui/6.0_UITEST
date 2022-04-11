package com.winning.testsuite.WINEX;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeInventory extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeInventory() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_进销存", "大his冒烟测试_进销存.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

	@Test
	public void test_001(){
		init("CASE-01:报损出库，部门出库，职工出库 ", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		
		browser.wnWINEXWorkflow.loginMenuByName("报损出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockOutLoss(Data.WINEX_stock_drug);
		
		browser.wnWINEXWorkflow.loginMenuByName("部门出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockOutDepartment(Data.WINEX_stock_drug);
		
		browser.wnWINEXWorkflow.loginMenuByName("职工出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockOutWorkers(Data.WINEX_stock_drug);
	}
	
	@Test
	public void test_002(){
		init("CASE-02:报溢入库，库存查询变化 ", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("库存查询");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		String stockBefore = browser.wnWINEXWorkflow.queryStockByMedicineName(Data.WINEX_stock_drug);
		stockBefore = Pattern.compile("[^0-9]").matcher(stockBefore).replaceAll("").trim(); 
		browser.wnWINEXWorkflow.loginMenuByName("报溢入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.addByMedicineStockInMore(Data.WINEX_stock_drug,"5");
		
		browser.wnWINEXWorkflow.loginMenuByName("库存查询");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		String stockAfter = browser.wnWINEXWorkflow.queryStockByMedicineName(Data.WINEX_stock_drug);
		stockAfter = Pattern.compile("[^0-9]").matcher(stockAfter).replaceAll("").trim(); 
		browser.logger.assertFalse(Long.parseLong(stockAfter)-Long.parseLong(stockBefore)!=5, "库存更新错误,库存预期增加应该为5，实际增加为"+(Long.parseLong(stockAfter)-Long.parseLong(stockBefore)));
		
		
	}
	@Test
	public void test_003(){
		init("CASE-03: 请调流程(请调申请，请调出库，请调入库)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("请调申请");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String billNo = browser.wnWINEXWorkflow.addByApplicationForTransfer(Data.WINEX_stock_drug,Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.loginMenuByName("请调出库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		billNo = browser.wnWINEXWorkflow.dealwithByMedicineStockOutTransfer(billNo);
		browser.wnWINEXWorkflow.loginMenuByName("请调入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.dealwithByMedicineStockInTransfer(billNo);
		
	}
	
	@Test
	public void test_004(){
		init("CASE-04: 请调退库(请调退库，请调退库接收)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("请调退库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String billNo = browser.wnWINEXWorkflow.dealwithByMedicineStockReturnTransfer(Data.WINEX_stock_drug,Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.loginMenuByName("请调退库接收");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name2);
		browser.wnWINEXWorkflow.dealwithByMedicineStockReceiveTransfer(billNo);
	}
	
	@Test
	public void test_005(){
		init("CASE-05: 采购入库(采购入库，采购退货)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.loginMenuByName("采购入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
	    String billNo = browser.wnWINEXWorkflow.addByMedicineStockInPurchase(Data.WINEX_stock_drug);
	    browser.wnWINEXWorkflow.loginMenuByName("采购退货");
	    browser.wnWINEXWorkflow.addByMedicineStockRuturnPurchase(Data.WINEX_stock_drug,billNo);
	}
		
		
		
}