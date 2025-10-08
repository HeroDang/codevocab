import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val PREFS = "app_prefs"
    private const val KEY_THEME = "pref_theme" // "system" | "light" | "dark"

    fun init(context: Context) {
        val mode = getSavedMode(context)
        applyMode(mode)
    }

    fun saveAndApply(context: Context, mode: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_THEME, mode).apply()
        applyMode(mode)
    }

    private fun getSavedMode(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_THEME, "system") ?: "system"
    }

    private fun applyMode(mode: String) {
        val modeInt = when (mode) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark"  -> AppCompatDelegate.MODE_NIGHT_YES
            else    -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(modeInt)
    }
}
