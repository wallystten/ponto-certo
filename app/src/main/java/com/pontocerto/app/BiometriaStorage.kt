package com.pontocerto.app

import android.content.Context

object BiometriaStorage {

    private const val PREF = "biometria_prefs"
    private const val KEY_FACE = "face_assinatura"

    fun salvarAssinatura(context: Context, assinatura: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FACE, assinatura)
            .apply()
    }

    fun obterAssinatura(context: Context): String? {
        return context
            .getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_FACE, null)
    }

    fun existeCadastro(context: Context): Boolean {
        return obterAssinatura(context) != null
    }
}
