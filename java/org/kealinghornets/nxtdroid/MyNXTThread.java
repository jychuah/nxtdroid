package org.kealinghornets.nxtdroid;

import org.kealinghornets.nxtdroid.NXT.NXTThread;
import org.kealinghornets.nxtdroid.NXT.UltrasonicSensor;

/**
 * Created by jychuah on 11/3/13.
 */
public class MyNXTThread extends NXTThread {

    public MyNXTThread() {
        setNXTThreadName("Ultrasonic Sensor Test");
    }

    public void run() {
		
      UltrasonicSensor us = getUltrasonicSensor(PORT_1);
      
      int lastReading = -1;
      int currentReading = -1;
      long lastTime = System.currentTimeMillis();
      int totalTime = 0;
      
      while (totalTime < 30000) {
        long thisTime = System.currentTimeMillis();
        long timeElapsed = thisTime - lastTime;
        totalTime = totalTime + (int)timeElapsed;
        
        currentReading = us.getDistance();
        // distance change divided by time change
        double speed = 0;
        if (timeElapsed > 0) {
        		speed = ((float)(currentReading - lastReading)) / (int)timeElapsed;
        }
        log("***");
        log("Current distance: " + currentReading);        
        log("Current speed: " + speed);

        
        
        if (speed < 0) {
          log("Speed is negative");
          if (currentReading < 50) {
         	 runMotor(PORT_A, 100);
          	runMotor(PORT_B, -100);
          
        	}          
        }
        
        if (speed > 0) {
          log("Speed is positive");
          
          
        }
        
        
        lastReading = currentReading;
        lastTime = thisTime;
        wait(100);
      }
      reset();
    }
}
