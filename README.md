# 🎠 Jetpack Compose Carousel with Video Player

This project showcases a Jetpack Compose carousel with integrated video playback using ExoPlayer. It features smooth transitions, custom animations, and looping functionality. The code includes a carousel for videos and photos, a video player, and animated carousel transitions.

## 🌟 Features

- **Carousel Composable**:
  - A `HorizontalPager` displaying a series of videos and photos.
  - Automatically transitions to the next page when a video ends or after a set delay for photos.
  - Loops back to the first video after the last photo, creating a seamless experience.

- **VideoPlayer Composable**:
  - Uses ExoPlayer to play videos from specified URIs.
  - Disables playback controls for a clean interface.
  - Includes error handling with `Toast` messages for video setup issues.
  - Triggers an event when a video ends, allowing smooth transitions within the carousel.

- **Animated Carousel**:
  - Implements custom animations for transitions between pages.
  - Supports looping back to the first video after reaching the end of the carousel.

## 🛠️ Setup and Usage

To set up and use this project:

1. Ensure you've added the necessary dependencies for ExoPlayer and Coil.
2. Verify you have the required permissions (like `android.permission.INTERNET`) for video playback.
3. Adjust the delay for automatic transitions in the carousel to control its pace.
4. Customize animations and effects to suit your application's design and requirements.

## 📝 Notes

- The carousel allows user swiping by default, but you can disable it to control transitions manually.
- The code loops back to the first video when the last page is reached, ensuring continuous playback.
- For error handling, the code uses `Toast` messages to display any video-related issues.

## 📚 Additional Resources

- [ExoPlayer Documentation](https://exoplayer.dev/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)

## 🧩 Contributing and Support

If you'd like to contribute to this project or need support, feel free to open an issue or submit a pull request.

Enjoy exploring the Jetpack Compose carousel with video playback! If you have any questions, don't hesitate to reach out.
