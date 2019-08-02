package com.axelor.gst.service;

import java.math.BigDecimal;
import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.InvoiceLine;

public class InvoiceLineServiceImpl implements InvoiceLineService {

	@Override
	public InvoiceLine calculateInvoiceLine(InvoiceLine invoiceLine, Address invoiceAddress, Address companyAddress) {

		BigDecimal netAmount = invoiceLine.getPrice().multiply(new BigDecimal(invoiceLine.getQty()));
		if (invoiceAddress == null || companyAddress == null || invoiceAddress.getState() == null
				|| companyAddress.getState() == null || companyAddress.getState().getName() == null) {
			invoiceLine.setIGST(null);
			invoiceLine.setSGST(null);
			invoiceLine.setCGST(null);
			invoiceLine.setNetAmount(netAmount);
			invoiceLine.setGrossAmount(netAmount);
			return invoiceLine;
		} 
		else {
			if (!invoiceAddress.getState().getName().equals(companyAddress.getState().getName())) {
				BigDecimal igst = (netAmount.multiply(invoiceLine.getGstRate())).divide(BigDecimal.valueOf(100));
				invoiceLine.setIGST(igst);
				invoiceLine.setGrossAmount(netAmount.add(igst));
				invoiceLine.setSGST(null);
				invoiceLine.setCGST(null);
			} else {
				BigDecimal sgst = (netAmount.multiply(invoiceLine.getGstRate())).divide(BigDecimal.valueOf(200));
				BigDecimal cgst = (netAmount.multiply(invoiceLine.getGstRate())).divide(BigDecimal.valueOf(200));
				invoiceLine.setSGST(sgst);
				invoiceLine.setCGST(cgst);
				invoiceLine.setGrossAmount(netAmount.add(sgst).add(cgst));
				invoiceLine.setIGST(null);
			}
			invoiceLine.setNetAmount(netAmount);
			return invoiceLine;
		}
	}

}
