# README

This is a rebuild of an IBM tool to validate connectivity to a database.

DBConnTestV2 is a small Java utility to verify that you can connect to a database outside of your application. It prints every parameter you supply (including passwords) so you can confirm exactly what’s being used, and helps you track down connectivity issues.

If it finds an existing properties file, it loads those settings. If no file is found (or the file is invalid), it enters interactive mode, prompts you for all required database connection details, and then creates a new properties file for future use.

Successful connections will display the database vendor and version, and the current date as retreived from the database.

---

## Usage

```bash
java -jar DBConnTestV2.jar [path/to/database.properties]
```

- **With no argument**: looks for `database.properties` in the current directory.  
- **With a file path**: loads settings from the file you specify.  
- **If the file is missing or unreadable**: prompts you interactively and then saves a new `database.properties`.

---

### Using an existing properties file

```powershell
cd C:\tools\DBConnTest\
java -jar DBConnTestV2.jar C:\coe\sandbox2.properties
```

![Loaded existing properties and ran test query](https://github.com/EricJCross/DBConnTest/blob/1872a64c6ae4b99e7589e1e2dca371f218433b4a/DBConnTest%202/documentation/successful.jpg)

---

### No properties file (Interactive mode)

```powershell
cd C:\tools\DBConnTest\
java -jar DBConnTestV2.jar
```

When no file is found, you’ll see:

![Prompt when properties file is missing](https://github.com/EricJCross/DBConnTest/blob/1872a64c6ae4b99e7589e1e2dca371f218433b4a/DBConnTest%202/documentation/missing_properties.jpg)

You enter your connection details:

![Interactive data entry](https://github.com/EricJCross/DBConnTest/blob/1872a64c6ae4b99e7589e1e2dca371f218433b4a/DBConnTest%202/documentation/interactive.jpg)

Afterwards, a new `database.properties` is created:

![New properties file created](https://github.com/EricJCross/DBConnTest/blob/1872a64c6ae4b99e7589e1e2dca371f218433b4a/DBConnTest%202/documentation/New_Properties_File.jpg)

---

### Help

```bash
java -jar DBConnTestV2.jar --help
```

![Help screen showing usage information](https://github.com/EricJCross/DBConnTest/blob/1872a64c6ae4b99e7589e1e2dca371f218433b4a/DBConnTest%202/documentation/help.jpg)

---

## Developer

- **Author:** Eric Cross  
- **Original Source:** [afuruk/DBConnTest](https://github.com/afuruk/DBConnTest)  
- **Rebuilt In:** Java 8, following Pascal_Snake_Case conventions  

Feel free to fork or submit issues on [GitHub](https://github.com/EricJCross/DBConnTest).
```

