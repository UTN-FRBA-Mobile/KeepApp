package com.utn.mobile.keepapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Created by julis on 2/10/2017.
 */

public class WidgetProvider extends AppWidgetProvider {

    private final String NEW_NATACION = "NEW_EJERCICIO_NATACION";
    private final String NEW_5KM = "NEW_EJERCICIO_5KM";
    private final String NEW_BANCO_PLANO = "NEW_EJERCICIO_BANCO_PLANO";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            //String number = String.format("%03d", (new Random().nextInt(900) + 100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            Intent natacionIntent = new Intent(context, AgregarEjercicio.class);
            natacionIntent.setAction(NEW_NATACION);
            //natacionIntent.putExtra("ejercicio", "Natacion");
            natacionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent configPendingIntent = PendingIntent.getActivity(context, 1, natacionIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.newEjercicio1, configPendingIntent);

            Intent cincoKIntent = new Intent(context, AgregarEjercicio.class);
            cincoKIntent.setAction(NEW_5KM);
            //cincoKIntent.putExtra("ejercicio", "5 KM");
            cincoKIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent configPendingIntent2 = PendingIntent.getActivity(context, 2, cincoKIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.newEjercicio2, configPendingIntent2);

            Intent bancoPlanoIntent = new Intent(context, AgregarEjercicio.class);
            bancoPlanoIntent.setAction(NEW_BANCO_PLANO);
            //bancoPlanoIntent.putExtra("ejercicio", "Banco Plano");
            bancoPlanoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent configPendingIntent3 = PendingIntent.getActivity(context, 3, bancoPlanoIntent, 0);

            remoteViews.setOnClickPendingIntent(R.id.newEjercicio3, configPendingIntent3);

            /*Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/
            //remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

}
