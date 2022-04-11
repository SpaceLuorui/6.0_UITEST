package com.winning.user.winex;

import java.util.HashMap;

import ui.sdk.base.TestBase;
import ui.sdk.util.SdkStat;

public class WINEXTestBase extends TestBase{
public static WINEXBrowser browser = null;
	
	@Override
	public void init(String description, Boolean openBrowser) {
		SdkStat.StatBuild(description);
		currentCase = new HashMap<String, String>();
		currentCase.put("CASE_NAME", description);
		currentCase.put("START", "" + System.currentTimeMillis());
		if (openBrowser) {
			browser = new WINEXBrowser(description);
			super.browser = browser;
			browser.wnwd.alertError = "//div[contains(@class,'el-message--error')]//p[@class='el-message__content']";
		} else if (browser != null) {
			browser.logger.resetPath(description + ".html");
		}
	}
}
