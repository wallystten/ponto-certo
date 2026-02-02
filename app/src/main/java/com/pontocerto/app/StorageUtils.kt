package com.pontocerto.app

import android.content.Context

object StorageUtils {

    private const val PREFS_NAME = "ponto_certo_prefs"

    // Histórico de pontos
    private const val KEY_HISTORICO = "historico_pontos"

    // Usuário logado
    private const val KEY_USUARIO_LOGADO = "usuario_logado"

    /* ===============================
       HISTÓRICO DE PONTO
       =============================== */

    fun salvarPonto(context: Context, registro: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historicoAtual = prefs.getString(KEY_HISTORICO, "") ?: ""

        val novoHistorico = if (historicoAtual.isBlank()) {
            registro
        } else {
            "$historicoAtual\n$registro"
        }

        prefs.edit()
            .putString(KEY_HISTORICO, novoHistorico)
            .apply()
    }

    fun obterHistorico(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_HISTORICO, "") ?: ""
    }

    fun limparHistorico(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_HISTORICO).apply()
    }

    /* ===============================
       LOGIN / USUÁRIO
       =============================== */

    fun salvarUsuarioLogado(context: Context, cpf: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USUARIO_LOGADO, cpf)
            .apply()
    }

    fun obterUsuarioLogado(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USUARIO_LOGADO, null)
    }

    fun limparUsuarioLogado(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_USUARIO_LOGADO).apply()
    }
}

