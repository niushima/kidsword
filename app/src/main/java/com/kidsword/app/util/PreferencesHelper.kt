package com.kidsword.app.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    
    private const val PREFS_NAME = "kidsword_prefs"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_CURRENT_LEVEL = "current_level"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun isFirstLaunch(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    fun setFirstLaunchComplete(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
    
    fun getCurrentLevel(context: Context): Int {
        return getPrefs(context).getInt(KEY_CURRENT_LEVEL, 1)
    }
    
    fun setCurrentLevel(context: Context, level: Int) {
        getPrefs(context).edit().putInt(KEY_CURRENT_LEVEL, level).apply()
    }
    
    fun isSoundEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_SOUND_ENABLED, true)
    }
    
    fun setSoundEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }
}
