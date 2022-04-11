package com.winning.testsuite;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestControlledDrugs extends OutpatientTestBase {
    public TestControlledDrugs() {
        super();
    }

    static {
        SdkTools.initReport("精麻毒放专项", "TestControlledDrugs.html");
        Data.closeBrowser = false;
    }


    @Test
    public void test_01_TestControlledDrugs() throws InterruptedException {
        init("CASE-01: 无对应处方开立精一、精二、麻醉、毒性药品", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin("8816", Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        browser.wnOutpatientWorkflow.unselectSearchStock();
        List<String> list = Arrays.asList(Data.Med_SpiritOneMed, Data.Med_SpiritTwoMed, Data.Med_NarcoticDrug, Data.Med_HighlyToxic, Data.Med_RadioPharmaceuticals);
        for (String s : list) {
            browser.wnOutpatientWorkflow.searchOrder(s);
            boolean res = browser.wnOutpatientWorkflow.getWarningMes();
            browser.logger.assertFalse(!res, "权限校验失败");
        }
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }


    @Test
    public void test_02_TestControlledDrugs() throws InterruptedException {
        init("CASE-02: 模板开立精一、精二、麻醉、毒性药品限制", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin("8816", Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.quoteTemplateForControlledSubstance("精麻毒放测试模板");
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();
    }


    @Test
    public void test_03_TestControlledDrugs() throws InterruptedException {
        init("CASE-03: 历史处置开立精一、精二、麻醉、毒性药品限制", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin("8816", Data.default_user_login_pwd);
        List<Data.PermissonType> pList = new ArrayList<>();
        pList.add(Data.PermissonType.Drug);
        pList.add(Data.PermissonType.SpiritOne);
        pList.add(Data.PermissonType.SpiritTwo);
        pList.add(Data.PermissonType.NarcoticDrug);
        pList.add(Data.PermissonType.HighlyToxic);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(pList);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.quoteTemplateForControlledSubstance("精麻毒放测试模板");
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(4));
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));

        browser.wnOutpatientWorkflow.enterHistory();
        browser.wnOutpatientWorkflow.quoteAllHistory();
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }


    @Test
    public void test_04_TestControlledDrugs() throws InterruptedException {
        init("CASE-04: 诊疗路径推荐开立精一、精二、麻醉、毒性药品限制", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin("8816", Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis("苯丙酮尿症");
        browser.wnOutpatientWorkflow.quoteRecommendTreat_pathWay();
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }

    @Test
    public void test_05_TestControlledDrugs() throws InterruptedException {
        init("CASE-05: 收藏开立精一、精二、麻醉、毒性药品限制", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin("8816", Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis("苯丙酮尿症");
        browser.wnOutpatientWorkflow.unselectSearchStock();
        browser.wnOutpatientWorkflow.CommonForControlledSubstance("苯丙酮尿症");
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }

    @Test
    public void test_06_TestControlledDrugs() throws InterruptedException {
        init("CASE-06: 精一、精二、麻醉、毒性药品、放射药品开立", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin("8816", Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis("苯丙酮尿症");
        browser.wnOutpatientWorkflow.unselectSearchStock();
        List<Data.PermissonType> pList2 = Arrays.asList(Data.PermissonType.SpiritOne, Data.PermissonType.SpiritTwo, Data.PermissonType.NarcoticDrug, Data.PermissonType.HighlyToxic, Data.PermissonType.RadioPharmaceuticals);
        List<String> dlist = Arrays.asList(Data.Med_SpiritOneMed, Data.Med_SpiritTwoMed, Data.Med_NarcoticDrug, Data.Med_HighlyToxic, Data.Med_RadioPharmaceuticals);
        for (int i = 0; i < dlist.size(); i++) {
            List<Data.PermissonType> pList = new ArrayList<>();
            pList.add(Data.PermissonType.Drug);
            pList.add(pList2.get(i));
            browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(pList);
            browser.wnOutpatientWorkflow.prescribeOrder(dlist.get(i));
            browser.wnOutpatientWorkflow.signOff(0);
        }
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();
    }

}



