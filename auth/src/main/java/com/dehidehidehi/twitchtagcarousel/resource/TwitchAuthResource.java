package com.dehidehidehi.twitchtagcarousel.resource;

import com.dehidehidehi.twitchtagcarousel.service.impl.TwitchAuthJakartaWebServerService;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A simple application which listens for oAuth callbacks from Twitch.
 * Should NOT be a CDI bean because we don't want this instantiated at CDI discovery.
 */
@Produces(MediaType.TEXT_HTML)
@Path("/")
public class TwitchAuthResource extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchAuthResource.class);

    private final Set<Class<?>> classes;

    private final TwitchAuthJakartaWebServerService twitchAuthJakartaWebServerService;

    public TwitchAuthResource(TwitchAuthJakartaWebServerService twitchAuthJakartaWebServerService) {
        classes = Set.of(TwitchAuthResource.class);
        this.twitchAuthJakartaWebServerService = twitchAuthJakartaWebServerService;
    }

    /**
     * Saves the access token locally then displays a `success` html page.
     */
    @Path("token")
    @GET
    public Response handleAuthToken(@QueryParam("access_token") @Nullable final String accessToken) throws URISyntaxException {
        LOGGER.debug("Entrée dans la méthode TwitchAuthResource.handleAuthToken");
        final String hiddenAccessToken = Optional.ofNullable(accessToken)
                                  .map(s -> "%s****".formatted(s.substring(0, 5)))
                                  .orElse("No access token received.");
        LOGGER.debug("accessToken={}", hiddenAccessToken);
        Optional
                .ofNullable(accessToken)
                .ifPresentOrElse(twitchAuthJakartaWebServerService::receiveAccessToken,
                                 () -> {throw new IllegalStateException("Received empty access_token!");});
        final URI page = Objects.requireNonNull(getClass().getResource("/WEB-INF/html/token_received.html")).toURI();
        final File file = new File(page);
        return Response.ok(file).build();
    }

    /**
     * Given Twitch send the auth token AFTER the fragment part of the URL (the # symbol),
     * a server cannot access the token value.<br>
     * However, redirecting the request to an HTML page with javascript, which CAN get that value, solves the issue.
     */
    @Path("token-redirect")
    @GET
    public Response handleAuthTokenCallBack() throws URISyntaxException {
        LOGGER.debug("Serving static html page which captures URL fragments then redirects the user.");
        final URI page = Objects.requireNonNull(getClass().getResource("/WEB-INF/html/token_redirect.html")).toURI();
        final File file = new File(page);
        return Response.ok(file).build();
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
