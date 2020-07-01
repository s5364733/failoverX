/**
 * The MIT License
 *
 * Copyright for portions of failover-safe are held by creatorchina Inc (c) 2020.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.creatorchina.core;

import com.creatorchina.util.Utils;
import net.jodah.failsafe.CircuitBreaker;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jack
 * @date 2020-6-15
 * <BR>
 * Configuration for circuitBreaker<p>
 * A circuit breaker can and should be shared across code that accesses common dependencies.
 * This ensures that if the circuit breaker is opened, all executions that share the same
 * dependency and use the same circuit breaker will be blocked until the circuit is closed again.
 * For example, if multiple connections or requests are made to the same external server,typically they should all go through the same circuit breaker.
 * <p/>
 * <BR/>
 */
public class CircuitBreakerRegistry {

    private static final String ALREADY_REGISTERED_ERROR = "There was a Circuit-Breaker registered already with name : %s ";

    private final Map<String, CircuitBreaker> concurrentBreakerMap = new ConcurrentHashMap<>();

    void registerCircuitBreaker(final CircuitBreaker breaker, final String name) {
        Utils.hasText(name, "Name for circuit breaker needs to be set");
        Utils.notNull(breaker, "Circuit breaker to add, can't be null");

        final CircuitBreaker replaced = concurrentBreakerMap.put(name, breaker);
        Utils.isNull(replaced, String.format(ALREADY_REGISTERED_ERROR, name));
    }

    /**
     * Returns the {@link Map} with registered circuit breakers.
     *
     * @return returns the referenced {@link Map}
     */
    public Map<String, CircuitBreaker> getConcurrentBreakerMap() {
        return concurrentBreakerMap;
    }


    private CircuitBreaker createCircuitBreaker(final String identifier) {
        final CircuitBreaker breaker = new CircuitBreaker();
        registerCircuitBreaker(breaker, identifier);
        return breaker;
    }


    public CircuitBreaker getOrCreate(final String identifier) {
        Utils.hasText(identifier, "Identifier for circuit breaker needs to be set");
        final CircuitBreaker circuitBreaker = concurrentBreakerMap.get(identifier);
        if (circuitBreaker != null) {
            return circuitBreaker;
        }
        return createCircuitBreaker(identifier);
    }

    @PreDestroy
    public void destroy() throws Exception {
        concurrentBreakerMap.clear();
    }
}
