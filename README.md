# EcoTag 🌿
### Citizen-Powered Portable Environmental Intelligence System

> *Turning raw environmental readings into plain-language action, one pocket-sized signal at a time.*

---

## ✦ Project Snapshot

```text
┌──────────────────────────────────────────────────────────────────┐
│  SENSE  →  DECIDE  →  ACT  →  REPORT                             │
│  BME680 + UV  →  Mobile Intelligence  →  Google Sheets Dashboard │
└──────────────────────────────────────────────────────────────────┘
```

| Spark | What it means |
|---|---|
| 🌍 Hyperlocal sensing | Reads the air around you, not a distant city average |
| 📡 Offline by design | Collects data through a local WiFi access point, no internet needed |
| 📍 GPS stamped | Every reading gets location and time from the phone |
| 🧠 Smart interpretation | Raw values become health and comfort guidance |
| ☁ Crowdsourced backend | Syncs to Google Sheets so governments can identify pollution hotspots |
| 🔋 Portable | Built for field use, commuting, and community surveys |

---

## Overview

EcoTag is a portable, low-cost environmental monitoring system that gives individuals real-time awareness of the air quality and environmental conditions around them. Unlike city-level AQI systems that generalize pollution data across large areas, EcoTag delivers **hyperlocal environmental intelligence** — precisely where you are, exactly when it matters.

The system operates on two levels simultaneously. At the individual level, a custom-built hardware tag pairs with a companion mobile app to give the citizen carrying it live readings, health guidance, and a GPS-mapped record of their environmental exposure. At the community level, data from all EcoTag users is quietly synced to a shared Google Sheets backend, building a crowdsourced pollution database that governments and environmental authorities can use to identify which areas need urgent attention, track pollution trends over time, and make evidence-based decisions about where to intervene.

No expensive infrastructure is required on either end. The citizen needs only a phone. The government needs only a browser.

---

## The Problem

```text
┌──────────────────────────────┐      ┌──────────────────────────────┐
│  FOR CITIZENS                │      │  FOR GOVERNMENTS             │
├──────────────────────────────┤      ├──────────────────────────────┤
│  City AQI is too broad       │      │  Official AQI stations are   │
│  No classroom data           │      │  expensive and sparse        │
│  No commute route data       │      │  No neighbourhood-level      │
│  No campus or park data      │      │  granularity                 │
└──────────────────────────────┘      └──────────────────────────────┘
                    ↓
     Both problems, one solution: EcoTag
```

Air pollution is one of the most significant public health challenges of our time, yet the tools available to ordinary citizens are either too expensive, too complex, or too generalized to be practically useful. A city-wide AQI reading tells you nothing about the air quality in your classroom, your office corridor, your morning commute route, or the park where your child plays. At the same time, governments lack the dense, ground-level data needed to prioritize where to place new monitoring stations, where to enforce emission controls, or which communities are bearing a disproportionate pollution burden. EcoTag addresses both gaps with a single, affordable, citizen-powered system.

---

## The Solution

EcoTag bridges this gap by putting a capable environmental sensor in your pocket and pairing it with an intelligent mobile application that translates raw sensor data into plain-language health guidance, safety alerts, and location-mapped pollution insights. Every reading a citizen takes is stored locally on their phone and, when internet is available, automatically synced to a shared Google Sheets database. This transforms individual monitoring acts into collective environmental intelligence — giving governments a real-time, ground-truth view of pollution across their jurisdiction that no fixed monitoring station could provide.

---

## How the Two-Tier System Works

Understanding EcoTag means understanding that it serves two completely different users at the same time.

**The Citizen** powers on the EcoTag device, connects their phone to it, and walks around their neighbourhood, school, or commute route. The app shows them live readings and tells them in plain language whether the air is safe. Every five seconds, a reading is saved locally on the phone stamped with their GPS coordinates and the exact time. They are not thinking about the government — they are thinking about their own health. But their data is quietly building something larger.

**The Government Authority** opens a browser and looks at a Google Sheets dashboard or a Looker Studio map built on top of it. They see thousands of rows of real readings from real citizens at real locations, updated continuously. They can filter by area, by time, by pollution level. They can see that a particular industrial zone consistently shows poor IAQ scores between 6am and 9am. They can see that a school campus has elevated VOC readings every weekday afternoon. This is the kind of ground-truth data that transforms environmental policy from guesswork into evidence.

---

## Hardware

### Components

