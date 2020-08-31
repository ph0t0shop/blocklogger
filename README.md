# Blocklogger
A block change logging tool for the Fabric mod loader. Part of the FabricAdmin suite of tools
## Todo
- [ ] Return the position of a block place using hitResult
- [ ] Add container transactions (Pos, player, items removed)
- [ ] Save to a SQLite database rather than a TXT file
# Use
Blocklogger (as of v0.1-alpha.1) saves the log to a txt file in the game directory called blocklogger-log
# Compile
Open in your IDE, and open a terminal. Run the following commands:
```
./gradlew genSources

./gradlew build
```
