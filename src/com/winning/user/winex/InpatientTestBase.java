package com.winning.user.winex;

import java.util.HashMap;

import ui.sdk.base.TestBase;
import ui.sdk.util.SdkStat;

public class InpatientTestBase extends TestBase{
	
	public static InpatientBrowser browser = null;
	
	@Override
	public void init(String description, Boolean openBrowser) {
		SdkStat.StatBuild(description);
		currentCase = new HashMap<String, String>();
		currentCase.put("CASE_NAME", description);
		currentCase.put("START", "" + System.currentTimeMillis());
		if (openBrowser) {
			browser = new InpatientBrowser(description);
			super.browser = browser;
		} else if (browser != null) {
			browser.logger.resetPath(description + ".html");
		}
	}

}
