package com.winning.testsuite;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDisposalOfDisposition extends OutpatientTestBase {
    public TestDisposalOfDisposition() {
        super();
    }

    static {
        SdkTools.initReport("处置开立专项", "TestDisposalOfDisposition.html");
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","autoTest");
            Data.addSaveHisSfList = true;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_01_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-01: 输液药品成组开立", true);
        List<Map<String, String>> medList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        System.out.println(medList);
        String DrugCS_NO = null;
        Map<String, String> medicine = null;
        for (Map<String, String> med : medList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                DrugCS_NO = med.get("CS_NO");
                medicine = med;
                System.out.println(DrugCS_NO);
                browser.logger.log(2, DrugCS_NO);
                break;
            }
        }
        System.out.println(DrugCS_NO);
        System.out.println(medicine);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));

        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnwd.waitElementByXpathAndClick("处置加工厂西成药", WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "西成药"), Framework.defaultTimeoutMax);
        browser.wnOutpatientWorkflow.prescribeOrderForDD(medicine.get("CS_NAME"), new ArrayList<>(Arrays.asList(medicine.get("CS_NAME"), medicine.get("PACK"))));
        browser.decouple.checkExecuteDiagnos(encounterInfo.get(2));
        browser.wnwd.waitElementByXpathAndClick("处置加工厂治疗", WnOutpatientXpath.outpatientDisposalFactoryButton.replace("?1", "治疗"), Framework.defaultTimeoutMax);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
        SdkTools.sleep(5000);
        browser.wnOutpatientWorkflow.signOff(0);
        String his_cost = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        String totalCost = "" + (Double.valueOf(browser.wnOutpatientWorkflow.getTotalCost()));
        browser.wnwd.assertTrue("费用对比不通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")", SdkTools.compareMoney(his_cost, totalCost));
        browser.logger.log(1, "费用对比通过(HIS:" + his_cost + "/UI显示:" + totalCost + ")");

        browser.logger.log("已收费医嘱：" + Data.SaveHisSfList);

        for (int i = 0; i < Data.SaveHisSfList.size(); i++) {
            if (Data.SaveHisSfList.get(i).get("ypmc").equals(Data.test_prescribe_treat)) {
                browser.decouple.hisRefundBySjh(Data.SaveHisSfList.get(i).get("jssjh"));
                browser.decouple.updatejlzt(encounterInfo.get(1), Data.SaveHisSfList.get(i).get("ypmc"));
            }
        }
        browser.wnOutpatientWorkflow.revokeOrder();
        browser.wnOutpatientWorkflow.deleteOrder();
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }


    @Test
    public void test_02_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-02: 初诊患者搜索开立所有类型医嘱", true);
        List<Map<String, String>> medList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        System.out.println(medList);
        String DrugCS_NO = null;
        Map<String, String> medicine = null;
        for (Map<String, String> med : medList) {
            String routeDesc = browser.decouple.db60.getRouteByMedId(med.get("MEDICINE_ID"));
            browser.logger.log(1, med.get("MEDICINE_ID") + "" + routeDesc);
            if (routeDesc.equals("静滴")) {
                DrugCS_NO = med.get("CS_NO");
                medicine = med;
                System.out.println(DrugCS_NO);
                browser.logger.log(2, DrugCS_NO);
                break;
            }
        }
        System.out.println(DrugCS_NO);
        System.out.println(medicine);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_Pathology);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_lab);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
//        browser.wnOutpatientWorkflow.prescribeOrderForDD(medicine.get("NAME"), new ArrayList<>(Arrays.asList(medicine.get("NAME"), medicine.get("PACK"))));
        browser.wnOutpatientWorkflow.signOff(0);

        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
        browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
    }


    @Test
    public void test_03_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-03: 初诊患者模板开立", true);
//        Data.orderTemplateName = Data.orderTemplateName+System.currentTimeMillis();
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.quoteTemplateForFactory(Data.DispositionTemplateName);
        browser.wnOutpatientWorkflow.signOff(0);
        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
        browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
    }


    @Test
    public void test_04_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-04: 历史处置，引用后修改医嘱内容并开立", true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_herb);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
//        browser.wnOutpatientWorkflow.quoteTemplateForFactory(Data.DispositionTemplateName);
        browser.wnOutpatientWorkflow.signOff(0);

        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(4));
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        List<String> diagnosisList = new ArrayList<>(Arrays.asList(Data.test_disease));
        List<String> disposalList = new ArrayList<>(Arrays.asList(Data.test_prescribe_drug, Data.test_prescribe_herb));
        browser.wnOutpatientWorkflow.quoteHistoryDiagnosisAndDisposal(diagnosisList, disposalList);
        browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_herb);
        browser.wnOutpatientWorkflow.updateOrder(Data.test_prescribe_drug);
        browser.wnOutpatientWorkflow.deleteOrder();
        browser.logger.log(1, "医嘱数量：" + browser.wnOutpatientWorkflow.getOrderNameList().size());
//        browser.logger.throwError(browser.helper.getOrderNameList().size()!=0, "删除医嘱失败");

        browser.wnOutpatientWorkflow.signOff(0);
//		browser.helper.emrSignoff();

        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
        browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");
    }


    /*@Test
    public void test_05_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-05: 知识体系推荐中快速开立", true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem("卫宁医学知识体系");
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.editAllPhysicalSign();
//        browser.wnOutpatientWorkflow.quoteRecommendLab_knowledge();
        browser.wnOutpatientWorkflow.quoteRecommendExam_knowledge();
//        browser.wnOutpatientWorkflow.quoteRecommendDrug_knowledge();
        browser.wnOutpatientWorkflow.signOff(0);
        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
        browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");

    }*/


    @Test
    public void test_06_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-06: 诊疗路径简易版快速开立", true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.editAllPhysicalSign();
        browser.wnOutpatientWorkflow.quoteRecommendLab_pathWay();
        browser.wnOutpatientWorkflow.quoteRecommendExam_pathWay();
        browser.wnOutpatientWorkflow.signOff(0);
        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
        browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");

    }


    @Test
    public void test_07_TestDisposalOfDisposition() throws InterruptedException {
        init("CASE-07: 收藏开立", true);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem("诊疗路径简易版");
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.CommonAll();
        browser.wnOutpatientWorkflow.signOff(0);
        String cost_60 = browser.wnOutpatientWorkflow.getTotalCost();
        String cost_his = browser.decouple.win60MedicineSF(encounterInfo.get(0)).get(0);
        browser.logger.assertFalse(!SdkTools.compareMoney(cost_his, cost_60), "his与60收费不一致:(his:" + cost_his + "/win60:" + cost_60 + ")");
        browser.logger.log(1, "收费对比通过:(his:" + cost_his + "/win60:" + cost_60 + ")");

    }

}
