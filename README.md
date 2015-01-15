# jdbc-ssh

JDBC driver over SSH tunnel [Maven Central](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven.svg) ]


## Build

If you just want to compile the project without running the tests:

```
mvn -DskipTests clean install
```

If you want to run the tests (Derby and H2 in server mode):

```
mvn -Djdbc.ssh.username="xxx" -Djdbc.ssh.password="xxx" clean install
```

NOTE: 

If your SSH server is not running on the default port 22 and/or localhost then you can change those paramaters:

```
mvn -Djdbc.ssh.username="xxx" -Djdbc.ssh.password="xxx" -Djdbc.ssh.host="192.168.0.1" -Djdbc.ssh.port="2222" clean install
```

At the moment a locally running SSH server is needed for the tests. The embedded SSH server in the unit tests is not yet 
ready (authentication works, but port forwarding fails at the moment).

## Maven dependencies

You can find the latest releases here:

[ ![Download](https://api.bintray.com/packages/cheetah/monkeysintown/jdbc-ssh/images/download.svg) ](https://bintray.com/cheetah/monkeysintown/jdbc-ssh/_latestVersion)

... or setup your Maven dependencies:

```xml
<dependency>
    <groupId>com.m11n.jdbc.ssh</groupId>
    <artifactId>jdbc-ssh</artifactId>
    <version>1.0.1</version>
</dependency>
```

... and configure Bintray's JCenter repository in your pom.xml:
 
```xml
...
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>central</id>
        <name>bintray</name>
        <url>http://jcenter.bintray.com</url>
    </repository>
</repositories>
...
```

Get automatic notifications about new releases here:

[ ![Get automatic notifications about new "jdbc-ssh" versions](https://www.bintray.com/docs/images/bintray_badge_color.png) ](https://bintray.com/cheetah/monkeysintown/jdbc-ssh/view?source=watch)
