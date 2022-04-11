package com.winning.user.winex;

import com.winning.testsuite.workflow.Wn60Db;
import com.winning.testsuite.workflow.WnDecouple;
import com.winning.testsuite.workflow.Inpatient.WnInpatientWorkflow;
import com.winning.testsuite.workflow.WINEX.WnWINEXWorkflow;

import ui.sdk.base.Browser;

import java.util.HashMap;
import java.util.Map;


public class InpatientBrowser extends Browser{

	public WnInpatientWorkflow wnInpatientWorkflow = null;
	public WnWINEXWorkflow wnWINEXWorkflow = null;
	public WnDecouple decouple = null;
	public Map<String, String> emptyBed =new HashMap<>();
	public String encounterId = null;

	public InpatientBrowser(String path) {
		super(path);
		this.wnInpatientWorkflow = new WnInpatientWorkflow(super.wnwd);
		this.wnWINEXWorkflow = new WnWINEXWorkflow(super.wnwd);
		this.decouple = new WnDecouple(super.logger);
		this.emptyBed=new HashMap<>();
	}


	@Override
	public void quit() {
		super.quit();
		this.decouple.disconnect();
	}

}
