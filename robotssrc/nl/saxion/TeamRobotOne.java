package nl.saxion;

import nl.saxion.Vector;
import robocode.*;

import java.awt.*;
import java.io.IOException;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class TeamRobotOne extends TeamRobot {
    private byte scanDirection = 1;
    public static boolean leader1IsDeath = false;

    private byte moveDirection =1;

    public void run(){
        setBodyColor(Color.yellow);
        setGunColor(Color.red);
        setRadarColor(Color.white);
        setScanColor(Color.blue);
        setBulletColor(Color.yellow);
        setAdjustRadarForRobotTurn(true);

        while (true){
            setTurnRadarRight(36000);
            ahead(100);
            back(100);
            execute();

        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        if (isTeammate(event.getName())) {
            return;
        }

        double enemyBearing = this.getHeading() + event.getBearing();
        double locationX = getX() + event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        double locationY = getY() + event.getDistance() * Math.cos(Math.toRadians(enemyBearing));

        if (leader1IsDeath){
            try {
                // Send enemy position to teammates
                broadcastMessage(new Vector(locationX, locationY));
            } catch (IOException ex) {
                System.out.println("Unable to send order: ");
                ex.printStackTrace(out);
            }
        }

        if (this.getEnergy()<5){
            leader1IsDeath = false;
        }

        double distanceX = locationX -this.getX();
        double distanceY = locationY -this.getY();

        double angle = Math.toDegrees(Math.atan2(distanceX,distanceY));

        Vector enemyPos = new Vector(distanceX,distanceY);
        setTurnRadarRight(getHeading() - getRadarHeading() + event.getBearing());
        turnGunRight(normalRelativeAngleDegrees(angle - getGunHeading()));
        doMove(event.getBearing());
        attack(enemyPos.length());

    }

    public void attack(double distance){
        if (distance < 50 && getEnergy() > 50) {
            fire(3);
        } // otherwise, fire 1.
        else {
            fire(2);
        }
    }

    public void doMove(double bearing) {
        if (getVelocity() == 0)
            moveDirection *= -1;
        // always square off against our enemy
   //     setTurnRight(normalizeBearing(bearing + 90 - (15 * moveDirection)));
        setTurnRight(bearing+ 90);

        // strafe by changing direction every 20 ticks
        if (getTime() % 20 == 0) {
            moveDirection *= -1;
            setAhead(150 * moveDirection);
        }
    }
    double normalizeBearing(double angle) {
        while (angle >  180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof ChangeLeaderMess){
            ChangeLeaderMess changeLeaderMess = (ChangeLeaderMess) event.getMessage();
            if (changeLeaderMess.getLeaderChange().equals("leader on")){
                leader1IsDeath = true;
            }
        }
    }
}