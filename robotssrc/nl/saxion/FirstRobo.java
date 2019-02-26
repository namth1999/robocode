package nl.saxion;

import robocode.Robot;
import robocode.ScannedRobotEvent;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class FirstRobo extends Robot {

    public void run(){
        goCorner(180);
        turnLeft(180);
        ahead(5000);
        while (true){
            turnRight(90);
            ahead(5000);
        }

    }

    public void onScannedRobot(ScannedRobotEvent event) {
        if (event.getDistance() < 100){
            fire(10);
        } else {
            fire(0.2);
        }
    }

    public void goCorner(int corner) {
        turnRight(normalRelativeAngleDegrees(corner - getHeading()));
        // Move to that wall
        ahead(5000);
        // Turn to face the corner
        turnLeft(90);
        // Move to the corner
        ahead(5000);
    }
}