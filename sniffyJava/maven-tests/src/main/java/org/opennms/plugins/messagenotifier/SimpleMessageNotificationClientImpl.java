package org.opennms.plugins.messagenotifier;

import java.util.List;

import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMessageNotificationClientImpl implements MessageNotificationClient {
	private static 	final Logger LOG = LoggerFactory.getLogger(SimpleMessageNotificationClientImpl.class);

	@Override
	public void sendMessageNotification(MessageNotification messageNotification) {
		if(LOG.isDebugEnabled()) LOG.debug("Notification received by VerySimpleMessageNotificationClient :\n topic:"+messageNotification.getTopic()
				+ "\n qos:"+messageNotification.getQos()
				+ "\n payload:"+new String(messageNotification.getPayload()));

	}

	@Override
	public void init() {
		LOG.debug("VerySimpleMessageNotificationClient initialised");

	}

	@Override
	public void destroy() {
		LOG.debug("VerySimpleMessageNotificationClient destroyed");

	}


	@Override
	public List<MessageNotifier> getIncommingMessageNotifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIncommingMessageNotifiers(List<MessageNotifier> messageNotifiers) {
		// TODO Auto-generated method stub

	}

}
