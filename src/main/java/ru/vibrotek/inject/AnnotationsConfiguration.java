package ru.vibrotek.inject;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.vibrotek.annotations.StaticInjectProcessor;


@Configuration
@ComponentScan(basePackages = StaticInjectProcessor.GENERATED_PACKAGE)
public class AnnotationsConfiguration {
}
