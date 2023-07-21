package com.khjxiaogu.convivium.util.evaluator;

public class NullEnvironment implements IEnvironment{
	public static final IEnvironment INSTANCE=new NullEnvironment();
	private NullEnvironment() {
		super();
	}
	@Override
	public Double getOptional(String key) {
		return null;
	}

	@Override
	public void set(String key, double v) {
		
	}

	@Override
	public double get(String key) {
		throw new IllegalStateException("Connot call variant on non variant enironment.");
	}
}
