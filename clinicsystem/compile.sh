#!/bin/bash
# ──────────────────────────────────────────────────────
# Compile & Run — Clinic Appointment System
# ──────────────────────────────────────────────────────
echo "Compiling Clinic Appointment System..."

mkdir -p out

# Compile all .java files
find src -name "*.java" | xargs javac -d out

if [ $? -eq 0 ]; then
    echo "✅  Compilation successful!"
    echo ""
    echo "Run with:  java -cp out Main"
    echo ""
    # Optionally auto-run
    # java -cp out Main
else
    echo "❌  Compilation failed."
fi
