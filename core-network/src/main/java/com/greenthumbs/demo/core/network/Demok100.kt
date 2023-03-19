package com.greenthumbs.demo.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL


@Serializable
data class NasaPicture(val copyright : String="", val date: String="", val explanation: String="", val hdurl: String="", val media_type: String="", val service_version: String="", val title: String="", val url: String="" )

object MainNetwork {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    suspend fun getManyNasaPictures(i: Int) : List<NasaPicture> {
        var list: List<NasaPicture> = emptyList()
        withContext(Dispatchers.IO) {
            launch{
                val urlMany = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&count=$i"
                val resp = URL(urlMany).readText()
                list = json.decodeFromString<List<NasaPicture>>(resp)
            }
        }
        return list
    }

    suspend fun getOneNasaPicture() : NasaPicture {
        var picture = NasaPicture()
        withContext(Dispatchers.IO) {
            launch{
                val urlMany = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"
                val resp = URL(urlMany).readText()
                picture = json.decodeFromString<NasaPicture>(resp)
            }
        }
        return picture
    }
}

object MainRepo {
    private val network = MainNetwork

    suspend fun ListOfPic(c: Int) : Flow<List<NasaPicture>> {
        return flow {
            val resp = network.getManyNasaPictures(c)
            emit(resp)
        }
    }

    suspend fun onePic() : Flow<NasaPicture> {
        return flow {
            val resp = network.getOneNasaPicture()
            emit(resp)
        }
    }
}

suspend fun main() {
    val pic = MainNetwork.getOneNasaPicture()
    println("${pic.date} ${pic.title}")

    val pics = MainNetwork.getManyNasaPictures(10)
    pics.forEach {
        println(" >> ${it.date} - ${it.title}")
    }
}