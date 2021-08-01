## Eagle Cleaners Delivery App

#### Background

Eagle Cleaners is a dry cleaners in Baton Rouge, LA. 
They wanted to switch their business to an online-centered business model, in order to improve convenience for their customers and allow their employees to stay safe during the pandemic. 
This app helps them achieve this goal by allowing customers to get their laundered clothes delivered.

#### Features

The app has two main screens - one for employees, and one for customers.
The customer screen has an interactive map and lets a customer request a delivery to a specific address.
The employee screen lets employees view all these deliveries, and remove them once the deliveries are complete.
There is also a login portal to allow employees to access the employee screen.

#### Implementation

The delivery service is implemented using several Firebase services, the Google Maps Api, and several open source libraries. 
To get the user's address, the app uses SeatGeek's PlacesAutocompleteTextView. This address is then displayed on the map.
Once the user's address has been obtained, the user can send a delivery request.
This sends an http request using Retrofit, which hits an endpoint for one of the cloud functions. 
This function processes the request data, adds it to the firestore database, and notifies any employees using Firebase Cloud Messaging.
Employees can then login via the admin portal (which uses Firebase Auth) and view all the requests. 
Once they complete the delivery, they can swipe to dismiss the request.
