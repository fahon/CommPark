package w.song.orchid.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 和DrawerLayout一样，但写法更好，注意不能为这个布局添加背景色 发现问题，DrawerLayoutB添加背景色后滑动或有叠影现象
 * 2013-8-1 这里做了原状态和执行后的状态的统一处理，没有了刚执行就变drawerState状态的情况，避免了混乱。
 * 
 * @author songwei
 * @date 2013-5-3 下午11:00:29
 */
public class ODrawerLayoutB extends OBaseLinearLayout {
	private String TAG = "DrawerLayoutB";
	/** 运动持续时间 单位 秒 */
	private float durationTime = 0.2F;

	/**
	 * 获取 运动持续时间
	 * 
	 * @return 单位秒
	 */
	public float getDurationTime() {
		return durationTime;
	}

	/**
	 * 设置运动持续的时间
	 * 
	 * @param durationTime
	 *            单位 秒
	 */
	public void setDurationTime(float durationTime) {
		this.durationTime = durationTime;
	}

	/** 非隐藏部分的宽度(这里指的滑块) 单位px 不可与sideWidth连用*/
	private float sideShowWidth = 0.0f;
	/** 隐藏部分的宽度(这里指的滑块) 单位px 不可与sideShowWidth连用*/
	private float sideWidth = 0.0f;

	public float getSideShowWidth() {
		return sideShowWidth;
	}

	/**
	 * 设置抽屉外body宽度，不要和setSideWidth连用
	 * @param sideShowWidth
	 */
	public void setSideShowWidth(float sideShowWidth) {
		this.sideShowWidth = sideShowWidth;
	}
	
	/**
	 * 设置抽屉内宽度，不要和setSideShowWidth连用
	 * @param sideShowWidth
	 */
	public void setSideWidth(float sideWidth) {
		this.sideWidth = sideWidth;
	}

	/** 运动状态 */
	private boolean moveState = false;// 默认为静止状态

	public boolean isMoveState() {
		return moveState;
	}

	public void setMoveState(boolean moveState) {
		this.moveState = moveState;
	}

	/** 滑动工具 */
	private Scroller scroller;

	public static enum Orientation {
		left, top, right, bottom;
	}

	/** 控件收缩的方向 */
	public Orientation mOrientation = Orientation.left;

	public Orientation getmOrientation() {
		return mOrientation;
	}

	public void setmOrientation(Orientation mOrientation) {
		this.mOrientation = mOrientation;
	}

	private int parentViewPaddingLeft = 0;
	private int parentViewPaddingRight = 0;
	private RelativeLayout parentRL = null;
	/** 启动时的初始布局 */
	private android.widget.RelativeLayout.LayoutParams lp;

	public ODrawerLayoutB(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (parentRL == null) {
			parentRL = (RelativeLayout) getParent();
		}
		if (parentRL != null) {
			parentViewPaddingLeft = ((RelativeLayout) getParent()).getPaddingLeft();
			parentViewPaddingRight = ((RelativeLayout) getParent()).getPaddingRight();
		}
		
		sideShowWidth=getWidth()-sideWidth;

	}

	/**
	 * DrawerState的值
	 * 
	 * @param 0.0 抽屉已经打开（滑块伸出为打开）
	 * @param 0.1 抽屉已经关闭
	 */
	public final static int[] DRAWERSTATEVALUE = { 0, 1 };
	/** 抽屉打开或者关闭状态 */
	private int drawerState = DRAWERSTATEVALUE[0];// 初始化时候认定为打开状态

	public int getDrawerState() {
		return drawerState;
	}

	public void setDrawerState(int drawerState) {
		this.drawerState = drawerState;
	}

	private boolean gesture = true;

	public boolean isGesture() {
		return gesture;
	}

	public void setGesture(boolean gesture) {
		this.gesture = gesture;
	}

	private AnimationStatusListener animationStatusListener;

	public AnimationStatusListener getAnimationStatusListener() {
		return animationStatusListener;
	}

	public void setAnimationStatusListener(AnimationStatusListener animationStatusListener) {
		this.animationStatusListener = animationStatusListener;
	}

