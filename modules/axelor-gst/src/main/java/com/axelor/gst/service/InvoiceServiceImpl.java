package com.axelor.gst.service;

import java.math.BigDecimal;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Contact;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.repo.AddressRepository;

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
			invoicegrossAmount = invoicegrossAmount.add(invoiceItemsList.getGrossAmount());
			if (invoice.getCompany() == null || invoice.getParty() == null || invoice.getCompany().getAddress() == null
					|| invoice.getCompany().getAddress().getState() == null || invoice.getInvoiceAddress() == null
					|| invoice.getInvoiceAddress().getState() == null) {

				invoice.setNetIGST(BigDecimal.ZERO);
				invoice.setNetSGST(BigDecimal.ZERO);
				invoice.setNetCGST(BigDecimal.ZERO);

			} else {
				invoiceigst = invoiceigst.add(invoiceItemsList.getIGST());
				invoicecgst = invoicecgst.add(invoiceItemsList.getCGST());
				invoicesgst = invoicesgst.add(invoiceItemsList.getSGST());
			}

		}
		invoice.setNetIGST(invoiceigst);
		invoice.setNetSGST(invoicesgst);
		invoice.setNetCGST(invoicecgst);
		invoice.setNetAmount(invoiceNetAmount);
		invoice.setGrossAmount(invoicegrossAmount);
		return invoice;
	}

	@Override
	public Invoice setInvoiceData(Invoice invoice) {

		if (invoice.getParty() != null) {
			if (!invoice.getParty().getContactList().isEmpty()) {
				for (Contact partyContactList : invoice.getParty().getContactList()) {
					if (partyContactList.getType().equals("primary")) {
						invoice.setPartyContact(partyContactList);
					}
				}
			} else {
				invoice.setPartyContact(null);
			}
			if (!invoice.getParty().getAddressList().isEmpty()) {
				for (Address address : invoice.getParty().getAddressList()) {

					if (address.getType().equals(AddressRepository.DEFAULT)) {
						invoice.setInvoiceAddress(address);
						invoice.setShippingAddress(address);
					} else if (address.getType().equals(AddressRepository.INVOICE)) {
						invoice.setInvoiceAddress(address);
						if (invoice.getUseInvoiceAddress() == Boolean.TRUE) {
							invoice.setShippingAddress(invoice.getInvoiceAddress());
						}
					} else if (address.getType().equals(AddressRepository.SHIPPING)) {
						if (invoice.getUseInvoiceAddress() == Boolean.TRUE) {
							invoice.setShippingAddress(invoice.getInvoiceAddress());
						} else
							invoice.setShippingAddress(address);
					}
				}
			} else {
				invoice.setInvoiceAddress(null);
				invoice.setShippingAddress(null);
			}
		} else {
			invoice.setPartyContact(null);
			invoice.setInvoiceAddress(null);
			invoice.setShippingAddress(null);
		}

		return invoice;

	}

}
