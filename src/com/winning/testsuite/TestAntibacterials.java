package com.winning.testsuite;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAntibacterials extends OutpatientTestBase {
    public TestAntibacterials() {
        super();
    }

    static {
        SdkTools.initReport("抗菌药专项", "TestAntibacterials.html");
        
		try{
			Config.loadOnlineDefaultConfig("DEV");
			Config.loadOnlineExtraConfig("DEV","autoTest");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
    }
    

    @Test
    public void test_01_TestAntibacterials() throws InterruptedException {
        init("CASE-01: 无抗菌药物权限医生开立普通、非限制级、限制级、特殊级", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.testAntibacterialsUserLoginAccount, Data.testAntibacterialsUserLoginPwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.outpatientDisaposalDrug);
        browser.wnOutpatientWorkflow.unselectSearchStock();
        List<String> list = Arrays.asList(Data.Med_Antibacterials_Unrestricted, Data.Med_Antibacterials_Restricted, Data.Med_Antibacterials_Special);
        for (String s : list) {
            browser.wnOutpatientWorkflow.searchOrder(s);
            boolean res = browser.wnOutpatientWorkflow.getWarningMes();
            browser.logger.assertFalse(!res, "权限校验不通过");
        }
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }


    @Test
    public void test_02_TestAntibacterials() throws InterruptedException {
        init("CASE-02: 无抗菌药物权限医生模板开立普通、非限制级、限制级、特殊级", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.testAntibacterialsUserLoginAccount, Data.testAntibacterialsUserLoginPwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.quoteTemplateForControlledSubstance("抗菌药测试模板");
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();
    }


    @Test
    public void test_03_TestAntibacterials() throws InterruptedException {
        init("CASE-03: 无抗菌药物权限医生历史处置开立普通、非限制级、限制级、特殊级", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.testAntibacterialsUserLoginAccount, Data.testAntibacterialsUserLoginPwd);
        List<Data.PermissonType> pList = new ArrayList<>();
        pList.add(Data.PermissonType.Drug);
        pList.add(Data.PermissonType.Antibacterials_Unrestricted);
        pList.add(Data.PermissonType.Antibacterials_Restricted);
        pList.add(Data.PermissonType.Antibacterials_Special);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(pList);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.quoteTemplateForControlledSubstance("抗菌药测试模板");
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
    public void test_04_TestAntibacterials() throws InterruptedException {
        init("CASE-04: 无抗菌药物权限医生诊疗路径开立普通、非限制级、限制级、特殊级", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.testAntibacterialsUserLoginAccount, Data.testAntibacterialsUserLoginPwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis("白细胞疾患");
        browser.wnOutpatientWorkflow.quoteRecommendTreat_pathWay();
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }


    @Test @Ignore
    public void test_05_TestAntibacterials() throws InterruptedException {
        init("CASE-05: 无抗菌药物权限医生收藏开立开立普通、非限制级、限制级、特殊级", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.testAntibacterialsUserLoginAccount, Data.testAntibacterialsUserLoginPwd);
        browser.wnOutpatientWorkflow.setDoctorPrescriptionPermissionForSync(Collections.singletonList(Data.PermissonType.Drug));
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis("白细胞疾患");
        browser.wnOutpatientWorkflow.unselectSearchStock();
        browser.wnOutpatientWorkflow.CommonForControlledSubstance("白细胞疾患");
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();
    }

    @Test
    public void test_06_TestAntibacterials() throws InterruptedException {
        init("CASE-06: 抗菌药权限非限制级、限制级、特殊级医嘱逐个开立", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.testAntibacterialsUserLoginAccount, Data.testAntibacterialsUserLoginPwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis("白细胞疾患");
        browser.wnOutpatientWorkflow.unselectSearchStock();
        //抗菌药开立权限
        List<Data.PermissonType> pList2 = Arrays.asList(Data.PermissonType.Antibacterials_Unrestricted, Data.PermissonType.Antibacterials_Restricted, Data.PermissonType.Antibacterials_Special);
        //抗菌药药品
        List<String> dlist = Arrays.asList(Data.Med_Antibacterials_Unrestricted, Data.Med_Antibacterials_Restricted, Data.Med_Antibacterials_Special);
        for (int i = 0; i < dlist.size(); i++) {
            List<Data.PermissonType> pList = new ArrayList<>();
            pList.add(Data.PermissonType.Drug);//西药开立权限
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



