package com.example.weatherapp.domain.models

import com.example.weatherapp.R

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
            85 -> "Небольшой снегопад"
            86 -> "Сильный снегопад"
            95 -> "Слабая или умеренная гроза"
            96 -> "Гроза со слабым градом"
            99 -> "Гроза с сильным градом"
            else -> "Неизвестно"
        }
    }

    fun weatherCodeOpenMeteoToIcon (weatherCode: Int) : Int {
        return when (weatherCode) {
            0 -> R.drawable.ic_day_clear
            1 -> R.drawable.ic_day_clear
            2 -> R.drawable.ic_day_cloudy
            3 -> R.drawable.ic_overcast
            45 -> R.drawable.ic_fog
            48 -> R.drawable.ic_fog
            51 -> R.drawable.ic_drizzle
            53 -> R.drawable.ic_drizzle
            55 -> R.drawable.ic_drizzle
            56 -> R.drawable.ic_drizzle
            57 -> R.drawable.ic_drizzle
            61 -> R.drawable.ic_light_rain
            63 -> R.drawable.ic_light_rain
            65 -> R.drawable.ic_heavy_rain
            66 -> R.drawable.ic_light_rain
            67 -> R.drawable.ic_heavy_rain
            71 -> R.drawable.ic_snow
            73 -> R.drawable.ic_snow
            75 -> R.drawable.ic_snow
            77 -> R.drawable.ic_snow_grains
            80 -> R.drawable.ic_light_rain
            81 -> R.drawable.ic_light_rain
            82 -> R.drawable.ic_heavy_rain
            85 -> R.drawable.ic_snow
            86 -> R.drawable.ic_snow
            95 -> R.drawable.ic_thunder
            96 -> R.drawable.ic_thunder_hail
            99 -> R.drawable.ic_thunder_hail
            else -> R.drawable.ic_launcher_foreground
        }
    }

    fun weatherCodeWeatherApiToIcon (weatherCode: Int, isDay: Int) : Int {
        return when {
            weatherCode == 1000 && isDay == 1 -> R.drawable.ic_day_clear
            weatherCode == 1000 && isDay == 0 -> R.drawable.ic_night_clear
            weatherCode == 1003 && isDay == 1 -> R.drawable.ic_day_cloudy
            weatherCode == 1003 && isDay == 0 -> R.drawable.ic_night_cloudy
            weatherCode == 1006 -> R.drawable.ic_overcast
            weatherCode == 1009 -> R.drawable.ic_overcast
            weatherCode == 1030 -> R.drawable.ic_mist
            weatherCode == 1063 && isDay == 1 -> R.drawable.ic_rain_day
            weatherCode == 1063 && isDay == 0 -> R.drawable.ic_rain_night
            weatherCode == 1066 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1066 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1069 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1069 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1072 -> R.drawable.ic_drizzle
            weatherCode == 1087 && isDay == 1 -> R.drawable.ic_thunderstorm_day
            weatherCode == 1087 && isDay == 0 -> R.drawable.ic_thunderstorm_night
            weatherCode == 1114 -> R.drawable.ic_snow
            weatherCode == 1117 -> R.drawable.ic_snow_grains
            weatherCode == 1135 -> R.drawable.ic_fog
            weatherCode == 1147 -> R.drawable.ic_fog
            weatherCode == 1150 -> R.drawable.ic_drizzle
            weatherCode == 1153 -> R.drawable.ic_drizzle
            weatherCode == 1168 -> R.drawable.ic_drizzle
            weatherCode == 1171 -> R.drawable.ic_drizzle
            weatherCode == 1180 && isDay == 1 -> R.drawable.ic_rain_day
            weatherCode == 1180 && isDay == 0 -> R.drawable.ic_rain_night
            weatherCode == 1183 -> R.drawable.ic_light_rain
            weatherCode == 1186 && isDay == 1 -> R.drawable.ic_rain_day
            weatherCode == 1186 && isDay == 0 -> R.drawable.ic_rain_night
            weatherCode == 1189 -> R.drawable.ic_light_rain
            weatherCode == 1192 && isDay == 1 -> R.drawable.ic_heavy_rain_day
            weatherCode == 1192 && isDay == 0 -> R.drawable.ic_heavy_rain_night
            weatherCode == 1195 -> R.drawable.ic_heavy_rain
            weatherCode == 1198 -> R.drawable.ic_light_rain
            weatherCode == 1201 -> R.drawable.ic_heavy_rain
            weatherCode == 1204 -> R.drawable.ic_snow
            weatherCode == 1207 -> R.drawable.ic_snow
            weatherCode == 1210 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1210 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1213 -> R.drawable.ic_snow
            weatherCode == 1216 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1216 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1219 -> R.drawable.ic_snow
            weatherCode == 1222 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1222 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1225 -> R.drawable.ic_snow
            weatherCode == 1237 -> R.drawable.ic_snow_grains
            weatherCode == 1240 -> R.drawable.ic_light_rain
            weatherCode == 1243 -> R.drawable.ic_heavy_rain
            weatherCode == 1246 -> R.drawable.ic_heavy_rain
            weatherCode == 1249 -> R.drawable.ic_rain_and_snow
            weatherCode == 1252 -> R.drawable.ic_rain_and_snow
            weatherCode == 1255 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1255 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1258 && isDay == 1 -> R.drawable.ic_snow_day
            weatherCode == 1258 && isDay == 0 -> R.drawable.ic_snow_night
            weatherCode == 1261 -> R.drawable.ic_snow_grains
            weatherCode == 1264 -> R.drawable.ic_snow_grains
            weatherCode == 1273 && isDay == 1 -> R.drawable.ic_thunderstorm_rain_day
            weatherCode == 1273 && isDay == 0 -> R.drawable.ic_thunderstorm_night
            weatherCode == 1276 -> R.drawable.ic_mix_rainfall
            weatherCode == 1279 && isDay == 1 -> R.drawable.ic_thunderstorm_rain_day
            weatherCode == 1279 && isDay == 0 -> R.drawable.ic_thunderstorm_night
            weatherCode == 1282 -> R.drawable.ic_mix_rainfall
            else -> R.drawable.ic_launcher_foreground
        }
    }
}