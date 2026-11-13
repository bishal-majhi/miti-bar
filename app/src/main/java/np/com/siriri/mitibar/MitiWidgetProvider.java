package np.com.siriri.mitibar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.RemoteViews;

public class MitiWidgetProvider extends AppWidgetProvider {

    private static final int REQ_WIDGET_CLICK = 301;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context, appWidgetManager, appWidgetIds);
        DateReceiver.scheduleNextMidnightAlarm(context);
    }

    public static boolean hasActiveWidgets(Context context) {
        return getActiveIds(context).length > 0;
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (manager == null) return;
        int[] ids = getActiveIds(context);
        if (ids.length == 0) return;
        updateWidgets(context, manager, ids);
    }

    private static int[] getActiveIds(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        if (manager == null) return new int[0];
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, MitiWidgetProvider.class));
        return ids != null ? ids : new int[0];
    }

    private static void updateWidgets(Context context, AppWidgetManager manager, int[] appWidgetIds) {
        String pkg = context.getPackageName();
        Resources res = context.getResources();
        int layoutId = res.getIdentifier("widget_miti", "layout", pkg);
        int rootId = res.getIdentifier("widget_root", "id", pkg);
        int dateId = res.getIdentifier("widget_date", "id", pkg);
        int weekdayId = res.getIdentifier("widget_weekday", "id", pkg);
        if (layoutId == 0 || dateId == 0 || weekdayId == 0) return;

        BsCalendar.BsDateResult today = BsCalendar.getToday(DateReceiver.isUseNepalTime(context));

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent clickPending = PendingIntent.getActivity(
                context, REQ_WIDGET_CLICK, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        for (int widgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(pkg, layoutId);
            views.setTextViewText(dateId, today.dateOnly);
            views.setTextViewText(weekdayId, today.weekday);
            if (rootId != 0) {
                views.setOnClickPendingIntent(rootId, clickPending);
            }
            manager.updateAppWidget(widgetId, views);
        }
    }
}
