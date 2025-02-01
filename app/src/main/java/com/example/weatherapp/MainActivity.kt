package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val lastCity = getLastSearchedLocation()
        fetchWeatherData(lastCity)
        SearchCity()
    }
    private fun saveLastSearchedLocation(cityName: String) {
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("last_city", cityName)
        editor.apply()
    }

    private fun getLastSearchedLocation(): String {
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("last_city", "Chapra") ?: "Chapra"
    }

    fun capitalizeFirstLetter(input: String): String {
        return input.lowercase().replaceFirstChar { it.uppercase() }
    }

    private fun SearchCity() {
        val searchView = binding.homeSearch
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val query1 = capitalizeFirstLetter(query)
                    saveLastSearchedLocation(query1)
                    fetchWeatherData(query1)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }
    private var sunriseTime: Long = 0
    private var sunsetTime: Long = 0

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val apiKey = BuildConfig.WEATHER_API_KEY
        val response = retrofit.getWeatherData(cityName,apiKey,"metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody != null){

                    sunriseTime = responseBody.sys.sunrise.toLong()
                    sunsetTime = responseBody.sys.sunset.toLong()

                    val temperature = responseBody.main.temp.toString()
                    val minTemp=responseBody.main.temp_min.toString()
                    val maxTemp=responseBody.main.temp_max.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure.toString()
                    val condition=responseBody.weather.firstOrNull()?.main?: "unknown"
                    val windspeed = responseBody.wind.speed

                    binding.sunrise.text = time(sunriseTime)
                    binding.sunset.text = time(sunsetTime)

                    binding.temp.text="$temperature °C"
                    binding.minTemp.text="MIN : $minTemp °C"
                    binding.maxTemp.text="MAX : $maxTemp °C"
                    binding.humidity.text="$humidity %"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.condition.text=condition
                    binding.weather.text=condition
                    binding.sea.text="$seaLevel hPa"
                    binding.cityName.text="$cityName"
                    binding.date.text= date()
                    binding.windSpeed.text="$windspeed m/s"
                    binding.day.text=dayName(System.currentTimeMillis())
                    changeImages(condition)

//                    Log.d("TAG", "onResponse: $temperature")
                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
//                Log.e("WeatherApp", "Failed to fetch weather data", p1)
            }

        })
    }

    private fun changeImages(condition: String) {
        // Get the current time in seconds
        val currentTime = System.currentTimeMillis() / 1000

        // Determine if it's day or night
//        val isDay = currentTime in (binding.sunrise.text.toLong() until binding.sunset.text.toLong())
        val isDay = currentTime in sunriseTime until sunsetTime

        when(condition){
            "Haze","Partly Clouds","Clouds","Overcast","Mist","Foggy","Fog","Smoke" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear","Clear Sky","Sunny" ->{
                binding.root.setBackgroundResource(if (isDay) R.drawable.sunny_background else R.drawable.night)
                binding.lottieAnimationView.setAnimation(if (isDay) R.raw.sun else R.raw.moon)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Blizzard","Moderate Snow","Heavy Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(if (isDay) R.drawable.sunny_background else R.drawable.night)
                binding.lottieAnimationView.setAnimation(if (isDay) R.raw.sun else R.raw.moon)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun time(timestamp:Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp:Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy")
        return sdf.format(Date())
    }
}