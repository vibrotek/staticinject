# staticinject
The library generates a special code to load and use Spring Beans

pom.xml
<pre>
<code>
...
&lt;plugin&gt;
    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
    &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;
    &lt;version&gt;3.5&lt;/version&gt;
    &lt;configuration&gt;
        &lt;source&gt;1.8&lt;/source&gt;
        &lt;target&gt;1.8&lt;/target&gt;
        &lt;compilerArgument&gt;-Xlint:unchecked&lt;/compilerArgument&gt;
        &lt;annotationProcessors&gt;
            &lt;annotationProcessor&gt;
                ru.vibrotek.annotations.StaticInjectProcessor
            &lt;/annotationProcessor&gt;
        &lt;/annotationProcessors&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;
...

</code>
