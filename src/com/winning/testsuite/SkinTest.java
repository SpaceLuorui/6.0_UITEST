package com.winning.testsuite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SkinTest extends OutpatientTestBase {
	
	public SkinTest() {
		super();
	}

	static {
		Data.getScreenShot=true;
		SdkTools.initReport("皮试专项","skin_test.html");
		try{
			Config.loadOnlineDefaultConfig("DEV");
			Config.loadOnlineExtraConfig("DEV","autoTest");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test_case_001() throws InterruptedException {
		init("CASE-01 - 非皮试药品", true);
		Map<String, String> medicine = browser.decouple.db60.getNomalMedicine("门诊药房", "98363");
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.helper.searchOrder(medicine.get("CS_NO"), new ArrayList<>(Arrays.asList(medicine.get("NAME"),medicine.get("PACK"))));
		browser.wnOutpatientWorkflow.searchOrder(medicine.get("CS_NAME"));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(false,null,"");
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
		browser.logger.assertFalse(orderNameList.size()==0, "开立医嘱失败");
//		browser.logger.throwError(orderNameList.size()>1, "医嘱数量不正确,不应开立皮试联动",""+orderNameList);
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
		Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList,"ypmc",medicine.get("NAME"));
		browser.logger.assertFalse(cfmx==null,"His处方明细中,没有找到药品:",medicine.get("NAME"));
		browser.logger.log(0,"处方明细检查通过:"+medicine.get("NAME"));
		browser.logger.assertFalse(!cfmx.get("shbz5").equals("0"),"His处方中,皮试标识不正确",""+cfmx);
		browser.logger.log(0,"皮试标识检查通过: shbz5=0");
		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}
	
	@Test
	public void test_case_002() throws InterruptedException {
		init("CASE-02 - 原液皮试", true);
		Map<String, String> OriginSkinTestMedicine = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249943")),false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(OriginSkinTestMedicine.get("NAME"));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(true,new ArrayList<>(Arrays.asList("皮试","取消")),"皮试");
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
		browser.logger.assertFalse(orderNameList.size()==0, "开立医嘱失败");
//		browser.logger.throwError(orderNameList.size()>1, "医嘱数量不正确,60不应开立皮试联动",""+orderNameList);
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
		Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList,"ypmc",OriginSkinTestMedicine.get("NAME"));
		browser.logger.assertFalse(cfmx==null,"His处方明细中,没有找到药品:",OriginSkinTestMedicine.get("NAME"));
		browser.logger.log(0,"处方明细检查通过: "+OriginSkinTestMedicine.get("NAME"));
		browser.logger.assertFalse(!cfmx.get("shbz5").equals("8"),"His处方中,皮试标识不正确",""+cfmx);
		browser.logger.log(0,"皮试标识检查通过: shbz5=8");
		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}
	
	@Test
	public void test_case_003() throws InterruptedException {
		init("CASE-03 - 皮试液皮试", true);
		Map<String, String> skinTestMedicine = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249942")),false);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(skinTestMedicine.get("NAME"));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(true,new ArrayList<>(Arrays.asList("皮试","取消")),"皮试");
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
		browser.logger.assertFalse(orderNameList.size()==0, "开立医嘱失败");
//		browser.logger.throwError(orderNameList.size()==1, "医嘱数量不正确,没有找到皮试联动");
//		browser.logger.throwError(!orderNameList.contains(skinTestMedicine.get("SKIN_TEST_NAME")), "医嘱数量不正确,没有找到皮试联动",""+orderNameList);
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
		Map<String, String> pscfmx = SdkTools.getMapByValue(cfmxList,"ypmc",skinTestMedicine.get("SKIN_TEST_NAME"));
		browser.logger.assertFalse(pscfmx==null,"His处方明细中,没有找到皮试联动服务:",skinTestMedicine.get("SKIN_TEST_NAME"));
		browser.logger.log(0,"联动服务处方明细检查通过:"+skinTestMedicine.get("SKIN_TEST_NAME"));
		Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList,"ypmc",skinTestMedicine.get("NAME"));
//		browser.logger.throwError(cfmx==null,"His处方明细中,没有找到药品:",skinTestMedicine.get("NAME"));
//		browser.logger.log(0,"药品处方明细检查通过:"+skinTestMedicine.get("NAME"));
		browser.logger.assertFalse(!cfmx.get("shbz5").equals("1"),"His处方中,皮试标识不正确",""+cfmx);
		browser.logger.log(0,"皮试标识检查通过:shbz5=1");
		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}

	@Test
	public void test_case_004() throws InterruptedException {
		init("CASE-04 - 免试", true);
		Map<String, String> freeTestMedicine = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249942","249943")),true);
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.searchOrder(freeTestMedicine.get("CS_NO"), new ArrayList<>(Arrays.asList(freeTestMedicine.get("NAME"),freeTestMedicine.get("PACK"))));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(true,new ArrayList<>(Arrays.asList("皮试","免试","取消")),"免试");
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
		browser.logger.assertFalse(orderNameList.size()==0, "开立医嘱失败");
//		browser.logger.throwError(orderNameList.size()>1, "医嘱数量不正确,不应开立皮试联动",""+orderNameList);
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
		Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList,"ypmc",freeTestMedicine.get("NAME"));
		browser.logger.assertFalse(cfmx==null,"His处方明细中,没有找到药品:",freeTestMedicine.get("NAME"));
		browser.logger.log(0,"药品处方明细检查通过:"+freeTestMedicine.get("NAME"));
		browser.logger.assertFalse(!cfmx.get("shbz5").equals("0"),"His处方中,皮试标识不正确",""+cfmx);
		browser.logger.log(0,"皮试标识检查通过:shbz5=0");
		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}

	//从0308迭代开始，不需要再维护药品的过敏源分类；依据皮试记录中药品的药理分类进行判断,预计需要调用第三方接口才能有数据接入，暂不处理

	@Test @Ignore
	public void test_case_005() throws InterruptedException {
		init("CASE-05 - 阴性 - 有效期内", true);
//		Map<String, String> skinTestMedicineHasPeriod = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249942","249943")),null,true);
//		Integer skinTestPeriod =  Integer.valueOf(skinTestMedicineHasPeriod.get("PERIOD"));
//		String skinTestDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis()-24*3600000*(skinTestPeriod-1)));
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.helper.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.helper.loginOutPatientNew(Data.test_select_subject);
//		browser.helper.skip();
//		browser.helper.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.helper.addSkinTestRecord("药物", skinTestMedicineHasPeriod.get("ALLERGY"),"阴性" ,skinTestDate);
//		browser.helper.addSkinTestRecord("食物", "猪肉","阳性" ,skinTestDate);
//		browser.helper.addSkinTestRecord("环境", "空气粉尘","阳性" ,skinTestDate);
//		browser.helper.closePatientDetail();
//		browser.helper.searchOrder(skinTestMedicineHasPeriod.get("CS_NO"), new ArrayList<>(Arrays.asList(skinTestMedicineHasPeriod.get("NAME"),skinTestMedicineHasPeriod.get("PACK"))));
//		browser.helper.addDiagnoseIfNeed();
//		browser.helper.checkSkinTestDialog(false,null,null);
//		browser.helper.editAndCommitOrder(null);
//		browser.helper.afterFactory();
//		List<String> orderNameList = browser.helper.getOrderNameList();
//		browser.logger.throwError(orderNameList.size()==0, "开立医嘱失败");
////		browser.logger.throwError(orderNameList.size()>1, "医嘱数量不正确,不应开立皮试联动",""+orderNameList);
//		browser.helper.signOff(0);
//		String totalCount = browser.helper.getTotalCost();
//		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
//		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
//		Map<String, String> cfmx = Public.getMapByValue(cfmxList,"ypmc",skinTestMedicineHasPeriod.get("NAME"));
//		browser.logger.throwError(cfmx==null,"His处方明细中,没有找到药品:",skinTestMedicineHasPeriod.get("NAME"));
//		browser.logger.log(0,"药品处方明细检查通过:"+skinTestMedicineHasPeriod.get("NAME"));
//		browser.logger.throwError(!cfmx.get("shbz5").equals("0"),"His处方中,皮试标识不正确",""+cfmx);
//		browser.logger.log(0,"皮试标识检查通过:shbz5=0");
//		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
//		browser.logger.throwError(!Public.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}
	
	@Test
	public void test_case_006() throws InterruptedException {
		init("CASE-06 - 阴性 - 有效期外", true);
		Map<String, String> skinTestMedicineHasPeriod = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249942","249943")),null,true);
		Integer skinTestPeriod =  Integer.valueOf(skinTestMedicineHasPeriod.get("PERIOD"));	
		String skinTestDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis()-24*3600000*(skinTestPeriod+1)));
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.addSkinTestRecord("药物", skinTestMedicineHasPeriod.get("ALLERGY"),"阴性" ,skinTestDate);
		browser.wnOutpatientWorkflow.addSkinTestRecord("食物", "猪肉","阳性" ,skinTestDate);
		browser.wnOutpatientWorkflow.addSkinTestRecord("环境", "空气粉尘","阳性" ,skinTestDate);
		browser.wnOutpatientWorkflow.closePatientDetail();
		browser.wnOutpatientWorkflow.searchOrder(skinTestMedicineHasPeriod.get("CS_NO"));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(true,null,"皮试");
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
		browser.logger.assertFalse(orderNameList.size()==0, "开立医嘱失败");
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
		Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList,"ypmc",skinTestMedicineHasPeriod.get("NAME"));
		browser.logger.assertFalse(cfmx==null,"His处方明细中,没有找到药品:",skinTestMedicineHasPeriod.get("NAME"));
		browser.logger.log(0,"药品处方明细检查通过:"+skinTestMedicineHasPeriod.get("NAME"));
		browser.logger.assertFalse(!cfmx.get("shbz5").equals("1")&&!cfmx.get("shbz5").equals("8"),"His处方中,皮试标识不正确",""+cfmx);
		browser.logger.log(0,"皮试标识检查通过:shbz5 in (1,8)");
		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}	

	//从0308迭代开始，不需要再维护药品的过敏源分类；依据皮试记录中药品的药理分类进行判断,预计需要调用第三方接口才能有数据接入，暂不处理
	@Test @Ignore
	public void test_case_007() throws InterruptedException {
		init("CASE-07 - 阳性 - 有效期内", true);
//		Map<String, String> skinTestMedicineHasPeriod = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249942","249943")),null,true);
//		Integer skinTestPeriod =  Integer.valueOf(skinTestMedicineHasPeriod.get("PERIOD"));
//		String skinTestDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis()-24*3600000*(skinTestPeriod-1)));
//		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
//		browser.wnwd.openUrl(Data.web_url);
//		browser.helper.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
//		browser.helper.loginOutPatientNew(Data.test_select_subject);
//		browser.helper.skip();
//		browser.helper.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
//		browser.helper.addSkinTestRecord("药物", skinTestMedicineHasPeriod.get("ALLERGY"),"阳性" ,skinTestDate);
//		browser.helper.addSkinTestRecord("食物", "猪肉","阴性" ,skinTestDate);
//		browser.helper.addSkinTestRecord("环境", "空气粉尘","阴性" ,skinTestDate);
//		browser.helper.closePatientDetail();
//		browser.helper.searchOrder(skinTestMedicineHasPeriod.get("CS_NO"), new ArrayList<>(Arrays.asList(skinTestMedicineHasPeriod.get("NAME"),skinTestMedicineHasPeriod.get("PACK"))));
//		browser.helper.addDiagnoseIfNeed();
//		browser.helper.checkAllergyWarnBox();
	}	
	
	@Test
	public void test_case_008() throws InterruptedException {
		init("CASE-08 - 阳性 - 有效期外", true);
		Map<String, String> skinTestMedicineHasPeriod = browser.decouple.db60.getSkinTestMedicine("门诊药房",new ArrayList<>(Arrays.asList("249942","249943")),null,true);
		Integer skinTestPeriod =  Integer.valueOf(skinTestMedicineHasPeriod.get("PERIOD"));	
		String skinTestDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(System.currentTimeMillis()-24*3600000*(skinTestPeriod+1)));
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.addSkinTestRecord("药物", skinTestMedicineHasPeriod.get("ALLERGY"),"阳性" ,skinTestDate);
		browser.wnOutpatientWorkflow.addSkinTestRecord("食物", "猪肉","阴性" ,skinTestDate);
		browser.wnOutpatientWorkflow.addSkinTestRecord("环境", "空气粉尘","阴性" ,skinTestDate);
		browser.wnOutpatientWorkflow.closePatientDetail();
		browser.wnOutpatientWorkflow.searchOrder(skinTestMedicineHasPeriod.get("CS_NO"));
		browser.wnOutpatientWorkflow.addDiagnoseIfNeed();
		browser.wnOutpatientWorkflow.checkSkinTestDialog(true,null,"皮试");
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
		browser.logger.assertFalse(orderNameList.size()==0, "开立医嘱失败");
		browser.wnOutpatientWorkflow.signOff(0);
		String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
		List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
		List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
		Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList,"ypmc",skinTestMedicineHasPeriod.get("NAME"));
		browser.logger.assertFalse(cfmx==null,"His处方明细中,没有找到药品:",skinTestMedicineHasPeriod.get("NAME"));
		browser.logger.log(0,"药品处方明细检查通过:"+skinTestMedicineHasPeriod.get("NAME"));
		browser.logger.assertFalse(!cfmx.get("shbz5").equals("1")&&!cfmx.get("shbz5").equals("8"),"His处方中,皮试标识不正确",""+cfmx);
		browser.logger.log(0,"皮试标识检查通过:shbz5 in (1,8)");
		String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
		browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge),"收费不一致","60界面显示费用:"+totalCount+"\nHis实际收费:"+hisCharge);
	}
}