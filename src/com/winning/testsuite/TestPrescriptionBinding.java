package com.winning.testsuite;

import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPrescriptionBinding extends OutpatientTestBase {
    public TestPrescriptionBinding() {
        super();
    }

    static {

        SdkTools.initReport("处方绑定","处方绑定专项.html");
        Data.ignoreErrors="None";
    }

    @Test
    public void test_001()throws InterruptedException{
        init("CASE-01:CL069=0,单诊断不分方显示默认诊断",true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        browser.wnOutpatientWorkflow.SetCL069(Data.host,"0","不启用");
        browser.wnOutpatientWorkflow.SetTCM_MEDICINE_DIAG_VERIFY(Data.host);
        //禁用所有分方规则
        browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
//        browser.helper.Multiplediagnosis_pathWay(Data.test_disease,Data.test_disease2);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.outpatientDisaposalDrug);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Chinesepatentmedicine);
        List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
        System.out.println(recipeMapList.get(0).get("title"));
        if (recipeMapList.get(0).get("title").equals(Data.test_disease)){
            browser.logger.log("诊断名称："+ Data.test_disease);
        }
        browser.wnOutpatientWorkflow.signOff(0);

    }

    @Test
    public void test_002() throws InterruptedException{
        init("CASE-02: CL069=0，多诊断不分方默认显示所有诊断", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        //设置CL069参数
        browser.wnOutpatientWorkflow.SetCL069(Data.host,"0","不启用");
        browser.wnOutpatientWorkflow.SetTCM_MEDICINE_DIAG_VERIFY(Data.host);
        //禁用所有分方规则
        browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
        //开立流程
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.Multiplediagnosis_pathWay(Data.test_disease,Data.test_disease2);
        String diseasesName = Data.test_disease +"、"+ Data.test_disease2;
        System.out.println(diseasesName);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.outpatientDisaposalDrug);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Chinesepatentmedicine);
        List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
        System.out.println(recipeMapList.get(0).get("title"));
        if (recipeMapList.get(0).get("title").contains(diseasesName)){
            browser.logger.log("诊断名称："+ diseasesName);
        }
        browser.wnOutpatientWorkflow.signOff(0);

    }

    @Test
    public void test_003() throws InterruptedException{
        init("CASE-03: CL069=0，多诊断分方默认显示所有诊断", true);
        browser.wnwd.openUrl(Data.web_url);
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        //设置CL069参数
        browser.wnOutpatientWorkflow.SetCL069(Data.host,"0","不启用");
        browser.wnOutpatientWorkflow.SetTCM_MEDICINE_DIAG_VERIFY(Data.host);
        //禁用所有分方规则
        browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
        //开启西药和成药单独分方
        browser.wnOutpatientWorkflow.allocationRule_saveByPriority(9, true, false);
        //开立流程
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.Multiplediagnosis_pathWay(Data.test_disease,Data.test_disease2);
        String diseasesName = Data.test_disease +"、"+ Data.test_disease2;
        System.out.println(diseasesName);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.outpatientDisaposalDrug);
        browser.wnOutpatientWorkflow.prescribeOrder(Data.Med_Chinesepatentmedicine);
        List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
        System.out.println(recipeMapList.size());
        System.out.println(recipeMapList.get(0).get("title"));
//        if (recipeMapList.get(0).get("title").contains(diseasesName)){
        for (int i = 0; i <recipeMapList.size() ; i++) {
            if (recipeMapList.get(i).get("title").contains(diseasesName)){
                System.out.println("诊断名称："+recipeMapList.get(i).get("title"));
                browser.logger.log("诊断名称："+ diseasesName);
            }
        }
//        }
        browser.wnOutpatientWorkflow.signOff(0);

    }


    @Test
    public void test_004() throws InterruptedException{
        init("CASE-04:CL069=0,修改诊断后显示诊断内容", true);

        Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
        Map<String, String> patentMedicine = browser.decouple.db60.getNomalMedicineList("门诊药房","98364").get(0);
        //挂号
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        //登录
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        //个性化配置草药、中成药开立校验中医诊断、中医证型
        browser.wnOutpatientWorkflow.SetTCM_MEDICINE_DIAG_VERIFY(Data.host);
        //启用分方规则9
        browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
        browser.wnOutpatientWorkflow.allocationRule_saveByPriority(9, true, false);
        //叫号
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        //主诉、诊断
        browser.wnOutpatientWorkflow.chiefComplaint(Data.test_chief_complain);
        browser.wnOutpatientWorkflow.Multiplediagnosis_pathWay(Data.test_disease,Data.test_disease2);
        String diseasesName = Data.test_disease +"、"+ Data.test_disease2;
        System.out.println(diseasesName);
        //开立药品
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null);
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, patentMedicine.get("MEDICINE_ID"), null);
        //检验处方列表
