package com.dehidehidehi.twitchtagcarousel.dao.impl;
import com.dehidehidehi.twitchtagcarousel.dao.UserPropertiesDao;
import com.dehidehidehi.twitchtagcarousel.domain.TwitchTag;
import com.dehidehidehi.twitchtagcarousel.util.CDIExtension;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.dehidehidehi.twitchtagcarousel.dao.impl.UserPropertiesDaoImpl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(CDIExtension.class)
class UserPropertiesDaoImplTest {

    private final String defaultMandatoryTags = "vtuber,envtuber,cool";
    private final String defaultRotatingTags = "chatting,eating,relaxed";

    @Inject
    private UserPropertiesDao userPropertiesDao;

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        setUpPropertiesFileWithDefaultValues();
    }

    private void setUpPropertiesFileWithDefaultValues() throws URISyntaxException, IOException {
        final URI fileUri = PrivateUserPropertiesDaoImpl.class.getResource(USER_PROPERTIES_FILE).toURI();
        final File propertiesFile = new File(fileUri);
        propertiesFile.createNewFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile)) {
            final String mandatoryTags = "%s=%s".formatted(PROPERTY_MANDATORY_TAGS, defaultMandatoryTags);
            final String rotatingTags = "%s=%s".formatted(PROPERTY_ROTATING_TAGS, defaultRotatingTags);
            final String properties = String.join(System.lineSeparator(), mandatoryTags, rotatingTags);
            fileOutputStream.write(properties.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Order(1)
    @Test
    void getMandatoryTagsShouldReturnMandatoryTagsFromPropertiesFile() {
        assertThatCode(() -> userPropertiesDao.getMandatoryTags()).doesNotThrowAnyException();
        final List<String> expected = Arrays.stream(defaultMandatoryTags.split(",")).toList();
        final List<String> result = userPropertiesDao.getMandatoryTags().stream().map(TwitchTag::toString).toList();
        assertThat(result)
                .isNotNull()
                .containsAll(expected);
    }

    @Order(1)
    @Test
    void getRotatingTagsShouldReturnRotatingTagsFromPropertiesFile() {
        assertThatCode(() -> userPropertiesDao.getRotatingTags()).doesNotThrowAnyException();
        final List<String> expected = Arrays.stream(defaultRotatingTags.split(",")).toList();
        final List<String> result = userPropertiesDao.getRotatingTags().stream().map(TwitchTag::toString).toList();
        assertThat(result)
                .isNotNull()
                .containsAll(expected);
    }

    @Test
    void setMandatoryTagsShouldOverwriteInPropertiesFile() {
        final List<TwitchTag> newMandatoryTags = List.of(
                new TwitchTag("english"),
                new TwitchTag("uk"),
                new TwitchTag("supercool")
        );
        userPropertiesDao.saveMandatoryTags(newMandatoryTags);
        final String expected = newMandatoryTags.stream().map(TwitchTag::toString).collect(Collectors.joining(","));
        final String result = userPropertiesDao.getMandatoryTags().stream().map(TwitchTag::toString).collect(Collectors.joining(","));
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void setRotatingTagsShouldOverwriteInPropertiesFile() {
        final List<TwitchTag> newRotatingTags = List.of(
                new TwitchTag("italian"),
                new TwitchTag("it"),
                new TwitchTag("pizza")
        );
        userPropertiesDao.saveRotatingTags(newRotatingTags);
        final String expected = newRotatingTags.stream().map(TwitchTag::toString).collect(Collectors.joining(","));
        final String result = userPropertiesDao.getRotatingTags().stream().map(TwitchTag::toString).collect(Collectors.joining(","));
        assertThat(result).isEqualTo(expected);
    }
}
