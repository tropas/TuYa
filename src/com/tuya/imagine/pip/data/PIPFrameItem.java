package com.tuya.imagine.pip.data;

         
public class PIPFrameItem {
	
	public String frameAssetPath;
	public String shapeAssetPath;
	public float[] layouts;
	
	public PIPFrameItem(String framePath,String shapePath,float[] layouts){
		this.frameAssetPath = framePath;
		this.shapeAssetPath = shapePath;
		this.layouts = layouts;
	}

}
