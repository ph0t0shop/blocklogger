# Blocklogger
A block change logging tool for the Fabric mod loader. Part of the FabricAdmin suite of tools
## Todo
- [ ] Return the position of a block place
- [ ] Add container transactions (Pos, player, items removed)
- [ ] Add /bl i

## Develop
How can you help develop blocklogger?
- First download the source code and open in your IDE
- Run the following commands:

```
./gradlew genSources
./gradlew vscode (if you are using VScode, if you're using IntelliJ then look on the fabric wiki)
Build with .\gradlew build 
```

# Use
Blocklogger (as of v0.2) saves the log to a file named interactions.bl in the game directory. Access via the command (Not yet implemented) /bl x y z

## Block breaks
### v0.2-beta.1 and above
Information is saved to interactions.bl.
Block interaction data is accessible via command /bl i x y z

# Compile
Open in your IDE, and open a terminal. Run the following commands:
```
./gradlew genSources

./gradlew build
```
The .jar files are located in build/libs
