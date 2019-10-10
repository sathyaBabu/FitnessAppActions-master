package com.example.fitnessappactions.Model;

public class FitStats {
   public int totalCount;
   public double totalDistanceMeters;
  public  long totalDurationMs;

    public FitStats() {
    }

    public FitStats(int totalCount, double totalDistanceMeters, long totalDurationMs) {
        this.totalCount = totalCount;
        this.totalDistanceMeters = totalDistanceMeters;
        this.totalDurationMs = totalDurationMs;
    }
}
