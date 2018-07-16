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


import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.opennms.plugins.messagenotifier.MessageNotificationClient;
import org.opennms.plugins.messagenotifier.SimpleMessageNotificationClientImpl;
import org.opennms.plugins.mqttclient.MQTTClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonLED {


	private static final Logger LOG = LoggerFactory.getLogger(ButtonLED.class);

	// works with 2017-10-19 10:15:02.854888
	private static final String DEFAULT_DATE_TIME_FORMAT_PATTERN="yyyy-MM-dd HH:mm:ss.SSSSSS";

	public static final String SERVER_URL = "tcp://localhost:1883";
	public static final String WRONG_SERVER_URL = "tcp://localhost:1884";
	public static final String MQTT_USERNAME = "mqtt-user";
	public static final String MQTT_PASSWORD = "mqtt-password";
	public static final String CONNECTION_RETRY_INTERVAL = "60000"; 
	public static final String CLIENT_CONNECTION_MAX_WAIT = "40000";

	public static final String CLIENT_ID = "sewatech";
	public static final String TOPIC_NAME = "sewatech";
	public static final int QOS_LEVEL = 0;

	public static final String jsonTestMessage="{"
			+ " \"time\": \""+ jsonTime(new Date())+ "\","
			+ " \"id\": \"monitorID\","
			+ " \"cityName\": \"Southampton\","
			+ " \"stationName\": \"Common#1\","
			+ " \"latitude\": 0,"
			+ " \"longitude\": 0,"
			+ " \"averaging\": 0,"
			+ " \"PM1\": 10,"
			+ " \"PM25\": 100,"
			+ " \"PM10\": 1000"
			+ "}";

	private static final String PROPERTIES_FILE_NAME = "./mqttproperties.properties";



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
				sendMessage();
				return null;
			}
		}));

		// keep program running until user aborts (CTRL-C)
		while (true) {
			Thread.sleep(500);
		}
		
		//gpio.shutdown();
	}


	public void sendMessage() {
		LOG.debug("sending message ");

		Properties properties = new Properties();
		File propertiesfile = new File(PROPERTIES_FILE_NAME);
		try{
			FileInputStream fileInput = new FileInputStream(propertiesfile);
			properties.load(fileInput);
			LOG.warn("fproperties loaded from file:"+propertiesfile.getAbsolutePath());
		}catch (Exception ex){
			LOG.warn("failed to load properties file. " +propertiesfile.getAbsolutePath()+
					" Using Defaults: Error:",ex);
		}

		String brokerUrl = properties.getProperty("mqtt.server.url", SERVER_URL);
		String clientId = properties.getProperty("mqtt.server.clientid", CLIENT_ID);
		String userName = properties.getProperty("mqtt.server.username", MQTT_USERNAME);
		String password = properties.getProperty("mqtt.server.password", MQTT_PASSWORD);
		String connectionRetryInterval= properties.getProperty("mqtt.server.retryinterval", CONNECTION_RETRY_INTERVAL);
		String clientConnectionMaxWait= properties.getProperty("mqtt.server.connectionmaxwait", CLIENT_CONNECTION_MAX_WAIT);
		String topic = properties.getProperty("mqtt.server.topicname",TOPIC_NAME); 
		int qos=0;
		try{
			qos = Integer.parseInt(properties.getProperty("mqtt.server.qoslevel",Integer.toString(QOS_LEVEL)));
		} catch (Exception ex){
			LOG.warn("cannot parse qos value",ex);
		}

		MQTTClientImpl client = new MQTTClientImpl(brokerUrl, clientId, userName, password,connectionRetryInterval,clientConnectionMaxWait );

		try{
			try{
				boolean connected = client.connect();
				LOG.debug("client connected="+connected);
			} catch(Exception e){
				LOG.debug("problem connecting", e);
			}

			MessageNotificationClient messageNotificationClient = new SimpleMessageNotificationClientImpl();

			client.addMessageNotificationClient(messageNotificationClient);

			//String topic=TOPIC_NAME;
			//int qos=QOS_LEVEL;
			try{
				client.subscribe(topic, qos);
			} catch(Exception e){
				LOG.debug("problem subscribing", e);
			}

			String message = jsonTestMessage;

			byte[] payload =null;
			for (int i=0;i<1;i++) {
				LOG.debug("sending message:"+message);
				payload = message.getBytes(); 

				try{
					client.publishSynchronous(topic, qos, payload);
				} catch(Exception e){
					LOG.debug("problem publishing message", e);
				}
			}

		} finally{
			// clean up
			if(client!=null) client.destroy();
		}

		LOG.debug("end of test testSimpleConnection()");
	}


	public static String jsonTime(Date date){
		SimpleDateFormat df = new SimpleDateFormat( DEFAULT_DATE_TIME_FORMAT_PATTERN );

		TimeZone tz = TimeZone.getTimeZone( "UTC" );

		df.setTimeZone( tz );

		String output = df.format( date );
		return output;
	}


}

