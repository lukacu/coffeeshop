package org.coffeeshop.animation;

public class CompositeCurve implements Curve {

	private int totalSteps;
	
	private int[] offsets;
	
	private Curve[] curves;
	
	public CompositeCurve(Curve ... curves) {
		
		this.curves = new Curve[curves.length];
		this.offsets = new int[curves.length];
		
		for (int i = 0; i < curves.length; i++) {
			this.offsets[i] = totalSteps;
			totalSteps += curves[i].getSteps();
			this.curves[i] = curves[i];
		}
		
	}
	
	@Override
	public double get(int step) {
		
		if (step < 0)
			return curves[0].get(step);
		
		for (int i = 0; i < curves.length; i++) {
			if (offsets[i] > step) {
				return curves[i-1].get(step-offsets[i-1]);
			}
		}
		
		return curves[curves.length-1].get(step-offsets[curves.length-1]);
	}

	@Override
	public int getSteps() {
		return totalSteps;
	}

}
