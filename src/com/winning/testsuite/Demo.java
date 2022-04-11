package com.winning.testsuite;

import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.constant.Framework;
import ui.sdk.util.SdkTools;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Demo extends OutpatientTestBase {
	public static Map<String, String> caseMap = new HashMap<String, String>();
	
	static {
		//控制Case结束后是否自动插入报告
		Framework.autoReport=false;
		//初始化报告
		SdkTools.initReport("报告示例","TEST.html");
	}

	@Test
	public void test_flow_001() throws InterruptedException {
		browser = new OutpatientBrowser("TEST");
		
		
		// CASE01内容
		browser.wnOutpatientWorkflow.wnwd.logger.log(1, "1111111111111111");
		// CASE01结果写入报告
		caseMap.put("CASE_NAME","Case01-XXX");
		caseMap.put("START", "开始时间");
		caseMap.put("END", "结束时间");
		caseMap.put("RESULT","通过");
		caseMap.put("CLASS","TABLEID passCase");
		caseMap.put("ERRMSG", "");
		caseMap.put("ONCLICK", "window.open('capture/"+browser.logger.fileName+"')");
    	SdkTools.saveCaseToReport(Framework.savereportFile,caseMap);
    	
    	
    	
    	// CASE02初始化
    	browser.logger.resetPath("Case02-YYY.html");
		// CASE02内容
		browser.wnOutpatientWorkflow.wnwd.logger.log(1, "22222222222222222");
		// CASE02结果写入报告
		caseMap.put("CASE_NAME","Case02-YYY.html");
		caseMap.put("START", "开始时间");
		caseMap.put("END", "结束时间");
    	caseMap.put("RESULT","不通过");
    	caseMap.put("CLASS","TABLEID failCase");
        caseMap.put("ERRMSG", "报错了");
        caseMap.put("ONCLICK", "window.open('capture/"+browser.logger.fileName+"')");
    	SdkTools.saveCaseToReport(Framework.savereportFile,caseMap);
		
    	
    	
    	// CASE03初始化
    	browser.logger.resetPath("Case03-ZZZ.html");
		// CASE03内容
		browser.wnOutpatientWorkflow.wnwd.logger.log(1, "333333333333333333");
		// CASE03结果写入报告
		caseMap.put("CASE_NAME","Case03-ZZZ");
		caseMap.put("START", "开始时间");
		caseMap.put("END", "结束时间");
		caseMap.put("RESULT","通过");
		caseMap.put("CLASS","TABLEID passCase");
		caseMap.put("ERRMSG", "通过了");
		caseMap.put("ONCLICK", "window.open('capture/"+browser.logger.fileName+"')");
		SdkTools.saveCaseToReport(Framework.savereportFile,caseMap);
	}
	
		
}