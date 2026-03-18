package com.app.gtw.service.filters.factory;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * Custom route-scoped {@link GatewayFilter} factory that logs a configurable message
 * and appends a cookie to every response that passes through the route it is applied to.
 *
 * <p>This factory is referenced in {@code application.yml} using the shortcut notation:
 * <pre>
 * filters:
 *   - ExampleCookie=Hello custom message, cookieName, cookieValue
 * </pre>
 *
 * <p>Or using the expanded form:
 * <pre>
 * filters:
 *   - name: ExampleCookie
 *     args:
 *       message: Hello custom message
 *       cookieName: user
 *       cookieValue: Brandon
 * </pre>
 *
 * <p>The factory name exposed to the YAML configuration is derived automatically by
 * removing the {@code GatewayFilterFactory} suffix from the class name, resulting in
 * {@code ExampleCookie}.
 *
 * @author Brandon
 * @version 1.0.0
 * @see AbstractGatewayFilterFactory
 */
@Component
public class ExampleCookieGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ExampleCookieGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(ExampleCookieGatewayFilterFactory.class);

    /**
     * Registers the {@link Config} class with the parent factory.
     */
    public ExampleCookieGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * Defines the argument order used by the YAML shortcut notation.
     *
     * <p>The position in this list maps directly to the positional arguments in the
     * shortcut string: {@code ExampleCookie=<message>, <cookieName>, <cookieValue>}.
     *
     * @return ordered list of config field names
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("message", "cookieName", "cookieValue");
    }

    /**
     * Creates and returns the {@link GatewayFilter} instance configured by the
     * provided {@link Config}.
     *
     * <p>The filter applies both a <b>pre</b> stage (logs the configured message)
     * and a <b>post</b> stage (adds the configured cookie to the response).
     *
     * @param config the configuration populated from the YAML route definition
     * @return the configured {@link GatewayFilter}
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Pre-filter: log the configured message
            log.info("Executing pre-filter [ExampleCookieGatewayFilterFactory] - message: {}", config.getMessage());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // Post-filter: add the configured cookie to the response
                log.info("Executing post-filter [ExampleCookieGatewayFilterFactory] - setting cookie: {}={}",
                        config.getCookieName(), config.getCookieValue());

                exchange.getResponse().getCookies().add(
                        config.getCookieName(),
                        ResponseCookie.from(config.getCookieName(), config.getCookieValue()).build()
                );
            }));
        };
    }

    /**
     * Configuration class that holds the parameters injected from the YAML route definition.
     */
    public static class Config {

        /** Message to log during the pre-filter stage. */
        private String message;

        /** Name of the cookie to be set in the response. */
        private String cookieName;

        /** Value of the cookie to be set in the response. */
        private String cookieValue;

        /**
         * Returns the log message.
         *
         * @return the configured message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the log message.
         *
         * @param message the message to log
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Returns the cookie name.
         *
         * @return the configured cookie name
         */
        public String getCookieName() {
            return cookieName;
        }

        /**
         * Sets the cookie name.
         *
         * @param cookieName the name of the cookie
         */
        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        /**
         * Returns the cookie value.
         *
         * @return the configured cookie value
         */
        public String getCookieValue() {
            return cookieValue;
        }

        /**
         * Sets the cookie value.
         *
         * @param cookieValue the value of the cookie
         */
        public void setCookieValue(String cookieValue) {
            this.cookieValue = cookieValue;
        }
    }
}
