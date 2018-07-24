/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.plugins.mqttclient.test.manual;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.opennms.plugins.messagenotifier.MessageNotificationClient;
import org.opennms.plugins.messagenotifier.SimpleMessageNotificationClientImpl;
import org.opennms.plugins.mqttclient.MQTTClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MQTTClientConnectionTests {
	private static final Logger LOG = LoggerFactory.getLogger(MQTTClientConnectionTests.class);

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


	@Test
	public void testSimpleConnection() {
		LOG.debug("start of test testSimpleConnection() ");

		String brokerUrl = SERVER_URL;
		String clientId = CLIENT_ID;
		String userName =MQTT_USERNAME;
		String password =MQTT_PASSWORD;
		String connectionRetryInterval= CONNECTION_RETRY_INTERVAL;
		String clientConnectionMaxWait= CLIENT_CONNECTION_MAX_WAIT;


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

			String topic=TOPIC_NAME;
			int qos=QOS_LEVEL;
			try{
				client.subscribe(topic, qos);
			} catch(Exception e){
				LOG.debug("problem subscribing", e);
			}

			String message = jsonTestMessage;
			
			byte[] payload =null;
			for (int i=0;i<5;i++) {
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
