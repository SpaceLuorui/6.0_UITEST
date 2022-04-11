package com.winning.testsuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.base.SdkWebDriver;
import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestIntravenousInfusion extends OutpatientTestBase {
    public TestIntravenousInfusion() {
        super();
    }
    static {
        SdkTools.initReport("输液专项", "输液专项.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","autoTest");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-01: 输液搜索开立", true);
        List<Map<String, String>> medList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        String DrugName = null;
        Map<String, String> medicine = null;
        for (Map<String, String> med : medList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                DrugName = med.get("CS_NAME");
                medicine = med;
                browser.logger.log(2, DrugName);
                break;
            }
        }
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.prescribeOrder(medicine.get("CS_NO"), new ArrayList<>(Arrays.asList(medicine.get("NAME"), medicine.get("PACK"))));
        browser.wnOutpatientWorkflow.signOff(0);
        String his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")", SdkTools.compareMoney(his_cost, totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")");


    }

    @Test
    public void test_02_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-02: 输液收藏快捷开立", true);
        List<Map<String, String>> medList = browser.decouple.db60.getAllCommonMedId();
        String DrugName = null;
        Map<String, String> medicine = null;
        for (Map<String, String> med : medList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                medicine = med;
                System.out.println("***********************"+medicine);
                DrugName = medicine.get("COMMODITY_NAME_CHINESE") + " " + medicine.get("PACK_SPEC");
                break;
            }
        }
        System.out.println("++++++++++" + DrugName);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.CommonHerb(DrugName);
//        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        SdkTools.sleep(2000);
        browser.wnOutpatientWorkflow.signOff(0);
        String his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")", SdkTools.compareMoney(his_cost, totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")");
    }

    @Test
    public void test_03_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-03:输液历史处置引用", true);
        List<Map<String, String>> medList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        String DrugName = null;
        Map<String, String> medicine = null;
        for (Map<String, String> med : medList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                DrugName = med.get("CS_NAME");
                medicine = med;
                browser.logger.log(2, DrugName);
                break;
            }
        }
        SdkWebDriver wnwd = browser.wnwd;
        wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetRE022(Data.host,"0","知识编辑器");
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
        browser.wnOutpatientWorkflow.quoteEmrTemplate(Data.emrTemplateName);
        browser.wnOutpatientWorkflow.prescribeOrder(medicine.get("CS_NO"), new ArrayList<>(Arrays.asList(medicine.get("NAME"), medicine.get("PACK"))));
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();
        SdkTools.sleep(2000);

        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(4));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.enterHistory();
        browser.wnOutpatientWorkflow.checkHistoryOrders();
        browser.wnOutpatientWorkflow.quoteAllHistory();
        browser.wnOutpatientWorkflow.signOff(0);
        String History_his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String History_totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + History_his_cost + "/UI显示:" + History_totalCost + ")", SdkTools.compareMoney(History_his_cost, History_totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + History_his_cost + "/UI显示:" + History_totalCost + ")");

        browser.wnOutpatientWorkflow.endOutpatient();
    }


    @Test
    public void test_04_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-04: 输液的模板引用开立", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.quoteTemplateForFactory("输液专项模板引用");
        browser.wnOutpatientWorkflow.signOff(0);
        String his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")", SdkTools.compareMoney(his_cost, totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")");
    }

    @Test
    public void test_05_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-05: 输液的诊疗路径引用开立", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.quoteTemplateForTreatmentPaths("输液专项模板开立测试");
        browser.wnOutpatientWorkflow.signOff(0);
        String his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")", SdkTools.compareMoney(his_cost, totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")");
    }


    @Test
    public void test_06_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-06: 皮试输液", true);
