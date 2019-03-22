package nl.saxion;

import robocode.ScannedRobotEvent;

public class Teamate {
    private double bearing;
    private double distance;
    private double energy;
    private double heading;
    private double velocity;
    private String name;

    public double getBearing(){
        return bearing;
    }
    public double getDistance(){
        return distance;
    }
    public double getEnergy(){
        return energy;
    }
    public double getHeading(){
        return heading;
    }
    public double getVelocity(){
        return velocity;
    }
    public String getName(){
        return name;
    }
    public void update(ScannedRobotEvent bot){
        bearing = bot.getBearing();
        distance = bot.getDistance();
        energy = bot.getEnergy();
        heading = bot.getHeading();
        velocity = bot.getVelocity();
        name = bot.getName();
    }
    public Boolean none(){
        if (name == null || name == "")
            return true;
        else
            return false;
    }
}
