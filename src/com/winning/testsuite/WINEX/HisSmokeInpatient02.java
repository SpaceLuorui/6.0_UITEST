package com.winning.testsuite.WINEX;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Inpatient.WnInpatientXpath;
import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeInpatient02 extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeInpatient02() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_住院", "大his冒烟测试_住院02.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
	
	@Test
	public void test_002() throws UnsupportedEncodingException{
		Data.host=Data.inpatient_host;
		init("CASE-02: 住院-大his完整全流程(入院预约、登记、住院入区、开立签署、签收申请执行、HIS发药、住院退药申请、HIS退药)", true);
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
			browser.wnInpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);   //开立西药
			browser.wnInpatientWorkflow.signOff(5000);
			browser.wnwd.waitElementByXpathAndClick("返回portal页面", WnInpatientXpath.portalInpatient, Framework.defaultTimeoutMid);
			browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.callNumberByName(patientName);
			browser.wnwd.waitElementByXpathAndClick("点击医嘱管理标签", "//span[contains(text(),'医嘱管理')]", Framework.defaultTimeoutMax);
			browser.wnInpatientWorkflow.signFor();
			browser.wnInpatientWorkflow.orderApply();
			browser.wnInpatientWorkflow.orderApplication();
			Map<String, String> pharmacyInfo = browser.wnWINEXWorkflow.getPharmacyInfoByPatientName(patientName);
			String pharmacyNo = pharmacyInfo.get("ORG_NO");
			String pharmacyName = pharmacyInfo.get("ORG_NAME");
			//大his进行住院发药
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnWINEXWorkflow.SetFIN03010(Data.his_host, "0", "否");
			String pharmacyId = browser.decouple.db60.getOrgIdByOrgName(pharmacyName, pharmacyNo);
			browser.wnWINEXWorkflow.updateReviewerStatus(pharmacyId, pharmacyName, pharmacyNo, "98176", "98176");
			browser.wnWINEXWorkflow.InpatientDispensing(pharmacyName, patientName);
			//大临床提交退药申请
			browser.wnwd.openUrl("http://" + Data.host + "/portal/login");
			browser.wnInpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//			browser.wnWINEXWorkflow.SetMT0028(Data.host, "0", "允许");
			browser.wnInpatientWorkflow.loginInpatientNurseStation(Data.default_user_login_account,Data.inpatient_select_ward);
			browser.wnInpatientWorkflow.dispensingBackApply(patientName);
			//大his进行住院退药
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnWINEXWorkflow.inpatientDispensingBack(pharmacyName, patientName);
		} catch (Throwable e) {
			throw new Error(e.getMessage()+"  患者姓名："+patientName);
		}
	}
	
	
}
