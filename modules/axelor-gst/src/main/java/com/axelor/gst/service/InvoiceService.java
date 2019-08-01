package com.axelor.gst.service;

import com.axelor.axelor.gst.db.Invoice;

public interface InvoiceService {

	public Invoice calculateInvoice(Invoice invoice);

	public Invoice setInvoiceData(Invoice invoice);

}
