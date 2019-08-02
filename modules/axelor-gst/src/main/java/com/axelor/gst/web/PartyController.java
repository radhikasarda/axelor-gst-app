package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Party;
import com.axelor.axelor.gst.db.Sequence;
import com.axelor.axelor.gst.db.repo.SequenceRepository;
import com.axelor.gst.service.SequenceService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaModel;
import com.axelor.meta.db.repo.MetaModelRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class PartyController {

	@Inject
	SequenceService sequenceService;
	@Inject
	SequenceRepository sequenceRepository;
	
	public void generateReferenceParty(ActionRequest request, ActionResponse response) {
		Party party = request.getContext().asType(Party.class);
		if (party.getReference() == null) {
			MetaModel model = Beans.get(MetaModelRepository.class).findByName("Party");
			Sequence sequence = sequenceRepository.all().filter("self.model = " + model.getId()).fetchOne();
			if (sequence == null) {
				response.setError("No sequence specified");
			} else {
				party.setReference(sequence.getNextNumber());
				sequenceService.getReferenceNumber(sequence);
				response.setValues(party);
			}

		}
		
	}	
}
