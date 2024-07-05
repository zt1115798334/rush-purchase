package org.example.rushpurchase.utils.excel

data class ExcelComplex<T>(
    val sheetName: String,
    val headerAlias: Map<String, String>,
    val dataSet: Collection<T>,
    val columnWidths: IntArray,
    val rowHeight: Int,
    val excelPictureComplexes: List<ExcelPictureComplex>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExcelComplex<*>

        if (sheetName != other.sheetName) return false
        if (headerAlias != other.headerAlias) return false
        if (dataSet != other.dataSet) return false
        if (!columnWidths.contentEquals(other.columnWidths)) return false
        if (rowHeight != other.rowHeight) return false
        if (excelPictureComplexes != other.excelPictureComplexes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sheetName.hashCode()
        result = 31 * result + headerAlias.hashCode()
        result = 31 * result + dataSet.hashCode()
        result = 31 * result + columnWidths.contentHashCode()
        result = 31 * result + rowHeight
        result = 31 * result + excelPictureComplexes.hashCode()
        return result
    }

}
