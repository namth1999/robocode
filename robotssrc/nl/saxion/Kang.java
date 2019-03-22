package nl.saxion;

import org.omg.CORBA.SetOverrideType;
import robocode.*;

import java.io.IOException;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Kang extends TeamRobot {
    public static boolean isLeaderMode = false;
    private boolean stopWhenSeeRobot = false;
    private byte scanDirection = 1;

    public void run() {
        //TODO receive message;
        //TODO LeaderSwitch ls;

        double headDgrees = getHeading();
        if (headDgrees != 0 && headDgrees < 0) {

            setTurnRight(-headDgrees);
        } else {
            setTurnLeft(headDgrees);
        }

        while (!isLeaderMode) {
            /**
             * send enegry
             * ls = new LeaderSwitch(3,LeaderSwitch.energy)
             * */
            scanDirection *= -1;
            setAdjustRadarForRobotTurn(false);
            setTurnGunRight(360 * scanDirection);
            //move forward 100
            //if((getX() >= 100 && getX() <= getBattleFieldWidth() - 100) && (getY() >= 100 && getY() <= getBattleFieldHeight() - 100)) {
            setAhead(100);
            waitFor(new MoveCompleteCondition(this));
            //move back 100
            setBack(100);
            waitFor(new MoveCompleteCondition(this));
        }
        /** {
         double degree = Math.abs(getBattleFieldHeight() /2 - getY()) / Math.sqrt(Math.pow((getBattleFieldHeight() /2 - getY()),2) + Math .pow((getBattleFieldWidth()/2 - getX()),2));
         if(getX() > getBattleFieldWidth() /2  && getY() > getBattleFieldHeight()/2){
         //第一象限
         setTurnLeft(90 + degree);
         } else if (getX() < getBattleFieldWidth() /2 && getY() > getBattleFieldHeight()/2) {
         //第二象限
         setTurnRight(90 + degree);
         } else if (getX() < getBattleFieldWidth() /2 && getY() < getBattleFieldHeight()/2){
         //第三象限
         setTurnRight(90 - degree);
         } else {
         //第四象限
         setTurnLeft(90 - degree);
         }
         setAhead(Math.sqrt(Math.pow((getBattleFieldHeight() / 2 - 100),2) + Math.pow((getBattleFieldWidth() / 2 - 100) ,2)));
         }
         */
        while (isLeaderMode) {
            //TODO switch to LeaderMode
            scanDirection *= -1;
            setAdjustRadarForRobotTurn(false);
            setTurnGunRight(360 * scanDirection);
            //move forward 100
            //if((getX() >= 100 && getX() <= getBattleFieldWidth() - 100) && (getY() >= 100 && getY() <= getBattleFieldHeight() - 100)) {
            setAhead(100);
            waitFor(new MoveCompleteCondition(this));
            //move back 100
            setBack(100);
            waitFor(new MoveCompleteCondition(this));
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

        if (isLeaderMode) {
            try {
                // Send enemy position to teammates
                broadcastMessage(new Vector(locationX, locationY));
            } catch (IOException ex) {
                System.out.println("Unable to send order: ");
                ex.printStackTrace(out);
            }
        }

        double distanceX = locationX - this.getX();
        double distanceY = locationY - this.getY();

        double angle = Math.toDegrees(Math.atan2(distanceX, distanceY));

        Vector enemyPos = new Vector(distanceX, distanceY);
        setTurnRadarRight(getHeading() - getRadarHeading() + event.getBearing());
        turnGunRight(normalRelativeAngleDegrees(angle - getGunHeading()));
        attack(enemyPos.length());

    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        changeDirection();
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        changeDirection();
    }


    @Override
    public void onHitRobot(HitRobotEvent event) {
        double gunTurnAmt = normalRelativeAngleDegrees(event.getBearing() + (getHeading() - getRadarHeading()));
        setTurnGunRight(gunTurnAmt);
        changeDirection();
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        changeDirection();
    }

    public void changeDirection() {
        double headDgrees = getHeading();
        out.println(headDgrees);
        if (headDgrees == 90) {
            if (getY() >= getBattleFieldHeight() / 2) {
                setTurnLeft(headDgrees);
            } else {
                setTurnRight(headDgrees);
            }
            out.println(headDgrees);
        } else if (headDgrees == 270) {
            if (getY() >= getBattleFieldHeight() / 2) {
                setTurnRight(90);
            } else {
                setTurnLeft(90);
            }
        } else if (headDgrees == 0) {
            if (getX() <= getBattleFieldWidth() / 2) {
                setTurnLeft(90);
            } else {
                setTurnRight(90);
            }
        } else if (headDgrees == 180 || headDgrees == -180) {
            if (getX() <= getBattleFieldWidth() / 2) {
                setTurnRight(90);
            } else {
                setTurnLeft(90);
            }
        }
    }

//    @Override
//    public void onMessageReceived(MessageEvent event) {
//        //TODO
//        /**
//         if(event.getMessage() instanceof LeaderSwitch){
//         LeaderSwitch ls = (LeaderSwitch) event.getMessage();
//         }
//         isLeadership =  ls.isLeaderAlive
//         */
//        if (event.getMessage() instanceof Vector) {
//            Vector v = (Vector) event.getMessage();
//            // Calculate x and y to target
//            Vector currentPos = new Vector(getX(), getY());
//            Vector delta = v.sub(currentPos);
//            // Calculate angle to target
//            double theta = v.angleInDegrees();
//            // Turn gun to target
//            turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
//            if (stopWhenSeeRobot) {
//                stop();
//                attack(delta.length());
//            } else {
//                attack(delta.length());
//            }
//        } else {
//            System.out.println("Error!");
//        }
//    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getMessage() instanceof ChangeLeaderMess){
            ChangeLeaderMess changeLeaderMess = (ChangeLeaderMess) event.getMessage();
            if (changeLeaderMess.getLeaderChange().equals("leader on")){
                isLeaderMode = true;
            }
        }
    }

    public void attack(double distance) {
        if (distance < 50 && getEnergy() > 50) {
            fire(3);
        } // otherwise, fire 1.
        else {
            fire(2);
        }
    }
}