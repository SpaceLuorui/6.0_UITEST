package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBill extends OutpatientTestBase {

	public TestBill() {
		super();
	}

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("计费策略专项","计费策略专项.html");
		try{
			Config.loadOnlineDefaultConfig("DEV");
			Config.loadOnlineExtraConfig("DEV","autoTest");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_01_single_drug_one() throws InterruptedException {
		init("CASE-01: 西药,单药品,单数量", true);
		Map<String, String> drug = browser.decouple.db60.getNomalMedicine("门诊药房", "98363");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();	
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(drug.get("CS_NO"),new ArrayList<>(Arrays.asList(drug.get("CS_NAME"),drug.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("1")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  String.valueOf(Double.valueOf(drug.get("RETAIL_PRICE")) / Double.valueOf(drug.get("RETAIL_PACK_CONV_FACTOR")));
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");	
	}
	
	@Test
	public void test_02_single_drug_much() throws InterruptedException {
		init("CASE-02: 西药,单药品,多数量", true);
		Map<String, String> drug = browser.decouple.db60.getNomalMedicine("门诊药房", "98363");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(drug.get("CS_NO"), new ArrayList<>(Arrays.asList(drug.get("CS_NAME"),drug.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		//String expectCost =  String.valueOf(Double.valueOf(drug.get("RETAIL_PRICE"))*3);
		String expectCost =  String.valueOf((Double.valueOf(drug.get("RETAIL_PRICE")) / Double.valueOf(drug.get("RETAIL_PACK_CONV_FACTOR")))*3);
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");	
	}
	
	@Test
	public void test_03_multi_drug_much() throws InterruptedException {
		init("CASE-03: 西药,多药品,多数量", true);
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<3, "有库存药品数量不足3个!");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立第一个药品
		browser.wnOutpatientWorkflow.searchOrder(drugList.get(0).get("CS_NO"), new ArrayList<>(Arrays.asList(drugList.get(0).get("CS_NAME"),drugList.get(0).get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
//		browser.wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder.replace("'previewItemWrap')", "'previewItemWrap') and contains(.,'"+drugList.get(0).get("NAME")+"')"), Framework.defaultTimeoutMax);
		//开立第二个药品
		browser.wnOutpatientWorkflow.searchOrder(drugList.get(1).get("CS_NO"), new ArrayList<>(Arrays.asList(drugList.get(1).get("CS_NAME"),drugList.get(1).get("PACK"))));
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
//		browser.wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder.replace("'previewItemWrap')", "'previewItemWrap') and contains(.,'"+drugList.get(1).get("NAME")+"')"), Framework.defaultTimeoutMax);
		//开立第三个药品
		browser.wnOutpatientWorkflow.searchOrder(drugList.get(2).get("CS_NO"), new ArrayList<>(Arrays.asList(drugList.get(2).get("CS_NAME"),drugList.get(2).get("PACK"))));
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
//		browser.wnwd.checkElementByXpath("未签署医嘱", WnOutpatientXpath.outpatientUnsignedOrder.replace("'previewItemWrap')", "'previewItemWrap') and contains(.,'"+drugList.get(2).get("NAME")+"')"), Framework.defaultTimeoutMax);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		double expectCost = 0.0;
		for (int i=0; i<3; i++) {
			double price = (Double.valueOf(drugList.get(i).get("RETAIL_PRICE"))) / (Double.valueOf(drugList.get(i).get("RETAIL_PACK_CONV_FACTOR")));
			double cost = price * 3;
			expectCost = expectCost + cost;
		}
		//String expectCost =  String.valueOf(Double.valueOf(drugList.get(0).get("RETAIL_PRICE"))*3+Double.valueOf(drugList.get(1).get("RETAIL_PRICE"))*3+Double.valueOf(drugList.get(2).get("RETAIL_PRICE"))*2);
		browser.wnwd.assertTrue("费用对比不通过(预期:" + expectCost +"/UI显示:"+totalCost+")",SdkTools.compareMoney(String.valueOf(expectCost),totalCost));
		browser.logger.log(1, "费用对比通过(预期:" + expectCost +"/UI显示:"+totalCost+")");
	}
	

	@Test
	public void test_04_single_medcine_one() throws InterruptedException {
		init("CASE-04: 中成药,单药品,单数量", true);
		Map<String, String> medicine = browser.decouple.db60.getNomalMedicine("门诊药房", "98364");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(medicine.get("CS_NO"), new ArrayList<>(Arrays.asList(medicine.get("CS_NAME"),medicine.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("1")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  medicine.get("RETAIL_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");	
	}
	
	@Test
	public void test_05_single_medcine_much() throws InterruptedException {
		init("CASE-05: 中成药,单药品,多数量", true);
		Map<String, String> medicine = browser.decouple.db60.getNomalMedicine("门诊药房", "98364");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(medicine.get("CS_NO"), new ArrayList<>(Arrays.asList(medicine.get("CS_NAME"),medicine.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  ""+Double.valueOf(medicine.get("RETAIL_PRICE"))*3;
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");	
	}
	
	@Test
	public void test_06_multi_medcine_much() throws InterruptedException {
		init("CASE-06: 中成药,多药品,多数量", true);
		List<Map<String, String>> medicineList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98364");
		browser.logger.assertFalse(medicineList.size()<3, "有库存药品数量不足3个!");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立第一个药品
		browser.wnOutpatientWorkflow.searchOrder(medicineList.get(0).get("CS_NO"), new ArrayList<>(Arrays.asList(medicineList.get(0).get("CS_NAME"),medicineList.get(0).get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
		//开立第二个药品
		browser.wnOutpatientWorkflow.searchOrder(medicineList.get(1).get("CS_NO"), new ArrayList<>(Arrays.asList(medicineList.get(1).get("CS_NAME"),medicineList.get(1).get("PACK"))));
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("3")));
		browser.wnOutpatientWorkflow.own_expense();
		//开立第三个药品
		browser.wnOutpatientWorkflow.searchOrder(medicineList.get(2).get("CS_NO"), new ArrayList<>(Arrays.asList(medicineList.get(2).get("CS_NAME"),medicineList.get(2).get("PACK"))));
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("2")));
		browser.wnOutpatientWorkflow.own_expense();
		//签署
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  String.valueOf(Double.valueOf(medicineList.get(0).get("RETAIL_PRICE"))*3+Double.valueOf(medicineList.get(1).get("RETAIL_PRICE"))*3+Double.valueOf(medicineList.get(2).get("RETAIL_PRICE"))*2);
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_07_single_herb_one() throws InterruptedException {
		init("CASE-07: 中草药,单药品,单数量", true);
		Double CL004 = Double.valueOf(browser.decouple.db60.getParam("CL004").get("PARAM_VALUE"));
		Map<String, String> herb = browser.decouple.db60.getNomalMedicine("草药房", "98365");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(herb.get("CS_NO"), new ArrayList<>(Arrays.asList(herb.get("CS_NAME"),herb.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList("1")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  ""+Double.valueOf(herb.get("RETAIL_PRICE"))*CL004;
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_08_single_herb_much() throws InterruptedException {
		init("CASE-08: 中草药,单药品,多数量", true);
		Double CL004 = Double.valueOf(browser.decouple.db60.getParam("CL004").get("PARAM_VALUE"));
		Map<String, String> herb = browser.decouple.db60.getNomalMedicine("草药房", "98365");
		Integer packNum = Integer.valueOf(SdkTools.findMatchList(herb.get("PACK"),"\\d+").get(0));
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(herb.get("CS_NO"), new ArrayList<>(Arrays.asList(herb.get("CS_NAME"),herb.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList(""+packNum*3)));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  ""+Double.valueOf(herb.get("RETAIL_PRICE"))*CL004*3;
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");	
	}
	
	@Test
	public void test_09_multi_herb_much() throws InterruptedException {
		init("CASE-09: 中草药,多药品,多数量", true);
		Double CL004 = Double.valueOf(browser.decouple.db60.getParam("CL004").get("PARAM_VALUE"));
		List<Map<String, String>> herbList = browser.decouple.db60.getNomalMedicineList("草药房", "98365");
		Integer packNum1 = Integer.valueOf(SdkTools.findMatchList(herbList.get(0).get("PACK"),"\\d+").get(0));
		Integer packNum2 = Integer.valueOf(SdkTools.findMatchList(herbList.get(0).get("PACK"),"\\d+").get(0));
		Integer packNum3 = Integer.valueOf(SdkTools.findMatchList(herbList.get(0).get("PACK"),"\\d+").get(0));
		browser.logger.assertFalse(herbList.size()<3, "有库存药品数量不足3个!");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立第一个药品
		browser.wnOutpatientWorkflow.searchOrder(herbList.get(0).get("CS_NO"), new ArrayList<>(Arrays.asList(herbList.get(0).get("CS_NAME"),herbList.get(0).get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList(""+packNum1*3)));
		browser.wnOutpatientWorkflow.own_expense();
		//开立第二个药品
		browser.wnOutpatientWorkflow.searchOrder(herbList.get(1).get("CS_NO"), new ArrayList<>(Arrays.asList(herbList.get(1).get("CS_NAME"),herbList.get(1).get("PACK"))));
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList(""+packNum2*3)));
		browser.wnOutpatientWorkflow.own_expense();
		//开立第三个药品
		browser.wnOutpatientWorkflow.searchOrder(herbList.get(2).get("CS_NO"), new ArrayList<>(Arrays.asList(herbList.get(2).get("CS_NAME"),herbList.get(2).get("PACK"))));
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<>(Arrays.asList(""+packNum3*2)));
		browser.wnOutpatientWorkflow.own_expense();
		//签署
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  String.valueOf(CL004*(Double.valueOf(herbList.get(0).get("RETAIL_PRICE"))*3+Double.valueOf(herbList.get(1).get("RETAIL_PRICE"))*3+Double.valueOf(herbList.get(2).get("RETAIL_PRICE"))*2));
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	
	
	

	
	
	@Test
	public void test_10_bsSelf_treat() throws InterruptedException {
		init("CASE-10: 本服务计费策略(治疗)", true);
		List<Map<String, String>> treatList = browser.decouple.db60.getServiceListByStrategy("98098", "256162", "256181");
		Map<String, String> treat = null;
		for (Map<String, String> map : treatList) {
			String unitPrice = browser.decouple.db60.getBsSelfCostByCsid(map.get("CS_ID"),"256181");
				if (unitPrice!=null) {
					treat = map;
					treat.put("UNIT_PRICE", unitPrice);
					break;
				
			}
		}
		browser.logger.log(1, ""+treat);
		browser.logger.assertFalse(treat==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		
		browser.wnOutpatientWorkflow.searchOrder(treat.get("CS_NO"), new ArrayList<>(Arrays.asList(treat.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  treat.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_11_bsSelf_pathology() throws InterruptedException {
		init("CASE-11: 本服务计费策略(病理)", true);
		List<Map<String, String>> pathologyList = browser.decouple.db60.getServiceListByStrategy("98088", "256162", "256181");
		Map<String, String> pathology = null;
		for (Map<String, String> map : pathologyList) {
			String unitPrice = browser.decouple.db60.getBsSelfCostByCsid(map.get("CS_ID"),"256181");
			if (unitPrice!=null) {
				pathology = map;
				pathology.put("UNIT_PRICE", unitPrice);
				break;
				
			}
		}
		browser.logger.log(1, ""+pathology);
		browser.logger.assertFalse(pathology==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		
		//开立签署
		browser.wnOutpatientWorkflow.searchOrder(pathology.get("CS_NO"), new ArrayList<>(Arrays.asList(pathology.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  pathology.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	
	@Test
	public void test_12_bsSelf_pathologyAndTreat() throws InterruptedException {
		init("CASE-12: 本服务计费策略(病理+治疗)", true);
		List<Map<String, String>> pathologyList = browser.decouple.db60.getServiceListByStrategy("98088", "256162", "256181");
		Map<String, String> pathology = null;
		for (Map<String, String> map : pathologyList) {
			String unitPrice = browser.decouple.db60.getBsSelfCostByCsid(map.get("CS_ID"),"256181");
			if (unitPrice!=null) {
				pathology = map;
				pathology.put("UNIT_PRICE", unitPrice);
				break;
				
			}
		}
		List<Map<String, String>> treatList = browser.decouple.db60.getServiceListByStrategy("98098", "256162", "256181");
		Map<String, String> treat = null;
		for (Map<String, String> map : treatList) {
			String unitPrice = browser.decouple.db60.getBsSelfCostByCsid(map.get("CS_ID"),"256181");
			if (unitPrice!=null) {
				treat = map;
				treat.put("UNIT_PRICE", unitPrice);
				break;
			}
		}
		browser.logger.log(1, ""+pathology);
		browser.logger.log(1, ""+treat);
		browser.logger.assertFalse(treat==null, "获取不到数据");
		browser.logger.assertFalse(pathology==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立病理 
		browser.wnOutpatientWorkflow.searchOrder(pathology.get("CS_NO"), new ArrayList<>(Arrays.asList(pathology.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		//开立治疗
		browser.wnOutpatientWorkflow.searchOrder(treat.get("CS_NO"), new ArrayList<>(Arrays.asList(treat.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost = ""+ (Float.valueOf(pathology.get("UNIT_PRICE"))+Float.valueOf(treat.get("UNIT_PRICE")));
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	
	@Test
	public void test_13_bsFree() throws InterruptedException {
		init("CASE-13: 不计费策略", true);
		List<Map<String, String>> serviceList = browser.decouple.db60.getServiceListByStrategy(null, "256163", "256181");
		Map<String, String> service = serviceList.get(0);
		browser.logger.log(1, ""+service);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立
		browser.wnOutpatientWorkflow.searchOrder(service.get("CS_NO"), new ArrayList<>(Arrays.asList(service.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  "0";
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_14_bsComposite() throws InterruptedException {
		init("CASE-14: 组合计费策略", true);
		List<Map<String, String>> serviceList = browser.decouple.db60.getServiceListByStrategy(null, "256164", "256181");
		Map<String, String> service = null;
		for (Map<String, String> map : serviceList) {
			String unitPrice = browser.decouple.db60.getBsCompositeCostByCsid(map.get("CS_ID"),"256181");
			if (unitPrice!=null) {
				service = map;
				service.put("UNIT_PRICE", unitPrice);
				break;
			}
		}
		browser.logger.log(1, ""+service);
		browser.logger.assertFalse(service==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(service.get("CS_NO"), new ArrayList<>(Arrays.asList(service.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  service.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");		
	}
	
	@Test
	public void test_15_bsLabtestIndex() throws InterruptedException {
		init("CASE-15: 成员计费_检验按指标明细合计计费策略 - 开立所有检验指标", true);
		List<Map<String, String>> labList = browser.decouple.db60.getServiceListByStrategy("98071", "256167", "256181");
		Map<String, String> lab = null;
		for (Map<String, String> map : labList) {	
			List<Map<String, String>> priceList = browser.decouple.db60.getBsLabtestIndexCostByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>1) {
				lab = map;
				Double finalPrice = 0.0;
				for (Map<String, String> price : priceList) {
					finalPrice += Double.valueOf(price.get("UNIT_PRICE"));
				}
				lab.put("UNIT_PRICE", ""+finalPrice);
				break;
			}
		}
		browser.logger.log(1, ""+lab);
		browser.logger.assertFalse(lab==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(lab.get("CS_NO"), new ArrayList<>(Arrays.asList(lab.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  lab.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");		
	}
	
	
	@Test
	public void test_16_bsLabtestIdxRangeExFee() throws InterruptedException {
		init("CASE-16: 成员计费_检验按指标个数区间_个数加收计费策略 - 开立所有检验指标", true);
		List<Map<String, String>> labList = browser.decouple.db60.getServiceListByStrategy("98071", "256168", "256181");
		Map<String, String> lab = null;
		for (Map<String, String> map : labList) {	
			List<Map<String, String>> priceList = browser.decouple.db60.getBsLabtestIdxRangeExFeeByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				lab = map;
				Double finalPrice = 0.0;
				for (Map<String, String> price : priceList) {
					//finalPrice += (1+Double.valueOf(price.get("LABTEST_INDEX_UPPER_LIMIT"))-Double.valueOf(price.get("LABTEST_INDEX_LOWER_LIMIT")))*Double.valueOf(price.get("UNIT_PRICE"))*Double.valueOf(price.get("CHARGING_ITEM_QTY"));
					finalPrice += ( Double.valueOf(price.get("UNIT_PRICE")) * Double.valueOf(price.get("CHARGING_ITEM_QTY")) );
				}
				lab.put("UNIT_PRICE", ""+finalPrice);
				break;
			}
		}
		browser.logger.log(1, ""+lab);
		browser.logger.assertFalse(lab==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(lab.get("CS_NO"), new ArrayList<>(Arrays.asList(lab.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  lab.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");		
	}


	@Test
	public void test_17_bsLabtestIdxRanNoneSum() throws InterruptedException {
		init("CASE-17: 成员计费_检验按指标个数区间_不累计计费策略 - 开立所有检验指标", true);
		List<Map<String, String>> labList = browser.decouple.db60.getServiceListByStrategy("98071", "256169", "256181");
		Map<String, String> lab = null;
		for (Map<String, String> map : labList) {	
			List<Map<String, String>> priceList = browser.decouple.db60.getBsLabtestIdxRanNoneSumByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				lab = map;
				Double finalPrice = Double.valueOf(priceList.get(priceList.size()-1).get("UNIT_PRICE"))*Double.valueOf(priceList.get(priceList.size()-1).get("CHARGING_ITEM_QTY"));
				lab.put("UNIT_PRICE", ""+finalPrice);
				break;
			}
		}
		browser.logger.log(1, ""+lab);
		browser.logger.assertFalse(lab==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(lab.get("CS_NO"), new ArrayList<>(Arrays.asList(lab.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  lab.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	
	@Test
	public void test_18_BsLabtestIndexRangeSum() throws InterruptedException {
		init("CASE-18: 成员计费_检验按指标个数区间_累计计费策略 - 开立所有检验指标", true);	
		List<Map<String, String>> labList = browser.decouple.db60.getServiceListByStrategy("98071", "256170", "256181");
		Map<String, String> lab = null;
		for (Map<String, String> map : labList) {	
			List<Map<String, String>> priceList = browser.decouple.db60.getBsLabtestIndexRangeSumByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				lab = map;
				Double finalPrice = 0.0;
				for (Map<String, String> price : priceList) {
					finalPrice += Double.valueOf(price.get("UNIT_PRICE"))*Double.valueOf(price.get("CHARGING_ITEM_QTY"));
				}
				lab.put("UNIT_PRICE", ""+finalPrice);
				break;
			}
		}
		browser.logger.log(1, ""+lab);
		browser.logger.assertFalse(lab==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(lab.get("CS_NO"), new ArrayList<>(Arrays.asList(lab.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  lab.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_19_bsExamItem() throws InterruptedException {
		init("CASE-19: 成员计费_检查按项目明细合计计费策略 - 开立所有检查指标", true);
		List<Map<String, String>> examList = browser.decouple.db60.getServiceListByStrategy("98078", "376726", "256181");
		Map<String, String> exam = null;
		for (Map<String, String> map : examList) {
			if (map.get("CS_NAME").toUpperCase().contains("CT")||map.get("CS_NAME").toUpperCase().contains("X")||map.get("CS_NAME").toUpperCase().contains("MR")) {
				browser.logger.log(3, "跳过带人体图的服务:"+map.get("CS_NAME"));
				continue;
			}
			List<Map<String, String>> priceList = browser.decouple.db60.getBsExamItemByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				exam = map;
				Double finalPrice = 0.0;
				for (Map<String, String> price : priceList) {
					finalPrice += Double.valueOf(price.get("UNIT_PRICE"))*Double.valueOf(price.get("CHARGING_ITEM_QTY"));
				}
				exam.put("UNIT_PRICE", ""+finalPrice);
				break;
			}
		}
		browser.logger.log(1, ""+exam);
		browser.logger.assertFalse(exam==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(exam.get("CS_NO"), new ArrayList<>(Arrays.asList(exam.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<String>(Arrays.asList("ALL")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		String expectCost =  exam.get("UNIT_PRICE");
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(expectCost,totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_20_bsExamItemRangeExtraFee() throws InterruptedException {
		init("CASE-20: 成员计费_检查服务项目按个数加收计费策略", true);	
		List<Map<String, String>> examList = browser.decouple.db60.getServiceListByStrategy("98078", "256172", "256181");
		Map<String, String> exam = null;
		List<Map<String, String>> priceList = null;
		for (Map<String, String> map : examList) {
			if (map.get("CS_NAME").toUpperCase().contains("CT")||map.get("CS_NAME").toUpperCase().contains("X")||map.get("CS_NAME").toUpperCase().contains("MR")) {
				browser.logger.log(3, "跳过带人体图的服务:"+map.get("CS_NAME"));
				continue;
			}
			priceList = browser.decouple.db60.getBsExamItemRangeExtraFeeByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				exam = map;
				break;
			}
		}

		browser.logger.log(1, ""+exam);		
		browser.logger.assertFalse(exam==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(exam.get("CS_NO"), new ArrayList<>(Arrays.asList(exam.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<String>(Arrays.asList("ALL")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		//String expectCost =  exam.get("UNIT_PRICE");
		Double expectCost = 0.0;
		Integer realCount = browser.wnOutpatientWorkflow.getClinicalItemCount();
		for (Map<String, String> price : priceList) {
			Double unitPrice = Double.valueOf(price.get("UNIT_PRICE"));
			Double qty = Double.valueOf(price.get("CHARGING_ITEM_QTY"));
			if ( realCount<=0 ) {
				expectCost += 0;
			} else if ( (realCount >=Double.parseDouble(price.get("EXAM_ITEM_LOWER_LIMIT"))) && (Double.parseDouble(price.get("EXAM_ITEM_UPPER_LIMIT")) > realCount) ) {
				expectCost += unitPrice * realCount * qty;
				continue;
			} else if ( realCount >= Double.parseDouble(price.get("EXAM_ITEM_UPPER_LIMIT")) ) {
				expectCost += Integer.parseInt(price.get("EXAM_ITEM_UPPER_LIMIT")) * unitPrice * qty;
				realCount = realCount - Integer.parseInt(price.get("EXAM_ITEM_UPPER_LIMIT"));
				continue;
			} else if ( (realCount <=Integer.parseInt(price.get("EXAM_ITEM_LOWER_LIMIT"))) && (realCount<=Integer.parseInt(price.get("EXAM_ITEM_UPPER_LIMIT"))) ) {
				expectCost += unitPrice * realCount;
				continue;
			}
		}
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(String.valueOf(expectCost),totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	

	@Test
	public void test_21_bsExamItemRangeNoneSum() throws InterruptedException {
		init("CASE-21: 成员计费_检查服务项目按个数区间计费_不累计计费策略", true);	
		List<Map<String, String>> examList = browser.decouple.db60.getServiceListByStrategy("98078", "256175", "256181");
		Map<String, String> exam = null;
		List<Map<String, String>> priceList = null;
		for (Map<String, String> map : examList) {
			if (map.get("CS_NAME").toUpperCase().contains("CT")||map.get("CS_NAME").toUpperCase().contains("X")||map.get("CS_NAME").toUpperCase().contains("MR")) {
				browser.logger.log(3, "跳过带人体图的服务:"+map.get("CS_NAME"));
				continue;
			}
			priceList = browser.decouple.db60.getBsExamItemRangeNoneSumByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				exam = map;
/*				Double finalPrice = Double.valueOf(priceList.get(priceList.size()-1).get("UNIT_PRICE"))*Double.valueOf(priceList.get(priceList.size()-1).get("CHARGING_ITEM_QTY"));
				exam.put("UNIT_PRICE", ""+finalPrice);*/
				break;
			}
		}

		browser.logger.log(1, ""+exam);		
		browser.logger.assertFalse(exam==null, "获取不到数据");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(exam.get("CS_NO"), new ArrayList<>(Arrays.asList(exam.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<String>(Arrays.asList("all")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		//String expectCost =  exam.get("UNIT_PRICE");
		Double expectCost = 0.0;
		Integer realCount = browser.wnOutpatientWorkflow.getClinicalItemCount();
		for (Map<String, String> price : priceList) {
			Double unitPrice = Double.valueOf(price.get("UNIT_PRICE"));
			Double qty = Double.valueOf(price.get("CHARGING_ITEM_QTY"));
			if ( realCount<=0 ) {
				expectCost += 0;
			} else if ( (realCount >=Double.parseDouble(price.get("EXAM_ITEM_LOWER_LIMIT"))) && realCount <= Double.parseDouble(price.get("EXAM_ITEM_UPPER_LIMIT")) ) {
				expectCost = unitPrice * qty;
				continue;
			}
		}
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(String.valueOf(expectCost),totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
	
	@Test
	public void test_22_bsExamItemRangeSum() throws InterruptedException {
		init("CASE-22: 成员计费_检查服务项目按个数区间计费_累计计费策略", true);	
		List<Map<String, String>> examList = browser.decouple.db60.getServiceListByStrategy("98078", "256176", "256181");
		Map<String, String> exam = null;
		List<Map<String, String>> priceList = null;
		for (Map<String, String> map : examList) {
			if (map.get("CS_NAME").toUpperCase().contains("CT")||map.get("CS_NAME").toUpperCase().contains("X")||map.get("CS_NAME").toUpperCase().contains("MR")) {
				browser.logger.log(3, "跳过带人体图的服务:"+map.get("CS_NAME"));
				continue;
			}
			priceList = browser.decouple.db60.getBsExamItemRangeSumByCsid(map.get("CS_ID"),"256181");
			if (priceList!=null && priceList.size()>=1) {
				exam = map;
				/*Double finalPrice = 0.0;
				for (Map<String, String> price : priceList) {
					finalPrice += Double.valueOf(price.get("UNIT_PRICE"))*Double.valueOf(price.get("CHARGING_ITEM_QTY"));
				}
				exam.put("UNIT_PRICE", ""+finalPrice);*/
				break;
			}
		}
		browser.logger.log(1, exam.toString());		
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.setParamsForTestAllService();
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(exam.get("CS_NO"), new ArrayList<>(Arrays.asList(exam.get("CS_NAME"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.editAndCommitOrder(new ArrayList<String>(Arrays.asList("all")));
		browser.wnOutpatientWorkflow.own_expense();
		browser.wnOutpatientWorkflow.signOff(0);
		//价格对比
		String totalCost = ""+(Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost())-browser.wnOutpatientWorkflow.getLinkCost());
		//String expectCost =  exam.get("UNIT_PRICE");
		Double expectCost = 0.0;
		Integer realCount = browser.wnOutpatientWorkflow.getClinicalItemCount();
		for (Map<String, String> price : priceList) {
			Double unitPrice = Double.valueOf(price.get("UNIT_PRICE"));
			Double qty = Double.valueOf(price.get("CHARGING_ITEM_QTY"));
			if ( realCount<=0 ) {
				expectCost += 0;
			} else if ( (realCount >=Double.parseDouble(price.get("EXAM_ITEM_LOWER_LIMIT"))) && (Double.parseDouble(price.get("EXAM_ITEM_UPPER_LIMIT")) > realCount) ) {
				expectCost += unitPrice * realCount * qty;
				continue;
			} else if ( realCount >= Double.parseDouble(price.get("EXAM_ITEM_UPPER_LIMIT")) ) {
				expectCost += Integer.parseInt(price.get("EXAM_ITEM_UPPER_LIMIT")) * unitPrice * qty;
				realCount = realCount - Integer.parseInt(price.get("EXAM_ITEM_UPPER_LIMIT"));
				continue;
			} else if ( (realCount <=Integer.parseInt(price.get("EXAM_ITEM_LOWER_LIMIT"))) && (realCount<=Integer.parseInt(price.get("EXAM_ITEM_UPPER_LIMIT"))) ) {
				expectCost += unitPrice * realCount;
				continue;
			}
		}
		browser.wnwd.assertTrue("费用对比不通过(预期:"+expectCost+"/UI显示:"+totalCost+")",SdkTools.compareMoney(String.valueOf(expectCost),totalCost));
		browser.logger.log(1, "费用对比通过(预期:"+expectCost+"/UI显示:"+totalCost+")");
	}
}