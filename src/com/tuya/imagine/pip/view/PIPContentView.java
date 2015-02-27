package com.tuya.imagine.pip.view;

import com.tuya.baselib.util.BitmapUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class PIPContentView extends View {

	static final int NONE = 0;
	static final int DRAG = 1; // 拖动中
	static final int ZOOM = 2; // 缩放中
	static final int BIGGER = 3; // 放大ing
	static final int SMALLER = 4; // 缩小ing
	private int mode = NONE; // 当前的事件

	public Bitmap mTargetBmp = null;
	private Bitmap bmpShape = null;

	/**
	 * 操作目标bmp的宽高
	 */
	private int mTargetBmpWidth = 0;
	private int mTargetBmpHeight = 0;

	private Matrix mMatrix = null;// 图片矩阵

	private Point mPrePivot = null;// 当前操作的图片中心点

	private Point mLastPivot = null;// 上一次操作的图片中心点

	private Point mLastPoint = null; // 移动操作记录XY坐标

	private float[] mSrcPoint = null;// 初始载入图片的point[]

	private float[] mDestPoint = null;// 操作后的Point[]

	private float mPreDegree = 0f;//

	private float mLastDegree = 0f;

	private RectF mSrcRect = null;// 初始

	private RectF mDstRect = null;

	private float mDeltaX = 0;// 所需要移动的距离

	private float mDeltaY = 0;

	private float mScale = 1f;// 当前实时的Scale

	public float mDegree = 0f;

	public RectF mBoundRect = null;// 当前的格子layout
	private Path mBmpPath = null;// 当前可展示的Path
	
	private Paint shapePaint = new Paint(Paint.DITHER_FLAG| Paint.FILTER_BITMAP_FLAG);


	/***
	 * 缩放大小
	 */
	private float mScaleValue = 1f;
	/***
	 * 旋转角度
	 */
	private float mRotateDegreeValue = 0f;
	private float mLastDist = 1f;
	private float mPreDist = 1f;

	private PaintFlagsDrawFilter drawFilter = null;

	private Paint bmpPaint;

	private boolean isOnMeasrue = false;

	private boolean isOnLayout = false;

	private RectF mBmpSrcRect;// 载入的图片Rect

	private RectF mBmpDstRect;

	private RectF BoundRect;// 载入的格子Rect

	private RectF BmpRect;// 载入的图片Rect

	private Matrix mUnAngleMatrix;// 加载后记录的Matrix,但不记录角度，用于获取不带旋转的Rect

	private Matrix mAngleMatrix;// 只记录加载角度的Matrix，用于获取旋转后映射的Rect

	private boolean isRotate = true;// 传入参数是否为模板拼图 模板拼图不考虑旋转

	public PIPContentView(Context context, boolean isRotate) {
		super(context);
		bmpPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
		bmpPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		bmpPaint.setAntiAlias(true);
		shapePaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		this.isRotate = isRotate;
	}

	public void initData(Bitmap bmp,Bitmap bmpShape ,RectF rectF, Path path) {
		mTargetBmp = bmp;
		if(BitmapUtil.isValid(bmpShape))
		this.bmpShape = bmpShape;
		mBoundRect = rectF;
		if(path != null){
			mBmpPath = path;
		}
		
		// mBmpPath.set(path);
		mTargetBmpWidth = mTargetBmp.getWidth();
		mTargetBmpHeight = mTargetBmp.getHeight();

		mSrcPoint = new float[] { 0, 0, mTargetBmpWidth, 0, mTargetBmpWidth, mTargetBmpHeight, 0, mTargetBmpHeight,
				mTargetBmpWidth / 2, mTargetBmpHeight / 2 };

		mDestPoint = mSrcPoint.clone();
		mSrcRect = new RectF(0, 0, mTargetBmpWidth, mTargetBmpHeight);
		mDstRect = new RectF();
		mMatrix = new Matrix();

		mPrePivot = new Point(mTargetBmpWidth / 2, mTargetBmpHeight / 2);
		mLastPivot = new Point(mTargetBmpWidth / 2, mTargetBmpHeight / 2);
		mLastPoint = new Point(0, 0);

	}

	/**
	 * 设置样式
	 * 
	 * @param x
	 *            图片中心点的X偏移
	 * @param y
	 *            图片中心点的Y偏移
	 * @param scale
	 * @param angle
	 */
	public void setStyle(float x, float y, float scale, float angle) {
		BoundRect = new RectF(0, 0, mBoundRect.width(), mBoundRect.height());
		BmpRect = new RectF(x, y, mBoundRect.width() - x, mBoundRect.height() - y);
		mBmpSrcRect = new RectF();
		mBmpDstRect = new RectF();
		mUnAngleMatrix = new Matrix();
		mAngleMatrix = new Matrix();
		setScaleValue(scale, 0f, 0f, true);
		setTranslateValue(x, y);
		if (!isOnMeasrue) {
			setRotateDegreeValue(angle, 0f, 0f, false);
		}

		// mMatrix.mapRect(mBmpSrcRect, BmpRect);
		// invalidate();

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!isOnMeasrue) {
			isOnMeasrue = true;
			if (mBoundRect != null) {
				if (mBoundRect == null)
					return;
				setMeasuredDimension((int) mBoundRect.width(), (int) mBoundRect.height());
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (!isOnLayout) {
			isOnLayout = true;
			if (mBoundRect == null)
				return;
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) mBoundRect.width(),
					(int) mBoundRect.height());
			lp.leftMargin = (int) mBoundRect.left;
			lp.topMargin = (int) mBoundRect.top;
			this.setLayoutParams(lp);
		}
	}

	/**
	 * 更换图片
	 * 
	 * @param bmp
	 */
	public void reloadBitmap(Bitmap bmp) {
		mTargetBmp = bmp;
		mTargetBmpWidth = mTargetBmp.getWidth();
		mTargetBmpHeight = mTargetBmp.getHeight();
		mSrcPoint = new float[] { 0, 0, mTargetBmpWidth, 0, mTargetBmpWidth, mTargetBmpHeight, 0, mTargetBmpHeight,
				mTargetBmpWidth / 2, mTargetBmpHeight / 2 };
		mDestPoint = mSrcPoint.clone();
		mMatrix.reset();
		mUnAngleMatrix.reset();
		mAngleMatrix.reset();
		mPrePivot.set(mTargetBmpWidth / 2, mTargetBmpHeight / 2);
		mLastPivot.set(mTargetBmpWidth / 2, mTargetBmpHeight / 2);
		invalidate();
	}

	/**
	 * 更改版式
	 * 
	 * @param rectF
	 *            图片的格子
	 * @param path
	 *            展示的路径
	 */
	public void reloadFormat(RectF rectF, Path path) {
		mBoundRect.set(rectF);
		mBmpPath.set(path);
		isOnMeasrue = false;
		isOnLayout = false;
		this.requestLayout();
		// this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (drawFilter == null) {
			drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG
					| Paint.DITHER_FLAG|Paint.HINTING_ON);
			
		}
		canvas.setDrawFilter(drawFilter);
		canvas.drawColor(Color.TRANSPARENT);
		canvas.saveLayer(0,0,this.getWidth(),this.getHeight(), null,
                 Canvas.FULL_COLOR_LAYER_SAVE_FLAG 
                 |Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                 |Canvas.CLIP_SAVE_FLAG
                 |Canvas.CLIP_TO_LAYER_SAVE_FLAG
                 |Canvas.MATRIX_SAVE_FLAG
       		  );
		if (mBmpPath != null) {
			if(BitmapUtil.isValid(mTargetBmp)){
				canvas.save();
				canvas.clipPath(mBmpPath, Region.Op.INTERSECT);
				canvas.drawBitmap(mTargetBmp, mMatrix, bmpPaint);
				canvas.restore();
			}
		}else{
			if(BitmapUtil.isValid(mTargetBmp)) {
				canvas.drawBitmap(mTargetBmp, mMatrix, bmpPaint);
			}
			
			if(BitmapUtil.isValid(bmpShape)) {
				canvas.drawBitmap(bmpShape, 0, 0, shapePaint);
			}
			
		}
	}

	private boolean isInPath(Path paramPath, float x, float y) {
		RectF localRectF = new RectF();
		paramPath.computeBounds(localRectF, true);
		Region localRegion = new Region();

		localRegion.setPath(paramPath, new Region((int) localRectF.left, (int) localRectF.top, (int) localRectF.right,
				(int) localRectF.bottom));
		return localRegion.contains((int) x, (int) y);
	}

	/**
	 * 获取当前的可展示图片的Path
	 * 
	 * @return
	 */
	public Path getBmpPath() {
		Path path = new Path();
		mBmpPath.transform(mMatrix, path);
		return path;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {

		case MotionEvent.ACTION_DOWN:
			// if(!isInFrameRect(x,y)){
			if(mBmpPath!= null){
				if (!isInPath(mBmpPath, x, y)) {
					return false;
				}
			}
			
			// this.getParent().bringChildToFront(this);
//			((View) this.getParent()).invalidate();
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mLastDist = spacing(event);// 计算两手指间的距离
			mLastDegree = computeDegree(new Point((int) event.getX(0), (int) event.getY(0)),
					new Point((int) event.getX(1), (int) event.getY(1)));
			mode = ZOOM;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				translate(x, y);// 单指移动
			} else if (mode == ZOOM) {
				if (!isRotate)
					rotate(event);
				scale(event);
			}
			break;

		default:
			break;
		}
		// this.setImageMatrix(mMatrix);
		mLastPoint.x = x;
		mLastPoint.y = y;
		invalidate();
		return true;
	}

	private void setTranslateValue(float x, float y) {

		Log.d("test", "x = " + x + "y = " + y);
		mDeltaX = x;
		mDeltaY = y;

		mMatrix.postTranslate(mDeltaX, mDeltaY);

		mMatrix.mapPoints(mDestPoint, mSrcPoint);
		mMatrix.mapRect(mDstRect, mSrcRect);

		// this.clearAnimation();
	}

	private void setScaleValue(float scale, float centerX, float centerY, boolean isScaleByCenterXY) {
		Log.d("scale", "scale = " + scale);
		mScale = scale;
		mScaleValue = scale;
		if (isScaleByCenterXY) {
			mMatrix.postScale(mScaleValue, mScaleValue, centerX, centerY);
		} else {
			mMatrix.postScale(mScaleValue, mScaleValue, mDestPoint[8], mDestPoint[9]);
		}
		mMatrix.mapPoints(mDestPoint, mSrcPoint);

		mMatrix.mapRect(mDstRect, mSrcRect);
	}

	/***
	 * 旋转
	 * 
	 * @param degree
	 * @param centerX
	 * @param centerY
	 * @param isRotateByCenterXY
	 */
	private void setRotateDegreeValue(float degree, float centerX, float centerY, boolean isRotateByCenterXY) {
		mRotateDegreeValue = degree;
		mDegree = degree;
		if (isRotateByCenterXY) {
			mMatrix.postRotate(mRotateDegreeValue, centerX, centerY);
			mAngleMatrix.postRotate(mRotateDegreeValue, centerX, centerY);
		} else {
			mMatrix.postRotate(mRotateDegreeValue, mDestPoint[8], mDestPoint[9]);
			mAngleMatrix.postRotate(mRotateDegreeValue, mDestPoint[8], mDestPoint[9]);
		}
		mMatrix.mapPoints(mDestPoint, mSrcPoint);
		mMatrix.mapRect(mDstRect, mSrcRect);
		mAngleMatrix.mapRect(mBmpSrcRect, BoundRect);
		// this.setImageMatrix(mMatrix);
		// invalidate();
	}

	/**
	 * 切换板式并执行动画
	 * 
	 * @param x
	 *            偏移
	 * @param y
	 *            偏移
	 * @param scale
	 * @param angle
	 */
	public void postChangeStyle(float x, float y, float scale, float angle) {

		mScaleValue = 1.0f / mScale * scale;// 先还原到原图比例再乘当前比例
		mScale = scale;
		Log.d("scale", "mScaleValue2 = " + mScaleValue);

		mRotateDegreeValue = -mDegree + angle;// 先还原到原图角度再加上当前角度
		mDegree = angle;
		Log.d("degree", "mRotateDegreeValue1 = " + mRotateDegreeValue);
		if (Math.abs(mRotateDegreeValue) > 180) {
			if (mRotateDegreeValue > 0) {
				mRotateDegreeValue = mRotateDegreeValue - 360;
			} else {
				mRotateDegreeValue = 360 + mRotateDegreeValue;
			}
		} else {
		}
		setScale(mScaleValue, mRotateDegreeValue);// 先以原点进行缩放
		mDeltaX = x - mDestPoint[0];// 获取偏移
		mDeltaY = y - mDestPoint[1];

		setTranslate(mDeltaX, mDeltaY);
	}

	private void setTranslate(float mDeltaX, float mDeltaY) {
		mMatrix.postTranslate(mDeltaX, mDeltaY);// 移动
		mUnAngleMatrix.postTranslate(mDeltaX, mDeltaY);
		mMatrix.mapPoints(mDestPoint, mSrcPoint);
		mMatrix.mapRect(mDstRect, mSrcRect);
		this.invalidate();

	}

	private void setScale(float scale, float degree) {
		mMatrix.postScale(scale, scale, mDestPoint[8], mDestPoint[9]);
		mUnAngleMatrix.postScale(scale, scale, mDestPoint[8], mDestPoint[9]);
		mMatrix.mapPoints(mDestPoint, mSrcPoint);
		mMatrix.mapRect(mDstRect, mSrcRect);
		mMatrix.postRotate(degree, mDestPoint[8], mDestPoint[9]);
		mAngleMatrix.postRotate(degree, mDestPoint[8], mDestPoint[9]);
		mMatrix.mapPoints(mDestPoint, mSrcPoint);
		mMatrix.mapRect(mDstRect, mSrcRect);
		// ItemView.this.setImageMatrix(mMatrix);
		PIPContentView.this.invalidate();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1:
				PIPContentView.this.invalidate();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public void translate(int x, int y) {
		// 临时记录的数据
		Matrix tempMatrix = new Matrix(mMatrix);
		Matrix tempUnAngleMatrix = new Matrix(mUnAngleMatrix);
		int midx = mPrePivot.x, midy = mPrePivot.y;

		midx += x - mLastPoint.x;
		midy += y - mLastPoint.y;
		// mPrePivot.x += x - mLastPoint.x;
		// mPrePivot.y += y - mLastPoint.y;
		// mDeltaX = mPrePivot.x - mLastPivot.x;
		// mDeltaY = mPrePivot.y - mLastPivot.y;
		mDeltaX = midx - mLastPivot.x;
		mDeltaY = midy - mLastPivot.y;
		tempMatrix.postTranslate(mDeltaX, mDeltaY);
		tempUnAngleMatrix.postTranslate(mDeltaX, mDeltaY);

		tempUnAngleMatrix.mapRect(mBmpDstRect, BmpRect);
		mAngleMatrix.mapRect(mBmpSrcRect, BoundRect);

		if (mBmpDstRect.contains(mBmpSrcRect) || !isRotate) {
			Log.d("contain", "translate-------------true!!!!!!!!!!!!!!!!!!!!");
			mMatrix.set(tempMatrix);
			mUnAngleMatrix.set(tempUnAngleMatrix);
			mMatrix.mapRect(mDstRect, mSrcRect);
			mMatrix.mapPoints(mDestPoint, mSrcPoint);
			mPrePivot.x = midx;
			mPrePivot.y = midy;
			mLastPivot.x = mPrePivot.x;
			mLastPivot.y = mPrePivot.y;
		} else {
			Log.d("contain", "translate-------------false~~~~~~~~~~~");
		}
	}

	public void rotate(MotionEvent event) {
		// 临时记录的数据
		Matrix tempMatrix = new Matrix(mMatrix);
		Matrix tempAngleMatrix = new Matrix(mAngleMatrix);
		mPreDegree = computeDegree(new Point((int) event.getX(0), (int) event.getY(0)), new Point((int) event.getX(1),
				(int) event.getY(1)));
		// setMatrix(OPT_ROTATE);
		tempMatrix.postRotate(mPreDegree - mLastDegree, mDestPoint[8], mDestPoint[9]);
		tempAngleMatrix.postRotate(mPreDegree - mLastDegree, mDestPoint[8], mDestPoint[9]);

		mUnAngleMatrix.mapRect(mBmpDstRect, BmpRect);
		tempAngleMatrix.mapRect(mBmpSrcRect, BoundRect);
		if (mBmpDstRect.contains(mBmpSrcRect) || !isRotate) {
			Log.d("contain", "rotate-------------true!!!!!!!!!!!!!!!!!!!!");
			mMatrix.set(tempMatrix);
			mAngleMatrix.set(tempAngleMatrix);
			mMatrix.mapRect(mDstRect, mSrcRect);
			mMatrix.mapPoints(mDestPoint, mSrcPoint);
			mDegree = (mDegree + (mPreDegree - mLastDegree)) % 360;
			mLastDegree = mPreDegree;
		} else {
			Log.d("contain", "rotate-------------false~~~~~~~~~~~");
		}
	}

	public void scale(MotionEvent event) {

		mPreDist = spacing(event);
		mScaleValue = 1 + (mPreDist - mLastDist) / 200;
		/*
		 * if (mScaleValue < mInitialMiniScale / mScale) {// 限定最小的比例 0.5
		 * mScaleValue = mInitialMiniScale / mScale; }
		 */
		// 临时记录的数据
		Matrix tempMatrix = new Matrix(mMatrix);
		Matrix tempUnAngleMatrix = new Matrix(mUnAngleMatrix);

		tempMatrix.postScale(mScaleValue, mScaleValue, mDestPoint[8], mDestPoint[9]);
		tempUnAngleMatrix.postScale(mScaleValue, mScaleValue, mDestPoint[8], mDestPoint[9]);

		tempUnAngleMatrix.mapRect(mBmpDstRect, BoundRect);
		mAngleMatrix.mapRect(mBmpSrcRect, BoundRect);

		if (mBmpDstRect.contains(mBmpSrcRect) || !isRotate) {
			Log.d("contain", "scale-------------true!!!!!!!!!!!!!!!!!!!!");
			mMatrix.set(tempMatrix);
			mUnAngleMatrix.set(tempUnAngleMatrix);
			mMatrix.mapRect(mDstRect, mSrcRect);
			mMatrix.mapPoints(mDestPoint, mSrcPoint);
			mScale = mScale * mScaleValue;
			mLastDist = mPreDist;
		} else {
			Log.d("contain", "scale-------------false~~~~~~~~~~~");
		}

	}

	public float computeDegree(Point p1, Point p2) {
		float tran_x = p1.x - p2.x;
		float tran_y = p1.y - p2.y;
		float degree = 0.0f;
		float angle = (float) (Math.asin(tran_x / Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);
		if (!Float.isNaN(angle)) {
			if (tran_x >= 0 && tran_y <= 0) {// 第一象限
				degree = angle;
			} else if (tran_x <= 0 && tran_y <= 0) {// 第二象限
				degree = angle;
			} else if (tran_x <= 0 && tran_y >= 0) {// 第三象限
				degree = -180 - angle;
			} else if (tran_x >= 0 && tran_y >= 0) {// 第四象限
				degree = 180 - angle;
			}
		}
		return degree;
	}

	private boolean isInFrameRect(int x, int y) {
		boolean isInRect = false;

		if ((isPointUpLine(mDestPoint[0], mDestPoint[1], mDestPoint[2], mDestPoint[3], x, y) ^ (isPointUpLine(
				mDestPoint[4], mDestPoint[5], mDestPoint[6], mDestPoint[7], x, y)))
				&& (isPointUpLine(mDestPoint[2], mDestPoint[3], mDestPoint[4], mDestPoint[5], x, y) ^ (isPointUpLine(
						mDestPoint[6], mDestPoint[7], mDestPoint[0], mDestPoint[1], x, y)))) {
			isInRect = true;
		}

		return isInRect;

	}

	private boolean isPointUpLine(float x1, float y1, float x2, float y2, float x, float y) {
		float n = 0;
		if (x1 == x2) {
			if (x > x1) {
				return false;
			} else {
				return true;
			}
		} else {
			n = y1 - (x1 - x) * (y1 - y2) / (x1 - x2);
			if (n > y) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 计算两手指间的距离
	 * 
	 * @param event
	 * @return
	 */
	private float spacing(MotionEvent event) {
		try {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		} catch (Exception e) {
			return 0;
		}
	}

	public void destroy() {
		if (mTargetBmp != null && !mTargetBmp.isRecycled())
			mTargetBmp.recycle();
		// setImageBitmap(null);
	}
	
	/**
	 * 获取当前Bitmap的Matrix
	 * @return
	 */
	public Matrix getPicMatrix(){
		return mMatrix;
	}

}
