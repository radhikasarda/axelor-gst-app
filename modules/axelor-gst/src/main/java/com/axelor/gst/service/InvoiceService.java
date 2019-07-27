package com.axelor.gst.service;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Sequence;

public interface InvoiceService {
	
	
	public Invoice calculateInvoice(Invoice invoice);
	public Invoice fetchInvoiceData(Invoice invoice);
	

}
