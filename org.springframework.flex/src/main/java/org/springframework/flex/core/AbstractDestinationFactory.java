package org.springframework.flex.core;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import flex.messaging.Destination;
import flex.messaging.MessageBroker;

/**
 * Base class for Flex Destination factories.
 * 
 * @author Jeremy Grelle
 * @author Mark Fisher
 */
public abstract class AbstractDestinationFactory implements InitializingBean, DisposableBean, BeanNameAware {

	private volatile String destinationId;

	private volatile String beanName;

	private String[] channels;

	private MessageBroker broker;


	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public void setBeanName(String name) {
		this.beanName = name;
	}

	public void setMessageBroker(MessageBroker broker) {
		this.broker = broker;
	}

	public void setChannels(String[] channels) {
		this.channels = StringUtils.trimArrayElements(channels);
	}

	protected String getDestinationId() {
		return StringUtils.hasText(destinationId) ? destinationId : beanName;
	}


	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(broker, "The 'messageBroker' property is required.");
		Destination destination = this.createDestination(getDestinationId(), broker);
		this.configureChannels(destination);
		this.initializeDestination(destination);
	}

	public final void destroy() throws Exception {
		if (broker == null || !broker.isStarted()) {
			return;
		}
		this.destroyDestination(getDestinationId(), this.broker);
	}

	private void configureChannels(Destination destination) {
		if (ObjectUtils.isEmpty(channels)) {
			return;
		}
		for (String channelId : channels) {
			Assert.isTrue(broker.getChannelIds().contains(channelId),
					"The channel " + channelId + " is not known to the MessageBroker "
					+ broker.getId() + " and cannot be set on the destination " + getDestinationId());
		}
		destination.setChannels(CollectionUtils.arrayToList(channels));
	}

	protected abstract Destination createDestination(String destinationId, MessageBroker broker) throws Exception;

	protected abstract void initializeDestination(Destination destination) throws Exception;

	protected abstract void destroyDestination(String destinationId, MessageBroker broker) throws Exception;

}