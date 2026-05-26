package net.culnane.pi.thing.actuator;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;
import com.pi4j.io.pwm.PwmType;

import net.culnane.pi.helper.PIN;

/**
 * Servo controller.
 *
 * https://github.com/Pi4J/pi4j-example-components/blob/main/src/main/java/com/pi4j/catalog/components/ServoMotor.java
 * 
 * Position "0" (1.5ms pulse) is middle, 
 * "90" (~2ms pulse) is all the way to the right, 
 * "-90" (~1ms pulse) is all the way to the left.
 */
public class Servo {

	/**
     * Default PWM frequency of the servo, based on values for SG92R
     */
    protected final static int DEFAULT_FREQUENCY = 50;

    /**
     * Default minimum angle of the servo motor, based on values for SG92R
     */
    protected final static int DEFAULT_MIN_ANGLE = -90;
    /**
     * Default maximum angle of the servo motor, based on values for SG92R
     */
    protected static final int DEFAULT_MAX_ANGLE = 90;
    
    /**
     * Default minimum PWM duty cycle to put the PWM into the minimum angle position in percent (0.5ms pulse)
     */
    protected final static int DEFAULT_MIN_DUTY_CYCLE = (int)(0.5 * 100 / (1000 / DEFAULT_FREQUENCY)) ;
    /**
     * Maximum PWM duty cycle to put the PWM into the maximum angle position in percent (2.5ms pulse)
     */
    protected final static int DEFAULT_MAX_DUTY_CYCLE = (int)(2.5 * 100 / (1000 / DEFAULT_FREQUENCY)) ;
    
	private Relay powerRelay;
	private int currentAngle = -1;
	private Pwm pwm;
	
	/**
	 * Name of the actuator.
	 */
	private String name = "MyServo";
	
	public Servo(Context pi4jContext, PIN pin) {
		this(pi4jContext, pin, null);
	}
	
	public Servo(Context pi4jContext, PIN pin, PIN powerRelayPin) {
		this(pi4jContext, pin, powerRelayPin, "Servo "+ pin.getPin());
	}
	
	public Servo(Context pi4jContext, PIN pin, PIN powerRelayPin, String name) {
	    if (pin != PIN.PWM18) {
	        throw new RuntimeException("For PWM use pin 18 the PCM Clock");
	    }
	    
	    // Build the PWM configuration for the hardware pin
	    PwmConfig pwmConfig = Pwm.newConfigBuilder(pi4jContext)
            .id(pin.toString())
            .name("Hardware PWM " + pin.getPin())
            .bcm(pin.getPin())
            .pwmType(PwmType.HARDWARE)
            .channel(0)
            .chip(0)
            .provider("ffm-pwm") 
            .frequency(DEFAULT_FREQUENCY) // Set frequency to 100 Hz
            .initial(50)    // Set initial duty cycle to 50%
            .build();

        // Create the PWM instance
		this.pwm = pi4jContext.create(pwmConfig);
		this.name = name;
		if (powerRelayPin != null) {
			powerRelay = new Relay(pi4jContext, powerRelayPin, true);
		}
	}
	
	public synchronized void setPositionAngle(int angle) {

		if (angle < DEFAULT_MIN_ANGLE) {
			angle = DEFAULT_MIN_ANGLE;
		}
		if (angle > DEFAULT_MAX_ANGLE) {
			angle = DEFAULT_MAX_ANGLE;
		}

		if (powerRelay != null &&
				(currentAngle < 0 || angle != currentAngle)) {
			
			if (powerRelay != null) {
				powerRelay.on();
			}
			
			// wait for servo to move position.
			ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			exec.schedule(new Runnable() {
				public void run() {
					powerRelay.off();
					off();
				}
			}, 5, TimeUnit.SECONDS);
		}
		
		pwm.on(mapToDutyCycle(angle, DEFAULT_MIN_ANGLE, DEFAULT_MAX_ANGLE));
		currentAngle = angle;
	}
	
	public String getName() {
	    return name;
	}
	
    /**
     * Helper function to map an input value between a specified range to the configured duty cycle range.
     *
     * @param input      Value to map
     * @param inputStart Minimum value for custom range
     * @param inputEnd   Maximum value for custom range
     * @return Duty cycle required to achieve this position
     */
	protected static int mapToDutyCycle(int input, int inputStart, int inputEnd) {
        return mapRange(input, inputStart, inputEnd, DEFAULT_MIN_DUTY_CYCLE, DEFAULT_MAX_DUTY_CYCLE);
    }

    /**
     * Helper function to map an input value from its input range to a possibly different output range.
     *
     * @param input       Input value to map
     * @param inputStart  Minimum value for input
     * @param inputEnd    Maximum value for input
     * @param outputStart Minimum value for output
     * @param outputEnd   Maximum value for output
     * @return Mapped input value
     */
	private static int mapRange(float input, float inputStart, float inputEnd, float outputStart, float outputEnd) {
        // Automatically clamp the input value and calculate the mapped value
        final float clampedInput = Math.min(inputEnd, Math.max(inputStart, input));
        return Math.round(outputStart + ((outputEnd - outputStart) / (inputEnd - inputStart)) * (clampedInput - inputStart));
    }

    /**
     * shutting down the component
     */
    public void off(){
        this.pwm.off();
    }
	
}
