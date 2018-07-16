package com.test.gpio.manual;

import static org.junit.Assert.*;

import org.junit.Test;

import com.test.gpio.ButtonLED;

public class Test1 {

	@Test
	public void test1() throws InterruptedException{
		ButtonLED bPress = new ButtonLED();
		bPress.getButtonPress();
	}

}
