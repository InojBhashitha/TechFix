# 🔧 TechFix

**Android-based Computer & Mobile Phone Repair Management System**

TechFix is a full-stack repair management platform designed for multi-branch electronics repair shops. It pairs a **Spring Boot REST API** backend with a **native Android (Java)** mobile app, enabling customers to book repairs, track progress in real time, and make payments — while giving staff and administrators complete operational control.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Demo Accounts](#-demo-accounts)
- [Repair Status Workflow](#-repair-status-workflow)
- [Screenshots](#-screenshots)
- [License](#-license)

---

## ✨ Features

### 👤 Customer Portal
- **Account Registration & Login** — Secure JWT-based authentication
- **Book a Repair** — Multi-step wizard: select device category → choose repair service → enter device details → attach photos → auto-select nearest branch via GPS
- **Real-Time Repair Tracking** — Live 8-step status timeline with history log
- **In-App Payment** — Credit/debit card checkout with payment confirmation
- **Booking History** — View all past and active repair bookings
- **Branch Finder** — Google Maps integration showing nearest branches with distance

### 🛠️ Staff Dashboard
- **Repair Queue Management** — View, filter, and manage all branch repair requests
- **Status Updates** — Progress bookings through the repair workflow with notes
- **Technician Assignment** — Assign available technicians to incoming repairs
- **Branch Inventory** — Track spare parts stock with low-stock alerts
- **Branch-Scoped Access** — Staff only see repairs for their assigned branch

### 👑 Admin Panel
- **Global Operations Oversight** — System-wide view across all branches
- **Branch Filter Controls** — Switch between Colombo, Galle, or all branches
- **Admin Status Override** — Update any repair status with cost adjustments
- **Global Inventory Management** — Manage spare parts across all branches
- **Technician Management** — Assign technicians across branches

---

## 🛠 Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 3.2.5 | REST API Framework |
| Spring Security | 6.1 | Authentication & Authorization |
| Spring Data JPA | 3.2 | ORM & Data Access |
| Hibernate | 6.4.4 | JPA Implementation |
| H2 Database | 2.x | In-Memory Dev Database |
| MySQL Connector | 8.x | Production Database Support |
| JWT (jjwt) | 0.11.5 | Token Authentication |
| SpringDoc OpenAPI | 2.5.0 | Swagger API Docs |
| Maven | 3.x | Build Tool |

### Android
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Android SDK | 34 (compileSdk) | Target Platform |
| Min SDK | 24 (Android 7.0+) | Minimum Support |
| Material Design 3 | 1.11.0 | UI Components |
| Retrofit 2 | 2.9.0 | HTTP Client |
| OkHttp | 4.12.0 | Network Layer |
| Room | 2.6.1 | Local SQLite Cache |
| Glide | 4.16.0 | Image Loading |
| Google Maps SDK | 18.2.0 | Branch Maps |
| Play Services Location | 21.2.0 | GPS / Geolocation |
| CameraX | 1.3.2 | Device Photo Capture |
| Gradle | 8.x | Build Tool |

---

## 🏛 Architecture

```
┌──────────────────────────────────────────────────────┐
│                  Android App (Java)                  │
│  ┌────────┐  ┌────────┐  ┌────────┐  ┌───────────┐  │
│  │Customer│  │ Staff  │  │ Admin  │  │   Auth    │  │
│  │  Flow  │  │  Flow  │  │  Flow  │  │  Flow     │  │
│  └───┬────┘  └───┬────┘  └───┬────┘  └─────┬─────┘  │
│      └───────────┴───────────┴──────────────┘        │
│                      │  Retrofit                     │
│               ┌──────┴──────┐                        │
│               │  ApiService │  (REST Client)         │
│               └──────┬──────┘                        │
│                      │                               │
│           ┌──────────┴──────────┐                    │
│           │  Room DB (SQLite)   │  (Offline Cache)   │
│           └─────────────────────┘                    │
└──────────────────────┬───────────────────────────────┘
                       │ HTTPS / JSON
┌──────────────────────┴───────────────────────────────┐
│              Spring Boot REST API                    │
│  ┌────────────────────────────────────────────────┐  │
│  │  Controllers (Auth, Booking, Staff, Payment)   │  │
│  └────────────────────┬───────────────────────────┘  │
│  ┌────────────────────┴───────────────────────────┐  │
│  │  Services (Business Logic)                     │  │
│  └────────────────────┬───────────────────────────┘  │
│  ┌────────────────────┴───────────────────────────┐  │
│  │  JPA Repositories (Data Access)                │  │
│  └────────────────────┬───────────────────────────┘  │
│  ┌────────────────────┴───────────────────────────┐  │
│  │  Spring Security + JWT Filter                  │  │
│  └────────────────────────────────────────────────┘  │
│                       │                              │
│           ┌───────────┴───────────┐                  │
│           │  H2 / MySQL Database  │                  │
│           └───────────────────────┘                  │
└──────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
TechFix/
├── backend/                          # Spring Boot REST API
│   ├── pom.xml                       # Maven dependencies
│   └── src/main/java/com/techfix/api/
│       ├── TechFixApplication.java   # Entry point
│       ├── config/                   # Security, CORS, data seeder
│       ├── controllers/              # REST endpoints
│       ├── dto/                      # Request/response DTOs
│       ├── entities/                 # JPA entity models
│       ├── enums/                    # RepairStatus, UserRole, PaymentStatus
│       ├── exceptions/               # Global error handling
│       ├── repositories/             # Spring Data JPA repos
│       ├── security/                 # JWT filter, auth provider
│       └── services/                 # Business logic layer
│
├── android/                          # Native Android App
│   └── app/src/main/
│       ├── java/com/techfix/app/
│       │   ├── data/
│       │   │   ├── local/            # Room DB, DAOs, entities
│       │   │   └── remote/           # Retrofit ApiService, DTOs
│       │   ├── ui/
│       │   │   ├── auth/             # Login, Register, Splash
│       │   │   ├── customer/         # Customer dashboard
│       │   │   ├── booking/          # Book repair wizard
│       │   │   ├── tracking/         # Real-time repair tracking
│       │   │   ├── payment/          # Payment checkout
│       │   │   ├── branches/         # Google Maps branch finder
│       │   │   ├── staff/            # Staff operations dashboard
│       │   │   ├── admin/            # Admin control panel
│       │   │   └── adapters/         # RecyclerView adapters
│       │   └── utils/                # SessionManager, helpers
│       ├── res/
│       │   ├── layout/               # XML layouts
│       │   ├── drawable/             # Icons & vector assets
│       │   ├── values/               # Colors, strings, themes
│       │   └── xml/                  # FileProvider paths
│       └── AndroidManifest.xml
│
└── README.md
```

---

## 📦 Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |
| Android Studio | Hedgehog (2023.1.1)+ |
| Android SDK | API 34 |
| Google Maps API Key | Required for branch maps |

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/InojBhashitha/TechFix.git
cd TechFix
```

### 2. Start the Backend

```bash
cd backend
mvn spring-boot:run
```

The API will start at `http://localhost:8080` with an in-memory H2 database.
Demo accounts are auto-seeded on first launch.

> **H2 Console:** Available at `http://localhost:8080/h2-console`
> - JDBC URL: `jdbc:h2:mem:techfixdb`
> - Username: `SA`
> - Password: *(empty)*

### 3. Build the Android App

```bash
cd android
./gradlew assembleDebug
```

The APK will be generated at `android/app/build/outputs/apk/debug/app-debug.apk`.

### 4. Configure the Android App

**API Base URL:** Update the base URL in the Android `ApiClient.java` to point to your backend server IP address (e.g., `http://192.168.x.x:8080/`).

**Google Maps API Key:** Add your API key to `android/app/src/main/res/values/strings.xml`:

```xml
<string name="google_maps_key" translatable="false">YOUR_API_KEY_HERE</string>
```

### 5. Install & Run

Transfer the APK to your Android device and install it, or run directly via Android Studio on a connected device / emulator.

---

## 📡 API Documentation

Interactive Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

### Key Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | ❌ | Register new customer |
| `POST` | `/api/auth/login` | ❌ | Login (returns JWT) |
| `GET` | `/api/bookings` | 🔒 | Get user's bookings |
| `POST` | `/api/bookings` | 🔒 | Create new repair booking |
| `GET` | `/api/bookings/{ref}/tracking` | 🔒 | Get repair tracking timeline |
| `POST` | `/api/bookings/{ref}/payment` | 🔒 | Process payment |
| `GET` | `/api/staff/bookings` | 🔒 Staff/Admin | Get staff repair queue |
| `PUT` | `/api/staff/bookings/{id}/status` | 🔒 Staff/Admin | Update repair status |
| `PUT` | `/api/staff/bookings/{id}/assign-technician` | 🔒 Staff/Admin | Assign technician |
| `GET` | `/api/staff/dashboard-stats` | 🔒 Staff/Admin | Dashboard statistics |
| `GET` | `/api/staff/inventory` | 🔒 Staff/Admin | Branch inventory |
| `PUT` | `/api/staff/inventory/{id}/stock` | 🔒 Staff/Admin | Update stock levels |
| `GET` | `/api/branches` | 🔒 | List all branches |
| `GET` | `/api/services` | 🔒 | List repair services |

---

## 🔑 Demo Accounts

The backend auto-seeds 4 demo accounts on startup:

| Role | Email | Password | Notes |
|---|---|---|---|
| 👤 Customer | `customer@techfix.lk` | `password123` | Can book repairs, track, and pay |
| 🛠️ Staff (Colombo) | `staff.colombo@techfix.lk` | `staff123` | Manages Colombo branch repairs |
| 🛠️ Staff (Galle) | `staff.galle@techfix.lk` | `staff123` | Manages Galle branch repairs |
| 👑 Admin | `admin@techfix.lk` | `admin123` | Full system access, all branches |

---

## 🔄 Repair Status Workflow

```
┌─────────────────────┐
│  Request Submitted   │  ← Customer books online
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│   Branch Assigned    │  ← Nearest branch auto-assigned, technician allocated
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│   Device Received    │  ← Customer hands over device at branch
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│ Diagnostic/Inspect.  │  ← Technician inspects hardware faults
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│  Repair in Progress  │  ← Component replacement & soldering
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│   Quality Check (QA) │  ← Testing display, battery & sensors
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│ Ready for Collection │  ← Device cleaned, ready for pickup + Pay Here
└─────────┬───────────┘
          ▼
┌─────────────────────┐
│  Repair Completed    │  ← Device collected, payment settled ✅
└─────────────────────┘

          ╳
┌─────────────────────┐
│     Cancelled        │  ← Can be cancelled at any stage
└─────────────────────┘
```

---

## 🗄️ Database Schema (Entities)

| Entity | Description |
|---|---|
| `User` | Customer, Staff, and Admin accounts |
| `Branch` | Repair branch locations (Colombo, Galle) |
| `DeviceCategory` | Device types (Laptop, Phone, Tablet, etc.) |
| `RepairService` | Available repair services per category |
| `RepairRequest` | Main booking entity with full repair lifecycle |
| `RepairStatusHistory` | Audit log of every status transition |
| `RepairImage` | Photos attached to repair bookings |
| `Technician` | Branch technicians with specialization & availability |
| `Payment` | Payment records (card-based) |
| `SparePart` | Spare parts catalog |
| `BranchInventory` | Per-branch spare part stock levels |

---

## 🔐 Security

- **JWT Authentication** — All API requests (except `/api/auth/*`) require a valid `Bearer` token
- **Role-Based Access Control (RBAC)** — Three roles: `CUSTOMER`, `STAFF`, `ADMIN`
- **Password Encryption** — BCrypt hashing for all stored passwords
- **Branch-Scoped Authorization** — Staff can only manage their assigned branch
- **Admin Elevation** — Admins bypass branch restrictions for global oversight

---

## 📱 Android App Highlights

- **Material Design 3** — Modern, clean UI with custom color system
- **Offline-First with Room** — Local SQLite caching for key data
- **GPS Auto-Branch Selection** — Automatically selects nearest branch during booking
- **Camera Integration** — Capture device photos directly during repair booking
- **Smooth Animations** — Micro-interactions on cards, buttons, and transitions
- **Dark Text Contrast** — Explicit high-contrast styling on all input fields

---

## 🏗️ Build & Run Commands

```bash
# Backend
cd backend
mvn clean test               # Run all 32 unit tests
mvn spring-boot:run          # Start dev server on port 8080

# Android
cd android
./gradlew assembleDebug      # Build debug APK
./gradlew assembleRelease    # Build release APK
./gradlew test               # Run unit tests
```

---

## 👤 Author

**Inoj Bhashitha**
- GitHub: [@InojBhashitha](https://github.com/InojBhashitha)

---

## 📄 License

This project is developed as an academic project for educational purposes.
