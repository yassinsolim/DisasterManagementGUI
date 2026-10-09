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
- PostgreSQL database (required when starting the GUI)

### 1. Download Dependencies
JARs are ignored by Git, so a fresh clone has no `lib/`. Create that folder
and download these three files into it:

- [PostgreSQL JDBC 42.7.3](https://repo.maven.apache.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar)
- [JUnit 4.13.2](https://repo.maven.apache.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar)
- [Hamcrest Core 1.3](https://repo.maven.apache.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar)

The classpath separator below is `;` on Windows. Use `:` on Linux or macOS.

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

`AllTests` uses test-mode adapters and does not connect to PostgreSQL. To test connection reuse, first set `DB_URL`, `DB_USER`, and `DB_PASSWORD` to a disposable local PostgreSQL database with scratch credentials. Then run this separate integration suite (replace `;` with `:` on Linux or macOS):

```sh
java -cp "out;lib/*" org.junit.runner.JUnitCore edu.ucalgary.oop.DatabaseAdapterLifecycleIntegrationTest
```

Run `DatabaseAdapterLoggingIntegrationTest` separately against the same disposable database, from an empty scratch directory with no `error.log`. Use absolute paths to the compiled `out` and `lib` directories, since this test creates and removes its own log file:

```sh
java -cp "<repo>/out;<repo>/lib/*" org.junit.runner.JUnitCore edu.ucalgary.oop.DatabaseAdapterLoggingIntegrationTest
```

## 🗄️ Database Connectivity
The application uses PostgreSQL. Runtime credentials come from environment variables; they must not be committed to this repository.

| Variable | Required | Default |
| --- | --- | --- |
| `DB_URL` | no | `jdbc:postgresql://localhost:5432/ensf380project` |
| `DB_USER` | no | `oop` |
| `DB_PASSWORD` | yes | none |

`DB_USER` and `DB_PASSWORD` supply the connection credentials. Do not put credentials in `DB_URL`: URL userinfo and `user`/`password` query options are rejected because PostgreSQL JDBC would give URL options precedence. Other URL options remain available. For a remote server, configure certificate-validated TLS (for example, `?sslmode=verify-full` with a trusted CA); the defaults do not guarantee it.

`DB_PASSWORD` has no default. When it is unset, `DatabaseAdapter` raises an explicit, credential-free error; the GUI shows a generic database dialog. Set the variable to an empty value only if your server intentionally uses trust authentication:

Windows PowerShell:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/ensf380project"
$env:DB_USER = "oop"
$env:DB_PASSWORD = "your-password"
java -cp "out;lib/*" edu.ucalgary.oop.Main
```

Linux or macOS shell:

```sh
export DB_URL="jdbc:postgresql://localhost:5432/ensf380project"
export DB_USER="oop"
export DB_PASSWORD="your-password"
java -cp "out:lib/*" edu.ucalgary.oop.Main
```

If the database is unavailable, the GUI shows a generic connection error and exits. Raw JDBC errors can contain the connection URL, so they are not displayed or logged during connection setup. A `.env` file is ignored by Git but is not loaded automatically; set the variables in the process environment.

New `DatabaseAdapter` SQL-error entries in `error.log` contain a fixed operation label and, when available, a validated SQLSTATE or numeric vendor code. They omit raw JDBC exception text. CLI and GUI error paths show a generic message when the exception or its cause is a `SQLException`; non-SQL validation messages remain unchanged. Logs created by older versions, including `data/errorlog.txt`, may still contain application data; restrict access to them and do not publish them.

Database credentials previously committed to this repository remain in public history. The database owner must revoke or rotate them and audit any exposure before using this application. Removing credentials from current source or adding ignore rules does not revoke or erase them.

## 💡 Try It Yourself!
1. Clone this repository.
2. Make sure you have Java installed.
3. Compile and run using the steps above.
4. Explore the GUI, try adding victims, and see how easy disaster management can be!

---

Feel free to contribute or open issues for suggestions and improvements!
