package com.timetablecarpenters.pocketcalendar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return null;
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
            
            do {

            } while (cursor.moveToNext());
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            cursor.close();
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            return null;
        }

        @Override
        public RemoteViews getLoadingView() {
            if(cursor.moveToNext()) {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
                views.setTextViewText(R.id.widget_event_title, cursor.getString(cursor.getColumnIndex(DBHelper.EVENT_NAME)));
                return views;
            }else
                return null;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
