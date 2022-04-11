package com.winning.testsuite;

import org.junit.Test;


import com.winning.user.winex.OutpatientBrowser;
import com.winning.user.winex.OutpatientTestBase;

import ui.sdk.config.Data;
import ui.sdk.util.SdkSystemProcess;


public class LoginTest extends OutpatientTestBase {
	public LoginTest() {
		super();
	}

	static {
		Data.getScreenShot = true;
		Data.headless = false;
		Data.hybirdAppPath = "D:\\SDEY\\1102\\Win60.exe";
		Data.useHybirdApp = true;
	}

	String startHybirdAppCmd = Data.hybirdAppPath + " " + "--remote-debugging-port=" + Data.hybirdAppPort;

	@Test
	public void loginTest_001() throws InterruptedException {
		init("Case01 - 循环打开混合框架截图测试", true);
		browser.wnwd.sleep(5000);
		SdkSystemProcess.stopExeApp("Win60.exe");
		browser.wnwd.sleep(3000);
		for (int i = 1; i <= 100; i++) {
			String picName = "第" + i + "次截图";
			SdkSystemProcess.startExeApp(startHybirdAppCmd);
			browser = new OutpatientBrowser("截图" + i);
			browser.wnwd.getScreenShot(picName);
			browser.wnwd.sleep(3000);
			SdkSystemProcess.stopExeApp("Win60.exe");
			browser.wnwd.sleep(3000);
		}
	}

}
