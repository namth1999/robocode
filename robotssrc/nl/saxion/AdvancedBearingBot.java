package nl.saxion;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;


public class AdvancedBearingBot extends AdvancedRobot {

    public void run() {
        setAdjustRadarForGunTurn(false);
        setTurnRadarRight(10000);
        while (true) {
            if (getRadarTurnRemaining() == 0)
                setTurnRadarRight(1000);
            // Turn the scanner until we find an enemy robot

            execute(); // you must call this!!!
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        out.println("scanned a robot at bearing " + e.getBearing());
        out.println("my heading is currently " + getHeading());

        // When we scan a robot, turn toward him
        setTurnRight(e.getBearing());
        // shoot at him
        // and ram into him
        setTurnRadarRight(getHeading() - getRadarHeading() + e.getBearing());
        setAhead(e.getDistance());
        if (e.getDistance()<100){
            if (e.getEnergy() > 16) {
                fire(3);
            } else if (e.getEnergy() > 10) {
                fire(2);
            } else if (e.getEnergy() > 4) {
                fire(1);
            } else if (e.getEnergy() > 2) {
                fire(.5);
            } else if (e.getEnergy() > .4) {
                fire(.2);
            }
        }


        // don't have to call execute here, the Robocode engine does it for
        // you after you handle an event.
    }

    public void onHitRobot(HitRobotEvent e) {
        setBack(100);
        setAhead(100);
        changeDirection();
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        changeDirection();
    }

    public void changeDirection(){
        double headDgrees = getHeading();
        out.println(headDgrees);
        if(headDgrees == 90) {
            if(getY() >= getBattleFieldHeight() / 2) {
                setTurnLeft(headDgrees);
            }else {
                setTurnRight(headDgrees);
            }
            out.println(headDgrees);
        }else if(headDgrees == 270){
            if(getY() >= getBattleFieldHeight() / 2) {
                setTurnRight(90);
            } else {
                setTurnLeft(90);
            }
        } else if (headDgrees == 0){
            if(getX() <= getBattleFieldWidth() / 2){
                setTurnLeft(90);
            } else {
                setTurnRight(90);
            }
        } else if (headDgrees == 180 ||headDgrees == -180){
            if(getX() <= getBattleFieldWidth() / 2){
                setTurnRight(90);
            } else {
                setTurnLeft(90);
            }
        }
    }
}
