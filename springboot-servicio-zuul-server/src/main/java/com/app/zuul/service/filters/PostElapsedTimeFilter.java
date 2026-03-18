package com.app.zuul.service.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Post-routing global filter that calculates and logs the total elapsed time for each request.
 *
 * <p>This filter is executed <em>after</em> the downstream service has returned a response.
 * It reads the start timestamp stored by {@link PreElapsedTimeFilter} from the exchange
 * attributes and computes the elapsed time, logging it in both seconds and milliseconds.</p>
 *
 * <p>Post-processing is scheduled via {@link Mono#then(Mono)}, which runs the logging logic
 * after the reactive pipeline (including the downstream response) has completed. This is the
 * idiomatic Spring Cloud Gateway approach and replaces the Zuul {@code "post"} filter type.</p>
 *
 * <p><strong>Bug fix:</strong> the original Zuul implementation computed elapsed time as
 * {@code startTime - endTime}, which produced a negative result. This implementation
 * correctly computes {@code endTime - startTime}.</p>
 *
 * <p>Migration note: this class replaces {@code PostTiempoTransacurridoFilter}, which extended
 * the now-removed {@code com.netflix.zuul.ZuulFilter}.</p>
 *
 * @author formacionbdi
 * @version 2.0.0
 * @see PreElapsedTimeFilter
 */
@Component
public class PostElapsedTimeFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(PostElapsedTimeFilter.class);

    /**
     * Delegates to the next filter in the chain and, once the downstream response is complete,
     * calculates and logs the total request elapsed time.
     *
     * @param exchange the current server web exchange containing request and response
     * @param chain    the gateway filter chain; calling {@code chain.filter(exchange)}
     *                 forwards the request and returns a {@link Mono} that completes
     *                 once the full response has been sent to the client
     * @return a {@link Mono}{@code <Void>} that completes after the elapsed time is logged
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("Entering post filter");
            Long startTime = exchange.getAttribute(PreElapsedTimeFilter.START_TIME_ATTR);
            if (startTime != null) {
                long elapsedMs = System.currentTimeMillis() - startTime;
                log.info("Elapsed time: {} s.", elapsedMs / 1000.0);
                log.info("Elapsed time: {} ms.", elapsedMs);
            }
        }));
    }

    /**
     * Returns the execution order of this filter within the global filter chain.
     * A lower value indicates higher priority (executed earlier).
     *
     * @return {@code 1}, matching the order of {@link PreElapsedTimeFilter}
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
