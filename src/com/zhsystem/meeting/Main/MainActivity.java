package com.zhsystem.meeting.Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.zhsystem.meeting.Adapter.MenuListAdapter;
import com.zhsystem.meeting.Bean.MenuBean;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnGestureListener,OnClickListener {
	private MyViewGroup myViewGroup;
	private LayoutInflater inflater;
	private View main_view;
	private View menu_view;
	private GestureDetector gestureDetector;
	private ViewTreeObserver viewTreeObserver;
	private boolean hasMeasured = false;
	private int iv_button_width, window_windth;
	private int distance;
	private boolean isScrolling = false;
	private boolean processFlag = true;// 用于防止用户多次点击
	// Menu--------------------
	private boolean taday_select = true;
	private LinearLayout layContent = null;
	private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();

	public static Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();

	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	private int iFirstDayOfWeek = Calendar.MONDAY;
	private int Calendar_Width = 0;
	private int Cell_Width = 0;
	private LinearLayout layout;

	private TextView text_month = null;
	private TextView text_year = null;
	private Button btn_pre_month = null;
	private Button btn_next_month = null;
	private TextView arrange_text = null;
	private LinearLayout mainLayout = null;
	private LinearLayout arrange_layout = null;
	private TextView now_day = null;
	private String currentYear;
	private String currentMonth;
	private boolean updateFlag = true;

	private Boolean[] flag = null;
	private Calendar startDate = null;
	private Calendar endDate = null;
	private int dayvalue = -1;

	public static int Calendar_WeekBgColor = 0;
	public static int Calendar_DayBgColor = 0;
	public static int isHoliday_BgColor = 0;
	public static int unPresentMonth_FontColor = 0;
	public static int isPresentMonth_FontColor = 0;
	public static int isToday_BgColor = 0;
	public static int special_Reminder = 0;
	public static int common_Reminder = 0;
	public static int Calendar_WeekFontColor = 0;
	private int screenWidth = 0;
	private int screenHeight = 0;
	private ImageView main_menu_btn;
	
	/*menu菜单*/
	private TextView user_name;
	private TextView user_dept;
	private TextView user_company;
	private ListView menu_list;
	private List<MenuBean> Menu_dataList;
	private MenuListAdapter menu_adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groundview);
		getScreen();
		initMainView();
		initValue();
		initView();	
		initMenuView();
		initData_menu();
	}
	
	private void initMenuView() {
		user_name=(TextView) this.findViewById(R.id.user_name);
		user_dept=(TextView) this.findViewById(R.id.user_dept);
		user_company=(TextView) this.findViewById(R.id.user_company);
		RelativeLayout menu=(RelativeLayout) this.findViewById(R.id.menu_list_layout);
		RelativeLayout.LayoutParams pm_top=new RelativeLayout.LayoutParams(screenWidth,dipTopx(this, 600));
		pm_top.addRule(RelativeLayout.BELOW, R.id.menu_top_layout);
		menu.setLayoutParams(pm_top);
		menu_list=(ListView) this.findViewById(R.id.menu_list);
		
	}
	
	private void initData_menu(){
		Menu_dataList=new ArrayList<MenuBean>();
		int[]images={R.drawable.home_btn_bg,R.drawable.meet_btn_bg,
				R.drawable.signin_btn_bg,R.drawable.setting_btn_bg};
		String home=getResources().getString(R.string.menu_home);
		String meeting=getResources().getString(R.string.menu_meeting);
		String singin=getResources().getString(R.string.menu_singin);
		String setting=getResources().getString(R.string.menu_setting);
		String[]names={home,meeting,singin,setting};
		for(int i=0;i<4;i++){
			MenuBean bean=new MenuBean();
			bean.setImageID(images[i]);
			bean.setTitle(names[i]);
			Menu_dataList.add(bean);
		}
		Log.e("长度", Menu_dataList.size()+"");
		menu_adapter=new MenuListAdapter(this,Menu_dataList);
		menu_list.setAdapter(menu_adapter);
	}
	
	public void menuList_onClick(){
		menu_list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
				case 0:
					if (myViewGroup.isMenuOpen){
						myViewGroup.closeMenu();
					}
					else{
						myViewGroup.showMenu();
					}
					break;
				case 1:
					//跳转到会议界面
					break;
				case 2:
					//跳转到签到界面
					break;
				case 3:
					//跳转到设置界面
					break;
				}
			}});
	}
	@SuppressWarnings("deprecation")
	private void initMainView() {
		gestureDetector = new GestureDetector(this);
		myViewGroup = (MyViewGroup) findViewById(R.id.main_vg);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		main_view = inflater.inflate(R.layout.activity_main, null);
		menu_view = inflater.inflate(R.layout.activity_menu, null);
		myViewGroup.addView(menu_view);
		myViewGroup.addView(main_view);		
		getMAX_WIDTH();
		main_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (myViewGroup.getScrollX() <= -distance) {
					myViewGroup.closeMenu();
				}
			}
		});

	}

	private void getMAX_WIDTH() {
		viewTreeObserver = menu_view.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_windth = getWindowManager().getDefaultDisplay()
							.getWidth();
					iv_button_width = main_view.findViewById(
							R.id.mian_menu_btn).getWidth();
					distance = window_windth - iv_button_width/3*2; // 改动
					myViewGroup.setDistance(distance);
					ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) menu_view
							.getLayoutParams();
					layoutParams.width = distance;
					menu_view.setLayoutParams(layoutParams);

					hasMeasured = true;
				}
				return true;
			}
		});
	}
	

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
		
			}
		}
	};

	private void initView() {
		
		RelativeLayout top_layout=(RelativeLayout)findViewById(R.id.top_layout);
		LayoutParams pm_top=new LayoutParams(screenWidth,dipTopx(this,60));
		top_layout.setLayoutParams(pm_top);
		
		RelativeLayout date_show_layout=(RelativeLayout)findViewById(R.id.date_show_layout);
		LayoutParams pm=new LayoutParams(screenWidth,dipTopx(this,60));
		date_show_layout.setLayoutParams(pm);
		
		RelativeLayout title_iamge_layout=(RelativeLayout) findViewById(R.id.title_iamge_layout);
		LayoutParams pm_image=new LayoutParams(screenWidth,dipTopx(this,30));
		//pm_image.addRule(RelativeLayout.RIGHT_OF,R.id.date_show_layout);
		title_iamge_layout.setLayoutParams(pm_image);

		layout = (LinearLayout) this.findViewById(R.id.cal_lay);
		text_month = (TextView) findViewById(R.id.text_month);
		text_year = (TextView) findViewById(R.id.text_year);
		btn_pre_month = (Button) findViewById(R.id.btn_pre_month);
		btn_next_month = (Button) findViewById(R.id.btn_next_month);
		btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
		btn_next_month.setOnClickListener(new Next_MonthOnClickListener());
		now_day = (TextView) this.findViewById(R.id.now_day);
		now_day.setOnClickListener(this);
		calStartDate = getCalendarStartDate();
		main_menu_btn=(ImageView) this.findViewById(R.id.mian_menu_btn);
		main_menu_btn.setOnClickListener(this);
		layout.addView(generateCalendarMain());

		DateWidgetDayCell daySelected = updateCalendar();

		if (daySelected != null)
			daySelected.requestFocus();

		LinearLayout.LayoutParams Param1 = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		ScrollView view = new ScrollView(this);

		arrange_layout = createLayout(LinearLayout.VERTICAL);
		arrange_layout.setPadding(5, 2, 0, 0);
		arrange_text = new TextView(this);
		// mainLayout.setBackgroundColor(Color.WHITE);
		arrange_text.setTextColor(Color.BLACK);
		arrange_text.setTextSize(18);
		arrange_layout.addView(arrange_text);

		startDate = GetStartDate();
		calToday = GetTodayDate();

		endDate = GetEndDate(startDate);
		view.addView(arrange_layout, Param1);
		layout.addView(view);
		Calendar_WeekBgColor = this.getResources().getColor(
				R.color.Calendar_WeekBgColor);
		Calendar_DayBgColor = this.getResources().getColor(
				R.color.Calendar_DayBgColor);
		isHoliday_BgColor = this.getResources().getColor(
				R.color.isHoliday_BgColor);
		unPresentMonth_FontColor = this.getResources().getColor(
				R.color.unPresentMonth_FontColor);
		isPresentMonth_FontColor = this.getResources().getColor(
				R.color.isPresentMonth_FontColor);
		isToday_BgColor = this.getResources().getColor(R.color.isToday_BgColor);
		special_Reminder = this.getResources()
				.getColor(R.color.specialReminder);
		common_Reminder = this.getResources().getColor(R.color.commonReminder);
		Calendar_WeekFontColor = this.getResources().getColor(
				R.color.Calendar_WeekFontColor);

		UpdateCurrentMonthDisplay(calToday.get(Calendar.DATE),
				calToday.get(Calendar.MONTH) + 1, calToday.get(Calendar.YEAR));
	}

	private void initValue() {
		
		Calendar_Width = screenWidth;
		Cell_Width = Calendar_Width / 7 + 1;
		Log.e("width", "cel width:" + Cell_Width);
	}



	protected String GetDateShortString(Calendar date) {
		String returnString = date.get(Calendar.YEAR) + "/";
		returnString += date.get(Calendar.MONTH) + 1 + "/";
		returnString += date.get(Calendar.DAY_OF_MONTH);

		return returnString;
	}

	private int GetNumFromDate(Calendar now, Calendar returnDate) {
		Calendar cNow = (Calendar) now.clone();
		Calendar cReturnDate = (Calendar) returnDate.clone();
		setTimeToMidnight(cNow);
		setTimeToMidnight(cReturnDate);

		long todayMs = cNow.getTimeInMillis();
		long returnMs = cReturnDate.getTimeInMillis();
		long intervalMs = todayMs - returnMs;
		int index = millisecondsToDays(intervalMs);

		return index;
	}

	private int millisecondsToDays(long intervalMs) {
		return Math.round((intervalMs / (1000 * 86400)));
	}

	private void setTimeToMidnight(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	private LinearLayout createLayout(int iOrientation) {
		LinearLayout lay = new LinearLayout(this);
		LinearLayout.LayoutParams Param = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lay.setLayoutParams(Param);
		lay.setOrientation(iOrientation);

		return lay;
	}


	private View generateCalendarMain() {
		layContent = createLayout(LinearLayout.VERTICAL);
		days.clear();
		for (int iRow = 0; iRow < 6; iRow++) {
			layContent.addView(generateCalendarRow());
		}

		return layContent;
	}

	private View generateCalendarRow() {
		LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);

		for (int iDay = 0; iDay < 7; iDay++) {
			DateWidgetDayCell dayCell = new DateWidgetDayCell(this, Cell_Width,
					Cell_Width);
			dayCell.setItemClick(mOnDayCellClick);
			days.add(dayCell);
			layRow.addView(dayCell);
		}

		return layRow;
	}

	// 启动时显示年月日
	private Calendar getCalendarStartDate() {
		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);

		if (calSelected.getTimeInMillis() == 0) {
			calStartDate.setTimeInMillis(System.currentTimeMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		} else {
			calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
			calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
		}

		UpdateStartDateForMonth();
		return calStartDate;
	}

	private void UpdateStartDateForMonth() {
		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		calStartDate.set(Calendar.HOUR_OF_DAY, 0);
		calStartDate.set(Calendar.MINUTE, 0);
		calStartDate.set(Calendar.SECOND, 0);
		// update days for week
		// UpdateCurrentMonthDisplay();
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;

		if (iStartDay == Calendar.MONDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 1;
			if (iDay < 0)
				iDay = 6;
		}

		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
			if (iDay < 0)
				iDay = 6;
		}

		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
	}

	private DateWidgetDayCell updateCalendar() {
		DateWidgetDayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());

		// begin
		for (int i = 0; i < days.size(); i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DateWidgetDayCell dayCell = days.get(i);

			boolean bToday = false;

			if (calToday.get(Calendar.YEAR) == iYear) {
				if (calToday.get(Calendar.MONTH) == iMonth) {
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay) {
						bToday = true;
					}
				}
			}

			// check holiday
			boolean bHoliday = false;
			if ((iDayOfWeek == Calendar.SATURDAY)
					|| (iDayOfWeek == Calendar.SUNDAY))
				bHoliday = true;
			if ((iMonth == Calendar.JANUARY) && (iDay == 1))
				bHoliday = true;

			bSelected = false;
			if (taday_select && bToday) {
				bSelected = true;
			}
			if (bIsSelection)
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}

			dayCell.setSelected(bSelected);

			boolean hasRecord = false;
			if (checkCalendarRecord(iDay, iMonth + 1, iYear)) {
				hasRecord = true;
			}
			if (bSelected)
				daySelected = dayCell;

			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					iMonthViewCurrentMonth, hasRecord);
			dayCell.invalidate();
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		// end

		layContent.invalidate();

		return daySelected;
	}

	private void UpdateCurrentMonthDisplay(int date, final int month,
			final int year) {
		// String date = calStartDate.get(Calendar.YEAR) + "��"
		// + (calStartDate.get(Calendar.MONTH) + 1) + "��";
		String strDate = date + "";
		if (strDate.length() == 1) {
			strDate = "0" + strDate;
		}

		this.currentMonth = month + "";
		this.currentYear = year + "";

		text_month.setText(getMonthFormInt(month));
		text_year.setText(year + "");

		if (updateFlag == true) {
			updateFlag = false;
			Log.e("in calendar", String.valueOf(month));
//			if (!NetInfo.getInstance(this).isConnection()) {
//				iphoneDialogBuilder builder = new iphoneDialogBuilder(this);
//
//				builder.setTitle("信息提示");
//				builder.setMessage("网络未连接，请稍候重试");
//				builder.setPositiveButton("确定",
//						new android.content.DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//								updateCalendar(); // 
//							}
//						});
//				builder.create().show();
//			} else {
//				new Thread(new Runnable() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Message msg = new Message();
//						msg.what = MessageNumber.CALENDAR_OK;
//						msg.obj = loadCalendarData(year, month);
//						handler.sendMessage(msg);
//
//					}
//
//				}).start();
//			}
			updateCalendar();
		}
	}

	private String getMonthFormInt(int month) {
		String strMonth = "";
		switch (month) {
		case 1:
			strMonth = "1月";
			break;
		case 2:
			strMonth = "2月";
			break;
		case 3:
			strMonth = "3月";
			break;
		case 4:
			strMonth = "4月";
			break;
		case 5:
			strMonth = "5月";
			break;
		case 6:
			strMonth = "6月";
			break;
		case 7:
			strMonth = "7月";
			break;
		case 8:
			strMonth = "8月";
			break;
		case 9:
			strMonth = "9月";
			break;
		case 10:
			strMonth = "10月";
			break;
		case 11:
			strMonth = "11月";
			break;
		case 12:
			strMonth = "12月";
			break;
		default:
			strMonth = "WrongMonth";
			break;
		}
		return strMonth;
	}

	// 点上个月按钮的相应事件
	// 在其中添加课程请求信息

	class Pre_MonthOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			updateFlag = true;

			arrange_text.setText("");
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth--;

			if (iMonthViewCurrentMonth == -1) {
				iMonthViewCurrentMonth = 11;
				iMonthViewCurrentYear--;
			}

			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			calStartDate.set(Calendar.HOUR_OF_DAY, 0);
			calStartDate.set(Calendar.MINUTE, 0);
			calStartDate.set(Calendar.SECOND, 0);
			calStartDate.set(Calendar.MILLISECOND, 0);

			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);

			UpdateCurrentMonthDisplay(1, iMonthViewCurrentMonth + 1,
					iMonthViewCurrentYear);

			UpdateStartDateForMonth();

		}

	}

	// 点下个月按钮的相应事件
	// 在其中添加课程请求信息

	class Next_MonthOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			updateFlag = true;

			arrange_text.setText("");
			calSelected.setTimeInMillis(0);
			iMonthViewCurrentMonth++;

			if (iMonthViewCurrentMonth == 12) {
				iMonthViewCurrentMonth = 0;
				iMonthViewCurrentYear++;
			}

			calStartDate.set(Calendar.DAY_OF_MONTH, 1);
			calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
			calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
			UpdateStartDateForMonth();

			startDate = (Calendar) calStartDate.clone();
			endDate = GetEndDate(startDate);
			UpdateCurrentMonthDisplay(1, iMonthViewCurrentMonth + 1,
					iMonthViewCurrentYear);

			// updateCalendar();
		}
	}

	private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
		public void OnClick(DateWidgetDayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			int day = GetNumFromDate(calSelected, startDate);

			long todayMiliSeconds = calToday.getTimeInMillis();
			long selectMiliSeconds = calSelected.getTimeInMillis();

			int date = calSelected.get(Calendar.DAY_OF_MONTH);
			int month = calSelected.get(Calendar.MONTH);
			int year = calSelected.get(Calendar.YEAR);
			int now_date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
			// 添加在线报名
/*			if (checkCalendarRecord(date, month + 1, year)) {

				String key = conbineDate(date, month + 1, year);
				final List<CalendarXMLBean> data_list = new ArrayList<CalendarXMLBean>();
				for (int i = 0; i < datalist.size(); i++) {
					if (datalist.get(i).getAblumDate().equals(key)) {
						data_list.add(datalist.get(i));
					}
				}

				CalenderAdaperActivity adaper = new CalenderAdaperActivity(
						MainActivity.this, data_list);
				list.setAdapter(adaper);
				list.setVisibility(View.VISIBLE);
				list.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							final int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (data_list.get(arg2).getAblumType().equals("2")) {
							// Toast.makeText(CalendarActivity.this,
							// "数据加载中，请稍后", Toast.LENGTH_SHORT).show();
							if (!NetInfo.getInstance(MainActivity.this)
									.isConnection()) {
								// Toast.makeText(this.getApplicationContext(),
								// "警告：当前网络处于不可用状态，由此可能会引起程序异常",
								// Toast.LENGTH_SHORT).show();
								iphoneDialogBuilder builder = new iphoneDialogBuilder(
										MainActivity.this);

								builder.setTitle("信息提示");
								builder.setMessage("网络未连接，请稍候重试");
								builder.setPositiveButton(
										"确定",
										new android.content.DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										});
								builder.create().show();
							} else {
								new Thread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Message msg = new Message();
										msg.what = MessageNumber.AD_GET_ISOVER;
										msg.obj = getSingnUp(data_list
												.get(arg2).getAblumId());
										handler.sendMessage(msg);
									}
								}).start();

							}
						} else if (data_list.get(arg2).getAblumType()
								.equals("1")) {
							Toast.makeText(MainActivity.this, "数据加载中，请稍后",
									Toast.LENGTH_SHORT).show();

							Intent intent = new Intent();
							intent.setClass(MainActivity.this,
									DetailMemberUnActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("ablum_id", data_list.get(arg2)
									.getAblumId());
							intent.putExtras(bundle);
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}
					}
				});

			} else
				Toast.makeText(MainActivity.this, "没有专辑或活动",
						Toast.LENGTH_SHORT).show();
						*/
			taday_select = false;
			item.setSelected(true);
			updateCalendar();

		}
	};

	public Calendar GetTodayDate() {
		Calendar cal_Today = Calendar.getInstance();
		cal_Today.set(Calendar.HOUR_OF_DAY, 0);
		cal_Today.set(Calendar.MINUTE, 0);
		cal_Today.set(Calendar.SECOND, 0);
		cal_Today.setFirstDayOfWeek(Calendar.MONDAY);

		return cal_Today;
	}

	public Calendar GetStartDate() {
		int iDay = 0;
		Calendar cal_Now = Calendar.getInstance();
		cal_Now.set(Calendar.DAY_OF_MONTH, 1);
		cal_Now.set(Calendar.HOUR_OF_DAY, 0);
		cal_Now.set(Calendar.MINUTE, 0);
		cal_Now.set(Calendar.SECOND, 0);
		cal_Now.setFirstDayOfWeek(Calendar.MONDAY);

		iDay = cal_Now.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;

		if (iDay < 0) {
			iDay = 6;
		}

		cal_Now.add(Calendar.DAY_OF_WEEK, -iDay);

		return cal_Now;
	}

	public Calendar GetEndDate(Calendar startDate) {
		Calendar endDate = Calendar.getInstance();
		endDate = (Calendar) startDate.clone();
		endDate.add(Calendar.DAY_OF_MONTH, 41);
		return endDate;
	}

	void insertCalendarRecord(int date, int month, int year) {
		String record = year + "-" + month + "-" + date;
	}

	boolean checkCalendarRecord(int date, int month, int year) {
		String smonth;
		if (month < 10)
			smonth = "0" + month;
		else
			smonth = String.valueOf(month);

		String sdate;
		if (date < 10)
			sdate = "0" + date;
		else
			sdate = String.valueOf(date);

		String record = year + "-" + smonth + "-" + sdate;
	/*	if (datalist.isEmpty() || datalist == null) {
		} else {
			Iterator<CalendarXMLBean> it = datalist.iterator();
			while (it.hasNext()) {

				CalendarXMLBean bean = it.next();
				if (bean.getAblumDate().equals(record)) {
					return true;
				}
			}
		}*/
		return false;
	}

	private String conbineDate(int date, int month, int year) {
		String smonth;
		if (month < 10)
			smonth = "0" + month;
		else
			smonth = String.valueOf(month);

		String sdate;
		if (date < 10)
			sdate = "0" + date;
		else
			sdate = String.valueOf(date);

		String record = year + "-" + smonth + "-" + sdate;
		return record;
	}


	@Override
	protected void onDestroy() {
		if (myViewGroup.isMenuOpen)
			myViewGroup.closeMenu();
		super.onDestroy();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP && isScrolling) {
			myViewGroup.slidingMenu();
		}
		gestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		menu_view.measure(0, 0);
		menu_view.layout(-distance, 0, 0, getWindowManager()
				.getDefaultDisplay().getHeight());
		// lv_menu.setSelection(0);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		
		 if (!myViewGroup.isMenuOpen && distanceX > 0) {
		 } else if (myViewGroup.getScrollX() == -distance
		 && myViewGroup.isMenuOpen && distanceX < 0) {
		 isScrolling = false;
		 }
		 else if (myViewGroup.getScrollX() >= -distance
		 && myViewGroup.getScrollX() <= 0) {
		 isScrolling = true;
		 myViewGroup.scrollBy((int) distanceX, 0);
		 }
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	/* 防止用户多次点击 */

	private synchronized void setProcessFlag() {
		processFlag = false;
	}

	private class TimeThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				sleep(1000);
				processFlag = true;
			} catch (Exception e) {
				Log.e("in timethread", e.getMessage());
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.now_day:
			if (processFlag) {
				updateFlag = true;

				arrange_text.setText("");
				calSelected.setTimeInMillis(0);
				calStartDate = getCalendarStartDate();
				// getCalendarStartDate() ;
				DateWidgetDayCell daySelected = updateCalendar();

				if (daySelected != null) {
					daySelected.setSelected(true);
				}

				taday_select = true;

				UpdateCurrentMonthDisplay(
						Calendar.getInstance().get(Calendar.DATE), Calendar
								.getInstance().get(Calendar.MONTH) + 1,
						Calendar.getInstance().get(Calendar.YEAR));
				setProcessFlag();
				TimeThread time = new TimeThread();
				time.start();			
				}
			break;
		case R.id.mian_menu_btn:
			if (myViewGroup.isMenuOpen){
				myViewGroup.closeMenu();
			}
			else{
				myViewGroup.showMenu();
			}
			break;
		}
	}
	
	public void getScreen(){
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}
	
	/** 
	     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	     */  
	    public static int dipTopx(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	    }  
	  
	    /** 
	     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
	     */  
	    public static int pxTodip(Context context, float pxValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (pxValue / scale + 0.5f);  
	    }  

}