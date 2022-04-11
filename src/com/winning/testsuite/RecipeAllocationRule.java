package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.gson.JsonArray;
import com.winning.testsuite.workflow.entity.AllocationCondition;
import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecipeAllocationRule extends OutpatientTestBase {

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("分方规则专项","分方规则.html");
	}


	@Test
	public void test_01(){
		init("CASE-01: 1.处方明细超过5个分方  (启用)", true);
		//查找6个可开立药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		try {
			drugList = drugList.subList(0, 6);
		} catch (Throwable e) {
			throw new Error("门诊药房有库存药品不足6个,此用例无法执行");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用第一条规则(不勾选成组优先)
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(1, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));

		for (int i = 0; i < 6; i++) {
			Map<String, String> drug = drugList.get(i);
			browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null);
			if (i<5) {
				List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
				browser.logger.assertFalse(recipeMapList.size()!=1, "开立"+(i+1)+"个药品,处方条数不应为:"+recipeMapList.size());
				browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals(""+(i+1)), "开立"+(i+1)+"个药品,处方明细条数不应为:"+recipeMapList.get(0).get("count"));
				browser.logger.assertFalse(!recipeMapList.get(0).get("medNames").contains(drug.get("NAME")), "开立"+(i+1)+"个药品,处方明细应该包含:"+drug.get("NAME"));
			}else {
				List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
				browser.logger.assertFalse(recipeMapList.size()!=2, "开立6个药品,处方条数不应为:"+recipeMapList.size());
				browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("5"), "开立6个药品,第一个处方明细条数不应为:"+recipeMapList.get(0).get("count"));
				browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "开立6个药品,第二个处方明细条数不应为:"+recipeMapList.get(1).get("count"));
				browser.logger.assertFalse(!recipeMapList.get(1).get("medNames").contains(drug.get("NAME")), "开立6个药品,第二个处方明细应该包含:"+drug.get("NAME"));
			}
		}
	}


	@Test
	public void test_02(){
		init("CASE-02: 1.处方明细超过5个分方  (停用)", true);
		//查找6个可开立药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		try {
			drugList = drugList.subList(0, 6);
		} catch (Throwable e) {
			throw new Error("门诊药房有库存药品不足6个,此用例无法执行");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立6个药品并检查,每条开立完后处方内容是否正确
		for (int i = 0; i < 6; i++) {
			Map<String, String> drug = drugList.get(i);
			browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null);
			List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
			browser.logger.assertFalse(recipeMapList.size()!=1, "开立"+(i+1)+"个药品,处方条数不应为:"+recipeMapList.size());
			browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals(""+(i+1)), "开立"+(i+1)+"个药品,处方明细条数不应为:"+recipeMapList.get(0).get("count"));
			browser.logger.assertFalse(!recipeMapList.get(0).get("medNames").contains(drug.get("NAME")), "开立"+(i+1)+"个药品,处方明细应该包含:"+drug.get("NAME"));
		}
	}


	@Test
	public void test_03(){
		init("CASE-03: 1.处方明细超过5个分方  (启用,勾选成组优先)", true);
		//查找1个可开立药品
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用第一条规则(不勾选成组优先)
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(1, true, true);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//获取成组药品
		JsonArray injectMedcines = browser.wnOutpatientWorkflow.getInjectMedinces();
		browser.logger.assertFalse(injectMedcines.size()<5,"可开立的注射药品不足");
		//校验药品通用名
		PrescribeDetail detail = new PrescribeDetail();
		detail.route="静滴";
		detail.groupMedicineName = new ArrayList<String>(Arrays.asList(	injectMedcines.get(0).getAsJsonObject().get("commodityNameChinese").getAsString()
				,injectMedcines.get(1).getAsJsonObject().get("commodityNameChinese").getAsString()
				,injectMedcines.get(2).getAsJsonObject().get("commodityNameChinese").getAsString()
				,injectMedcines.get(3).getAsJsonObject().get("commodityNameChinese").getAsString()
				,injectMedcines.get(4).getAsJsonObject().get("commodityNameChinese").getAsString()));
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), detail);
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "开立6个成组药品,处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("6"), "开立6个成组药品,处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_04(){
		init("CASE-04: 1.处方明细超过5个分方  (启用,不勾选成组优先)", true);
		//查找1个可开立药品
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用第一条规则(不勾选成组优先)
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(1, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//获取成组药品
		JsonArray injectMedcines = browser.wnOutpatientWorkflow.getInjectMedinces();
		browser.logger.assertFalse(injectMedcines.size()<5,"可开立的注射药品不足");
		//校验药品通用名
		PrescribeDetail detail = new PrescribeDetail();
		detail.route="静滴";
		detail.groupMedicineName = new ArrayList<String>(Arrays.asList(injectMedcines.get(0).getAsJsonObject().get("commodityNameChinese").getAsString(),
				injectMedcines.get(1).getAsJsonObject().get("commodityNameChinese").getAsString(),
				injectMedcines.get(2).getAsJsonObject().get("commodityNameChinese").getAsString(),
				injectMedcines.get(3).getAsJsonObject().get("commodityNameChinese").getAsString(),
				injectMedcines.get(4).getAsJsonObject().get("commodityNameChinese").getAsString()));
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug.get("MEDICINE_ID"), detail);
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "开立6个成组药品,处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("5"), "开立6个成组药品,第一个处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "开立6个成组药品,第二个处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_05(){
		init("CASE-05: 2.精麻毒放药品单独分方 (启用, 设置1 分方条件: 毒性/麻醉/放射性单独分方 )", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_pt = browser.decouple.db60.getDrugByPsychotropicsCode("152650");
		List<Map<String, String>> drugList_js1 = browser.decouple.db60.getDrugByPsychotropicsCode("152651");
		List<Map<String, String>> drugList_js2 = browser.decouple.db60.getDrugByPsychotropicsCode("152652");
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getDrugByPsychotropicsCode("152653");
		List<Map<String, String>> drugList_dx = browser.decouple.db60.getDrugByPsychotropicsCode("152654");
		List<Map<String, String>> drugList_fs = browser.decouple.db60.getDrugByPsychotropicsCode("152655");
		Map<String, String> drug_pt = null;
		Map<String, String> drug_js1 = null;
		Map<String, String> drug_js2 = null;
		Map<String, String> drug_mz = null;
		Map<String, String> drug_dx = null;
		Map<String, String> drug_fs = null;
		//从普通药品中取一个有库存的
		for (Map<String, String> drug : drugList_pt) {
			drug_pt = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_pt!=null) {
				break;
			}
		}
		//从一类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js1) {
			drug_js1 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js1!=null) {
				break;
			}
		}
		//从二类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js2) {
			drug_js2 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js2!=null) {
				break;
			}
		}
		//从麻醉药品中取一个有库存的
		for (Map<String, String> drug : drugList_mz) {
			drug_mz = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_mz!=null) {
				break;
			}
		}
		//从毒性药品中取一个有库存的
		for (Map<String, String> drug : drugList_dx) {
			drug_dx = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_dx!=null) {
				break;
			}
		}
		//从放射药品中取一个有库存的
		for (Map<String, String> drug : drugList_fs) {
			drug_fs = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_fs!=null) {
				break;
			}
		}
		browser.logger.log(1, "普通药品:"+drug_pt);
		browser.logger.log(1, "一类精神:"+drug_js1);
		browser.logger.log(1, "二类精神:"+drug_js2);
		browser.logger.log(1, "麻醉药品:"+drug_mz);
		browser.logger.log(1, "毒性药品:"+drug_dx);
		browser.logger.log(1, "放射药品:"+drug_fs);
		browser.logger.assertFalse(drug_pt==null, "当前环境,没有获取到普通药品");
		browser.logger.assertFalse(drug_js1==null, "当前环境,没有获取到一类精神药品");
		browser.logger.assertFalse(drug_js2==null, "当前环境,没有获取到二类精神药品");
		browser.logger.assertFalse(drug_mz==null, "当前环境,没有获取到麻醉药品");
		browser.logger.assertFalse(drug_dx==null, "当前环境,没有获取到毒性药品");
		browser.logger.assertFalse(drug_fs==null, "当前环境,没有获取到放射药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		// 启用, 设置1条分方条件:毒性/麻醉/放射性 单独分方
		AllocationCondition condition = new AllocationCondition(0, Data.ShortName.specialType, Data.OperationCode.equal, Arrays.asList(Data.medicineType.dx.code,Data.medicineType.mz.code,Data.medicineType.fsx.code));
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(2, true, true, Arrays.asList(condition));
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_pt.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js1.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js2.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_mz.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_dx.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_fs.get("MEDICINE_ID"), null);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为3条
		browser.logger.assertFalse(recipeList.size()!=2, "处方条数不应为"+recipeList.size());
		//校验第一条处方有3条明细 , 分别为 普通/一类精神/二类精神
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("3"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drug_pt.get("NAME")), "第一条处方,明细中不包含药品"+drug_pt.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drug_js1.get("NAME")), "第一条处方,明细中不包含药品"+drug_js1.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drug_js2.get("NAME")), "第一条处方,明细中不包含药品"+drug_js2.get("NAME"));
		//校验第二条处方有2条明细 , 分别为 一类精神药品/二类精神药品
		browser.logger.assertFalse(!recipeList.get(1).get("count").equals("3"), "第二条处方,明细数量不应为"+recipeList.get(1).get("count"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drug_mz.get("NAME")), "第二条处方,明细中不包含药品"+drug_mz.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drug_fs.get("NAME")), "第二条处方,明细中不包含药品"+drug_fs.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drug_dx.get("NAME")), "第二条处方,明细中不包含药品"+drug_dx.get("NAME"));
	}


	@Test
	public void test_06(){
		init("CASE-06: 2.精麻毒放药品单独分方 (启用, 设置2个分方条件:精神一二类单独分方  /  毒性药品单独分方)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_pt = browser.decouple.db60.getDrugByPsychotropicsCode("152650");
		List<Map<String, String>> drugList_js1 = browser.decouple.db60.getDrugByPsychotropicsCode("152651");
		List<Map<String, String>> drugList_js2 = browser.decouple.db60.getDrugByPsychotropicsCode("152652");
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getDrugByPsychotropicsCode("152653");
		List<Map<String, String>> drugList_dx = browser.decouple.db60.getDrugByPsychotropicsCode("152654");
		List<Map<String, String>> drugList_fs = browser.decouple.db60.getDrugByPsychotropicsCode("152655");
		Map<String, String> drug_pt = null;
		Map<String, String> drug_js1 = null;
		Map<String, String> drug_js2 = null;
		Map<String, String> drug_mz = null;
		Map<String, String> drug_dx = null;
		Map<String, String> drug_fs = null;
		//从普通药品中取一个有库存的
		for (Map<String, String> drug : drugList_pt) {
			drug_pt = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_pt!=null) {
				break;
			}
		}
		//从一类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js1) {
			drug_js1 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js1!=null) {
				break;
			}
		}
		//从二类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js2) {
			drug_js2 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js2!=null) {
				break;
			}
		}
		//从麻醉药品中取一个有库存的
		for (Map<String, String> drug : drugList_mz) {
			drug_mz = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_mz!=null) {
				break;
			}
		}
		//从毒性药品中取一个有库存的
		for (Map<String, String> drug : drugList_dx) {
			drug_dx = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_dx!=null) {
				break;
			}
		}
		//从放射药品中取一个有库存的
		for (Map<String, String> drug : drugList_fs) {
			drug_fs = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_fs!=null) {
				break;
			}
		}
		browser.logger.log(1, "普通药品:"+drug_pt);
		browser.logger.log(1, "一类精神:"+drug_js1);
		browser.logger.log(1, "二类精神:"+drug_js2);
		browser.logger.log(1, "麻醉药品:"+drug_mz);
		browser.logger.log(1, "毒性药品:"+drug_dx);
		browser.logger.log(1, "放射药品:"+drug_fs);
		browser.logger.assertFalse(drug_pt==null, "当前环境,没有获取到普通药品");
		browser.logger.assertFalse(drug_js1==null, "当前环境,没有获取到一类精神药品");
		browser.logger.assertFalse(drug_js2==null, "当前环境,没有获取到二类精神药品");
		browser.logger.assertFalse(drug_mz==null, "当前环境,没有获取到麻醉药品");
		browser.logger.assertFalse(drug_dx==null, "当前环境,没有获取到毒性药品");
		browser.logger.assertFalse(drug_fs==null, "当前环境,没有获取到放射药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		//启用 精麻毒放药品单独分方 设置条件为  精神一二类单独分方/毒性药品单独分方
		AllocationCondition condition_js = new AllocationCondition(0, Data.ShortName.specialType, Data.OperationCode.equal, Arrays.asList(Data.medicineType.js1.code,Data.medicineType.js2.code));
		AllocationCondition condition_dx = new AllocationCondition(1, Data.ShortName.specialType, Data.OperationCode.equal, Arrays.asList(Data.medicineType.dx.code));
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(2, true, true, Arrays.asList(condition_js,condition_dx));
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_pt.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js1.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js2.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_mz.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_dx.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_fs.get("MEDICINE_ID"), null);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为3条
		browser.logger.assertFalse(recipeList.size()!=3, "处方条数不应为"+recipeList.size());
		//校验第一条处方有3条明细 , 分别为 普通药品/放射性药品/麻醉药品
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("3"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drug_pt.get("NAME")), "第一条处方,明细中不包含药品"+drug_pt.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drug_fs.get("NAME")), "第一条处方,明细中不包含药品"+drug_fs.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drug_mz.get("NAME")), "第一条处方,明细中不包含药品"+drug_mz.get("NAME"));
		//校验第二条处方有2条明细 , 分别为 一类精神药品/二类精神药品
		browser.logger.assertFalse(!recipeList.get(1).get("count").equals("2"), "第二条处方,明细数量不应为"+recipeList.get(1).get("count"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drug_js1.get("NAME")), "第二条处方,明细中不包含药品"+drug_js1.get("NAME"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drug_js2.get("NAME")), "第二条处方,明细中不包含药品"+drug_js2.get("NAME"));
		//校验第三条处方有1条明细 , 为 毒性药品
		browser.logger.assertFalse(!recipeList.get(2).get("count").equals("1"), "第三条处方,明细数量不应为"+recipeList.get(2).get("count"));
		browser.logger.assertFalse(!recipeList.get(2).get("medNames").contains(drug_dx.get("NAME")), "第三条处方,明细中不包含药品"+drug_dx.get("NAME"));
	}


	@Test
	public void test_07(){
		init("CASE-07: 2.精麻毒放药品单独分方 (启用, 设置不设置分方条件)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_pt = browser.decouple.db60.getDrugByPsychotropicsCode("152650");
		List<Map<String, String>> drugList_js1 = browser.decouple.db60.getDrugByPsychotropicsCode("152651");
		List<Map<String, String>> drugList_js2 = browser.decouple.db60.getDrugByPsychotropicsCode("152652");
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getDrugByPsychotropicsCode("152653");
		List<Map<String, String>> drugList_dx = browser.decouple.db60.getDrugByPsychotropicsCode("152654");
		List<Map<String, String>> drugList_fs = browser.decouple.db60.getDrugByPsychotropicsCode("152655");
		Map<String, String> drug_pt = null;
		Map<String, String> drug_js1 = null;
		Map<String, String> drug_js2 = null;
		Map<String, String> drug_mz = null;
		Map<String, String> drug_dx = null;
		Map<String, String> drug_fs = null;
		//从普通药品中取一个有库存的
		for (Map<String, String> drug : drugList_pt) {
			drug_pt = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_pt!=null) {
				break;
			}
		}
		//从一类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js1) {
			drug_js1 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js1!=null) {
				break;
			}
		}
		//从二类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js2) {
			drug_js2 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js2!=null) {
				break;
			}
		}
		//从麻醉药品中取一个有库存的
		for (Map<String, String> drug : drugList_mz) {
			drug_mz = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_mz!=null) {
				break;
			}
		}
		//从毒性药品中取一个有库存的
		for (Map<String, String> drug : drugList_dx) {
			drug_dx = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_dx!=null) {
				break;
			}
		}
		//从放射药品中取一个有库存的
		for (Map<String, String> drug : drugList_fs) {
			drug_fs = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_fs!=null) {
				break;
			}
		}
		browser.logger.log(1, "普通药品:"+drug_pt);
		browser.logger.log(1, "一类精神:"+drug_js1);
		browser.logger.log(1, "二类精神:"+drug_js2);
		browser.logger.log(1, "麻醉药品:"+drug_mz);
		browser.logger.log(1, "毒性药品:"+drug_dx);
		browser.logger.log(1, "放射药品:"+drug_fs);
		browser.logger.assertFalse(drug_pt==null, "当前环境,没有获取到普通药品");
		browser.logger.assertFalse(drug_js1==null, "当前环境,没有获取到一类精神药品");
		browser.logger.assertFalse(drug_js2==null, "当前环境,没有获取到二类精神药品");
		browser.logger.assertFalse(drug_mz==null, "当前环境,没有获取到麻醉药品");
		browser.logger.assertFalse(drug_dx==null, "当前环境,没有获取到毒性药品");
		browser.logger.assertFalse(drug_fs==null, "当前环境,没有获取到放射药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		//启用 精麻毒放药品单独分方 不设置分方条件
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(2, true, true, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_pt.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js1.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js2.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_mz.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_dx.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_fs.get("MEDICINE_ID"), null);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为1条
		browser.logger.assertFalse(recipeList.size()!=1, "处方条数不应为"+recipeList.size());
		//校验第一条处方有6条明细
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("6"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
	}


	@Test
	public void test_08(){
		init("CASE-08: 2.精麻毒放药品单独分方 (停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_pt = browser.decouple.db60.getDrugByPsychotropicsCode("152650");
		List<Map<String, String>> drugList_js1 = browser.decouple.db60.getDrugByPsychotropicsCode("152651");
		List<Map<String, String>> drugList_js2 = browser.decouple.db60.getDrugByPsychotropicsCode("152652");
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getDrugByPsychotropicsCode("152653");
		List<Map<String, String>> drugList_dx = browser.decouple.db60.getDrugByPsychotropicsCode("152654");
		List<Map<String, String>> drugList_fs = browser.decouple.db60.getDrugByPsychotropicsCode("152655");
		Map<String, String> drug_pt = null;
		Map<String, String> drug_js1 = null;
		Map<String, String> drug_js2 = null;
		Map<String, String> drug_mz = null;
		Map<String, String> drug_dx = null;
		Map<String, String> drug_fs = null;
		//从普通药品中取一个有库存的
		for (Map<String, String> drug : drugList_pt) {
			drug_pt = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_pt!=null) {
				break;
			}
		}
		//从一类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js1) {
			drug_js1 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js1!=null) {
				break;
			}
		}
		//从二类精神药品中取一个有库存的
		for (Map<String, String> drug : drugList_js2) {
			drug_js2 = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_js2!=null) {
				break;
			}
		}
		//从麻醉药品中取一个有库存的
		for (Map<String, String> drug : drugList_mz) {
			drug_mz = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_mz!=null) {
				break;
			}
		}
		//从毒性药品中取一个有库存的
		for (Map<String, String> drug : drugList_dx) {
			drug_dx = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_dx!=null) {
				break;
			}
		}
		//从放射药品中取一个有库存的
		for (Map<String, String> drug : drugList_fs) {
			drug_fs = SdkTools.getMapByValue(drugList, "CS_ID", drug.get("CS_ID"));
			if (drug_fs!=null) {
				break;
			}
		}
		browser.logger.log(1, "普通药品:"+drug_pt);
		browser.logger.log(1, "一类精神:"+drug_js1);
		browser.logger.log(1, "二类精神:"+drug_js2);
		browser.logger.log(1, "麻醉药品:"+drug_mz);
		browser.logger.log(1, "毒性药品:"+drug_dx);
		browser.logger.log(1, "放射药品:"+drug_fs);
		browser.logger.assertFalse(drug_pt==null, "当前环境,没有获取到普通药品");
		browser.logger.assertFalse(drug_js1==null, "当前环境,没有获取到一类精神药品");
		browser.logger.assertFalse(drug_js2==null, "当前环境,没有获取到二类精神药品");
		browser.logger.assertFalse(drug_mz==null, "当前环境,没有获取到麻醉药品");
		browser.logger.assertFalse(drug_dx==null, "当前环境,没有获取到毒性药品");
		browser.logger.assertFalse(drug_fs==null, "当前环境,没有获取到放射药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_pt.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js1.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_js2.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_mz.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_dx.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_fs.get("MEDICINE_ID"), null);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为1条
		browser.logger.assertFalse(recipeList.size()!=1, "处方条数不应为"+recipeList.size());
		//校验第一条处方有6条明细
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("6"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
	}


	@Test
	public void test_09(){
		init("CASE-09: 3.给药途径单独分方 (启用 设置1个分方条件: 皮下注射单独分方)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		try {
			drugList = drugList.subList(0, 3);
		} catch (Throwable e) {
			throw new Error("门诊药房有库存药品不足3个,此用例无法执行");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		//启用 精麻毒放药品单独分方 不设置分方条件
		AllocationCondition condition = new AllocationCondition(0, Data.ShortName.route, Data.OperationCode.equal, Arrays.asList(Data.route.piXiaZhuShe.code));
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(3, true, false, Arrays.asList(condition));
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.route=Data.route.kouFu.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.route=Data.route.piXiaZhuShe.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		detail.route=Data.route.jingDi.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), detail);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为2条
		browser.logger.assertFalse(recipeList.size()!=2, "处方条数不应为"+recipeList.size());
		//校验第一条处方有2条明细
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("2"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drugList.get(0).get("NAME")), "第一条处方,明细中不包含药品"+drugList.get(0).get("NAME"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drugList.get(2).get("NAME")), "第一条处方,明细中不包含药品"+drugList.get(2).get("NAME"));
		//校验第一条处方有1条明细
		browser.logger.assertFalse(!recipeList.get(1).get("count").equals("1"), "第二条处方,明细数量不应为"+recipeList.get(1).get("count"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drugList.get(1).get("NAME")), "第二条处方,明细中不包含药品"+drugList.get(1).get("NAME"));
	}


	@Test
	public void test_10(){
		init("CASE-10: 3.给药途径单独分方 (启用 设置2个分方条件: 皮下注射单独分方 / 口服单独分方)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		try {
			drugList = drugList.subList(0, 4);
		} catch (Throwable e) {
			throw new Error("门诊药房有库存药品不足4个,此用例无法执行");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		//启用 精麻毒放药品单独分方 不设置分方条件
		AllocationCondition condition = new AllocationCondition(0, Data.ShortName.route, Data.OperationCode.equal, Arrays.asList(Data.route.piXiaZhuShe.code));
		AllocationCondition condition2 = new AllocationCondition(1, Data.ShortName.route, Data.OperationCode.equal, Arrays.asList(Data.route.kouFu.code));
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(3, true, false, Arrays.asList(condition,condition2));
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.route=Data.route.kouFu.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.route=Data.route.piXiaZhuShe.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		detail.route=Data.route.jingDi.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), detail);
		detail.route=Data.route.jingTui.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(3).get("MEDICINE_ID"), detail);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为3条
		browser.logger.assertFalse(recipeList.size()!=3, "处方条数不应为"+recipeList.size());
		//校验第一条处方有1条明细
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("1"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
		browser.logger.assertFalse(!recipeList.get(0).get("medNames").contains(drugList.get(0).get("NAME")), "第一条处方,明细中不包含药品"+drugList.get(0).get("NAME"));
		//校验第二条处方有1条明细
		browser.logger.assertFalse(!recipeList.get(1).get("count").equals("1"), "第二条处方,明细数量不应为"+recipeList.get(1).get("count"));
		browser.logger.assertFalse(!recipeList.get(1).get("medNames").contains(drugList.get(1).get("NAME")), "第二条处方,明细中不包含药品"+drugList.get(1).get("NAME"));
		//校验第三条处方有2条明细
		browser.logger.assertFalse(!recipeList.get(2).get("count").equals("2"), "第三条处方,明细数量不应为"+recipeList.get(1).get("count"));
		browser.logger.assertFalse(!recipeList.get(2).get("medNames").contains(drugList.get(2).get("NAME")), "第二条处方,明细中不包含药品"+drugList.get(2).get("NAME"));
		browser.logger.assertFalse(!recipeList.get(2).get("medNames").contains(drugList.get(3).get("NAME")), "第二条处方,明细中不包含药品"+drugList.get(3).get("NAME"));
	}


	@Test
	public void test_11(){
		init("CASE-11: 3.给药途径单独分方 (启用 不设置分方条件)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		try {
			drugList = drugList.subList(0, 3);
		} catch (Throwable e) {
			throw new Error("门诊药房有库存药品不足3个,此用例无法执行");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		//启用 精麻毒放药品单独分方 不设置分方条件
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(3, true, false, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.route=Data.route.kouFu.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.route=Data.route.piXiaZhuShe.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		detail.route=Data.route.jingDi.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), detail);
		//获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为1条
		browser.logger.assertFalse(recipeList.size()!=1, "处方条数不应为"+recipeList.size());
		//校验第一条处方有3条明细
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("3"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
	}


	@Test
	public void test_12(){
		init("CASE-12: 3.给药途径单独分方 (停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		try {
			drugList = drugList.subList(0, 3);
		} catch (Throwable e) {
			throw new Error("门诊药房有库存药品不足3个,此用例无法执行");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用所有分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false,false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立各类型药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.route=Data.route.kouFu.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.route=Data.route.piXiaZhuShe.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		detail.route=Data.route.jingDi.desc;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), detail);
		///获取分方结果
		List<Map<String, String>> recipeList = browser.wnOutpatientWorkflow.getRecipeList();
		//校验处方条数为1条
		browser.logger.assertFalse(recipeList.size()!=1, "处方条数不应为"+recipeList.size());
		//校验第一条处方有3条明细
		browser.logger.assertFalse(!recipeList.get(0).get("count").equals("3"), "第一条处方,明细数量不应为"+recipeList.get(0).get("count"));
	}


	@Test
	public void test_13(){
		init("CASE-13: 4.不同药房单独分方 (启用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_jz = browser.decouple.db60.getNomalMedicineList("自费药房", "98363");
		try {
			drugList_mz = drugList_mz.subList(0, 2);
			drugList_jz = drugList_jz.subList(0, 2);
		} catch (Throwable e) {
			browser.logger.assertFalse(true, "获取有库存药品失败");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则4
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(4,true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.pharmacy="门诊药房";
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_mz.get(0).get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_mz.get(1).get("MEDICINE_ID"), detail);
		detail.pharmacy="自费药房";
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_jz.get(0).get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_jz.get(1).get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("2"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("2"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_14(){
		init("CASE-14: 4.不同药房单独分方 (停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList_mz = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_jz = browser.decouple.db60.getNomalMedicineList("自费药房", "98363");
		try {
			drugList_mz = drugList_mz.subList(0, 2);
			drugList_jz = drugList_jz.subList(0, 2);
		} catch (Throwable e) {
			browser.logger.assertFalse(true, "获取有库存药品失败");
		}
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则4
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.pharmacy="门诊药房";
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_mz.get(0).get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_mz.get(1).get("MEDICINE_ID"), detail);
		detail.pharmacy="自费药房";
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_jz.get(0).get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList_jz.get(1).get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("4"), "处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_15(){
		init("CASE-15: 5.皮试药品单独分方 (启用)", true);
		//获取各类型药品
		Map<String, String> medicine = browser.decouple.db60.getNomalMedicine("门诊药房", "98363");
		Map<String, String> SkinTestMedicine = browser.decouple.db60.getSkinTestMedicine("门诊药房",Arrays.asList("249942","249943"), null, null);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则5
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(5,true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicine.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,SkinTestMedicine.get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
//		browser.logger.throwError(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
//		browser.logger.throwError(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_16(){
		init("CASE-16: 5.皮试药品单独分方 (停用)", true);
		//获取各类型药品
		Map<String, String> medicine = browser.decouple.db60.getNomalMedicine("门诊药房", "98363");
		Map<String, String> SkinTestMedicine = browser.decouple.db60.getSkinTestMedicine("门诊药房",Arrays.asList("249942","249943"), null, null);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用分方规则5
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,medicine.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,SkinTestMedicine.get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
//		browser.logger.throwError(!recipeMapList.get(0).get("count").equals("2"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_17(){
		init("CASE-17: 6.自费药品单独分方 (启用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<2, "获取药品失败,数量不足2个");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则6
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(6, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.selfPay=false;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.selfPay=true;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_18(){
		init("CASE-18: 6.自费药品单独分方 (停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<2, "获取药品失败,数量不足2个");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则6
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.selfPay=false;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.selfPay=true;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("2"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_19(){
		init("CASE-19: 7.处方金额超过500需要单独分方(启用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		Map<String, String> drug_highPrice = null;
		Map<String, String> drug_lowPrice_100 = null;
		Map<String, String> drug_lowPrice_200 = null;
		for (Map<String, String> drug : drugList) {
			if (drug_highPrice!=null && drug_lowPrice_100!=null && drug_lowPrice_200!=null) {
				break;
			}
			if (drug_highPrice==null && Double.valueOf(drug.get("RETAIL_PRICE"))>500d) {
				Map<String, String> drugDetail = browser.decouple.db60.getMedicineByMedicineId(drug.get("MEDICINE_ID"));
				Map<String, String> drugOutpUnit = browser.decouple.db60.getOutpUnitByMedicineId(drug.get("MEDICINE_ID"));
				if (drugDetail.get("RETAIL_PACK_UNIT_CODE").equals(drugOutpUnit.get("PACK_UNIT_CODE"))) {
					drug_highPrice=drug;
				}else {
					browser.logger.log(3, "门诊单位和药品单位不一致 跳过");
				}
			}else if (drug_lowPrice_100==null && Double.valueOf(drug.get("RETAIL_PRICE"))<100d) {
				Map<String, String> drugDetail = browser.decouple.db60.getMedicineByMedicineId(drug.get("MEDICINE_ID"));
				Map<String, String> drugOutpUnit = browser.decouple.db60.getOutpUnitByMedicineId(drug.get("MEDICINE_ID"));
				if (drugDetail.get("RETAIL_PACK_UNIT_CODE").equals(drugOutpUnit.get("PACK_UNIT_CODE"))) {
					drug_lowPrice_100=drug;
				}else {
					browser.logger.log(3, "门诊单位和药品单位不一致 跳过");
				}
			}else if (drug_lowPrice_200==null && Double.valueOf(drug.get("RETAIL_PRICE"))<200d) {
				Map<String, String> drugDetail = browser.decouple.db60.getMedicineByMedicineId(drug.get("MEDICINE_ID"));
				Map<String, String> drugOutpUnit = browser.decouple.db60.getOutpUnitByMedicineId(drug.get("MEDICINE_ID"));
				if (drugDetail.get("RETAIL_PACK_UNIT_CODE").equals(drugOutpUnit.get("PACK_UNIT_CODE"))) {
					drug_lowPrice_200=drug;
				}else {
					browser.logger.log(3, "门诊单位和药品单位不一致 跳过");
				}
			}
		}
		browser.logger.log(1,"价格高于500的药品:"+drug_highPrice);
		browser.logger.log(1,"价格低于100的药品:"+drug_lowPrice_100);
		browser.logger.log(1,"价格低于200的药品:"+drug_lowPrice_200);
		browser.logger.assertFalse(drug_highPrice==null, "获取不到价格高于500的药品");
		browser.logger.assertFalse(drug_lowPrice_100==null, "获取不到价格低于100的药品");
		browser.logger.assertFalse(drug_lowPrice_200==null, "获取不到价格低于200的药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则7
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(7, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.num=1;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_highPrice.get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_lowPrice_100.get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_lowPrice_200.get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(0).get("medNames").contains(drug_highPrice.get("NAME")), "第一个处方明细应该包含:"+drug_highPrice.get("NAME"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("2"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("medNames").contains(drug_lowPrice_100.get("NAME")), "第二个处方明细应该包含:"+drug_lowPrice_100.get("NAME"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("medNames").contains(drug_lowPrice_200.get("NAME")), "第二个处方明细应该包含:"+drug_lowPrice_200.get("NAME"));
	}


	@Test
	public void test_20(){
		init("CASE-20: 7.处方金额超过500需要单独分方(停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		Map<String, String> drug_highPrice = null;
		Map<String, String> drug_lowPrice_100 = null;
		Map<String, String> drug_lowPrice_200 = null;
		for (Map<String, String> drug : drugList) {
			if (drug_highPrice!=null && drug_lowPrice_100!=null && drug_lowPrice_200!=null) {
				break;
			}
			if (drug_highPrice==null && Double.valueOf(drug.get("RETAIL_PRICE"))>500d) {
				Map<String, String> drugDetail = browser.decouple.db60.getMedicineByMedicineId(drug.get("MEDICINE_ID"));
				Map<String, String> drugOutpUnit = browser.decouple.db60.getOutpUnitByMedicineId(drug.get("MEDICINE_ID"));
				if (drugDetail.get("RETAIL_PACK_UNIT_CODE").equals(drugOutpUnit.get("PACK_UNIT_CODE"))) {
					drug_highPrice=drug;
				}else {
					browser.logger.log(3, "门诊单位和药品单位不一致 跳过");
				}
			}else if (drug_lowPrice_100==null && Double.valueOf(drug.get("RETAIL_PRICE"))<100d) {
				Map<String, String> drugDetail = browser.decouple.db60.getMedicineByMedicineId(drug.get("MEDICINE_ID"));
				Map<String, String> drugOutpUnit = browser.decouple.db60.getOutpUnitByMedicineId(drug.get("MEDICINE_ID"));
				if (drugDetail.get("RETAIL_PACK_UNIT_CODE").equals(drugOutpUnit.get("PACK_UNIT_CODE"))) {
					drug_lowPrice_100=drug;
				}else {
					browser.logger.log(3, "门诊单位和药品单位不一致 跳过");
				}
			}else if (drug_lowPrice_200==null && Double.valueOf(drug.get("RETAIL_PRICE"))<200d) {
				Map<String, String> drugDetail = browser.decouple.db60.getMedicineByMedicineId(drug.get("MEDICINE_ID"));
				Map<String, String> drugOutpUnit = browser.decouple.db60.getOutpUnitByMedicineId(drug.get("MEDICINE_ID"));
				if (drugDetail.get("RETAIL_PACK_UNIT_CODE").equals(drugOutpUnit.get("PACK_UNIT_CODE"))) {
					drug_lowPrice_200=drug;
				}else {
					browser.logger.log(3, "门诊单位和药品单位不一致 跳过");
				}
			}
		}
		browser.logger.log(1,"价格高于500的药品:"+drug_highPrice);
		browser.logger.log(1,"价格低于100的药品:"+drug_lowPrice_100);
		browser.logger.log(1,"价格低于200的药品:"+drug_lowPrice_200);
		browser.logger.assertFalse(drug_highPrice==null, "获取不到价格高于500的药品");
		browser.logger.assertFalse(drug_lowPrice_100==null, "获取不到价格低于100的药品");
		browser.logger.assertFalse(drug_lowPrice_200==null, "获取不到价格低于200的药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用分方规则7
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false,null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.num=1;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_highPrice.get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_lowPrice_100.get("MEDICINE_ID"), detail);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_lowPrice_200.get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("3"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_21(){
		init("CASE-21: 8.外配药单独分方(启用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_extProvision = browser.decouple.db60.getExtProvisionMedicineList();
		Map<String, String> drug_normal = null;
		Map<String, String> drug_extProvision = null;
		for (Map<String, String> drug : drugList) {
			if (drug_normal!=null && drug_extProvision!=null) {
				break;
			}
			Map<String, String> drug_extPro = SdkTools.getMapByValue(drugList_extProvision, "MEDICINE_ID", drug.get("MEDICINE_ID"));
			if (drug_normal==null && drug_extPro==null) {
				drug_normal = drug;
			}
			if (drug_extProvision==null && drug_extPro!=null) {
				drug_extProvision = drug;
			}
		}
		browser.logger.log(1,"外配药品:"+drug_extProvision);
		browser.logger.log(1,"非外配药品:"+drug_normal);
		browser.logger.assertFalse(drug_normal==null, "获取不到非外配药品");
		browser.logger.assertFalse(drug_extProvision==null, "获取不到外配药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用分方规则7
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(8, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_normal.get("MEDICINE_ID"), detail);
		detail.extProvision=true;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_extProvision.get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_22(){
		init("CASE-22: 8.外配药单独分方(停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		List<Map<String, String>> drugList_extProvision = browser.decouple.db60.getExtProvisionMedicineList();
		Map<String, String> drug_normal = null;
		Map<String, String> drug_extProvision = null;
		for (Map<String, String> drug : drugList) {
			if (drug_normal!=null && drug_extProvision!=null) {
				break;
			}
			Map<String, String> drug_extPro = SdkTools.getMapByValue(drugList_extProvision, "MEDICINE_ID", drug.get("MEDICINE_ID"));
			if (drug_normal==null && drug_extPro==null) {
				drug_normal = drug;
			}
			if (drug_extProvision==null && drug_extPro!=null) {
				drug_extProvision = drug;
			}
		}
		browser.logger.log(1,"外配药品:"+drug_extProvision);
		browser.logger.log(1,"非外配药品:"+drug_normal);
		browser.logger.assertFalse(drug_normal==null, "获取不到非外配药品");
		browser.logger.assertFalse(drug_extProvision==null, "获取不到外配药品");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//停用分方规则7
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_normal.get("MEDICINE_ID"), detail);
		detail.extProvision=true;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug_extProvision.get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("2"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_23(){
		init("CASE-23: 9.西药和成药单独分方(启用)", true);
		//获取各类型药品
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
		Map<String, String> patentMedicine = browser.decouple.db60.getNomalMedicineList("门诊药房","98364").get(0);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(9, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,patentMedicine.get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_24(){
		init("CASE-24: 9.西药和成药单独分方(停用)", true);
		//获取各类型药品
		Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
		Map<String, String> patentMedicine = browser.decouple.db60.getNomalMedicineList("门诊药房","98364").get(0);
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drug.get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,patentMedicine.get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("2"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}


	@Test
	public void test_25(){
		init("CASE-25: 10.慢病药品单独分方(启用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<2, "获取可开立药品失败");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRule_saveByPriority(10, true, false);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.chronicDisease=false;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.chronicDisease=true;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}


	@Test
	public void test_26(){
		init("CASE-26: 10.慢病药品单独分方(停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<2, "获取可开立药品失败");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		PrescribeDetail detail = new PrescribeDetail();
		detail.chronicDisease=false;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), detail);
		detail.chronicDisease=true;
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), detail);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("2"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}

	@Test
	public void test_27(){
		init("CASE-27: 11.单独药品可设置分方(启用,设置一条规则)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<2, "获取可开立药品失败");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		AllocationCondition condition_1 = new AllocationCondition(0, Data.ShortName.medicine, Data.OperationCode.equal, Arrays.asList(drugList.get(0).get("MEDICINE_ID")));
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false, Arrays.asList(condition_1));
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
	}

	@Test
	public void test_28(){
		init("CASE-28: 11.单独药品可设置分方(启用,设置两条规则)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<4, "获取可开立药品失败");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		AllocationCondition condition_1 = new AllocationCondition(0, Data.ShortName.medicine, Data.OperationCode.equal, Arrays.asList(drugList.get(0).get("MEDICINE_ID")));
		AllocationCondition condition_2 = new AllocationCondition(1, Data.ShortName.medicine, Data.OperationCode.equal, Arrays.asList(drugList.get(1).get("MEDICINE_ID"),drugList.get(2).get("MEDICINE_ID")));
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false, Arrays.asList(condition_1,condition_2));
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(3).get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=3, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(0).get("medNames").contains(drugList.get(0).get("NAME")), "第一条处方明细应该包含:"+drugList.get(0).get("NAME"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("count").equals("2"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("medNames").contains(drugList.get(1).get("NAME")), "第二条处方明细应该包含:"+drugList.get(1).get("NAME"));
		browser.logger.assertFalse(!recipeMapList.get(1).get("medNames").contains(drugList.get(2).get("NAME")), "第二条处方明细应该包含:"+drugList.get(2).get("NAME"));
		browser.logger.assertFalse(!recipeMapList.get(2).get("count").equals("1"), "第三条处方明细条数不应为:"+recipeMapList.get(2).get("count"));
		browser.logger.assertFalse(!recipeMapList.get(2).get("medNames").contains(drugList.get(3).get("NAME")), "第三条处方明细应该包含:"+drugList.get(3).get("NAME"));
	}

	@Test
	public void test_29(){
		init("CASE-29: 11.单独药品可设置分方(启用,不设置规则)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<4, "获取可开立药品失败");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则9
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(11, true, false, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(3).get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("4"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}

	@Test
	public void test_30(){
		init("CASE-30: 11.单独药品可设置分方(停用)", true);
		//获取各类型药品
		List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
		browser.logger.assertFalse(drugList.size()<4, "获取可开立药品失败");
		//挂号
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		//登录
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		//启用分方规则
		browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
		browser.wnOutpatientWorkflow.allocationRuleAll_saveByPriority(3, true, false, null);
		//叫号
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		//开立药品
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(0).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(1).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(2).get("MEDICINE_ID"), null);
		browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60,drugList.get(3).get("MEDICINE_ID"), null);
		//检验处方列表
		List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
		browser.logger.assertFalse(recipeMapList.size()!=1, "处方条数不应为:"+recipeMapList.size());
		browser.logger.assertFalse(!recipeMapList.get(0).get("count").equals("4"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
	}

}