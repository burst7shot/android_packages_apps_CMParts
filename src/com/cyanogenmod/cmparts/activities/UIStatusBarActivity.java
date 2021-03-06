/*
 * Copyright (C) 2011 The CyanogenMod Project
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

package com.cyanogenmod.cmparts.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Window;
import android.widget.Toast;
import java.util.ArrayList;
import android.provider.MediaStore;

import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.widget.EditText;
import android.net.Uri;
import com.cyanogenmod.cmparts.utils.CMDProcessor;
import com.cyanogenmod.cmparts.utils.Helpers;

import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import android.util.Log;
import java.util.logging.Level;

import com.cyanogenmod.cmparts.R;
import com.cyanogenmod.cmparts.activities.ColorPickerDialog.OnColorChangedListener;

public class UIStatusBarActivity extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String PREF_STATUS_BAR_AM_PM = "pref_status_bar_am_pm";

    private static final String PREF_RESTART_STATUS_BAR = "pref_restart_status_bar";

    private static final String PREF_STATUS_BAR_BATTERY = "pref_status_bar_battery";

    private static final String PREF_STATUS_BAR_BATTERY_COLOR = "pref_status_bar_battery_color";

    private static final String PREF_STATUS_BAR_CLOCK = "pref_status_bar_clock";

    private static final String PREF_STATUS_BAR_CENTERCLOCK = "pref_status_bar_centerclock";

    private static final String PREF_STATUS_BAR_CLOCKCOLOR = "pref_status_bar_clockcolor";

    private static final String PREF_STATUS_BAR_ALARM = "pref_status_bar_alarm";

    private static final String PREF_STATUS_BAR_CARRIER_LABEL =
            "pref_status_bar_carrier_label";

    private static final String PREF_STATUS_BAR_CARRIER_LABEL_CUSTOM =
            "pref_status_bar_carrier_label_custom";

    private static final String PREF_STATUS_BAR_COMPACT_CARRIER = "pref_status_bar_compact_carrier";

    private static final String PREF_STATUS_BAR_COLOR = "pref_status_bar_color";

    private static final String PREF_TRANSPARENT_STATUS_BAR = "pref_transparent_status_bar";

    private static final String PREF_NOTIFICATION_BACKGROUND_COLOR = "pref_notification_background_color";

    private static final String PREF_TRANSPARENT_NOTIFICATION_BACKGROUND = "pref_transparent_notification_background";

    private static final String PREF_STATUS_BAR_BRIGHTNESS_CONTROL =
            "pref_status_bar_brightness_control";

    private static final String PREF_STATUS_BAR_CM_SIGNAL = "pref_status_bar_cm_signal";

    private static final String PREF_STATUS_BAR_HEADSET = "pref_status_bar_headset";

    private static final String COPY_BACKGROUND_INTENT = "com.cyanogenmod.cmbackgroundchooser.COPY_BACKGROUND";

    private static final int REQUEST_CODE_PICK_FILE = 999;

    private ListPreference mStatusBarAmPm;

    private Preference mRestartStatusBar;

    private ListPreference mStatusBarBattery;

    private ListPreference mStatusBarBatteryColor;

    private ListPreference mStatusBarCmSignal;

    private ListPreference mStatusBarCarrierLabel;

    private ListPreference mTransparentStatusBarPref;

    private ListPreference mTransparentNotificationBackgroundPref;

    private CheckBoxPreference mStatusBarClock;

    private CheckBoxPreference mStatusBarCenterClock;

    private Preference mStatusBarClockColor;

    private Preference mStatusBarColor;

    private Preference mNotificationBackgroundColor;

    private CheckBoxPreference mStatusBarAlarm;

    private CheckBoxPreference mStatusBarCompactCarrier;

    private CheckBoxPreference mStatusBarBrightnessControl;

    private CheckBoxPreference mStatusBarHeadset;

    private EditTextPreference mStatusBarCarrierLabelCustom;

    private File notificationBackgroundImage;

    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.ui_status_bar_title);
        addPreferencesFromResource(R.xml.ui_status_bar);

        PreferenceScreen prefSet = getPreferenceScreen();

        mRestartStatusBar = (Preference) prefSet.findPreference(PREF_RESTART_STATUS_BAR);
        mStatusBarClock = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_CLOCK);
        mStatusBarCenterClock = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_CENTERCLOCK);
        mStatusBarClockColor = (Preference) prefSet.findPreference(PREF_STATUS_BAR_CLOCKCOLOR);
        mStatusBarClockColor.setOnPreferenceChangeListener(this);
        mStatusBarColor = (Preference) prefSet.findPreference(PREF_STATUS_BAR_COLOR);
        mStatusBarColor.setOnPreferenceChangeListener(this);
        mNotificationBackgroundColor = (Preference) prefSet.findPreference(PREF_NOTIFICATION_BACKGROUND_COLOR);
        mNotificationBackgroundColor.setOnPreferenceChangeListener(this);
        mStatusBarAlarm = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_ALARM);
        mStatusBarCompactCarrier = (CheckBoxPreference) prefSet
                .findPreference(PREF_STATUS_BAR_COMPACT_CARRIER);
        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet
                .findPreference(PREF_STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarHeadset = (CheckBoxPreference) prefSet
                .findPreference(PREF_STATUS_BAR_HEADSET);
        mStatusBarClock.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCK, 1) == 1));
        mStatusBarCenterClock.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CENTERCLOCK, 1) == 1));
        mStatusBarAlarm.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_ALARM, 1) == 1));
        mStatusBarCompactCarrier.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COMPACT_CARRIER, 0) == 1));
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_TOGGLE, 0) == 1));
        mStatusBarHeadset.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_HEADSET, 1) == 1));

        try {
            if (Settings.System.getInt(getContentResolver(), 
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.ui_status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        int clockColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CLOCKCOLOR, 0);
        mStatusBarClockColor.setSummary(Integer.toHexString(clockColor));

        int transparentStatusBarPref = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_STATUS_BAR, 0);
        mTransparentStatusBarPref = (ListPreference) prefSet.findPreference(PREF_TRANSPARENT_STATUS_BAR);
        mTransparentStatusBarPref.setValue(String.valueOf(transparentStatusBarPref));
        mTransparentStatusBarPref.setOnPreferenceChangeListener(this);

        int statusBarColor = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COLOR, 0);
        mStatusBarColor.setSummary(Integer.toHexString(statusBarColor));
        mStatusBarColor.setEnabled(transparentStatusBarPref == 2);

        int transparentNotificationBackgroundPref = Settings.System.getInt(getContentResolver(),
                Settings.System.TRANSPARENT_NOTIFICATION_BACKGROUND, 0);
        mTransparentNotificationBackgroundPref = (ListPreference) prefSet.findPreference(PREF_TRANSPARENT_NOTIFICATION_BACKGROUND);
        mTransparentNotificationBackgroundPref.setValue(String.valueOf(transparentNotificationBackgroundPref));
        mTransparentNotificationBackgroundPref.setOnPreferenceChangeListener(this);

        int notificationBackgroundColor = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_BACKGROUND_COLOR, 0);
        mNotificationBackgroundColor.setSummary(Integer.toHexString(notificationBackgroundColor));
        mNotificationBackgroundColor.setEnabled(transparentNotificationBackgroundPref == 2);
        notificationBackgroundImage = new File(getApplicationContext().getFilesDir()+"/nb_background");

        mStatusBarAmPm = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_AM_PM);
        mStatusBarBattery = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_BATTERY);
        mStatusBarBatteryColor = (ListPreference) prefSet.
                findPreference(PREF_STATUS_BAR_BATTERY_COLOR);
        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(PREF_STATUS_BAR_CM_SIGNAL);

        int statusBarAmPm = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_AM_PM, 2);
        mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
        mStatusBarAmPm.setOnPreferenceChangeListener(this);

        int statusBarBattery = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY, 0);
        mStatusBarBattery.setValue(String.valueOf(statusBarBattery));
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        mStatusBarBatteryColor.setValue(Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_COLOR));
        mStatusBarBatteryColor.setOnPreferenceChangeListener(this);
        mStatusBarBatteryColor.setEnabled(statusBarBattery == 3);

        int signalStyle = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_CM_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mStatusBarCarrierLabel = (ListPreference) prefSet
                .findPreference(PREF_STATUS_BAR_CARRIER_LABEL);
        mStatusBarCarrierLabelCustom = (EditTextPreference) prefSet
                .findPreference(PREF_STATUS_BAR_CARRIER_LABEL_CUSTOM);

        if (mStatusBarCarrierLabelCustom != null) {
            EditText carrierEditText = mStatusBarCarrierLabelCustom.getEditText();

            if (carrierEditText != null) {
                InputFilter lengthFilter = new InputFilter.LengthFilter(20);
                carrierEditText.setFilters(new InputFilter[]{lengthFilter});
                carrierEditText.setSingleLine(true);
            }
        }

        int statusBarCarrierLabel = Settings.System.getInt(getContentResolver(),
                Settings.System.CARRIER_LABEL_TYPE, 0);
        String statusBarCarrierLabelCustom = Settings.System.getString(getContentResolver(),
                Settings.System.CARRIER_LABEL_CUSTOM_STRING);

        if (statusBarCarrierLabelCustom == null) {
            statusBarCarrierLabelCustom = "BurstKANG@HIAPK";
            Settings.System.putString(getContentResolver(),
                    Settings.System.CARRIER_LABEL_CUSTOM_STRING,
                    statusBarCarrierLabelCustom);
        }

        mStatusBarCarrierLabel.setValue(String.valueOf(statusBarCarrierLabel));
        mStatusBarCarrierLabel.setOnPreferenceChangeListener(this);

        mStatusBarCarrierLabelCustom.setText(statusBarCarrierLabelCustom);
        mStatusBarCarrierLabelCustom.setOnPreferenceChangeListener(this);
        mStatusBarCarrierLabelCustom.setEnabled(
                statusBarCarrierLabel == 3);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_AM_PM,
                    statusBarAmPm);
            return true;
        } else if (preference == mStatusBarBattery) {
            int statusBarBattery = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_BATTERY,
                    statusBarBattery);
            mStatusBarBatteryColor.setEnabled(statusBarBattery == 3);
            return true;
        } else if (preference == mStatusBarBatteryColor) {
            String statusBarBatteryColor = (String) newValue;
            if ("custom".equals(statusBarBatteryColor)) {
                int color = -1;
                String colorString = Settings.System.getString(getContentResolver(),
                        Settings.System.STATUS_BAR_BATTERY_COLOR);
                if (!TextUtils.isEmpty(colorString)) {
                    try {
                        color = Color.parseColor(colorString);
                    } catch (IllegalArgumentException e) { }
                    new ColorPickerDialog(this, mColorChangedListener, color).show();
                }
            } else {
                Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_BATTERY_COLOR, statusBarBatteryColor);
            }
            return true;
        } else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CM_SIGNAL_TEXT,
                    signalStyle);
            return true;
        } else if (preference == mStatusBarCarrierLabel) {
            int carrierLabelType = Integer.valueOf((String) newValue);
            mStatusBarCarrierLabelCustom.setEnabled(carrierLabelType == 3);
            Settings.System.putInt(getContentResolver(), Settings.System.CARRIER_LABEL_TYPE,
                    carrierLabelType);
            return true;
        } else if (preference == mStatusBarCarrierLabelCustom) {
            String carrierLabelCustom = String.valueOf(newValue);
            Settings.System.putString(getContentResolver(),
                    Settings.System.CARRIER_LABEL_CUSTOM_STRING,
                    carrierLabelCustom);
            return true;
        } else if (preference == mTransparentStatusBarPref) {
            final CMDProcessor cmd = new CMDProcessor();
            int transparentStatusBarPref = Integer.parseInt(String.valueOf(newValue));
            mStatusBarColor.setEnabled(transparentStatusBarPref == 2);
            Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_STATUS_BAR,
                    transparentStatusBarPref);
            new AlertDialog.Builder(this)
            .setTitle(R.string.reboot_notice_title)
            .setMessage(R.string.reboot_notice_summary)
            .setPositiveButton(com.android.internal.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                             cmd.su.runWaitFor("pkill -TERM -f  com.android.systemui");
                    }
            })
            .setNegativeButton(com.android.internal.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
             })
            .show();
            return true;
        } else if (preference == mTransparentNotificationBackgroundPref) {
            final CMDProcessor cmd = new CMDProcessor();
            int transparentNotificationBackgroundPref = Integer.parseInt(String.valueOf(newValue));
            if (transparentNotificationBackgroundPref == 5) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                int width = getWindowManager().getDefaultDisplay().getWidth();
                int height = getWindowManager().getDefaultDisplay().getHeight();
                Rect rect = new Rect();
                Window window = getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                int statusBarHeight = rect.top;
                int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                int titleBarHeight = contentViewTop - statusBarHeight;
                boolean isPortrait = getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT;
                intent.putExtra("aspectX", isPortrait ? width : height - titleBarHeight);
                intent.putExtra("aspectY", isPortrait ? height - titleBarHeight : width);
                try {
                    notificationBackgroundImage.createNewFile();
                    notificationBackgroundImage.setReadable(true, false);
                    notificationBackgroundImage.setWritable(true, false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(notificationBackgroundImage));
                    intent.putExtra("return-data", false);
                    startActivityForResult(intent,REQUEST_CODE_PICK_FILE);
                } catch (IOException e) {
                    Log.e("Picker", "IOException: ", e);
                } catch (ActivityNotFoundException e) {
                    Log.e("Picker", "ActivityNotFoundException: ", e);
                }
            }
            mNotificationBackgroundColor.setEnabled(transparentNotificationBackgroundPref == 2);
            Settings.System.putInt(getContentResolver(), Settings.System.TRANSPARENT_NOTIFICATION_BACKGROUND,
                    transparentNotificationBackgroundPref);
            new AlertDialog.Builder(this)
            .setTitle(R.string.reboot_notice_title)
            .setMessage(R.string.reboot_notice_summary)
            .setPositiveButton(com.android.internal.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                             cmd.su.runWaitFor("pkill -TERM -f  com.android.systemui");
                    }
            })
            .setNegativeButton(com.android.internal.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
             })
            .show();
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        /* Preference Screens */
        if(preference == mRestartStatusBar) {
            // attempt to restart the status bar
            // it should restart on it's own after being killed, but we
            // will make an attempt anyway to be sure, and our attempt
            // will be aborted by the system if it systemui is already running
            String[] str1 ={"su","-c","killall com.android.systemui"};
            final String[] str2 ={"su","-c","am","startservice","-n","com.android.systemui/com.android.systemui.statusbar.StatusBarService"};
            try {
                // kill the status bar service (com.android.systemui)
                // --first we disable the GUI restart element to prevent the
                // --user from pressing it repeatedly despite warnings
                // TODO: enable/disabled gui element if systemui service is running/stopped
                mRestartStatusBar.setEnabled(false);
                mRestartStatusBar.setSummary(R.string.reboot_notice_summary2);
                Runtime.getRuntime().exec(str1);
                // schedule an attempt to restart the status bar service
                Handler handleRestartSystemUI = new Handler();
                handleRestartSystemUI.postDelayed(new Runnable() {
                    public void run() {
                        mRestartStatusBar.setSummary(R.string.reboot_notice_summary3);
                        try {
                            Runtime.getRuntime().exec(str2);
                        } catch (Exception e){
                            Log.e("startservice", "am startservice failed to restart systemui", e);
                        }
                    }
                }, 8000);
                // --set a delay before re-enabling this gui element
                Handler handleReenableGUIElement = new Handler();
                handleReenableGUIElement.postDelayed(new Runnable() {
                    public void run() {
                        mRestartStatusBar.setSummary(R.string.reboot_notice_summary4);
                        mRestartStatusBar.setEnabled(true);
                    }
                }, 20000);
                Toast.makeText(getApplicationContext(), R.string.reboot_notice_summary4 ,Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Log.e("killall", "failed to restart statusbar", e);
            }
        } else if (preference == mStatusBarClock) {
            value = mStatusBarClock.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CLOCK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarCenterClock) {
            value = mStatusBarCenterClock.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CENTERCLOCK,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarClockColor) {
            ColorPickerDialog cp = new ColorPickerDialog(this, mClockColorListener, getClockColor());
            cp.show();
            return true;
        } else if (preference == mStatusBarAlarm) {
            value = mStatusBarAlarm.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_ALARM,
                    value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarCompactCarrier) {
            value = mStatusBarCompactCarrier.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_COMPACT_CARRIER, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarColor) {
            SBColorPickerDialog sbcp = new SBColorPickerDialog(this, mStatusBarColorListener, getStatusBarColor());
            sbcp.show();
            return true;
        } else if (preference == mNotificationBackgroundColor) {
            NBColorPickerDialog nbcp = new NBColorPickerDialog(this, mNotificationBackgroundColorListener, getNotificationBackgroundColor());
            nbcp.show();
            return true; 
        } else if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_TOGGLE, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarHeadset) {
            value = mStatusBarHeadset.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_HEADSET,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }

    private OnColorChangedListener mColorChangedListener = new OnColorChangedListener() {
        @Override
        public void colorChanged(int color) {
            String colorString = String.format("#%02x%02x%02x", Color.red(color),
                    Color.green(color), Color.blue(color));
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_COLOR, colorString);
        }

        @Override
        public void colorUpdate(int color) {
            // no-op
        }
    };

    private int getStatusBarColor() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_COLOR, 1);
    }

    SBColorPickerDialog.OnColorChangedListener mStatusBarColorListener =
        new SBColorPickerDialog.OnColorChangedListener() {
            public void SBcolorChanged(int SBcolor) {
                Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_COLOR, SBcolor);
                mStatusBarColor.setSummary(Integer.toHexString(SBcolor));
            }
            public void SBcolorUpdate(int SBcolor) {
            }
    };

    private int getNotificationBackgroundColor() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_BACKGROUND_COLOR, 1);
    }

    NBColorPickerDialog.OnColorChangedListener mNotificationBackgroundColorListener =
        new NBColorPickerDialog.OnColorChangedListener() {
            public void NBcolorChanged(int NBcolor) {
                Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_BACKGROUND_COLOR, NBcolor);
                mNotificationBackgroundColor.setSummary(Integer.toHexString(NBcolor));
            }
            public void NBcolorUpdate(int NBcolor) {
            }
    };

    private int getClockColor() {
        try {
            return Settings.System.getInt(getContentResolver(),
                     Settings.System.STATUS_BAR_CLOCKCOLOR);
        } catch (SettingNotFoundException e) {
            return -16777216;
        }
    }

    ColorPickerDialog.OnColorChangedListener mClockColorListener =
        new ColorPickerDialog.OnColorChangedListener() {
            public void colorChanged(int color) {
                Settings.System.putInt(getContentResolver(), Settings.System.STATUS_BAR_CLOCKCOLOR, color);
                mStatusBarClockColor.setSummary(Integer.toHexString(color));
            }
            public void colorUpdate(int color) {
            }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context = getApplicationContext();
        switch (requestCode) {
            case REQUEST_CODE_PICK_FILE:
                if (resultCode != RESULT_OK) {
                    Log.d("Copy_Notification_Error", "Error: " + resultCode);
                } else {
                    Toast.makeText(context, "Notification background set to new image" ,Toast.LENGTH_LONG).show();
                }
            break;
        }
    }


}
