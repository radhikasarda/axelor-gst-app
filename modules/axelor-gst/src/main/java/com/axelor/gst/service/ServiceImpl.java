package com.axelor.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;

public class ServiceImpl implements Service {

	@Override
	public InvoiceLine calculate(InvoiceLine invoiceLine, Address invoiceAddress, Address companyAddress) {

		BigDecimal netAmount = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal grossAmount = BigDecimal.ZERO;

		netAmount = invoiceLine.getPrice().multiply(new BigDecimal(invoiceLine.getQty()));
		if (!invoiceAddress.getState().getName().equals(companyAddress.getState().getName())) {
			igst = netAmount.multiply(invoiceLine.getGstRate());
			invoiceLine.setIGST(igst);
		} else {
			sgst = netAmount.multiply(invoiceLine.getGstRate()).divide(new BigDecimal(200));
			invoiceLine.setSGST(sgst);
			invoiceLine.setCGST(sgst);
		}

		grossAmount = netAmount.add(igst).add(cgst).add(sgst);
		invoiceLine.setGrossAmount(grossAmount);
		invoiceLine.setNetAmount(netAmount);
		return invoiceLine;
	}

	@Override
	public Invoice calculateInvoice(Invoice invoice) {
		BigDecimal invoiceNetAmount = BigDecimal.ZERO;
		BigDecimal invoiceigst = BigDecimal.ZERO;
		BigDecimal invoicesgst = BigDecimal.ZERO;
		BigDecimal invoicecgst = BigDecimal.ZERO;
		BigDecimal invoicegrossAmount = BigDecimal.ZERO;

		for (InvoiceLine invoiceItemsList : invoice.getInvoiceItemsList()) {

			invoiceNetAmount = invoiceNetAmount.add(invoiceItemsList.getNetAmount());
			invoiceigst = invoiceigst.add(invoiceItemsList.getIGST());
			invoicecgst = invoicecgst.add(invoiceItemsList.getCGST());
			invoicesgst = invoicesgst.add(invoiceItemsList.getSGST());
			invoicegrossAmount = invoicegrossAmount.add(invoiceItemsList.getGrossAmount());

		}
		invoice.setNetIGST(invoiceigst);
		invoice.setNetSGST(invoicesgst);
		invoice.setNetCGST(invoicecgst);
		invoice.setNetAmount(invoiceNetAmount);
		invoice.setGrossAmount(invoicegrossAmount);
		return invoice;

	}

}
