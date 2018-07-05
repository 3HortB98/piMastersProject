package com.test.gpio;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.w1.W1Master;
import com.pi4j.temperature.TemperatureScale;

import java.io.IOException;

public class TempSensor {
	public float getTempSensor(){
		W1Master w1m = new W1Master();
		
		System.out.println(W1m);
		
		for (TemperatureSensor device : w1Master.getDevices(TemperatureSensor.class)) {
            System.out.printf("%-20s %3.1f°C %3.1f°F\n", device.getName(), device.getTemperature(),
                    device.getTemperature(TemperatureScale.CELSIUS));
        }

        System.out.println("Exiting W1TempExample");
    }
}
