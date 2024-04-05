package cn.cover.database.parser.mysql.visitor.dm.support;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class BooleanConsumer<T, C> {

    private Predicate<T> predicate;
    private T t;

    private BooleanConsumer() {
    }

    private BooleanConsumer(Predicate<T> predicate, T t) {
        this.predicate = predicate;
        this.t = t;
    }

    public void accept(Consumer<C> trueAction, Consumer<C> falseAction, C c) {
        if (predicate.test(t)) {
            trueAction.accept(c);
        } else {
            falseAction.accept(c);
        }
    }

   public static <T,C> BooleanConsumer<T,C> newInstance(Predicate<T> predicate, T t) {
        return new BooleanConsumer<>(predicate, t);
   }

    public static<C> BooleanConsumer<Boolean,C> newInstance(Boolean b) {
        Predicate<Boolean> pre = aBoolean -> aBoolean;
        return new BooleanConsumer<>(pre, b);
    }
}
