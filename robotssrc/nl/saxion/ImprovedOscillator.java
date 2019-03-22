package nl.saxion;

import robocode.*;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ImprovedOscillator extends TeamRobot {

    ArrayList<Teamate> teamates = new ArrayList<>();

    private AdvancedEnemyBot enemy = new AdvancedEnemyBot();
    private final double PERCENT_BUFFER = .15;
    private byte radarDirection = 1;

    public void run() {
        setAdjustRadarForGunTurn(true);
        enemy.reset();
        while (true) {
            doScanner();
            doMovement();
            doGun();
            execute(); // you must call this!!!
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (isTeammate(e.getName())){
            Teamate teamate = new Teamate();
            boolean duplicatedTeamate = false;
            for (int i=0;i<teamates.size();i++){
                if (e.getName().equals(teamates.get(i).getName())){
                    duplicatedTeamate = true;
                }
            }
            if (teamate.none() || !duplicatedTeamate){
                teamate.update(e);
                teamates.add(teamate);
            }
            return;
        }
        if (
            // we have no enemy, or...
                enemy.none() ||
                        // the one we just spotted is significantly closer, or...
                        e.getDistance() < enemy.getDistance() - 70 ||
                        // we found the one we've been tracking
                        e.getName().equals(enemy.getName())
        ) {
            // track him
            enemy.update(e, this);
            setTurnGunRight(getHeading() - getGunHeading() + enemy.getBearing());
        }
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        setBack(100);
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // check if the enemy we were tracking died
        if (e.getName().equals(enemy.getName())) {
            enemy.reset();
//            stop();
        }
    }

    public void onHitByBullet(HitByBulletEvent e) {
        setBack(100);
    }

    void doScanner() {
        if (enemy.none()) {
            // look around
            setTurnRadarRight(100000);
        } else {
            // keep him inside a cone
            double turn = getHeading() - getRadarHeading() + enemy.getBearing();
            turn += 20 * radarDirection;
            setTurnRadarRight(turn);
            radarDirection *= -1;
        }
    }

    void doMovement() {
        setTurnRight(enemy.getBearing());
        // move a little closer
        if (enemy.getDistance() > 200) {
            setAhead(enemy.getDistance() / 2);
//            avoidWall();
        }
        // but not too close
        if (enemy.getDistance() < 100) {
            setBack(enemy.getDistance());
//            avoidWall();
        }

        for (Teamate teamate : teamates){
            if (teamate.getDistance() < 100){
                setBack(teamate.getDistance());
            }
        }

    }

    void doGun() {

        // don't fire if there's no enemy
        if (enemy.none()) return;

        // convenience variable
        double max = Math.max(getBattleFieldHeight(), getBattleFieldWidth());

        // only shoot if we're (close to) pointing at our enemy
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) { //Math.abs(getTurnRemaining()) < 10
            // calculate firepower based on distance
            double firePower = Math.min(500 / enemy.getDistance(), 3);
// calculate speed of bullet
            double bulletSpeed = 20 - firePower * 3;
// distance = rate * time, solved for time
            long time = (long) (enemy.getDistance() / bulletSpeed);
            // calculate gun turn to predicted x,y location
            double futureX = enemy.getFutureX(time);
            double futureY = enemy.getFutureY(time);
            double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
// turn the gun to the predicted x,y location
            setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));
            setFire(Math.min(400 / enemy.getDistance(), 2));
        }
    }

//    void avoidWall() {
//        double width = getBattleFieldWidth();
//        double height = getBattleFieldHeight();
//        double xPos = getX();
//        double yPos = getY();
//        if (yPos < PERCENT_BUFFER * height) {
//            if ((this.getHeading() < 180) && (this.getHeading() > 90)) {
//                this.setTurnRight(90);
//                setBack(50);
//            } else if (this.getHeading() > 180 && this.getHeading() < 270) {
//                this.setTurnLeft(90);
//                setBack(50);
//            } else if (this.getHeading() > 0 && this.getHeading() < 90) {
//                setAhead(30);
//            } else setAhead(30);
//        } else if (yPos > height - PERCENT_BUFFER * height) {
//            if ((this.getHeading() < 90) && (this.getHeading() > 0)) {
//                this.setTurnLeft(90);
//                setBack(50);
//            } else if (this.getHeading() > 270 && this.getHeading() < 360) {
//                this.setTurnRight(90);
//                setBack(50);
//            } else if (this.getHeading() > 90 && this.getHeading() < 180) {
//                setAhead(30);
//            } else setAhead(30);
//        }
//
//        if (xPos < PERCENT_BUFFER * width) {
//            if ((this.getHeading() < 270) && (this.getHeading() > 180)) {
//                this.setTurnLeft(90);
//                setBack(50);
//            } else if (this.getHeading() > 270 && this.getHeading() < 360) {
//                this.setTurnRight(90);
//                setBack(50);
//            } else if (this.getHeading() > 0 && this.getHeading() < 90) {
//                setAhead(30);
//            } else setAhead(30);
//        } else if (xPos > width - PERCENT_BUFFER * width) {
//            if ((this.getHeading() < 90) && (this.getHeading() > 0)) {
//                this.setTurnRight(90);
//                setBack(50);
//            } else if (this.getHeading() > 90 && this.getHeading() < 180) {
//                this.setTurnLeft(90);
//                setBack(50);
//            } else if (this.getHeading() > 180 && this.getHeading() < 270) {
//                setAhead(30);
//            } else setAhead(30);
//        }
//
//        execute();
//    }

    public double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Point2D.distance(x1, y1, x2, y2);
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }

    public double normalizeBearing(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
}
