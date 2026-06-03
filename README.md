# WiFiMeasure

<table>
  <tr>
    <td width="50%">
WiFiMeasure is an Android app for measuring <b>signal strength</b>, <b>distance to access point</b>, <b>link speed</b>, and <b>network frequency</b> in real time, with interactive graphs for tracking signal strength over time.

Built to help optimize indoor access point placement by making signal analysis easier.
    </td>
    <td width="33%">
      <img src="https://github.com/user-attachments/assets/0142177c-7bed-4b51-8404-34f70f1de041" alt="App Screenshot" width="100%"/>
    </td>
  </tr>
</table>

## Features

- Real-time WiFi signal measurement
- Interactive signal strength graphs via MPAndroidChart
- Distance-to-access-point calculation
- Link speed and frequency display
- Local storage with Room Database
- Material 3 UI built with Jetpack Compose
- Follows Google's recommended Android app architecture
- Supports Android 8.0 (API 26) and newer

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Database:** Room
- **Charts:** MPAndroidChart
- **Architecture:** AndroidX Lifecycle & ViewModel

## Compatibility

- **Minimum:** Android 8.0 Oreo (API 26)
- **Target:** Android 15 (API 35)

## Getting Started

### Prerequisites

- Android Studio Giraffe or newer
- Android SDK 26+
- Java 11 (configured in Android Studio)
- A physical device or emulator

### Setup

```bash
git clone https://github.com/yourusername/WiFiMeasure-App.git
cd WiFiMeasure-App
```

Open the project in Android Studio via **File → Open**, then wait for Gradle to sync.

### Building

Via the IDE: **Build → Make Project**

Via command line:
```bash
./gradlew build
```

### Running

**Emulator** — Open Device Manager, create a virtual device targeting API 26+, and hit Run.

**Physical device** — Enable Developer Options and USB Debugging, connect via USB, authorize the connection if prompted, then hit Run.
