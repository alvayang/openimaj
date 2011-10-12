package org.openimaj.math.model;

import gov.sandia.cognition.learning.algorithm.bayes.VectorNaiveBayesCategorizer;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.UnivariateGaussian.PDF;

import java.util.ArrayList;
import java.util.List;

import org.openimaj.util.pair.IndependentPair;

public class UnivariateGaussianNaiveBayesModel<T> implements Model<Double, T> {
	VectorNaiveBayesCategorizer.BatchGaussianLearner<T> learner = new VectorNaiveBayesCategorizer.BatchGaussianLearner<T>();
	private VectorNaiveBayesCategorizer<T, PDF> model;
	
	@Override
	public void estimate(List<? extends IndependentPair<Double, T>> data) {
		List<InputOutputPair<Vector,T>> cfdata = new ArrayList<InputOutputPair<Vector,T>>();
		
		for (IndependentPair<Double,T> d : data) {
			InputOutputPair<Vector,T> iop = new DefaultInputOutputPair<Vector,T>(VectorFactory.getDefault().createVector1D(d.firstObject()) , d.secondObject());
			cfdata.add(iop);
		}
		
		model = learner.learn(cfdata);
	}

	@Override
	public boolean validate(IndependentPair<Double, T> data) {
		return predict(data.firstObject()).equals(data.secondObject());
	}

	@Override
	public T predict(Double data) {
		return model.evaluate(VectorFactory.getDefault().createVector1D(data));
	}

	@Override
	public int numItemsToEstimate() {
		return 0;
	}

	@Override
	public double calculateError(List<? extends IndependentPair<Double, T>> data) {
		int count = 0;
		
		for (IndependentPair<Double, T> d : data) {
			if (!validate(d))
				count++;
		}
		
		return (double)count / (double)data.size();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public UnivariateGaussianNaiveBayesModel<T> clone() {
		try {
			return (UnivariateGaussianNaiveBayesModel<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		UnivariateGaussianNaiveBayesModel<Boolean> model = new UnivariateGaussianNaiveBayesModel<Boolean>();
		
		List<IndependentPair<Double, Boolean>> data = new ArrayList<IndependentPair<Double, Boolean>>();
		
		data.add(IndependentPair.pair(0.0, true));
		data.add(IndependentPair.pair(0.1, true));
		data.add(IndependentPair.pair(-0.1, true));
		
		data.add(IndependentPair.pair(9.9, false));
		data.add(IndependentPair.pair(10.0, false));
		data.add(IndependentPair.pair(10.1, false));
		
		model.estimate(data);
		
		System.out.println(model.predict(5.1));
		
		
		System.out.println(model.model.getPriors());
	}
}