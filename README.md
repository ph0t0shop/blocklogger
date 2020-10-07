# Blocklogger
A block change logging tool for the Fabric mod loader. Part of the FabricAdmin suite of tools
## Todo
- [ ] Return the position of a block place
- [ ] Add container transactions (Pos, player, items removed)
- [X] Save to a SQLite database rather than a TXT file

# Use
Blocklogger (as of v0.1) saves the log to a txt file in the game directory called blocklogger.txt 

## Block breaks
### v0.1 and below 
Found in blocklogger.txt as a block break + information 
### v0.2-alpha.1 and above
Information is saved to blocklogger/log.db.
Accessible via command /bl x y z

# Compile
Open in your IDE, and open a terminal. Run the following commands:
```
./gradlew genSources

./gradlew build
```
