
# Bharat Script Transliterator – Street Sign Reader

### 📱 Read any Indian signboard in your language — instantly and offline.

---

## 🚀 Overview

**Bharat Script Transliterator** is an Android application that helps travelers, migrants, and language learners read **Indian street signs, public boards, and direction markers** written in unfamiliar scripts.

Instead of translating the meaning, it **preserves pronunciation** — showing how a word is *read* in your preferred script.
For example, a Tamil street name can be transliterated into Devanagari or Gurmukhi, allowing users to **sound it out naturally**.

---

## 🌍 Problem Statement

India’s 22+ official scripts make navigation across states challenging.
Travelers and migrants often find it difficult to read local signboards written in unfamiliar scripts.
Existing apps focus on **translation**, not **transliteration**, making them ineffective for proper nouns like *place names*.

**Our goal:** Bridge this linguistic gap through a real-time, offline transliteration tool for all Indian scripts.

---

## 🎯 Key Features

### 🔹 MVP Features (Phase 1)

* **Camera-based transliteration** — Point your phone at a signboard and get instant reading in your preferred script.
* **Automatic script detection** — Detects the original script (e.g., Tamil, Bengali, Devanagari, etc.) automatically.
* **Offline functionality** — Works without internet using on-device models.
* **History storage** — Keeps your last 50 transliterations.
* **User settings** — Choose target script, adjust font size, and toggle camera quality.

**Supported Scripts (Phase 1):**

> Devanagari (Hindi, Marathi), Tamil, Bengali, Telugu, Kannada, Malayalam, Gurmukhi (Punjabi), Gujarati



### Phase 2 – Enhanced Recognition

* Multi-sign detection and low-light optimization
* Voice output for transliterated text
* Additional scripts (Odia, Urdu, Assamese, Kashmiri, Konkani, Manipuri)
* Share transliterations and favorite them

### Phase 3 – Advanced Capabilities

* AR mode for real-time overlays
* Continuous recognition while moving
* Crowdsourced corrections and learning module
* Smart region-based script detection

---

## ⚙️ Technical Stack

| Component                       | Technology                                  |
| ------------------------------- | ------------------------------------------- |
| **App Platform**                | Android (8.0 and above)                     |
| **Architecture**                | MVVM + Clean Architecture                   |
| **OCR Engine**                  | Google ML Kit / Tesseract                   |
| **Transliteration Engine**      | AI4Bharat, Indic-trans, or Hybrid ML models |
| **Storage**                     | Room Database (SQLite)                      |
| **Analytics & Crash Reporting** | Firebase                                    |
| **Optional Cloud Processing**   | REST API + Firebase Remote Config           |



---

## 🎨 User Experience Highlights

* Clean, minimal **camera-first interface**
* **One-tap capture** and instant result overlay
* **Large readable fonts** and accessibility support
* Fully **offline-first** with graceful fallbacks

---

## 🧠 Challenges & Solutions

| Challenge                 | Solution                                            |
| ------------------------- | --------------------------------------------------- |
| Multi-script detection    | Ensemble ML models and location-based hints         |
| Large model sizes         | Quantized (<5MB per script) and on-demand downloads |
| Transliteration ambiguity | Phonetic-first, ISO 15919 standard                  |
| Poor lighting and angles  | Image preprocessing & perspective correction        |

---







## 🧭 Repository Structure

```
streetlight/
 ├── app/
 │   ├── src/
 │   ├── java/com/example/street_light/
 │   ├── res/
 │   └── build.gradle.kts
 ├── gradle/
 ├── settings.gradle.kts
 ├── .idea/
 └── README.md
```




