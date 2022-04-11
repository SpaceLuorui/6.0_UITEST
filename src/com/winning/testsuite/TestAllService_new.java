package com.winning.testsuite;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.WnDecouple;
import com.winning.testsuite.workflow.Outpatient.ServiceThread_new;
import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientBrowserManager;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.base.Browser;
import ui.sdk.base.BrowsersManager;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

/**
 * 专项测试，西药全量测试
 * 
 * @author Administrator
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestAllService_new extends OutpatientTestBase {
	public static String reportTempFile = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "全医嘱报告.temp";
	public static String reportFile = Framework.userDir + Framework.fileSeparator + "xtest" + Framework.fileSeparator + "全医嘱报告.html";
	public static List<String> sucIdmList = null;
	public static Integer last_test_num =0;	
	public static OutpatientBrowserManager brsManager = new OutpatientBrowserManager();
	
	static {
//		Config.load("config");
//		Data.testMedicineFlag=false;
//		Data.testExamItemFlag=true;
//		Data.testLabFlag=false;
//		Data.testPathologyFlag=false;
//		Data.testTreatFlag=false;
//		Data.retryFlag="Case";
//		Data.headless=false;
//		Data.threadNum=10;
//		Data.getScreenShot=true;
//		Data.ignoreErrors="";
//		Data.allServiceTestMaxNo=10;
		Data.specialErrorType="库存&&该药品没有CS_ID&&该服务类型不是&&服务医嘱标识未勾选&&服务已停用&&服务就诊类型中不包含门诊&&界面报错&&确认开立失败&&签署医嘱失败&&开立医嘱失败&&服务医嘱标识未勾选&&搜索到结果但无法正常开立&&搜索无结果&&计费策略&&加工厂默认值错误&&签署后2分钟未落库&&His收费失败&&His退费失败&&请求医嘱列表失败&&搜索医嘱失败";
		Framework.autoReport=false;
	}
	
	@Test
	public void test_01() throws InterruptedException {
		// 开始测试前关闭所有浏览器和驱动
		SdkSystemProcess.stopExeApp("chrome.exe");
	    SdkSystemProcess.stopExeApp("chromedriver_chrome.exe");	
		// 获取需要测试的服务
	    Wn60Db windb60 = new Wn60Db(SdkTools.logger);
	    WnDecouple windbhis = new WnDecouple(SdkTools.logger);
	    
	    Map<String, String> param_CL059 = null;
	    try {
	    	param_CL059 = windb60.getParam("CL059");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	    if (param_CL059!=null && param_CL059.containsKey("PARAM_VALUE")&&!param_CL059.get("PARAM_VALUE").equals("1")) {
			SdkTools.logger.log("请先将CL059参数设置成1");
			throw new Error("请先将CL059参数设置成1");
		}
	    
	    List<Map<String, String>> allServiceList =null;
		
		if(Data.hisType=="TJWN") {
			allServiceList = windbhis.getServiceList(reportTempFile,Data.retryFlag,Data.notRetryFlag);
			windbhis.disconnect();
		}else {
		    allServiceList = windb60.getServiceList(reportTempFile,Data.retryFlag,Data.notRetryFlag);
			windb60.disconnect();
		}
		// 启动线程监控浏览器数量
	    brsManager.initBrowsers(Data.threadNum);
		//循环测试取到的service
		for (int i = last_test_num; i < allServiceList.size(); i++) {
			// 记录当前测试进度,retry时有用
			last_test_num = i;
			try {
				// 获取下一个测试项目
				Map<String, String> service = allServiceList.get(i);
				SdkTools.logger.log("("+last_test_num+"/"+allServiceList.size()+"): "+service);
				OutpatientBrowser browser = brsManager.getFreeBrowser("主线程:"+service.get("NAME")+" :");
				new ServiceThread_new(service,browser).start();
				if (i % 5 == 0) {
					SdkTools.makeReport(reportTempFile,reportFile);
				}
				SdkTools.sleep(1000);
			} catch (Throwable e) {
				SdkTools.logger.log(i+"主线程报错："+e.getMessage());
				i--;
				continue;
			}
		}
		while (ServiceThread_new.threadNum!=0) {
			SdkTools.logger.log("threadNum:" + ServiceThread_new.threadNum);
			SdkTools.sleep(3000);
		}
		Data.testOver = true; 
		SdkTools.logger.log("整理开始"+System.currentTimeMillis());
		SdkTools.makeReport(reportTempFile,reportFile);
		SdkTools.logger.log("整理完成"+System.currentTimeMillis());
		SdkTools.logger.log("主线程结束");
	}
}