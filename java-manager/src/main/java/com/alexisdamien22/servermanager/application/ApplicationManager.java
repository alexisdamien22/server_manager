package com.alexisdamien22.servermanager.application;

import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.alexisdamien22.servermanager.config.ManagerConfig;
import com.alexisdamien22.servermanager.console.ConsoleReader;

import java.io.IOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

public class ApplicationManager {

	private final Map<String, ApplicationConfig>
        configuredApplications =
        new ConcurrentHashMap<>();

    private final Map<String, ManagedApplication>
            runningApplications =
            new ConcurrentHashMap<>();

    private final ApplicationLauncher launcher =
            new ApplicationLauncher();
	
	public ApplicationManager(
		ManagerConfig config) {

		for (ApplicationConfig application :
				config.getApplications()) {

			configuredApplications.put(
					application.getId(),
					application
			);
		}
	}

    public synchronized ManagedApplication startApplication(
        ApplicationConfig config)
        throws IOException {

		System.out.println(
			"Attempting to start: "
			+ config.getId()
		);

		if (isRunning(config.getId())) {

			System.out.println(
				"Already running: "
				+ config.getId()
			);

			return runningApplications.get(
				config.getId()
			);
		}

		Process process =
			launcher.launch(config);

		System.out.println(
			"Process started for: "
			+ config.getId()
		);

		Thread consoleThread =
				new Thread(
					new ConsoleReader(
						config.getId(),
						process.getInputStream()
					)
				);

		consoleThread.setDaemon(true);

		consoleThread.start();

		ManagedApplication application =
			new ManagedApplication(
				config,
				process
			);

		runningApplications.put(
			config.getId(),
			application
		);

		return application;
	}

	public ManagedApplication startApplication(
        String id)
		throws IOException {

		System.out.println(
			"Start requested: " + id
		);

		System.out.println(
			"Configured applications: "
			+ configuredApplications.size()
		);

		ApplicationConfig config =
			configuredApplications.get(id);

		if (config == null) {

			System.out.println(
				"Application not found: " + id
			);

			throw new IllegalArgumentException(
				"Unknown application: " + id
			);
		}

		return startApplication(config);
	}

    public boolean stopApplication(
        String id) {

		System.out.println(
			"Stop requested: " + id
		);

		ManagedApplication application =
			runningApplications.get(id);

		if (application == null) {

			System.out.println(
				"Application not running: "
				+ id
			);

			return false;
		}

		Process process =
			application.getProcess();

		process.destroy();

		runningApplications.remove(id);

		System.out.println(
			"Stopped application: "
			+ id
		);

		System.out.println(
			"Running applications: "
			+ runningApplications.size()
		);

		return true;
	}

    public boolean isRunning(
            String id) {

        ManagedApplication application =
            runningApplications.get(id);

        if (application == null) {
            return false;
        }

        if (!application.isRunning()) {

            runningApplications.remove(id);

            return false;
        }

        return true;
    }

    public ManagedApplication getApplication(
            String id) {

		return runningApplications.get(id);
	}

	public int getRunningCount() {

		return runningApplications.size();
	}

	public Collection<ManagedApplication>
		getRunningApplications() {

		return runningApplications.values();
	}

	public Collection<ApplicationConfig>
        getConfiguredApplications() {

		return configuredApplications.values();
	}
}