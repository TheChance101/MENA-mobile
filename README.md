<p align="center">
    <img width="720" height="372" alt="MENA" src="https://github.com/user-attachments/assets/58bb55b7-960c-4ed2-af67-e65d24272d0d" />
</p>
<p align="center">
    <b>MENA Project:</b> The biggest Kotlin & Compose Multiplatform (KMP - CMP) open-source initiative.
    <br><br>
    The Chance MENA App is a super app crafted for the Middle Eastern and North African community - empowering you to do everything you need, all while nurturing a genuine sense of belonging.<br>
    Built with <b>layered architecture, modularized by feature</b>, with <b>MVVM pattern used for the UI layer.</b>
    It consolidates six distinct business domains: <b>Identity, Chat, Wallet, Dukan, Trend, and Faith</b>
    into a unified platform optimized for large-scale distributed team development with support of an Admin panel that oversees
    and manages most of these business domains.
    <br><br>
</p>

---

## 📑 Table of Contents

1. [System Architecture Overview](#system-architecture-overview)
    - [High-Level Architecture](#high-level-architecture)
    - [Layered Architecture Per Feature](#layered-architecture-per-feature)
    - [Data Flow Architecture](#data-flow-architecture)
    - [Technologies](#technologies)
2. [Identity Feature](#identity-feature)
3. [Chat Feature](#chat-feature)
4. [Wallet Feature](#wallet-feature)
5. [Trends Feature](#trends-feature)
6. [Dukan Feature](#dukan-feature)
7. [Faith Feature](#faith-feature)
8. [Admin Panel](#admin-panel)
9. [Requirements](#requirements)
10. [Contributors](#contributors)
11. [License](#license)

---

## System Architecture Overview

### High-Level Architecture

The Chance MENA App implements a **Feature-Based Multi-Module Architecture** where each business domain is encapsulated in an independent feature module following MVVM patterns used for the UI layer.

<p align="center">
  <img width="3166" height="2365" alt="Mena-Page-3" src="https://github.com/user-attachments/assets/08c1b809-5bbb-4f13-aefa-69bb3b37ec71" />
<p/>

### Layered architecture per feature

Each feature module follows a strict three-layer architecture

### Data Flow Architecture

<p align="center">
  <img width="648" height="700" alt="Mena-Page-2 drawio" src="https://github.com/user-attachments/assets/b3fbf84c-93a5-4316-b41f-c8f3653f7fd5" />
<p/>
  
---

## Technologies

### Core
- [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html)
- [Kotlinx Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlinx Serialization (JSON)](https://kotlinlang.org/docs/serialization.html)
- [Kotlinx Datetime](https://kotlinlang.org/api/kotlinx-datetime/kotlinx-datetime/kotlinx.datetime/)
### Dependency Injection
- [Koin (Core, Android, Compose, Annotations)](https://insert-koin.io/)
### Networking
- [Ktor Client (Android, OkHttp, CIO, Darwin)](https://ktor.io/docs/welcome.html)
- [Ktorfit](https://foso.github.io/Ktorfit/quick-start/)
### Database & Storage
- [Room / AndroidX Room](https://developer.android.com/jetpack/androidx/releases/room)
- [SQLite (Bundled)](https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase)
- [AndroidX DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Multiplatform Settings](https://github.com/russhwolf/multiplatform-settings)
### UI / Compose
- [Compose Multiplatform](https://kotlinlang.org/docs/multiplatform/compose-multiplatform.html)
- [AndroidX Navigation Compose](https://developer.android.com/develop/ui/compose/navigation)
- [Voyager](https://voyager.adriel.cafe/)
- [Capturable Compose](https://github.com/PatilShreyas/Capturable)
### Media
- [Coil (GIF & Compose integration)](https://coil-kt.github.io/coil/compose/)
- [Peekaboo](https://github.com/steipete/Peekaboo)
- [Record (Audio)](https://github.com/theolm/kmp-record)
- [Media3 ExoPlayer (DASH & UI)](https://developer.android.com/media/media3/exoplayer)
### Maps & Location
- [MapLibre Compose](https://maplibre.org/maplibre-compose/)
- [OSMDroid](https://github.com/osmdroid/osmdroid)
- [GeoCoder / GeoLocation](https://developer.android.com/reference/android/location/Geocoder)
- [JXMapViewer (Desktop)](https://github.com/msteiger/jxmapviewer2)
### Other Utilities
- [FileKit](https://github.com/vinceglb/FileKit)
- [Krop](https://github.com/timhuang1018/Krop)
- [Squircle Shape](https://github.com/stoyan-vuchev/squircle-shape)
- [Qrose (QR Code)](https://github.com/alexzhirkevich/qrose)
- [Batik (Desktop SVG)](https://xmlgraphics.apache.org/batik/)
### Testing
- [Turbine](https://github.com/cashapp/turbine)
- [Coroutines Test](https://developer.android.com/kotlin/coroutines/test)
- [Mokkery](https://mokkery.dev/)

---

## Identity Feature

> The cornerstone of MENA's user management system. It provides a seamless and secure experience for users to join, access, and manage their personal information.
> This module acts as a centralized service, ensuring consistency and security across all other parts of MENA.

<img width="1312" height="726" alt="Identity" src="https://github.com/user-attachments/assets/96b5684a-988a-4da9-8f2e-d3a571a07011" /><br>

> [!Note]
> ### 🔐 Authentication & Account Management
> - **Login**: Access your existing account with your credentials.
> - **Register**: Create a new account and get started with the application.
> - **Forget Password**: Reset your password securely if you forget it.
> - **Logout**: Sign out of your account from the current device.
> - **Delete Account**: Permanently delete your account and all associated data, respecting privacy.
> ### 👥 User Profile & Social Features
> - **Profile**: View and manage your personal information, and share your profile with others.
> - **Invite Friends**: Invite your contacts to join the application.
> - **Location & Addresses**: Add, view, and manage your saved addresses.
> ### 🎨 Personalization & Accessibility
> - **Multi Themes**: Switch between Dark & Light themes.
> - **Multi Languages**: Support for English and Arabic to reach a wider audience.

---

## Chat Feature

> The Chat enables seamless communication and interaction within the MENA app. It provides a rich set of capabilities designed to connect users, enhance social engagement, and integrate core app services.

![chat mockup](https://github.com/user-attachments/assets/e2213944-721c-47fc-ae15-36a631a502f9)<br>

> [!Note]
> ### 💬 Communication
> - **Messaging**: Start conversations with any synced contact and exchange text, photos, and voice messages.
> - **Message Reactions**: React to messages, photos, and voice notes for quick and expressive communication.
> - **Share Quran Ayat**: Send any selected Ayah from any Surah directly inside the chat.
>
> ### 📇 Contacts & Connectivity
> - **Contact Synchronization**: Sync device contacts to easily find and connect with people already using the MENA app.
>
> ### 💸 Financial Actions
> - **Send Money in Chat**: Transfer money directly to contacts within the chat, provided sufficient wallet balance.
>
> ### ⚙️ Management & Utility
> - **Chat Management**: Delete entire conversations when needed.
> - **Chat List Overview**: View all chats with indicators for recent or unread messages.
> - **Order Details Integration**: For Dukan conversations, view detailed order information directly inside the chat.

---

## Wallet Feature

> The Wallet serves as the core financial engine of the application, managing user balances and enabling secure transactions within the ecosystem.
> It supports peer-to-peer transfers and facilitates payments for Dukan orders.

<img width="1312" height="726" alt="wallet-photo" src="https://github.com/user-attachments/assets/599dbcc0-854e-4ef7-a3d2-728868e54744" /><br>

> [!Note]
> ### 💳 Transactions & Payments
> - **Peer-to-Peer (P2P) Transfer**: Enables users to transfer funds securely to other registered users on the platform.
> - **Dukan Payments**: Integrated payment gateway allowing users to purchase items from Dukans using their wallet balance.
>
> ### 📊 Account Overview
> - **Real-time Balance**: Displays the current available balance fetched from the backend.
> - **Transaction History**: A comprehensive log of all incoming and outgoing transactions with filters by type, status, and date range.
>
> ### 📑 Reporting & Exports
> - **Transaction Sharing**: Export and share individual transaction receipts via native sharing sheets.
> - **Generation**: Generate statements with custom filters.
> - **Download & View**: Render and download statements in PDF format.
> - **Statement Manager**: Manage and delete previously downloaded statements locally.

---

## Trends Feature

> A short-video module offering a personalized Trend feed and simple tools to create, publish, and manage 1-minute trends.

<img width="1436" height="805" alt="trends" src="https://github.com/user-attachments/assets/65e5fb7e-e482-4ff3-a6f5-e016a0fabadb" /><br>

> [!Note]
> ### 🎯 Personalized Content Discovery (The Trend)
> - On first use, users select their interests; categories are fetched dynamically for a tailored experience.
> - The Trend updates instantly as interests change, ensuring fresh and relevant content.
> - Smart playback analytics continuously refine recommendations for higher quality discovery.
>
> ### 🎬 Trend Video Creation & Publishing
> - Upload short videos (up to 1 minute / 100MB) with built‑in pre‑upload validation for smooth publishing.
> - Each trend requires selecting 1–3 categories, with an optional description to boost discoverability.
>
> ### 📂 Content Management & Favorites
> - **My Trends** provides a centralized hub where users can view or delete all their own published content.
> - Liked trends are automatically saved to **Favorites** for quick access later.
>
> ### 🤝 User Interaction & Engagement
> - Engage with the community by liking any trend.
> - For your own trends: manage, delete, or jump directly to your profile.
> - For others’ trends: express yourself through reactions and support creators.

---

## Dukan Feature

> Dukan connects you with local stores and markets in your area. Discover exclusive deals, browse products across multiple categories, and shop from popular retailers. Whether you're looking for beauty products, clothes, furniture, or household essentials, Dukan makes local shopping simple and convenient.

<img width="2048" height="1132" alt="dukan" src="https://github.com/user-attachments/assets/03c25946-1daa-4c18-8c7d-cc9304a27a09" /><br>

> [!Note]
> ### 🏪 As a Dukan Owner
> - **Create Your Dukan**: Design your store with custom styles and colors — *Your Dukan, Your Vibe*.
> - **Organize Products**: Build shelves to neatly categorize and display your items.
> - **Product Management**:
>   - Add products to shelves with full details.
>   - Mark items as **Out of Stock** when unavailable.
>   - Apply discounts to encourage purchases.
>   - Remove products easily when you stop selling them.
>
> ### 👤 As a User — Discover
> - Find the **best Dukans nearby** based on your location.
> - Explore Dukans offering the **highest discounts**.
> - Browse by category or search for specific Dukans/products by name.
> - Get personalized recommendations based on your **purchase history**.
> - View detailed product information before making a decision.
>
> ### 🛒 As a User — Easy Shopping
> - Quickly identify **best-selling products** in each Dukan.
> - Enjoy **fast checkout** with delivery to your active location.
> - Maintain a **unique cart per Dukan** for organized shopping.
  
---

## Faith Feature

> Faith serves as your comprehensive Islamic companion, bringing together essential tools for daily worship, Quran study, and spiritual growth. Whether you're at home or traveling, stay connected to your faith with accurate prayer times, the complete Quran, and helpful features designed for modern Muslim life.


<img width="1423" height="798" alt="faith" src="https://github.com/user-attachments/assets/aad4474f-b9fe-4d49-8fa8-8859f8ef82e9"/><br>

> [!Note]
> ### 🕌 Core Worship Features
> - **Prayer Times**: Accurate notifications with countdown to the next prayer, displayed in Hijri calendar.
>   - Browse past and upcoming schedules.
>   - Worldwide support with automatic location-based calculations.
> - **Qiblah Direction**: Real-time compass to find the precise Qiblah direction, with calibration support.
> ### 📖 Quran Experience
> - **Browse & Read**: Navigate all Surahs with Makki/Madani classification and a seamless reading interface.
> - **Interactive Tools**:
>   - Select Ayah: Tap any verse to share instantly.
>   - Bookmark: Save positions, manage multiple bookmarks.
>   - Copy: Copy verses to clipboard for easy sharing.
> - **Listen & Recite**:
>   - Stream or download Surahs for offline listening.
>   - Choose from multiple Reciters, preview voices, and control playback (play, pause, repeat, and continue).
> - **Search**:
>   - Search within a Surah or globally across the Quran.
>   - Jump directly to verses from search results.
> ### 🌍 Community
> - **Nearby Mosques**: Discover mosques around you on an interactive map. Search any location, tap the map to find nearby mosques, view mosque details, and navigate to them directly through your device's map app. You can also contribute by adding new prayer spaces.
> - **Personalized Islamic dashboard with**:
>   - Today’s prayer times at a glance (next prayer highlighted).
>   - Quran reading progress tracking (current page & Surah).
>   - Quick shortcuts to Quran reader, Qiblah finder, mosque locator, and Tilawah player.
  
---

## Admin Panel

> The Admin Panel is a desktop application designed to give administrators full control over user management, Dukan onboarding, and operational workflows across the MENA platform. It provides secure access, comprehensive data visibility, and powerful management tools to ensure smooth platform operations.

<img width="6864" height="5100" alt="MENA-Admin-min" src="https://github.com/user-attachments/assets/8308c4db-3271-4cdd-b2db-4432bc1bb819" /><br>

> [!Note]
> ### 🔐 Secure Authentication
> - Admins can log in to access the system with full security.
> - Secure logout ensures sessions are properly terminated.
> ### 👥 User Management
> - View a complete list of users with detailed information (phone number, last login date, last visited date).
> - Monitor user status in real time (Active or Blocked).
> - Activate or block users directly from the admin panel.
> ### 📝 Dukan Requests Review
> - Access all pending Dukan registration requests with full details (name, location, etc.).
> - Approve requests instantly or reject them with a personalized rejection message sent to the user.
> ### 🏪 Dukan Management
> - Manage all approved Dukans with complete data: name, location, shelves, and product prices.
> - Control Dukan activation status (Activated or Deactivated).
> - When deactivating, send a clear deactivation message to the Dukan owner.
> ### 💰 User Wallet Deposits
> - Admins can add money directly to any MENA app user’s wallet.
> - Deposits are made by entering the user’s phone number and the desired amount.
  
---

## Requirements

Before you begin, ensure you have met the following requirements:

- **Android Studio**
    - [Download Android Studio](https://developer.android.com/studio)

- **MENA Backend Server**
    - You must run the backend locally or on your own hosting environment before running the app.
    - Backend Repo: [MENA-backend](https://github.com/TheChance101/MENA-backend)

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/TheChance101/MENA-mobile.git
    cd MENA-mobile
    ```

2. Run the backend server and obtain its Base URL (e.g., `http://localhost:8080/` or your deployed server URL).

3. Add the Base URL to your `local.properties` file:
    ```properties
    BASE_URL=YOUR_BACKEND_BASE_URL
    BASE_URL_DEVELOPMENT=YOUR_BACKEND_BASE_URL
    BASE_URL_STAGING=YOUR_BACKEND_BASE_URL
    BASE_URL_PRODUCTION=YOUR_BACKEND_BASE_URL
    ```

4. Sync the project and run it.

---

## Contributors

<p align="center">
  <a href="https://github.com/TheChance101/MENA-mobile/graphs/contributors">
    <img src="https://contrib.rocks/image?repo=TheChance101/MENA-mobile" alt="picture"/>
  </a>
</p>

---

## License

    Copyright 2025 The Chance

    Licensed under the Apache License, Version 2.0 (the "License");
    You may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
