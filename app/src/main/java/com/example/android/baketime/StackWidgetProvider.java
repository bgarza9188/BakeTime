/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.baketime;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import static android.app.PendingIntent.getBroadcast;

public class StackWidgetProvider extends AppWidgetProvider {
    public static final String UPDATE_ACTION = "com.example.android.baketime.UPDATE_ACTION";
    public static final String EXTRA_ITEM = "com.example.android.baketime.EXTRA_ITEM";
    public static final String RESET_ACTION = "com.example.android.baketime.REST_ACTION";
    private String LOG_TAG = StackWidgetProvider.class.getName();

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(LOG_TAG, "Ben in onRecieve");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        if (intent.getAction().equals(UPDATE_ACTION)) {
            String viewIngredients = intent.getStringExtra(EXTRA_ITEM);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            views.setTextViewText(R.id.widget_list, viewIngredients);

            Intent resetIntent = new Intent(context, StackWidgetProvider.class);
            resetIntent.setAction(StackWidgetProvider.RESET_ACTION);
            resetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
            PendingIntent pt = getBroadcast(context, 0, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_list, pt);
            appWidgetManager.updateAppWidget(appWidgetID, views);


        }
        if(intent.getAction().equals(RESET_ACTION)){
            Log.e(LOG_TAG, "Reset Action");
            int[] appWidgetIds =
                    appWidgetManager.getAppWidgetIds(new ComponentName(context.getPackageName(), StackWidgetProvider.class.getName()));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.e(LOG_TAG, "Ben in onUpdate");
        // update each of the widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; ++i) {

            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            //rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
            rv.setRemoteAdapter(R.id.stack_view, intent);
            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            rv.setEmptyView(R.id.stack_view, R.id.empty_view);

            // Here we setup the a pending intent template. Individuals items of a collection
            // cannot setup their own pending intents, instead, the collection as a whole can
            // setup a pending intent template, and the individual items can set a fillInIntent
            // to create unique before on an item to item basis.
            Intent updateIntent = new Intent(context, StackWidgetProvider.class);
            updateIntent.setAction(StackWidgetProvider.UPDATE_ACTION);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = getBroadcast(context, 0, updateIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}