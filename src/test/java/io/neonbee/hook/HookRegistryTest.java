package io.neonbee.hook;

import static com.google.common.truth.Truth.assertThat;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class HookRegistryTest {
    private final String correlID = "correl";

    @Test
    @Timeout(value = 2, timeUnit = TimeUnit.SECONDS)
    @DisplayName("Check that registerHooks instanciate class correct")
    void registerHooksSuccess(VertxTestContext testContext) {
        HookRegistry registry = new TestHookRegistry() {

            @Override
            public Future<Collection<HookRegistration>> registerInstanceHooks(Object instance, String correlationId) {
                testContext.verify(() -> {
                    assertThat(instance).isInstanceOf(Object.class);
                    assertThat(correlationId).isEqualTo(correlID);
                });
                return Future.succeededFuture();
            }
        };

        registry.registerHooks(Object.class, correlID).onComplete(testContext.succeedingThenComplete());
    }

    @Test
    @Timeout(value = 2, timeUnit = TimeUnit.SECONDS)
    @DisplayName("Check that registerHooks throw an error if no constructor is available")
    void registerHooksFails(VertxTestContext testContext)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        HookRegistry registry = new TestHookRegistry();

        registry.registerHooks(Void.class, correlID).onComplete(testContext.failing(t -> {
            testContext.verify(() -> assertThat(t).isInstanceOf(NoSuchMethodException.class));
            testContext.completeNow();
        }));
    }

    private static class TestHookRegistry implements HookRegistry {

        @Override
        public Future<Collection<HookRegistration>> registerInstanceHooks(Object instance, String correlationId) {
            return null;
        }

        @Override
        public Future<Collection<HookRegistration>> getHookRegistrations() {
            return null;
        }

        @Override
        public CompositeFuture executeHooks(HookType type, Map<String, Object> parameters) {
            return null;
        }
    }
}