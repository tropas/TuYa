package com.tuya.imagine.pip.data;

import com.tuya.imagine.app.BaseApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader.TileMode;

public class PipFrameProperties {

	public static PIPFrameItem[] pipFrames;

	static {
		pipFrames = new PIPFrameItem[15];

		pipFrames[0] = new PIPFrameItem(
				"pip/frame/0.png",
				"pip/shape/0.png",
				new float[] { 131f / 540f, 67f / 600f, 465f / 540f, 375f / 600f });
		pipFrames[1] = new PIPFrameItem(
				"pip/frame/1.png",
				"pip/shape/1.png",
				new float[] { 261f / 540f, 45f / 400f, 488f / 540f, 272f / 400f });
		pipFrames[2] = new PIPFrameItem(
				"pip/frame/2.png",
				"pip/shape/2.png",
				new float[] { 35f / 540f, 59f / 700f, 465f / 540f, 602f / 700f });
		
		pipFrames[3] = new PIPFrameItem(
				"pip/frame/3.png",
				"pip/shape/3.png",
				new float[] { 35f / 540f, 152f / 540f, 328f / 540f, 406f / 540f });
		pipFrames[4] = new PIPFrameItem(
				"pip/frame/4.png",
				"pip/shape/4.png",
				new float[] { 315f / 540f, 68f / 540f, 481f / 540f, 498f / 540f });
		pipFrames[5] = new PIPFrameItem(
				"pip/frame/5.png",
				"pip/shape/5.png",
				new float[] { 21f / 540f, 162f / 800f, 384f / 540f, 528f / 800f });
		pipFrames[6] = new PIPFrameItem(
				"pip/frame/6.png",
				"pip/shape/6.png",
				new float[] { 307f / 540f, 27f / 464f, 540f / 540f, 268f / 464f });
		pipFrames[7] = new PIPFrameItem(
				"pip/frame/7.png",
				"pip/shape/7.png",
				new float[] { 27f / 540f, 95f / 400f, 303f / 540f, 306f / 400f });
		pipFrames[8] = new PIPFrameItem("pip/frame/8.png", "pip/shape/8.png",
				new float[] { 128f / 540f, 226f / 540f, 485f / 540f,
						479f / 540f });
		pipFrames[9] = new PIPFrameItem(
				"pip/frame/9.png",
				"pip/shape/9.png",
				new float[] { 23f / 540f, 359f / 679f, 418f / 540f, 652f / 679f });
		pipFrames[10] = new PIPFrameItem(
				"pip/frame/10.png",
				"pip/shape/10.png",
				new float[] { 89f / 540f, 315f / 685f, 460f / 540f, 575f / 685f });
		pipFrames[11] = new PIPFrameItem("pip/frame/11.png", "pip/shape/11.png",
				new float[] { 121f / 540f, 123f / 540f, 420f / 540f,
						417f / 540f });
		pipFrames[12] = new PIPFrameItem(
				"pip/frame/12.png",
				"pip/shape/12.png",
				new float[] { 43f / 540f, 126f / 540f, 498f / 540f, 473f / 540f });
		pipFrames[13] = new PIPFrameItem(
				"pip/frame/13.png",
				"pip/shape/13.png",
				new float[] { 23f / 550f, 30f / 615f, 532f / 550f, 398f / 615f });

		pipFrames[14] = new PIPFrameItem("pip/frame/14.png", "pip/shape/14.png",
				new float[] { 114f / 540f, 207f / 540f, 457f / 540f,
						494f / 540f });
	}

	private static int getColor(int id) {
		return BaseApplication.getApplication().getResources().getColor(id);
	}

	private static BitmapShader getBmpShader(int id) {
		Bitmap bmp = BitmapFactory.decodeResource(BaseApplication
				.getApplication().getResources(), id);
		if (bmp != null) {
			return new BitmapShader(bmp, TileMode.REPEAT, TileMode.REPEAT);
		} else {
			return null;
		}
	}
}
