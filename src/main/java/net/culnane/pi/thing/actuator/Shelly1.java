package net.culnane.pi.thing.actuator;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Shelly 1 switch.
 *
 * @author Doug Culnane
 */
public class Shelly1 {

	/**
	 * Name of the actuator.
	 */
	private String hostname = "shelly1";

	private String username = null;

	private String password = null;

	private boolean on = false;

	public Shelly1(String hostname) {
		this(hostname, null, null);
	}

	public Shelly1(String hostname, String username, String password) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
	}

	public void on() throws IOException {

		URL url = new URL("http://" + hostname + "/relay/0?turn=on");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		if (username != null && password != null) {
			con.setRequestProperty("Authorization",
					"Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
		}

		int status = con.getResponseCode();
		if (status == 200) {
			on = true;
		} else {
			throw new IOException("Shelly1 + '" + hostname + "' failed to turn on.  Response: [" + status + "] "
					+ con.getResponseMessage());
		}
	}

	public void off() throws IOException {

		URL url = new URL("http://" + hostname + "/relay/0?turn=off");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");

		if (username != null && password != null) {
			con.setRequestProperty("Authorization",
					"Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
		}

		int status = con.getResponseCode();
		if (status == 200) {
			on = true;
		} else {
			throw new IOException("Shelly1 + '" + hostname + "' failed to turn on.  Response: [" + status + "] "
					+ con.getResponseMessage());
		}

	}

	public String getName() {
		return hostname;
	}

	public boolean isOn() {
		return on;
	}
}
