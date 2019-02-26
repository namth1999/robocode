package nl.saxion;

import robocode.Robot;
import robocode.util.Utils;

import java.util.ArrayList;

public class _3_W2A1_Basic_Robot_Driving extends Robot {

    private ArrayList<Vector> waypoints = new ArrayList<Vector>();

    @Override
    public void run(){
        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();
        double w = getWidth();
        double h = getHeight();

        Vector boundary = new Vector(w, h).scale(1.5);

        waypoints.add(new Vector(0+boundary.x,0+boundary.y));
        waypoints.add(new Vector(width-boundary.x, 0+boundary.y));
        waypoints.add(new Vector(0+boundary.x, height-boundary.y));
        waypoints.add(new Vector(width-boundary.x, height-boundary.y));

        int waypointIndex = 0;
        double minDistance = Double.MAX_VALUE;

        //find the closest
        Vector currentPos = new Vector(getX(), getY());
        for (int i = 0; i < waypoints.size();i++) {
            double distance =Vector.distance(waypoints.get(i), currentPos);
            if (distance < minDistance) {
                minDistance = distance;
                waypointIndex = i;
            }
        }

        while (true) {

            double currentAngle = getHeading();
            currentPos = new Vector(getX(), getY());
            Vector targetPos = waypoints.get(waypointIndex);
            Vector delta = targetPos.clone().sub(currentPos);
            double targetAngle = delta.angleInDegrees();

            double requiredTurn = Utils.normalRelativeAngleDegrees(targetAngle-currentAngle);
            turnRight(requiredTurn);

            ahead (delta.length());

            waypointIndex++;
            waypointIndex %= waypoints.size();
        }
    }
}