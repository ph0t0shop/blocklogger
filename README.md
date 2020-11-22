# Blocklogger
A block change logging tool for the Fabric mod loader. Part of the FabricAdmin suite of tools
#### Join the discord at https://discord.gg/UxHnDWr
## Todo
- [ ] Rollbacks
- [ ] Fully multithread database

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
Blocklogger (as of v0.2) saves the log to a file named blocklogger.bl in the game directory. Access via the command /bl in game

### Important note regarding migration from v0.2.x -> v0.3.x
Existing databases will be broken by updating. Before v0.3.0 is released, there will be an in game tool to update databases

## All interactions
### v0.3 and above
Information is saved to interactions.bl.
There are multiple in game commands to help with reading a database
##### /bl inspect
When provided a BlockPos argument, this command prints the latest 10 interactions from the database to chat
When no argument given, this command toggles inspect mode. Whenever a player with inspect mode attacks a block, the database entries for it are given
Requires permission level 3+

##### /bl search
A query builder in a command. You can specify many parameters and an SQL query is built and executed, with the results printed to chat
Requires permission level 3+

##### /bl sql
An experimental command to allow custom queries to be entered from an in game command
Runs on a different thread to help reduce lag
Requires permission level 4+

### v0.2.x
Information is saved to interactions.bl.
Block interaction data for a block is accessible via command /bl i x y z
You can filter for players and block types by using /bl s

# Compile
Open in your IDE, and open a terminal. Run the following commands:
```
./gradlew genSources

./gradlew build
```
The .jar files are located in build/libs

# License
Blocklogger is licensed under the GNU GPL v3.0, found in license.txt
