package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceLineController {

	@Inject
	InvoiceLineService invoiceLineService;

	public void calculateInvoiceLine(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceline = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = request.getContext().getParent().asType(Invoice.class);
		Address companyAddress = null;
		Address invoiceAddress = invoice.getInvoiceAddress();
		if (invoice.getCompany() == null) {
			companyAddress = null;
		} else {
			companyAddress = invoice.getCompany().getAddress();
		}
		invoiceline = invoiceLineService.calculateInvoiceLine(invoiceline, invoiceAddress, companyAddress);
		response.setValues(invoiceline);
	}
}
