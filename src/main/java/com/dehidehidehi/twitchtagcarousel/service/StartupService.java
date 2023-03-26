package com.dehidehidehi.twitchtagcarousel.service;

import com.dehidehidehi.twitchtagcarousel.annotation.Property;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Orchestrates startup of application.
 */
@ApplicationScoped
public class StartupService {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartupService.class);
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	@Inject
	@Property("twitch-app.start-delay-seconds")
	private int startDelaySeconds;

	@Inject @Property("twitch-app.tag-rotation-frequency-seconds")
	private int tagRotationFrequencySeconds;

	private final TagRotatorService tagRotatorService;

	@Inject
	public StartupService(final TagRotatorService tagRotatorService) {
		this.tagRotatorService = tagRotatorService;
	}

	/**
	 * Executes tag updater at a specified frequency.
	 */
	public void start() {
		LOGGER.debug("Scheduling execution of app every {} {}.", tagRotationFrequencySeconds, TIME_UNIT);
		LOGGER.info(getBanner());
		LOGGER.info("First execution of Twitch Tag rotator will start in {} {}", startDelaySeconds, TIME_UNIT);
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(tagRotatorService::updateTags, startDelaySeconds, tagRotationFrequencySeconds, TIME_UNIT);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LOGGER.debug("Shutting down executor service.");
			executorService.shutdown();
		}));

	}

	private String getBanner() {
		return """
                 
                 _____              ____                                    _\s
                |_   _|_ _  __ _   / ___|__ _ _ __ _ __ ___  _   _ ___  ___| |
                  | |/ _` |/ _` | | |   / _` | '__| '__/ _ \\| | | / __|/ _ \\ |
                  | | (_| | (_| | | |__| (_| | |  | | | (_) | |_| \\__ \\  __/ |
                  |_|\\__,_|\\__, |  \\____\\__,_|_|  |_|  \\___/ \\__,_|___/\\___|_|
                           |___/""";
	}
}