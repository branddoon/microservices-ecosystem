package com.app.gtw.service.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Global filter that runs for every route handled by the API Gateway.
 *
 * <p>Implements a <strong>pre</strong> and <strong>post</strong> processing pattern
 * using Project Reactor's {@link Mono}:
 * <ul>
 *   <li><b>Pre-filter:</b> Adds a {@code token} header to every incoming request
 *       before it is forwarded to the downstream service.</li>
 *   <li><b>Post-filter:</b> Propagates the {@code token} header value back in the
 *       response and appends a {@code color} cookie.</li>
 * </ul>
 *
 * <p>The execution order is controlled by {@link #getOrder()}, where lower values
 * indicate higher priority.
 *
 * @author Brandon
 * @version 1.0.0
 * @see GlobalFilter
 * @see Ordered
 */
@Component
public class ExampleGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ExampleGlobalFilter.class);

    /**
     * Executes the pre-filter logic (before forwarding the request) and the
     * post-filter logic (after the response is received from the downstream service).
     *
     * <p>Everything <em>before</em> {@code chain.filter(exchange)} is the <b>pre</b> stage.
     * Everything inside {@code Mono.fromRunnable(...)} is the <b>post</b> stage.
     *
     * @param exchange the current server exchange (request + response)
     * @param chain    the gateway filter chain used to invoke the next filter
     * @return a {@link Mono} that signals when the request/response processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Executing pre-filter: {}", getClass().getSimpleName());

        // Pre-filter: inject a token header into the upstream request
        exchange.getRequest().mutate().headers(headers -> headers.add("token", "123456"));

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Executing post-filter: {}", getClass().getSimpleName());

            // Post-filter: propagate the token header value back in the response
            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token"))
                    .ifPresent(value -> exchange.getResponse().getHeaders().add("token", value));

            // Post-filter: append a color cookie to the response
            exchange.getResponse().getCookies().add(
                    "color",
                    ResponseCookie.from("color", "blue").build()
            );
        }));
    }

    /**
     * Returns the execution order of this filter within the filter chain.
     *
     * @return {@code 1} — runs early but after built-in Gateway filters
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
