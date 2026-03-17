package com.example.fastlaps.presentation.util

import androidx.compose.ui.graphics.Color

object F1Constants {

    fun countryFlag(country: String): String {
        return when (country) {
            "Australia" -> "\uD83C\uDDE6\uD83C\uDDFA"
            "China" -> "\uD83C\uDDE8\uD83C\uDDF3"
            "Japan" -> "\uD83C\uDDEF\uD83C\uDDF5"
            "Bahrain" -> "\uD83C\uDDE7\uD83C\uDDED"
            "Saudi Arabia" -> "\uD83C\uDDF8\uD83C\uDDE6"
            "USA" -> "\uD83C\uDDFA\uD83C\uDDF8"
            "Canada" -> "\uD83C\uDDE8\uD83C\uDDE6"
            "Monaco" -> "\uD83C\uDDF2\uD83C\uDDE8"
            "Spain" -> "\uD83C\uDDEA\uD83C\uDDF8"
            "Austria" -> "\uD83C\uDDE6\uD83C\uDDF9"
            "UK" -> "\uD83C\uDDEC\uD83C\uDDE7"
            "Belgium" -> "\uD83C\uDDE7\uD83C\uDDEA"
            "Hungary" -> "\uD83C\uDDED\uD83C\uDDFA"
            "Netherlands" -> "\uD83C\uDDF3\uD83C\uDDF1"
            "Italy" -> "\uD83C\uDDEE\uD83C\uDDF9"
            "Azerbaijan" -> "\uD83C\uDDE6\uD83C\uDDFF"
            "Singapore" -> "\uD83C\uDDF8\uD83C\uDDEC"
            "Mexico" -> "\uD83C\uDDF2\uD83C\uDDFD"
            "Brazil" -> "\uD83C\uDDE7\uD83C\uDDF7"
            "Qatar" -> "\uD83C\uDDF6\uD83C\uDDE6"
            "UAE" -> "\uD83C\uDDE6\uD83C\uDDEA"
            "Portugal" -> "\uD83C\uDDF5\uD83C\uDDF9"
            "France" -> "\uD83C\uDDEB\uD83C\uDDF7"
            "Germany" -> "\uD83C\uDDE9\uD83C\uDDEA"
            else -> "\uD83C\uDFC1"
        }
    }

    fun teamColor(constructorId: String?): Color {
        return when (constructorId?.lowercase()) {
            "mclaren" -> Color(0xFFF47600)
            "red_bull" -> Color(0xFF4781D7)
            "mercedes" -> Color(0xFF00D7B6)
            "ferrari" -> Color(0xFFED1131)
            "aston_martin" -> Color(0xFF229971)
            "alpine" -> Color(0xFF00A1E8)
            "williams" -> Color(0xFF1868DB)
            "haas" -> Color(0xFF9C9FA2)
            "rb" -> Color(0xFF6C98FF)
            "sauber" -> Color(0xFF01C00E)
            else -> Color.Gray
        }
    }

    fun nationalityFlag(nationality: String): String {
        return when (nationality.lowercase()) {
            "british" -> "\uD83C\uDDEC\uD83C\uDDE7"
            "spanish" -> "\uD83C\uDDEA\uD83C\uDDF8"
            "dutch" -> "\uD83C\uDDF3\uD83C\uDDF1"
            "monegasque" -> "\uD83C\uDDF2\uD83C\uDDE8"
            "australian" -> "\uD83C\uDDE6\uD83C\uDDFA"
            "thai" -> "\uD83C\uDDF9\uD83C\uDDED"
            "french" -> "\uD83C\uDDEB\uD83C\uDDF7"
            "canadian" -> "\uD83C\uDDE8\uD83C\uDDE6"
            "japanese" -> "\uD83C\uDDEF\uD83C\uDDF5"
            "chinese" -> "\uD83C\uDDE8\uD83C\uDDF3"
            "german" -> "\uD83C\uDDE9\uD83C\uDDEA"
            "italian" -> "\uD83C\uDDEE\uD83C\uDDF9"
            "american" -> "\uD83C\uDDFA\uD83C\uDDF8"
            "swiss" -> "\uD83C\uDDE8\uD83C\uDDED"
            "new zealander" -> "\uD83C\uDDF3\uD83C\uDDFF"
            "argentine" -> "\uD83C\uDDE6\uD83C\uDDF7"
            "brazilian" -> "\uD83C\uDDE7\uD83C\uDDF7"
            "austrian" -> "\uD83C\uDDE6\uD83C\uDDF9"
            else -> "\uD83C\uDFF3\uFE0F"
        }
    }
}
