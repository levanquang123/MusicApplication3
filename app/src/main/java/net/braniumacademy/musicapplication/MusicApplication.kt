package net.braniumacademy.musicapplication

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import net.braniumacademy.musicapplication.ui.settings.SettingsFragment.Companion.KEY_PREF_LANGUAGE
import java.util.Locale

@HiltAndroidApp
class MusicApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupLanguage()
    }

    private fun setupLanguage() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val defaultLanguage = Locale.getDefault().language
        val language = sharedPref.getString(KEY_PREF_LANGUAGE, defaultLanguage) ?: defaultLanguage
        val localeListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(localeListCompat)
        val editor = sharedPref.edit()
        editor.putString(KEY_PREF_LANGUAGE, language)
        editor.apply()
    }
}