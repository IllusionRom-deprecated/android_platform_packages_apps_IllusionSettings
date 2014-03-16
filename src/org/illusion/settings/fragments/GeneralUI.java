package org.illusion.settings.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

import org.illusion.settings.SettingsPreferenceFragment;
import org.illusion.settings.R;

public class GeneralUI extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "General User Interface";
    private static final String POWER_MENU_SCREENSHOT = "power_menu_screenshot";
    private static final String POWER_MENU_SCREENRECORD = "power_menu_screenrecord";
    private static final String POWER_MENU_ONTHEGO_ENABLED = "power_menu_onthego_enabled";

    private ContentResolver resolver;

    private CheckBoxPreference mScreenshotPowerMenu;
    private CheckBoxPreference mScreenrecordPowerMenu;
    private CheckBoxPreference mOnTheGoPowerMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_generalui);

        addPreferencesFromResource(R.xml.general_ui_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
	 resolver = getActivity().getContentResolver();

       boolean mHasScreenRecord = getActivity().getResources().getBoolean(
                com.android.internal.R.bool.config_enableScreenrecordChord);

        mScreenshotPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_SCREENSHOT);
        mScreenshotPowerMenu.setChecked(Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_IN_POWER_MENU, 0) == 1);
        mScreenshotPowerMenu.setOnPreferenceChangeListener(this);

        mOnTheGoPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_ONTHEGO_ENABLED);
        mOnTheGoPowerMenu.setChecked(Settings.System.getInt(resolver,
                Settings.System.POWER_MENU_ONTHEGO_ENABLED, 0) == 1);
        mOnTheGoPowerMenu.setOnPreferenceChangeListener(this);

        mScreenrecordPowerMenu = (CheckBoxPreference) prefSet.findPreference(POWER_MENU_SCREENRECORD);
        if(mHasScreenRecord) {
            mScreenrecordPowerMenu.setChecked(Settings.System.getInt(resolver,
                    Settings.System.SCREENRECORD_IN_POWER_MENU, 0) == 1);
           mScreenrecordPowerMenu.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mScreenrecordPowerMenu);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
	   if (preference == mScreenshotPowerMenu) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver, Settings.System.SCREENSHOT_IN_POWER_MENU, value ? 1 : 0);
       } else if (preference == mOnTheGoPowerMenu) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver, Settings.System.POWER_MENU_ONTHEGO_ENABLED, value ? 1 : 0);
       } else if (preference == mScreenrecordPowerMenu) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver, Settings.System.SCREENRECORD_IN_POWER_MENU, value ? 1 : 0);
        }else {
            return false;
        }
        return true;
    }
}
