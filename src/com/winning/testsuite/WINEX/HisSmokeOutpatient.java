package com.winning.testsuite.WINEX;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeOutpatient extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;
	public static String patientName = "";
	
	public HisSmokeOutpatient() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_门诊", "大his冒烟测试_门诊.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX_35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

	@Test
	public void test_001(){
		init("CASE-01: 患者门诊就诊(患者建档、挂号、查询、退号、预约挂号、预约取消)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
		patientName = patInfo.get(0);
		browser.wnWINEXWorkflow.encounter(patientName, Data.test_select_subject, Data.default_user_login_account);
		browser.wnWINEXWorkflow.queryEncounterRecord(patientName,Data.test_select_subject);
		browser.wnWINEXWorkflow.cancelEncounter(patientName, Data.test_select_subject);
		ArrayList<String> info = browser.wnWINEXWorkflow.appointmentRegister(patientName);
		if(info.get(0).equals("false")){
			browser.logger.boxLog(0, "暂无可预约班次", "");
			return;
		}
		browser.wnWINEXWorkflow.queryAppointmentRegister(patientName,info.get(1));
		browser.wnWINEXWorkflow.cancelAppointmentRegister(patientName,info.get(1));
	}
		
	@Test
	public void test_002() throws UnsupportedEncodingException{
		init("CASE-02: 门诊-大his流程——挂号、门诊开立、签署、门诊收费、未发药退费", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
		patientName = patInfo.get(0);
		ArrayList<String> info = browser.wnWINEXWorkflow.encounter(patientName, Data.test_select_subject, Data.default_user_login_account);
		
		String visitNumber = info.get(0);  //挂号号序
		String outpatientNumber = info.get(1);  //门诊号
		
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(patientName, visitNumber);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		browser.wnOutpatientWorkflow.signOff(5000);
		
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.SetFIN03010(Data.his_host, "0", "否");
		String cost = browser.wnWINEXWorkflow.outpatientCharges(patientName);
		browser.wnWINEXWorkflow.outpatientChargesBack(outpatientNumber,patientName,cost);
	}

	@Test
	public void test_003() throws UnsupportedEncodingException{
		init("CASE-03: 门诊-大his流程——挂号、门诊开立、签署、收费、发药、退药、退费", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.outpatientInformationSetUp();
		patientName = patInfo.get(0);
		ArrayList<String> info = browser.wnWINEXWorkflow.encounter(patientName, Data.test_select_subject, Data.default_user_login_account);
		
		String visitNumber = info.get(0);  //挂号号序
		String outpatientNumber = info.get(1); //门诊号

		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(patientName, visitNumber);
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);

		browser.wnOutpatientWorkflow.signOff(5000);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnWINEXWorkflow.SetFIN03010(Data.his_host, "0", "否");
		Map<String, String> pharmacyInfo = browser.wnWINEXWorkflow.getPharmacyInfoByPatientName(patientName);
		String pharmacyNo = pharmacyInfo.get("ORG_NO");
		String pharmacyName = pharmacyInfo.get("ORG_NAME");
		String pharmacyId = browser.decouple.db60.getOrgIdByOrgName(pharmacyName, pharmacyNo);
		browser.wnWINEXWorkflow.updateReviewerStatus(pharmacyId, pharmacyName, pharmacyNo, "98176", "98176");
		String cost = browser.wnWINEXWorkflow.outpatientCharges(patientName);
		browser.wnWINEXWorkflow.queryOutpatientCharges(patientName, cost);
		browser.wnWINEXWorkflow.OutpatientDispensing(Data.windowsName, patientName);
		browser.wnWINEXWorkflow.OutpatientDispensingBack(pharmacyName, patientName);
		browser.wnWINEXWorkflow.outpatientChargesBack(outpatientNumber,patientName,cost);
	}	
}
