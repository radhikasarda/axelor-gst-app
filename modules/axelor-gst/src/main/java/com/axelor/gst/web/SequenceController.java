package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Sequence;
import com.axelor.axelor.gst.db.repo.SequenceRepository;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;

public class SequenceController {

	@Inject
	SequenceService sequenceService;
	@Inject
	SequenceRepository sequenceRepository;

	public void generateNextNumber(ActionRequest request, ActionResponse response) {

		Sequence sequence = request.getContext().asType(Sequence.class);
		sequence = sequenceService.generateNextNumber(sequence);
		response.setValue("nextNumber", sequence.getNextNumber());
	}

	@Transactional
	public void generateReference(ActionRequest request, ActionResponse response) {

		String models = (String) request.getContext().get("_model");
		if (models.equals("com.axelor.axelor.gst.db.Invoice")) {
			Invoice invoice = request.getContext().asType(Invoice.class);
			if (invoice.getReference() == null) {
				MetaModel model = Beans.get(MetaModelRepository.class).findByName("Invoice");
				Sequence sequence = sequenceRepository.all().filter("self.model = " + model.getId()).fetchOne();
				if (sequence == null) {
					response.setError("No sequence specified");
				} 
				else {
					String refNumber = sequence.getNextNumber();
					invoice.setReference(refNumber);
					sequence = sequenceService.getReferenceNumber(sequence);
					sequenceRepository.save(sequence);
					response.setValue("reference", invoice.getReference());
					}
			}
		}else if(models.equals("com.axelor.axelor.gst.db.Party")){
			
			Party party = request.getContext().asType(Party.class);
			if (party.getReference() == null) {
				MetaModel model = Beans.get(MetaModelRepository.class).findByName("Party");
				Sequence sequence = sequenceRepository.all().filter("self.model = " + model.getId()).fetchOne();
				if (sequence == null) {
					response.setError("No sequence specified");
				} 
				else {
					String refNumber = sequence.getNextNumber();
					party.setReference(refNumber);
					sequence = sequenceService.getReferenceNumber(sequence);
					sequenceRepository.save(sequence);
					response.setValue("reference", party.getReference());
				}
			
		}
	}
}
}
