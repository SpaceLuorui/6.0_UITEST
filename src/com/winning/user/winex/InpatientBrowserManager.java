package com.winning.user.winex;

import ui.sdk.base.Browser;
import ui.sdk.base.BrowsersManager;
import ui.sdk.config.Data;
import ui.sdk.util.SdkTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InpatientBrowserManager extends BrowsersManager{

	public List<InpatientBrowser> browsers = new ArrayList<InpatientBrowser>();

	public InpatientBrowserManager() {
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
							InpatientBrowser br = new InpatientBrowser("detail_" + System.currentTimeMillis() + ".html");
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
	public InpatientBrowser getFreeBrowser(String desc) {
		if (!initBrowser) {
			throw new Error("please call initBrowsers first");
		}
		while (true) {
			try {
				SdkTools.logger.log(desc + "---" + browsers.size());
				for (InpatientBrowser br : browsers) {
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

	// 浏览器获取一个空床编号
//	public void getEmptyBedNo(InpatientBrowser br,List<String>EmptyBedNoList) {
//		if(EmptyBedNoList.size()>0){
//			br.emptyBedNo=EmptyBedNoList.get(0);
//			SdkTools.logger.log(1,"浏览器获取可用空床编号："+br.emptyBedNo);
//			EmptyBedNoList.remove(br.emptyBedNo);
//			System.out.println("EmptyBedNoList.size()："+EmptyBedNoList.size());
//		} else {
//				SdkTools.logger.log(2, "不存在可用的空床！ ");
//		}
//	}
	public void getEmptyBed(InpatientBrowser br,List<Map<String, String>>EmptyBedList) {
		if(EmptyBedList.size()>0){
			br.emptyBed=EmptyBedList.get(0);
			SdkTools.logger.log(1,"浏览器获取可用空床，编号："+br.emptyBed.get("BED_NO"));
			EmptyBedList.remove(br.emptyBed);
			System.out.println("EmptyBedNoList.size()："+EmptyBedList.size());
		} else {
			SdkTools.logger.log(2, "不存在可用的空床！ ");
		}
	}

	public void quitBrowser(Browser br) {
		this.browsers.remove(br);
		br.quit();
		br = null;
		SdkTools.logger.log("Browser_size_after_delete :" + this.browsers.size());
	}

}
