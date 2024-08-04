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
    val resetTrigger = remember { mutableStateOf(0) } // State to trigger a reset

    LaunchedEffect(resetTrigger.value) {
        // This is triggered when the reset state changes
        // You can initialize or reinitialize any necessary state here
    }

    val totalPages = links.videoLinks.size + links.photoLinks.size

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { totalPages })

    var currentVideoIndex by remember { mutableStateOf(-1) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = 0.dp,
        flingBehavior = PagerDefaults.flingBehavior(pagerState),
        userScrollEnabled = true // Allow swiping
    ) { page ->
        if (page < links.videoLinks.size) {
            VideoPlayer(
                videoLink = links.videoLinks[page],
                startPlayback =  currentVideoIndex == page,
                onVideoEnded = {
                    coroutineScope.launch {
                        val nextPage = (pagerState.currentPage + 1) % totalPages
                        pagerState.animateScrollToPage(nextPage) // Loop back to the first page
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
                    delay(10_000) // Delay before moving to the next page
                    if (pagerState.currentPage == totalPages - 1) {
                        resetTrigger.value += 1 // Reset the carousel when at the last page
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