| Component | Role |
|---|---|
| Wemos D1 Mini (ESP8266) | Main microcontroller and WiFi access point |
| BME680 | Temperature, humidity, atmospheric pressure, and gas/VOC sensing |
| UV Sensor | UV index measurement for outdoor safety awareness |
| OLED Display | On-device real-time data display |
| Buzzer | On-device audio alert system |
| LiPo Battery | Portable power source |

### How the Hardware Works

```text
┌───────────────┐
│  BME680 + UV  │
└───────┬───────┘
        ↓
┌──────────────────────┐
│  Wemos D1 Mini       │
│  reads + processes   │
└───────┬──────────────┘
        ↓
┌──────────────────────┐      ┌────────────────┐
│  OLED Display        │      │  Buzzer Alert  │
└──────────────────────┘      └────────────────┘
```

The BME680 is the core sensing element. It measures four environmental parameters simultaneously: temperature, relative humidity, atmospheric pressure, and gas resistance. The gas resistance value is a direct indicator of volatile organic compound (VOC) concentration in the surrounding air — higher resistance means cleaner air, lower resistance means elevated pollutants. The UV sensor adds an additional outdoor safety dimension by measuring ultraviolet radiation intensity. All readings are processed by the Wemos D1 Mini and displayed locally on the OLED screen. The buzzer fires audio alerts when thresholds are breached.

### Connectivity — AP Mode

EcoTag operates as a **WiFi Access Point** rather than connecting to an existing network. This is a deliberate architectural decision. When powered on, the device creates its own WiFi network:

```text
SSID     :  EcoTag
Password :  ecotag123
```

Any phone connecting to this network can reach the device at `192.168.4.1`. This means EcoTag works anywhere — in a field, inside a building, at an event — with zero dependency on routers, internet, or external infrastructure. Multiple users can connect simultaneously, making it ideal for group demonstrations and collaborative monitoring sessions.

---

## Data

### What the Device Sends

The Wemos exposes a single REST endpoint:

```text
GET http://192.168.4.1/sensor
```

Response:

```json
{
  "temperature": 41.8,
  "humidity": 32.2,
  "pressure": 949,
  "gas": 290,
  "uv": 3
}
```

### What the App Calculates

The mobile app receives the five raw sensor values and derives a richer set of parameters locally on the phone:

```json
{
  "id": "a3f9c1",
  "timestamp": "2025-06-05T14:32:10",
  "lat": 20.2634,
  "lng": 73.0169,
  "temperature": 41.8,
  "humidity": 32.2,
  "pressure": 949,
  "gas": 290,
  "uv": 3,
  "iaq_score": 62,
  "iaq_status": "Moderate",
  "iaq_color": "#ffff00",
  "heat_index": 44.2,
  "comfort_status": "Uncomfortable",
  "uv_status": "Moderate",
  "uv_advice": "Wear sunscreen outdoors",
  "weather_trend": "Stable",
  "health_advice": "Air quality moderate. Sensitive groups should take caution.",
  "alert": null,
  "session_id": "session_20250605_001",
  "device_id": "ecotag_001",
  "synced_to_sheets": false
}
```

The `synced_to_sheets` flag is set to `false` on creation and updated to `true` once the row is successfully uploaded to Google Sheets. This ensures no reading is ever uploaded twice and no reading is ever lost if the upload fails.

### IAQ Calculation

```text
┌──────────────────────┬────────────┬───────────┐
│ Gas Resistance       │ IAQ Score  │ Status    │
├──────────────────────┼────────────┼───────────┤
│ > 400 kΩ             │ 0 - 50     │ Good      │
│ 200 - 400 kΩ         │ 51 - 100   │ Moderate  │
│ 100 - 200 kΩ         │ 101 - 150  │ Poor      │
│ < 100 kΩ             │ 151+       │ Hazardous │
└──────────────────────┴────────────┴───────────┘
```

The Indoor Air Quality score is derived from the BME680 gas resistance reading, aligned with Bosch's BSEC (Bosch Sensortec Environmental Cluster) methodology.

---

## Mobile Application

### Architecture

The app is built on a **local-first, cloud-sync** architecture. All data is written to a SQLite database on the phone first, making collection completely resilient to connectivity loss. When the phone returns to internet connectivity after a monitoring session, unsynced records are automatically batched and uploaded to Google Sheets in the background.

