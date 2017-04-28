# staticinject
The library generates a special code to load and use Spring Beans

pom.xml
...
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.5</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgument>-Xlint:unchecked</compilerArgument>
        <annotationProcessors>
            <annotationProcessor>
                ru.vibrotek.annotations.StaticInjectProcessor
            </annotationProcessor>
        </annotationProcessors>
    </configuration>
</plugin>
...
