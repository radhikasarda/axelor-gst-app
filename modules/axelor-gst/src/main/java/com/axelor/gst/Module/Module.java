package com.axelor.gst.Module;



import com.axelor.app.AxelorModule;
import com.axelor.gst.service.Service;
import com.axelor.gst.service.ServiceImpl;


public class Module extends AxelorModule{

	@Override
	protected void configure() {
	
		bind(Service.class).to(ServiceImpl.class);
		
	}

}