package com.zhsystem.meeting.Main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

public class DateWidgetDayHeader extends View {
	private final static int fTextSize = 22;
	private Paint pt = new Paint();
	private RectF rect = new RectF();
	private int iWeekDay = -1;

	public DateWidgetDayHeader(Context context, int iWidth, int iHeight) {
		super(context);
		setLayoutParams(new LayoutParams(iWidth, iHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		rect.set(0, 0, this.getWidth(), this.getHeight());
		rect.inset(1, 1);
		drawDayHeader(canvas);
	}

	private void drawDayHeader(Canvas canvas) {
		pt.setColor(MainActivity.Calendar_WeekBgColor);
		canvas.drawRect(rect, pt);
		pt.setTypeface(null);
		pt.setTextSize(fTextSize);
		pt.setAntiAlias(true);
		pt.setFakeBoldText(true);
		pt.setColor(MainActivity.Calendar_WeekFontColor);
		final String sDayName = DayStyle.getWeekDayName(iWeekDay);
		final int iPosX = (int) rect.left + ((int) rect.width() >> 1)
				- ((int) pt.measureText(sDayName) >> 1);
		final int iPosY = (int) (this.getHeight()
				- (this.getHeight() - getTextHeight()) / 2 - pt
				.getFontMetrics().bottom);
		canvas.drawText(sDayName, iPosX, iPosY, pt);
	}
	private int getTextHeight() {
		return (int) (-pt.ascent() + pt.descent());
	}
	public void setData(int iWeekDay) {
		this.iWeekDay = iWeekDay;
	}
}