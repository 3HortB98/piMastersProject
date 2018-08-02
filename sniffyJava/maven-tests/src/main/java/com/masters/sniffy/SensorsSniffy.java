package com.masters.sniffy;


import java.util.concurrent.Callable;

import com.pi4j.io.serial.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.opennms.plugins.messagenotifier.MessageNotificationClient;
import org.opennms.plugins.messagenotifier.SimpleMessageNotificationClientImpl;
import org.opennms.plugins.mqttclient.MQTTClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorsSniffy {


	private static final Logger LOG = LoggerFactory.getLogger(SensorsSniffy.class);

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
	
	private static final String CSVFILENAME = "./sensorcsv.csv";


	public static void main(String args[]) throws InterruptedException, IOException {

		final SensorsSniffy sniffy = new SensorsSniffy();
		
		
		
		// create Pi4J console wrapper/helper
		final Console console = new Console();
		
		// print program title/header
		console.title("<-- The Masters sniffy Project -->", "Serial Communication");
		
		// allow for user to exit program using CTRL-C
		console.promptForExit();
	
		// create an instance of the serial communications class
		final Serial serial = SerialFactory.createInstance();

		serial.addListener(new SerialDataEventListener() 
		{
			@Override
			public void dataReceived(SerialDataEvent event) 
			{
				try{
					//console.println("[HEX DATA]   " + event.getHexByteString());
					
					byte[] rxBytes=event.getBytes();
					
					if(rxBytes[0] == 66){
						
						if(rxBytes[1]== 77){
							String pm1= Integer.toString(rxBytes[4]+256*rxBytes[5]);
							String pm25= Integer.toString(rxBytes[6]+256*rxBytes[7]);
							String pm10= Integer.toString(rxBytes[8]+256*rxBytes[9]);
							String tMessage = "{"
									+ " \"time\": \""+ jsonTime(new Date())+ "\","
									+ " \"id\": \"monitorID\","
									+ " \"cityName\": \"Southampton\","
									+ " \"stationName\": \"Common#1\","
									+ " \"latitude\": 0,"
									+ " \"longitude\": 0,"
									+ " \"averaging\": 0,"
									+ " \"PM1\": "+ pm1 +","
									+ " \"PM25\": "+ pm25 +","
									+ " \"PM10\": "+ pm10
									+ "}";
							 console.println("[Message]	" + tMessage);
							 
							 //sniffy.sendMessage(tMessage);
							 
							 try{
								 File file = new File(CSVFILENAME);
								 if(!file.exists()){
									 file.createNewFile();
								 }
								 FileWriter fw = new FileWriter(file,true);
								 BufferedWriter bw = new BufferedWriter(fw);
								 bw.write(tMessage);
								 bw.newLine();
								 bw.close();
							 }catch(IOException e){
								 e.printStackTrace();
							 }
							 try{
							Thread.sleep(30000);
							 }catch(InterruptedException e){
								 console.println("System got inturrpted");
							 }
						}
					}else{
						console.println("not found");
					}
							
					/*String hexbyte = event.getHexByteString();
					String hexbytenospace = hexbyte.replaceAll("\\s","");
					String[] hexbytearray = hexbytenospace.split(",");
					int[] hex = new int[hexbytearray.length];
					int i=0;
					for (String h : hexbytearray){
						int hexint = Integer.parseInt(h,16);
						hex[i] = hexint;
						i++;
					}*/
					/*String tMessage = "{"
							+ " \"time\": \""+ jsonTime(new Date())+ "\","
							+ " \"id\": \"monitorID\","
							+ " \"cityName\": \"Southampton\","
							+ " \"stationName\": \"Common#1\","
							+ " \"latitude\": 0,"
							+ " \"longitude\": 0,"
							+ " \"averaging\": 0,"
							+ " \"PM1\": "+ (hex[3] * 256 + hex[4]) +","
							+ " \"PM25\": "+ (hex[5] * 256 + hex[6]) +","
							+ " \"PM10\": "+ (hex[7] * 256 + hex[8])
							+ "}";*/
					
					 
					//testing to see what hex values are in array correctly
                    /*for (int j=0; j<hex.length; j++){
                    	console.println("[Hex value "+ j + "]	" + hex[j]);
                    }*/
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
			}

		});

		try {
            // create serial config object
            SerialConfig config = new SerialConfig();

            // set default serial settings (device, baud rate, flow control, etc)
            //
            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
            // NOTE: this utility method will determine the default serial port for the
            //       detected platform and board/model.  For all Raspberry Pi models
            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
            //       environment configuration.
            config.device(SerialPort.getDefaultPort())
                  .baud(Baud._9600)
                  .dataBits(DataBits._8)
                  .parity(Parity.NONE)
                  .stopBits(StopBits._1)
                  .flowControl(FlowControl.NONE);

            // parse optional command argument options to override the default serial settings.
            if(args.length > 0){
                config = CommandArgumentParser.getSerialConfig(config, args);
            }

            // display connection details
            console.box(" Connecting to: " + config.toString(),
                    " We are sending ASCII data on the serial port every 1 second.",
                    " Data received on serial port will be displayed below.");


            // open the default serial device/port with the configuration settings
            serial.open(config);

            // continuous loop to keep the program running until the user terminates the program
            while(console.isRunning()) {
                try {
                    // write a formatted string to the serial transmit buffer
                    serial.write("CURRENT TIME: " + new Date().toString());

                    // write a individual bytes to the serial transmit buffer
                    serial.write((byte) 13);
                    serial.write((byte) 10);

                    // write a simple string to the serial transmit buffer
                    serial.write("Second Line");

                    // write a individual characters to the serial transmit buffer
                    serial.write('\r');
                    serial.write('\n');

                    // write a string terminating with CR+LF to the serial transmit buffer
                    serial.writeln("Third Line");
                }
                catch(IllegalStateException ex){
                    ex.printStackTrace();
                }

                // wait 1 second before continuing
                Thread.sleep(1000);
            }

        }
        catch(IOException ex) {
            console.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
}

		
		/*myButton.addTrigger(new GpioCallbackTrigger(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				System.out.println(" --> GPIO TRIGGER CALLBACK RECEIVED ");
				sendMessage();
				return null;
			}
		}));*/

		
	}


	public void sendMessage(String messaged) {
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

			//String message = jsonTestMessage;
			String message = messaged;

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