//        List<Map<String,String>> medList = browser.decouple.db60.getNomalMedicineList("门诊药房","98363");
        List<Map<String, String>> medlist = browser.decouple.db60.getSkinTestMedicineList("门诊药房", new ArrayList<>(Arrays.asList("249942", "249943")), null, null);
        String DrugCS_NO = null;
        Map<String, String> medicine = null;
        for (Map<String, String> med : medlist) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                DrugCS_NO = med.get("CS_NO");
                medicine = med;
                browser.logger.log(2, DrugCS_NO);
                break;
            }
        }
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.searchOrder(medicine.get("NAME"));
        browser.wnOutpatientWorkflow.checkSkinTestDialog(true, null, "皮试");
        browser.wnOutpatientWorkflow.editAndCommitOrder(null);
        List<String> orderNameList = browser.wnOutpatientWorkflow.getOrderNameList();
        browser.logger.assertFalse(orderNameList.size() == 0, "开立医嘱失败");
        browser.logger.assertFalse(orderNameList.size() == 1, "医嘱数量不正确,没有找到皮试联动" + orderNameList);
        browser.wnOutpatientWorkflow.signOff(0);
        String totalCount = browser.wnOutpatientWorkflow.getTotalCost();
        List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
        List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
        Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList, "ypmc", medicine.get("NAME"));
        browser.logger.assertFalse(cfmx == null, "His处方明细中,没有找到药品:", medicine.get("NAME"));
        browser.logger.log(0, "处方明细检查通过:" + medicine.get("NAME"));
        browser.logger.assertFalse(!cfmx.get("sycfbz").equals("1"), "His处方中,输液医嘱标识不正确", "" + cfmx);
        browser.logger.log(0, "输液医嘱标识检查通过: sycfbz=1");
        String hisCharge = browser.decouple.chargeByCisxhList(cisxhList).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(totalCount, hisCharge), "收费不一致", "60界面显示费用:" + totalCount + "\nHis实际收费:" + hisCharge);
    }


    @Test
    public void test_07_TestIntravenousInfusion() throws InterruptedException {
        init("CASE-07: 多输液医嘱项开立", true);
        //获取静滴类型输液医嘱数据-
        List<Map<String, String>> MedList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        String DrugNO = null;
        Map<String, String> Medicine = null;
        for (Map<String, String> med : MedList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                DrugNO = med.get("CS_NO");
                Medicine = med;
                browser.logger.log(2, DrugNO);
                break;
            }
        }
        //获取静滴类型输液医嘱数据-提供给收藏快捷开立医嘱使用
        List<Map<String, String>> ComList = browser.decouple.db60.getAllCommonMedId();
        String ComDrugName = null;
        Map<String, String> MeDicine = null;
        for (Map<String, String> med : ComList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                MeDicine = med;
                ComDrugName = MeDicine.get("COMMODITY_NAME_CHINESE") + " " + MeDicine.get("PACK_SPEC");
                break;
            }
        }
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setParamsForTestAllService();
        browser.wnOutpatientWorkflow.SetRE022(Data.host,"0","知识编辑器");
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
//        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.prescribeOrder(Medicine.get("CS_NO"), new ArrayList<>(Arrays.asList(Medicine.get("NAME"), Medicine.get("PACK"))));//搜索开立输液医嘱
        SdkTools.sleep(2000);
        browser.wnOutpatientWorkflow.CommonHerb(ComDrugName);//收藏开立输液医嘱
        SdkTools.sleep(2000);
        browser.wnOutpatientWorkflow.signOff(2000);
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();
        SdkTools.sleep(2000);

        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(4));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.findDignoseCheck(Data.test_disease);
        browser.wnOutpatientWorkflow.diagnoseRelationRecords();


        browser.wnOutpatientWorkflow.quoteTemplateForFactory("输液专项模板引用");
        browser.wnOutpatientWorkflow.enterHistory();
        browser.wnOutpatientWorkflow.checkHistoryOrders();
        browser.wnOutpatientWorkflow.quoteAllHistory();
        browser.wnOutpatientWorkflow.signOff(1000);


        List<String> cisxhList = browser.decouple.waitSignOffSync(encounterInfo.get(0));
        List<Map<String, String>> cfmxList = browser.decouple.getCfmxList(cisxhList);
        Map<String, String> cfmx = SdkTools.getMapByValue(cfmxList, "ypmc", Medicine.get("NAME"));
        browser.logger.assertFalse(cfmx == null, "His处方明细中,没有找到药品:", Medicine.get("NAME"));
        browser.logger.log(0, "处方明细检查通过:" + Medicine.get("NAME"));
        browser.logger.assertFalse(!cfmx.get("sycfbz").equals("1"), "His处方中,输液医嘱标识不正确", "" + cfmx);
        browser.logger.log(0, "输液医嘱标识检查通过: sycfbz=1");

        String History_his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String History_totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + History_his_cost + "/UI显示:" + History_totalCost + ")", SdkTools.compareMoney(History_his_cost, History_totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + History_his_cost + "/UI显示:" + History_totalCost + ")");
        browser.wnOutpatientWorkflow.endOutpatient();


    }


}
