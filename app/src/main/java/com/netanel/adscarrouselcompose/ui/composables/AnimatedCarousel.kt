package com.netanel.adscarrouselcompose.ui.composables

import VideoPlayer
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.netanel.adscarrouselcompose.model.Links
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimatedCarousel(links: Links) {
    val pagerState = rememberPagerState(initialPage = 0) {
        links.videoLinks.size + links.photoLinks.size
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = 16.dp, // Add spacing between pages
        flingBehavior = PagerDefaults.flingBehavior(pagerState),
        userScrollEnabled = true // Allow user swiping for custom animation
    ) { page ->
        val scrollOffset = calculateCurrentOffsetForPage(pagerState, page)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Apply custom parallax effect
                    translationX = scrollOffset * -100 // Adjust translation for a parallax effect
                }
        ) {


            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 0.dp,
                flingBehavior = PagerDefaults.flingBehavior(pagerState),
                userScrollEnabled = true // Enable or disable swiping
            ) { page ->
                if (page < links.videoLinks.size) {
                    // Load the video and automatically move to the next when finished
                    VideoPlayer(videoLink = links.videoLinks[page], onVideoEnded = {
                        coroutineScope.launch {
                            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }, onError = { errorMessage ->
                        Toast.makeText(context, "Video error: $errorMessage", Toast.LENGTH_SHORT).show()
                    })
                } else {
                    val photoIndex = page - links.videoLinks.size
                    val photoLink = links.photoLinks[photoIndex]

                    // Delay for automatic page scroll after 10 seconds
                    LaunchedEffect(Unit) {
                        coroutineScope.launch {
                            delay(10_000)
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }

                    // Display the photo using Coil's AsyncImage
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = photoLink,
                            contentDescription = "Photo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun calculateCurrentOffsetForPage(
    pagerState: PagerState,
    page: Int
): Float {
    val currentPage = pagerState.currentPage
    val scrollFraction = pagerState.currentPageOffsetFraction
    return (page - currentPage + scrollFraction)
}



