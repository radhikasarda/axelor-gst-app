package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Address;
import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.InvoiceLine;
import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Sequence;
import com.axelor.axelor.gst.db.repo.SequenceRepository;
import com.axelor.db.Query;
import com.axelor.gst.service.Service;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;

public class Controller {

	
	@Inject Service service;
	@Inject SequenceRepository sequenceRepository;
	
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
	
	public void generateNextNumber(ActionRequest request, ActionResponse response) {
		
		Sequence sequence=request.getContext().asType(Sequence.class);
		sequence=service.generateNextNumber(sequence);
		response.setValue("nextNumber",sequence.getNextNumber());
	}
	
	@Transactional
	public void generateReferenceParty(ActionRequest request, ActionResponse response) {
		
		Party party=request.getContext().asType(Party.class);
		
		if(party.getReference()==null) {
		MetaModel model = Beans.get(MetaModelRepository.class).findByName("Party");	
		Sequence sequence=sequenceRepository.all().filter("self.model = "+model.getId()).fetchOne();
		if(sequence==null) {
			System.err.println("No sequence specified");
		}
		String refNumber=sequence.getNextNumber();
		party.setReference(refNumber);
		String prefix=sequence.getPrefix();
		String suffix=sequence.getSuffix();
		Long newNum=Long.parseLong(refNumber.substring(sequence.getPrefix().length(),prefix.length()+sequence.getPadding()));
		newNum++;
		String newStringNumber=newNum.toString();
		
		for (int i = 0; i <( sequence.getPadding() - newStringNumber.length()); i++) {
				prefix=prefix.concat("0");
		}
		System.out.println(newStringNumber);
		String nextNum=prefix+newStringNumber;
		if(suffix!=null) { 
		nextNum=nextNum+suffix;
		}
		sequence.setNextNumber(nextNum);	
		sequenceRepository.save(sequence);
		response.setValue("reference", party.getReference());
		}
	}
	@Transactional
	public void getReferenceInvoice(ActionRequest request, ActionResponse response) {
		
		Invoice invoice=request.getContext().asType(Invoice.class);
		if(invoice.getReference()==null ) {
			MetaModel model = Beans.get(MetaModelRepository.class).findByName("Invoice");	
			Sequence sequence=sequenceRepository.all().filter("self.model = "+model.getId()).fetchOne();
			if(sequence==null) {
				System.err.println("No sequence specified");
			}
			String refNumber=sequence.getNextNumber();
			invoice.setReference(refNumber);
			String prefix=sequence.getPrefix();
			String suffix=sequence.getSuffix();
			Long newNum=Long.parseLong(refNumber.substring(sequence.getPrefix().length(),prefix.length()+sequence.getPadding()));
			newNum++;
			String newStringNumber=newNum.toString();
			
			for (int i = 0; i <( sequence.getPadding() - newStringNumber.length()); i++) {
					prefix=prefix.concat("0");
			}
			System.out.println(newStringNumber);
			String nextNum=prefix+newStringNumber;
			if(suffix!=null) { 
			nextNum=nextNum+suffix;
			}
			sequence.setNextNumber(nextNum);	
			sequenceRepository.save(sequence);
			response.setValue("reference", invoice.getReference());
			}	
		}
	}
	

