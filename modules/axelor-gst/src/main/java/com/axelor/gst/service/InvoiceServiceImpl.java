package com.axelor.gst.service;

import java.math.BigDecimal;
import java.util.List;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Contact;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Sequence;

public class InvoiceServiceImpl implements InvoiceService {


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
		
		if(invoice.getParty()==null) {
			invoice.setInvoiceAddress(null);
			invoice.setShippingAddress(null);
			invoice.setPartyContact(null);
		}
		else {
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
		}

		return invoice;

	}

}
