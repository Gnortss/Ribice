# Ribice

## Project setup for IntelliJ IDEA

Clone project:

 - New Project from **Git Version Control**
 - Copy this ```https://github.com/Gnortss/Ribice.git``` to URL
 - **Test** connection and then **Clone**
 
Add libraries:

 - File > Project Structure > Libraries
 - Click **+** to add New Project Library
 - Select a path to ```./Ribice/libs/jars``` -> should look similar to ```/home/<user>/IdeaProjects/Ribice/libs/jars```
 
Edit configuration for ```MainLoop.java```:

 - Run ```src/engineTester/MainLoop``` -> should throw an error
 - On the top right click on ```MainLoop > Edit Configurations...```
 - Select ```Applications/MainLoop```
 - Paste ```-Djava.library.path=libs/natives/linux>``` to ```VM Options```
 - **Note:** change ```/linux``` to ```/windows``` if on windows
 
 You should now be able to run ```MainLoop```

## NOTE: Before doing any changes!!

 - Go under ```VCS > Git > Pull..``` and click ```Pull``` to update your local project
 