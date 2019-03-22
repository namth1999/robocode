package nl.saxion;

import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;

import java.awt.*;
import java.util.ArrayList;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class TeamDroid extends TeamRobot implements Droid {

    private ArrayList<Vector> waypoints = new ArrayList<>();
    private boolean stopWhenSeeRobot = false;

    public void run() {

        setBodyColor(Color.yellow);
        setGunColor(Color.red);
        setRadarColor(Color.white);
        setScanColor(Color.blue);
        setBulletColor(Color.yellow);


        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();
        double w = getWidth();
        double h = getHeight();

        Vector boundary = new Vector(w, h).scale(1.0);

        waypoints.add(new Vector(0 + boundary.x, 0 + boundary.y));
        waypoints.add(new Vector(width - boundary.x, 0 + boundary.y));
        waypoints.add(new Vector(0 + boundary.x, height - boundary.y));
        waypoints.add(new Vector(width - boundary.x, height - boundary.y));

        int waypointIndex = 0;
        double minDistance = Double.MAX_VALUE;

        int gunIncrement = 3;

        //find the closest
        Vector currentPos = new Vector(getX(), getY());
        for (int i = 0; i < waypoints.size(); i++) {
            double distance = Vector.distance(waypoints.get(i), currentPos);
            if (distance < minDistance) {
                minDistance = distance;
                waypointIndex = i;
            }
        }


        while (true) {

            for (int i = 0; i < 30; i++) {
                turnGunLeft(gunIncrement);
            }
            gunIncrement *= -1;

            double currentAngle = getHeading();
            currentPos = new Vector(getX(), getY());
            //     double targetAngle = delta.angleInDegrees();


            turnRight(normalRelativeAngleDegrees(0 - currentAngle));
            ahead(5000);
            while (true) {
                turnLeft(90);
                ahead(5000);
                waypointIndex++;
                waypointIndex %= waypoints.size();
                execute();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof Vector) {
            Vector v = (Vector) event.getMessage();
            // Calculate x and y to target
            Vector currentPos = new Vector(getX(), getY());
            Vector delta = v.sub(currentPos);
            // Calculate angle to target
            double theta = v.angleInDegrees();
            // Turn gun to target
            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
            if (stopWhenSeeRobot) {
                stop();
                attack(delta.length());
            } else {
                attack(delta.length());
            }
            execute();
        } else {
            System.out.println("Error!");
        }
    }

    public void attack(double distance) {
        if (distance > 200 || getEnergy() < 15) {
            fire(1);
        } else if (distance > 50) {
            fire(2);
        } else {
            fire(3);
        }
    }

    public void goCorner() {

        // Turn to face the corner
        turnLeft(90);
        // Move to the corner
        ahead(5000);
        // Turn gun to starting point
        turnGunLeft(90);
    }

}