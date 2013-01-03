/**
 * @author SwordBearer (XiaMen University of China )
 * @email ranxiedao@163.com
 * @state You may use these codes without any restriction in your apps and to
 *        develop your apps. If you have a better way to get the same target
 *        ,please let me know
 */

package xmu.swordbearer.customviews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import xmu.swordbearer.customviews.R;
import xmu.swordbearer.customviews.CalendarView.OnDateSelectedListener;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity {
	CalendarView calendarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		calendarView = (CalendarView) findViewById(R.id.calendarview);
		Button btnPrev = (Button) findViewById(R.id.btnPrev);
		final Button btnDate = (Button) findViewById(R.id.btnDate);
		Button btnNext = (Button) findViewById(R.id.btnNext);
		btnPrev.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				calendarView.showLastsMonth();
				Calendar calendar = calendarView.getCalendar();
				btnDate.setText(getDateString(calendar));
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				calendarView.showNextMonth();
				btnDate.setText(getDateString(calendarView.getCalendar()));
			}
		});
		calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
			@Override
			public void onDateSelected(Calendar calendar) {
				Log.e("TEST", "selected date is : " + getDateString2(calendar));
			}
		});

		Calendar calendar = Calendar.getInstance();
		Calendar minCalendar = Calendar.getInstance();
		minCalendar.add(Calendar.MONTH, -2);
		Calendar maxCalendar = Calendar.getInstance();
		maxCalendar.add(Calendar.MONTH, 2);

		calendarView.setMinMaxCalendar(minCalendar, maxCalendar);
		calendarView.setCalendar(calendar);
		// calendarView.setShowLines(false);
		calendarView.setLineColor(Color.DKGRAY);
		calendarView.setLineWidth(1);
		// calendarView.hideWeekLabel();
		calendarView.setbgColorOfToday(Color.YELLOW);

		/************* using CalendarView in Java **************/
		CalendarView calendarView2 = new CalendarView(this);
		Log.e("Java Code TEST", getDateString2(calendarView2.getCalendar()));
	}

	private String getDateString(Calendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		return format.format(calendar.getTime());
	}

	private String getDateString2(Calendar calendar) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}
}
