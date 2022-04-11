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
public class HisSmokeOutpatient01 extends WINEXTestBase {

	public static ArrayList<String> patInfo=null;
	public static String patientName = "";
	
	public HisSmokeOutpatient01() {
		super();
	}

    static{
    	SdkTools.initReport("大his冒烟_门诊", "大his冒烟测试_门诊01.html");
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
		
}
