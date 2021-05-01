package com.timetablecarpenters.pocketcalendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.widget.RemoteViews;

/**
 * Creates the home screen App Widget for viewing Upcoming events
 * @author Deniz Mert Dilaverler
 * @version 01.05.2021
 */
public class UpComingEventsWidget extends AppWidgetProvider {
    private static final String TAG = "UpComingEventsWidget";

    /**
     * Updates widget by calling a remoteAdapter to set inflate and fill its ListView
     * @param context
     * @param appWidgetManager
     * @param appWidgetId
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent intent = new Intent(context, UpcomingEvents.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent serviceIntent = new Intent(context, WidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.up_comming_events_widget);
        // views.setOnClickPendingIntent(R.id.widget_event_list, pendingIntent);
        views.setRemoteAdapter(R.id.widget_event_list, serviceIntent);
        //views.setEmptyView();
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * calls updateAppWidget on the each widget that the user has
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Method called when the widget is initialized for the first time,
     * this method has no implementation or function on our app
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }


}