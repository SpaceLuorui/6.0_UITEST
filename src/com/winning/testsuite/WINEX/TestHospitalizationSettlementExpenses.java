package com.winning.testsuite.WINEX;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.winning.user.winex.WINEXBrowser;
import com.winning.user.winex.WINEXTestBase;

import ui.sdk.config.Data;
import ui.sdk.constant.Framework;
import ui.sdk.util.Config;
import ui.sdk.util.SdkTools;

public class TestHospitalizationSettlementExpenses extends WINEXTestBase{
	Map<String, String> caseMap = null;
	

	public TestHospitalizationSettlementExpenses() {
		super();
	}
	
	public static String TableHead = "<table class='table table-bordered'>\r\n<tr id='TABLEID' class='header_row' onclick='searchChange(this.id)'><td width='120'>患者序号</td><td width='120'>患者姓名</td><td width='200'>开始时间</td><td width='150'>60显示收费</td><td width='150'>5.x显示自付</td><td width='100'>测试结果</td><td>错误信息<button style='float:right'>▼</button></td></tr>";

	static {
		Data.getScreenShot=true;
//		SdkTools.initReport("住院结算费用测试","住院结算费用测试报告.html");
		Framework.savereportFile = Framework.savereportFile.replace("report.html", "住院结算费用测试报告.html");
		Framework.savereportTempFile = Framework.savereportTempFile.replace("report.temp", "HospitalizationSettlementExpensesTemp.temp");
		Framework.reportHead = Framework.reportHead.replace("全医嘱开立", "5.x历史住院结算费用");
		SdkTools.writeToFile(Framework.savereportFile, Framework.reportHead, false);
		SdkTools.writeToFile(Framework.savereportFile, TableHead, true);
		Data.closeBrowser=false;
		Framework.autoReport=false;

	}
	
    static{
        try{
            Config.loadOnlineDefaultConfig("DEV");
            Config.loadOnlineExtraConfig("DEV","WINEX-35");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

	@Test
	public void test_001() throws UnsupportedEncodingException{
		browser = new WINEXBrowser("TEST");
		caseMap = new HashMap<String, String>();
		caseMap.put("Id", "");
		caseMap.put("TYPE", "");
		caseMap.put("TITLE", "");
		caseMap.put("CLASS", "");
		caseMap.put("PRICE_60", "");
		caseMap.put("PRICE_HIS", "");
		caseMap.put("RESULT", "");
		
		List<String> syxhBeenTested= new ArrayList<String>();
		List<Map<String, String>> patients=new ArrayList<Map<String, String>>();
		
		syxhBeenTested= SdkTools.readFileLineAsList(Framework.savereportTempFile);
		System.out.println("==="+syxhBeenTested);
		
		try {
			browser.wnwd.openUrl("http://"+Data.his_host+"/portal/#/login?W-APP=his");
			browser.wnWINEXWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
	    	patients = browser.decouple.getNotSettlementPatient();
		} catch (Throwable e) {
			caseMap.put("CLASS","TABLEID failCase");
    		caseMap.put("RESULT","不通过");
			
			if(e.getMessage().contains("未找到可以结算的患者")) {
				caseMap.put("ERRMSG", "5.x通过存储过程脚本未找到可以结算的患者，请检查sql语句");
			}else {
				caseMap.put("ERRMSG", e.getMessage());
			}
			SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
			return;
		}
		
    	String syxh = "";
    	String hzxm = "";
    	String yjjxh= "";
    	String price_60 = "0";
    	for(int i=0;i<patients.size();i++) {
    		caseMap = new HashMap<String, String>();
    		
    		
    		caseMap.put("ONCLICK", "window.open('capture/"+browser.logger.fileName+"')");
    		
    		
    		syxh = patients.get(i).get("syxh");
    		if(syxhBeenTested.contains(syxh)) {
    			continue;
    		}
    		hzxm = patients.get(i).get("hzxm");
    		caseMap.put("ID",syxh);
    		caseMap.put("TYPE",hzxm);
    		caseMap.put("TITLE",new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_SSS", Locale.getDefault()).format(new Date(Long.valueOf(System.currentTimeMillis()))));
    		caseMap.put("PRICE_60",price_60);
    		
    		try {
    			yjjxh = browser.decouple.getPatientAdvancePayment(syxh);
        		List<Map<String,Double>> cost = browser.decouple.preSettlement(syxh,yjjxh);
        		caseMap.put("PRICE_HIS",cost.get(0).get("qzzf").toString());
			} catch (Exception e) {
				caseMap.put("CLASS","TABLEID failCase");
	    		caseMap.put("RESULT","不通过");
				caseMap.put("ERRMSG", "获取5.x价格失败"+e.getMessage());
	    		SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
				continue;
			}
    		
    			
			try {
				price_60 = browser.wnWINEXWorkflow.inpatientCharges(patients.get(i).get("hzxm"));
    			caseMap.put("PRICE_60",price_60);
			} catch (Throwable e) {
				caseMap.put("CLASS","TABLEID failCase");
	    		caseMap.put("RESULT","不通过");
				caseMap.put("ERRMSG", "获取集团大his价格失败:"+e.getMessage());
	    		SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
				continue;
			}
    		
//    		try {
//    			browser.wnWINEXWorkflow.getChargeByWINEX(patients.get(i).get("hzxm"));
//    		    price_60 = browser.wnWINEXWorkflow.getChargeByWINEX();
//    			caseMap.put("PRICE_60",price_60);
//			} catch (Throwable e) {
//				caseMap.put("CLASS","TABLEID failCase");
//	    		caseMap.put("RESULT","不通过");
//				caseMap.put("ERRMSG", "获取his价格失败"+e.getMessage());
//	    		SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
//				continue;
//			}
    		try {
    			if (!SdkTools.compareMoney(caseMap.get("PRICE_60"), caseMap.get("PRICE_HIS"))) {
                    throw new Error("价格对比不通过");
                }
			} catch (Throwable e) {
				caseMap.put("CLASS","TABLEID failCase");
	    		caseMap.put("RESULT","不通过");
				caseMap.put("ERRMSG", e.getMessage());
				SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
				continue;
			}
//    		try {
//    			if (!SdkTools.compareMoney(caseMap.get("PRICE_60"), caseMap.get("PRICE_HIS"))) {
//                    throw new Error("价格对比不通过");
//                }
//			} catch (Throwable e) {
//				caseMap.put("CLASS","TABLEID failCase");
//	    		caseMap.put("RESULT","不通过");
//				caseMap.put("ERRMSG", e.getMessage());
//				SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
//				continue;
//			}
            
    		
    		caseMap.put("CLASS","TABLEID passCase");
    		caseMap.put("RESULT","通过");
    		SdkTools.writeToFile(Framework.savereportTempFile, syxh, true);
    		caseMap.put("ERRMSG", "");
    		SdkTools.saveServiceToReport(Framework.savereportFile,caseMap);
    	}
    	
	}

}
