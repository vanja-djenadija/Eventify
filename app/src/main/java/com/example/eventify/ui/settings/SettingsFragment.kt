package com.example.eventify.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.eventify.MainActivity
import com.example.eventify.NotificationOption
import com.example.eventify.R
import java.util.Locale


class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>(getString(R.string.language_preference_key))?.onPreferenceChangeListener =
            this
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        Log.i("PREFERENCE_CHANGE", preference.key)
        //Log.i("PREFERENCE", requireContext().resources.getString(R.string.notification_preference_key))

        if (preference.key.equals(requireContext().resources.getString(R.string.language_preference_key))) {
            Log.i("ETF_N", "language")
            val intent = requireActivity().intent
            requireActivity().finish()
            startActivity(intent)
        }
        if (preference.key.equals(requireContext().resources.getString(R.string.notification_preference_key))) {
            val newOptionIndex = newValue.toString().toInt()
            val newOption: NotificationOption = NotificationOption.values()[newOptionIndex]
            Log.i("ETF_N", newOption.toString())
            val mainActivity = activity as? MainActivity
            mainActivity?.handleNotificationPreferenceChange(newOption)
        }
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