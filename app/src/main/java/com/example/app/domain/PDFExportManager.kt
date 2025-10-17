package com.example.app.domain

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.List
import com.itextpdf.layout.element.ListItem
import com.itextpdf.layout.property.TextAlignment
import com.example.app.data.model.ResearchDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PDFExportManager(private val context: Context) {
    
    fun exportResearchToPdf(document: ResearchDocument): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "research_${document.title.replace("\\s+".toRegex(), "_")}_$timestamp.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdfDocument = PdfDocument(pdfWriter)
        val documentPdf = Document(pdfDocument)
        
        // Title
        val title = Paragraph(document.title)
            .setTextAlignment(TextAlignment.CENTER)
            .setBold()
            .setFontSize(18f)
        documentPdf.add(title)
        
        // Summary
        documentPdf.add(Paragraph("Summary").setBold().setFontSize(14f))
        documentPdf.add(Paragraph(document.summary).setFontSize(12f))
        documentPdf.add(Paragraph("\n"))
        
        // Sections
        document.sections.forEach { section ->
            documentPdf.add(Paragraph(section.title).setBold().setFontSize(14f))
            documentPdf.add(Paragraph(section.content).setFontSize(12f))
            documentPdf.add(Paragraph("\n"))
        }
        
        // Sources
        if (document.sources.isNotEmpty()) {
            documentPdf.add(Paragraph("Sources").setBold().setFontSize(14f))
            val sourcesList = List()
            document.sources.forEach { source ->
                sourcesList.add(ListItem("${source.title}: ${source.url}"))
            }
            documentPdf.add(sourcesList)
        }
        
        documentPdf.close()
        return file
    }
}
