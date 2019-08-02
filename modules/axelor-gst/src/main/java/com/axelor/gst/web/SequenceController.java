package com.axelor.gst.web;

import javax.inject.Inject;

import com.axelor.axelor.gst.db.Sequence;
import com.axelor.gst.service.SequenceService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class SequenceController {

	@Inject
	SequenceService sequenceService;

	public void generateNextNumber(ActionRequest request, ActionResponse response) {

		Sequence sequence = request.getContext().asType(Sequence.class);
		sequence = sequenceService.generateNextNumber(sequence);
		response.setValues(sequence);
	}
	
}
