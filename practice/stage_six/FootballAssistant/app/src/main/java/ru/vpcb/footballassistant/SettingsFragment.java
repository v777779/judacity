package ru.vpcb.footballassistant;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);
//
//            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_data_delay_time_key)));
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_news_delay_time_key)));
        }
    }