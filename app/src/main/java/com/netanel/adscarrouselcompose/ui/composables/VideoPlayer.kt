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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(exoPlayer) {
        try {
            val mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(Uri.parse(videoLink))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        onVideoEnded()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("ExoPlayerError", "Error setting up video: ${e.message}")
            onError(e.message ?: "Unknown error")
        }

        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            StyledPlayerView(it).apply {
                this.player = exoPlayer
                this.useController = false // Disable playback controls
                this.hideController() // Hide the controls
                this.requestFocus() // Ensure focus
                this.setBackgroundColor(android.graphics.Color.BLACK) // Optional: set background color
            }
        },
        modifier = Modifier.fillMaxSize(), // Fill the available space
        update = { playerView ->
            playerView.hideController() // Keep controls hidden
        }
    )
}
