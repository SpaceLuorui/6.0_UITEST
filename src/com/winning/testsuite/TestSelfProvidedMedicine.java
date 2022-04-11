package com.winning.testsuite;

import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSelfProvidedMedicine extends OutpatientTestBase {
    public TestSelfProvidedMedicine() {
        super();
    }

    static {
        SdkTools.initReport("自备药专项", "TestSelfProvidedMedicine.html");
        Data.closeBrowser = false;
        Data.headless = true;
        Data.ignoreErrors="";
        Data.outOfStockFlag=true;
    }



    @Test
    public void test_01_TestAntibacterials() throws InterruptedException {
        init("CASE-01: 自备药正常开立流程", true);
        Map<String, String> param_CL023 = null;
        try {
            param_CL023 = browser.decouple.db60.getParam("CL023");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (param_CL023!=null && param_CL023.containsKey("PARAM_VALUE")&&!param_CL023.get("PARAM_VALUE").equals("0")) {
            SdkTools.logger.log("请先将CL023参数设置成0");
            throw new Error("请先将CL023参数设置成0");
        }

        List<Map<String, String>> drugList_self = browser.decouple.db60.getSelfProvisionMedicineList();//取自备药药品list
        List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        Map<String, String> drugSelf_no = null;
        for (Map<String, String> drug : drugList_self) {
            drugSelf_no = SdkTools.getMapByValue(drugList, "MEDICINE_ID", drug.get("MEDICINE_ID"));
            if (drugSelf_no!=null) {
                break;
            }
        }
        browser.logger.log(1, "自备药品"+drugSelf_no);
        browser.logger.assertFalse(drugSelf_no==null, "没有获取到自备药品");

        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.unselectSearchStock();
        PrescribeDetail detail = new PrescribeDetail();
        detail.self = true;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drugSelf_no.get("MEDICINE_ID"),detail);
        browser.wnOutpatientWorkflow.signOff(0);
        Boolean ifCheck = null;
        if (encounterInfo.get(0).equals("")) {
            ifCheck = false;
        }
        else {
            ifCheck = browser.decouple.getzbybz(encounterInfo.get(0));
        }

        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }

    @Test
    public void test_02_TestAntibacterials() throws InterruptedException {
        init("CASE-01: 自备药成组开立", true);
        Map<String, String> param_CL023 = null;
        try {
            param_CL023 = browser.decouple.db60.getParam("CL023");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (param_CL023!=null && param_CL023.containsKey("PARAM_VALUE")&&!param_CL023.get("PARAM_VALUE").equals("0")) {
            SdkTools.logger.log("请先将CL023参数设置成0");
            throw new Error("请先将CL023参数设置成0");
        }

        List<Map<String, String>> drugList_self = browser.decouple.db60.getSelfProvisionMedicineList();//取自备药药品list
        List<Map<String, String>> drugList = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363");
        Map<String, String> drugSelf_no = null;
        for (Map<String, String> drug : drugList_self) {
            drugSelf_no = SdkTools.getMapByValue(drugList, "MEDICINE_ID", drug.get("MEDICINE_ID"));
            if (drugSelf_no!=null) {
                break;
            }
        }
        browser.logger.log(1, "自备药品"+drugSelf_no);
        browser.logger.assertFalse(drugSelf_no==null, "没有获取到自备药品");

        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.unselectSearchStock();
        PrescribeDetail detail = new PrescribeDetail();
        detail.self = true;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drugSelf_no.get("MEDICINE_ID"),detail);
        browser.wnOutpatientWorkflow.signOff(0);
        Boolean ifCheck = null;
        if (encounterInfo.get(0).equals("")) {
            ifCheck = false;
        }
        else {
            ifCheck = browser.decouple.getzbybz(encounterInfo.get(0));
        }

        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }




    @Test
    public void test_03_TestAntibacterials() throws InterruptedException {
        init("CASE-03: 无库存药品开立自备药", true);

        List<Map<String, String>> drugList_self = browser.decouple.db60.getSelfProvisionMedicineList();//取自备药药品list
        List<Map<String, String>> drugList = browser.decouple.db60.getOutOfStocklMedicineList("门诊药房", "98363");
        Map<String, String> drugSelf = null;
        String drugName = null;
        for (Map<String, String> drug : drugList_self) {
            drugSelf = SdkTools.getMapByValue(drugList, "MEDICINE_ID", drug.get("MEDICINE_ID"));
            if (drugSelf!=null) {
                break;
            }

        }
        assert drugSelf != null;
        drugName=drugSelf.get("NAME");
        System.out.println(drugName);
        browser.logger.log(1, "自备药品"+drugSelf);
        browser.logger.assertFalse(false, "没有获取到自备药品");

        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, "456");
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.unselectSearchStock();
        PrescribeDetail detail = new PrescribeDetail();
        detail.self = true;

        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drugSelf.get("MEDICINE_ID"),detail);
        browser.wnOutpatientWorkflow.signOff(0);
        Boolean ifCheck = null;
        if (encounterInfo.get(0).equals("")) {
            ifCheck = false;
        }
        else {
            ifCheck = browser.decouple.getzbybz(encounterInfo.get(0));
        }

        browser.wnOutpatientWorkflow.saveEmr();
        browser.wnOutpatientWorkflow.emrSignoff();
        browser.wnOutpatientWorkflow.endOutpatient();

    }


}
