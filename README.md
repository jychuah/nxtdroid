#NXT Droid

Android based controller for Lego Minstorms NXT. 

- Implements Lego Command Protocol over Bluetooth for NXT and EV2 units
- Capable of controlling multiple NXT or EV2 bricks at the same time

###Known Issues

- Doesn't handle Android task switching or sleep gracefully
- Bluetooth should be stateless, but the NXT/EV2 implementation doesn't quite implement that way
- Force quit intentionally generates a Null Pointer Exception to force the application to quit (instead of gracefully handling stateful reset) 

###Maintenance Disclaimer

This project is provided "as-is". I originally wrote it for my Lego Robotics class, but I no longer teach it. Therefore, the code will only be updated if, for some reason, I decide that I want to play around with Lego robots controlled by my phone or tablet again, one day.