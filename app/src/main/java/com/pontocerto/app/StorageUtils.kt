package com.pontocerto.app

import android.content.Context

object StorageUtils {

    private const val PREFS_NAME = "ponto_certo_prefs"
    private const val KEY_HISTORICO = "historico_pontos"

    fun salvarPonto(context: Context, registro: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historicoAtual = prefs.getString(KEY_HISTORICO, "") ?: ""
        val novoHistorico = if (historicoAtual.isBlank()) {
            registro
        } else {
            "$historicoAtual\n$registro"
        }

        prefs.edit().putString(KEY_HISTORICO, novoHistorico).apply()
    }

    fun obterHistorico(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HISTORICO, "") ?: ""
    }

    fun limparHistorico(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORICO).apply()
    }
}
