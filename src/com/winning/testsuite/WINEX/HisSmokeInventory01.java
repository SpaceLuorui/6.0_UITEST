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
public class HisSmokeInventory01 extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeInventory01() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_进销存", "大his冒烟测试_进销存01.html");
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
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String stockBefore = browser.wnWINEXWorkflow.queryStockByMedicineName(Data.WINEX_stock_drug2);
		stockBefore = Pattern.compile("[^0-9]").matcher(stockBefore).replaceAll("").trim(); 
		browser.wnWINEXWorkflow.loginMenuByName("报溢入库");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		browser.wnWINEXWorkflow.addByMedicineStockInMore(Data.WINEX_stock_drug2,"5");
		
		browser.wnWINEXWorkflow.loginMenuByName("库存查询");
		browser.wnWINEXWorkflow.pharmacySelection(Data.WINEX_pharmacy_name1);
		String stockAfter = browser.wnWINEXWorkflow.queryStockByMedicineName(Data.WINEX_stock_drug2);
		stockAfter = Pattern.compile("[^0-9]").matcher(stockAfter).replaceAll("").trim(); 
		browser.logger.assertFalse(Long.parseLong(stockAfter)-Long.parseLong(stockBefore)!=5, "库存更新错误,库存预期增加应该为5，实际增加为"+(Long.parseLong(stockAfter)-Long.parseLong(stockBefore)));
		
	}
		
}