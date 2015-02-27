package com.tuya.imagine.pip.view;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tuya.baselib.util.BitmapUtil;
import com.tuya.imagine.Imagine;
import com.tuya.imagine.app.BaseApplication;
import com.tuya.imagine.pip.data.PIPFrameItem;

public class PIPView extends RelativeLayout {

	private Context mContext;
	private Bitmap frameBmp = null;
	private Bitmap bmpSrc = null;
	private Bitmap bmpBlur = null;// 模糊的底图

	private float mScale = 1.0f;// 载入相框的Scale

	private Bitmap bmpShape = null;// 形状的图
	private Paint shapePaint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
	private Matrix matrixBlur = null;// 模糊地图OnDraw的matrix
	// private int posX;
	// private int posY;
	private float[] fRects;
	private int initWidth = 0;
	private int initHeight = 0;
	// private int color = 0;
	// private BitmapShader shader = null;
	private int viewWidth = 0;
	private int viewHeight = 0;
	// private Paint paint = new Paint(Paint.DITHER_FLAG
	// | Paint.FILTER_BITMAP_FLAG);
	private Paint bmpPaint = new Paint(Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

	private boolean isInitData = false;
	private boolean isOnLayout = false;

	public PIPView(Context context) {
		super(context);
		this.mContext = context;
		setWillNotDraw(false);
	}

	public PIPView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		setWillNotDraw(false);
		shapePaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	}

