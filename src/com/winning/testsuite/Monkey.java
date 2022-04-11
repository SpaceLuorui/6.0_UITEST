package com.winning.testsuite;

import java.util.ArrayList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.winning.testsuite.workflow.Outpatient.WnOutpatientXpath;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Monkey extends OutpatientTestBase {
	public Monkey() {
		super();
	}
		
	@Test
	public void test_01_debug() throws InterruptedException {
		init("UI_Mokey_Test", true);
		//MOKEY测试前置流程
		try {
			ArrayList<String> encounterInfo = browser.decouple.newEncounter();
			browser.wnwd.openUrl(Data.web_url);
			browser.wnOutpatientWorkflow.wnlogin(Data.default_user_login_account, Data.default_user_login_pwd);
			browser.wnOutpatientWorkflow.loginOutPatientNew(Data.test_select_subject);
			browser.wnOutpatientWorkflow.skip();
			browser.wnOutpatientWorkflow.callNumberByName(encounterInfo.get(0));
		} catch (Exception e) {
			browser.wnwd.assertTrue("前置流程失败,Monkey测试无法进行: "+e.getMessage(), false);
		}catch (Throwable e) {
			browser.wnwd.assertTrue("前置流程失败,Monkey测试无法进行: "+e.getMessage(), false);
		}
		//随机遍历推荐区域,100秒
		browser.wnwd.mokeyTest(WnOutpatientXpath.recommendRootPath,Data.recommendTestSeconds);
		//随机遍历处置区域,100秒
		browser.wnwd.mokeyTest(WnOutpatientXpath.disposalRootPath,Data.disposalTestSeconds);
		//随机遍历病例区域,100秒
		browser.wnwd.mokeyTest(WnOutpatientXpath.emrRootPath,Data.emrTestSeconds);
		browser.logger.log("\nsuc:"+ browser.wnwd.sucCount+"\n元素不可点击:"+browser.wnwd.errCount1+"\n元素已消失:"+browser.wnwd.errCount2+"\n其它异常:"+browser.wnwd.errCount3+"\n抓取报错信息:"+Data.errMsgList);
		
		//检查报错信息列表长度为0
		browser.wnwd.assertTrue("随机操作过程中检查到报错信息:"+Data.errMsgList, Data.errMsgList.size()==0);
	}
}