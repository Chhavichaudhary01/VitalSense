package com.vitalsense.app.core.util

import android.content.Context

/**
 * Persists dismissed advisories, cleared emergency SOS alerts, and dismissed directives
 * across application restarts using SharedPreferences.
 */
object DismissedNoticeHelper {
    private const val PREFS_NAME = "vitalsense_dismissed_notices_prefs"
    private const val KEY_DISMISSED_ADVISORIES = "dismissed_advisories_"
    private const val KEY_CLEARED_SOS = "cleared_sos_ids"
    private const val KEY_DISMISSED_DIRECTIVES = "dismissed_directive_ids"

    fun getDismissedAdvisoryIds(context: Context, role: String): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet("$KEY_DISMISSED_ADVISORIES$role", emptySet()) ?: emptySet()
    }

    fun dismissAdvisory(context: Context, role: String, noticeId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getDismissedAdvisoryIds(context, role).toMutableSet()
        current.add(noticeId)
        prefs.edit().putStringSet("$KEY_DISMISSED_ADVISORIES$role", current).apply()
    }

    fun clearDismissedAdvisories(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith(KEY_DISMISSED_ADVISORIES) }.forEach { key ->
            editor.remove(key)
        }
        editor.apply()
    }

    fun getClearedSosIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_CLEARED_SOS, emptySet()) ?: emptySet()
    }

    fun clearSos(context: Context, sosId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getClearedSosIds(context).toMutableSet()
        current.add(sosId)
        prefs.edit().putStringSet(KEY_CLEARED_SOS, current).apply()
    }

    fun getDismissedDirectiveIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DISMISSED_DIRECTIVES, emptySet()) ?: emptySet()
    }

    fun dismissDirective(context: Context, directiveId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getDismissedDirectiveIds(context).toMutableSet()
        current.add(directiveId)
        prefs.edit().putStringSet(KEY_DISMISSED_DIRECTIVES, current).apply()
    }

    fun getDismissedRestockReminderIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet("dismissed_restock_reminders", emptySet()) ?: emptySet()
    }

    fun dismissRestockReminder(context: Context, reminderId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getDismissedRestockReminderIds(context).toMutableSet()
        current.add(reminderId)
        prefs.edit().putStringSet("dismissed_restock_reminders", current).apply()
    }

    fun getRemindedMedicineIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet("reminded_medicines", emptySet()) ?: emptySet()
    }

    fun recordRemindedMedicine(context: Context, medicineId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getRemindedMedicineIds(context).toMutableSet()
        current.add(medicineId)
        prefs.edit().putStringSet("reminded_medicines", current).apply()
    }

    fun clearRemindedMedicine(context: Context, medicineId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getRemindedMedicineIds(context).toMutableSet()
        current.remove(medicineId)
        prefs.edit().putStringSet("reminded_medicines", current).apply()
    }
}
