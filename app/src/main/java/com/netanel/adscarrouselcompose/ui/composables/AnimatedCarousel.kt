package com.netanel.adscarrouselcompose.ui.composables

import VideoPlayer
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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

    // State to track the need to reset the auto-scroll timer
    var resetAutoScroll by remember { mutableStateOf(false) }

    // State to track the current video being played
    var currentVideoIndex by remember { mutableStateOf(-1) }

    // Launch a coroutine to handle auto-scrolling
    LaunchedEffect(pagerState, resetAutoScroll) {
        while (true) {
            delay(5000) // Change auto-scroll delay as needed
            if (!resetAutoScroll) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            } else {
                resetAutoScroll = false
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { change, dragAmount -> },
                    onDragEnd = {
                        resetAutoScroll = true // Reset the auto-scroll timer on swipe
                        currentVideoIndex = pagerState.currentPage // Reset the video
                    }
                )
            },
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
                    translationX = scrollOffset * -10f
                }
        ) {
            if (page < links.videoLinks.size) {
                // Update the current video index when the page changes
                LaunchedEffect(page) {
                    currentVideoIndex = page
                }

                // Conditionally reset and start the video
                VideoPlayer(
                    videoLink = links.videoLinks[page],
                    startPlayback = currentVideoIndex == page,
                    onVideoEnded = {
                        coroutineScope.launch {
                            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                            pagerState.animateScrollToPage(nextPage)
                        }
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, "Video error: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                val photoIndex = page - links.videoLinks.size
                val photoLink = links.photoLinks[photoIndex]

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        delay(10000) // Delay before moving to the next page
                        if (pagerState.currentPage == pagerState.pageCount - 1) {
                            pagerState.scrollToPage(0) // Reset the carousel when at the last page
                        } else {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = photoLink,
                        contentDescription = "Photo",
                        modifier = Modifier.fillMaxSize() // Full-size display
                    )
                }
            }
        }
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
