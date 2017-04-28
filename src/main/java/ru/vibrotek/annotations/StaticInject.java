package ru.vibrotek.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Используется для inject-а зависимостей вне контекста Spring.
 * <br>Для $(className) и $(staticField) порождает:
 * <br>@Component
 * <br>public class $(className)StaticInject {
 * <br>
 * <br>     @Autoware
 * <br>     public $(field);
 * <br>     ...
 * <br>
 * <br>     @PostConstruct
 * <br>     private void init() {
 * <br>         $(className).$(staticField) = this.$(field);
 * <br>         ...
 * <br>     }
 * <br>
 * <br>     @PreDestroy
 * <br>     private void destroy() {
 * <br>         $(className).$(staticField) = null;
 * <br>         ...
 * <br>     }
 * <br>}
 * <br>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface StaticInject {
}