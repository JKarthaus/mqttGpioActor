/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.filiberry.mqttGpioActor;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import model.Decision;
import tools.ActorExecutor;

public class Activator implements BundleActivator, ManagedService, MqttCallback {

	private ServiceRegistration serviceReg;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private MqttClient client = null;
	private String host;
	private String listenOnTopic;
	private String gpioActorScript;
	private String gpioInitScript;

	@Override
	public void start(BundleContext context) {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, "mqttGpioActor");
		serviceReg = context.registerService(ManagedService.class.getName(), this, properties);
	}

	@Override
	public void stop(BundleContext context) {
		log.info("The mqttGpioActor Bundle stopped.");
		if (client != null && client.isConnected()) {
			try {
				client.disconnect();
			} catch (MqttException e) {
				log.warning(e.getMessage());
			}
		}
		client = null;
	}

	/**
	 * 
	 */
	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties == null) {
			log.info("mqttGpioActor config is null - Please give me a config File");
			return;
		}
		log.info("mqttGpioActor Config was set.");
		this.host = (String) properties.get("mqttHost");
		this.listenOnTopic = (String) properties.get("mqttTopic");
		this.gpioActorScript = (String) properties.get("scriptGpioActor");
		this.gpioInitScript = (String) properties.get("scriptGpioInit");
		log.info("The mqttGpioActor Bundle startet.");
		try {
			ActorExecutor.RunGPIOInit(gpioInitScript);
		} catch (Exception e) {
			log.warning(e.getMessage());
		}
		connectToBroker();
	}

	/**
	 * 
	 */
	private boolean connectToBroker() {
		try {
			log.info("Try to connect to: " + host + " Topic: " + listenOnTopic);
			if (client == null) {
				client = new MqttClient(host, "mqttGpioActor");
			}
			if (!client.isConnected()) {
				client.connect();
			}
			client.subscribe(listenOnTopic);
			client.setCallback(this);
			log.info("mqttGpioActor connected !");
			// --

		} catch (MqttException e) {
			client = null;
			log.info(e.getMessage());
			return false;
		}
		return true;

	}

	@Override
	public void connectionLost(Throwable arg0) {
		log.warning("mqttGpioActor Connection to Broker is LOST");
		boolean isConnected = false;
		while (!isConnected) {
			log.info("Wait a Minute before reconnect...");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e1) {
				log.warning("Wait Thread was interrupted");
				e1.printStackTrace();
			}
			isConnected = connectToBroker();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String messageData = new String(message.getPayload());
		// --
		try {
			log.info("Message on Topic " + topic + " Arrived...");
			String gpioPort = topic.substring(listenOnTopic.length() - 1);
			String data = new String(message.getPayload());
			// --
			Decision decision = null;

			if (data.equalsIgnoreCase("ON")) {
				decision = new Decision(Decision.ON);
			}
			if (data.equalsIgnoreCase("OFF")) {
				decision = new Decision(Decision.OFF);
			}
			if (decision != null) {
				log.info("Run Actor :" + gpioActorScript + " " + gpioPort + " " + decision.toString());
				ActorExecutor.RunActionUnatended(gpioActorScript, gpioPort, decision);
			} else {
				log.warning("Message ist not on or Off");
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

}