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
| **Jorel Louie Chim** | Implement MainActivity logic, Activity Result API registries, the onSaveInstanceState lifecycle handling  |
| **Shivraj Banwait** | ConstraintLayout XML Design (ImageView, Buttons, and theme customization), createImageFile logic |
| **Chan Woo Hwang** | FileProvider setup implementation, createImageFile logic, AndroidManifest provider configurations| 
| **Asif Javed** | Runtime permission requests handling, feedback (Toasts), error handling for null/canceled results | 

## Architecture
This app follows the MVVM (Model-View-ViewModel) architectural pattern, leveraging Android Architecture Components.

*   **Model**: The data layer is represented by the FileProvider and the createImageFile() logic. It manages the creation of temporary files and secure URIs for media storage, ensuring that the image data is accessible to external camera apps while maintaining scoped storage security.
*   **Controller**: MainActivity.java acts as the controller. It manages the Activity Result API launchers to handle external intents, processes runtime permission requests only when a button is clicked, and coordinates the flow between user actions and data updates.
*   **View**: The user interface is defined in activity_main.xml using a ConstraintLayout. It contains the ImageView for profile display, "Take Photo" and "Select from Gallery" buttons, and a TextView for status feedback.


This structure enhances testability and maintainability by clearly separating concerns.
