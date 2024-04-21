import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.CoroutineScope

@Composable
fun VideoPlayer(videoLink: String, onVideoEnded: () -> Unit, onError: (String) -> Unit) {
    val context = LocalContext.current // Get the current context
    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope for asynchronous operations

    // Initialize ExoPlayer using the current context
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // Handle ExoPlayer lifecycle with DisposableEffect
    DisposableEffect(exoPlayer) {
        try {
            // Set up the media item from the provided video link (URI)
            val mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(Uri.parse(videoLink))
            exoPlayer.setMediaItem(mediaItem) // Assign the media item to the player
            exoPlayer.prepare() // Prepare the player for playback
            exoPlayer.playWhenReady = true // Start playback

            // Add a listener to handle events like playback state changes
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        // If the playback ends, call the onVideoEnded callback
                        onVideoEnded()
                    }
                }
            })
        } catch (e: Exception) {
            // If an error occurs, log it and call onError callback
            Log.e("ExoPlayerError", "Error setting up video: ${e.message}")
            onError(e.message ?: "Unknown error")
        }

        // Clean up ExoPlayer resources when the composable is disposed
        onDispose {
            exoPlayer.release()
        }
    }

    // Use AndroidView to display the StyledPlayerView with ExoPlayer
    AndroidView(
        factory = { StyledPlayerView(it).apply {
            this.player = exoPlayer // Assign ExoPlayer to the view
            this.useController = false // Disable playback controls
            this.hideController() // Hide the controller to ensure no UI elements are visible
            this.requestFocus() // Ensure the view has focus
            this.setBackgroundColor(android.graphics.Color.BLACK) // Set background color for aesthetics
        }},
        modifier = Modifier.fillMaxSize(), // Make the view fill the available space
        update = { playerView ->
            playerView.hideController() // Keep the controls hidden to avoid user interaction
        }
    )
}
