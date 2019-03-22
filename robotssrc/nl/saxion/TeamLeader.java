package nl.saxion;

import robocode.DeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

import java.awt.*;
import java.io.IOException;

public class TeamLeader  extends TeamRobot {
    EnemyBot enemyBot = new EnemyBot();

    public void run(){
        setBodyColor(Color.yellow);
        setGunColor(Color.red);
        setRadarColor(Color.white);
        setScanColor(Color.blue);
        setBulletColor(Color.yellow);


        while (true) {
            setTurnRadarRight(10000);
            ahead(100);
            back(100);

            if (this.getEnergy()<10){
                try {
                    broadcastMessage(new ChangeLeaderMess("leader on"));
                } catch (IOException ex) {
                    System.out.println("Unable to send order: ");
                    ex.printStackTrace(out);
                }
            }

            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (isTeammate(e.getName())) {
            return;
        }

        // Calculate enemy bearing
        double enemyBearing = this.getHeading() + e.getBearing();
        // Calculate enemy's position
        double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

        try {
            // Send enemy position to teammates
            broadcastMessage(new Vector(enemyX, enemyY));
        } catch (IOException ex) {
            System.out.println("Unable to send order: ");
            ex.printStackTrace(out);
        }
    }
}