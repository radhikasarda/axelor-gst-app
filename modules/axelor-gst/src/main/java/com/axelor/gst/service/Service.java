package com.axelor.gst.service;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;

public interface Service {
	
	public InvoiceLine calculate(InvoiceLine invoiceline, Address invoiceAddress, Address companyAddress);
	public Invoice calculateInvoice(Invoice invoice);
	

}
