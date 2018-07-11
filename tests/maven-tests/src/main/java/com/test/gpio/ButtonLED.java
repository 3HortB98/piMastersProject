package com.test.gpio;


import java.util.concurrent.Callable;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ButtonLED {
	
	public void getButtonPress() throws InterruptedException{
		
		System.out.println("<--Pi4J--> GPIO Trigger Example ... started.");
		
		final GpioController gpio = GpioFactory.getInstance();
		
		final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02,
                PinPullResistance.PULL_DOWN);
		
		System.out.println(" ... complete the GPIO #02 circuit and see the triggers take effect.");
		
		myButton.setShutdownOptions(true);
		
		final GpioPinDigitalOutput myLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LED #1", PinState.LOW);
		
		
		
		myButton.addListener(new GpioPinListenerDigital()
			{
	            @Override
	            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event)
				{
	                // display pin state on console
	                System.out.println(" Switch change detected: " + event.getPin() + " = " + event.getState());
	            }
	 
	        });
	 
	 
	        
		
		
		myButton.addTrigger(new GpioSetStateTrigger(PinState.HIGH, myLed, PinState.HIGH));
		
		myButton.addTrigger(new GpioSetStateTrigger(PinState.LOW, myLed, PinState.LOW));
		
		myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
			@Override
            public Void call() throws Exception {
                System.out.println(" --> GPIO TRIGGER CALLBACK RECEIVED ");
                return null;
            }
        }));

        // keep program running until user aborts (CTRL-C)
        while (true) {
            Thread.sleep(500);
        }
    }
}
