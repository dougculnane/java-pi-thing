package net.culnane.pi.thing.actuator;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

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
	
	private GpioPinDigitalOutput pin = null;
	
	public Relay(Pin pin, boolean wiredOffWithNoPower) {
		this.pin = GpioFactory.getInstance().provisionDigitalOutputPin(pin, PinState.LOW);
		this.wiredOffWithNoPower = wiredOffWithNoPower;
	}
	
	public Relay(Pin pin, boolean wiredOffWithNoPower, String name) {
		this(pin, wiredOffWithNoPower);
		this.name = name;
	}
	
	public void on() {
		if (wiredOffWithNoPower) {
			pin.setState(PinState.HIGH);
		} else {
			pin.setState(PinState.LOW);
		}
		on = true;
	}
	
	public void off() {
		if (wiredOffWithNoPower) {
			pin.setState(PinState.LOW);
		} else {
			pin.setState(PinState.HIGH);
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
