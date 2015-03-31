import dataFilter.FilterAlert;
import dataFilter.FilterRawLog;
import dataFilter.LabelSetFilter;
import dataFilter.Statistics;
import analysis.FPGrowth;
import analysis.LogMerge;
import training.FeatureExtraction;
import training.LogMergeByLCS;
import training.RMNoiseWordByRule;
import training.Structured;
import training.Structured_Center;
import training.Tagging;
import training.Vectorization;


public class OneKeyRunning {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		Structured_Center.main(null);
		Structured.main(null);
		RMNoiseWordByRule.main(null);
		Vectorization.main(null);
		Tagging.main(null);
		LogMergeByLCS.main(null);
		FeatureExtraction.main(null);
		LogMerge.main(null);
//		FPTree.main(null);
		
//		LabelSetFilter.main(null);
	}

}
