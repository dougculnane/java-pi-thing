package net.culnane.pi.thing.actuator;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pi4j.context.Context;
import com.pi4j.io.pwm.Pwm;
import com.pi4j.io.pwm.PwmConfig;

import net.culnane.pi.helper.PIN;

/**
 * Servo controller.
 *
 * https://github.com/Pi4J/pi4j-example-components/blob/main/src/main/java/com/pi4j/catalog/components/ServoMotor.java
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
     * Default minimum PWM duty cycle to put the PWM into the minimum angle position
     */
    protected final static int DEFAULT_MIN_DUTY_CYCLE = 2;
    /**
     * Maximum PWM duty cycle to put the PWM into the maximum angle position
     */
    protected final static int DEFAULT_MAX_DUTY_CYCLE = 12;
    
	private Relay powerRelay;
    private final Pwm pwm;
	private int currentAngle = -1;
	
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
		PwmConfig pwmConfig = Pwm.newConfigBuilder(pi4jContext)
	                .id("BCM" + pin.getPin())
	                .name(name)
	                .address(pin.getPin())
	                .frequency(DEFAULT_FREQUENCY)
	                .initial(0)
	                .shutdown(0)
	                .build();
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
    private float mapToDutyCycle(int input, int inputStart, int inputEnd) {
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
    private static float mapRange(float input, float inputStart, float inputEnd, float outputStart, float outputEnd) {
        // Automatically swap minimum/maximum of input if inverted
        if (inputStart > inputEnd) {
            final float tmp = inputEnd;
            inputEnd = inputStart;
            inputStart = tmp;
        }

        // Automatically swap minimum/maximum of output if inverted
        if (outputStart > outputEnd) {
            final float tmp = outputEnd;
            outputEnd = outputStart;
            outputStart = tmp;
        }

        // Automatically clamp the input value and calculate the mapped value
        final float clampedInput = Math.min(inputEnd, Math.max(inputStart, input));
        return outputStart + ((outputEnd - outputStart) / (inputEnd - inputStart)) * (clampedInput - inputStart);
    }

    /**
     * shutting down the component
     */
    public void off(){
        this.pwm.off();
    }
	
}
