package com.axelor.gst.service;

import java.math.BigDecimal;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.InvoiceLine;

public class InvoiceLineServiceImpl implements InvoiceLineService {

	@Override
	public InvoiceLine calculateInvoiceLine(InvoiceLine invoiceLine, Address invoiceAddress, Address companyAddress) {

		BigDecimal netAmount = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal grossAmount = BigDecimal.ZERO;

		netAmount = invoiceLine.getPrice().multiply(new BigDecimal(invoiceLine.getQty()));
		if(invoiceAddress!=null && companyAddress!=null) {
			if (!invoiceAddress.getState().getName().equals(companyAddress.getState().getName())) {
				igst = netAmount.multiply(invoiceLine.getGstRate());
				invoiceLine.setIGST(igst);
				invoiceLine.setGrossAmount(netAmount.add(igst));
				invoiceLine.setSGST(BigDecimal.ZERO);

				invoiceLine.setCGST(BigDecimal.ZERO);
			} else {
				sgst = netAmount.multiply(invoiceLine.getGstRate()).divide(new BigDecimal(200));
				invoiceLine.setSGST(sgst);

				invoiceLine.setCGST(sgst);
				invoiceLine.setGrossAmount(netAmount.add(sgst).add(sgst));
				invoiceLine.setIGST(BigDecimal.ZERO);
			}
		}else {
			invoiceLine.setGrossAmount(netAmount);
		}

		invoiceLine.setNetAmount(netAmount);
		return invoiceLine;
	
	}

}