	/**
	 * flashSort的字段
	 * 
	 * @param 0.line 直线匀速运动
	 * @param 1.bounce 动画结束的时候弹起
	 * @param 2.accelerate 在动画开始的地方速率改变比较慢，然后开始加速
	 *        (开启手势后，不推荐使用这个加速滑动，因为速度的不衔接，松手时候会感觉卡顿)
	 * @param 3.decelerate 在动画开始的地方快然后慢（开启手势后，强烈推荐这个减速滑动）
	 */
	public final static String[] FLASHSORT_FILED = { "line", "bounce", "accelerate", "decelerate" };
	private String flashSort = FLASHSORT_FILED[3];// 动画的类型

	public String getFlashSort() {
		return flashSort;
	}

	/**
	 * 设置动画效果类型
	 * 
	 * @param flashSort
	 */
	public void setFlashSort(String flashSort) {
		this.flashSort = flashSort;
	}

	public void drawerDoNoAnim() {
		if (moveState)// 运动状态时防止重复运动
			return;

		switch (mOrientation) {
		case left:
			if (DRAWERSTATEVALUE[0] == drawerState) {// 执行动作前抽屉为打开
				new MoveAnimationListener().onAnimationEnd(null);
			} else if (DRAWERSTATEVALUE[1] == drawerState) {// 执行动作前抽屉为关闭
				new MoveAnimationListener().onAnimationEnd(null);

			} else {
				// 什么都不做
			}
			break;
		case top:
			break;
		case right:

			if (DRAWERSTATEVALUE[0] == drawerState) {// 执行动作前抽屉为打开
				drawerState = DRAWERSTATEVALUE[1];
				new MoveAnimationListener().onAnimationEnd(null);

			} else if (DRAWERSTATEVALUE[1] == drawerState) {// 执行动作前抽屉为关闭

				drawerState = DRAWERSTATEVALUE[0];
				new MoveAnimationListener().onAnimationEnd(null);

			} else {
				// 什么都不做
			}
			break;
		case bottom:
			break;
		default:
			break;
		}
	}

