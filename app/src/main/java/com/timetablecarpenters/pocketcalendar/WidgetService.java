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

        /**
         * recieves events from the db and puts them inside an ArrayList to be extracted agains to inflate the ListView items
         */
        @Override
        public void onCreate() {
            DBHelper dbHelper = new DBHelper(context , DBHelper.DB_NAME, null);
            Calendar today = Calendar.getInstance();
            Calendar until = (Calendar) today.clone();
            until.add(Calendar.DATE, TOTAL_DAYS);
            events = dbHelper.getEventsInAnIntervalInArray(today, until);

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            // events.clear();
        }

        /**
         * @return how many items for the widget there are
         */
        @Override
        public int getCount() {
            return events.size();
        }

        /**
         * creates a remote view and fills it with the data from the cursor
         * @param position
         * @return RemoteView (1 item of the UpcomingEvents widget's listView)
         */
        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            CalendarEvent event = events.get(position);
            Log.d(TAG, "getViewAt: current event: " + event.getName());
            views.setTextViewText(R.id.widget_event_title, event.getName());

            String dateString = String.format("%d.%d.%d", event.getDay(), event.getMonth() , event.getYear());
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
