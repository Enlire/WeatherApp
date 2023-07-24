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

    fun weatherCodeToDescription (weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "Ясно"
            1 -> "В основном ясно"
            2 -> "Переменная облачность"
            3 -> "Пасмурно"
            45 -> "Туман"
            48 -> "Переохлажденный туман"
            51 -> "Слабая морось"
            53 -> "Умеренная морось"
            55 -> "Сильная морось"
            56 -> "Слабая замерзающая морось"
            57 -> "Сильная замерзающая морось"
            61 -> "Небольшой дождь"
            63 -> "Умеренный дождь"
            65 -> "Сильный дождь"
            66 -> "Слабый переохлажденный дождь"
            67 -> "Сильный переохлажденный дождь"
            71 -> "Небольшой снег"
            73 -> "Умеренный снег"
            75 -> "Сильный снег"
            77 -> "Снежные зерна"
            80 -> "Небольшой ливневый дождь"
            81 -> "Умеренный ливневый дождь"
            82 -> "Сильные ливни"
            85 -> "Небольшой снег"
            86 -> "Сильный снег"
            95 -> "Слабая или умеренная гроза"
            96 -> "Гроза со слабым градом"
            99 -> "Гроза с сильным градом"
            else -> "Неизвестно"
        }
    }
}