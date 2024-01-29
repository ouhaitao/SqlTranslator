package util;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author parry 2024/01/22
 */
public class CollectionUtils {
    
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    public static <T> List<T> nonNullAndDefaultEmpty(List<T> list) {
        return nonNull(list, Collections::emptyList);
    }
    
    public static <T> List<T> nonNull(List<T> list) {
        return nonNull(list, LinkedList::new);
    }
    
    public static <T> List<T> nonNull(List<T> list, Supplier<List<T>> supplier) {
        return Optional.ofNullable(list).orElseGet(supplier);
    }
}
