package com.pontocerto.app

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileWriter

object ExportUtils {

    /**
     * Exporta o histórico de pontos para um arquivo CSV.
     * O arquivo será salvo em:
     * Documents/PontoCerto/historico_ponto.csv
     */
    fun exportarCSV(context: Context): File? {
        val historico = StorageUtils.obterHistorico(context)

        if (historico.isBlank()) {
            return null
        }

        val pasta = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS
            ),
            "PontoCerto"
        )

        if (!pasta.exists()) {
            pasta.mkdirs()
        }

        val arquivo = File(pasta, "historico_ponto.csv")
        val writer = FileWriter(arquivo)

        // Cabeçalho do CSV
        writer.append("DataHora,Registro\n")

        // Conteúdo
        historico.split("\n").forEach { linha ->
            // Formato esperado: "dd/MM/yyyy HH:mm:ss - PONTO REGISTRADO"
            writer.append(linha.replace(" - ", ","))
            writer.append("\n")
        }

        writer.flush()
        writer.close()

        return arquivo
    }

    /**
     * Base para exportação em PDF.
     * A implementação completa será adicionada no build
     * (GitHub Actions + biblioteca PDF).
     */
    fun exportarPDF(context: Context) {
        // Implementação completa entra depois
    }
}
