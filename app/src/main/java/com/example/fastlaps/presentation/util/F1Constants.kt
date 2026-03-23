package com.example.fastlaps.presentation.util

import androidx.compose.ui.graphics.Color
import com.leandro.fastlaps.R

object F1Constants {

    private val circuitDrawableMap = mapOf(
        "albert_park" to R.drawable.circuit_albert_park,
        "shanghai" to R.drawable.circuit_shanghai,
        "suzuka" to R.drawable.circuit_suzuka,
        "bahrain" to R.drawable.circuit_bahrain,
        "jeddah" to R.drawable.circuit_jeddah,
        "miami" to R.drawable.circuit_miami,
        "imola" to R.drawable.circuit_imola,
        "monaco" to R.drawable.circuit_monaco,
        "catalunya" to R.drawable.circuit_catalunya,
        "madring" to R.drawable.circuit_madring,
        "villeneuve" to R.drawable.circuit_villeneuve,
        "red_bull_ring" to R.drawable.circuit_red_bull_ring,
        "silverstone" to R.drawable.circuit_silverstone,
        "spa" to R.drawable.circuit_spa,
        "hungaroring" to R.drawable.circuit_hungaroring,
        "zandvoort" to R.drawable.circuit_zandvoort,
        "monza" to R.drawable.circuit_monza,
        "baku" to R.drawable.circuit_baku,
        "marina_bay" to R.drawable.circuit_marina_bay,
        "americas" to R.drawable.circuit_americas,
        "rodriguez" to R.drawable.circuit_rodriguez,
        "interlagos" to R.drawable.circuit_interlagos,
        "losail" to R.drawable.circuit_losail,
        "yas_marina" to R.drawable.circuit_yas_marina,
        "las_vegas" to R.drawable.circuit_las_vegas,
        "vegas" to R.drawable.circuit_las_vegas
    )

    fun circuitDrawable(circuitId: String): Int = circuitDrawableMap[circuitId] ?: 0

    fun allCircuitDrawables(): Map<String, Int> = circuitDrawableMap

    fun driverImageUrl(driverId: String): String {
        val mediaId = driverMediaId(driverId)
        return "https://media.formula1.com/d_driver_fallback_image.png/content/dam/fom-website/drivers/${mediaId.first().uppercase()}/${mediaId.uppercase()}_${driverFullName(driverId)}/${mediaId}.png.transform/1col/image.png"
    }

    private fun driverMediaId(driverId: String): String {
        return when (driverId) {
            "max_verstappen" -> "MAXVER01"
            "norris" -> "LANNOR01"
            "leclerc" -> "CHALEC01"
            "piastri" -> "OSCPIA01"
            "sainz" -> "CARSAI01"
            "hamilton" -> "LEWHAM01"
            "russell" -> "GEORUS01"
            "alonso" -> "FERALO01"
            "stroll" -> "LANSTR01"
            "gasly" -> "PIEGAS01"
            "doohan" -> "JACDOO01"
            "tsunoda" -> "YUKTSU01"
            "hadjar" -> "ISAHAD01"
            "albon" -> "ALEALB01"
            "colapinto" -> "FRACOL01"
            "hulkenberg" -> "NICHUL01"
            "bortoleto" -> "GABBOR01"
            "bearman" -> "OLIBEA01"
            "ocon" -> "ESTOCO01"
            "antonelli" -> "KIMANT01"
            "lawson" -> "LIALAW01"
            "perez" -> "SERPER01"
            "ricciardo" -> "DANRIC01"
            "bottas" -> "VALBOT01"
            "zhou" -> "GUAZHO01"
            "magnussen" -> "KEVMAG01"
            "sargeant" -> "LOGSAR01"
            "de_vries" -> "NYKVRI01"
            else -> ""
        }
    }

    private fun driverFullName(driverId: String): String {
        return when (driverId) {
            "max_verstappen" -> "Max_Verstappen"
            "norris" -> "Lando_Norris"
            "leclerc" -> "Charles_Leclerc"
            "piastri" -> "Oscar_Piastri"
            "sainz" -> "Carlos_Sainz"
            "hamilton" -> "Lewis_Hamilton"
            "russell" -> "George_Russell"
            "alonso" -> "Fernando_Alonso"
            "stroll" -> "Lance_Stroll"
            "gasly" -> "Pierre_Gasly"
            "doohan" -> "Jack_Doohan"
            "tsunoda" -> "Yuki_Tsunoda"
            "hadjar" -> "Isack_Hadjar"
            "albon" -> "Alexander_Albon"
            "colapinto" -> "Franco_Colapinto"
            "hulkenberg" -> "Nico_Hulkenberg"
            "bortoleto" -> "Gabriel_Bortoleto"
            "bearman" -> "Oliver_Bearman"
            "ocon" -> "Esteban_Ocon"
            "antonelli" -> "Kimi_Antonelli"
            "lawson" -> "Liam_Lawson"
            "perez" -> "Sergio_Perez"
            "ricciardo" -> "Daniel_Ricciardo"
            "bottas" -> "Valtteri_Bottas"
            "zhou" -> "Guanyu_Zhou"
            "magnussen" -> "Kevin_Magnussen"
            "sargeant" -> "Logan_Sargeant"
            "de_vries" -> "Nyck_De_Vries"
            else -> ""
        }
    }

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
