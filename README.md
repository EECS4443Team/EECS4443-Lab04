# EECS4443 Lab 04
This repository is for EECS 4443 W2026 Lab 04
## Team Members
| Full Name | Section (Lab) | Student ID | Email |
|----------|----------|----------|----------|
| **Jorel Louie Chim**   | M (Tuesday Lab)   | 217207879   | jorelc@my.yorku.ca   |
| **Chan Woo Hwang**  | M (Tuesday Lab)   | 218972539   | htry02@my.yorku.ca   |
| **Shivraj Banwait**   | M (Tuesday Lab)   | 217279373   | shivrajb@my.yorku.ca   |
| **Asif Javed**   | M (Friday Lab)   | 219913433  | Asif2004@my.yorku.ca   |
## Team Contributions
| Team Member        | Contributions | 
|--------------------|------------------|
| **Jorel Louie Chim** | Data Model, Contact Row UI, Add Contact UI, Edit Contact UI  |
| **Shivraj Banwait** | Edit Contact UI, Data Model, Data Storage (SQLite) |
| **Chan Woo Hwang** | Main Activity UI, Gesture Handling, Data Storage (SharedPreferences), MVVM Refactoring, Backend | 
| **Asif Javed** | Error Handling, Comments, Validation | 

## Architecture
This app follows the MVVM (Model-View-ViewModel) architectural pattern, leveraging Android Architecture Components.

*   **Model**: The `Contact` class defines the data structure of the application.
*   **View**: `Fragment`s (e.g., `ContactFragment`, `ContactDetailsFragment`) compose the UI and handle user input. They observe `LiveData` from the ViewModel to automatically update the UI upon data changes.
*   **ViewModel**: `ContactViewModel` manages UI-related data and business logic. It's lifecycle-aware, preserving data across configuration changes like screen rotations.
*   **Repository**: `ContactRepository` abstracts the data source, allowing the ViewModel to request data operations without direct knowledge of the underlying database (`ContactDBHelper`).

This structure enhances testability and maintainability by clearly separating concerns.
