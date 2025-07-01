# DisasterManagementGUI

A modern, user-friendly Java GUI application for disaster response management. This tool helps organizations manage victims, supplies, inquiries, and locations efficiently—now supporting both **English** and **French**!

## 🌐 Language Selection
When you launch the program, you'll be greeted with a language selection screen:

![Language Selection](images/en(fr).png)

Choose between English and French to use the app in your preferred language.

## 🖥️ Main GUI Overview
After selecting your language, you'll see the main dashboard, where you can manage all aspects of disaster response:

![Main GUI](images/DisasterManagementGUI.png)

- **Victim Management**: Add, update, view, or delete disaster victims.
- **Supply Management**: Track and manage supplies.
- **Inquiry Management**: Handle external and victim inquiries.
- **Location Management**: Organize and update locations.

## ➕ Add Victim Menu
Easily add new victims with detailed information:

![Add Victim](images/AddVictim.png)

## 🚀 Getting Started

### Prerequisites
- Java 8 or newer
- (Optional) PostgreSQL database for full functionality

### 1. Download Dependencies
All required JARs (PostgreSQL JDBC, JUnit, Hamcrest) are included in the `lib/` folder.

### 2. Compile the Project
```sh
javac -cp "lib/*" -d out src/edu/ucalgary/oop/*.java
```

### 3. Run the Application
```sh
java -cp "out;lib/*" edu.ucalgary.oop.Main
```

### 4. Run the Tests
```sh
javac -cp "lib/*;out" -d out test/edu/ucalgary/oop/*.java
java -cp "out;lib/*" org.junit.runner.JUnitCore edu.ucalgary.oop.AllTests
```

## 🗄️ Database Connectivity
The application can connect to a PostgreSQL database for persistent data storage. By default, it attempts to connect to:
- `jdbc:postgresql://localhost:5432/ensf380project`
- User: `oop`

You can modify these settings in the source code to match your environment. If the database is unavailable, the app may still run in a limited mode.

## 💡 Try It Yourself!
1. Clone this repository.
2. Make sure you have Java installed.
3. Compile and run using the steps above.
4. Explore the GUI, try adding victims, and see how easy disaster management can be!

---

Feel free to contribute or open issues for suggestions and improvements!
