# BRAVIA NEXUS — Android TV Launcher

**Package:** `com.nexus.bravia`  
**Target:** Android TV / Sony BRAVIA (`minSdk 24`, `targetSdk 35`)  
**Stack:** Kotlin, Jetpack Compose for TV (`androidx.tv:tv-material:1.0.0`), Material 3, Gradle 8.9, AGP 8.7.2

---

## Automatic APK Build via GitHub Actions

This repository includes `.github/workflows/build-apk.yml`, which automatically compiles a debug APK (`BRAVIA_NEXUS.apk`) on every push or manual workflow trigger:

1. Create a new GitHub repository and push the contents of this project (including the hidden `.github/` folder).
2. Open the **Actions** tab on GitHub and select **Build BRAVIA NEXUS APK** (or click **Run workflow**).
3. Once the job finishes, download the **`BRAVIA_NEXUS`** artifact ZIP and extract **`BRAVIA_NEXUS.apk`**.

Alternatively, open this folder directly in **Android Studio** (Ladybug or newer) and click **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

---

## Installing `BRAVIA_NEXUS.apk` on Your Sony BRAVIA TV via USB

1. **Prepare the USB Drive:** Copy `BRAVIA_NEXUS.apk` onto a FAT32/exFAT-formatted USB flash drive.
2. **Enable Unknown Sources on BRAVIA:**
   - Open **Settings → Apps → Security & restrictions → Unknown sources** (or **Special app access → Install unknown apps** on newer Android TV / Google TV versions).
   - Enable installation permission for your file manager app (e.g., *FX File Explorer* or *X-plore* from the Google Play Store on your TV).
3. **Install the APK:**
   - Plug the USB drive into a USB port on your Sony BRAVIA TV.
   - Open your file manager on the TV, navigate to the USB drive, select **`BRAVIA_NEXUS.apk`**, and choose **Install**.
4. **Launch & Optional Default Home Setup:**
   - Open **BRAVIA NEXUS** from your TV's Apps row.
   - To set it as your default Home launcher (on firmware versions that allow it), open **NEXUS Settings → Open Default Home App Settings**. *(Note: Newer Sony Google TV firmware may lock the Home button to the stock Google TV launcher unless configured via ADB.)*
