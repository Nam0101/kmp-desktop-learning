package com.example.testdesktop

import com.example.testdesktop.data.Medication
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ExcelExporter {
    
    fun exportToExcel(medications: List<Medication>, filePath: String): Result<String> {
        return try {
            val workbook: Workbook = XSSFWorkbook()
            val sheet: Sheet = workbook.createSheet("Quản Lý Thuốc")
            
            // Create header style
            val headerStyle = workbook.createCellStyle().apply {
                fillForegroundColor = IndexedColors.LIGHT_BLUE.index
                fillPattern = FillPatternType.SOLID_FOREGROUND
                borderBottom = BorderStyle.THIN
                borderTop = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
                alignment = HorizontalAlignment.CENTER
                verticalAlignment = VerticalAlignment.CENTER
            }
            
            val headerFont = workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 12
            }
            headerStyle.setFont(headerFont)
            
            // Create data style
            val dataStyle = workbook.createCellStyle().apply {
                borderBottom = BorderStyle.THIN
                borderTop = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
                verticalAlignment = VerticalAlignment.CENTER
            }
            
            // Create numeric style
            val numericStyle = workbook.createCellStyle().apply {
                borderBottom = BorderStyle.THIN
                borderTop = BorderStyle.THIN
                borderLeft = BorderStyle.THIN
                borderRight = BorderStyle.THIN
                verticalAlignment = VerticalAlignment.CENTER
                alignment = HorizontalAlignment.RIGHT
            }
            
            // Create header row
            val headerRow = sheet.createRow(0)
            val headers = listOf("Mã Góc", "Tên Thuốc Góc", "Tồn đầu", "Nhập", "Xuất", "Tồn cuối")
            
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
                cell.cellStyle = headerStyle
            }
            
            // Add data rows
            medications.forEachIndexed { index, medication ->
                val row = sheet.createRow(index + 1)
                
                // Code
                row.createCell(0).apply {
                    setCellValue(medication.code)
                    cellStyle = dataStyle
                }
                
                // Name
                row.createCell(1).apply {
                    setCellValue(medication.name)
                    cellStyle = dataStyle
                }
                
                // Initial Stock
                row.createCell(2).apply {
                    setCellValue(medication.initialStock.toDouble())
                    cellStyle = numericStyle
                }
                
                // Imported
                row.createCell(3).apply {
                    setCellValue(medication.imported.toDouble())
                    cellStyle = numericStyle
                }
                
                // Exported
                row.createCell(4).apply {
                    setCellValue(medication.exported.toDouble())
                    cellStyle = numericStyle
                }
                
                // Final Stock
                row.createCell(5).apply {
                    setCellValue(medication.finalStock.toDouble())
                    cellStyle = numericStyle
                }
            }
            
            // Auto-size columns
            for (i in 0 until headers.size) {
                sheet.autoSizeColumn(i)
                // Add some extra width for better readability
                val currentWidth = sheet.getColumnWidth(i)
                sheet.setColumnWidth(i, (currentWidth * 1.1).toInt())
            }
            
            // Write to file
            val file = File(filePath)
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            
            Result.success(filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getDefaultFileName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val timestamp = LocalDateTime.now().format(formatter)
        return "QuanLyThuoc_$timestamp.xlsx"
    }
}

