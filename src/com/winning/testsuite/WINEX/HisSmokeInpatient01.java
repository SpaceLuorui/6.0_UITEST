package com.winning.testsuite.WINEX;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeInpatient01 extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeInpatient01() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_住院", "大his冒烟测试_住院01.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
	@Test
	public void test_001(){
		init("CASE-01: 患者住院就诊(患者入院预约、登记、缴纳押金、红冲押金、查询、取消)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.admissionAppointment(Data.test_select_subject, Data.inpatient_select_ward);
		String patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.admissionRegistration(patientName);
		browser.wnWINEXWorkflow.loginMenuByName("住院预交金");
		browser.wnWINEXWorkflow.searchPatientByName(patientName);
		browser.wnwd.checkElementByXpath("加载患者完成标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		browser.wnWINEXWorkflow.depositPayment("100");
		browser.wnWINEXWorkflow.searchPatientByName(patientName);
		browser.wnwd.checkElementByXpath("加载患者完成标识","//div[contains(@class,'info-layout')]//div[contains(text(),'"+patientName+"')]",Framework.defaultTimeoutMax);
		browser.wnWINEXWorkflow.reversePayment();
		browser.wnWINEXWorkflow.queryHospitalized(patientName);
		browser.wnWINEXWorkflow.cancelHospitalized(patientName);
	}
	
}
