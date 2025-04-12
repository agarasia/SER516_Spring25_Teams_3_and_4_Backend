# SER516_Spring25_Teams_3_and_4_Backend

# User Guide - Software Quality Metrics Backend Services(APIs)

# Project Overview

**Project Name:** **Software Quality Metrics**

This guide provides instructions on how to run the application backend services using Docker.

## 1. Project Description:
Software Quality Metrics Calculator. This project is designed to analyze software quality by computing essential metrics (in this case, Afferent coupling, Efferent coupling, Defect density, LCOM4, LCOMHS and Defect score) for any GitHub Public Repository that has Java code. This tool can help user to maintain software quality for projects at all scale.
* **Afferent Coupling (Ca):** Measures how many other classes depend on a given class.
* **Efferent Coupling (Ce):** Measures how many external classes a given class depends on.
* **Instability Metric(I):** Measures how susceptible a software package is, to changes based on its outgoing dependencies. It is calculated using both Ce and Ca.
* **Defect Density:** Calculates the number of defects per lines of code to assess software reliability.
* **Lack of Cohesion of Methods - version 4 (LCOM4):** Measures how well the methods of a class are related to each other via shared instance variables.
* **Henderson-Sellers Cohesion Metric (LCOMHS):** Evaluates class cohesion with a formula involving the number of methods and attributes, and how often attributes are used across methods.
* **Defect score:** A weighted average summaries of defect severity.

* This project enables developers to upload github link containing Java code, analyze quality metrics, and track file history. It also supports benchmark values for quality evaluation.

## 2. System Requirements:

### 2.1 Prerequisites

Before setting up the project, ensure you have the following installed:
* **Java SDK 21**
* **Python 3.11**
* **Maven 3.8+**
* **spring-boot 3.2+**
* **Docker & Docker Compose**
* **Git**

## 3. Project Setup and Execution

### 3.1 Cloning the Repository
   ```bash
    git clone https://github.com/agarasia/SER516_Spring25_Teams_3_and_4_Backend.git
```

### 3.2 Building and Running the Application Backend Services

Step-1. **Navigate to the Project root Directory:**
   ```bash
   cd SER516_Spring25_Teams_3_and_4_Backend
   ```
Step-2. **Building and Running the Application Backend services:**
   ```bash
   docker-compose up --build
   ```

Step-6 **Uploading Input Value**

- The expected input is a github URL containing Java source code for analysis.
- You can use sample test files located in the TESTDATA.md for testing.

## 4. Application Features
### 4.1 Quality Metrics Computation

The system calculates:

* Afferent Coupling (Ca): Measures the number of incoming dependencies. 
* Efferent Coupling (Ce): Measures the number of outgoing dependencies.
* Defect Density: Number of defects per lines of code.
* Instability Metric(I): Measures how susceptible a software package is, to changes based on its outgoing dependencies.
* LCOM4 :  Measures methods of a class are related to each other via shared instance variables.
* LCOMHS : Measures the number of methods and attributes, how often attributes are used across methods.
* Defect score : A weighted average summaries of defect severity.
* Visualization of the metrics: Line graph for Afferent, Efferent, Instability, LCOM4, LCOMHS and Defect score metrics.
* User customised Benchmark value for each metric allowed and graph shows the metrics against this idealized baseline(benchmark) over time.

## 5. Development Workflow

### 5.1 Branching Strategy

- All development is done in **task branches** and **userstory branches**
- Completed stories are merged into **Period branches** (`Period-YY`).
- Before the period ends, `Period-YY` is merged into `main`.
- Releases are tagged as **`Period-YY-release-v"majorversionnumber"."minorversionnumber"`**.

### 5.2 Code Quality

- **Unit Testing:** JUnit is used for test coverage in Afferent, Efferent and Defect Dendity APIs. pyTest used for test coverage in DefectScore, LCOM4 and LCOMHS python services.
- **CI/CD:** GitHub Actions runs automated build and tests on each commit.
- **Code Style:** Follows standard Java and Python coding conventions.

## 5. Contributors

This project is a collaborative effort by the SER 516 Teams-3&4. If you have any questions or need assistance, please reach out to one of the team members:
- Thrupthi Hosahalli Manjunatha (thosahal@asu.edu)
- Smit Panchal (spanch19@asu.edu)
- Hitesh Kolluru (hkolluru@asu.edu)
- Aniket Patil (@aapati17)
- Aum Jitendra Garasia (@agarasia)
- Satyam Shekhar (@sshekh30)
- Uma Maheshwar Reddy N (@unallami)
- Faisal Alaqal (falaqal@asu.edu)
