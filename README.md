# 📡 WiFiMeasure

<table>
  <tr>
    <td>
      WiFiMeasure is an Android app that lets you measure <b>signal strength</b>, <b>distance to access point</b>, <b>link speed</b>, and <b>network frequency</b> in real time — with interactive graphs for visualizing signal strength over time.
      <br><br>
      The app was built to help optimize access point placement indoors by helping to analyze signal strength.
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/0142177c-7bed-4b51-8404-34f70f1de041" alt="App Screenshot" width="800"/>
    </td>
  </tr>
</table>



---

## ✨ Features
- 📶 Real-time WiFi signal measurement.
- 📊 Interactive signal strength graphs using MPAndroidChart.
- 📏 Distance to access point calculation.
- ⚡ Link speed and frequency display.
- 🗄 Local storage with Room Database.
- 🖌 Modern UI built with Jetpack Compose and Material Design 3.
- 📐 Built following Google’s recommended Android app architecture.
- 📱 Compatible with Android 8.0 Oreo and newer.

---

## 🛠 Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose, Material 3
- **Database:** Room
- **Charts:** MPAndroidChart
- **Architecture:** AndroidX Lifecycle & ViewModel

---

## 📱 Compatibility
- **Minimum Android version:** 8.0 Oreo (API 26)
- **Target Android version:** 15 (API 35)

---

## 🚀 Getting Started

Follow these steps to build, install, and launch **WiFiMeasure** using **Android Studio**.

---

### 1️⃣ Prerequisites
- [Android Studio](https://developer.android.com/studio) (Giraffe or newer recommended)
- Android SDK 26 or newer
- Java 11 installed (configured in Android Studio)
- An Android device (real or emulator)

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/yourusername/WiFiMeasure-App.git
cd WiFiMeasure-App
```

### 3️⃣ Open in Android Studio

- Launch **Android Studio**.
- Select **File → Open** and choose the project folder.
- Wait for Gradle to sync all dependencies.


### 4️⃣ Build the Project

  You can build via the **UI** or **command line**:

  ### UI Method
  1. Go to **Build → Make Project**.
  
  ### Command Line Method
  ```bash
  ./gradlew build
  ```
  
### 5️⃣ Run the App
Option A – Android Emulator

    - In Android Studio, open Device Manager.

    - Create and start a virtual device (API 26+).

    - Click Run ▶ in the toolbar or use:

Option B – Physical Device (USB Debugging)

    - Enable Developer Options and USB Debugging on your device.

    - Connect your device via USB.

    - Authorize the connection if prompted.

    - Click Run ▶ in Android Studio or run:

✅ The app should now install and launch on your selected device!

