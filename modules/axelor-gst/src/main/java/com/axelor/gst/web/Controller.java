package com.axelor.gst.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.axelor.app.AppSettings;
import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Product;
import com.axelor.axelor.gst.db.Sequence;
import com.axelor.axelor.gst.db.repo.ProductRepository;
import com.axelor.axelor.gst.db.repo.SequenceRepository;
import com.axelor.db.Query;
import com.axelor.gst.service.Service;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.meta.schema.actions.ActionView.ActionViewBuilder;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.axelor.rpc.Context;

public class Controller {

	@Inject
	Service service;
	@Inject
	SequenceRepository sequenceRepository;
	@Inject
	ProductRepository productRepository;

	public void calculate(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceline = request.getContext().asType(InvoiceLine.class);
		Invoice invoice = request.getContext().getParent().asType(Invoice.class);

		Address invoiceAddress = invoice.getInvoiceAddress();
		Address companyAddress = invoice.getCompany().getAddress();
		invoiceline = service.calculate(invoiceline, invoiceAddress, companyAddress);
		response.setValue("netAmount", invoiceline.getNetAmount());
		response.setValue("IGST", invoiceline.getIGST());
		response.setValue("SGST", invoiceline.getSGST());
		response.setValue("CGST", invoiceline.getCGST());
		response.setValue("grossAmount", invoiceline.getGrossAmount());
	}

	
	public void calculateInvoice(ActionRequest request, ActionResponse response) {

		Invoice invoice = request.getContext().asType(Invoice.class);
		if(invoice.getInvoiceItemsList()==null) {
			return;
		}
		else {
		List<InvoiceLine> invoiceLineList=invoice.getInvoiceItemsList();
		List<InvoiceLine> invoiceLineItems=new ArrayList<InvoiceLine>();
		for(InvoiceLine invoiceLineItem:invoiceLineList) {
			InvoiceLine invoiceline = service.calculate(invoiceLineItem, invoice.getInvoiceAddress(),invoice.getCompany().getAddress());	
			invoiceLineItems.add(invoiceline);
		}
		invoice.setInvoiceItemsList(invoiceLineItems);
		invoice = service.calculateInvoice(invoice);
		response.setValues(invoice);
		}
	}

	public void fetchInvoiceData(ActionRequest request, ActionResponse response) {

		Invoice invoice = request.getContext().asType(Invoice.class);
		invoice = service.fetchInvoiceData(invoice);
		response.setValue("partyContact", invoice.getPartyContact());
		response.setValue("invoiceAddress", invoice.getInvoiceAddress());
		response.setValue("shippingAddress", invoice.getShippingAddress());
	}

	public void generateNextNumber(ActionRequest request, ActionResponse response) {

		Sequence sequence = request.getContext().asType(Sequence.class);
		sequence = service.generateNextNumber(sequence);
		response.setValue("nextNumber", sequence.getNextNumber());
	}

	@Transactional
	public void generateReferenceParty(ActionRequest request, ActionResponse response) {

		Party party = request.getContext().asType(Party.class);

		if (party.getReference() == null) {
			MetaModel model = Beans.get(MetaModelRepository.class).findByName("Party");
			Sequence sequence = sequenceRepository.all().filter("self.model = " + model.getId()).fetchOne();
			if (sequence != null) {
				System.err.println("No sequence specified");

				String refNumber = sequence.getNextNumber();
				party.setReference(refNumber);
				String prefix = sequence.getPrefix();
				String suffix = sequence.getSuffix();
				Long newNum = Long.parseLong(
						refNumber.substring(sequence.getPrefix().length(), prefix.length() + sequence.getPadding()));
				newNum++;
				String newStringNumber = newNum.toString();

				for (int i = 0; i < (sequence.getPadding() - newStringNumber.length()); i++) {
					prefix = prefix.concat("0");
				}
				String nextNum = prefix + newStringNumber;
				if (suffix != null) {
					nextNum = nextNum + suffix;
				}
				sequence.setNextNumber(nextNum);
				sequenceRepository.save(sequence);
				response.setValue("reference", party.getReference());
			}
		} else
			System.err.println("No sequence specified");
	}

	@Transactional
	public void getReferenceInvoice(ActionRequest request, ActionResponse response) {

		Invoice invoice = request.getContext().asType(Invoice.class);
		if (invoice.getReference() == null) {
			MetaModel model = Beans.get(MetaModelRepository.class).findByName("Invoice");
			Sequence sequence = sequenceRepository.all().filter("self.model = " + model.getId()).fetchOne();
			if (sequence != null) {
				String refNumber = sequence.getNextNumber();
				invoice.setReference(refNumber);
				String prefix = sequence.getPrefix();
				String suffix = sequence.getSuffix();
				Long newNum = Long.parseLong(
						refNumber.substring(sequence.getPrefix().length(), prefix.length() + sequence.getPadding()));
				newNum++;
				String newStringNumber = newNum.toString();

				for (int i = 0; i < (sequence.getPadding() - newStringNumber.length()); i++) {
					prefix = prefix.concat("0");
				}
				System.out.println(newStringNumber);
				String nextNum = prefix + newStringNumber;
				if (suffix != null) {
					nextNum = nextNum + suffix;
				}
				sequence.setNextNumber(nextNum);
				sequenceRepository.save(sequence);
				response.setValue("reference", invoice.getReference());
			} else
				System.err.println("No sequence specified");

		}
	}

	public void fetchReportData(ActionRequest request, ActionResponse response) {

		Context context = request.getContext();
		String filePath = AppSettings.get().get("file.upload.dir");
		List<String> list = (List<String>) context.get("_ids");
		if (list != null) {
			String lists = list.toString();
			context.put("listId", lists.substring(1, lists.length() - 1));
		} else {
			Long ids = (Long) context.get("id");
			context.put("listId", ids.toString());
		}
		context.put("filePath", filePath);

	}

	public void fetchGstRate(ActionRequest request, ActionResponse response) {

		Product product = request.getContext().asType(Product.class);
		BigDecimal gstRate = BigDecimal.ZERO;
		gstRate = product.getCategory().getGstRate();
		product.setGstRate(gstRate);
		response.setValue("gstRate", gstRate);

	}

	public void createInvoice(ActionRequest request, ActionResponse response) {
		Context context = request.getContext();
		List<String> list = (List<String>) context.get("_ids");
		
		List<InvoiceLine> invoiceLineList = new ArrayList<>();
		if (list != null) {

			for (int i = 0; i < list.size(); i++) {
				Product product = Beans.get(ProductRepository.class).all().filter("self.id = ?", list.get(i))
						.fetchOne();
				InvoiceLine invoiceLine = new InvoiceLine();
				invoiceLine.setProduct(product);
				invoiceLine.setItem(product.getName());
				invoiceLine.setHSBN(product.getHSBN());
				invoiceLine.setPrice(product.getSalePrice());
				invoiceLine.setGstRate(product.getGstRate());
				invoiceLineList.add(invoiceLine);
			}

			ActionViewBuilder actionViewBuilder = ActionView.define(String.format("Create Invoice"))
					.model(Invoice.class.getName()).add("form", "invoice-form").context("invoiceLineList", invoiceLineList);
			response.setView(actionViewBuilder.map());
		}

	}
	
	public void fetchInvoiceLineData(ActionRequest request, ActionResponse response) {
		
		Context context=request.getContext();
		response.setValue("invoiceItemsList", context.get("invoiceLineList"));

	}
}
