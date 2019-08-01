package com.axelor.gst.service;

import com.axelor.axelor.gst.db.Sequence;

public interface SequenceService {

	public Sequence generateNextNumber(Sequence sequence);

	public Sequence getReferenceNumber(Sequence sequence);
}
