package com.test.gpio;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TempSensor sensor = new TempSensor();
		double temp=sensor.getTempSensor();
		if(temp!=0){		
			System.out.println("Temperature is: "+temp);
		}
		else{
			System.out.println("Sensor not found");	
		}
	}

}
