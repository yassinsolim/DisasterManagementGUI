# DisasterManagementGUI

A modern, user-friendly Java GUI application for disaster response management. This tool helps organizations manage victims, supplies, inquiries, and locations efficiently, now supporting both **English** and **French**!

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
The application can connect to a PostgreSQL database for persistent data storage. Connection settings come from environment variables, so no credentials live in the source tree.

| Variable | Required | Default |
| --- | --- | --- |
| `DB_URL` | no | `jdbc:postgresql://localhost:5432/ensf380project` |
| `DB_USER` | no | `oop` |
| `DB_PASSWORD` | yes | none |

`DB_PASSWORD` has no default. Startup fails with an explicit message when it is unset. Set it to an empty value if your server uses trust authentication:

```sh
export DB_URL="jdbc:postgresql://localhost:5432/ensf380project"
export DB_USER="oop"
export DB_PASSWORD="your-password"
java -cp "out;lib/*" edu.ucalgary.oop.Main
```

If the database is unavailable, `DisasterManagementGUI` reports the connection error in a dialog and exits.

## 💡 Try It Yourself!
1. Clone this repository.
2. Make sure you have Java installed.
3. Compile and run using the steps above.
4. Explore the GUI, try adding victims, and see how easy disaster management can be!

---

Feel free to contribute or open issues for suggestions and improvements!
