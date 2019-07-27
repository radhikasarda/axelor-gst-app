package com.axelor.gst.service;

import com.axelor.axelor.gst.db.Invoice;
import com.axelor.axelor.gst.db.Sequence;

public class SequenceServiceImpl implements SequenceService {

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

	@Override
	public Sequence getReferenceNumber(Sequence sequence) {
			
			String prefix = sequence.getPrefix();
			String suffix = sequence.getSuffix();
			Long newNum = Long.parseLong(
					sequence.getNextNumber().substring(sequence.getPrefix().length(), prefix.length() + sequence.getPadding()));
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
			return sequence;
}
}
