package com.axelor.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Contact;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Sequence;

public class ServiceImpl implements Service {

	@Override
	public InvoiceLine calculate(InvoiceLine invoiceLine, Address invoiceAddress, Address companyAddress) {

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

	@Override
	public Invoice fetchInvoiceData(Invoice invoice) {

		for (Contact partyContactList : invoice.getParty().getContactList()) {
			if (partyContactList.getType().equals("primary")) {
				invoice.setPartyContact(partyContactList);
			}
		}

		for (Address address : invoice.getParty().getAddressList()) {

			if (address.getType().equals("default")) {
				invoice.setInvoiceAddress(address);
				invoice.setShippingAddress(address);
			} else if (address.getType().equals("invoice")) {
				invoice.setInvoiceAddress(address);

				if (invoice.getUseInvoiceAddress() == Boolean.TRUE) {
					invoice.setShippingAddress(invoice.getInvoiceAddress());
				}
			} else if (address.getType().equals("shipping")) {
				if (invoice.getUseInvoiceAddress() == Boolean.TRUE) {
					invoice.setShippingAddress(invoice.getInvoiceAddress());
				} else
					invoice.setShippingAddress(address);

			}
		}

		return invoice;

	}

	@Override
	public Sequence generateNextNumber(Sequence sequence) {
		String prefix = sequence.getPrefix();
		String suffix = sequence.getSuffix();
		int padding = sequence.getPadding();
		String nextNumber = prefix;
		if (sequence.getNextNumber() == null) {

			for (int i = 0; i < padding; i++) {
				nextNumber = nextNumber.concat("0");
			}
			if (suffix != null) {
				nextNumber = nextNumber.concat(suffix);
			}
			sequence.setNextNumber(nextNumber);
		}

		return sequence;
	}
}
