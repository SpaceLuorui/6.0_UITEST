package com.winning.testsuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestInsurance extends OutpatientTestBase {
	
	public static String insuranceID = "57393491145648129";
	public static String soid = "256181";
	public static String herbOrgName = "草药房";
	public static String drugOrgName = "门诊药房";
	public static String herbCode = "98365";
	public static String drugCode = "98363";
	public static String examCode = "98078";
	public static String labCode = "98071";
	public static String treatCode = "98098";
	public static String pathologyCode = "98088";
	public static String bsSelf = "256162";
	
	
	
	public TestInsurance() {
		super();
//		TestRunner2.retryCount = 3;
	}

	static {
		Data.getScreenShot=true;
		Framework.savereportFile = Framework.savereportFile.replace("report.html", "insurance.html");
		SdkTools.initReport("医保审批专项","insurance.html");	
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","autoTest");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
	}
	
	
	@Test
	public void test_01() throws InterruptedException {
		init("CASE-01: 单个西药-需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> drug = browser.decouple.db60.getInsurApprovalMedicine(drugOrgName, drugCode,insuranceID,soid);
		browser.logger.log(1,"找到需要审批的药品:"+drug);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> insurOrderItem = SdkTools.getMapByValue(insurOrderItems, "MEDICINE_ID", drug.get("MEDICINE_ID"));
		browser.logger.assertFalse(insurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.boxLog(1, "医保医嘱项落库检查通过", ""+insurOrderItem);
	}
	
	
	
	
	@Test
	public void test_02() throws InterruptedException {
		init("CASE-02: 单个西药-不需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> drug = browser.decouple.db60.getNotInsurApprovalMedicine(drugOrgName, drugCode,insuranceID,soid);
		browser.logger.log(1,"不需要审批的药品:"+drug);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null, false);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		browser.logger.assertFalse(insurOrderItems.size()!=0, "非医保医嘱项 不应落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	
	@Test
	public void test_03() throws InterruptedException {
		init("CASE-03: 中草药-需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> herb = browser.decouple.db60.getInsurApprovalMedicine(herbOrgName, herbCode,insuranceID,soid);
		browser.logger.log(1,"需要审批的药品:"+herb + ",id=" + herb.get("MEDICINE_ID"));
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, herb.get("MEDICINE_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> insurOrderItem = SdkTools.getMapByValue(insurOrderItems, "MEDICINE_ID", herb.get("MEDICINE_ID"));
		System.out.println("->" + insurOrderItems.toString());
		browser.logger.assertFalse(insurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.boxLog(1, "医保医嘱项落库检查通过", ""+insurOrderItem);
	}
	
	@Test
	public void test_04() throws InterruptedException {
		init("CASE-04: 单个中草药-不需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> herb = browser.decouple.db60.getNotInsurApprovalMedicine(herbOrgName, herbCode,insuranceID,soid);
		browser.logger.log(1,"不需要审批的药品:"+herb);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, herb.get("MEDICINE_ID"), null, false);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		browser.logger.assertFalse(insurOrderItems.size()!=0, "非医保医嘱项 不应落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	
	@Test
	public void test_05() throws InterruptedException {
		init("CASE-05: 单个治疗-需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(treatCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(treatCode,bsSelf,soid);
		for (Map<String, String> insuranceTreat : insuranceTreatList) {
			if (SdkTools.getMapByValue(bsSelfTreatList, "CS_ID", insuranceTreat.get("CS_ID"))!=null) {
				service=insuranceTreat;
				break;
			}
		}
		browser.logger.log(1,"需要审批的本服务计费治疗:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 需要审批的本服务计费治疗");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeTreatByCsid(browser.decouple.db60, service.get("CS_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> insurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", service.get("CHARGING_ITEM_ID"));
		browser.logger.assertFalse(insurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.boxLog(1, "医保医嘱项落库检查通过", ""+insurOrderItem);
	}
	
	@Test
	public void test_06() throws InterruptedException {
		init("CASE-06: 单个治疗-不需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(treatCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(treatCode,bsSelf,soid);
		for (Map<String, String> bsSelfTreat : bsSelfTreatList) {
			if (SdkTools.getMapByValue(insuranceTreatList, "CS_ID", bsSelfTreat.get("CS_ID"))==null) {
				service=bsSelfTreat;
				break;
			}
		}
		browser.logger.log(1,"不需要审批的本服务计费治疗:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 不需要审批的本服务计费治疗");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeTreatByCsid(browser.decouple.db60, service.get("CS_ID"), null, false);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		browser.logger.assertFalse(insurOrderItems.size()!=0, "非医保医嘱项 不应落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	
	@Test
	public void test_07() throws InterruptedException {
		init("CASE-07: 单个检查-需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> exam_item = null;
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(examCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(examCode,bsSelf,soid);
		for (Map<String, String> insuranceTreat : insuranceTreatList) {
			if (SdkTools.getMapByValue(bsSelfTreatList, "CS_ID", insuranceTreat.get("CS_ID"))!=null) {
				List<Map<String, String>> exam_item_list=browser.decouple.db60.getExamItemListByCsid(insuranceTreat.get("CS_ID"));
				if (exam_item_list.size()>0) {
					exam_item=exam_item_list.get(0);
					service = insuranceTreat;
					break;
				}				
			}
		}
		browser.logger.log(1,"需要审批的检查项目:"+exam_item);
		browser.logger.assertFalse(exam_item==null, "当前环境中未取到 需要审批的检查");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, exam_item.get("EXAM_ITEM_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> insurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", service.get("CHARGING_ITEM_ID"));
		browser.logger.assertFalse(insurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.boxLog(1, "医保医嘱项落库检查通过", ""+insurOrderItem);
	}
	
	@Test
	public void test_08() throws InterruptedException {
		init("CASE-08: 单个检查-不需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> exam_item = null;
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(examCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(examCode,bsSelf,soid);
		for (Map<String, String> bsSelfTreat : bsSelfTreatList) {
			if (SdkTools.getMapByValue(insuranceTreatList, "CS_ID", bsSelfTreat.get("CS_ID"))==null) {
				List<Map<String, String>> exam_item_list=browser.decouple.db60.getExamItemListByCsid(bsSelfTreat.get("CS_ID"));
				if (exam_item_list.size()>0) {
					exam_item=exam_item_list.get(0);
					service = bsSelfTreat;
					break;
				}
			}
		}
		browser.logger.log(1,"不需要审批的本服务计费治疗:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 不需要审批的本服务计费治疗");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, exam_item.get("EXAM_ITEM_ID"), null, false);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		browser.logger.assertFalse(insurOrderItems.size()!=0, "非医保医嘱项 不应落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	
	@Test @Ignore
	public void test_09() throws InterruptedException {
		init("CASE-09: 单个检验-需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(labCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(labCode,bsSelf,soid);
		for (Map<String, String> insuranceTreat : insuranceTreatList) {
			if (SdkTools.getMapByValue(bsSelfTreatList, "CS_ID", insuranceTreat.get("CS_ID"))!=null) {
				service=insuranceTreat;
				break;
			}
		}
		browser.logger.log(1,"需要审批的本服务计费检验:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 需要审批的本服务计费检验");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeLabByCsid(browser.decouple.db60, service.get("CS_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> insurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", service.get("CHARGING_ITEM_ID"));
		browser.logger.assertFalse(insurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.boxLog(1, "医保医嘱项落库检查通过", ""+insurOrderItem);
	}
	
	@Test @Ignore
	public void test_10() throws InterruptedException {
		init("CASE-10: 单个检验-不需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(labCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(labCode,bsSelf,soid);
		for (Map<String, String> bsSelfTreat : bsSelfTreatList) {
			if (SdkTools.getMapByValue(insuranceTreatList, "CS_ID", bsSelfTreat.get("CS_ID"))==null) {
				service=bsSelfTreat;
				break;
			}
		}
		browser.logger.log(1,"不需要审批的本服务计费检验:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 不需要审批的本服务计费检验");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeLabByCsid(browser.decouple.db60, service.get("CS_ID"), null, false);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		browser.logger.assertFalse(insurOrderItems.size()!=0, "非医保医嘱项 不应落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	

	@Test @Ignore
	public void test_11() throws InterruptedException {
		init("CASE-11: 单个病理-需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(pathologyCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(pathologyCode,bsSelf,soid);
		for (Map<String, String> insuranceTreat : insuranceTreatList) {
			if (SdkTools.getMapByValue(bsSelfTreatList, "CS_ID", insuranceTreat.get("CS_ID"))!=null) {
				service=insuranceTreat;
				break;
			}
		}
		browser.logger.log(1,"需要审批的本服务计费病理:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 需要审批的本服务计费病理");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribePathologyByCsid(browser.decouple.db60, service.get("CS_ID"), null, true);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> insurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", service.get("CHARGING_ITEM_ID"));
		browser.logger.assertFalse(insurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.boxLog(1, "医保医嘱项落库检查通过", ""+insurOrderItem);
	}
	
	@Test @Ignore
	public void test_12() throws InterruptedException {
		init("CASE-12: 单个病理-不需要审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> service = null;
		List<Map<String, String>> insuranceTreatList = browser.decouple.db60.getInsurApprovalService(pathologyCode,insuranceID,soid);
		List<Map<String, String>> bsSelfTreatList = browser.decouple.db60.getServiceListByStrategy(pathologyCode,bsSelf,soid);
		for (Map<String, String> bsSelfTreat : bsSelfTreatList) {
			if (SdkTools.getMapByValue(insuranceTreatList, "CS_ID", bsSelfTreat.get("CS_ID"))==null) {
				service=bsSelfTreat;
				break;
			}
		}
		browser.logger.log(1,"不需要审批的本服务计费病理:"+service);
		browser.logger.assertFalse(service==null, "当前环境中未取到 不需要审批的本服务计费病理");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
		
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribePathologyByCsid(browser.decouple.db60, service.get("CS_ID"), null, false);
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		browser.logger.assertFalse(insurOrderItems.size()!=0, "非医保医嘱项 不应落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	
	@Test @Ignore
	public void test_13() throws InterruptedException {
		init("CASE-13: 多类型医嘱 - 需要医保审批", true);
		//获取数据
		Map<String, String> insurance = browser.decouple.db60.getInsuranceListByID(insuranceID);
		browser.logger.assertFalse(!insurance.get("IS_DEL").equals("0"), "医保被删除");
		browser.logger.assertFalse(!insurance.get("ENABLED_FLAG").equals("98175"), "医保被停用");
		browser.logger.assertFalse(!insurance.get("MED_INSUR_APPROVAL_NEEDED_FLAG").equals("98175"), "医保被停用");
		Map<String, String> drug = browser.decouple.db60.getInsurApprovalMedicine(drugOrgName, drugCode,insuranceID,soid);
		Map<String, String> herb = browser.decouple.db60.getInsurApprovalMedicine(herbOrgName, herbCode,insuranceID,soid);
		List<Map<String, String>> bsSelfServiceList = browser.decouple.db60.getServiceListByStrategy(null,bsSelf,soid);
		Map<String, String> exam = null;
		Map<String, String> exam_item = null;
		Map<String, String> lab = null;
		Map<String, String> treat = null;
		Map<String, String> pathology = null;
		List<Map<String, String>> insuranceServiceList = browser.decouple.db60.getInsurApprovalService(examCode,insuranceID,soid);
		for (Map<String, String> insuranceService : insuranceServiceList) {
			if (SdkTools.getMapByValue(bsSelfServiceList, "CS_ID", insuranceService.get("CS_ID"))!=null) {
				List<Map<String, String>> exam_item_list=browser.decouple.db60.getExamItemListByCsid(insuranceService.get("CS_ID"));
				if (exam_item_list.size()>0) {
					exam_item=exam_item_list.get(0);
					exam = insuranceService;
					break;
				}	
			}
		}
		
		insuranceServiceList = browser.decouple.db60.getInsurApprovalService(labCode,insuranceID,soid);
		for (Map<String, String> insuranceService : insuranceServiceList) {
			if (SdkTools.getMapByValue(bsSelfServiceList, "CS_ID", insuranceService.get("CS_ID"))!=null) {
				lab=insuranceService;
				break;
			}
		}
		
		insuranceServiceList = browser.decouple.db60.getInsurApprovalService(treatCode,insuranceID,soid);
		for (Map<String, String> insuranceService : insuranceServiceList) {
			if (SdkTools.getMapByValue(bsSelfServiceList, "CS_ID", insuranceService.get("CS_ID"))!=null) {
				treat=insuranceService;
				break;
			}
		}
		
		insuranceServiceList = browser.decouple.db60.getInsurApprovalService(pathologyCode,insuranceID,soid);
		for (Map<String, String> insuranceService : insuranceServiceList) {
			if (SdkTools.getMapByValue(bsSelfServiceList, "CS_ID", insuranceService.get("CS_ID"))!=null) {
				pathology=insuranceService;
				break;
			}
		}
		browser.logger.log(1,"需要审批的本服务计费病理:"+pathology);
		browser.logger.log(1,"需要审批的本服务计费治疗:"+treat);
		browser.logger.log(1,"需要审批的本服务计费检查:"+exam);
		browser.logger.log(1,"需要审批的本服务计费检验:"+lab);
		browser.logger.assertFalse(pathology==null, "当前环境中未取到 不需要审批的本服务计费病理");
		browser.logger.assertFalse(treat==null, "当前环境中未取到 不需要审批的本服务计费治疗");
		browser.logger.assertFalse(exam==null, "当前环境中未取到 不需要审批的本服务计费检查");
		browser.logger.assertFalse(lab==null, "当前环境中未取到 不需要审批的本服务计费检验");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		Map<String, String> patientRecord = browser.decouple.db60.getPatientRecordByName(encounterInfo.get(0)).get(0);
	
		//测试流程
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立西药
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null, true);
		//开立草药
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, herb.get("MEDICINE_ID"), null, true);
		//开立检查
		browser.wnOutpatientWorkflow.prescribeExamByExamItemId(browser.decouple.db60, exam_item.get("EXAM_ITEM_ID"), null, true);
		//开立检验
		browser.wnOutpatientWorkflow.prescribeLabByCsid(browser.decouple.db60, lab.get("CS_ID"), null, true);
		//开立病理
		browser.wnOutpatientWorkflow.prescribePathologyByCsid(browser.decouple.db60, pathology.get("CS_ID"), null, true);
		//开立治疗
		browser.wnOutpatientWorkflow.prescribeTreatByCsid(browser.decouple.db60, treat.get("CS_ID"), null, true);
		//签署
		browser.wnOutpatientWorkflow.signOff(0);
		List<Map<String, String>> insurOrderItems = browser.decouple.db60.getInsurOrderItemsByEncounterid(patientRecord.get("ENCOUNTER_ID"));
		Map<String, String> drugInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "MEDICINE_ID", drug.get("MEDICINE_ID"));
		Map<String, String> herbInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "MEDICINE_ID", herb.get("MEDICINE_ID"));
		Map<String, String> examInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", exam.get("CHARGING_ITEM_ID"));
		Map<String, String> labInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", lab.get("CHARGING_ITEM_ID"));
		Map<String, String> treatInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", treat.get("CHARGING_ITEM_ID"));
		Map<String, String> pathologyInsurOrderItem = SdkTools.getMapByValue(insurOrderItems, "CHARGING_ITEM_ID", pathology.get("CHARGING_ITEM_ID"));
		browser.logger.assertFalse(drugInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.assertFalse(herbInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.assertFalse(examInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.assertFalse(labInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.assertFalse(treatInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.assertFalse(pathologyInsurOrderItem==null, "医保医嘱项未落库到 PURCH_ORDER_MED_INSUR_APPROVAL");
		browser.logger.log(1, "医保医嘱项落库检查通过");
	}
	
	
	
	
	
	
	
	
}