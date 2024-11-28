# Vision Aid

Vision Aid is a mobile application designed to assist visually impaired individuals by leveraging Azure AI Vision services to scan images, extract text, and describe scenes or objects. The application is built using Kotlin Multiplatform Mobile (KMM) for both Android and iOS platforms.

## Features

### Current Implementation
- Camera integration for real-time image capture
- Image analysis capabilities
- Scene and object detection
- User-friendly interface with accessibility considerations
- Cross-platform support (Android/iOS)

### Planned Features
- Text extraction (OCR) capabilities
- Text-to-Speech integration
- Voice command support
- High contrast mode
- Multilingual support
- Gallery image selection

## Technical Stack

- **Mobile Development**: 
  - Kotlin Multiplatform Mobile (KMM)
  - Jetpack Compose for Android UI
  - SwiftUI for iOS UI
  
- **Dependencies**:
  - CameraX for Android camera functionality
  - Ktor for networking
  - Kotlinx.Serialization for JSON parsing
  - Koin for dependency injection
  - Coil for image loading

## Project Structure

```
VisionAid/
├── androidApp/           # Android-specific code
├── iosApp/              # iOS-specific code
└── shared/              # Shared KMM code
    ├── commonMain/      # Common code for both platforms
    ├── androidMain/     # Android-specific implementations
    └── iosMain/         # iOS-specific implementations
```

## Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Xcode 13 or later (for iOS development)
- JDK 11 or later
- Kotlin 1.9.20 or later
- Azure Account with Computer Vision API access

### Android Setup
1. Clone the repository
2. Open the project in Android Studio
3. Configure your Azure API credentials in the shared module
4. Sync project with Gradle files
5. Run the application on an Android device or emulator

### iOS Setup
1. Install Xcode
2. Open the `iosApp.xcworkspace` file
3. Configure your Azure API credentials
4. Build and run the application

## Development Setup

### Environment Configuration
1. Create a `local.properties` file in the root directory
2. Add your Azure API configuration:
```properties
AZURE_VISION_API_KEY=your_api_key
AZURE_VISION_ENDPOINT=your_endpoint
```

### Building the Project
```bash
# Build Android app
./gradlew :androidApp:build

# Build iOS app
./gradlew :iosApp:build
```

## License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details

## Acknowledgments

- Azure AI Vision Services for providing the core AI capabilities
- The KMM community for their excellent documentation and support

## Contact

Email: williamj949@gmail.com
