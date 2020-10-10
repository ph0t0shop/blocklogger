# Blocklogger
A block change logging tool for the Fabric mod loader. Part of the FabricAdmin suite of tools
## Todo
- [ ] Return the position of a block place
- [ ] Add container transactions (Pos, player, items removed)
- [ ] Add /bl i

# Use
Blocklogger (as of v0.2) saves the log to a file named interactions.bl in the game directory. Access via the command (Not yet implemented) /bl x y z

## Block breaks
### v0.2-alpha.1 and above
Information is saved to interactions.bl.
Block interaction data is accessible via command /bl i x y z

# Compile
Open in your IDE, and open a terminal. Run the following commands:
```
./gradlew genSources

./gradlew build
```
The .jar files are located in build/libs