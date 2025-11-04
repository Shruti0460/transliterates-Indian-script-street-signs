package com.example.street_light.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.street_light.data.model.IndianScript

/**
 * Manages app preferences using SharedPreferences
 */
class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var preferredScript: IndianScript
        get() {
            val isoCode = prefs.getString(KEY_PREFERRED_SCRIPT, IndianScript.DEVANAGARI.isoCode) 
                ?: IndianScript.DEVANAGARI.isoCode
            return IndianScript.fromIsoCode(isoCode) ?: IndianScript.DEVANAGARI
        }
        set(value) {
            prefs.edit().putString(KEY_PREFERRED_SCRIPT, value.isoCode).apply()
        }

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) {
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
        }

    var fontSizeMultiplier: Float
        get() = prefs.getFloat(KEY_FONT_SIZE, 1.0f)
        set(value) {
            prefs.edit().putFloat(KEY_FONT_SIZE, value).apply()
        }

    companion object {
        private const val PREFS_NAME = "streetlight_prefs"
        private const val KEY_PREFERRED_SCRIPT = "preferred_script"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_FONT_SIZE = "font_size"
    }
}