	public void initData(PIPFrameItem item, Bitmap bmp) {
		// this.frameBmp =
		if (BitmapUtil.isValid(bmp)) {
			bmpSrc = bmp;
			bmpBlur = bmp.copy(Config.ARGB_8888, true);
			int w = bmpBlur.getWidth(), h = bmpBlur.getHeight();
			int[] pix = new int[w * h];
			bmpBlur.getPixels(pix, 0, w, 0, 0, w, h);
			Imagine.doBlurProcess(pix, w, h, 6);
			bmpBlur.setPixels(pix, 0, w, 0, 0, w, h);
		}
		try {
			this.frameBmp = BitmapFactory.decodeStream(BaseApplication.getApplication().getResources().getAssets()
					.open(item.frameAssetPath));
			this.bmpShape = BitmapFactory.decodeStream(BaseApplication.getApplication().getResources().getAssets()
					.open(item.shapeAssetPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.fRects = item.layouts;
	}

	public void changePic(Bitmap bmp) {
		this.removeAllViews();
		if (BitmapUtil.isValid(bmp)) {
			bmpSrc = bmp;
			bmpBlur = bmp.copy(Config.ARGB_8888, true);
			int w = bmpBlur.getWidth(), h = bmpBlur.getHeight();
			int[] pix = new int[w * h];
			bmpBlur.getPixels(pix, 0, w, 0, 0, w, h);
//			Imagine.doBlurProcess(pix, w, h, 6);
			bmpBlur.setPixels(pix, 0, w, 0, 0, w, h);
			// isOnLayout = false;
			// setOnLayout();
			addContenView();
		}
	}

	public void ChangeFrame(PIPFrameItem item) {
		this.removeAllViews();
		isOnLayout = false;

		BitmapUtil.safeRecycled(frameBmp);
		BitmapUtil.safeRecycled(bmpShape);
		try {
			this.frameBmp = BitmapFactory.decodeStream(BaseApplication.getApplication().getResources().getAssets()
					.open(item.frameAssetPath));
			this.bmpShape = BitmapFactory.decodeStream(BaseApplication.getApplication().getResources().getAssets()
					.open(item.shapeAssetPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setOnLayout();
		this.fRects = item.layouts;
		addContenView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	private void setOnLayout() {
		if (!isOnLayout) {
			isOnLayout = true;
			if (initWidth == 0 || initHeight == 0) {
				initWidth = this.getWidth();
				initHeight = this.getHeight();
			}
			if (BitmapUtil.isValid(frameBmp)) {
				float[] val = BitmapUtil.bmpInViewWH(frameBmp, initWidth, initHeight);
				viewWidth = (int) val[0];
				viewHeight = (int) val[1];
				mScale = val[2];
			}

			this.measure(viewWidth, viewHeight);
			if (BitmapUtil.isValid(bmpShape)) {
				this.bmpShape = BitmapUtil.createScaledBitmap(bmpShape, (int) (bmpShape.getWidth() * mScale),
						(int) (bmpShape.getHeight() * mScale), null);
			}

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
			layoutParams.leftMargin = (initWidth - viewWidth) / 2;
			layoutParams.topMargin = (initHeight - viewHeight) / 2;
			this.setLayoutParams(layoutParams);
			this.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + viewWidth, layoutParams.topMargin
					+ viewHeight);

		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (!isOnLayout)
			setOnLayout();

		if (changed && !isInitData) {
			isInitData = true;
			RectF rectF = new RectF(viewWidth * fRects[0], viewHeight * fRects[1], viewWidth * fRects[2], viewHeight * fRects[3]);
			PIPContentView imageView = new PIPContentView(mContext, false);

			float[] val2 = BitmapUtil.BmpInRect(bmpSrc, rectF);
			// paint.setShader(new BitmapShader(bmpBlur, TileMode.CLAMP,
			// TileMode.CLAMP));

			// 获取XY偏移及scale
			float[] blurVals = BitmapUtil.BmpInRect(bmpBlur, new RectF(0.0f, 0.0f, (float) viewWidth, (float) viewHeight));

			matrixBlur = new Matrix();

			matrixBlur.postScale(blurVals[2], blurVals[2]);
			matrixBlur.postTranslate(blurVals[0], blurVals[1]);

			imageView.initData(bmpSrc, bmpShape, rectF, null);
			imageView.setStyle(val2[0], val2[1], val2[2], 0);
			RelativeLayout.LayoutParams imageViewLp = new RelativeLayout.LayoutParams((int) rectF.width(), (int) rectF.height());

			imageViewLp.topMargin = (int) rectF.top;
			imageViewLp.leftMargin = (int) rectF.left;
			imageView.setLayoutParams(imageViewLp);
			this.addViewInLayout(imageView, 0, imageView.getLayoutParams(), true);
			imageView.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
			// this.addView(imageView, 0);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
			ImageView frameView = new ImageView(mContext);
			if (BitmapUtil.isValid(frameBmp)) {
				BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), frameBmp);
				frameView.setBackgroundDrawable(drawable);
			}
			frameView.setLayoutParams(lp);
			this.addViewInLayout(frameView, 1, frameView.getLayoutParams(), true);
			frameView.layout(0, 0, viewWidth, viewHeight);
			// this.addView(frameView, 1);
		}
		/*
		 * if (isInitData && changed) { isInitData = false;
		 * 
		 * ImageView frameView = new ImageView(mContext); BitmapDrawable
		 * drawable = new BitmapDrawable(mContext.getResources(), frameBmp);
		 * frameView.setBackgroundDrawable(drawable); this.addView(frameView); }
		 */
	}

	private void addContenView() {
		isInitData = true;
		RectF rectF = new RectF(viewWidth * fRects[0], viewHeight * fRects[1], viewWidth * fRects[2], viewHeight * fRects[3]);
		PIPContentView imageView = new PIPContentView(mContext, false);
		// bmpSrc = PJData.getShowBitmap().copy(Config.ARGB_8888, false);
		// 获取XY偏移及scale
		float[] blurVals = BitmapUtil.BmpInRect(bmpBlur, new RectF(0.0f, 0.0f, (float) viewWidth, (float) viewHeight));

		matrixBlur = new Matrix();

		matrixBlur.postScale(blurVals[2], blurVals[2]);
		matrixBlur.postTranslate(blurVals[0], blurVals[1]);

		// 获取XY偏移及scale
		float[] val2 = BitmapUtil.BmpInRect(bmpSrc, rectF);
		imageView.initData(bmpSrc, bmpShape, rectF, null);
		imageView.setStyle(val2[0], val2[1], val2[2], 0);
		RelativeLayout.LayoutParams imageViewLp = new RelativeLayout.LayoutParams((int) rectF.width(), (int) rectF.height());

		imageViewLp.topMargin = (int) rectF.top;
		imageViewLp.leftMargin = (int) rectF.left;
		imageView.setLayoutParams(imageViewLp);
		this.addViewInLayout(imageView, 0, imageView.getLayoutParams(), true);
		// this.addView(imageView, 0);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
		// lp.leftMargin = (width - viewWidth) / 2;
		// lp.topMargin = (height - viewHeight) / 2;
		ImageView frameView = new ImageView(mContext);
		if (BitmapUtil.isValid(frameBmp)) {
			BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(), frameBmp);
			frameView.setBackgroundDrawable(drawable);
		}

		frameView.setLayoutParams(lp);
		this.addViewInLayout(frameView, 1, frameView.getLayoutParams(), true);
		// this.addView(frameView, 1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// canvas.drawPaint(paint);
		if (BitmapUtil.isValid(bmpBlur)) {
			canvas.drawBitmap(bmpBlur, matrixBlur, bmpPaint);
		}

	}

	public Bitmap getSavePic() {
		float scale = 1f;
		Matrix matrix = null;
		if (this.getChildAt(0) instanceof PIPContentView) {
			matrix = ((PIPContentView) this.getChildAt(0)).getPicMatrix();
			if (this.getWidth() > this.getHeight()) {// 横构图 保存宽为900
				scale = 900f / this.getWidth();
			} else {// 竖型图 保存宽为640
				scale = 640f / this.getWidth();
			}
			matrix.postTranslate(fRects[0] * this.getWidth(), fRects[1] * this.getHeight());
			matrix.postScale(scale, scale);

		}
		Matrix shapeMatrix = new Matrix();
		shapeMatrix.postTranslate(fRects[0] * this.getWidth(), fRects[1] * this.getHeight());
		shapeMatrix.postScale(scale, scale);

		Bitmap bmp = Bitmap.createBitmap((int) (this.getWidth() * scale), (int) (this.getHeight() * scale), Config.ARGB_8888);

		Canvas canvas = new Canvas(bmp);
		Matrix blurMatrix = new Matrix();// 底图的Matrix
		blurMatrix.postConcat(matrixBlur);
		blurMatrix.postScale(scale, scale);

		if (BitmapUtil.isValid(bmpBlur)) {
			canvas.drawBitmap(bmpBlur, blurMatrix, bmpPaint);
		}

		canvas.drawColor(Color.TRANSPARENT);
		int sa = canvas.saveLayer(0, 0, bmp.getWidth(), bmp.getHeight(), null, Canvas.FULL_COLOR_LAYER_SAVE_FLAG
				| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.MATRIX_SAVE_FLAG);
		// canvas.drawPaint(paint);
		canvas.save();
		canvas.clipRect(new RectF(fRects[0] * bmp.getWidth(), fRects[1] * bmp.getHeight(), fRects[2] * bmp.getWidth(), fRects[3]
				* bmp.getHeight()));
		if (BitmapUtil.isValid(bmpSrc))
			canvas.drawBitmap(bmpSrc, matrix, bmpPaint);

		if (BitmapUtil.isValid(bmpShape))
			canvas.drawBitmap(bmpShape, shapeMatrix, shapePaint);
		canvas.restore();
		canvas.restoreToCount(sa);
		if (BitmapUtil.isValid(frameBmp))
			canvas.drawBitmap(frameBmp, null, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), bmpPaint);
		return bmp;

	}

	public void releaseView() {
		BitmapUtil.safeRecycled(bmpSrc);
		BitmapUtil.safeRecycled(frameBmp);
		BitmapUtil.safeRecycled(bmpShape);
		BitmapUtil.safeRecycled(bmpBlur);
	}

}
