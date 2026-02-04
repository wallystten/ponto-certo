package com.pontocerto.app

import android.content.Context

object EmpresaStorage {

    private const val PREFS = "empresa_prefs"
    private const val KEY_EMPRESA = "empresa_codigo"

    fun salvarEmpresa(context: Context, codigo: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_EMPRESA, codigo)
            .apply()
    }

    fun obterEmpresa(context: Context): String? {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_EMPRESA, null)
    }

    fun existeEmpresa(context: Context): Boolean {
        return obterEmpresa(context) != null
    }

    fun limparEmpresa(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_EMPRESA)
            .apply()
    }
}
