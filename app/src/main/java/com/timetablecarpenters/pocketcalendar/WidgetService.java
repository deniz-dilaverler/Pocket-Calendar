package com.timetablecarpenters.pocketcalendar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Calendar;

public class WidgetService extends RemoteViewsService {
    private static final String TAG = "WidgetService";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetItemFactory(getApplicationContext(), intent);
    }


    class WidgetItemFactory implements  RemoteViewsFactory {
        private final int TOTAL_DAYS = 7;
        private Context context;
        private int appWidgetId;
        private Cursor cursor;
        private ArrayList<CalendarEvent> events;

        WidgetItemFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID );
            this.cursor = cursor;
        }
        @Override
        public void onCreate() {
            DBHelper dbHelper = new DBHelper(context , DBHelper.DB_NAME, null);
            Calendar today = Calendar.getInstance();
            Calendar until = (Calendar) today.clone();
            until.add(Calendar.DATE, TOTAL_DAYS);
            cursor = dbHelper.getEventsInAnInterval(today, until);
            events = new ArrayList<CalendarEvent>();

            if (cursor.moveToFirst()) {
                do {
                    Calendar eventStart = Calendar.getInstance();
                    eventStart.set(Calendar.YEAR, cursor.getInt(cursor.getColumnIndex(DBHelper.YEAR)));
                    eventStart.set(Calendar.MONTH, cursor.getInt(cursor.getColumnIndex(DBHelper.MONTH)));
                    eventStart.set(Calendar.DATE, cursor.getInt(cursor.getColumnIndex(DBHelper.DAY)));
                    // times are stored as strings in the db in HH:MM format, this code beneath parses the hour and minutes into an
                    // int value and later sets the Calendar class
                    try {
                        eventStart.set(Calendar.HOUR,
                                Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START))).substring(0,2)));
                    }catch (Exception e) {
                        Log.e(TAG, "onCreate: eventStart setting the hour: " + e);
                    }
                    try {
                        eventStart.set(Calendar.MINUTE,
                                Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_START))).substring(3)));
                    }catch (Exception e) {
                        Log.e(TAG, "onCreate: eventStart setting the minute: " + e);
                    }

                    Calendar eventEnd = (Calendar) eventStart.clone();

                    try {
                        eventStart.set(Calendar.HOUR,
                                Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END))).substring(0,2)));
                    }catch (Exception e) {
                        Log.e(TAG, "onCreate: eventEnd setting the hour: " + e);
                    }
                    try {
                        eventEnd.set(Calendar.MINUTE,
                                Integer.parseInt((cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_END))).substring(3)));
                    }catch (Exception e) {
                        Log.e(TAG, "onCreate: eventEnd setting the minute: " + e);
                    }
                    String eventName = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME));
                    String eventType = cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_TYPE));
                    int id = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
                    // rest of the properties are not needed for the widget to be shown

                    events.add(new CalendarEvent(eventStart, eventEnd, eventName, id,eventType));
                } while (cursor.moveToNext());
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            // events.clear();
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            CalendarEvent event = events.get(position);
            Log.d(TAG, "getViewAt: current event: " + event.getName());
            views.setTextViewText(R.id.widget_event_title, event.getName());

            String dateString = String.format("%d.%d.%d", event.getDay(), event.getMonth(), event.getYear());
            views.setTextViewText(R.id.widget_date_text, dateString );

            String timeText = event.getEventStartTime();
            if (!event.getEventStartTime().equals(event.getEventEndTime()))
                timeText = timeText + "-" + event.getEventEndTime();
            views.setTextViewText(R.id.widget_time, timeText);

            return views;
        }

        /**
         * returns the view that is shown untill the wanted views load
         * uses the default loading view when returned null
         * @return null
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /**
         * returns the number of different views used
         * @return
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * @param position
         * @return the id of each item on the list
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * @return true if the same id always refers to the same item, returning true makes the projection of views faster
         */
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
