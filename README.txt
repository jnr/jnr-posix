jna-posix is a lightweight cross-platform POSIX emulation layer for Java, written in Java and leveraging the JNA library (https://jna.dev.java.net/).

= Building

JNA should build fine from within NetBeans. Otherwise, copy the file nbproject/private/private.properties.tmpl to nbproject/private/private.properties and edit it to point at a local junit.jar. The one mentioned in the template file is in the default location on Mac OS X with Xcode installed.
