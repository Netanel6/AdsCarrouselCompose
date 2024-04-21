package com.netanel.adscarrouselcompose

import Carousel
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.gson.Gson
import com.netanel.adscarrouselcompose.model.Links
import com.netanel.adscarrouselcompose.ui.theme.AdsCarrouselComposeTheme
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            AdsCarrouselComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    DisplayLinks()
                    Carousel(links = loadJsonFromFile(context, "links.json"))
//                    AnimatedCarousel(links = loadJsonFromFile(context, "links.json"))
                }
            }
        }
    }
}



fun loadJsonFromFile(context: Context, fileName: String): Links {
    val gson = Gson()
    val inputStream = context.assets.open(fileName)
    val reader = InputStreamReader(inputStream)
    return gson.fromJson(reader, Links::class.java)
}


@Composable
fun DisplayLinks() {
    val context = LocalContext.current
    val links = loadJsonFromFile(context, "links.json")

    if (links != null) {
        Column {
            links.videoLinks.forEach { videoLink ->
                Text(
                    text = videoLink,
                    modifier = Modifier.clickable {
                        // Handle click
                    }
                )
            }
            links.photoLinks.forEach { photoLink ->
                Text(
                    text = photoLink,
                    modifier = Modifier.clickable {
                        // Handle click
                    }
                )
            }
        }
    } else {
        Text("No data found.")
    }
}

@Preview
@Composable
fun DisplayLinksPreview() {
    DisplayLinks()
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AdsCarrouselComposeTheme {
        Greeting("Android")
    }
}