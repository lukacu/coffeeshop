package org.coffeeshop.animation;

public class Curve {

	public enum Type {LINEAR, LOGARTIMIC, CUBIC}
	
	private Type type;
	
	private double begin, end, k; 
	
	private int steps;
	
	public Curve(double begin, double end, int steps, Type type) {
		
		if (steps < 1) 
			throw new IllegalArgumentException("Number of steps must be 1 or more");
		
		this.type = type;
		
		this.begin = begin;
		this.end = end;
		this.steps = steps;
		
		switch(type) {
		case LINEAR: {
			
			k = (end - begin) / (double) steps;

			break;
		}
		case LOGARTIMIC: {
			
			k = (double)(end - begin) / Math.log(steps);
			
			break;
		}
		case CUBIC: {
			
			k = (end - begin) / (double) (steps * steps);
			
			break;
		}
		}
		
		
	}
	
	public double get(int step) {

		if (step <= 0)
			return begin;
		
		if (step >= steps)
			return end;
		
		switch( type) {
		case LINEAR: {
			//System.out.println(begin + " " + k);
			return begin + (k * step);
		}
		case LOGARTIMIC: {
			return begin + (k * Math.log(step));
		}
		case CUBIC: {
			return begin + (k * (step * step));
		}
		
		
		}
		
		return 0;
	}
	
	public int getSteps() {
		return steps;
	}
	
}
