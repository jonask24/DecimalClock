# Decimal Clock

A modern Android application that provides decimal time representations alongside standard time formats. This app helps users understand and convert between traditional 24-hour time and decimal time measurements.

## Device Requirements

### Operating System
- Minimum: Android 5.0 (API level 21)
- Target: Android 14 (API level 34)

### Storage
- Application size: ~10MB
- Additional storage for saved preferences

### Permissions Required
- `FOREGROUND_SERVICE` - For stopwatch background operation

### Hardware Features
- Standard Android device
- No special hardware requirements

### Optional Features
- Background service support
- Notification support

## Features

- **Real-time Clock Display**
  - Standard time (HH:MM:SS)
  - Decimal time conversion
  - Live updates

- **Calendar Converter**
  - Date and time picker
  - Converts selected dates to decimal format
  - Persistent storage of selected dates
  - Day of year calculations

- **Decimal Stopwatch**
  - Standard and decimal time display
  - Start, pause, and reset functionality
  - Add minute/hour shortcuts
  - Runs in background with notification
  - Precise timing calculations

## Technical Details

### Development Environment
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Gradle 7.0 or higher

### Key Components
- `TimeConverter`: Utility class for time format conversions
- `PreferenceManager`: Handles persistent storage
- `StopwatchService`: Background service for stopwatch functionality
- Separate ViewModels for Clock, Calendar, and Stopwatch features

## Installation

1. Clone the repository