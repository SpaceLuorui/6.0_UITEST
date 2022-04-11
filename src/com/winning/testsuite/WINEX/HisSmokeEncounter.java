package com.winning.testsuite.WINEX;
import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HisSmokeEncounter extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;

	public HisSmokeEncounter() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟", "大his冒烟测试_挂号入院.html");
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
		browser.wnWINEXWorkflow.encounter(patInfo.get(0), Data.test_select_subject, Data.default_user_login_account);
		browser.wnWINEXWorkflow.queryEncounterRecord(patInfo.get(0),Data.test_select_subject);
		browser.wnWINEXWorkflow.cancelEncounter(patInfo.get(0), Data.test_select_subject);
		ArrayList<String> info = browser.wnWINEXWorkflow.appointmentRegister(patInfo.get(0));
		if(info.get(0).equals("false")){
			browser.logger.boxLog(0, "暂无可预约班次", "");
			return;
		}
		browser.wnWINEXWorkflow.queryAppointmentRegister(patInfo.get(0),info.get(1));
		browser.wnWINEXWorkflow.cancelAppointmentRegister(patInfo.get(0),info.get(1));
	}
		
	@Test
	public void test_002(){
		init("CASE-02: 患者住院就诊(患者入院预约、登记、查询、取消)", true);
		browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
		browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		ArrayList<String> patInfo = browser.wnWINEXWorkflow.admissionAppointment(Data.test_select_subject, Data.inpatient_select_ward);
		browser.wnWINEXWorkflow.admissionRegistration(patInfo.get(0));
		browser.wnWINEXWorkflow.queryHospitalized(patInfo.get(0));
		browser.wnWINEXWorkflow.cancelHospitalized(patInfo.get(0));
	}
}
