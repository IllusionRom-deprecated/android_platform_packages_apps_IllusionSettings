/*
 *  Copyright (C) 2013 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.illusion.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DisplayInfo;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import org.illusion.settings.R;
import org.illusion.settings.SettingsPreferenceFragment;
import org.illusion.settings.Utils;


public class BarsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "BarsSettings";

    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String QUICK_PULLDOWN = "quick_pulldown";
    private static final String QUICKSETTINGS_DYNAMIC = "quicksettings_dynamic_row";
    private static final String STATUS_BAR_TRAFFIC = "status_bar_traffic";
    private static final String STATUS_BAR_CUSTOM_HEADER = "custom_status_bar_header";
    private static final String SMART_PULLDOWN = "smart_pulldown";
    private static final String DOUBLE_TAP_TO_SLEEP = "double_tap_to_sleep";

    // Device types
    private static final int DEVICE_PHONE  = 0;
    private static final int DEVICE_HYBRID = 1;
    private static final int DEVICE_TABLET = 2;

    private CheckBoxPreference mStatusBarBrightnessControl;
    private ListPreference mQuickPulldown;
    private ListPreference mSmartPulldown;
    private CheckBoxPreference mStatusBarTraffic;
    private CheckBoxPreference mStatusBarCustomHeader;
    private CheckBoxPreference mQuickSettingsDynamic;
    private CheckBoxPreference mDoubleTapGesture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.bars_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
	 mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(resolver,Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        mStatusBarBrightnessControl.setOnPreferenceChangeListener(this);
        mQuickPulldown = (ListPreference) findPreference(QUICK_PULLDOWN);
        mSmartPulldown = (ListPreference) findPreference(SMART_PULLDOWN);

        int quickPulldown = Settings.System.getInt(resolver,
        Settings.System.QS_QUICK_PULLDOWN, 0);
        mQuickPulldown.setValue(String.valueOf(quickPulldown));
        updateQuickPulldownSummary(quickPulldown);
        mQuickPulldown.setOnPreferenceChangeListener(this);

        int smartPulldown = Settings.System.getInt(resolver,
        Settings.System.QS_SMART_PULLDOWN, 0);
        mSmartPulldown.setValue(String.valueOf(smartPulldown));
        updateSmartPulldownSummary(smartPulldown);
        mSmartPulldown.setOnPreferenceChangeListener(this);

        mQuickSettingsDynamic = (CheckBoxPreference) prefSet.findPreference(QUICKSETTINGS_DYNAMIC);
        mQuickSettingsDynamic.setChecked(Settings.System.getInt(resolver,
            Settings.System.QUICK_SETTINGS_TILES_ROW, 1) != 0);
        mQuickSettingsDynamic.setOnPreferenceChangeListener(this);

         try {
             if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                 mStatusBarBrightnessControl.setEnabled(false);
                 mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
             }
         } catch (SettingNotFoundException e) {
        }
        mStatusBarTraffic = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_TRAFFIC);
        mStatusBarTraffic.setChecked(Settings.System.getInt(resolver,Settings.System.STATUS_BAR_TRAFFIC, 0) == 1);
        mStatusBarTraffic.setOnPreferenceChangeListener(this);

        mStatusBarCustomHeader = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_CUSTOM_HEADER);
        mStatusBarCustomHeader.setChecked(Settings.System.getInt(resolver,Settings.System.STATUS_BAR_CUSTOM_HEADER, 0) == 1);
        mStatusBarCustomHeader.setOnPreferenceChangeListener(this);

	mDoubleTapGesture = (CheckBoxPreference) prefSet.findPreference(DOUBLE_TAP_TO_SLEEP);
        mDoubleTapGesture.setChecked(Settings.System.getInt(resolver,Settings.System.DOUBLE_TAP_TO_SLEEP, 0) == 1);
        mDoubleTapGesture.setOnPreferenceChangeListener(this);
	}

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return true;
    }
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
		if (preference == mStatusBarBrightnessControl) {
      		      boolean value = (Boolean) objValue;
        	      Settings.System.putInt(resolver,Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
		} else if (preference == mQuickPulldown) {
		      int quickPulldown = Integer.valueOf((String) objValue);
                    Settings.System.putInt(resolver,Settings.System.QS_QUICK_PULLDOWN, quickPulldown);
			updateQuickPulldownSummary(quickPulldown);
		} else if (preference == mQuickSettingsDynamic) {
 	              boolean value = (Boolean) objValue;
                     Settings.System.putInt(resolver,
                     Settings.System.QUICK_SETTINGS_TILES_ROW, value ? 1 : 0);
		} else if (preference == mSmartPulldown) {
             		int smartPulldown = Integer.valueOf((String) objValue);
             		Settings.System.putInt(resolver, Settings.System.QS_SMART_PULLDOWN, smartPulldown);
  		       updateSmartPulldownSummary(smartPulldown);
		} else if (preference == mStatusBarTraffic) {
            	      boolean value = (Boolean) objValue;
            	      Settings.System.putInt(resolver,Settings.System.STATUS_BAR_TRAFFIC, value ? 1 : 0);
		} else if (preference == mStatusBarCustomHeader) {
            	      boolean value = (Boolean) objValue;
	             Settings.System.putInt(resolver,Settings.System.STATUS_BAR_CUSTOM_HEADER, value ? 1 : 0);
		} else if (preference == mDoubleTapGesture) {
		      boolean value = (Boolean) objValue;
		      Settings.System.putInt(resolver,Settings.System.DOUBLE_TAP_TO_SLEEP, value ? 1 : 0);
		}
	  else {
            return false;
	       }
	return true;
	}
    private void updateQuickPulldownSummary(int i) {
        if (i == 0) {
            mQuickPulldown.setSummary(R.string.quick_pulldown_off);
        } else if (i == 1) {
            mQuickPulldown.setSummary(R.string.quick_pulldown_right);
        } else if (i == 2) {
            mQuickPulldown.setSummary(R.string.quick_pulldown_left);
        } else if (i == 3) {
            mQuickPulldown.setSummary(R.string.quick_pulldown_centre);
        }
    }

    private void updateSmartPulldownSummary(int i) {
        if (i == 0) {
            mSmartPulldown.setSummary(R.string.smart_pulldown_off);
        } else if (i == 1) {
            mSmartPulldown.setSummary(R.string.smart_pulldown_dismissable);
        } else if (i == 2) {
            mSmartPulldown.setSummary(R.string.smart_pulldown_persistent);
        }
    }

    private static int getScreenType(Context con) {
        WindowManager wm = (WindowManager) con.getSystemService(Context.WINDOW_SERVICE);
        DisplayInfo outDisplayInfo = new DisplayInfo();
        wm.getDefaultDisplay().getDisplayInfo(outDisplayInfo);
        int shortSize = Math.min(outDisplayInfo.logicalHeight, outDisplayInfo.logicalWidth);
        int shortSizeDp =
            shortSize * DisplayMetrics.DENSITY_DEFAULT / outDisplayInfo.logicalDensityDpi;
        if (shortSizeDp < 600) {
            return DEVICE_PHONE;
        } else if (shortSizeDp < 720) {
            return DEVICE_HYBRID;
        } else {
            return DEVICE_TABLET;
        }
    }

    public static boolean isPhone(Context con) {
        return getScreenType(con) == DEVICE_PHONE;
    }
}
