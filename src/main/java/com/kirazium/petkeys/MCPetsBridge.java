package com.kirazium.petkeys;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reflection keeps this addon independent from a specific MCPets JAR build.
 * Both the current multi-pet API and the older single-pet API are supported.
 */
final class MCPetsBridge {

    private static final String API_CLASS = "fr.nocsy.mcpets.api.MCPetsAPI";

    private final Method getActivePets;
    private final boolean returnsCollection;
    private final Logger logger;
    private final ConcurrentHashMap<Class<?>, PetMethods> methodCache = new ConcurrentHashMap<>();
    private final Set<String> reportedFailures = ConcurrentHashMap.newKeySet();

    private MCPetsBridge(
            final Method getActivePets,
            final boolean returnsCollection,
            final Logger logger
    ) {
        this.getActivePets = getActivePets;
        this.returnsCollection = returnsCollection;
        this.logger = logger;
    }

    static MCPetsBridge create(final Logger logger) throws ReflectiveOperationException {
        final Class<?> api = Class.forName(API_CLASS);
        try {
            return new MCPetsBridge(
                    api.getMethod("getActivePetsForPlayer", UUID.class),
                    true,
                    logger
            );
        } catch (NoSuchMethodException ignored) {
            return new MCPetsBridge(
                    api.getMethod("getActivePet", UUID.class),
                    false,
                    logger
            );
        }
    }

    boolean castFirstAvailableSignal(final UUID playerId) {
        try {
            for (final Object pet : activePets(playerId)) {
                if (pet == null) {
                    continue;
                }

                final PetMethods methods = methodsFor(pet.getClass());
                if (!methods.isStillHere(pet)) {
                    continue;
                }

                final String signal = methods.firstSignal(pet);
                if (signal == null) {
                    continue;
                }

                methods.sendSignal(pet, signal);
                return true;
            }
        } catch (ReflectiveOperationException | RuntimeException exception) {
            reportOnce(exception);
        }
        return false;
    }

    private List<?> activePets(final UUID playerId)
            throws InvocationTargetException, IllegalAccessException {
        final Object result = getActivePets.invoke(null, playerId);
        if (result == null) {
            return Collections.emptyList();
        }
        if (returnsCollection) {
            if (result instanceof List<?> list) {
                return list;
            }
            throw new IllegalStateException("MCPets aktif pet sonucu liste degil: "
                    + result.getClass().getName());
        }
        return Collections.singletonList(result);
    }

    private PetMethods methodsFor(final Class<?> petClass) throws NoSuchMethodException {
        final PetMethods cached = methodCache.get(petClass);
        if (cached != null) {
            return cached;
        }

        final Method getSignals = petClass.getMethod("getSignals");
        final Method sendSignal = petClass.getMethod("sendSignal", String.class);
        Method isStillHere = null;
        try {
            isStillHere = petClass.getMethod("isStillHere");
        } catch (NoSuchMethodException ignored) {
            // Older MCPets builds do not require this guard.
        }

        final PetMethods discovered = new PetMethods(getSignals, sendSignal, isStillHere);
        final PetMethods raced = methodCache.putIfAbsent(petClass, discovered);
        return raced == null ? discovered : raced;
    }

    private void reportOnce(final Exception exception) {
        final Throwable cause = exception instanceof InvocationTargetException invocation
                && invocation.getCause() != null
                ? invocation.getCause()
                : exception;
        final String key = cause.getClass().getName() + ':' + String.valueOf(cause.getMessage());
        if (reportedFailures.add(key)) {
            logger.log(Level.WARNING,
                    "F tusuyla pet yetenegi calistirilamadi; normal el degistirme korundu.",
                    cause);
        }
    }

    private record PetMethods(Method getSignals, Method sendSignal, Method isStillHere) {

        boolean isStillHere(final Object pet)
                throws InvocationTargetException, IllegalAccessException {
            if (isStillHere == null) {
                return true;
            }
            final Object result = isStillHere.invoke(pet);
            return !(result instanceof Boolean value) || value;
        }

        String firstSignal(final Object pet)
                throws InvocationTargetException, IllegalAccessException {
            final Object result = getSignals.invoke(pet);
            if (!(result instanceof Iterable<?> signals)) {
                return null;
            }
            for (final Object entry : signals) {
                if (entry == null) {
                    continue;
                }
                final String signal = entry.toString().trim();
                if (!signal.isEmpty()) {
                    return signal;
                }
            }
            return null;
        }

        void sendSignal(final Object pet, final String signal)
                throws InvocationTargetException, IllegalAccessException {
            sendSignal.invoke(pet, signal);
        }
    }
}
