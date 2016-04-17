package com.challenges.vehicle_survey.analysis;


public class AnalysisResultEntry implements Comparable<AnalysisResultEntry>{
	
	private Double resultEntry;
	
	public AnalysisResultEntry(Double result) {
		super();
		this.resultEntry = result;
	}
	
	public AnalysisResultEntry(Long result) {
		super();
		this.resultEntry = new Double(result);
	}

	public Double getResultEntry() {
		return resultEntry;
	}
	
	@Override
	public int compareTo(AnalysisResultEntry o) {
		
		if ( o == null ) {
			return -1;
		}
		
		return this.resultEntry.compareTo(o.getResultEntry());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resultEntry == null) ? 0 : resultEntry.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AnalysisResultEntry))
			return false;
		AnalysisResultEntry other = (AnalysisResultEntry) obj;
		if (resultEntry == null) {
			if (other.resultEntry != null)
				return false;
		} else if (!resultEntry.equals(other.resultEntry))
			return false;
		return true;
	}
}
