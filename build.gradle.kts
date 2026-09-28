/*
 * Standalone root build for niagaramcp. Replaces the parent Niagara dev
 * workspace the upstream project was built from.
 */

plugins {
  id("com.tridium.niagara")
  id("com.tridium.vendor")
  id("com.tridium.niagara-signing")
  id("com.tridium.convention.niagara-home-repositories")
}

vendor {
  defaultVendor("niagaramcp")
  defaultModuleVersion(providers.gradleProperty("moduleVersion").get())
}

subprojects {
  repositories {
    mavenCentral()
  }
}

// Optional -PbuildDirName=<dir> sends all build output to <project>/<dir>
// instead of build/, so builds against different Niagara releases keep
// separate jars (e.g. -PbuildDirName=build-4.14).
providers.gradleProperty("buildDirName").orNull?.let { dirName ->
  allprojects {
    layout.buildDirectory.set(layout.projectDirectory.dir(dirName))
  }
}
