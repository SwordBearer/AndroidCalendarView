/**
 * @author SwordBearer (XiaMen University of China )
 * @email ranxiedao@163.com
 * @github https://github.com/SwordBearer
 * @state You may use these codes without any restriction in your apps and to develop your apps
 *  If you have a better way to get the same target ,please let me know
 */

package xmu.swordbearer.customviews;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import xmu.swordbearer.customviews.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarView extends LinearLayout {
	private static final int CALENDAR_CELL_COLUMNS = 7;
	private static final int CALENDAR_CELL_ROWS = 6;
	// style
	private static final boolean DEFAULT_SHOW_WEEKLABEL = true;
	private static int DEFAULT_BG_WEEKLABEL;
	private static int DEFAULT_CELL_TEXTSIZE = 14;
	private static int DEFAULT_VALID_CELL_TEXTCOLOR;
	private static int DEFAULT_INVALID_CELL_TEXTCOLOR;
	private static int DEFAULT_BG_COLOR_TODAY;
	private static int DEFAULT_BG_COLOR_NORMAL_CELL;
	private static int DEFAULT_BG_COLOR_SELECTED_CELL;
	//
	private static final boolean DEFAULT_SHOW_LINES = true;
	private static int DEFAULT_LINE_WIDTH = 1;
	private static int DEFAULT_LINE_COLOR;

	private Calendar currentCalendar;
	private Calendar maxCalendar;
	private Calendar minCalendar;

	private WeekLabel mWeekLabel;
	private GridView cellsGridView;

	/* 显示的所有天数 */
	private String[] mDays;
	private int firstDayPos;
	private int todayPos;
	private int lastDayPos;
	/**
	 * custom attributes
	 * 
	 * 
	 * @isShowWeekLabel
	 * @bgColorOfWeek
	 * 
	 * @invalidCellTextColor
	 * 
	 * @bgOfNormalCell
	 * @cellTextSize
	 * @validCellTextColor
	 * @bgOfSelectedCell
	 * @bgColorOfToday
	 * 
	 * @isShowLines
	 * @lineWidth
	 * @lineColor
	 */

	// weekLabel attrs
	private boolean isShowWeekLabel;
	private int bgColorOfWeek;
	// invalidCell attrs
	private int invalidCellTextColor;
	//
	private float cellTextSize = DEFAULT_CELL_TEXTSIZE;
	private int validCellTextColor;
	private int bgOfNormalCell;
	private int bgOfSelectedCell;
	private int bgColorOfToday;
	//
	private boolean isShowLines = DEFAULT_SHOW_LINES;
	private int lineColor;
	private int lineWidth = DEFAULT_LINE_WIDTH;

	public OnDateSelectedListener mOnDateSelectedListener;

	/**
	 * OnDateChangeListener
	 * 
	 * @author swordbearer
	 * 
	 */
	public interface OnDateSelectedListener {
		public void onDateSelected(Calendar calendar);
	}

	public void setOnDateSelectedListener(OnDateSelectedListener listener) {
		this.mOnDateSelectedListener = listener;
	}

	public CalendarView(Context context) {
		this(context, null);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.calendar_view);
		this.setOrientation(LinearLayout.VERTICAL);
		initDefaultStyles();
		// week label
		isShowWeekLabel = array
				.getBoolean(R.styleable.calendar_view_showWeekLabel,
						DEFAULT_SHOW_WEEKLABEL);
		bgColorOfWeek = array.getColor(
				R.styleable.calendar_view_bgColorOfWeekLabel,
				DEFAULT_BG_WEEKLABEL);
		// invalid cell
		invalidCellTextColor = array.getColor(
				R.styleable.calendar_view_invalidCellTextColor,
				DEFAULT_INVALID_CELL_TEXTCOLOR);
		// valid cell
		cellTextSize = array.getDimension(
				R.styleable.calendar_view_cellTextSize, DEFAULT_CELL_TEXTSIZE);
		validCellTextColor = array.getColor(
				R.styleable.calendar_view_validCellTextColor,
				DEFAULT_VALID_CELL_TEXTCOLOR);
		//
		bgOfNormalCell = array.getColor(
				R.styleable.calendar_view_bgColorOfNormalCell,
				DEFAULT_BG_COLOR_NORMAL_CELL);
		bgColorOfToday = array.getColor(
				R.styleable.calendar_view_bgColorOfToday,
				DEFAULT_BG_COLOR_TODAY);
		bgOfSelectedCell = array.getColor(
				R.styleable.calendar_view_bgColorOfSelectedCell,
				DEFAULT_BG_COLOR_SELECTED_CELL);
		//
		isShowLines = array.getBoolean(R.styleable.calendar_view_showLines,
				DEFAULT_SHOW_LINES);
		lineWidth = (int) array.getDimension(
				R.styleable.calendar_view_lineWidth, DEFAULT_LINE_WIDTH);
		lineColor = array.getColor(R.styleable.calendar_view_lineColor,
				DEFAULT_LINE_COLOR);
		if (currentCalendar == null) {
			currentCalendar = Calendar.getInstance();
		}
		initWeekLable(context);
		initCellGridView(context);
	}

	private final void initDefaultStyles() {
		Resources res = getResources();
		DEFAULT_VALID_CELL_TEXTCOLOR = res
				.getColor(R.color.default_valid_cell_text_color);
		DEFAULT_INVALID_CELL_TEXTCOLOR = res
				.getColor(R.color.default_invalid_cell_text_color);
		DEFAULT_INVALID_CELL_TEXTCOLOR = res
				.getColor(R.color.default_invalid_cell_text_color);
		DEFAULT_BG_COLOR_TODAY = res.getColor(R.color.default_bg_today);
		DEFAULT_BG_COLOR_NORMAL_CELL = res
				.getColor(R.color.default_bg_normal_cell);
		DEFAULT_BG_COLOR_SELECTED_CELL = res
				.getColor(R.color.default_bg_selected_cell);
		DEFAULT_BG_WEEKLABEL = res.getColor(R.color.default_bg_weeklabel);
		DEFAULT_LINE_COLOR = res.getColor(R.color.default_line_color);

		isShowWeekLabel = DEFAULT_SHOW_WEEKLABEL;
		bgColorOfWeek = DEFAULT_BG_WEEKLABEL;
		invalidCellTextColor = DEFAULT_INVALID_CELL_TEXTCOLOR;
		validCellTextColor = DEFAULT_VALID_CELL_TEXTCOLOR;
		bgOfSelectedCell = DEFAULT_BG_COLOR_TODAY;
		bgColorOfToday = DEFAULT_BG_COLOR_TODAY;
	}

	/**
	 * init the weeklabel(Sun,Mon,Tue,Wed,Thu,Fri,Sat)
	 */
	private void initWeekLable(Context context) {
		if (isShowWeekLabel) {
			mWeekLabel = new WeekLabel(context);
			mWeekLabel.setBackgroundColor(bgColorOfWeek);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			addView(mWeekLabel, params);
		}
	}

	private void initCellGridView(Context context) {
		cellsGridView = new GridView(context);
		cellsGridView.setNumColumns(CALENDAR_CELL_COLUMNS);
		setCalendarCellLines();
		cellsGridView.setOnItemClickListener(new OnCalendarCellClickListener());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		addView(cellsGridView, params);
		updateCells();
	}

	private void setCalendarCellLines() {
		if (isShowLines) {
			cellsGridView.setVerticalSpacing(lineWidth);
			cellsGridView.setHorizontalSpacing(lineWidth);
			cellsGridView.setBackgroundColor(lineColor);
			cellsGridView
					.setPadding(lineWidth, lineWidth, lineWidth, lineWidth);
		} else {
			cellsGridView.setVerticalSpacing(0);
			cellsGridView.setHorizontalSpacing(0);
			cellsGridView.setBackgroundDrawable(null);
			cellsGridView.setPadding(0, 0, 0, 0);
		}
	}

	private void updateCells() {
		calculateDays();
		cellsGridView.setAdapter(new CalendarCellAdapter(getContext()));
	}

	/**
	 * calculate the days of this month
	 */
	private void calculateDays() {
		mDays = new String[CALENDAR_CELL_COLUMNS * CALENDAR_CELL_ROWS];
		int currentYear = currentCalendar.get(Calendar.YEAR);
		int currentMonth = currentCalendar.get(Calendar.MONTH);
		int firstDayOfMonth = currentCalendar
				.getActualMinimum(Calendar.DAY_OF_MONTH);
		int lastDayOfMonth = currentCalendar
				.getActualMaximum(Calendar.DAY_OF_MONTH);
		Calendar tempCalendar = Calendar.getInstance();
		tempCalendar.set(Calendar.DAY_OF_MONTH, firstDayOfMonth);
		/* the day-of-week of current month */
		int firstDayWeekInMonth = tempCalendar.get(Calendar.DAY_OF_WEEK);
		firstDayPos = firstDayWeekInMonth - 1;
		todayPos = firstDayPos
				+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1;
		lastDayPos = firstDayPos + lastDayOfMonth - 1;
		/* number of current month days */
		for (int i = firstDayOfMonth; i <= lastDayOfMonth; i++) {
			mDays[firstDayPos + i - 1] = i + "";
		}
		Calendar lastMonth = Calendar.getInstance();
		if (currentMonth == 0) {// January
			lastMonth.set(Calendar.YEAR, currentYear - 1);
			lastMonth.set(Calendar.MONTH, 11);// last month of last year
		} else {
			lastMonth.set(Calendar.YEAR, currentYear);
			lastMonth.set(Calendar.MONTH, currentMonth - 1);

		}
		Log.e("TEST", "date of last Month: " + lastMonth.get(Calendar.YEAR)
				+ "-" + lastMonth.get(Calendar.MONTH));
		Log.e("TEST",
				"date of current Month: " + currentCalendar.get(Calendar.YEAR)
						+ "-" + currentCalendar.get(Calendar.MONTH));

		int lastDayOfLastMonth = lastMonth
				.getActualMaximum(Calendar.DAY_OF_MONTH);
		/* last month days */
		for (int i = 0; i < firstDayPos; i++) {
			mDays[i] = (lastDayOfLastMonth - firstDayPos + i + 1) + "";
		}
		/* number of next month days */
		int daysCountOfNextMonth = CALENDAR_CELL_COLUMNS * CALENDAR_CELL_ROWS
				- lastDayPos;
		for (int i = 1; i < daysCountOfNextMonth; i++) {
			mDays[lastDayPos + i] = i + "";
		}
	}

	/**
	 * change current Calendar
	 * 
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		if (minCalendar == null || maxCalendar == null) {
			minCalendar = calendar;
			maxCalendar = calendar;
		}
		if (calendar.compareTo(minCalendar) < 0
				|| calendar.compareTo(maxCalendar) > 0) {
			throw new IllegalArgumentException(
					"Error:current calendar is out of range !");
		}
		this.currentCalendar = calendar;
		updateCells();
	}

	public Calendar getCalendar() {
		return currentCalendar;
	}

	public Calendar getMaxCalendar() {
		return maxCalendar;
	}

	public Calendar getMinCalendar() {
		return minCalendar;
	}

	public void setMinMaxCalendar(Calendar minCalendar, Calendar maxCalendar) {
		if (minCalendar.compareTo(maxCalendar) > 0) {
			throw new IllegalArgumentException(
					" Error: the minimun calendar is larger than maximum calendar !");
		}
		this.minCalendar = minCalendar;
		this.maxCalendar = maxCalendar;
	}

	public void showNextMonth() {
		if (currentCalendar.compareTo(maxCalendar) > 0) {
			return;
		}
		int currentMonth = currentCalendar.get(Calendar.MONTH);
		/*
		 * if current month is December ,the set current month to January ,and
		 * current year plus 1
		 */
		if (currentMonth == 11) {// December
			this.currentCalendar.add(Calendar.YEAR, 1);// add year
			this.currentCalendar.set(Calendar.MONTH, 0);// set month to January
		} else {
			this.currentCalendar.add(Calendar.MONTH, 1);
		}
		updateCells();
	}

	public void showLastsMonth() {
		if (currentCalendar.compareTo(minCalendar) < 0) {
			return;
		}
		int currentMonth = currentCalendar.get(Calendar.MONTH);
		/*
		 * if current month is January ,then set current month to December,and
		 * currnet year minus 1
		 */
		if (currentMonth == 0) {
			this.currentCalendar.add(Calendar.YEAR, -1);
			this.currentCalendar.set(Calendar.MONTH, 11);
		} else {
			this.currentCalendar.add(Calendar.MONTH, -1);
		}
		updateCells();
	}

	public void hideWeekLabel() {
		this.isShowWeekLabel = false;
		this.removeView(mWeekLabel);
	}

	public void setInvalidCellTextColor(int invalidCellTextColor) {
		this.invalidCellTextColor = invalidCellTextColor;
	}

	public void setValidCellTextColor(int validCellTextColor) {
		this.validCellTextColor = validCellTextColor;
	}

	public void setShowLines(boolean isShowLines) {
		this.isShowLines = isShowLines;
		setCalendarCellLines();
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public void setbgColorOfWeek(int bgColorOfWeekLabel) {
		this.bgColorOfWeek = bgColorOfWeekLabel;
	}

	public void setBgOfSelectedCell(int bgColorOfSelectedCell) {
		this.bgOfSelectedCell = bgColorOfSelectedCell;
	}

	public void setbgColorOfToday(int bgColorOfToday) {
		this.bgColorOfToday = bgColorOfToday;
	}

	private class CalendarCellAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public class CalendarCell {
			public TextView tv;
		}

		public CalendarCellAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mDays.length;
		}

		@Override
		public Object getItem(int position) {
			return mDays[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CalendarCell cell;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.cell_label, null);
				cell = new CalendarCell();
				cell.tv = (TextView) convertView
						.findViewById(R.id.calendar_cell_tv);
			} else {
				cell = (CalendarCell) convertView.getTag();
			}
			if (position < firstDayPos || position > lastDayPos) {
				cell.tv.setTextColor(invalidCellTextColor);
			} else {
				cell.tv.setTextColor(validCellTextColor);
			}
			if ((todayPos == position)
					&& (currentCalendar.get(Calendar.MONTH) == Calendar
							.getInstance().get(Calendar.MONTH))) {
				cell.tv.setBackgroundColor(bgColorOfToday);
			}
			String day = mDays[position];
			cell.tv.setTextSize(cellTextSize);
			cell.tv.setText(day);
			convertView.setTag(cell);
			return convertView;
		}
	}

	private class OnCalendarCellClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if ((position >= firstDayPos) && (position <= lastDayPos)) {
				if (parent.getTag() != null) {
					((View) parent.getTag()).setBackgroundColor(bgOfNormalCell);
				}
				parent.setTag(view);
				view.setBackgroundColor(bgOfSelectedCell);
				currentCalendar.set(Calendar.DAY_OF_MONTH, new Integer(
						mDays[position]));
				Log.e("TEST ",
						"selected date is: "
								+ currentCalendar.get(Calendar.MONTH) + " "
								+ currentCalendar.get(Calendar.DAY_OF_MONTH));
				Log.e("TEST", "todayPos " + todayPos);
				mOnDateSelectedListener.onDateSelected(currentCalendar);
			}
		}
	}

	private class WeekLabel extends GridView {
		public WeekLabel(Context context) {
			super(context, null);
			String[] tempWeekdays = new DateFormatSymbols().getShortWeekdays();
			int n = tempWeekdays.length - 1;
			String weekdays[] = new String[n];
			for (int i = 0; i < n; i++) {
				weekdays[i] = tempWeekdays[i + 1];
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					R.layout.week_label, weekdays);
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
			this.setLayoutParams(params);
			this.setGravity(Gravity.CENTER);
			this.setNumColumns(CALENDAR_CELL_COLUMNS);
			this.setAdapter(adapter);
		}
	}
}
