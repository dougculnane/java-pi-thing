package net.culnane.pi.thing.actuator;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfigBuilder;
import com.pi4j.io.gpio.digital.DigitalState;

import net.culnane.pi.helper.PIN;

/**
 * Relay switch.
 *
 * @author Doug Culnane
 */
public class Relay {
	
	/**
	 * Name of the actuator.
	 */
	private String name = "MyRelay";
	
	private boolean wiredOffWithNoPower = false;
	
	private boolean on = false;
	
	private DigitalOutput digitalOutput = null;
	
	public Relay(Context pi4jContext, PIN pin, boolean wiredOffWithNoPower) {
		this(pi4jContext, pin, wiredOffWithNoPower, "Relay pin " + pin.getPin());
	}
	
	public Relay(Context pi4jContext, PIN pin, boolean wiredOffWithNoPower, String name) {
		DigitalOutputConfigBuilder relayConfig = DigitalOutput.newConfigBuilder(pi4jContext)
			      .id("relay-" + pin.getPin())
			      .name(name)
			      .address(pin.getPin())
			      .shutdown(DigitalState.LOW)
			      .initial(DigitalState.LOW)
			      .provider("pigpio-digital-output");
			      
		digitalOutput = pi4jContext.create(relayConfig);
		this.name = name;
	}
	
	public void on() {
		if (wiredOffWithNoPower) {
			digitalOutput.high();
		} else {
			digitalOutput.low();
		}
		on = true;
	}
	
	public void off() {
		if (wiredOffWithNoPower) {
			digitalOutput.low();
		} else {
			digitalOutput.high();
		}
		on = false;
	}

	public String getName() {
	    return name;
	}
	
	public boolean isOn() {
		return on;
	}
}
