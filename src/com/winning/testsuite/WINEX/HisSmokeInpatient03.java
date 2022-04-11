package com.winning.testsuite.WINEX;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;
import xunleiHttpTest.HttpTestHeader;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeInpatient03 extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeInpatient03() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_住院", "大his冒烟测试_住院03.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
	
	@Test
	public void test_003() throws UnsupportedEncodingException{
		Data.host=Data.inpatient_host;
		init("CASE-03: 住院-大his完整全流程(入院预约、登记、住院入区、开立检查、签署、签收申请执行、中途结算、取消结算、出区、出院结算)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.admissionAppointment(Data.test_select_subject, Data.inpatient_select_ward);
		String patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.admissionRegistration(patientName);
		browser.wnwd.openUrl("http://" + Data.host + "/portal/login");
		browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnInpatientWorkflow.setParamsForTestAllService();
		browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
		String BedNo =browser.wnInpatientWorkflow.getEmptyBedNo();
		browser.wnInpatientWorkflow.addBed(BedNo);
		browser.wnInpatientWorkflow.enterWard(patientName,BedNo);
		try {
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientResidentStation(Data.test_select_subject, Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);   //开立检查
			browser.wnInpatientWorkflow.signOff(1000);
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnwd.waitElementByXpathAndClick("点击医嘱管理标签", "//span[contains(text(),'医嘱管理')]", Framework.defaultTimeoutMax);
			browser.wnInpatientWorkflow.signFor();
			browser.wnInpatientWorkflow.orderApply();
			browser.wnInpatientWorkflow.orderApplication();
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnWINEXWorkflow.inpatientCharges(patientName);
			browser.wnWINEXWorkflow.cancelInpatientCharges(patientName);
			HttpTestHeader header = browser.wnWINEXWorkflow.confusionLoginForWINEX(Data.inpatient_host,Data.default_user_login_account, Data.default_user_login_pwd);
			String loginEmployeeId =browser.decouple.db60.getEmployeeIdByEmployeeNo(Data.default_user_login_account);
			browser.wnWINEXWorkflow.outhospitalByInterface(header, loginEmployeeId, patientName);
			browser.wnWINEXWorkflow.inpatientCharges(patientName);
			browser.wnWINEXWorkflow.cancelInpatientCharges(patientName);
		} catch (Throwable e) {
			throw new Error(e.getMessage()+"  患者姓名："+patientName);
		}
		
	}
	
}