```text
EcoTag AP  →  Poll /sensor every 5s  →  Stamp GPS + timestamp
                                              ↓
                                       Calculate derived values
                                              ↓
                                       Write to local SQLite
                                       (synced_to_sheets: false)
                                              ↓
              ┌───────────────────────────────┼──────────────────────┐
              ↓                               ↓                      ↓
        Live Dashboard                  Heatmap View         Insights & History
                                              ↓
                              (when internet returns)
                                              ↓
                              Batch upload unsynced rows
                              to Google Sheets via App Script
                                              ↓
                              synced_to_sheets → true
                                              ↓
                              Government Dashboard (Sheets / Looker Studio)
```

### Why Local-First Matters

The phone is connected to EcoTag's WiFi access point during data collection, which means it has no internet at that moment. A cloud-only architecture would simply fail to record anything. By writing to SQLite first and syncing later, EcoTag guarantees that not a single reading is ever lost — whether the collection happens in a basement, a forest, or a moving vehicle.

### Features

**Live Dashboard** shows all sensor readings updating in real time, expressed as plain-language status rather than raw numbers. A user connecting their phone to EcoTag sees immediately whether the air around them is good, moderate, or poor, what the UV risk level is, how comfortable the thermal conditions are, and whether any alerts are active.

**Pollution Heatmap** renders GPS-stamped readings on a map, color-coded by IAQ status. As the user walks with EcoTag, the heatmap draws itself — green dots in clean areas, yellow in moderate zones, red where pollution is elevated. After a monitoring session, the map shows exactly which locations had the worst and best air quality.

**Personal Exposure Score** calculates how long the user has spent in each air quality category throughout the day, giving people a genuinely new piece of health information they have never had access to before.

**Focus Score** is derived from the IAQ reading. Elevated VOC concentrations correlate with reduced cognitive performance, and the app translates the IAQ score into a focus index, making the data relevant to students and office workers who spend long hours indoors.

**Smart Alerts** monitor trends rather than single readings. A single spike does not trigger an alert. A sustained rise over multiple readings does, along with a plain-language explanation of what it likely means and what action to take.

**Session History** stores each monitoring walk as a named session, allowing the user to revisit previous heatmaps, compare air quality across different locations, and observe patterns over time.

**Background Sync** runs automatically when the phone reconnects to internet. All unsynced readings are uploaded to Google Sheets in a single batch. The user does not need to do anything — it happens silently in the background.

### Tech Stack

The mobile application is built with **React Native** using the Expo framework, chosen for cross-platform compatibility and rapid development. Key libraries include `expo-sqlite` for local data persistence, `expo-location` for GPS access, `react-native-maps` for heatmap rendering, and the standard `fetch` API for Google Sheets sync via App Script. All computation happens on-device.

---

## Google Sheets Backend

### Why Google Sheets

Google Sheets was chosen as the cloud backend deliberately. It requires zero server infrastructure, zero cost, and zero technical knowledge to view. A government officer does not need to understand databases or APIs — they open a spreadsheet and they see the data. It is also trivially easy to build a Looker Studio dashboard directly on top of a Google Sheet, turning raw rows into visual maps and charts with no additional development.

### How the Sync Works

A Google Apps Script Web App acts as the receiving endpoint. The mobile app sends an HTTP POST request containing an array of unsynced readings. The Apps Script appends each reading as a new row in the sheet.

```text
Mobile App
    ↓  HTTP POST (JSON array of readings)
Google Apps Script Web App URL
    ↓  appends rows
Google Sheet
    ↓  powers
Looker Studio Map / Government Dashboard
```

### What Each Row Looks Like in the Sheet

```text
timestamp            | lat      | lng      | temperature | humidity | pressure | gas | uv | iaq_score | iaq_status | session_id          | device_id
2025-06-05T14:32:10  | 20.2634  | 73.0169  | 41.8        | 32.2     | 949      | 290 | 3  | 62        | Moderate   | session_20250605_001 | ecotag_001
```

### What the Government Sees

With data from multiple citizens flowing into the same sheet, environmental authorities gain a ground-truth picture of air quality at street level across their entire jurisdiction. They can filter by date, by location, or by IAQ status. They can identify pollution hotspots that no fixed monitoring station is close enough to detect. They can track how a particular area's air quality changes over weeks and months. And they can do all of this through a tool — Google Sheets — that every government office already has access to.

---

## System Architecture

