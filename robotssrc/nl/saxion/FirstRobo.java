package nl.saxion;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class FirstRobo extends Robot {
    public void onScannedRobot(ScannedRobotEvent event) {
        boolean found = false;
        while (!found){
            turnRight(1);
        }
        if (event.getDistance() < 100){
            fire(10);
        } else {
            fire(0.2);
        }
    }
}