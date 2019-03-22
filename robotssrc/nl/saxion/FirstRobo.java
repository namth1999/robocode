package nl.saxion;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class FirstRobo extends AdvancedRobot {
    private final double PERCENT_BUFFER = .15;

    public void run(){
        setAdjustRadarForGunTurn(false);
        while (true){
            setAhead(3000);
            avoidWall();
            execute();

        }

    }

    void avoidWall(){
        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();
        double xPos = getX();
        double yPos = getY();
        if (yPos<PERCENT_BUFFER*height){
            if ((this.getHeading()<180)&&(this.getHeading()>90)){
                this.setTurnLeft(90);
            } else if (this.getHeading()>180&&this.getHeading()<270){
                this.setTurnRight(90);
            }
        } else if (yPos>height-PERCENT_BUFFER*height){
            if ((this.getHeading()<90)&&(this.getHeading()>0)){
                this.setTurnRight(90);
            } else if (this.getHeading()>270&&this.getHeading()<360){
                this.setTurnLeft(90);
            }
        }

        if (xPos<PERCENT_BUFFER*width){
            if ((this.getHeading()<270)&&(this.getHeading()>180)){
                this.setTurnLeft(90);
            } else if (this.getHeading()>270&&this.getHeading()<360){
                this.setTurnRight(90);
            }
        } else if (xPos>width-PERCENT_BUFFER*width){
            if ((this.getHeading()<90)&&(this.getHeading()>0)){
                this.setTurnLeft(90);
            } else if (this.getHeading()>90&&this.getHeading()<180){
                this.setTurnRight(90);
            }
        }
        setAhead(10);
        execute();
    }


}