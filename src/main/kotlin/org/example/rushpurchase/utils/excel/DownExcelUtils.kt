package org.example.rushpurchase.utils.excel

import cn.hutool.core.io.file.PathUtil
import cn.hutool.core.util.IdUtil
import cn.hutool.http.ContentType
import cn.hutool.poi.excel.ExcelWriter
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.ss.usermodel.*
import java.nio.charset.StandardCharsets
import java.nio.file.Paths


class DownExcelUtils<T> {
    fun downExcel(request: HttpServletRequest, response: HttpServletResponse, excelComplexList: List<ExcelComplex<T>>) {
        val userAgent = request.getHeader("User-Agent").uppercase()
        val filename = IdUtil.getSnowflakeNextIdStr() + ".xlsx"
        response.contentType = ContentType.OCTET_STREAM.value
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.setHeader("Content-disposition", "attachment;fileName=$filename")
        val first = excelComplexList.first()
        ExcelWriter(true, first.sheetName).use { writer ->
            writer.setDefaultRowHeight(20)
            val headFont = writer.createFont()
            headFont.bold = true
            headFont.color = Font.COLOR_NORMAL
            headFont.fontHeightInPoints = 12

            val valueFont = writer.createFont()
            valueFont.fontHeightInPoints = 11
            writer.styleSet.headCellStyle.setFont(headFont)
            writer.styleSet.cellStyle.setFont(valueFont)
            writer.styleSet.setAlign(HorizontalAlignment.FILL, VerticalAlignment.CENTER)
            extracted(first, writer)
            for (i in 1 until excelComplexList.size) {
                val excelComplex = excelComplexList[i]
                writer.setSheet(excelComplex.sheetName)
                extracted(excelComplex, writer)
            }
            response.outputStream.use {
                writer.flush(it, true)
                writer.close()
            }
        }

    }

    private fun extracted(first: ExcelComplex<T>, writer: ExcelWriter) {
        for (j in 0 until first.columnWidths.size) {
            writer.setColumnWidth(j, first.columnWidths[1])
        }

        for ((key, value) in first.headerAlias.entries) {
            writer.addHeaderAlias(key, value)
        }
        writer.write(first.dataSet, true)
        first.rowHeight.let {
            for (j in 0 until first.dataSet.size) {
                writer.setRowHeight(j + 1, it)
            }
        }
        if (first.excelPictureComplexes.isNotEmpty()) {
            for (excelPictureComplex in first.excelPictureComplexes) {
                writePic(
                    writer, excelPictureComplex.x, excelPictureComplex.y,
                    PathUtil.readBytes(Paths.get(excelPictureComplex.picPath))
                )
            }
        }
    }

    fun writePic(writer: ExcelWriter, x: Int, y: Int, pictureData: ByteArray) {
        val sheet = writer.sheet
        val drawingPatriarch = sheet.createDrawingPatriarch()
        val anchor = drawingPatriarch.createAnchor(0, 0, 0, 0, x, y, x + 1, y + 1)
        anchor.anchorType = ClientAnchor.AnchorType.MOVE_AND_RESIZE
        val pictureIndex = sheet.workbook.addPicture(pictureData, Workbook.PICTURE_TYPE_JPEG)
        drawingPatriarch.createPicture(anchor, pictureIndex)
    }
}