package com.challenges.vehicle_survey.analysis;

import com.challenges.vehicle_survey.analysis.result_write.AnalysisResultWriter;

public interface AnalysisPrintable {

	void accept(AnalysisResultWriter analysisResultWriter);
}
