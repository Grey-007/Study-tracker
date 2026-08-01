package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {

    private val IS_SETUP_COMPLETE = booleanPreferencesKey("is_setup_complete")
    private val SELECTED_EXAMS = stringSetPreferencesKey("selected_exams")
    private val SELECTED_SUBJECTS = stringSetPreferencesKey("selected_subjects")

    val isSetupComplete: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_SETUP_COMPLETE] ?: false
    }

    val selectedExams: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_EXAMS] ?: emptySet()
    }

    val selectedSubjects: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SELECTED_SUBJECTS] ?: emptySet()
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
}
