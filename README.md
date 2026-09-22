# EazyRes — OPSC6312 POE Part 2 (App Prototype)

"Find Your Home Near Campus, With Total Confidence" — a student
accommodation app for the South African market, built for Part 2 of the
OPSC6312 POE. This repo contains two projects:

- **`android/`** — the Kotlin/Android Studio app (Retrofit + Coroutines, MVVM-ish layering)
- **`backend/`** — the REST API the app talks to (Node.js/Express + SQLite, bcrypt password hashing, JWT auth)

## What's implemented (Part 2 scope — PoE-only items excluded)

| Requirement | Where |
|---|---|
| Register & log in, password encrypted | `ui/auth/*` (app) + `routes/auth.js` — bcrypt-hashes the password server-side, only a JWT token is stored on-device (in `EncryptedSharedPreferences`, backed by the Android Keystore) |
| Change settings | `ui/settings/SettingsActivity.kt` + `PUT /users/settings` |
| Connect to a REST API + database | `data/api/*` (Retrofit) talking to the Express API, which persists to SQLite |
| A few of your Part-1 EazyRes features | Property browsing (Home), property detail, contact-landlord stub |
| Input validation / no crash on bad input | `utils/Validators.kt`, empty/loading/error states on every screen |
| Unit testing | `app/src/test/.../ValidatorsTest.kt` (Kotlin) and `backend/__tests__/auth.test.js` (Node) |
| GitHub Actions | `.github/workflows/android-build.yml` and `backend-ci.yml` |

Left for the Final PoE (as instructed — these are marked "PoE only" in the
brief): SSO, offline mode + sync (Room/SQLite), real-time push
notifications, multi-language support.

## Running the backend

```bash
cd backend
npm install
cp .env.example .env      # edit JWT_SECRET
npm start                 # http://localhost:3000
```

This seeds three demo properties into `eazyres.db` (SQLite) the first
time it runs. To make it reachable from the Android emulator, no change
is needed — the app already points at `http://10.0.2.2:3000/`, which is
the emulator's alias for your machine's localhost. For a physical
device, put your machine's LAN IP in `BASE_URL` in
`android/app/build.gradle.kts` instead, or deploy the `backend/` folder
to a free host (Render, Railway, Fly.io) and use that HTTPS URL — the
POE brief requires the API to be hosted so it works outside your own
machine.

## Opening the Android app

1. Open `android/` as a project in Android Studio (Hedgehog or newer).
2. Let it sync — Android Studio will generate the Gradle wrapper
   (`gradlew`, `gradle/wrapper/*`) automatically the first time it
   opens the project; these files are intentionally not checked in
   raw here since the wrapper jar is a binary. If Android Studio
   doesn't offer to add it, run `gradle wrapper` once with a local
   Gradle install.
3. Run the backend first (see above), then run the app on an emulator
   or device.
4. Register a new account, log in, browse properties, open a property,
   and update your settings — each of those exercises the REST API.

## Running tests

```bash
# Kotlin unit tests
cd android && ./gradlew testDebugUnitTest

# Backend unit tests
cd backend && npm install && npm test
```

YouTube Link

https://youtube.com/shorts/tQk5-_ej_lc?si=6R1OF9O44ZTtojM-