//        browser.logger.throwError(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
//        browser.logger.throwError(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
//        browser.logger.throwError(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));
        browser.wnOutpatientWorkflow.signOff(0);
        browser.wnOutpatientWorkflow.deleteDiagnosis_pathWay(Data.test_disease);
        browser.wnOutpatientWorkflow.diagnosis(Data.test_disease3);
        String diseasesNames= Data.test_disease2 +"、"+Data.test_disease3;
        browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
        List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
        for (Map<String, String> stringStringMap : recipeMapList) {
            System.out.println("每一条诊断内容：" + stringStringMap.get("title"));
            if (stringStringMap.get("title").contains(diseasesName)) {
                browser.logger.log("诊断名称：" + diseasesName);
            }else if (stringStringMap.size() == recipeMapList.size()&&stringStringMap.get("title").contains(diseasesNames)){
                browser.logger.log("诊断名称：" + diseasesNames);
            }
        }
        browser.wnOutpatientWorkflow.signOff(0);
    }



    @Test
    public void test_005()throws InterruptedException{
        init("CASE-05:CL069=1,", true);

//        Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
//        Map<String, String> patentMedicine = browser.decouple.db60.getNomalMedicineList("门诊药房","98364").get(0);
        //挂号
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        //登录
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        //个性化配置草药、中成药开立校验中医诊断、中医证型
        browser.wnOutpatientWorkflow.SetTCM_MEDICINE_DIAG_VERIFY(Data.host);
        browser.wnOutpatientWorkflow.SetCL069(Data.host,"1","处方不默认加载诊断模式");
        //启用分方规则9
        browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
        browser.wnOutpatientWorkflow.allocationRule_saveByPriority(9, true, false);
        //叫号
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        //主诉、诊断
//        browser.helper.chiefComplaint(Data.test_chief_complain);
//        browser.helper.Multiplediagnosis_pathWay(Data.test_disease,Data.test_disease2);
//        String diseasesName = Data.test_disease +"、"+ Data.test_disease2;
//        System.out.println(diseasesName);
        //开立药品
        browser.wnOutpatientWorkflow.prescribeOrder(Data.outpatientDisaposalDrug);
//        browser.helper.prescribeOrder(Data.Med_Chinesepatentmedicine);
//        browser.prescribeByMedicineId(drug.get("MEDICINE_ID"), null);
//        browser.prescribeByMedicineId(patentMedicine.get("MEDICINE_ID"), null);
        //检验处方列表
//        browser.logger.throwError(recipeMapList.size()!=2, "处方条数不应为:"+recipeMapList.size());
//        browser.logger.throwError(!recipeMapList.get(0).get("count").equals("1"), "第一条处方明细条数不应为:"+recipeMapList.get(0).get("count"));
//        browser.logger.throwError(!recipeMapList.get(1).get("count").equals("1"), "第二条处方明细条数不应为:"+recipeMapList.get(1).get("count"));

        browser.wnOutpatientWorkflow.bindingDiagnosis();
//        browser.helper.deleteDiagnosis_pathWay(Data.test_disease);
//        browser.helper.diagnosis(Data.test_disease3);
//        String diseasesNames= Data.test_disease2 +"、"+Data.test_disease3;
//        System.out.println(diseasesNames);
//        browser.helper.prescribeOrder(Data.test_prescribe_drug);
//        List<Map<String, String>> recipeMapList = browser.helper.getRecipeList();
//        for (Map<String, String> stringStringMap : recipeMapList) {
//            System.out.println("每一条诊断内容：" + stringStringMap.get("title"));
//            if (stringStringMap.get("title").contains(diseasesName)) {
//                browser.logger.log("诊断名称：" + diseasesName);
//            }else if (stringStringMap.size() == recipeMapList.size()&&stringStringMap.get("title").contains(diseasesNames)){
//                browser.logger.log("诊断名称：" + diseasesNames);
//            }
//        }
//        browser.helper.signOff(0);
    }


    @Test
    public void test_008() throws InterruptedException{
        init("CASE-04: 测试", true);
        Map<String, String> drug = browser.decouple.db60.getNomalMedicineList("门诊药房", "98363").get(0);
        Map<String, String> patentMedicine = browser.decouple.db60.getNomalMedicineList("门诊药房","98364").get(0);
        //挂号
        ArrayList<String> encounterInfo = browser.decouple.newEncounter();
        //登录
        browser.wnwd.openUrl(Data.web_url);
        browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
        //个性化配置草药、中成药开立校验中医诊断、中医证型
        browser.wnOutpatientWorkflow.SetTCM_MEDICINE_DIAG_VERIFY(Data.host);
        //启用分方规则9
        browser.wnOutpatientWorkflow.allocationRule_saveAll(false, false);
        browser.wnOutpatientWorkflow.allocationRule_saveByPriority(9, true, false);
        //叫号
        browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
        browser.wnOutpatientWorkflow.skip();
        browser.wnOutpatientWorkflow.emrTemplateRecommend();
        browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
        browser.wnOutpatientWorkflow.changeKnowledgeSystem(Data.knowledge_system);
        //开立药品
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, drug.get("MEDICINE_ID"), null);
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, patentMedicine.get("MEDICINE_ID"), null);
        
        List<Map<String, String>> recipeMapList = browser.wnOutpatientWorkflow.getRecipeList();
        for (Map<String, String> stringStringMap : recipeMapList) {
            System.out.println("每一条诊断内容：" + stringStringMap.get("title"));
            if (stringStringMap.get("diagnoses")==null) {
                System.out.println("111111111111111111111111111");
            }else{
                System.out.println("2222222222 :"+ recipeMapList.get(0).toString());
            }
        }
        browser.wnOutpatientWorkflow.signOff(0);
    }


}