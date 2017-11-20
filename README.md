## ninja-pebble-module
Pebble Template integration for Ninja web framework

Updated Pebble module for Ninja framework. Java 8, Ninja 6.2.0. Allows configuration of most Pebble options including the Syntax.

### Install
Currently pending project creation on central repository.

### Usage

In your conf/Module.java, install the module:

```java
@Override
protected void configure() {
    install(new PebbleModule());
}
```

Also in conf/Module.java, add this `@Provides` method. The `NinjaExtensionService` gets injected into the module and allows you to pass in custom extensions. 

```java
@Provides
NinjaExtensionService provideNinjaExtensionService() {
    NinjaExtensionService service = new NinjaExtensionService();
    // add custom extensions here:
    service.addExtension(new CustomExtension());
    return service;
}
```

Rename your views to remove the `.ftl` if you want and change the Freemarker syntax to Pebble.

Create the `views/error/500internalServerError.html` file with these contents (or customize as you like):

```html
<!DOCTYPE html>
<html>
<head>
<title>500 Internal Server Error</title>
</head>
<body>
</body>
</html>
```
### Configure
The following configuration is available via conf/application.conf

[See Pebble Engine Settings for more info](http://www.mitchellbosecke.com/pebble/documentation/guide/installation)

```
# You can leave these out if not changing these defaults
ninja.template.pebble.fileExt=.html
ninja.template.pebble.managedContentType=text/html
ninja.template.pebble.500ErrorViewLocation=views/error/500internalServerError.html
ninja.template.pebble.cacheActive=true
ninja.template.pebble.executorServiceClass=CachedThreadPool
ninja.template.pebble.poolSize=-1
inja.template.pebble.strictVariables=false
ninja.template.pebble.defaultLocale=en-US
# Syntax options
ninja.template.pebble.delimiterCommentOpen={#
ninja.template.pebble.delimiterCommentClose=#}
ninja.template.pebble.delimiterExecuteOpen={%
ninja.template.pebble.delimiterExecuteClose=%}
ninja.template.pebble.delimiterPrintOpen={{
ninja.template.pebble.delimiterPrintClose=}}
ninja.template.pebble.whitespaceTrim=-
```

**ninja.template.pebble.executorServiceClass**
One of (`CachedThreadPool`, `FixedThreadPool`, `ScheduledThreadPool`, `SingleThreadScheduledExecutor`, `WorkStealingPool`)  

**ninja.template.pebble.poolSize**
`-1` if one of (`CachedThreadPool`, `SingleThreadScheduledExecutor`) or > 0 if one of (`FixedThreadPool`, `ScheduledThreadPool`) and either `-1` or > `0` for (`WorkStealingPool`)

For example:

```
ninja.template.pebble.executorServiceClass=ScheduledThreadPool
ninja.template.pebble.poolSize=5
```


