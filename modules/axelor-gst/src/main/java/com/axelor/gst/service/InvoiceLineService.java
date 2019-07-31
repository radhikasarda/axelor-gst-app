package com.axelor.gst.service;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;

public interface InvoiceLineService {
	public InvoiceLine calculateInvoiceLine(InvoiceLine invoiceLine, Address invoiceAddress, Address companyAddress);
}