	/**
	 * 执行抽屉动画
	 */
	public void drawerDo() {
		if (moveState)// 运动状态时防止重复运动
			return;

		switch (mOrientation) {
		case left:
			if (DRAWERSTATEVALUE[0] == drawerState) {// 执行动作前抽屉为打开
				TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, -(getWidth() - sideShowWidth
						+ getLeft() - parentViewPaddingLeft), 0.0F, 0.0F);
				localTranslateAnimation.setDuration((long) (1000 * durationTime));
				if (FLASHSORT_FILED[0].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				} else if (FLASHSORT_FILED[1].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new BounceInterpolator());
				} else if (FLASHSORT_FILED[2].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new AccelerateInterpolator());
				} else if (FLASHSORT_FILED[3].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new DecelerateInterpolator());
				} else {// 默认直线运动
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				}

				localTranslateAnimation.setAnimationListener(new MoveAnimationListener());

				startAnimation(localTranslateAnimation);
				// drawerState = DRAWERSTATEVALUE[1];

			} else if (DRAWERSTATEVALUE[1] == drawerState) {// 执行动作前抽屉为关闭
				TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, -getLeft()
						+ parentViewPaddingLeft, 0.0F, 0.0F);
				localTranslateAnimation.setDuration((long) (1000 * durationTime));
				if (FLASHSORT_FILED[0].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				} else if (FLASHSORT_FILED[1].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new BounceInterpolator());
				} else if (FLASHSORT_FILED[2].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new AccelerateInterpolator());
				} else if (FLASHSORT_FILED[3].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new DecelerateInterpolator());
				} else {// 默认直线运动
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				}
				localTranslateAnimation.setAnimationListener(new MoveAnimationListener());

				startAnimation(localTranslateAnimation);
				// drawerState = DRAWERSTATEVALUE[0];

			} else {
				// 什么都不做
			}
			break;
		case top:
			break;
		case right:

			if (DRAWERSTATEVALUE[0] == drawerState) {// 执行动作前抽屉为打开
				TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, parentRL.getWidth() - getLeft()
						- sideShowWidth - parentViewPaddingRight, 0.0F, 0.0F);
				localTranslateAnimation.setDuration((long) (1000 * durationTime));
				if (FLASHSORT_FILED[0].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				} else if (FLASHSORT_FILED[1].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new BounceInterpolator());
				} else if (FLASHSORT_FILED[2].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new AccelerateInterpolator());
				} else if (FLASHSORT_FILED[3].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new DecelerateInterpolator());
				} else {// 默认直线运动
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				}

				localTranslateAnimation.setAnimationListener(new MoveAnimationListener());

				startAnimation(localTranslateAnimation);
				// drawerState = DRAWERSTATEVALUE[1];

			} else if (DRAWERSTATEVALUE[1] == drawerState) {// 执行动作前抽屉为关闭
				TranslateAnimation localTranslateAnimation = new TranslateAnimation(0, -getLeft()
						+ parentViewPaddingLeft, 0.0F, 0.0F);
				localTranslateAnimation.setDuration((long) (1000 * durationTime));
				if (FLASHSORT_FILED[0].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				} else if (FLASHSORT_FILED[1].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new BounceInterpolator());
				} else if (FLASHSORT_FILED[2].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new AccelerateInterpolator());
				} else if (FLASHSORT_FILED[3].equals(flashSort)) {
					localTranslateAnimation.setInterpolator(new DecelerateInterpolator());
				} else {// 默认直线运动
					localTranslateAnimation.setInterpolator(new LinearInterpolator());
				}
				localTranslateAnimation.setAnimationListener(new MoveAnimationListener());

				startAnimation(localTranslateAnimation);
				// drawerState = DRAWERSTATEVALUE[0];

			} else {
				// 什么都不做
			}
			break;
		case bottom:
			break;
		default:
			break;
		}

	}

	float startRawX = 0f;
	/** 最近一次的水平位置 */
	float lastRawX = 0f;
	/** 水平微距 */
	float Dx;
	// /** 水平位移 */
	// float dX;
	/** 速度跟踪器 */
	VelocityTracker mVelocityTracker;
	float matchV = 100;// 用于判断甩手的动作

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!gesture) {
			return false;
		}
		if (moveState)// 运动状态时防止重复运动
		{
			return false;
		}

		super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (this.mVelocityTracker == null) {
				this.mVelocityTracker = VelocityTracker.obtain();
				this.mVelocityTracker.addMovement(event);
			}
			startRawX = event.getRawX();
			lastRawX = startRawX;
			break;
		case MotionEvent.ACTION_MOVE:

			if (this.mVelocityTracker != null) {
				this.mVelocityTracker.addMovement(event);
				this.mVelocityTracker.computeCurrentVelocity(1000);
			}

			// 水平滑动
			float currX = event.getRawX();
			Dx = lastRawX - currX;
			lastRawX = currX;

			switch (mOrientation) {
			case left:

				if (getLeft() >= parentViewPaddingLeft) {// 防止右过界(边界情况处理)
					if (Dx < 0) {
						layout(parentViewPaddingLeft, getTop(), parentViewPaddingLeft + getWidth(), getBottom());// 误差校正
						return true;
					}
				} else if (getLeft() <= -(getWidth() - sideShowWidth - parentViewPaddingLeft)) {// 防止左过界(边界情况处理)
					if (Dx > 0) {
						layout(-(int) (getWidth() - sideShowWidth - parentViewPaddingLeft), getTop(),
								(int) (sideShowWidth + parentViewPaddingLeft), getBottom());// 误差校正
						return true;
					}
				}

				break;
			case top:
				break;
			case right:
				if (getLeft() <= parentViewPaddingLeft) {// 防止左过界(边界情况处理)
					if (Dx > 0) {
						layout(parentViewPaddingLeft, getTop(), getWidth() + parentViewPaddingLeft, getBottom());// 误差校正
						return true;
					}
				} else if (getLeft() >= (getWidth() - sideShowWidth + parentViewPaddingLeft)) {// 防止右过界(边界情况处理)
					if (Dx < 0) {
						layout((int) (getWidth() - sideShowWidth + parentViewPaddingLeft), getTop(), (int) (2
								* getWidth() - sideShowWidth + parentViewPaddingLeft), getBottom());// 误差校正
						return true;
					}
				}
				break;
			case bottom:
				break;
			default:
				break;
			}

			layout(getLeft() - (int) Dx, getTop(), getRight() - (int) Dx, getBottom());

			break;

		default:

			float vXD = 0;

			if (this.mVelocityTracker != null) {
				vXD = this.mVelocityTracker.getXVelocity();
				this.mVelocityTracker.recycle();
				this.mVelocityTracker = null;
			}

			if (Math.abs(vXD) > matchV) {// 甩手动作
				float dU = event.getRawX() - startRawX;
				switch (mOrientation) {
				case left:
					if (dU > 0) {// 向右甩
						drawerState = DRAWERSTATEVALUE[1];
						drawerDo();
					} else if (dU < 0) {// 向左甩
						drawerState = DRAWERSTATEVALUE[0];
						drawerDo();
					} else {// 此处判断成普通滑动
						if (getRight() > (getWidth() - sideShowWidth) / 2 + sideShowWidth + parentViewPaddingLeft) {// 确定为左运动
							drawerState = DRAWERSTATEVALUE[1];
							drawerDo();
						} else {// 确定为右运动
							drawerState = DRAWERSTATEVALUE[0];
							drawerDo();
						}
					}
					break;
				case top:
					break;
				case right:
					if (dU > 0) {// 向右甩
						drawerState = DRAWERSTATEVALUE[0];
						drawerDo();
					} else if (dU < 0) {// 向左甩
						drawerState = DRAWERSTATEVALUE[1];
						drawerDo();
					} else {// 此处判断成普通滑动
						if (getLeft() < (getWidth() - sideShowWidth) / 2 + parentViewPaddingLeft) {// 确定为右运动
							drawerState = DRAWERSTATEVALUE[0];
							drawerDo();
						} else {// 确定为左运动
							drawerState = DRAWERSTATEVALUE[1];
							drawerDo();
						}
					}
					break;
				case bottom:
					break;
				default:
					break;
				}

				break;
			} else {// 滑动判断
				switch (mOrientation) {
				case left:
					if (getRight() > (getWidth() - sideShowWidth) / 2 + sideShowWidth + parentViewPaddingLeft) {// 确定为左运动
						drawerState = DRAWERSTATEVALUE[1];
						drawerDo();
					} else {// 确定为右运动
						drawerState = DRAWERSTATEVALUE[0];
						drawerDo();
					}
					break;
				case top:
					break;
				case right:
					if (getLeft() > (getWidth() - sideShowWidth) / 2 + parentViewPaddingLeft) {// 确定为右运动
						drawerState = DRAWERSTATEVALUE[0];
						drawerDo();
					} else {// 确定为左运动
						drawerState = DRAWERSTATEVALUE[1];
						drawerDo();
					}
					break;
				case bottom:
					break;
				default:
					break;
				}

				break;

			}
		}
		return true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		boolean boo = super.dispatchTouchEvent(paramMotionEvent);
		switch (paramMotionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onTouchEvent(paramMotionEvent);
			break;
		}

		return boo;
	}

	/** 速度跟踪器 */
	private VelocityTracker mVelocityTrackerB;
	/**
	 * orientation的值
	 * 
	 * @param 0.0 方向不确定
	 * @param 0.1 左右滑动
	 * @param 0.2 上下滑动
	 */
	static private int[] ORIENTATION_VALUE = { 0, 1, 2 };
	private int orientation = ORIENTATION_VALUE[0]; // 默认方向不确定
	private float vx = 0;
	private float vy = 0;
	private float matchV2 = 300;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		// Log.d("orientation", "orientation=" + orientation);
		boolean boo = super.onInterceptTouchEvent(paramMotionEvent);
		// Log.d(TAG, "onInterceptTouchEvent，，" + paramMotionEvent.getAction());
		switch (paramMotionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (this.mVelocityTrackerB == null) {
				this.mVelocityTrackerB = VelocityTracker.obtain();
				this.mVelocityTrackerB.addMovement(paramMotionEvent);
			}
			orientation = ORIENTATION_VALUE[0];
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.d("orientation", "orientation=" + orientation);
			if (this.mVelocityTrackerB != null) {
				this.mVelocityTrackerB.addMovement(paramMotionEvent);
				this.mVelocityTrackerB.computeCurrentVelocity(1000);
				vx = Math.abs(mVelocityTrackerB.getXVelocity());
				vy = Math.abs(mVelocityTrackerB.getYVelocity());
			}

			// Log.d("orientation", "orientation=" + vx + "," + vy);
			if (vx > vy && vx > matchV2) {// 判断为左右滑动
				orientation = ORIENTATION_VALUE[1];
			} else if (vx < vy && vy > matchV2) {// 判断为上下滑动
				orientation = ORIENTATION_VALUE[2];
			} else {// 方向不确定
				orientation = ORIENTATION_VALUE[0];
			}
			break;
		case MotionEvent.ACTION_UP:
			if (this.mVelocityTrackerB != null) {
				this.mVelocityTrackerB.recycle();
				this.mVelocityTrackerB = null;
			}
			orientation = ORIENTATION_VALUE[0]; // 恢复方向不确定
			break;
		case MotionEvent.ACTION_CANCEL:
			if (this.mVelocityTrackerB != null) {
				this.mVelocityTrackerB.recycle();
				this.mVelocityTrackerB = null;
			}
			orientation = ORIENTATION_VALUE[0]; // 恢复方向不确定
			break;
		default:
			if (this.mVelocityTrackerB != null) {
				this.mVelocityTrackerB.recycle();
				this.mVelocityTrackerB = null;
			}
			break;
		}

		if (ORIENTATION_VALUE[1] == orientation) {// 水平方向
			return true;
		} else {// 其它方向
			return false;
		}

	}

	class MoveAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			moveState = false;
			ODrawerLayoutB.this.clearAnimation();
			switch (mOrientation) {
			case left:
				if (DRAWERSTATEVALUE[1] == drawerState) {// 原状态关闭，现在打开
					android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
							getLayoutParams());
					lp.leftMargin = 0;
					lp.rightMargin = 0;
					setLayoutParams(lp);
					drawerState = DRAWERSTATEVALUE[0];
					// layout(0, 0, getWidth(), getHeight());
				} else if (DRAWERSTATEVALUE[0] == drawerState) {
					android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
							getLayoutParams());
					lp.leftMargin = -(int) (getWidth() - sideShowWidth);
					lp.rightMargin = (int) (getWidth() - sideShowWidth);
					setLayoutParams(lp);
					// layout((int) (getWidth() - sideShowWidth), 0, getWidth()
					// + (int) (getWidth() - sideShowWidth), getHeight());
					drawerState = DRAWERSTATEVALUE[1];
				} else {
					// 什么都不做
				}
