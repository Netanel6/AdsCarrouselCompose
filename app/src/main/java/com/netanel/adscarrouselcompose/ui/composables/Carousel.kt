import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.netanel.adscarrouselcompose.model.Links
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(links: Links) {
    // Create a pager state with the initial page set to 0
    // It calculates the total number of pages based on the number of video and photo links
    val pagerState = rememberPagerState(initialPage = 0) {
        links.videoLinks.size + links.photoLinks.size
    }

    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope for launching coroutines
    val context = LocalContext.current // Get the current context for Toasts and other UI operations

    HorizontalPager(
        state = pagerState, // The pager state controlling the current page and scroll behavior
        modifier = Modifier.fillMaxSize(), // Make the pager take the full size of its parent
        pageSpacing = 0.dp, // Set no spacing between pages
        flingBehavior = PagerDefaults.flingBehavior(pagerState), // Use default fling behavior
        userScrollEnabled = true // Allow users to swipe manually
    ) { page ->
        if (page < links.videoLinks.size) {
            // If the current page is a video page
            // Load the video and move to the next when it finishes
            VideoPlayer(
                videoLink = links.videoLinks[page],
                onVideoEnded = {
                    // When the video ends, move to the next page
                    coroutineScope.launch {
                        val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                        pagerState.animateScrollToPage(nextPage) // Loop back to the first page if at the end
                    }
                },
                onError = { errorMessage ->
                    // Show a Toast message if there's an error with the video
                    Toast.makeText(context, "Video error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // If the current page is a photo page
            val photoIndex = page - links.videoLinks.size
            val photoLink = links.photoLinks[photoIndex]

            // Delay for automatic page scroll after a specified time (10 seconds here)
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    delay(10_000) // Delay before moving to the next page
                    if (pagerState.currentPage == pagerState.pageCount - 1) {
                        // If on the last page, loop back to the first
                        pagerState.animateScrollToPage(0)
                    } else {
                        // Otherwise, move to the next page
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }

            // Display the photo using Coil's AsyncImage
            Column(
                modifier = Modifier.fillMaxSize(), // Make the column fill its parent
                verticalArrangement = Arrangement.Center, // Center the content vertically
                horizontalAlignment = Alignment.CenterHorizontally // Center the content horizontally
            ) {
                AsyncImage(
                    model = photoLink, // Load the photo from the given URL
                    contentDescription = "Photo",
                    modifier = Modifier.fillMaxSize() // Ensure full-size display
                )
            }
        }
    }
}
