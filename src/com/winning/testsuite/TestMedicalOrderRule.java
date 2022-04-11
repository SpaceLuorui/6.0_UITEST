package com.winning.testsuite;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.entity.PrescribeDetail;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMedicalOrderRule extends OutpatientTestBase {
    public TestMedicalOrderRule() {
        super();
    }

    static {
        SdkTools.initReport("医嘱规则专项", "医嘱规则专项.html");
        Data.closeBrowser = false;
    }

    


    @Test
    public void test_01() throws InterruptedException {
        init("CASE-01: 服务规则控制-->服务名称:心电向量图;控制方式:禁止,提示语:心电向量图禁止开立", true);
        Data.test_prescribe_exam = "心电向量图";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("心电向量图_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("心电向量图禁止开立_禁止")) {
				browser.logger.boxLog(0, "医嘱规则生效", "心电向量图禁止开立_禁止");

			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()!=0,"心电向量图医嘱开立成功,报错");
    }
    
    
    @Test
    public void test_02() throws InterruptedException {
        init("CASE-02: 服务规则控制-->服务名称:心电向量图;控制方式:提示,提示语:心电向量图禁止开立", true);
        Data.test_prescribe_drug = "心电向量图";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("心电向量图_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		
		try {
			Data.checkWarning = true;
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_drug);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("心电向量图禁止开立_提示")) {
				browser.logger.boxLog(0, "医嘱规则生效", "心电向量图禁止开立_提示");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.wnOutpatientWorkflow.editAndCommitOrder(null);
		browser.wnOutpatientWorkflow.afterFactory();
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()<1,"心电向量图医嘱未开立成功,报错");
    }
    
    @Test
    public void test_03() throws InterruptedException {
        init("CASE-03: 服务规则控制-->服务类型:治疗;控制方式:禁止,提示语:治疗禁止开立", true);
        Data.test_prescribe_treat = "洗胃";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("治疗_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("禁止开立治疗")) {
				browser.logger.boxLog(0, "医嘱规则生效", "禁止开立治疗");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()!=0,"治疗开立成功,报错");
    }
    
    @Test
    public void test_04() throws InterruptedException {
        init("CASE-04: 服务规则控制-->服务类型:治疗;控制方式:提示,提示语:治疗禁止开立", true);
        Data.test_prescribe_treat = "小抢救";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("治疗_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			Data.checkWarning = true;
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_treat);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("提示开立治疗")) {
				browser.logger.boxLog(0, "医嘱规则生效", "提示开立治疗");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.wnOutpatientWorkflow.commitFactory();
		browser.wnOutpatientWorkflow.afterFactory();
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()<1,"治疗未开立成功,报错");
    }
    
    @Test
    public void test_05() throws InterruptedException {
        init("CASE-05: 医嘱天数规则控制-->服务名称:氯雷他定片;控制方式:禁止,提示语:氯雷他定片不能超过5天", true);
        String medicineId = "57396033766287361";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("医嘱天数规则测试_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		
		try {
	        PrescribeDetail prescribeDetail = new PrescribeDetail();
	        prescribeDetail.Days = 6;
			browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立失败")||e.toString().contains("氯雷他定片不能超过5天")) {
				browser.logger.boxLog(0, "医嘱规则生效", "氯雷他定片不能超过5天");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()!=0,"氯雷他定片开立成功,报错");
    }
    
    @Test
    public void test_06() throws InterruptedException {
        init("CASE-06: 医嘱天数规则控制-->服务名称:氯雷他定片;控制方式:提示,提示语:氯雷他定片不能超过5天", true);
        String medicineId = "57396033766287361";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("医嘱天数规则测试_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			PrescribeDetail prescribeDetail = new PrescribeDetail();
	        prescribeDetail.Days = 6;
			browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("氯雷他定片不能超过5天")) {
				browser.logger.boxLog(0, "医嘱规则生效", "氯雷他定片不能超过5天");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
//		browser.wnOutpatientWorkflow.commitFactory();
//		browser.wnOutpatientWorkflow.afterFactory();
		browser.wnOutpatientWorkflow.checkAbnormalOrdersAndWarningTxt("氯雷他定片不能超过5天");
//		browser.logger.assertFalse(browser.wnOutpatientWorkflow.getClinicalOrderCount()<1,"氯雷他定片未开立成功,报错");
    }
    
    @Test
    public void test_07() throws InterruptedException {
        init("CASE-07: 服务重复开立规则控制-->服务名称:无痛麻醉(麻醉药需另开);控制方式:禁止,提示语:无痛麻醉只能开一次", true);
        Data.test_prescribe_exam = "无痛麻醉(麻醉药需另开)";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("服务重复开立规则控制_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		try {
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("无痛麻醉只能开一次")) {
				browser.logger.boxLog(0, "医嘱规则生效", "无痛麻醉只能开一次");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "服务重复开立规则控制，此处应该有1个氯雷他定片医嘱");
		browser.logger.assertFalse(orderCount>1,"无痛麻醉开立成功,报错");
    }
    
    @Test
    public void test_08() throws InterruptedException {
        init("CASE-08: 服务重复开立规则控制-->服务名称:无痛麻醉(麻醉药需另开);控制方式:提示,提示语:无痛麻醉只能开一次", true);
        Data.test_prescribe_exam = "无痛麻醉(麻醉药需另开)";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("服务重复开立规则控制_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		try {
			Data.checkWarning = true;
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("无痛麻醉只能开一次")) {
				browser.logger.boxLog(0, "医嘱规则生效", "无痛麻醉只能开一次，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.wnOutpatientWorkflow.commitFactory();
		browser.wnOutpatientWorkflow.afterFactory();
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "服务重复开立规则控制，此处应该有2个氯雷他定片医嘱");
		browser.logger.assertFalse(orderCount<2,"氯雷他定片未开立成功,报错");
    }
    
    @Test
    public void test_09() throws InterruptedException {
        init("CASE-09: 检验数量规则控制-->服务名称:血细胞分析;控制方式:禁止,提示语:血细胞分析数量不能超过3", true);
        String cs_id = "4300132689";//血细胞分析
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("检验数量规则控制_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			PrescribeDetail prescribeDetail = new PrescribeDetail();
	        prescribeDetail.num = 5;
	        Data.checkWarning = true;
			browser.wnOutpatientWorkflow.prescribeLabByCsid(browser.decouple.db60, cs_id, prescribeDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("血细胞分析数量不能超过3")) {
				browser.logger.boxLog(0, "医嘱规则生效", "血细胞分析数量不能超过3，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "服务重复开立规则控制，此处应该有0个血细胞分析嘱");
		browser.logger.assertFalse(orderCount!=0,"血细胞分析开立成功,报错");
    }
    
    @Test
    public void test_10() throws InterruptedException {
        init("CASE-10: 检验数量规则控制-->服务名称:血细胞分析;控制方式:提示,提示语:血细胞分析数量不能超过3", true);
        String cs_id = "4300132689";//血细胞分析
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("检验数量规则控制_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			Data.checkWarning = true;
			PrescribeDetail prescribeDetail = new PrescribeDetail();
	        prescribeDetail.num = 5;
	        browser.wnOutpatientWorkflow.prescribeLabByCsid(browser.decouple.db60, cs_id, prescribeDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("血细胞分析数量不能超过3")) {
				browser.logger.boxLog(0, "医嘱规则生效", "血细胞分析数量不能超过3，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.wnOutpatientWorkflow.commitFactory();
		browser.wnOutpatientWorkflow.afterFactory();
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "服务重复开立规则控制，此处应该有1个血细胞分析医嘱");
		browser.logger.assertFalse(orderCount<1,"血细胞分析未开立成功,报错");
    }
    
    @Test
    public void test_11() throws InterruptedException {
        init("CASE-11: 检查数量规则控制-->服务名称:单脏器彩色多普勒超声检查;控制方式:禁止,提示语:单脏器彩色多普勒超声检查项目不能超过2", true);
        Data.test_prescribe_exam = "单脏器彩色多普勒超声检查";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("检查数量规则控制_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			List<String> orderDetail = new ArrayList<String>();
			orderDetail.add("1");
			orderDetail.add("2");
			orderDetail.add("3");
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam,null,orderDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("单脏器彩色多普勒超声检查项目不能超过2")) {
				browser.logger.boxLog(0, "医嘱规则生效", "单脏器彩色多普勒超声检查项目不能超过2，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "检查数量规则控制，此处应该有0个单脏器彩色多普勒超声检查医嘱");
		browser.logger.assertFalse(orderCount!=0,"单脏器彩色多普勒超声检查开立成功,报错");
    }
    
    @Test
    public void test_12() throws InterruptedException {
        init("CASE-12: 检查数量规则控制-->服务名称:单脏器彩色多普勒超声检查;控制方式:提示,提示语:单脏器彩色多普勒超声检查项目不能超过2", true);
        Data.test_prescribe_exam = "单脏器彩色多普勒超声检查";
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("检查数量规则控制_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		try {
			Data.checkWarning = true;
			List<String> orderDetail = new ArrayList<String>();
			orderDetail.add("1");
			orderDetail.add("2");
			orderDetail.add("3");
			browser.wnOutpatientWorkflow.prescribeOrder(Data.test_prescribe_exam,null,orderDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("单脏器彩色多普勒超声检查项目不能超过2")) {
				browser.logger.boxLog(0, "医嘱规则生效", "单脏器彩色多普勒超声检查项目不能超过2，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.wnOutpatientWorkflow.commitFactory();
		browser.wnOutpatientWorkflow.afterFactory();
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "检查数量规则控制，此处应该有1个单脏器彩色多普勒超声检查医嘱");
		browser.logger.assertFalse(orderCount<1,"单脏器彩色多普勒超声检查未开立成功,报错");
    }
    
    @Test
    public void test_13() throws InterruptedException {
        init("CASE-13: 西成药余量天数规则控制-->服务名称:复方皂矾丸;控制方式:禁止,提示语:复方皂矾丸，余量天数2天", true);
        String medicineId = "57395887737399323";//复方皂矾丸
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("西成药余量天数规则控制_禁止", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.Days = 5;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
		try {
			browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("复方皂矾丸，余量天数2天")) {
				browser.logger.boxLog(0, "医嘱规则生效", "复方皂矾丸，余量天数2天，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "西成药余量天数规则控制，此处应该有1个复方皂矾丸医嘱");
		browser.logger.assertFalse(orderCount!=1,"复方皂矾丸开立成功,报错");
    }
    
    @Test
    public void test_14() throws InterruptedException {
        init("CASE-14: 西成药余量天数规则控制-->服务名称:复方皂矾丸;控制方式:提示,提示语:复方皂矾丸，余量天数2天", true);
        String medicineId = "57395887737399323";//复方皂矾丸
		ArrayList<String> encounterInfo = browser.decouple.newEncounter();
		browser.wnwd.openUrl(Data.web_url);
		browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
		browser.wnOutpatientWorkflow.SetCL091(Data.host, "1", "");
		browser.wnOutpatientWorkflow.medicalOrderRule_saveAll(false);
		browser.wnOutpatientWorkflow.medicalOrderRule_saveByName("西成药余量天数规则控制_提示", true);
		browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
		browser.wnOutpatientWorkflow.skip();
		browser.wnOutpatientWorkflow.callNumberByNo(encounterInfo.get(0), encounterInfo.get(3));
		PrescribeDetail prescribeDetail = new PrescribeDetail();
        prescribeDetail.Days = 5;
        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
		try {
			Data.checkWarning = true;
	        browser.wnOutpatientWorkflow.prescribeByMedicineId(browser.decouple.db60, medicineId, prescribeDetail);
		} catch (Throwable e) {
			if(e.toString().contains("开立医嘱失败")||e.toString().contains("复方皂矾丸，余量天数2天")) {
				browser.logger.boxLog(0, "医嘱规则生效", "复方皂矾丸，余量天数2天，提示出现");
			}else {
				browser.logger.assertFalse(true, "医嘱规则未生效!");
			}
		}
		browser.wnOutpatientWorkflow.commitFactory();
		browser.wnOutpatientWorkflow.afterFactory();
		int orderCount = browser.wnOutpatientWorkflow.getClinicalOrderCount();
		browser.logger.boxLog(2, "当前医嘱项的个数为："+orderCount, "西成药余量天数规则控制，此处应该有2个复方皂矾丸医嘱");
		browser.logger.assertFalse(orderCount<2,"复方皂矾丸未开立成功,报错");
    }
    
    @Before
    public void setUp() throws Exception {
        Data.checkWarning = false;
    }
}



