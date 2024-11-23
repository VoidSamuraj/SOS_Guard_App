
# SOS Application - Security Guard

<p>
<img src="https://img.shields.io/badge/Kotlin-purple" alt="Kotlin 2.0.10"> 
<img src="https://img.shields.io/badge/Jetpack_Compose-1.4.3-purple?color=5C2D91" alt="Jetpack Compose 1.4.3"> 
<img src="https://img.shields.io/badge/minSdk_33-%233DDC84" alt="minSdk 33"> 
<img src="https://img.shields.io/badge/targetSdk_34-%23008B02" alt="targetSdk 34 "> 
<img src="https://img.shields.io/badge/Mapbox-API-0073e6" alt="Mapbox API"> 
<img src="https://img.shields.io/badge/HTTPS-Secure-green?color=008B02" alt="HTTPS Secure"> 
<img src="https://img.shields.io/badge/WebSocket-WSS-blue?color=1E90FF" alt="WebSocket WSS">
<img src="https://img.shields.io/badge/JWT-Secure-blue?color=008B8B" alt="JWT Secure">
<img src="https://img.shields.io/badge/Retrofit-2.9.0-orange?color=FF4500" alt="Retrofit 2.9.0"> 
<img src="https://img.shields.io/badge/ViewModel-Android-green?color=3DDC84" alt="ViewModel Android"> 
<img src="https://img.shields.io/badge/Location_Service-Background-%23008B02" alt="Location Service Background"> 
<img src="https://img.shields.io/badge/Notifications-Android-purple?color=5C2D91" alt="Notifications Android"> 
</p>

## Purpose:
The SOS application is designed to support the operations of a security company by automating and speeding up processes related to handling emergency requests. It ensures that users get quick and effective help in critical situations.
It works with <a href="https://github.com/VoidSamuraj/SOS_Server" target="_blank">SOS Server</a> and <a href="https://github.com/VoidSamuraj/SOS_Client_App" target="_blank">SOS Client</a>

![guard](https://github.com/user-attachments/assets/15d883a8-3093-49de-b5b4-cecd94102f1f)
  
## Key Features:
- **Real-Time Location Updates:** The app continuously tracks the security guard’s position and provides updates at short intervals.
- **Status Update:** Guards can change their availability status (Available/Unavailable).
- **Task Assignment:** Upon receiving an emergency request, the guard can confirm it and start intervention, in the system  the status will change to "Intervention" for the guard guard. If guard will not respond (confirm or deny request), his status will change to "not responding"
- **Client Navigation:** The app provides the client’s position and allows the guard to use navigation to reach the client after confirming the intervention.

## Technologies:
- **Kotlin**: A statically typed programming language compatible with Java, ideal for cross-platform development.
- **Jetpack Compose**: A framework for building modern UIs for Android apps in a declarative way.
- **WebSocket**: For real-time communication with the server.
- **Mapbox API**: For navigation and geolocation features.
- **Retrofit**: For simple server communication.

## How it Works:
1. **Receiving SOS Alerts**: Upon receiving an alert, the guard's mobile app updates the system about their status.
2. **Intervention Confirmation**: After confirming the intervention, the guard navigates to the client’s location, assisting as needed.
3. **Support Requests**: If needed, guards can request additional backup or resources.

## Requirements
Before running the project, make sure to add the following API key to your `secret.properties` file:
```
MAPBOX_DOWNLOADS_TOKEN="YOUR_API_KEY"
```
and set address on which you host server in `MainActivity` file:
```
const val address="10.0.2.2:8443" //default local intelij adress(make sure it uses the same port as the server)
```
You probably also wanna to enable keystore cert checking (temporaty turned off) in `NetworkClient`

## Getting Started:
To start using the application, simply install the app and log in with your credentials. Make sure you have location services enabled for accurate tracking and navigation.
