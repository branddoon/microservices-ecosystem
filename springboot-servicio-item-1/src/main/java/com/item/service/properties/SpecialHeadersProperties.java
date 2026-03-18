package com.item.service.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for special HTTP headers passed between microservices.
 *
 * <p>Properties are bound from the {@code special-headers} prefix in
 * {@code application.yml} or the remote Config Server. Add fields here as
 * inter-service header requirements grow.</p>
 *
 * <p>Example configuration:</p>
 * <pre>{@code
 * special-headers:
 *   token-name: token-request
 * }</pre>
 */
@Component
@ConfigurationProperties(prefix = "special-headers")
public class SpecialHeadersProperties {

	/**
	 * The name of the custom request token header forwarded between services.
	 * Defaults to {@code token-request}.
	 */
	private String tokenName = "token-request";

	/**
	 * Returns the name of the custom request token header.
	 *
	 * @return the token header name
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * Sets the name of the custom request token header.
	 *
	 * @param tokenName the token header name
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
}
