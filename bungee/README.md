## Bungee
You can get the latest version from [bintray](https://bintray.com/drschlaubi/maven/kaesk)
### Gradle (Kotlin)
```kotlin
repositories {
    jcenter()
}

dependencies {
    implementation("me.schlaubi", "kaesk-bungee", "2.0")
}
```

### Gradle (Groovy)
```groovy
repositories {
    jcenter()
}

dependencies {
    implementation 'me.schlaubi:kaesk-bungee:2.0"'
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
    <version>2.0</version>
  </dependency>
</dependencies>
```


## Register the command (java)
```java
  @Override
  public void onEnable() {
    commandClient = new BungeeCommandClientBuilder(this)
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
    noPermissionHandler = NoPermissionHandler { sender, permission -> sender.sendMessage( "You need the $permission permission to execitre that command") }

    argumentHandler = InvalidArgumentHandler { error, sender ->sender.sendMessage("Please enter a valid ${error.parameterType.simpleName}!") }

}

    commandClient.registerCommand(SumCommand());
```

There are some more kotlin extensions you can find in the Documentation
If you use the Plugin.commandClient() extension you do not have to specify a plugin instance