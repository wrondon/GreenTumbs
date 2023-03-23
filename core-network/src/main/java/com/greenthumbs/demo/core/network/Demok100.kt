package com.greenthumbs.demo.core.network

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URL


@Serializable
data class NasaPicture(val copyright : String="", val date: String="", val explanation: String="", val hdurl: String="", val media_type: String="", val service_version: String="", val title: String="", val url: String="" )

interface MainNetworkInterface {
    suspend fun getManyNasaPictures(i: Int) : List<NasaPicture>
    suspend fun getOneNasaPicture() : NasaPicture
}

object MainNetworkKTOR : MainNetworkInterface{

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    override suspend fun getManyNasaPictures(i: Int) : List<NasaPicture> {
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

    override suspend fun getOneNasaPicture() : NasaPicture {
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

object MainNetworkRetrofit : MainNetworkInterface {

    interface NASAPicService {
        @GET("apod?api_key=DEMO_KEY")
        suspend  fun getManyNasaPictures(@Query("count") count: Int): List<NasaPicture>

        @GET("apod?api_key=DEMO_KEY")
        suspend  fun getOneNasaPicture(): NasaPicture
    }

    val nasaApiService = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/planetary/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NASAPicService::class.java)

    override suspend fun getManyNasaPictures(i: Int): List<NasaPicture> {
        return nasaApiService.getManyNasaPictures(i)
    }

    override suspend fun getOneNasaPicture(): NasaPicture {
        return nasaApiService.getOneNasaPicture()
    }

}

interface MainRepoInterface {
    suspend fun ListOfPic(c: Int) : Flow<List<NasaPicture>>
    suspend fun onePic() : Flow<NasaPicture>
}

class MainRepo(val network : MainNetworkInterface) {

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

/*
Gson gson = new Gson();
gson.toJson(1);            // ==> 1
gson.toJson("abcd");       // ==> "abcd"
gson.toJson(new Long(10)); // ==> 10
int[] values = { 1 };
gson.toJson(values);       // ==> [1]

// Deserialization
int i = gson.fromJson("1", int.class);
Integer intObj = gson.fromJson("1", Integer.class);






 */
val jsonNasaPicture = """
    {
      "copyright": "Neil Corke",
      "date": "2023-03-16",
      "explanation": "Globular star cluster Omega Centauri, also known as NGC 5139, is 15,000 light-years away. The cluster is packed with about 10 million stars much older than the Sun within a volume about 150 light-years in diameter. It's the largest and brightest of 200 or so known globular clusters that roam the halo of our Milky Way galaxy. Though most star clusters consist of stars with the same age and composition, the enigmatic Omega Cen exhibits the presence of different stellar populations with a spread of ages and chemical abundances. In fact, Omega Cen may be the remnant core of a small galaxy merging with the Milky Way. Omega Centauri's red giant stars (with a yellowish hue) are easy to pick out in this sharp, color telescopic view.",
      "hdurl": "https://apod.nasa.gov/apod/image/2303/NGC5139_Omega_Centauri_3700px.jpg",
      "media_type": "image",
      "service_version": "v1",
      "title": "Millions of Stars in Omega Centauri",
      "url": "https://apod.nasa.gov/apod/image/2303/NGC5139_Omega_Centauri_1024px.jpg"
    }
""".trimIndent()
fun main() {
    val gson = Gson()

    //gson.toJson()
    // NasaPicture

    val picNasa = gson.fromJson(jsonNasaPicture, NasaPicture::class.java)
    println(" picNasa : $picNasa")

    val otherJson = gson.toJson(picNasa.copy(copyright = "!!!!! copyright new : !!!", date = "1234-56-78"))
    println("otherJson : $otherJson")

    println("*".repeat(60))

    runBlocking {
        launch {
            tryingNetworkObject()
            println("#".repeat(60))
            tryingMainRepo()
        }
    }

}

private suspend fun  tryingMainRepo(){
    val networkKtor     = MainNetworkKTOR
    val networkRetrofit = MainNetworkRetrofit
    val repoUsingKtor       = MainRepo(networkKtor)
    val repoUsingRetrofit   = MainRepo(networkRetrofit)

    repoUsingKtor.onePic().collect(){
        println("(using repo with ktor >>) : ${it.date} --- ${it.title}")
    }
    repoUsingKtor.ListOfPic(7).collect(){
        it.forEach {
            println(" (using repo with ktor >>) >> ${it.date} - ${it.title}")
        }
    }
    println("%".repeat(60))
    repoUsingRetrofit.onePic().collect(){
        println("(using repo with RETROFIT >>>>) : ${it.date} --- ${it.title}")
    }
    repoUsingRetrofit.ListOfPic(4).collect(){
        it.forEach {
            println(" (using repo with RETROFIT >>>>) >>>> ${it.date} - ${it.title}")
        }
    }
    println("%".repeat(60))

}

private suspend fun tryingNetworkObject() {
    val pic = MainNetworkKTOR.getOneNasaPicture()
    println(" (ktor) ${pic.date} ${pic.title}")
    val pics = MainNetworkKTOR.getManyNasaPictures(10)
    pics.forEach {
        println(" (ktor) >> ${it.date} - ${it.title}")
    }
    println("-".repeat(60))
    val pic2 = MainNetworkRetrofit.getOneNasaPicture()
    println(" (retrofit) ${pic2.date} ${pic2.title}")
    val pics2 = MainNetworkRetrofit.getManyNasaPictures(5)
    pics2.forEach {
        println(" (retrofit) >>>> ${it.date} - ${it.title}")
    }
}