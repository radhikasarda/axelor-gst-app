package com.axelor.gst.Module;

import com.axelor.app.AxelorModule;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.InvoiceServiceImpl;

public class InvoiceModule extends AxelorModule {

	@Override
	protected void configure() {

		bind(InvoiceService.class).to(InvoiceServiceImpl.class);

	}

}