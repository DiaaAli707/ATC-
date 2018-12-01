import java.util.ArrayList;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class NaiveBayes {
	private double priorProb;
	private RealMatrix posProb, negProb, cat1, cat2, cat3, cat4;
	private ArrayList<String> vocab = new ArrayList<String>();

	public NaiveBayes(String[][] data, String[] label, ArrayList<String> vocab) throws Exception {
		this.vocab = vocab;
		double[][] probArray = new double[data.length][];
		IntStream.range(0, data.length).forEach(i -> probArray[i] = mapDocToVocab(data[i]));
		calcCategoryProb(label).calcCondProbs(MatrixUtils.createRealMatrix(probArray), label);

	}

	public String classify(String[] docArray) {
		String categoryClassification = "sports";
		RealMatrix doc = MatrixUtils.createRowRealMatrix(mapDocToVocab(docArray));
		double pLogSums = Math.log(priorProb) + doc.multiply(posProb.transpose()).getData()[0][0];
		double nLogSums = Math.log(1 - priorProb) + doc.multiply(negProb.transpose()).getData()[0][0];

		if (pLogSums > nLogSums)
			categoryClassification = "politics";

		return "Text Classified as: " + categoryClassification +"\n";
	}

	public NaiveBayes calcCategoryProb(String[] label) {
		int sum = 0;
		for (int i = 0; i < label.length; i++) {
			if (label[i].equals("politics")) {
				sum += 1;
			}

		}
		priorProb = sum/(double) label.length;

		return this;
	}

	public NaiveBayes calcCondProbs(RealMatrix prob, String[] label) {
		RealMatrix nProbNum = MatrixUtils.createRowRealMatrix(new double[vocab.size()]);
		for (int i = 0; i < vocab.size(); i++) {
			nProbNum.setEntry(0, i, 1.0);
		}

		RealMatrix pProbNum = MatrixUtils.createRowRealMatrix(new double[vocab.size()]);
		for (int i = 0; i < vocab.size(); i++) {
			pProbNum.setEntry(0, i, 1.0);
		}

		double politicsDenom = vocab.size();
		double nDenom = vocab.size();

		for (int i = 0; i < label.length; i++) {
			if (label[i].equals("pol")) {
				pProbNum = pProbNum.add(prob.getRowMatrix(i));
				for (int j = 0; j < prob.getData()[0].length; j++) {
					politicsDenom += prob.getEntry(i, j);
				}
			} else {
				nProbNum = nProbNum.add(prob.getRowMatrix(i));
				for (int j = 0; j < prob.getData()[0].length; j++) {
					nDenom += prob.getEntry(i, j);
				}

			}

		}

		posProb = log(pProbNum.scalarMultiply(1 / politicsDenom));
		negProb = log(nProbNum.scalarMultiply(1 / nDenom));

		return this;
	}

	public RealMatrix log(RealMatrix matrix) {
		double[] returnData = new double[matrix.getData()[0].length];
		IntStream.range(0, returnData.length).forEach(j -> returnData[j] = Math.log((matrix.getData()[0][j])));
		return MatrixUtils.createRowRealMatrix(returnData);
	}

	public double[] mapDocToVocab(String[] doc) {
		double[] mappedDoc = new double[vocab.size()];
		IntStream.range(0, vocab.size()).forEach(i -> mappedDoc[i] = 0);
		for (int i = 0; i < doc.length; i++) {
			for (int j = 0; j < vocab.size(); j++) {
				if (doc[i].equalsIgnoreCase(vocab.get(j))) {
					mappedDoc[j] += 1;
				}
			}
		}

		return mappedDoc;
	}

}
