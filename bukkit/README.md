## Bukkit
You can get the latest version from [bintray](https://bintray.com/drschlaubi/maven/kaesk)
### Gradle (Kotlin)
```kotlin
repositories {
    jcenter()
}

dependencies {
    implementation("me.schlaubi", "kaesk-bukkit", "2.1")
}
```

### Gradle (Groovy)
```groovy
repositories {
    jcenter()
}

dependencies {
    implementation 'me.schlaubi:kaesk-bukkit:2.1"'
}
```

```xml
<repositories>
  <repository>
    <name>jcenter</name>
    <url>https://jcenter.bintray.com</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>me.schlaubi</groupId>
    <artifactId>kaesk-bukkit</artifactId>
    <version>2.1</version>
  </dependency>
</dependencies>
```


Register the command
```java
  @Override
  public void onEnable() {
    commandClient = new BukkitCommandClientBuilder(this)
            // no longer needed since this is registered by default
    //        .addDeserializer(GameMode.class, Converters.newEnumDeserializer(GameMode[]::new))
            .setArgumentHandler((error, sender) -> sender.sendMessage(
                "Please enter a valid %s!".formatted(error.getParameterType().getSimpleName())))
            .setNoPermissionHandler((sender, permission) -> sender.sendMessage("You need the permission %s to proceed".formatted(permission)))
            .build();
    commandClient.registerCommand(new SumCommand());
  }
```

## Register the command (Kotlin)
```kotlin
override fun onEnable() {
commandClient = commandClient { 
    noPermissionHandler = NoPermissionHandler { sender, permission -> sender.sendMessage( "You need the $permission permission to execute that command") }

    argumentHandler = InvalidArgumentHandler { error, sender ->sender.sendMessage("Please enter a valid ${error.parameterType.simpleName}!") }

}

    commandClient.registerCommand(SumCommand());
```

There are some more kotlin extensions you can find in the Documentation
If you use the JavaPlugin.commandClient() extension you do not have to specify a plugin instance

Docs: https://p.mik.wtf/kaesk-bukkit
