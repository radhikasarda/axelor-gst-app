package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceLineController {
	
	@Inject InvoiceLineService invoiceLineService;

	public void calculateInvoiceLine(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceline = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = request.getContext().getParent().asType(Invoice.class);

		Address invoiceAddress = invoice.getInvoiceAddress();
		if(invoice.getCompany()!=null) {
		Address companyAddress = invoice.getCompany().getAddress();
		invoiceline = invoiceLineService.calculateInvoiceLine(invoiceline, invoiceAddress, companyAddress);
		response.setValue("netAmount", invoiceline.getNetAmount());
		response.setValue("IGST", invoiceline.getIGST());
		response.setValue("SGST", invoiceline.getSGST());
		response.setValue("CGST", invoiceline.getCGST());
		response.setValue("grossAmount", invoiceline.getGrossAmount());
		}else {response.setError("No company selected");}
	}
}
