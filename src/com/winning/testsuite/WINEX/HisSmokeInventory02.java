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
public class HisSmokeInventory02 extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeInventory02() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_进销存", "大his冒烟测试_进销存02.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
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