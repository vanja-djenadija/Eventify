package com.example.eventify.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.eventify.R
import java.util.Locale


class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>(getString(R.string.language_preference_key))?.onPreferenceChangeListener =
            this
        findPreference<Preference>(getString(R.string.notification_preference_key))?.onPreferenceChangeListener =
            this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)
        return true
    }

    private fun setLocale(language: String, context: Context) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config: Configuration = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val language = sharedPreferences.getString(
            "language",
            context.resources.getString(R.string.language_preference_default)
        )
        setLocale(language!!, context)
    }
}