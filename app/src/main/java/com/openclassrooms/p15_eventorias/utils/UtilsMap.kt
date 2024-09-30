package com.openclassrooms.p15_eventorias.utils

import com.openclassrooms.p15_eventorias.model.CoordinatesGPS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import com.openclassrooms.p15_eventorias.BuildConfig

const val CST_MAPS_API_KEY = BuildConfig.MAPS_API_KEY

/**
 * @param coordGPS Coordonnées GPS
 * @return URL de la carte Google à afficher dans une image
 */
suspend fun googleAPIDrawCard(
    coordGPS : CoordinatesGPS?
): String {


    if (coordGPS==null){
        return ""
    }
    else{

        // Thread d'entrée/sortie
        return withContext(Dispatchers.IO) {
            try {

                // Renvoie l'URL de la carte
                val sURL = "https://maps.googleapis.com/maps/api/staticmap?" +
                        "center=${coordGPS.latitude},${coordGPS.longitude}" +
                        "&zoom=15" +
                        "&size=600x400" +
                        "&key=$CST_MAPS_API_KEY"

                return@withContext sURL

            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext ""
            }
        }

    }


}

/**
 * Renvoie les coordonnées GPS d'une adresse textuelle par ex : "3 grand rue, 3400. Montpellier"
 */
suspend fun getCoordinatesFromAddress(
    address: String
): CoordinatesGPS? {

//    return withContext(Dispatchers.IO) {
//        return@withContext CoordinatesGPS(1.0, 2.0)
//    }

    // Thread d'entrée/sortie
    return withContext(Dispatchers.IO) {
        //try {

            // https://developers.google.com/maps/documentation/geocoding/overview?hl=fr

            val client = OkHttpClient()
            val url = "https://maps.googleapis.com/maps/api/geocode/json?" +
                    "address=${address.replace(" ", "+")}" +
                    "&key=$CST_MAPS_API_KEY"
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("API request failed with code: ${response.code} : ${response.body}")
            }

            response.body?.let { body ->
                val jsonResponse = JSONObject(body.string())

                val results = jsonResponse.getJSONArray("results")
                // Si aucun résultat, on lève une exception
                if (results.length() == 0) {
                    throw Exception("No GPS coordinates found for the address: $address")
                }

                // Renvoie le 1er résultat (le plus pertinent selon google)
                val location = results
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location")

                val lat = location.getDouble("lat")
                val lng = location.getDouble("lng")

                return@withContext CoordinatesGPS(lat, lng)
            }

            return@withContext null

//        } catch (e: Exception) {
//            e.printStackTrace()
//            return@withContext null
//        }

    }
}

