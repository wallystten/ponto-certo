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
     * Salva ponto SOMENTE se empresa e usuário existirem
     */
    fun salvarPonto(context: Context, registro: String) {

        val empresa = obterEmpresa(context)
        val usuario = obterUsuarioLogado(context)

        if (empresa.isNullOrBlank() || usuario.isNullOrBlank()) {
            throw IllegalStateException(
                "Tentativa de registrar ponto sem empresa ou usuário"
            )
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val historicoAtual = prefs.getString(KEY_HISTORICO, "") ?: ""

        val registroSeguro = gerarRegistroSeguro(
            registro = registro,
            empresa = empresa,
            usuario = usuario
        )

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
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_HISTORICO, "") ?: ""
    }

    fun limparHistorico(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_HISTORICO)
            .apply()
    }

    /* ===============================
       USUÁRIO
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
        return !obterEmpresa(context).isNullOrBlank()
    }

    fun limparEmpresa(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_EMPRESA)
            .apply()
    }

    /* ===============================
       🔐 ANTIFRAUDE
       =============================== */

    private fun gerarRegistroSeguro(
        registro: String,
        empresa: String,
        usuario: String
    ): String {

        val dataSistema = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val device = "${Build.MANUFACTURER} ${Build.MODEL}"

        return "$registro | $dataSistema | $empresa | $usuario | $device"
    }
}
