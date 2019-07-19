# Using

This component is available via maven.  Include in your pom.xml file the following :-

```
    <project>
        ...
        <dependencies>
            <dependency>
                <groupId>com.tibco.ep.samples.highavailability</groupId>
                <artifactId>ca-statefilter</artifactId>
                <type>ep-eventflow-fragment</type>
            </dependency>
            ...
        </dependencies>
        ...
        <dependencyManagement>
            <dependencies>
                <dependency>
                     <groupId>com.tibco.ep.samples.highavailability</groupId>
                     <artifactId>ca-statefilter</artifactId>
                     <version>1.0.0</version>
                 </dependency>
                 ...
             </dependencies>
        </dependencyManagement>
        ...
    </project>
```
