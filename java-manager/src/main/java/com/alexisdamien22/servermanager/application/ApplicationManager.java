package com.alexisdamien22.servermanager.application;

import com.alexisdamien22.servermanager.config.ApplicationConfig;
import com.alexisdamien22.servermanager.config.ManagerConfig;
import com.alexisdamien22.servermanager.console.ConsoleManager;
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

	private final ConsoleManager consoleManager =
        new ConsoleManager();

	public ConsoleManager getConsoleManager() {
		return consoleManager;
	}
	
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

		if (isRunning(config.getId())) {
			return runningApplications.get(
				config.getId()
			);
		}

		Process process =
			launcher.launch(config);

		Thread consoleThread =
			new Thread(
				new ConsoleReader(
					config.getId(),
					process.getInputStream(),
					consoleManager
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

		ApplicationConfig config =
			configuredApplications.get(id);

		if (config == null) {


			throw new IllegalArgumentException(
				"Unknown application: " + id
			);
		}

		return startApplication(config);
	}

    public boolean stopApplication(
        String id) {

		ManagedApplication application =
			runningApplications.get(id);

		if (application == null) {

			return false;
		}

		Process process =
			application.getProcess();

		process.destroy();

		runningApplications.remove(id);

		return true;
	}

	public ManagedApplication restartApplication(
        String id)
        throws IOException {

		stopApplication(id);

		return startApplication(id);
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