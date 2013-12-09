package com.zhsystem.meeting.Main;

import java.util.Calendar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;
public class DateWidgetDayCell extends View {

	private static final int fTextSize = 28;

	private OnItemClick itemClick = null;
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private String sDate = "";
	private int iDateYear = 0;
	private int iDateMonth = 0;
	private int iDateDay = 0;
	private boolean bSelected = false;
	private boolean bIsActiveMonth = false;
	private boolean bToday = false;
	private boolean bTouchedDown = false;
	private boolean bHoliday = false;
	private boolean hasRecord = false;

	public static int ANIM_ALPHA_DURATION = 100;

	public interface OnItemClick {
		public void OnClick(DateWidgetDayCell item);
	}

	public DateWidgetDayCell(Context context, int iWidth, int iHeight) {
		super(context);
		setFocusable(true);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
	}

	public Calendar getDate() {
		Calendar calDate = Calendar.getInstance();
		calDate.clear();
		calDate.set(Calendar.YEAR, iDateYear);
		calDate.set(Calendar.MONTH, iDateMonth);
		calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
		return calDate;
	}

	public void setData(int iYear, int iMonth, int iDay, Boolean bToday,
			Boolean bHoliday, int iActiveMonth, boolean hasRecord) {
		iDateYear = iYear;
		iDateMonth = iMonth;
		iDateDay = iDay;

		this.sDate = Integer.toString(iDateDay);
		this.bIsActiveMonth = (iDateMonth == iActiveMonth);
		this.bToday = bToday;
		this.bHoliday = bHoliday;
		this.hasRecord = hasRecord;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);
		
		final boolean bFocused = IsViewFocused();

		drawDayView(canvas, bFocused);
		drawDayNumber(canvas);
	}

	public boolean IsViewFocused() {
		return (this.isFocused() || bTouchedDown);
	}


	private void drawDayView(Canvas canvas, boolean bFocused) {

		if (bSelected || bFocused) {
			LinearGradient lGradBkg = null;

			if (bFocused) {
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						0xffaa5500, 0xffffddbb, Shader.TileMode.CLAMP);
			}

			if (bSelected) {
				lGradBkg = new LinearGradient(rect.left, 0, rect.right, 0,
						0xff225599, 0xffbbddff, Shader.TileMode.CLAMP);
			}

			if (lGradBkg != null) {
				pt.setShader(lGradBkg);
				canvas.drawRect(rect, pt);
			}

			pt.setShader(null);

		} else {
			pt.setColor(getColorBkg(bHoliday, bToday));
			canvas.drawRect(rect, pt);
		}

		if (hasRecord) {
			CreateReminder(canvas, MainActivity.special_Reminder);
		}
		// else if (!hasRecord && !bToday && !bSelected) {
		// CreateReminder(canvas, Calendar_TestActivity.Calendar_DayBgColor);
		// }
	}

	public void drawDayNumber(Canvas canvas) {
		// draw day number
		pt.setTypeface(null);
		pt.setAntiAlias(true);
		pt.setShader(null);
		pt.setFakeBoldText(true);
		pt.setTextSize(fTextSize);
		pt.setColor(MainActivity.isPresentMonth_FontColor);
		pt.setUnderlineText(false);
		
		if (!bIsActiveMonth)
			pt.setColor(MainActivity.unPresentMonth_FontColor);

		if (bToday)
			pt.setUnderlineText(true);

		final int iPosX = (int) rect.left + ((int) rect.width() >> 1)
				- ((int) pt.measureText(sDate) >> 1);

		final int iPosY = (int) (this.getHeight()
				- (this.getHeight() - getTextHeight()) / 2 - pt
				.getFontMetrics().bottom);

		canvas.drawText(sDate, iPosX, iPosY, pt);

		pt.setUnderlineText(false);
	}

	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}

	public static int getColorBkg(boolean bHoliday, boolean bToday) {
		if (bToday)
			return MainActivity.isToday_BgColor;
		// if (bHoliday) //������ĩ�����ⱳ��ɫ����ȥ��ע��
		// return Calendar_TestActivity.isHoliday_BgColor;
		return MainActivity.Calendar_DayBgColor;
	}

	@Override
	public void setSelected(boolean bEnable) {
		if (this.bSelected != bEnable) {
			this.bSelected = bEnable;
			this.invalidate();
		}
	}

	public void setItemClick(OnItemClick itemClick) {
		this.itemClick = itemClick;
	}

	public void doItemClick() {
		if (itemClick != null)
			itemClick.OnClick(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean bHandled = false;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			bHandled = true;
			bTouchedDown = true;
			invalidate();
			startAlphaAnimIn(DateWidgetDayCell.this);
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			bHandled = true;
			bTouchedDown = false;
			invalidate();
			doItemClick();
		}
		return bHandled;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bResult = super.onKeyDown(keyCode, event);
		if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
				|| (keyCode == KeyEvent.KEYCODE_ENTER)) {
			doItemClick();
		}
		return bResult;
	}

	public static void startAlphaAnimIn(View view) {
		AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
		anim.setDuration(ANIM_ALPHA_DURATION);
		anim.startNow();
		view.startAnimation(anim);
	}
	
	public void CreateReminder(Canvas canvas, int Color) {
		pt.setStyle(Paint.Style.FILL_AND_STROKE);
		pt.setColor(Color);
		Path path = new Path();
		path.moveTo(rect.right - rect.width() / 4, rect.top);
		path.lineTo(rect.right, rect.top);
		path.lineTo(rect.right, rect.top + rect.width() / 4);
		path.lineTo(rect.right - rect.width() / 4, rect.top);
		path.close();
		canvas.drawCircle(getWidth()/2, getHeight()/4+getHeight()/2+getHeight()/10, 5, pt);
		//canvas.drawPath(path, pt);
	}
}