# Building niagaramcp standalone

The upstream project builds from inside a parent Niagara developer workspace.
This repo adds the root Gradle files and the Gradle wrapper so that it builds
on its own, against any local Niagara 4.15 install.

## Requirements

- A Niagara 4.15 install. The build resolves Tridium's Gradle plugins from
  `<niagara_home>/etc/m2/repository`, and it compiles against the modules in
  `<niagara_home>/modules` and the jars in `<niagara_home>/bin/ext`.
- A Java 8 JDK, such as Eclipse Temurin 8.
- Internet access on the first run. The wrapper downloads Gradle 7.6.4 from
  services.gradle.org and checks its SHA-256.

## Per-machine settings

Machine-specific values stay out of this repo. Put them in the user-home
Gradle properties file (`~/.gradle/gradle.properties`):

```properties
niagara_home=C:/Niagara/Niagara-4.15.x.y
org.gradle.java.home=C:/path/to/jdk8
```

The wrapper script also needs `JAVA_HOME` set in the shell that runs it:

```powershell
$env:JAVA_HOME = 'C:\path\to\jdk8'
```

Command-line flags override both settings, for example
`-Pniagara_home=...`. Per-machine settings scripts can also go in
`local/my-settings.gradle.kts`. The `local/` folder is gitignored.

## Build

```powershell
.\gradlew.bat :niagaramcp-rt:jar
```

The build writes the module to `niagaramcp-rt/build/libs/niagaramcp-rt.jar`.
It does not install anything. Tridium's signing plugin copies module jars into
`<niagara_home>/modules` by default. This build sends that copy to
`niagaramcp-rt/build/module` instead.

## Signing

Signing is off by default, so the build produces an unsigned jar
(`niagaramcp.sign=false` in `gradle.properties`). To sign:

1. Create a signing profile at `~/.tridium/security/niagara.signing.xml`
   that holds a certificate with the alias `Niagara4Modules`. Tridium's
   signing plugin provides the `createProfile`, `generateCertificate` and
   `importCertificate` tasks for this. An enterprise code-signing certificate
   also works.
2. Build with `-Pniagaramcp.sign=true`.

A station accepts an unsigned or self-signed module only if it has been set
up to allow that.

## Other Niagara versions

The plugin versions in `gradle.properties` are the ones that ship with
Niagara 4.15.4. When you build against another release, check
`<niagara_home>/etc/m2/repository/com/tridium/tools/niagara-plugins` and
`.../gradle-settings-plugins`. If the versions there differ, override them:

```powershell
.\gradlew.bat :niagaramcp-rt:jar -Pniagara_home=... -PniagaraGradlePluginVersion=7.6.x
```

The module manifest records dependency versions as `major.minor` (for
example `vendorVersion="4.15"`), taken from the install being built against.
