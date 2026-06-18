Java Native Runtime - POSIX
===========================

Overview
--------

jnr-posix is a lightweight cross-platform POSIX emulation layer for Java, written in Java and is part of the JNR project (http://github.com/jnr)

## Releasing


Deploy artifacts to Sonatype Central with the following command:

```text
mvn clean deploy
```

Deploy release artifacts to Maven Central by updating the release version to remove `-SNAPSHOT` and running the following command:

```text
mvn clean deploy -Prelease
```

Update the release version to the next snapshot after the release artifacts have been published.
