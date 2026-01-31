package com.pontocerto.app

import android.content.Context

object StorageUtils {

    private const val PREFS_NAME = "ponto_certo_prefs"
    private const val KEY_USUARIO = "usuario_logado"

    fun salvarUsuarioLogado(context: Context, cpf: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USUARIO, cpf).apply()
    }

    fun obterUsuarioLogado(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USUARIO, null)
    }

    fun limparUsuario(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
