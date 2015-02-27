package com.tuya.imagine;

public class Imagine {

	static{
		System.loadLibrary("imagine");
	}
	
	
	public native static void doBlurProcess(int[] buf, int w, int h, int raidux);
	
	
	public native static String hello();
}
