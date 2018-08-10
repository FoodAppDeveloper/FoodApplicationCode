# README
This version of the code was tested with a Samsung Galaxy s7 with API level 24 and built with Android Studio 3.1.3.
## Changes
* Updated Backendless version by adding 'implementation 'com.backendless:backendless:5.0.+' to build.gradle
   * Full code differences shown in [Backendless documentation](https://backendless.com/docs/android/doc.html#dynanchor1)
* Open camera to take image and save to Backendless database
* Method to add comments to image and saves separate text file to Backendless database
* Query/Delete images and text files from Backendless database
* Displays multiple images with option to take another image for the same meal
* File system where image file has the same name as text file but with different extention and prefix
* Plate image indicates whether or not the meal has images associated with it already
* Implemented bottom bar functionality for "Today" and "Settings"
   * Settings current has logout option
   * Today moves page to current day or switches view from Settings to the current day
* Timestamp of image is the time the image was taken

New files:
* ImageText.java
* SettingsFragment.java
* EachMealSectionAdapter.java
* EachMealFragment.java
* captured_meal_popup.xml
* description_popup.xml
* imagetext_display.xml
* settings.xml
* bottombar.xml
