package np.com.siriri.mitibar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;

import java.util.Calendar;
import java.util.TimeZone;

public class DateReceiver extends BroadcastReceiver {

    public static final String ACTION_UPDATE_DATE = "np.com.siriri.mitibar.ACTION_UPDATE_DATE";
    private static final String CHANNEL_ID = "bs_status_channel";
    private static final int NOTIFICATION_ID = 8315;
    private static final int REQ_ALARM = 0;
    private static final int REQ_DELETE = 1;
    private static final int REQ_CONTENT = 2;

    private static final String PREFS_NAME = "miti_bar_prefs";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_USE_NEPAL_TIME = "use_nepal_time";

    public static boolean isWidgetEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ENABLED, true);
    }

    public static void setWidgetEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply();
        if (enabled) {
            showNotification(context);
            scheduleNextMidnightAlarm(context);
        } else {
            cancelAll(context);
        }
    }

    public static boolean isUseNepalTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_USE_NEPAL_TIME, true);
    }

    public static void setUseNepalTime(Context context, boolean useNepalTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_USE_NEPAL_TIME, useNepalTime).apply();
        if (isWidgetEnabled(context)) {
            showNotification(context);
            scheduleNextMidnightAlarm(context);
        }
    }

    public static boolean isNotificationPermitted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return nm != null && nm.areNotificationsEnabled();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !isExpectedAction(intent.getAction())) {
            return;
        }
        if (!isWidgetEnabled(context)) {
            return;
        }
        showNotification(context);
        scheduleNextMidnightAlarm(context);
    }

    private static boolean isExpectedAction(String action) {
        if (action == null) return false;
        return ACTION_UPDATE_DATE.equals(action)
                || Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_DATE_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action);
    }

    public static void showNotification(Context context) {
        if (!isWidgetEnabled(context) || !isNotificationPermitted(context)) {
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Nepali Date Status", NotificationManager.IMPORTANCE_LOW
        );
        channel.setShowBadge(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        nm.createNotificationChannel(channel);

        BsCalendar.BsDateResult date = BsCalendar.getToday(isUseNepalTime(context));
        int smallIconRes = 0;
        if (date.inRange && date.dayOfMonth >= 1 && date.dayOfMonth <= 32) {
            smallIconRes = context.getResources().getIdentifier(
                    "ic_day_" + date.dayOfMonth, "drawable", context.getPackageName()
            );
        }
        if (smallIconRes == 0) {
            smallIconRes = context.getApplicationInfo().icon;
        }

        Bitmap largeBadge = renderDateBadge(context, date.monthShort, date.dayNum);

        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentPending = PendingIntent.getActivity(
                context, REQ_CONTENT, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent deleteIntent = new Intent(context, DateReceiver.class);
        deleteIntent.setAction(ACTION_UPDATE_DATE);
        PendingIntent deletePending = PendingIntent.getBroadcast(
                context, REQ_DELETE, deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(smallIconRes)
                .setLargeIcon(largeBadge)
                .setColor(0xFFD11339)
                .setContentTitle(date.fullLockscreen)
                .setContentText("विक्रम संवत • पात्रो")
                .setOngoing(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(contentPending)
                .setDeleteIntent(deletePending)
                .build();

        nm.notify(NOTIFICATION_ID, notification);
    }

    private static Bitmap renderDateBadge(Context context, String monthShort, String dayNum) {
        float density = context.getResources().getDisplayMetrics().density;
        int size = Math.max(96, Math.min(192, Math.round(48f * density)));

        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        float radius = size * 0.22f;
        paint.setColor(0xFF0D1D2D);
        canvas.drawRoundRect(new RectF(0, 0, size, size), radius, radius, paint);

        paint.setColor(0xFFD11339);
        canvas.save();
        canvas.clipRect(0, 0, size, size * 0.34f);
        canvas.drawRoundRect(new RectF(0, 0, size, size), radius, radius, paint);
        canvas.restore();

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        float centerX = size * 0.5f;
        float maxWidth = size * 0.86f;

        drawFittedCenteredText(canvas, paint, monthShort, centerX, size * 0.25f, size * 0.20f, maxWidth);
        drawFittedCenteredText(canvas, paint, dayNum, centerX, size * 0.80f, size * 0.44f, maxWidth);

        return bitmap;
    }

    private static void drawFittedCenteredText(
            Canvas canvas, Paint paint, String text,
            float centerX, float baselineY, float baseSize, float maxWidth) {
        float textSize = baseSize;
        paint.setTextSize(textSize);
        float measured = paint.measureText(text);
        if (measured > maxWidth && measured > 0f) {
            textSize *= (maxWidth / measured);
            paint.setTextSize(textSize);
        }
        canvas.drawText(text, centerX, baselineY, paint);
    }

    public static void scheduleNextMidnightAlarm(Context context) {
        if (!isWidgetEnabled(context)) return;

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        PendingIntent pi = buildAlarmPendingIntent(context, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        TimeZone tz = isUseNepalTime(context) ? BsCalendar.getNptTimeZone() : TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(tz);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 5);
        calendar.set(Calendar.MILLISECOND, 0);
        long triggerAtMillis = calendar.getTimeInMillis();

        boolean canUseExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms();
        if (canUseExact) {
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
                return;
            } catch (SecurityException ignored) {
                // Fall back to inexact allow-while-idle alarm below.
            }
        }
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi);
    }

    public static void cancelAll(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(NOTIFICATION_ID);
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            PendingIntent pi = buildAlarmPendingIntent(context, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
            if (pi != null) {
                am.cancel(pi);
                pi.cancel();
            }
        }
    }

    private static PendingIntent buildAlarmPendingIntent(Context context, int flags) {
        Intent intent = new Intent(context, DateReceiver.class);
        intent.setAction(ACTION_UPDATE_DATE);
        return PendingIntent.getBroadcast(context, REQ_ALARM, intent, flags);
    }
}
