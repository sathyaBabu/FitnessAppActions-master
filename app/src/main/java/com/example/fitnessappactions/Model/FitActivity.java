package com.example.fitnessappactions.Model;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.example.fitnessappactions.R;


//database table to hold the fitness activities
@Entity(tableName = "fitness_activities",indices = {@Index("id")})
//entity is a pojo class for database
public class FitActivity {
    public FitActivity() {
        //required empty constructor
    }

    public FitActivity(String id, long date, Type type, double distanceMeters, long durationMs) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.distanceMeters = distanceMeters;
        this.durationMs = durationMs;
    }

    //column id is the primary key
    @PrimaryKey
    @NonNull
    @ColumnInfo(name="id")
   public String id;
    //column for the date
    @ColumnInfo(name="date")
   public long date;

    //column for the type
    //type is in enum
    @ColumnInfo(name="type")

    public Type type = Type.UNKNOWN;

    //column for distance in meters
    @ColumnInfo(name="distanceMeters")
   public double distanceMeters;

    //column for duration
    @ColumnInfo(name="durationMs")
    public
    long durationMs;

    //enum consists of the types of activities
    //be default enum has a private parameterless constructor
    //in this case when every constant is a new object of enum class, so declare a constructor
    public enum Type{ UNKNOWN(R.string.activity_unknown),
       RUNNING(R.string.activity_running),
       WALKING(R.string.activity_walking),
       CYCLING(R.string.activity_cycling);

      private int nameId;
      Type(int id){
          nameId = id;
      }

   }

   public Type find(String type1){
       Type returnType;

       if(type1.equalsIgnoreCase(type.name()))
           returnType = type;
           else
               returnType = Type.UNKNOWN;
       return returnType;
   }



}
