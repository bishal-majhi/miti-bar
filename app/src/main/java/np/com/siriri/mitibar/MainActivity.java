package np.com.siriri.mitibar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.TimeZone;

public class MainActivity extends Activity {

    private static final int REQ_NOTIF = 101;
    private static final int COLOR_BG = 0xFFF4F6FA;
    private static final int COLOR_NAVY = 0xFF0D1D2D;
    private static final int COLOR_CRIMSON = 0xFFD11339;
    private static final int COLOR_MUTED = 0xFF64748B;
    private static final int COLOR_SUBTLE = 0xFF94A3B8;
    private static final int COLOR_BORDER = 0xFFE2E8F0;
    private static final int COLOR_GREEN = 0xFF15803D;

    private TextView dateView;
    private TextView weekdayView;
    private TextView statusView;
    private TextView modeView;
    private Button actionButton;
    private Button timezoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(COLOR_BG);
        scroll.setFitsSystemWindows(true);

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.setGravity(Gravity.CENTER);
        outer.setPadding(dp(24), dp(32), dp(24), dp(32));
        scroll.addView(outer, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        int containerWidth = Math.min(getResources().getDisplayMetrics().widthPixels - dp(48), dp(360));
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setGravity(Gravity.CENTER_HORIZONTAL);
        outer.addView(column, new LinearLayout.LayoutParams(containerWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView logo = new ImageView(this);
        logo.setImageDrawable(getPackageManager().getApplicationIcon(getApplicationInfo()));
        GradientDrawable logoBg = new GradientDrawable();
        logoBg.setCornerRadius(dp(16));
        logo.setBackground(logoBg);
        logo.setClipToOutline(true);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(dp(64), dp(64));
        logoParams.bottomMargin = dp(14);
        column.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText("Miti Bar");
        title.setTextSize(24f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(COLOR_NAVY);
        title.setGravity(Gravity.CENTER);
        column.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Nepali Date Status");
        subtitle.setTextSize(14f);
        subtitle.setTextColor(COLOR_MUTED);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        subParams.bottomMargin = dp(24);
        column.addView(subtitle, subParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(24), dp(24), dp(24), dp(22));
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dp(20));
        cardBg.setStroke(dp(1), COLOR_BORDER);
        card.setBackground(cardBg);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dp(18);
        column.addView(card, cardParams);

        TextView todayLabel = new TextView(this);
        todayLabel.setText("आजको मिति");
        todayLabel.setTextSize(13f);
        todayLabel.setTextColor(COLOR_MUTED);
        todayLabel.setGravity(Gravity.CENTER);
        card.addView(todayLabel);

        dateView = new TextView(this);
        dateView.setTextSize(26f);
        dateView.setTypeface(Typeface.DEFAULT_BOLD);
        dateView.setTextColor(COLOR_NAVY);
        dateView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dateParams.topMargin = dp(6);
        card.addView(dateView, dateParams);

        weekdayView = new TextView(this);
        weekdayView.setTextSize(16f);
        weekdayView.setTypeface(Typeface.DEFAULT_BOLD);
        weekdayView.setTextColor(COLOR_CRIMSON);
        weekdayView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams wdParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        wdParams.topMargin = dp(4);
        card.addView(weekdayView, wdParams);

        View divider = new View(this);
        divider.setBackgroundColor(COLOR_BORDER);
        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        divParams.topMargin = dp(18);
        divParams.bottomMargin = dp(16);
        card.addView(divider, divParams);

        statusView = new TextView(this);
        statusView.setTextSize(14f);
        statusView.setTypeface(Typeface.DEFAULT_BOLD);
        statusView.setGravity(Gravity.CENTER);
        card.addView(statusView);

        modeView = new TextView(this);
        modeView.setTextSize(13f);
        modeView.setTextColor(COLOR_MUTED);
        modeView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams modeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        modeParams.topMargin = dp(6);
        card.addView(modeView, modeParams);

        actionButton = createButton(COLOR_NAVY, Color.WHITE);
        LinearLayout.LayoutParams actionParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        actionParams.bottomMargin = dp(10);
        column.addView(actionButton, actionParams);

        timezoneButton = createButton(0xFFE7EDF4, COLOR_NAVY);
        column.addView(timezoneButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView footer = new TextView(this);
        footer.setText("0 MB background RAM • Midnight auto-refresh");
        footer.setTextSize(12f);
        footer.setTextColor(COLOR_SUBTLE);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        footParams.topMargin = dp(18);
        column.addView(footer, footParams);

        setContentView(scroll);

        if (DateReceiver.isWidgetEnabled(this) && !DateReceiver.isNotificationPermitted(this)
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
        } else if (DateReceiver.isWidgetEnabled(this) && DateReceiver.isNotificationPermitted(this)) {
            activateWidget();
        }
    }

    private Button createButton(int bgColor, int textColor) {
        Button btn = new Button(this);
        btn.setAllCaps(false);
        btn.setTextSize(14f);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setTextColor(textColor);
        btn.setPadding(dp(20), dp(14), dp(20), dp(14));
        btn.setStateListAnimator(null);
        styleButton(btn, bgColor, textColor);
        return btn;
    }

    private void styleButton(Button btn, int bgColor, int textColor) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        bg.setCornerRadius(dp(14));
        btn.setBackground(bg);
        btn.setTextColor(textColor);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DateReceiver.isWidgetEnabled(this) && DateReceiver.isNotificationPermitted(this)) {
            activateWidget();
        }
        updateUi();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIF) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activateWidget();
            }
            updateUi();
        }
    }

    private void activateWidget() {
        DateReceiver.showNotification(this);
        DateReceiver.scheduleNextMidnightAlarm(this);
    }

    private void updateUi() {
        final boolean useNepalTime = DateReceiver.isUseNepalTime(this);
        BsCalendar.BsDateResult today = BsCalendar.getToday(useNepalTime);
        boolean enabled = DateReceiver.isWidgetEnabled(this);
        boolean permitted = DateReceiver.isNotificationPermitted(this);

        dateView.setText(today.dateOnly);
        weekdayView.setText(today.weekday);

        String tzLabel = useNepalTime
                ? "Timezone: Nepal Time (Asia/Kathmandu)"
                : "Timezone: Local (" + TimeZone.getDefault().getID() + ")";
        modeView.setText(tzLabel);

        timezoneButton.setText(useNepalTime ? "Switch to Local Phone Time" : "Switch to Nepal Time (NPT)");
        timezoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateReceiver.setUseNepalTime(MainActivity.this, !useNepalTime);
                updateUi();
            }
        });

        if (!enabled) {
            statusView.setText("○ Status Bar & Lockscreen paused");
            statusView.setTextColor(COLOR_MUTED);
            styleButton(actionButton, COLOR_NAVY, Color.WHITE);
            actionButton.setText("Enable Status Bar Date");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                            && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        DateReceiver.setWidgetEnabled(MainActivity.this, true);
                        requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
                    } else {
                        DateReceiver.setWidgetEnabled(MainActivity.this, true);
                        updateUi();
                    }
                }
            });
        } else if (!permitted) {
            statusView.setText("Notification permission required");
            statusView.setTextColor(COLOR_CRIMSON);
            styleButton(actionButton, COLOR_CRIMSON, Color.WHITE);
            actionButton.setText("Open Notification Settings");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(intent);
                }
            });
        } else {
            statusView.setText("● Active in Status Bar & Lockscreen");
            statusView.setTextColor(COLOR_GREEN);
            styleButton(actionButton, 0xFFFDE8EC, COLOR_CRIMSON);
            actionButton.setText("Disable Status Bar Date");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateReceiver.setWidgetEnabled(MainActivity.this, false);
                    updateUi();
                }
            });
        }
    }
}
