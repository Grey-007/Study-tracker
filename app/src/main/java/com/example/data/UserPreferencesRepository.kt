package com.example.data
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    private val IS_SETUP_COMPLETE = booleanPreferencesKey("is_setup_complete")
    private val SELECTED_EXAMS = stringSetPreferencesKey("selected_exams")
    private val SELECTED_SUBJECTS = stringSetPreferencesKey("selected_subjects")
    private val USER_NAME = stringPreferencesKey("user_name")
    private val PROFILE_PIC_URI = stringPreferencesKey("profile_pic_uri")
    private val USER_AIM = stringPreferencesKey("user_aim")
    private val THEME_COLOR = androidx.datastore.preferences.core.intPreferencesKey("theme_color")
    private val DARK_MODE = booleanPreferencesKey("dark_mode")

    val isSetupComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_SETUP_COMPLETE] ?: false
    }

    val selectedExams: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_EXAMS] ?: emptySet()
    }

    val selectedSubjects: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_SUBJECTS] ?: emptySet()
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    val profilePicUri: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PROFILE_PIC_URI]
    }
    val userAim: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_AIM]
    }
    val themeColor: Flow<Int?> = context.dataStore.data.map { preferences ->
        preferences[THEME_COLOR]
    }
    val isDarkMode: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE]
    }

    suspend fun saveSetupComplete(isComplete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_SETUP_COMPLETE] = isComplete
        }
    }

    suspend fun saveSelectedExams(exams: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_EXAMS] = exams
        }
    }

    suspend fun saveSelectedSubjects(subjects: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_SUBJECTS] = subjects
        }
    }

    suspend fun saveUserProfile(name: String, uri: String?, aim: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            if (uri != null) {
                preferences[PROFILE_PIC_URI] = uri
            } else {
                preferences.remove(PROFILE_PIC_URI)
            }
            if (aim != null) {
                preferences[USER_AIM] = aim
            } else {
                preferences.remove(USER_AIM)
            }
        }
    }
    suspend fun saveThemeColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_COLOR] = color
        }
    }
    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = isDark
        }
    }
}
