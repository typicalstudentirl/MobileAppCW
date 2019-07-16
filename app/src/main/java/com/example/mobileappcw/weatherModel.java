package com.example.mobileappcw;

public class weatherModel {

    public String temp;
    public String pressure;
    public String description;
    public String humidity;
    public String speed;


    public weatherModel(){

    }

    //Temperature
    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    //Pressure
    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Humidity
    public String getHumidity(){
        return humidity;
    }

    public void setHumidity(String humidity){
        this.humidity = humidity;
    }

    public String getSpeed(){
        return speed;
    }

    public void setSpeed(String speed){
        this.speed = speed;
    }  
}
