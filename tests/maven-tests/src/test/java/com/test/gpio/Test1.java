package com.test.gpio;

import static org.junit.Assert.*;

import org.junit.Test;

public class Test1 {

	@Test
	public void test() throws InterruptedException{
		ButtonLED bPress = new ButtonLED();
		bPress.getButtonPress();
	}

}
