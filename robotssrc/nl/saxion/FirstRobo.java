package nl.saxion;

import robocode.Robot;

public class FirstRobo extends Robot {
    public void run(){
        while (true){
            ahead(100);
            turnLeft(100);
        }
    }
}