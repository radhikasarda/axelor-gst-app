package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.gst.service.Service;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class Controller {

	
	@Inject Service service;
	
	public void calculate(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceline = request.getContext().asType(InvoiceLine.class);
		Invoice invoice=request.getContext().getParent().asType(Invoice.class);
		
		Address invoiceAddress=invoice.getInvoiceAddress();
		Address companyAddress=invoice.getCompany().getAddress();
		invoiceline = service.calculate(invoiceline,invoiceAddress, companyAddress);
		response.setValue("netAmount", invoiceline.getNetAmount());
		response.setValue("IGST", invoiceline.getIGST());
		response.setValue("SGST", invoiceline.getSGST());
		response.setValue("CGST", invoiceline.getCGST());
		response.setValue("grossAmount",invoiceline.getGrossAmount());
	}
	
	public void calculateInvoice(ActionRequest request, ActionResponse response) {
		
		Invoice invoice=request.getContext().asType(Invoice.class);
		invoice=service.calculateInvoice(invoice);
		response.setValue("netAmount", invoice.getNetAmount());
		response.setValue("netIGST", invoice.getNetIGST());
		response.setValue("netSGST", invoice.getNetSGST());
		response.setValue("netCGST", invoice.getNetCGST());
		response.setValue("grossAmount",invoice.getGrossAmount());	
	
	}

	public void fetchInvoiceData(ActionRequest request, ActionResponse response) {
		
		Invoice invoice=request.getContext().asType(Invoice.class);
		invoice=service.fetchInvoiceData(invoice);
		response.setValue("partyContact", invoice.getPartyContact());
		response.setValue("invoiceAddress",invoice.getInvoiceAddress());
		response.setValue("shippingAddress", invoice.getShippingAddress());
	}
}
