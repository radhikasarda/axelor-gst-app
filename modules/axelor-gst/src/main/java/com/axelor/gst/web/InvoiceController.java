package com.axelor.gst.web;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.Product;
import com.axelor.axelor.gst.db.Sequence;
import com.axelor.axelor.gst.db.repo.ProductRepository;
import com.axelor.axelor.gst.db.repo.SequenceRepository;
import com.axelor.gst.service.InvoiceLineService;
import com.axelor.gst.service.InvoiceService;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.meta.schema.actions.ActionView.ActionViewBuilder;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class InvoiceController {

	@Inject
	InvoiceService service;
	@Inject
	InvoiceLineService invoiceLineService;
	@Inject
	SequenceRepository sequenceRepository;
	@Inject
	ProductRepository productRepository;
	@Inject
	SequenceService sequenceService;

	public void calculateInvoice(ActionRequest request, ActionResponse response) {

		Invoice invoice = request.getContext().asType(Invoice.class);
		Address invoiceAddress = null;
		Address companyAddress = null;
		if (invoice.getInvoiceAddress() != null) {
			invoiceAddress = invoice.getInvoiceAddress();
		}
		if (invoice.getCompany() != null) {
			if (invoice.getCompany().getAddress() != null) {
				companyAddress = invoice.getCompany().getAddress();
			}
		}
		if (invoice.getInvoiceItemsList() == null) {
			return;
		} else {
			List<InvoiceLine> invoiceLineList = invoice.getInvoiceItemsList();
			List<InvoiceLine> invoiceLineItems = new ArrayList<InvoiceLine>();

			for (InvoiceLine invoiceLineItem : invoiceLineList) {
				InvoiceLine invoiceline = invoiceLineService.calculateInvoiceLine(invoiceLineItem, invoiceAddress,
						companyAddress);
				invoiceLineItems.add(invoiceline);
				invoice.setInvoiceItemsList(invoiceLineItems);
				response.setValue("invoiceItemsList", invoice.getInvoiceItemsList());
			}
			invoice = service.calculateInvoice(invoice);
			response.setValue("netCGST", invoice.getNetCGST());
			response.setValue("netIGST", invoice.getNetIGST());
			response.setValue("netSGST", invoice.getNetSGST());
			response.setValue("grossAmount", invoice.getGrossAmount());
			response.setValue("netAmount", invoice.getNetAmount());

		}

	}

	public void setInvoiceData(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		invoice = service.setInvoiceData(invoice);
		response.setValue("partyContact", invoice.getPartyContact());
		response.setValue("invoiceAddress", invoice.getInvoiceAddress());
		response.setValue("shippingAddress", invoice.getShippingAddress());
	}

	public void createInvoice(ActionRequest request, ActionResponse response) {
		Context context = request.getContext();
		List<String> list = (List<String>) context.get("_ids");
		if (list != null) {

			List<InvoiceLine> invoiceLineList = new ArrayList<>();
			if (list != null) {

				for (int i = 0; i < list.size(); i++) {
					Product product = Beans.get(ProductRepository.class).all().filter("self.id = ?", list.get(i))
							.fetchOne();
					InvoiceLine invoiceLine = new InvoiceLine();
					invoiceLine.setProduct(product);
					invoiceLine.setItem(product.getName());
					invoiceLine.setHSBN(product.getHSBN());
					invoiceLine.setQty(1);
					invoiceLine.setPrice(product.getSalePrice());
					invoiceLine.setGstRate(product.getGstRate());
					invoiceLineList.add(invoiceLine);
				}

				ActionViewBuilder actionViewBuilder = ActionView.define(String.format("Create Invoice"))
						.model(Invoice.class.getName()).add("form", "invoice-form").add("grid", "invoice-grid")
						.context("invoiceLineList", invoiceLineList);
				response.setView(actionViewBuilder.map());
			}
		} else {
			response.setError("Please Select a Product!!");
		}

	}

	public void fetchInvoiceLineData(ActionRequest request, ActionResponse response) {

		Context context = request.getContext();
		response.setValue("invoiceItemsList", context.get("invoiceLineList"));

	}

	public void generateReferenceInvoice(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		if (invoice.getReference() == null) {
			MetaModel model = Beans.get(MetaModelRepository.class).findByName("Invoice");
			Sequence sequence = sequenceRepository.all().filter("self.model = " + model.getId()).fetchOne();
			if (sequence == null) {
				response.setError("No sequence specified");
			} else {
				invoice.setReference(sequence.getNextNumber());
				sequenceService.getReferenceNumber(sequence);
				response.setValues(invoice);
			}

		}
	}

}
