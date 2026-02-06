package com.pontocerto.app

import android.content.Context

object BiometriaStorage {

    private const val PREF = "biometria_prefs"
    private const val KEY_FACE = "face_assinatura"

    /**
     * Salva a assinatura facial
     */
    fun salvarAssinatura(context: Context, assinatura: String) {
        if (assinatura.isBlank()) return

        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FACE, assinatura)
            .apply()
    }

    /**
     * Obtém a assinatura facial salva
     */
    fun obterAssinatura(context: Context): String? {
        return context
            .getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_FACE, null)
            ?.takeIf { it.isNotBlank() }
    }

    /**
     * Verifica se existe cadastro facial válido
     */
    fun existeCadastro(context: Context): Boolean {
        return obterAssinatura(context) != null
    }

    /**
     * (Opcional, mas recomendado)
     * Limpa a biometria — útil para testes
     */
    fun limparCadastro(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_FACE)
            .apply()
    }
}
