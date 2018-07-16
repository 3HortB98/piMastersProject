package com.test.gpio.manual;

import static org.junit.Assert.*;

import org.junit.Test;

import com.test.gpio.ButtonLED;

public class MessageTest {

	@Test
	public void test() {
		ButtonLED buttonLED = new ButtonLED();
		buttonLED.sendMessage();
	}

}
