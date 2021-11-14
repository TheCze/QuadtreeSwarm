package de.zoneofcontrol.quadtree;

public class Timer {
    double[] timer;
    int index;
    int maxsize=100;
    double stoppedtime=0;
    double sum=0;


    public Timer(){
        timer=new double[maxsize];
        index=0;
    }

    public void start(){
        stoppedtime=System.nanoTime();
    }

    public void stop(){
        double passedtime=System.nanoTime()-stoppedtime;
        sum-=timer[index%maxsize];
        timer[index%maxsize]=passedtime;
        sum+=passedtime;
        index++;
    }

    public double getAverageMStime(){
        return sum/maxsize;
    }




}
