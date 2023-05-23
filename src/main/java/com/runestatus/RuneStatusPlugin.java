package com.runestatus;

import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@PluginDescriptor(
	name = "RuneStatus"
)

// TODO: If handle server sends too much data, reduce bits for testing (Will use faster JSON method to capture data). Don't want to slow down RuneLite.

public class RuneStatusPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private RuneStatusConfig config;

	@Inject
	private ConfigManager configManager;

	private JButton toggleButton;
	private boolean isOn = false;

	@Override
	protected void startUp() throws Exception
	{
		toggleButton = new JButton();
		toggleButton.setText("Toggle");
		toggleButton.addActionListener(e -> toggle());
		toggleButton.setBackground(Color.RED);
		JPanel panel = new JPanel();
		panel.add(toggleButton);
		// More logic for adding this panel to your plugin's interface.
	}

	private void toggle()
	{
		isOn = !isOn;
		toggleButton.setBackground(isOn ? Color.GREEN : Color.RED);
		sendPostRequest(isOn);
	}

	private void sendPostRequest(boolean isOn)
	{
		try
		{
			URL url = new URL("https://RuneStatus.com/api/toggle");
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("POST");
			http.setDoOutput(true);
			byte[] out = ("{\"toggle\":" + isOn + "}").getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			http.setFixedLengthStreamingMode(length);
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.connect();
			try(OutputStream os = http.getOutputStream()) {
				os.write(out);
			}
			// TODO: Handle server response if necessary
		}
		catch (Exception e)
		{
			// TODO: Handle exception
		}
	}
}