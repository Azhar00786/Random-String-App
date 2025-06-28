# Random-String-App
![project_image](https://github.com/user-attachments/assets/fb6759d9-a6d1-4641-ada7-3a79870adce8)

This Android app demonstrates how to query a random string from an existing Android Content Provider, using MVVM architecture and Jetpack Compose for modern, declarative UI development.

# The app implements the following features:
	• Users can set the desired length of the random string to be generated.
	• Generation of Random String, On button click, the app queries the content provider to generate a random string with the specified length.
	• Every generated string is displayed along with: The string value, The specified length, The date and time when it was created	
	• Older strings remain visible when new strings are generated.
	• Users can delete all generated strings from the app.
	• Users can delete a specific generated string from the list.
	• The app includes proper error handling to manage edge cases and content provider errors gracefully.
  
# Architecture
	• MVVM (Model-View-ViewModel) pattern for separation of concerns and testability.
	• Jetpack Compose for building modern, reactive UI components.
