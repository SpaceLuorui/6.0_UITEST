package com.winning.user.winex;

import com.winning.testsuite.workflow.WnDecouple;
import com.winning.testsuite.workflow.Outpatient.WnOutpatientWorkflow;

import ui.sdk.base.Browser;

public class OutpatientBrowser extends Browser{
	
	public WnOutpatientWorkflow wnOutpatientWorkflow = null;
	public WnDecouple decouple = null;

	public OutpatientBrowser(String path) {
		super(path);
		this.wnOutpatientWorkflow = new WnOutpatientWorkflow(super.wnwd);
		this.decouple = new WnDecouple(super.logger);
	}
	
	@Override
	public void quit() {
		super.quit();
		this.decouple.disconnect();
	}

}
