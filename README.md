This plugin can be used to generate Xcore file from Ecore files.

This generator has only been tested on a limited number of Ecore metamodels. Please report any missing feature.

# Usage

Add the plugin to your `build.gradle` file:

```gradle
plugins {
    // your others plugins

    id 'fr.naomod.ecore2xcore' version '1.0'

}
```

Optionally configure the plugin like that.

```gradle
ecore2xcore {
    // defaults values

    sourceDir = 'models'
    targetDir = "${project.buildDir}/xcore-gen"
    basePackage = 'atl.research'
}
```

This plugins provides a `generateXcore` task that finds all `*.ecore` files in the source directory and generate the corresponding `xcore` files in the target directory.


# Chaining this plugin with Xtext to generate Java sources

It can be chained with the [Xtext gradle plugin](https://github.com/xtext/xtext-gradle-plugin) to generate java sources from ecore files.
See example project [here](https://github.com/ATL-Research/benchmarks/tree/main/runners/Class2Relational/atol/metamodels).