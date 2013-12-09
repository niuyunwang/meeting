package com.zhsystem.meeting.Main;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyViewGroup extends ViewGroup {

	private View content_view;
	private View menu_view;	
	private Scroller scroller;
	public boolean isMenuOpen = false;
	private int distance ;
	private int duration = 500;
	
	public MyViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		scroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		menu_view = getChildAt(0);
		content_view = getChildAt(1);		
		content_view.measure(0, 0);
		content_view.layout(0, 0,getWidth(),getHeight());
		menu_view.measure(0, 0);
		menu_view.layout(-distance, 0, 0, getHeight());
		
		
	}

	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			postInvalidate();

		}
	}
	public void setDistance(int distance)
	{
		this.distance=distance;
	}
	
	public void showMenu()
	{
			isMenuOpen = true;
			Log.e("MY","in showMenu");
			scroller.startScroll(this.getScrollX(),0, -distance,0,duration);
			invalidate();
	}
	public void closeMenu()
	{

			isMenuOpen = false;
			scroller.startScroll(this.getScrollX(), 0, distance, 0,duration);
			invalidate();
	}
	
	public void slidingMenu() {

		if (getScrollX() > -getWidth() / 2) {
			scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, duration);
			isMenuOpen = false;
		}
		else if (getScrollX() <= -getWidth() / 2) {
			scroller.startScroll(getScrollX(), 0, -(distance + getScrollX()),
					0, duration);
			isMenuOpen = true;
		}
		invalidate();
	}


}
