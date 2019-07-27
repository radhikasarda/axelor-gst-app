package com.axelor.gst.Module;

import com.axelor.app.AxelorModule;
import com.axelor.gst.service.SequenceService;
import com.axelor.gst.service.SequenceServiceImpl;


public class SequenceModule extends AxelorModule{

	@Override
	protected void configure() {
		bind(SequenceService.class).to(SequenceServiceImpl.class);
	}

}
