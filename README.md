MiniBank — Java Banking Application

MiniBank is a Java-based banking application developed as part of Java programming practicals. It demonstrates core Java concepts through a series of practical implementations, progressing from basic program structure to a complete banking application.

Overview

The project contains 14 practicals covering object-oriented programming, exception handling, multithreading, file handling, collections, generics, reflection, and other Java concepts.

The final practical combines these concepts into a MiniBank application.

Technologies Used

* Java
* Object-Oriented Programming (OOP)
* Java Collections Framework
* Multithreading and Synchronization
* File Handling
* Exception Handling
* Java Reflection and Annotations
* JAR Packaging

Practical List

No.	Practical	Concepts Covered
01	Menu Shell	Basic Java structure and menu-driven programs
02	Customer and Account	Classes, objects, and account management
03	Object Methods and Nested Address	Object methods and nested classes
04	Validation and Command Parsing	Input validation and command parsing
05	Inheritance and Polymorphism	Inheritance and polymorphism
06	Interfaces, Packages and JAR	Interfaces, packages, and JAR creation
07	Annotations and Reflection	Custom annotations and reflection
08	Custom Exceptions	User-defined exception handling
09	Multithreading and Synchronization	Threads and synchronization
10	Transaction Processing and Deadlock	Transaction processing and concurrency
11	File Handling and Reporting	File operations, logs, and reports
12	Collections and Account Store	Collections and account storage
13	Generic Repository	Java generics and reusable repositories
14	Final MiniBank	Integration of banking features and Java concepts

Project Structure

MiniBank/
├── Practical_01_MenuShell/
├── Practical_02_CustomerAndAccount/
├── Practical_03_ObjectMethodsAndNestedAddress/
├── Practical_04_ValidationAndCommandParsing/
├── Practical_05_InheritanceAndPolymorphism/
├── Practical_06_InterfacesPackagesAndJar/
├── Practical_07_AnnotationsAndReflection/
├── Practical_08_CustomExceptions/
├── Practical_09_MultithreadingAndSynchronization/
├── Practical_10_TransactionProcessingAndDeadlock/
├── Practical_11_FileHandlingAndReporting/
├── Practical_12_CollectionsAndAccountStore/
├── Practical_13_GenericRepository/
├── Practical_14_FinalMiniBank/
└── README.md

Requirements

* Java Development Kit (JDK) 17 or later
* Terminal or command-line interface
* A Java-compatible code editor, such as Visual Studio Code or IntelliJ IDEA

Useful resources:

* Java Documentation
* Download the JDK
* Visual Studio Code

Getting Started

1. Clone the Repository

git clone https://github.com/Prince94-p/MiniBank.git

2. Navigate to the Project

cd MiniBank

3. Open a Practical

Navigate to the practical you want to run. Each practical may have its own source files and execution requirements.

For practicals containing a src directory, compile the Java source files from that practical’s directory.

Example:

cd Practical_14_FinalMiniBank
javac -d out $(find src -name "*.java")

Run the application using its main class:

java -cp out MiniBank

The main class name and run command may differ depending on the practical’s implementation and package structure.

Final MiniBank Application

Practical 14 contains the final MiniBank implementation, along with supporting test files and a JAR artifact.

If the JAR is executable, run it using:

java -jar MiniBank.jar

Learning Objectives

This project provides practical experience with:

* Writing and organizing Java programs
* Applying object-oriented programming principles
* Managing exceptions and validating input
* Working with threads and synchronized operations
* Reading and writing files
* Using collections and generics
* Organizing code with packages and interfaces
* Building and packaging Java applications

Author

Prince Patel

* GitHub: Prince94-p
* Repository: MiniBank

License

This project is intended for educational purposes.
