package net.culnane.pi.thing.actuator;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Servo controller.
 *
 * @author Doug Culnane
 */
public class Servo {

	private Relay powerRelay;
	private GpioPinPwmOutput pwm;
	
	public Servo(Pin pwmPin) {
		this(pwmPin, null);
	}
	
	/**
	 * All Raspberry Pi models support a hardware PWM pin on GPIO_01. Raspberry
	 * Pi models A+, B+, 2B, 3B also support hardware PWM pins: GPIO_23,
	 * GPIO_24, GPIO_26
	 * 
	 * @param pwmPin null for default GPIO_23.
	 * @param powerRelayPin null if always on.
	 */
	public Servo(Pin pwmPin, Pin powerRelayPin) {

		GpioController gpio = GpioFactory.getInstance();
		
		if (powerRelayPin == null) {
			powerRelay = null;
		} else {
			powerRelay = new Relay(powerRelayPin, true);
		}
		if (pwmPin == null) {
			pwmPin = RaspiPin.GPIO_23;
		}
		
		// REF: https://raspberrypi.stackexchange.com/questions/4906/control-hardware-pwm-frequency
		pwm = gpio.provisionPwmOutputPin(pwmPin);
		com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
		com.pi4j.wiringpi.Gpio.pwmSetRange(200);
		com.pi4j.wiringpi.Gpio.pwmSetClock(1920);
	}

	public synchronized void setPositionAngle(int angle) {

		if (angle < 0) {
			angle = 0;
		}
		if (angle > 180) {
			angle = 180;
		}

		if (powerRelay != null) {
			
			if (powerRelay != null) {
				powerRelay.on();
			}
			
			// wait for servo to move position.
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			exec.schedule(new Runnable() {
				public void run() {
					powerRelay.off();
				}
			}, 5, TimeUnit.SECONDS);
		}
		
		// 10 = 0
		// 15 = 90
		// 20 = 180
		int pwmRate = 10 + angle * 10 / 180;
		pwm.setPwm(pwmRate);
	}
	
}
