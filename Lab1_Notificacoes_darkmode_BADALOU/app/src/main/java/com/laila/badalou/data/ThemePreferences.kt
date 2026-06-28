package com.laila.badalou.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Cria o DataStore com o nome "theme_preferences"
// Fica salvo no dispositivo mesmo após fechar o app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

class ThemePreferences(private val context: Context) {

    companion object {
        // Chave para salvar o valor do tema no DataStore
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Lê o tema salvo — retorna false (claro) por padrão
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    // Salva a preferência de tema escolhida pelo usuário
    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }
}