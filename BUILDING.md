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
(`niagaramcp.sign=false` in `gradle.properties`). The build signs with the
alias `Niagara4Modules` from the signing profile at
`~/.tridium/security/niagara.signing.xml`. The profile and its keystore
belong in the user home, never in this repo.

### Self-signed development certificate

1. Create the profile, unless it already exists:

   ```powershell
   .\gradlew.bat createProfile --create --profile-path "$HOME\.tridium\security\niagara.signing.xml"
   ```

   Tridium's plugin can also create a default profile the first time it
   loads. The profile stores randomly generated keystore and key passwords.
   Keep it private.

2. Optional: change the certificate subject. The default
   `niagara.signing.dname` in the profile embeds `${user.name}@${host.name}`,
   so every jar you sign would carry your username and hostname. Edit the
   profile first to use a neutral subject, for example:

   ```text
   O=<your org>,OU=For Development Purposes Only Do Not Distribute,CN=<name>${alias}
   ```

3. Generate the certificate:

   ```powershell
   .\gradlew.bat generateCertificate --alias Niagara4Modules --profile-path "$HOME\.tridium\security\niagara.signing.xml"
   ```

4. Build signed:

   ```powershell
   .\gradlew.bat :niagaramcp-rt:jar -Pniagaramcp.sign=true
   ```

5. Verify: `jarsigner -verify niagaramcp-rt\build\module\niagaramcp-rt.jar`
   should print `jar verified.` Warnings about a self-signed certificate or
   an invalid chain are expected for a development certificate.

The signed jar is written to `niagaramcp-rt/build/libs` and copied to
`niagaramcp-rt/build/module`. An enterprise code-signing certificate can
replace the self-signed one. Load it with Tridium's `importCertificate`
task under the same alias.

### Trusting the certificate

A self-signed certificate is not trusted anywhere by default. For a Niagara
station or Workbench to accept the module, the certificate has to be in
that host's trust store. Export it with Tridium's `exportCertificate` task
and import it into the trust store of the test host only.

## Running a test station

Read [docs/SAFETY.md](docs/SAFETY.md) before starting any station copied
from a live site.

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
