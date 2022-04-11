package com.winning.user.winex;

import com.winning.testsuite.workflow.WnDecouple;
import com.winning.testsuite.workflow.Inpatient.WnInpatientWorkflow;
import com.winning.testsuite.workflow.Outpatient.WnOutpatientWorkflow;
import com.winning.testsuite.workflow.WINEX.WnWINEXWorkflow;

import ui.sdk.base.Browser;

public class WINEXBrowser extends Browser{
	public WnWINEXWorkflow wnWINEXWorkflow = null;
	public WnOutpatientWorkflow wnOutpatientWorkflow = null;
	public WnInpatientWorkflow wnInpatientWorkflow = null;
	public WnDecouple decouple = null;

	public WINEXBrowser(String path) {
		super(path);
		this.wnWINEXWorkflow = new WnWINEXWorkflow(super.wnwd);
		this.wnOutpatientWorkflow = new WnOutpatientWorkflow(super.wnwd);
		this.wnInpatientWorkflow = new WnInpatientWorkflow(super.wnwd);
		this.decouple = new WnDecouple(super.logger);
	}

	@Override
	public void quit() {
		super.quit();
		this.decouple.disconnect();
	}

}
