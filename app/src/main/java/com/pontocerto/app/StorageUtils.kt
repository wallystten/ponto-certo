package com.pontocerto.app

import android.content.Context

object StorageUtils {

    private const val PREF_NAME = "ponto_certo_prefs"
    private const val KEY_HISTORICO = "historico_pontos"

    fun salvarPonto(context: Context, registro: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val atual = prefs.getString(KEY_HISTORICO, "") ?: ""
        val novo = if (atual.isEmpty()) registro else "$atual\n$registro"

        prefs.edit()
            .putString(KEY_HISTORICO, novo)
            .apply()
    }

    fun obterHistorico(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HISTORICO, "Nenhum ponto registrado.") ?: ""
    }
}
