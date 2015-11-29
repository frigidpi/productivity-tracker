package com.example.android.productivitytracker;

/**
 * Created by qedpi on 2015-11-29.
 */
public class CalcScore {
    public double calcSingle(int duration, int goal, int limit, double weight){
        double linearValue = 0;
        double decayedValue = 0;
        if (duration <= goal){
            linearValue = duration;
        } else {
            linearValue = goal;
            double state = (-1) * ((double)(duration) - goal) / (limit - goal);
            decayedValue = (-1) * Math.pow(2.0, state) / Math.log(2.0);
        }
        return (weight * (linearValue + decayedValue));
    }
    public double[] calcMultiple(int[] duration, int[] goal, int[] limit, double[] weight){
        int cats = duration.length;
        double[] outs = new double[cats];
        for (int i = 0; i < cats; i++){
            outs[i] = calcSingle(duration[i], goal[i], limit[i], weight[i]);
        }
        return outs;
    }
}
