package com.pontocerto.app

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StorageUtils {

    private const val PREFS_NAME = "ponto_certo_prefs"

    /* ===============================
       HISTÓRICO DE PONTO
       =============================== */

    private const val KEY_HISTORICO = "historico_pontos"

    /**
     * Salva ponto com metadados antifraude
     */
    fun salvarPonto(context: Context, registro: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val historicoAtual = prefs.getString(KEY_HISTORICO, "") ?: ""

        val registroSeguro = gerarRegistroSeguro(context, registro)

        val novoHistorico = if (historicoAtual.isBlank()) {
            registroSeguro
        } else {
            "$historicoAtual\n$registroSeguro"
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
       USUÁRIO LOGADO
       =============================== */

    private const val KEY_USUARIO_LOGADO = "usuario_logado"

    fun salvarUsuarioLogado(context: Context, cpf: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USUARIO_LOGADO, cpf)
            .apply()
    }

    fun obterUsuarioLogado(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USUARIO_LOGADO, null)
    }

    fun limparUsuarioLogado(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_USUARIO_LOGADO)
            .apply()
    }

    /* ===============================
       EMPRESA
       =============================== */

    private const val KEY_EMPRESA = "empresa_codigo"

    fun salvarEmpresa(context: Context, codigoEmpresa: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_EMPRESA, codigoEmpresa)
            .apply()
    }

    fun obterEmpresa(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMPRESA, null)
    }

    fun existeEmpresa(context: Context): Boolean {
        return obterEmpresa(context) != null
    }

    fun limparEmpresa(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_EMPRESA)
            .apply()
    }

    /* ===============================
       🔐 ANTIFRAUDE (INTERNO)
       =============================== */

    private fun gerarRegistroSeguro(context: Context, registro: String): String {
        val dataSistema = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val device = "${Build.MANUFACTURER} ${Build.MODEL}"
        val empresa = obterEmpresa(context) ?: "SEM_EMPRESA"
        val usuario = obterUsuarioLogado(context) ?: "SEM_USUARIO"

        return "$registro | $dataSistema | $empresa | $usuario | $device"
    }
}
