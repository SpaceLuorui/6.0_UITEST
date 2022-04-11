package com.winning.user.winex;

import java.util.ArrayList;
import java.util.List;

import ui.sdk.base.Browser;
import ui.sdk.base.BrowsersManager;
import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

public class OutpatientBrowserManager extends BrowsersManager{
	
	public List<OutpatientBrowser> browsers = new ArrayList<OutpatientBrowser>();

	public OutpatientBrowserManager() {
		super();
	}

	public void initBrowsers(Integer browserNum) {
		initBrowser = true;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Data.testOver) {
					SdkTools.logger.log("--------------Browser_size: " + browsers.size());
					try {
						if (browsers.size() < browserNum) {
							OutpatientBrowser br = new OutpatientBrowser("detail_" + System.currentTimeMillis() + ".html");
							browsers.add(br);
						}
					} catch (Throwable e) {
						SdkTools.logger.log(e.getMessage());
					}
					SdkTools.sleep(2000);
				}
				SdkTools.logger.log("" + Data.testOver + "测试结束!!!");
			}
		});
		t.start();
	}

	// 获取一个空闲浏览器
	public OutpatientBrowser getFreeBrowser(String desc) {
		if (!initBrowser) {
			throw new Error("please call initBrowsers first");
		}
		while (true) {
			try {
				SdkTools.logger.log(desc + "---" + browsers.size());
				for (OutpatientBrowser br : browsers) {
					if (!br.inUse) {
						SdkTools.logger.log(1, "获取到浏览器: " + br.name);
						br.inUse = true;
						return br;
					}
				}
				SdkTools.sleep(3000);
			} catch (Throwable e) {
				SdkTools.logger.log("获取浏览器异常:" + e.getMessage());
			}
		}
	}

	public void quitBrowser(Browser br) {
		this.browsers.remove(br);
		br.quit();
		br = null;
		SdkTools.logger.log("Browser_size_after_delete :" + this.browsers.size());
	}
}
