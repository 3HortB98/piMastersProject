package com.test.gpio;

import static org.junit.Assert.*;

import org.junit.Test;

public class Test1 {

	@Test
	public void test() {
		TempSensor sensor = new TempSensor();
		sensor.getTempSensor();
	}

}
