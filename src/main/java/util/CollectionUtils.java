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
    
    public static <T> Set<T> nonNullAndDefaultEmpty(Set<T> set) {
        return nonNull(set, Collections::emptySet);
    }
    
    public static <T> List<T> nonNull(List<T> list) {
        return nonNull(list, LinkedList::new);
    }
    
    public static <T> Set<T> nonNull(Set<T> set) {
        return nonNull(set, HashSet::new);
    }
    
    public static <T> List<T> nonNull(List<T> list, Supplier<List<T>> supplier) {
        return list == null ? supplier.get() : list;
    }
    
    public static <T> Set<T> nonNull(Set<T> set, Supplier<Set<T>> supplier) {
        return set == null ? supplier.get() : set;
    }

}
