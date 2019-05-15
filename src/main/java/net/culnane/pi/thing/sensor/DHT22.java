package net.culnane.pi.thing.sensor;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.Gpio;

/**
 * Implements the DHT22 / AM2302 reading in Java using Pi4J.
 * 
 * See sensor specification sheet for details.
 *
 * @author Doug Culnane
 */
public class DHT22 {

	/**
	 * Time in nanoseconds to separate ZERO and ONE signals.
	 */
	private static final int LONGEST_ZERO = 50000;
	
	/**
	 * Minimum time in milliseconds to wait between reads of sensor.
	 */
	public static final int MIN_MILLISECS_BETWEEN_READS = 2500;
    
	/**
	 * PI4J Pin number.
	 */
    private int pinNumber;
    
    /**
     * 40 bit Data from sensor
     */
    private byte[] data = null;
    
    /**
     * Value of last successful humidity reading.
     */
    private Double humidity = null;
    
    /**
     * Value of last successful temperature reading.
     */
    private Double temperature = null;
    
    /**
     * Last read attempt
     */
    private Long lastRead = null;
    
    /**
     * Constructor with pin used for signal.  See PI4J and WiringPI for
     * pin numbering systems.....
     *
     * @param pin
     */
    public DHT22(Pin pin) {
        pinNumber = pin.getAddress();
    }

    /**
     * Communicate with sensor to get new reading data.
     *
     * @throws Exception if failed to successfully read data.
     */
    private void getData() throws Exception {
    	ExecutorService executor = Executors.newSingleThreadExecutor();
    	ReadSensorFuture readSensor = new ReadSensorFuture();
    	Future<byte[]> future = executor.submit(readSensor);
        // Reset data
        data = new byte[5];
        try {
            data = future.get(2, TimeUnit.SECONDS);
            readSensor.close();
        } catch (TimeoutException e) {
        	readSensor.close();
        	future.cancel(true);
            executor.shutdown();
            throw e;
        }
        readSensor.close();
        executor.shutdown();
    }
    
    public boolean doBestPossibleReadLoop() throws InterruptedException, IOException {
		Exception returnException = null;
	    for (int i=0; i < 5; i++) {
			try {
				if (read(true)) {
					return true;
				}
			} catch (ParityCheckException pce) {
				returnException = pce;
			} catch (Exception e) {
				if (Objects.isNull(returnException)) {
					returnException = e;
				}
			}
			Thread.sleep(DHT22.MIN_MILLISECS_BETWEEN_READS);
		}
	    // Failed so turn of parity check and hope to return something better than and error!
	    if (ParityCheckException.class.getName().equals(returnException.getClass().getName())) {
	    	for (int i=0; i < 5; i++) {
		    	try {
					if (read(false)) {
						return true;
					}
		    	} catch (Exception e) {
					returnException = e;
				}
		    	Thread.sleep(DHT22.MIN_MILLISECS_BETWEEN_READS);
		    }
	    }
	    throw new IOException(returnException);
	}
    
    /**
     * Make a new sensor reading.
     * 
     * @return
     * @throws Exception
     */
    public boolean read() throws Exception {
    	return read(true);
    }

    /**
     * Make a new sensor reading
     * 
     * @param checkParity Should a parity check be performed?
     * @return
     * @throws Exception
     */
	public boolean read(boolean checkParity) throws Exception {
		checkLastReadDelay();
		lastRead = System.currentTimeMillis();
    	getData();
    	if (checkParity) {
			checkParity();
		}
    	humidity = getReadingValueFromBytes(data[0], data[1]);
    	temperature = getReadingValueFromBytes(data[2], data[3]);
    	lastRead = System.currentTimeMillis();
    	return true;
    }
	
	private void checkLastReadDelay() throws Exception {
		if (Objects.nonNull(lastRead)) {
			if (lastRead > System.currentTimeMillis() - 2000) {
				throw new Exception("Last read was under 2 seconds ago. Please wait longer between reads!");
			}
		}
	}

	private double getReadingValueFromBytes(final byte hi, final byte low) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.put(hi);
		bb.put(low);
		short shortVal = bb.getShort(0);
		return new Double(shortVal) / 10;
	}

	private void checkParity() throws ParityCheckException {
		if (!(data[4] == (data[0] + data[1] + data[2] + data[3]))) {
			throw new ParityCheckException();
		}
	}

	public Double getHumidity() {
		return humidity;
	}
	
	public Double getTemperature() {
		return temperature;
	}
	
	/**
	 * Callable Future for reading sensor.  Allows timeout if it gets stuck.
	 */
    private class ReadSensorFuture implements Callable<byte[]>, Closeable {

    	private boolean keepRunning = true;
    	
    	public ReadSensorFuture() {
    		Gpio.pinMode(pinNumber, Gpio.OUTPUT);
       		Gpio.digitalWrite(pinNumber, Gpio.HIGH);
    	}
    	
		@Override
		public byte[] call() throws Exception {
	    	
	    	// do expensive (slow) stuff before we start and privoritize thread.  	
			byte[] data = new byte[5];
	        long startTime = System.nanoTime(); 
	        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

	    	sendStartSignal();
	    	waitForResponseSignal();
	    	for (int i = 0; i < 40; i++) {
	            while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.LOW) {
	        	}
	        	startTime = System.nanoTime();    
	            while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.HIGH) {
	            }
	            long timeHight = System.nanoTime() - startTime;
	            data[i / 8] <<= 1;
	            if ( timeHight > LONGEST_ZERO) {
	            	data[i / 8] |= 1;
	            }
	        }
	    	
	    	Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
	        return data;
		}
		
		private void sendStartSignal() {
	    	// Send start signal.
	    	Gpio.pinMode(pinNumber, Gpio.OUTPUT);
	        Gpio.digitalWrite(pinNumber, Gpio.LOW);
	        Gpio.delay(10);
	        Gpio.digitalWrite(pinNumber, Gpio.HIGH);
		}

	    /**
	     * AM2302 will pull low 80us as response signal, then 
	     * AM2302 pulls up 80us for preparation to send data.
	     */
	    private void waitForResponseSignal() {
	    	Gpio.pinMode(pinNumber, Gpio.INPUT);
	        while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.HIGH) {
	        }
	        while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.LOW) {
	    	}
	        while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.HIGH) {
	        }
	    }

		@Override
		public void close() throws IOException {
			keepRunning = false;
			
			// Set pin high for end of transmission.
			Gpio.pinMode(pinNumber, Gpio.OUTPUT);
	    	Gpio.digitalWrite(pinNumber, Gpio.HIGH);
		}
    }
    
    public class ParityCheckException extends IOException {
		private static final long serialVersionUID = 1L;
    }

}
