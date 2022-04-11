package com.winning.testsuite.workflow.Outpatient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.winning.testsuite.TestAllService_new;
import com.winning.user.winex.OutpatientBrowser;

import ui.sdk.base.Browser;
import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

public class ServiceThread_new extends Thread {
    public static Integer threadNum = 0;
    public Map<String, String> service = null;
    public OutpatientBrowser br = null;

    public ServiceThread_new(Map<String, String> service, OutpatientBrowser br) {
        super();
        br.inUse = true;
        this.service = service;
        this.br = br;
    }

    @Override
    public void run() {
        threadNum++;
        try {
            testServiceInThread(service, br, 0);
        }
        catch (Throwable e) {
            e.printStackTrace();
            SdkTools.logger.log("" + e.getStackTrace());
            String finalMsg = br.name + "线程异常:" + service + "  msg:" + e.getMessage();
            SdkTools.logger.log(finalMsg);
            if (br != null && TestAllService_new.brsManager.browsers.contains(br)) {
            	TestAllService_new.brsManager.quitBrowser(br);
            }
        }
        finally {
            if (br != null) {
                br.inUse = false;
            }
            threadNum--;
        }
    }


    // 全医嘱开立
    public void testServiceInThread(Map<String, String> service, OutpatientBrowser br, Integer retryCount) {
//    	retryCount=10;
        
        service.put("PRICE_60", "");
        service.put("PRICE_HIS", "");
        service.put("RESULT", "");
        service.put("DETAIL", "");
        service.put("START", "" + System.currentTimeMillis());
        if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("中草药") || service.get("TYPE").equals("西成药")) {
            service.put("TITLE", service.get("NAME") + "[规格:" + service.get("PACKAGE") + "]");
        }
        else {
            service.put("TITLE", service.get("NAME"));
        }
        if (service.get("TYPE").equals("检查项目")) {
        	service.put("TYPE", "检查");
		}
        br.inUse = true;
        br.logger.resetPath(service.get("ID") + service.get("NAME") + ".html");
        service.put("FILENAME", br.logger.fileName);
        br.logger.log(1, "测试服务: " + service);
        br.logger.log(1, br.name
                + "\n开始时间" + new SimpleDateFormat("_yyyyMMdd_HH_mm_ss_SSS", Locale.getDefault()).format(new Date(Long.valueOf(service.get("START"))))
                + "\n重新登录:" + br.needLogin
                + "\n重新叫号:" + br.needReCall
                + "\n患者姓名:" + br.patientName
                + "\n重试次数:" + retryCount
                + "\nID:" + service.get("ID")
                + "\n类型:" + service.get("TYPE")
                + "\n名称:" + service.get("TITLE")
        );
        retryCount++;
        // 打开浏览器并登录
        if (br.needLogin) {
            try {
                br.logger.log(1, "开始登录");
                br.wnwd.openUrl(Data.web_url);
                br.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
                br.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
                br.wnOutpatientWorkflow.skip();
                br.needLogin = false;
            }
            catch (Throwable e) {
                e.printStackTrace();
                SdkTools.logger.log("登录失败:" + e.getStackTrace());
                service.put("ERRMSG", "登录失败:" + e.getMessage());
                br.logger.boxLog(2, "登录失败", service.get("ERRMSG"));
                br.wnwd.getScreenShot("登录失败");
                //重启浏览器重新测试这一条
                TestAllService_new.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, 0);
                return;
            }
        }

        if (br.needReCall) {
            // 挂号
            try {
                ArrayList<String> encounterInfo = br.decouple.newEncounter();
                br.patientName = encounterInfo.get(0);// 用于挂号检索
                br.visitNumber = encounterInfo.get(1);// 用于收费
                br.ghhx = encounterInfo.get(3);
            }
            catch (Throwable e) {
                e.printStackTrace();
                service.put("ERRMSG", "挂号失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                //两分钟后使用同一个浏览器重新测试本条
                br.wnwd.sleep(120000);
                testServiceInThread(service, br, 0);
                br.inUse = false;
                return;
            }
            // 叫号
            try {
                br.logger.log(1, br.name + " 开始叫号");
                br.wnOutpatientWorkflow.callNumberByNo(br.patientName, br.ghhx);
            }
            catch (Throwable e) {
                e.printStackTrace();
                service.put("ERRMSG", "叫号失败:" + e.getMessage());
                SdkTools.logger.log(2, service.get("ERRMSG"));
                br.logger.log(2, service.get("ERRMSG"));
                br.wnwd.getScreenShot("叫号失败");
                //重启浏览器重新测试这一条
                TestAllService_new.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, 0);
                return;
            }
        }

        // 搜索医嘱(如果失败,下一条不重新叫号)
        try {
        	String searchType = "default";
            String searchTypeDesc = "default";
            String expectItemName = "csId";
        	if (service.get("TYPE").equals("药品") || service.get("TYPE").equals("中草药") || service.get("TYPE").equals("西成药")){
        		expectItemName = "medicineId";
                List<String> expectTypeCodeList = new ArrayList<String>(Arrays.asList("98095", "98096", "98097"));
                //校验药品
                Map<String, String> medicine = br.decouple.db60.getMedicineByMedicineId(service.get("ID"));
                br.logger.assertFalse(!medicine.get("COMMODITY_ENABLE_FLAG").equals("98360"), "药品已停用");
                br.logger.assertFalse(medicine.get("CS_ID") == null, "该药品没有CS_ID");
                //校验药品通用名
                Map<String, String> cs = br.decouple.db60.getServiceByCsId(medicine.get("CS_ID"));
                br. logger.assertFalse(!cs.get("CS_STATUS").equals("98360"), "服务已停用");
                br. logger.assertFalse(!expectTypeCodeList.contains(cs.get("CS_TYPE_CODE")), "类型不是西药或中成药 cs_id:" + medicine.get("CS_ID"));
                if (cs.get("CS_TYPE_CODE").equals("98097")) {
                    //中草药
                    searchType = "9";
                    searchTypeDesc = "草药";
                    service.put("TYPE", "中草药");
                }
                else {
                    //西成药
                    searchType = "5";
                    searchTypeDesc = "西成药";
                    service.put("TYPE", "西成药");
                }
        	}else if (service.get("TYPE").equals("检验")) {
        		searchType = "3";
                searchTypeDesc = "检验";
                //校验临床服务数据
                Map<String, String> cs = br.decouple.db60.getServiceByCsId(service.get("ID"));
                br.logger.assertFalse(!cs.get("CS_STATUS").equals("98360"), "服务已停用");
                br.logger.assertFalse(cs.get("CS_TYPE_CODE")==null, "没有服务类型");
                if (!cs.get("CS_TYPE_CODE").equals(Data.labCode)) {
                	String parentTypeCode = br.decouple.db60.getValueSetByValueId(cs.get("CS_TYPE_CODE")).get("PARENT_VALUE_ID");
                	br.logger.assertFalse(parentTypeCode==null || !parentTypeCode.equals(Data.labCode), "该服务类型不是检验:"+service.get("ID"));
        		}
                String errMsg = br.wnOutpatientWorkflow.checkLabAvailable(service.get("ID"));
                br.logger.assertFalse(!(errMsg==null), errMsg);
			}else if (service.get("TYPE").equals("治疗")) {
				searchType = "2";
		        searchTypeDesc = "治疗";
                //校验临床服务数据
                Map<String, String> cs = br.decouple.db60.getServiceByCsId(service.get("ID"));
                br.logger.assertFalse(!cs.get("CS_STATUS").equals("98360"), "服务已停用");
                br.logger.assertFalse(cs.get("CS_TYPE_CODE")==null, "没有服务类型");
                if (!cs.get("CS_TYPE_CODE").equals(Data.treatCode)) {
                	String parentTypeCode = br.decouple.db60.getValueSetByValueId(cs.get("CS_TYPE_CODE")).get("PARENT_VALUE_ID");
                	br.logger.assertFalse(parentTypeCode==null || !parentTypeCode.equals(Data.treatCode), "该服务类型不是治疗:"+service.get("ID"));
        		}
                String errMsg = br.wnOutpatientWorkflow.checkTreatAvailable(service.get("ID"));
                br.logger.assertFalse(!(errMsg==null), errMsg);
			}else if (service.get("TYPE").equals("病理")) {
				searchType = "11";
		        searchTypeDesc = "病理";
                //校验临床服务数据
                Map<String, String> cs = br.decouple.db60.getServiceByCsId(service.get("ID"));
                br.logger.assertFalse(!cs.get("CS_STATUS").equals("98360"), "服务已停用");
                br.logger.assertFalse(cs.get("CS_TYPE_CODE")==null, "没有服务类型");
                if (!cs.get("CS_TYPE_CODE").equals(Data.pathologyCode)) {
                	String parentTypeCode = br.decouple.db60.getValueSetByValueId(cs.get("CS_TYPE_CODE")).get("PARENT_VALUE_ID");
                	br.logger.assertFalse(parentTypeCode==null || !parentTypeCode.equals(Data.pathologyCode), "该服务类型不是病理:"+service.get("ID"));
        		}
                String errMsg = br.wnOutpatientWorkflow.checkPathologyAvailable(service.get("ID"));
                br.logger.assertFalse(!(errMsg==null), errMsg);
			}else if (service.get("TYPE").equals("检查")) {
				searchType = "4";
		        searchTypeDesc = "检查";
		        expectItemName = "examItemId";
		        //校验检查项目
		        Map<String, String> examItem = br.decouple.db60.getExamItemByExamItemId(service.get("ID"));
		        String CS_ID = examItem.get("CS_ID");
		        //校验临床服务数据
		        Map<String, String> cs = br.decouple.db60.getServiceByCsId(CS_ID);
		        br.logger.assertFalse(!cs.get("CS_STATUS").equals("98360"), "服务已停用");
		        br.logger.assertFalse(cs.get("CS_TYPE_CODE")==null, "没有服务类型");
		        if (!cs.get("CS_TYPE_CODE").equals(Data.examCode)) {
		        	String parentTypeCode = br.decouple.db60.getValueSetByValueId(cs.get("CS_TYPE_CODE")).get("PARENT_VALUE_ID");
		        	br.logger.assertFalse(parentTypeCode==null || !parentTypeCode.equals(Data.examCode), "该服务类型不是病理:"+CS_ID);
				}
		        String errMsg = br.wnOutpatientWorkflow.checkExamAvailable(CS_ID);
		        br.logger.assertFalse(!(errMsg==null), errMsg);
			}else {
				throw new Error("服务类型错误:"+service);
			}
        	//获取搜索结果序号
        	int seq = br.wnOutpatientWorkflow.getMedicineSeqByApi(service.get("NAME"), searchType, expectItemName,service.get("ID"));
        	//搜索并选择
        	br.wnOutpatientWorkflow.searchOrderBySeq(searchTypeDesc, service.get("NAME"), seq);
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = false;
            service.put("ERRMSG", "搜索医嘱失败:" + e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("搜索医嘱失败");
            if (service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
            	TestAllService_new.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, retryCount);
            }
            else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.inUse = false;
            }
            return;
        }

        // 获取加工厂类型-编辑医嘱开立-签署-收费-对比价格(如果失败,下一条重新叫号)
        try {
            br.wnOutpatientWorkflow.beforeFactory();
            if (service.get("TYPE").equals("西成药")) {
				br.wnOutpatientWorkflow.editDrugFactory(null);
			}else if (service.get("TYPE").equals("中草药")) {
				br.wnOutpatientWorkflow.editHerbFactory(null);
			}else if (service.get("TYPE").equals("检验")) {
				br.wnOutpatientWorkflow.editLabFactory(null);
			}else if (service.get("TYPE").equals("病理")) {
				br.wnOutpatientWorkflow.editPathologyFactory(null);
			}else if (service.get("TYPE").equals("治疗")) {
				br.wnOutpatientWorkflow.editTreatFactory(null);
			}else if (service.get("TYPE").equals("检查")) {
				br.wnOutpatientWorkflow.editExamFactory(null);
			}else {
				throw new Error("服务类型错误:"+service);
			}
            br.wnOutpatientWorkflow.commitFactory();
            br.wnOutpatientWorkflow.afterFactory();
            br.wnOutpatientWorkflow.signOff(0);

            if (Data.getEncounterFromFile) {
                //TODO 暂时未实现收费
                service.put("PRICE_60", br.wnOutpatientWorkflow.getTotalCost());
                service.put("PRICE_HIS", "0");
            }
            else {
                service.put("PRICE_60", br.wnOutpatientWorkflow.getTotalCost());
                service.put("PRICE_HIS", br.decouple.win60MedicineSF(br.patientName).get(0));
                // 价格对比不通过
                if (!SdkTools.compareMoney(service.get("PRICE_60"), service.get("PRICE_HIS"))) {
                    throw new Error("价格对比不通过");
                } 
            }
            // 撤销医嘱
            if (Data.hisType.equals("JTWN")) {
            	// his退费
                br.decouple.hisRefundByGhxh(br.visitNumber);
                br.wnOutpatientWorkflow.revokeOrders();
                br.wnOutpatientWorkflow.deleteOrders();
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            br.needReCall = true;
            service.put("ERRMSG", e.getMessage());
            SdkTools.logger.log(2, service.get("ERRMSG"));
            br.logger.log(2, service.get("ERRMSG"));
            br.wnwd.getScreenShot("开立签署或对比价格失败");
            if (service.get("ERRMSG").contains("等待诊断选择框不可见") || service.get("ERRMSG").contains("element is not attached") || service.get("ERRMSG").contains("WebDriver") || service.get("ERRMSG").contains("invalid session")) {
            	TestAllService_new.brsManager.quitBrowser(br);
                OutpatientBrowser newbrowser = TestAllService_new.brsManager.getFreeBrowser(service.get("NAME"));
                testServiceInThread(service, newbrowser, retryCount);
            }
            else if ((service.get("ERRMSG").contains("His收费失败") && !(service.get("ERRMSG").contains("未落库"))) || service.get("ERRMSG").contains("His退费失败")) {
                if (retryCount < 5) {
                    //重新叫号重新测试本条
                    testServiceInThread(service, br, retryCount);
                }
                else {
                    //写入测试报告
                    service.put("RESULT", "不通过");
                    service.put("CLASS", "TABLEID failCase");
                    SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                    br.inUse = false;
                }
            }
            else {
                //写入测试报告
                service.put("RESULT", "不通过");
                service.put("CLASS", "TABLEID failCase");
                SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
                br.inUse = false;
            }
            br.inUse = false;
            return;
        }

        // 测试通过
        SdkTools.logger.log(1, "测试通过!");
        br.logger.log(1, "测试通过!");
        br.wnwd.getScreenShot("测试通过");
        service.put("RESULT", "通过");
        service.put("CLASS", "TABLEID passCase");
        service.put("ERRMSG", "");
        SdkTools.saveServiceToTempFile(TestAllService_new.reportTempFile, service);
        br.needReCall = true;
        br.inUse = false;
    }
}