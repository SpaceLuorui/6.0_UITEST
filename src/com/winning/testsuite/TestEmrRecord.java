package com.winning.testsuite;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientWorkflow;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.base.SdkWebDriver;
import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestEmrRecord extends OutpatientTestBase {
    public TestEmrRecord() {
        super();
    }

    static {
//        Frmcons.savereportFile = Frmcons.savereportFile.replace("report.html", "emr.html");
//        Data.headless = false;
        SdkTools.initReport("病历专项", "emr.html");
    }


    @Test
    public void test_01_emr_record() throws InterruptedException {
        init("病历流程（诊断为高血压）", true);
        SdkWebDriver wnwd = browser.wnwd;
        WnOutpatientWorkflow helper = browser.wnOutpatientWorkflow;
        wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        helper.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        helper.SetRE022(Data.host,"0","知识编辑器");
        helper.loginOutPatientNew(Data.test_select_subject);
        helper.skip();
        helper.emrTemplateRecommend();
        helper.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        helper.changeKnowledgeSystem(Data.knowledge_system);
        SdkTools.sleep(20000);
        helper.findDignoseCheck("高血压");
        String MrtName = helper.diagnoseRelationRecords1();
        SdkTools.sleep(3000);
        helper.prescribeOrder(Data.test_prescribe_herb);
        helper.prescribeOrder(Data.test_prescribe_treat);
        helper.prescribeOrder(new ArrayList<>(Arrays.asList(Data.test_prescribe_drug, Data.test_prescribe_drug_pack)), new ArrayList<>(Arrays.asList("1")));
        helper.EmrTypeSet();
        helper.signOff(0);

        Boolean ifCheck = null;
        if (MrtName.equals("")) {
            ifCheck = false;
        }
        else {
            ifCheck = browser.decouple.db60.getFlagByEmrMrtId(MrtName);
        }

        helper.emrSignoffAndcheck(ifCheck);
        helper.endOutpatient();
    }

    @Test
    public void test_02_emr_record_test() throws InterruptedException {
        init("历史病历引用", true);
        SdkWebDriver wnwd = browser.wnwd;
        WnOutpatientWorkflow helper = browser.wnOutpatientWorkflow;
        wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        helper.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        helper.SetRE022(Data.host,"0","知识编辑器");
        helper.loginOutPatientNew(Data.test_select_subject);
        helper.skip();
        helper.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        helper.changeKnowledgeSystem(Data.knowledge_system);
        SdkTools.sleep(4000);
        helper.findDignoseCheck("高血压");
        String MrtName = helper.diagnoseRelationRecords1();
        SdkTools.sleep(3000);
        helper.prescribeOrder(Data.test_prescribe_herb);
        helper.prescribeOrder(Data.test_prescribe_treat);
        helper.prescribeOrder(new ArrayList<>(Arrays.asList(Data.test_prescribe_drug, Data.test_prescribe_drug_pack)), new ArrayList<>(Arrays.asList("1")));
        helper.EmrTypeSet();
        helper.signOff(0);

        Boolean ifCheck = null;
        if (MrtName.equals("")) {
            ifCheck = false;
        }
        else {
            ifCheck = browser.decouple.db60.getFlagByEmrMrtId(MrtName);
        }

        helper.emrSignoffAndcheck(ifCheck);
        helper.endOutpatient();
        SdkTools.sleep(2000);
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(4));
        SdkTools.sleep(5000);
        helper.emrTemplateRecommend();
        helper.findDignoseCheck("高血压");
        helper.diagnoseRelationRecords1();
        helper.historyEmrRecord();
        helper.emrSignoffAndcheck(ifCheck);
        helper.endOutpatient();
    }


    @Test
    public void test_03_emr_record_test() throws InterruptedException {
        init("书写助手医嘱引用", true);
        SdkWebDriver wnwd = browser.wnwd;
        WnOutpatientWorkflow helper = browser.wnOutpatientWorkflow;
        wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounterTwice();
        helper.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        helper.SetRE022(Data.host,"0","知识编辑器");
        helper.loginOutPatientNew(Data.test_select_subject);
        helper.skip();
        helper.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(2));
        helper.changeKnowledgeSystem(Data.knowledge_system);
        SdkTools.sleep(4000);
        helper.findDignoseCheck("高血压");
        String MrtName = helper.diagnoseRelationRecords1();
        SdkTools.sleep(3000);
        helper.prescribeOrder(Data.test_prescribe_herb);
        helper.prescribeOrder(Data.test_prescribe_treat);
        helper.prescribeOrder(new ArrayList<>(Arrays.asList(Data.test_prescribe_drug, Data.test_prescribe_drug_pack)), new ArrayList<>(Arrays.asList("1")));
        helper.EmrTypeSet();
        helper.signOff(0);
        Boolean ifCheck = null;
        if (MrtName.equals("")) {
            ifCheck = false;
        }
        else {
            ifCheck = browser.decouple.db60.getFlagByEmrMrtId(MrtName);
        }

        helper.emrSignoffAndcheck(ifCheck);
        helper.endOutpatient();
        SdkTools.sleep(2000);
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(4));
        SdkTools.sleep(5000);
        helper.emrTemplateRecommend();
        helper.findDignoseCheck("高血压");
        helper.diagnoseRelationRecords1();
        helper.historyEmrRecord();
        helper.writingAssistantForMedicAladvice();
        helper.emrSignoffAndcheck(ifCheck);
        helper.endOutpatient();
    }

}
