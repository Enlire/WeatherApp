package com.example.weatherapp.domain.models

class WeatherCondition {
    // A method to translate weather descriptions from English to Russian
    fun translateWindDir(direction: String): String {
        return when (direction) {
            "N" -> "С"
            "NW" -> "СЗ"
            "NE" -> "СВ"
            "NNE" -> "ССВ"
            "NNW" -> "ССЗ"
            "S" -> "Ю"
            "SW" -> "ЮЗ"
            "SE" -> "ЮВ"
            "SSE" -> "ЮЮВ"
            "SSW" -> "ЮЮЗ"
            "W" -> "З"
            "WSW" -> "ЗЮЗ"
            "WNW" -> "ЗСЗ"
            "E" -> "В"
            "ESE" -> "ВЮВ"
            "ENE" -> "ВСВ"
            else -> direction // Default to the original description if no translation is available
        }
    }
}