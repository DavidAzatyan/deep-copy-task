package DeepCopyTask;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class DeepCopyUtils {
    public static <T> T deepCopy(T original) {
        Map<Object, Object> visited = new IdentityHashMap<>();
        return deepCopy(original, visited);
    }

    @SuppressWarnings("unchecked")
    private static <T> T deepCopy(T original, Map<Object, Object> visited) {
        if (original == null) {
            return null;
        }

        if (visited.containsKey(original)) {
            return (T) visited.get(original);
        }

        Class<?> clazz = original.getClass();

        // Handle arrays
        if (clazz.isArray()) {
            int length = Array.getLength(original);
            Object copy = Array.newInstance(clazz.getComponentType(), length);
            visited.put(original, copy);
            for (int i = 0; i < length; i++) {
                Array.set(copy, i, deepCopy(Array.get(original, i), visited));
            }
            return (T) copy;
        }

        // Handle collections
        if (original instanceof Collection) {
            Collection<?> originalCollection = (Collection<?>) original;
            Collection copyCollection = createCollectionInstance(originalCollection);
            visited.put(original, copyCollection);
            for (Object item : originalCollection) {
                copyCollection.add(deepCopy(item, visited));
            }
            return (T) copyCollection;
        }

        // Handle maps
        if (original instanceof Map) {
            Map<?, ?> originalMap = (Map<?, ?>) original;
            Map copyMap = createMapInstance(originalMap);
            visited.put(original, copyMap);
            for (Map.Entry<?, ?> entry : originalMap.entrySet()) {
                Object copiedKey = deepCopy(entry.getKey(), visited);
                Object copiedValue = deepCopy(entry.getValue(), visited);
                copyMap.put(copiedKey, copiedValue);
            }
            return (T) copyMap;
        }

        // Handle immutables (e.g., String, Integer, etc.)
        if (clazz.isPrimitive() || clazz == String.class || isWrapperType(clazz)) {
            return original;
        }

        // Handle complex objects
        try {
            T copy = (T) clazz.getDeclaredConstructor().newInstance();
            visited.put(original, copy);
            for (Field field : getAllFields(clazz)) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Object fieldValue = field.get(original);
                    field.set(copy, deepCopy(fieldValue, visited));
                }
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deep copy the object", e);
        }
    }

    private static Collection<?> createCollectionInstance(Collection<?> original) {
        if (original instanceof List) {
            return new ArrayList<>();
        }
        if (original instanceof Set) {
            return new HashSet<>();
        }
        if (original instanceof Queue) {
            return new LinkedList<>();
        }
        throw new UnsupportedOperationException("Unsupported collection type: " + original.getClass());
    }

    private static Map<?, ?> createMapInstance(Map<?, ?> original) {
        if (original instanceof HashMap) {
            return new HashMap<>();
        }
        if (original instanceof LinkedHashMap) {
            return new LinkedHashMap<>();
        }
        if (original instanceof TreeMap) {
            return new TreeMap<>();
        }
        throw new UnsupportedOperationException("Unsupported map type: " + original.getClass());
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Integer.class || clazz == Long.class || clazz == Short.class ||
                clazz == Byte.class || clazz == Boolean.class || clazz == Character.class ||
                clazz == Float.class || clazz == Double.class || clazz == Void.class;
    }
}