//				moveState = false;
				if (animationStatusListener != null) {
					animationStatusListener.onStop(drawerState);
				}
				break;
			case top:
				break;
			case right:
				if (DRAWERSTATEVALUE[1] == drawerState) {
					android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
							getLayoutParams());
					lp.leftMargin = 0;
					lp.rightMargin = 0;
					setLayoutParams(lp);
					// layout(0, 0, getWidth(), getHeight());
					drawerState = DRAWERSTATEVALUE[0];
				} else if (DRAWERSTATEVALUE[0] == drawerState) {
					android.widget.RelativeLayout.LayoutParams lp = new android.widget.RelativeLayout.LayoutParams(
							getLayoutParams());
					lp.leftMargin = (int) (getWidth() - sideShowWidth);
					lp.rightMargin = -(int) (getWidth() - sideShowWidth);
					setLayoutParams(lp);
					// layout((int) (getWidth() - sideShowWidth), 0, getWidth()
					// + (int) (getWidth() - sideShowWidth), getHeight());
					drawerState = DRAWERSTATEVALUE[1];
				} else {
					// 什么都不做
				}
//				moveState = false;
				if (animationStatusListener != null) {
					animationStatusListener.onStop(drawerState);
				}
				break;
			case bottom:
				break;
			default:
				break;
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
			moveState = true;
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					moveState=false;
				}
			}, (long) (1000*durationTime));
		}

	}

	public interface AnimationStatusListener {
		/**
		 * 抽屉关闭与开启状态
		 * 
		 * @param drawerState
		 *            值参考 DrawerLayoutB.DRAWERSTATEVALUE
		 */
		public void onStop(int drawerState);
	}

	@Override
	public void getParams() {
	}

	@Override
	public void init(Context context, AttributeSet attrs) {
	}

	@Override
	public void getComponent() {

	}

	@Override
	public void setView() {
	}

	@Override
	public void setListener() {
	}

	@Override
	public void last() {
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	

}
