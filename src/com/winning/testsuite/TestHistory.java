package com.winning.testsuite;

import com.winning.testsuite.workflow.WnDecouple;
import com.winning.testsuite.workflow.Outpatient.HistoryThread;
import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientBrowserManager;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.base.Browser;
import ui.sdk.base.BrowsersManager;
import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.SdkStat;
import ui.sdk.util.SdkSystemProcess;
import ui.sdk.util.SdkTools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHistory extends OutpatientTestBase {
	public static String reportTempFile = Framework.userDir +  Framework.fileSeparator + "xtest" + Framework.fileSeparator + "历史处置引用报告.html";
	public static String reportFile = Framework.userDir +  Framework.fileSeparator + "xtest" + Framework.fileSeparator + "history.temp";
	public static List<OutpatientBrowser> browsers = new ArrayList<OutpatientBrowser>();
	public static OutpatientBrowserManager brsManager = new OutpatientBrowserManager();
	
	
    @Test
    public void test_001() throws InterruptedException {
        // 开始测试前关闭所有浏览器和驱动
        SdkSystemProcess.stopExeApp("chrome.exe");
     	SdkSystemProcess.stopExeApp("chromedriver_chrome.exe");	
     	// 获取需要测试的服务
     	List<Map<String, String>> historyList = new WnDecouple(SdkTools.logger).getHistoryList(reportFile,Data.retryFlag,Data.notRetryFlag);
     	// 启动线程监控浏览器数量
	    brsManager.initBrowsers(Data.threadNum);
	    // 循环测试每个service
        for (int i = 0; i < historyList.size(); i++) {    
        	try {
        		// 获取下一个测试项目s
				Map<String, String> history = historyList.get(i);
				SdkTools.logger.log("("+i+"/"+historyList.size()+"): "+history);
				OutpatientBrowser browser = brsManager.getFreeBrowser("主线程:"+i+" :");
            	new HistoryThread(history,browser).start();
				SdkStat.StatBuild("正在执行第 " + i + "/" + historyList.size() + " 条医嘱测试，名称:" + history.get("NAME"));
            	if (i % 5 == 0) {
            		SdkTools.makeHistoryReport(reportFile,reportTempFile);
				}
            	try {
    				browser.wnwd.sleep(1000);
    			} catch (Throwable e) {
    				SdkTools.logger.log("主线程sleep报错:"+e.getMessage());
    			}
			} catch (Throwable e) {
				SdkTools.logger.log(i+":主线程报错:"+e.getMessage());
				i--;
				continue;
			}
        }
        
        while (HistoryThread.threadNum!=0) {
        	SdkTools.logger.log("threadNum:" + HistoryThread.threadNum);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
        Data.testOver = true; 
		SdkTools.makeHistoryReport(reportFile,reportTempFile);
		SdkTools.logger.log("主线程结束");
    }
}


