package com.axelor.gst.Module;

import com.axelor.app.AxelorModule;

import com.axelor.gst.service.InvoiceLineService;
import com.axelor.gst.service.InvoiceLineServiceImpl;


public class InvoiceLineModule extends AxelorModule{
	@Override
	protected void configure() {
	
		bind(InvoiceLineService.class).to(InvoiceLineServiceImpl.class);
		
	}

}
