package com.test.gpio;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.w1.W1Master;
import com.pi4j.temperature.TemperatureScale;


public class TempSensor {
	
	public void getTempSensor(){

		W1Master w1Master = new W1Master();

        System.out.println(w1Master);

        for (TemperatureSensor device : w1Master.getDevices(TemperatureSensor.class)) {
            System.out.printf("Device Name"+device.getName() +" Device Temperature "+device.getTemperature(),
                    device.getTemperature(TemperatureScale.FARENHEIT));
        }


    }
}
