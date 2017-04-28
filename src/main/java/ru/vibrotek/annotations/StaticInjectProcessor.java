package ru.vibrotek.annotations;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SupportedAnnotationTypes({"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class StaticInjectProcessor extends AbstractProcessor {
    public static final String GENERATED_PACKAGE = "ru.vibrotek.inject.generated";
    private Map<Element, Injector> injectors = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(StaticInject.class)) {
            appendDependency(element.getEnclosingElement(), element);
        }
        injectors.values().forEach((injector) -> injector.createClass(processingEnv));
        return true;
    }

    private void appendDependency(Element owner, Element element) {
        injectors.computeIfAbsent(owner, Injector::new).add(element);
    }

    private static class Injector {
        private Element owner;
        private List<Element> dependencies = new ArrayList<>();
        private String createdClass;
        private String createdFile;
        private String ownerType;

        public Injector(Element owner) {
            this.owner = owner;
            createdClass = owner.getSimpleName() + "StaticInjector";
            createdFile = GENERATED_PACKAGE + "." + createdClass;
            ownerType = modifyType(owner.asType().toString());
        }

        private static String modifyType(String rawOwnerType) {
            Pattern pattern = Pattern.compile("(.*)<(.*)>(.*)");
            Matcher matcher = pattern.matcher(rawOwnerType);
            if (matcher.find()) {
                return matcher.group(1) + matcher.group(3);
            } else {
                return rawOwnerType;
            }
        }

        public void add(Element element) {
            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.size() != 2 || !modifiers.contains(Modifier.PUBLIC) || !modifiers.contains(Modifier.STATIC)) {
                throw new RuntimeException(
                        "Incorrect modifiers on field(required 'public static'): " + owner.asType().toString() + "." + element.toString());
            }
            dependencies.add(element);
        }

        public void createClass(ProcessingEnvironment processingEnv) {
            try {
                try {
                    JavaFileObject f = processingEnv.getFiler().createSourceFile(createdFile);
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "NOTE Creating " + f.toUri());
                    Writer w = f.openWriter();
                    try {
                        PrintWriter pw = new PrintWriter(w);
                        writeClass(createdClass, pw);
                        pw.flush();
                    } finally {
                        w.close();
                    }
                } catch (FilerException ignored) {
                }
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            }
        }

        private void writeClass(String createdClass, PrintWriter pw) {
            pw.print("package " + GENERATED_PACKAGE + ";\n" + "\n" + "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Component;\n" + "import javax.annotation.PostConstruct;\n" +
                    "import javax.annotation.PreDestroy;\n" + "\n" + "@Component\n" + "public class " + createdClass + " {\n");
            for (Element dependency : dependencies) {
                TypeMirror type = dependency.asType();
                pw.print("" + "    @Autowired\n" + "    public " + type + " " + dependency.getSimpleName() + ";\n");
            }
            pw.print("" + "    @PostConstruct\n" + "    private void init() {\n");
            for (Element dependency : dependencies) {
                pw.print("" + "        " + ownerType + "." + dependency.getSimpleName() + " = this." + dependency.getSimpleName() + ";\n");
            }
            pw.print("" + "    }\n" + "    @PreDestroy\n" + "    private void destroy() {\n");
            for (Element dependency : dependencies) {
                pw.print("" + "        " + ownerType + "." + dependency.getSimpleName() + " = null;\n");
            }
            pw.println("" + "    }\n" + "}");
        }
    }
}
