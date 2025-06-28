# Random-String-App
![project_image](https://github.com/user-attachments/assets/fb6759d9-a6d1-4641-ada7-3a79870adce8)

This Android app demonstrates how to query a random string from an existing Android Content Provider, using MVVM architecture and Jetpack Compose for modern, declarative UI development.

# Functional Requirements

The app implements the following features:
	1.	Custom String Length
	  •	Users can set the desired length of the random string to be generated.
	2.	Generate Random String
	  •	On button click, the app queries the content provider to generate a random string with the specified length.
	3.	Display Generated Strings
	  •	Every generated string is displayed along with:
	  •	The string value
	  •	The specified length
	  •	The date and time when it was created
	4.	Persist Generated Strings
	  •	Older strings remain visible when new strings are generated.
	5.	Delete All Strings
	  •	Users can delete all generated strings from the app.
	6.	Delete Single String
	  •	Users can delete a specific generated string from the list.
	7.	Error Handling
	  •	The app includes proper error handling to manage edge cases and content provider errors gracefully.
  
# Architecture
	•	MVVM (Model-View-ViewModel) pattern for separation of concerns and testability.
	•	Jetpack Compose for building modern, reactive UI components.
