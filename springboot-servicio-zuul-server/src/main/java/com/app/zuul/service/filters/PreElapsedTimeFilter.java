package com.app.zuul.service.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Pre-routing global filter that records the start time of each incoming request.
 *
 * <p>This filter is executed <em>before</em> the request is forwarded to any downstream
 * service. It stores the current epoch timestamp (in milliseconds) in the exchange's
 * attribute map under the key {@link #START_TIME_ATTR}, making it available to downstream
 * filters — in particular {@link PostElapsedTimeFilter} — for elapsed-time calculation.</p>
 *
 * <p>It also logs the HTTP method and request path at {@code INFO} level, providing a
 * lightweight audit trail of all routed requests.</p>
 *
 * <p>Migration note: this class replaces {@code PreTiempoTransacurridoFilter}, which extended
 * the now-removed {@code com.netflix.zuul.ZuulFilter}. The reactive {@link GlobalFilter}
 * interface is the Spring Cloud Gateway equivalent.</p>
 *
 * @author formacionbdi
 * @version 2.0.0
 * @see PostElapsedTimeFilter
 */
@Component
public class PreElapsedTimeFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(PreElapsedTimeFilter.class);

    /**
     * Exchange attribute key used to share the request start time with post-filters.
     * The value stored under this key is a {@link Long} representing milliseconds since epoch.
     */
    public static final String START_TIME_ATTR = "startTime";

    /**
     * Records the incoming request details and stores the start timestamp, then delegates
     * to the next filter in the chain.
     *
     * @param exchange the current server web exchange containing request and response
     * @param chain    the gateway filter chain; calling {@code chain.filter(exchange)}
     *                 forwards the request to the next filter or the downstream service
     * @return a {@link Mono}{@code <Void>} that signals completion of the filter execution
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("{} request routed to {}", request.getMethod(), request.getPath());
        exchange.getAttributes().put(START_TIME_ATTR, System.currentTimeMillis());
        return chain.filter(exchange);
    }

    /**
     * Returns the execution order of this filter within the global filter chain.
     * A lower value indicates higher priority (executed earlier).
     *
     * @return {@code 1}, placing this filter early in the chain
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
