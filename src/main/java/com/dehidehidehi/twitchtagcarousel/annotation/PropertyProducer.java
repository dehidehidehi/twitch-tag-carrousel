package com.dehidehidehi.twitchtagcarousel.annotation;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Producer for property value injection using the {@link Property} annotation.
 */
@ApplicationScoped
public class PropertyProducer {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyProducer.class);

	private static final String USER_PROPERTIES = "/user.properties";
	private final String APPLICATION_PROPERTIES = "/application.properties";
	private Properties applicationProperties;
	private Properties userProperties;

	/**
	 * Loads the {@link PropertyProducer#APPLICATION_PROPERTIES} contents into the {@link Properties} field.
	 */
	@SneakyThrows
	@PostConstruct
	public void init() {
		// load application properties
		this.applicationProperties = new Properties();
		final InputStream applicationPropertiesInputStream = PropertyProducer.class.getResourceAsStream(APPLICATION_PROPERTIES);
		loadPropertiesFile(applicationProperties, applicationPropertiesInputStream);
		
		// load user properties
		this.userProperties = new Properties();
		final File userPropertiesFile = new File(getDirPathOfThisJar() + USER_PROPERTIES);
		final FileInputStream fileInputStream = new FileInputStream(userPropertiesFile);
		loadPropertiesFile(userProperties, fileInputStream);
	}

	@Property
	@Produces
	public String produceString(final InjectionPoint ip) {
		return findProperty(ip);
	}

	@Property
	@Produces
	public int produceInt(final InjectionPoint ip) {
		return Integer.parseInt(findProperty(ip));
	}

	@Property
	@Produces
	public boolean produceBoolean(final InjectionPoint ip) {
		return Boolean.parseBoolean(findProperty(ip));
	}

	/**
	 * Convenience method for getting the absolute path of where the .jar file will be deployed.
	 */
	@SneakyThrows
	private File getDirPathOfThisJar() {
		final String jarLocation = new File(PropertyProducer.class
																.getProtectionDomain()
																.getCodeSource()
																.getLocation()
																.toURI()
																.getPath()).getParent();
		return new File(jarLocation);
	}

	private void loadPropertiesFile(Properties properties, InputStream inputStream) {
		final Consumer<InputStream> tryReadProperties = propertiesStream -> {
			try {
				properties.load(propertiesStream);
			} catch (final IOException e) {
				throw new IllegalStateException("Configuration could not be loaded!");
			}
		};
		tryReadProperties.accept(inputStream);
	}

	/**
	 * Looks for property first in {@link #APPLICATION_PROPERTIES}, then in {@link #USER_PROPERTIES}
	 */
	private String findProperty(final InjectionPoint ip) {
		// first check app.properties
		final String property = this.applicationProperties.getProperty(getKey(ip));
		if (property == null) {
			LOGGER.trace("Did not find property {} in {}", getKey(ip), APPLICATION_PROPERTIES);
			LOGGER.trace("Checking {}", USER_PROPERTIES);
			// else check user.properties
			return this.userProperties.getProperty(getKey(ip));
		}
		return property;
	}

	/**
	 * Returns the property value, or if not found, the name of the path to that property.
	 */
	private String getKey(final InjectionPoint ip) {
		final boolean isIpPropertyPresent = ip.getAnnotated().isAnnotationPresent(Property.class);
		final BooleanSupplier isIpPropertyValueEmpty = () -> ip.getAnnotated().getAnnotation(Property.class).value().isEmpty();
		return (isIpPropertyPresent && !isIpPropertyValueEmpty.getAsBoolean()) ? ip
				.getAnnotated()
				.getAnnotation(Property.class)
				.value() : ip.getMember().getName();
	}
}