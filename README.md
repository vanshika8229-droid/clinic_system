# 🏥 Clinic Appointment System

---

## Project Overview

A desktop clinic appointment management system built with **Java Swing**, using a layered **OOP architecture** (Model → DAO → Service → UI).  
No external dependencies. Pure Java. Works on any machine with JDK 11+.

---

## Architecture (All 5 Phases)

```
Phase 0 — Proposal & Backlog
Phase 1 — Requirements & Use Cases  
Phase 2 — UML & Package Design      →  clinic.model / clinic.dao / clinic.service / clinic.ui
Phase 3 — Database & DAO Layer       →  DataStore + DoctorDao / PatientDao / AppointmentDao / ...
Phase 4 — UI & Integration           →  MainFrame + all Panel classes + Theme.java
Phase 5 — Testing, Build, Demo       →  Conflict checking, validation, build scripts
```

---

## Package Structure

```
src/
├── Main.java
└── clinic/
    ├── model/
    │   ├── Doctor.java
    │   ├── Patient.java
    │   ├── Appointment.java
    │   ├── Schedule.java
    │   └── VisitRecord.java
    ├── dao/
    │   ├── DataStore.java        ← central file-backed persistence
    │   ├── DoctorDao.java
    │   ├── PatientDao.java
    │   ├── ScheduleDao.java
    │   ├── AppointmentDao.java
    │   └── VisitRecordDao.java
    ├── service/
    │   ├── AppointmentService.java   ← conflict-free scheduling logic
    │   ├── DoctorService.java
    │   └── PatientService.java
    └── ui/
        ├── Theme.java            ← design tokens
        ├── MainFrame.java        ← main window + sidebar nav
        ├── DashboardPanel.java
        ├── DoctorPanel.java
        ├── PatientPanel.java
        ├── SchedulePanel.java
        ├── AppointmentPanel.java
        └── HistoryPanel.java
```

---

## How to Compile & Run

### Prerequisites
- JDK 11 or later installed
- Terminal / Command Prompt

### Steps

```bash
# 1. Go into the project folder
cd ClinicSystem

# 2. Compile (Linux/macOS)
chmod +x compile.sh
./compile.sh

# 2. Compile (Windows)
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt

# 3. Run
java -cp out Main
```

---

## Core Features (Phase 4)

| Feature | Description |
|---|---|
| **Dashboard** | Live stats: total doctors, patients, today's appointments |
| **Doctor Management** | Add, view, soft-delete doctors |
| **Patient Registration** | Register, view, delete patients with validation |
| **Schedule Management** | Define doctor availability by day/time/slot duration |
| **Appointment Booking** | Load available slots, book with conflict detection |
| **Cancel / Reschedule** | Cancel or reschedule with history tracking |
| **History View** | Filter appointments by doctor or patient |

---

## Key Business Rules (Phase 5 — Conflict Checking)

1. **No double-booking** — same doctor + date + time slot → rejected
2. **Schedule validation** — booking only allowed within defined doctor schedule
3. **Day validation** — doctor must have schedule for that day of the week  
4. **Past date block** — cannot book appointments in the past
5. **Field validation** — name, phone (10 digits), age all validated before save

---

## Demo Flow (Phase 5)

1. Open app → Dashboard shows seeded data (3 doctors, 2 patients, 4 schedules)
2. **Add Doctor** → Go to Doctors → Fill form → Add Doctor
3. **Add Schedule** → Go to Schedules → Pick doctor + day + time range
4. **Register Patient** → Go to Patients → Fill and Register
5. **Book Appointment** → Appointments → Select patient + doctor + date → Load Slots → Book Now
6. **Test Conflict** → Try booking the same slot again → See rejection message
7. **History** → View by doctor or patient filter

---

## Data Persistence

Data is saved to `clinic_data.dat` in the working directory using Java serialization.  
The file is auto-created on first run with seed data (3 doctors + schedules + 2 patients).

---

## Why This Project Fits Unit 06

- Demonstrates **OOP** with encapsulated model classes, inheritance-ready structure
- **Layered architecture**: model → DAO → service → UI (no SQL in UI handlers)
- **Java Swing** for full desktop GUI with event-driven programming
- **Conflict-free scheduling** is the core algorithmic challenge
- Validation at service layer — not just UI popups
