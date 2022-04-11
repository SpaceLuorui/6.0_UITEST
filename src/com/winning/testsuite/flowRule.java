package com.winning.testsuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.base.TestBase;
import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class flowRule extends OutpatientTestBase {

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("医嘱流向专项","医嘱流向.html","预期流向","实际流向");
		try{
			Config.loadOnlineDefaultConfig("DEV");
			Config.loadOnlineExtraConfig("DEV","autoTest");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_01(){
		init("CASE-01:西成药流向", true);
		//查找6个可开立药品
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);		
		TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   药品名称:"+drug.get("NAME"));
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//查找挂号科室对应的业务单元ID
		String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//计算预期流向
		String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("药品", drug.get("CS_ID"), orgID);
		TestBase.currentCase.put("START",expectedPharmacy);
		if (expectedPharmacy.equals("@开立科室")) {
			expectedPharmacy = Data.test_select_subject;
		}
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//打开加工厂并获取默认值
        int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(drug.get("NAME"), "5", "medicineId", drug.get("MEDICINE_ID"));
        browser.wnOutpatientWorkflow.searchOrderBySeq("西成药", drug.get("NAME"), medicineSeq);
        browser.wnOutpatientWorkflow.beforeFactory();
        PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editDrugFactory(null);
		TestBase.currentCase.put("END",default_detail.pharmacy);
        browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
	}
	
	@Test
	public void test_02(){
		init("CASE-02:中成药流向", true);
		//查找6个可开立的中成药
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98364").get(0);	
		TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   药品名称:"+drug.get("NAME"));
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//查找挂号科室对应的业务单元ID
		String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//计算预期流向
		String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("药品", drug.get("CS_ID"), orgID);
		TestBase.currentCase.put("START",expectedPharmacy);
		if (expectedPharmacy.equals("@开立科室")) {
			expectedPharmacy = Data.test_select_subject;
		}
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//打开加工厂并获取默认值
        int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(drug.get("NAME"), "5", "medicineId", drug.get("MEDICINE_ID"));
        browser.wnOutpatientWorkflow.searchOrderBySeq("西成药", drug.get("NAME"), medicineSeq);
        browser.wnOutpatientWorkflow.beforeFactory();
        PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editDrugFactory(null);
        TestBase.currentCase.put("END",default_detail.pharmacy);
        browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
	}
		
	@Test
	public void test_03(){
		init("CASE-03:中草药流向", true);
		//查找6个可开立的中草药
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98365").get(0);	
		TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   药品名称:"+drug.get("NAME"));
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//查找挂号科室对应的业务单元ID
		String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//计算预期流向
		String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("药品", drug.get("CS_ID"), orgID);
		TestBase.currentCase.put("START",expectedPharmacy);
		if (expectedPharmacy.equals("@开立科室")) {
			expectedPharmacy = Data.test_select_subject;
		}
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//打开加工厂并获取默认值
        int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(drug.get("NAME"), "9", "medicineId", drug.get("MEDICINE_ID"));
        browser.wnOutpatientWorkflow.searchOrderBySeq("中草药", drug.get("NAME"), medicineSeq);
        browser.wnOutpatientWorkflow.beforeFactory();
        PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editHerbFactory(null);
        TestBase.currentCase.put("END",default_detail.pharmacy);
        browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
	}

	
	@Test
	public void test_04(){
		init("CASE-04:检验(临检常规)流向:", true);
		//查找6个可开立的临检常规服务
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.LJCG.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无临检常规服务数据");
		}else{
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检验", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "3", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检验", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editLabFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_05(){
		init("CASE-05:检验(生化常规)流向:", true);
		//查找6个可开立的生化常规服务
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.SHCG.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无生化常规服务数据");
		}else{
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检验",cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "3", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检验", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editLabFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_06(){
		init("CASE-06:检验(免疫常规)流向:", true);
		//查找6个可开立的免疫常规
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.MYCG.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无免疫常规服务数据");
		}else{
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检验", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "3", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检验", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editLabFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	
	@Test
	public void test_07(){
		init("CASE-07:检验(微生物常规)流向:", true);
		//查找6个可开立的微生物常规服务
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.WSWCG.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无微生物常规服务数据");
		}else{
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检验", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "3", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检验", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editLabFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}

	}
	
	@Test
	public void test_08(){
		init("CASE-08:病理流向:", true);
		//查找6个可开立的病理
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.BL.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无病理流向服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("病理", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "11", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("病理", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editPathologyFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_09(){
		init("CASE-09:常规病理流向:", true);
		//查找6个可开立的常规病理
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.CGBL.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无常规病理流向服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("病理", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "11", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("病理", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editPathologyFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_10(){
		init("CASE-10:治疗(一般治疗)流向:", true);
		//查找6个可开立的一般治疗
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.YBZL.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无一般治疗服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("一般治疗", cs.get("CS_ID"),orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "2", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("治疗", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editTreatFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_11(){
		init("CASE-11:治疗(普通治疗)流向:", true);
		//查找6个可开立的普通治疗
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.PTZL.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无普通治疗服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("一般治疗", cs.get("CS_ID"),orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "2", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("治疗", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editTreatFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_12(){
		init("CASE-12:治疗(放射治疗)流向:", true);
		//查找6个可开立的放射治疗
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.FSZL.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无放射治疗服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("一般治疗", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "2", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("治疗", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editTreatFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	
	@Test
	public void test_13(){
		init("CASE-13:检查流向:", true);
		//查找6个可开立的检查
		Map<String, String> cs = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.JC.code).get(0);
		TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//查找挂号科室对应的业务单元ID
		String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//计算预期流向
		String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
		TestBase.currentCase.put("START",expectedPharmacy);
		if (expectedPharmacy.equals("@开立科室")) {
			expectedPharmacy = Data.test_select_subject;
		}
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//打开加工厂并获取默认值
		int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
		browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
		browser.wnOutpatientWorkflow.beforeFactory();
		PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
		TestBase.currentCase.put("END",default_detail.pharmacy);
		browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
	}
	
	@Test
	public void test_14(){
		init("CASE-14:检查(普放)流向:", true);
		//查找6个可开立的普放检查
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.PF.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无普放检查服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_15(){
		init("CASE-15:检查(超声)流向:", true);
		//查找6个可开立的超声检查
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.CS.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无超声检查服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_16(){
		init("CASE-16:检查(CT)流向:", true);
		//查找6个可开立的CT检查服务
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.CT.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无CT检查服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_17(){
		init("CASE-17:检查(MRI)流向:", true);
		//查找6个可开立的MRI检查
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.MRI.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无MRI检查服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_18(){
		init("CASE-18:检查(内镜)流向:", true);
		//查找6个可开立的内镜检查
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.NJ.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无内镜检查服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	@Test
	public void test_19(){
		init("CASE-19:检查(电生理)流向:", true);
		//查找6个可开立的电生理检查
		List<Map<String, String>> csList = browser.decouple.db60.getCsByCsCategoryId(Data.csTypeCode.DSL.code);
		if ( null==csList || csList.size()<1 ) {
			browser.logger.log(3, "无电生理检查服务数据");
		}else {
			Map<String, String> cs = csList.get(0);
			TestBase.currentCase.put("CASE_NAME", TestBase.currentCase.get("CASE_NAME")+"   服务名称:"+cs.get("CS_NAME"));
			//挂号
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			//查找挂号科室对应的业务单元ID
			String orgID = browser.decouple.db60.getOrgIdByOrgName(Data.test_select_subject,Data.newEncounterSubjectCode);
			//登录
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			//计算预期流向
			String expectedPharmacy = browser.wnOutpatientWorkflow.getBizUnitName("检查", cs.get("CS_ID"), orgID);
			TestBase.currentCase.put("START",expectedPharmacy);
			if (expectedPharmacy.equals("@开立科室")) {
				expectedPharmacy = Data.test_select_subject;
			}
			//叫号
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
			//打开加工厂并获取默认值
			int medicineSeq = browser.wnOutpatientWorkflow.getMedicineSeqByApi(cs.get("CS_NAME"), "4", "csId", cs.get("CS_ID"));
			browser.wnOutpatientWorkflow.searchOrderBySeq("检查", cs.get("CS_NAME"), medicineSeq);
			browser.wnOutpatientWorkflow.beforeFactory();
			PrescribeDetail default_detail = browser.wnOutpatientWorkflow.editExamFactory(null);
			TestBase.currentCase.put("END",default_detail.pharmacy);
			browser.logger.assertFalse(!default_detail.pharmacy.equals(expectedPharmacy), "默认药房应为:"+expectedPharmacy);
		}
	}
	
	
}