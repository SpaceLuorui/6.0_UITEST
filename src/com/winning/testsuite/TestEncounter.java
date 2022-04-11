package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEncounter extends OutpatientTestBase {

	public TestEncounter() {
		super();
	}

	static {
		Data.getScreenShot = true;
		SdkTools.initReport("挂号同步专项", "挂号同步专项.html");
	}

	@Test
	public void case_01() throws InterruptedException {
		init("Case01 -常规挂号：年龄：15岁，性别：男", true);
		Boolean sucFlag = true;
		Data.patientage = 15;
		
		ArrayList<String> patInfo = browser.decouple.newEncounter(null, Data.newEncounterSubjectCode,"男");
		String ybsm = browser.decouple.getYbsmFromYBFLKByYbdm(Data.ybdm);
		String subject =  browser.decouple.getNameFromKSBMKById(Data.newEncounterSubjectCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		Map<String,String> encounterDetail = browser.wnOutpatientWorkflow.getEncounterDetail();
		
		if(encounterDetail.get("age")!=""||encounterDetail.get("age")!=null||encounterDetail.get("age").contains(Data.patientage.toString())||encounterDetail.get("age").contains(String.valueOf(Data.patientage-1))) {
			browser.logger.boxLog(1, "检查年龄同步", "挂号年龄同步正常，期望年龄为："+Data.patientage+",实际年龄为："+encounterDetail.get("age"));
		}else {
			browser.logger.boxLog(2, "挂号年龄同步异常","期望年龄为："+Data.patientage+",实际年龄为："+encounterDetail.get("age"));
			sucFlag = false;
		}
		
		if(encounterDetail.get("sex")==null||encounterDetail.get("sex")==""||!encounterDetail.get("sex").contains("男")) {
			browser.logger.boxLog(2, "挂号性别同步异常","期望性别为：男,实际性别为："+encounterDetail.get("sex"));
			sucFlag = false;
		}else {
			browser.logger.boxLog(1, "检查性别同步", "挂号性别同步正常，期望性别为：男，实际性别为：男");

		}
		
		if(encounterDetail.get("subject")==null||encounterDetail.get("subject")==""||!subject.contains(encounterDetail.get("subject"))) {
			browser.logger.boxLog(2,"挂号科目同步异常","期望科目为："+subject+",实际科目为："+encounterDetail.get("subject"));
			sucFlag = false;
		}else {
			browser.logger.boxLog(1, "检查科目同步", "挂号科目同步正常，期望科目为："+subject+",实际科目为："+encounterDetail.get("subject"));

		}
		
		if(encounterDetail.get("ybsm")==null||encounterDetail.get("ybsm")==""||!encounterDetail.get("ybsm").contains(ybsm)) {
			browser.logger.boxLog(2,"挂号保险信息同步异常","期望保险信息为："+ybsm+",实际保险信息为："+encounterDetail.get("ybsm"));
			sucFlag = false;
		}else {
			browser.logger.boxLog(1, "检查保险信息同步", "挂号保险信息同步正常，期望保险信息为："+ybsm+",实际保险信息为："+encounterDetail.get("ybsm"));
		}
		
		if(!sucFlag) {
			browser.logger.assertFalse(!sucFlag, "挂号同步信息校验有误，请查看详细报告");
		}
	}

	
	@Test
	public void case_02() throws InterruptedException {
		init("Case02 -常规挂号：年龄：50，性别：女", true);
		Boolean sucFlag = true;
		Data.patientage = 50;
		
		ArrayList<String> patInfo = browser.decouple.newEncounter(null, Data.newEncounterSubjectCode,"女");
		String ybsm = browser.decouple.getYbsmFromYBFLKByYbdm(Data.ybdm);
		String subject =  browser.decouple.getNameFromKSBMKById(Data.newEncounterSubjectCode);
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(patInfo.get(0), patInfo.get(3));
		Map<String,String> encounterDetail = browser.wnOutpatientWorkflow.getEncounterDetail();
		if(encounterDetail.get("age").contains(Data.patientage.toString())||encounterDetail.get("age").contains(String.valueOf(Data.patientage-1))) {
			browser.logger.boxLog(1, "检查年龄同步", "挂号年龄同步正常，期望年龄为："+Data.patientage+",实际年龄为："+encounterDetail.get("age"));
		}else {
			browser.logger.boxLog(2, "挂号年龄同步异常","期望年龄为："+Data.patientage+",实际年龄为："+encounterDetail.get("age"));
			sucFlag = false;
		}
		
		if(!encounterDetail.get("sex").contains("女")) {
			browser.logger.boxLog(2, "挂号性别同步异常","期望性别为：女，实际性别为："+encounterDetail.get("sex"));
			sucFlag = false;
		}else {
			browser.logger.boxLog(1, "检查性别同步", "挂号性别同步正常，期望性别为：女，实际性别为：女");
		}
		
		if(!subject.contains(encounterDetail.get("subject"))) {
			browser.logger.boxLog(2,"挂号科目同步异常","期望科目为："+subject+",实际科目为："+encounterDetail.get("subject"));
			sucFlag = false;
		}else {
			browser.logger.boxLog(1, "检查科目同步", "挂号科目同步正常，期望科目为："+subject+",实际科目为："+encounterDetail.get("subject"));
		}
		
		if(!encounterDetail.get("ybsm").contains(ybsm)) {
			browser.logger.boxLog(2,"挂号保险信息同步异常","期望保险信息为："+ybsm+",实际保险信息为："+encounterDetail.get("ybsm"));
			sucFlag = false;
		}else {
			browser.logger.boxLog(1, "检查保险信息同步", "挂号保险信息同步正常，期望保险信息为："+ybsm+",实际保险信息为："+encounterDetail.get("ybsm"));
		}
		
		if(!sucFlag) {
			browser.logger.assertFalse(!sucFlag, "挂号同步信息校验有误，请查看详细报告");
		}
	}
	
	@Before
	public void setUp() throws Exception {
		if (Data.useHybirdApp) {
			SdkSystemProcess.stopExeApp("chrome.exe");
			SdkSystemProcess.stopExeApp("Win60.exe");
			SdkSystemProcess.stopExeApp("Win6.0.exe");
//				String startHybirdAppCmd = "cmd /c "+Data.hybirdAppPath + " " + "--remote-debugging-port=" + Data.hybirdAppPort;
//				Cmd.startExeApp(startHybirdAppCmd);
		}
	}

}