```text
┌─────────────────────────────┐
│  BME680 + UV Sensor         │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  Wemos D1 Mini              │
│  processes sensor readings  │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  OLED Display + Buzzer      │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  AP Mode WiFi (EcoTag)      │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  EcoTag Mobile App          │
│  GPS + IAQ + Calculations   │
└──────────────┬──────────────┘
               ↓
┌─────────────────────────────┐
│  Local SQLite (primary)     │
└──────┬──────────────────────┘
       │                ↓ (internet available)
       │         HTTP POST to App Script
       │                ↓
       │         Google Sheets (cloud)
       │                ↓
       │         Government Dashboard
       ↓
┌─────────────────────────────┐
│  Live Cards | Heatmap |     │
│  Insights   | Alerts        │
└─────────────────────────────┘
```

---

## Repository Structure

```text
ecotag/
│
├── firmware/                    # Wemos D1 Mini Arduino code
│   ├── ecotag_firmware.ino      # Main firmware file
│   └── config.h                 # WiFi credentials and pin definitions
│
├── app/                         # React Native mobile application
│   ├── src/
│   │   ├── api/                 # Sensor polling and data fetching
│   │   ├── db/                  # SQLite schema and queries
│   │   ├── logic/               # IAQ, heat index, UV calculations
│   │   ├── sync/                # Google Sheets batch upload logic
│   │   ├── screens/             # Dashboard, Heatmap, History, Alerts
│   │   └── components/          # Reusable UI components
│   ├── App.js
│   └── package.json
│
├── sheets/
│   └── ecotag_appscript.gs      # Google Apps Script Web App code
│
├── docs/                        # Project documentation and diagrams
│   ├── architecture.png
│   └── hardware_schematic.png
│
└── README.md
```

---

## Getting Started

### Hardware Setup

Connect the BME680 to the Wemos D1 Mini via I2C (SDA → D2, SCL → D1). Connect the UV sensor to the analog pin (A0). Connect the OLED display via I2C on the same bus. Connect the buzzer to D5. Flash the firmware using the Arduino IDE with the ESP8266 board package installed.

### Firmware Dependencies

Install the following libraries via the Arduino Library Manager before flashing:

- `Adafruit BME680` by Adafruit
- `Adafruit SSD1306` by Adafruit
- `ArduinoJson` by Benoit Blanchon
- `ESP8266WiFi` (included with ESP8266 board package)

### Google Sheets Setup

Create a new Google Sheet and open the Apps Script editor from the Extensions menu. Paste the contents of `sheets/ecotag_appscript.gs` and deploy it as a Web App with access set to "Anyone". Copy the deployment URL and add it to the app's configuration file as `SHEETS_WEBHOOK_URL`.

### App Setup

```bash
# Clone the repository
git clone https://github.com/your-username/ecotag.git

# Navigate to the app directory
cd ecotag/app

# Install dependencies
npm install

# Start the Expo development server
npx expo start
```

Connect your phone to the EcoTag WiFi network, then launch the app. The app will automatically begin polling `http://192.168.4.1/sensor` every 5 seconds and syncing to Sheets whenever internet is available.

---

## Applications

EcoTag serves two distinct user groups with the same system. Individual citizens gain personal environmental awareness they have never had access to before — understanding their own exposure during commutes, outdoor activity, or time spent in poorly ventilated spaces. Schools and colleges can monitor classroom air quality and correlate it with student focus and wellbeing. Community groups and NGOs can conduct systematic surveys of pollution hotspots and build evidence for local advocacy.

At the governance level, environmental authorities and municipal bodies gain access to a continuously updated, citizen-contributed pollution dataset covering areas and granularities that fixed monitoring stations cannot reach. This data can directly inform decisions about where to enforce emission controls, where to install additional monitoring infrastructure, and which communities are most in need of environmental intervention.

---

## Future Scope

The current implementation establishes the complete sensing, communication, local storage, and cloud reporting pipeline. Future development directions include AI-based pollution prediction using historical session data aggregated across the Sheets backend, automated alert generation to government dashboards when a threshold number of citizen readings from the same area exceed a danger level, integration with municipal smart city platforms via standard APIs, PM2.5 particulate matter sensing via an additional sensor module, and an EcoTag-S variant with extended battery life and ruggedized enclosure for permanent outdoor installation.

---

## Team

| Member | Name | Role |
|---|---|---|
| Member 1 | R. Vidya Shankar | Project Lead, Hardware Development & Sensor Integration |
| Member 2 | K. Shiva Shankara Rama Krishna Mohan Rao | Presentation & Documentation |
| Member 3 | G. Tarun Naga Venkat | Software & Mobile Application Development |

---

## License

This project is open source under the MIT License. See `LICENSE` for details.

---

> **EcoTag — Know your air. Own your health. Power your government's decisions.**
