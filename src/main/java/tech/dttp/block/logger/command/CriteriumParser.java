package tech.dttp.block.logger.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CriteriumParser implements SuggestionProvider<ServerCommandSource> {
    private Set<String> criteria;
    private HashMap<String, Suggestor> criteriumSuggestors = new HashMap<>();

    public CriteriumParser() {
        criteriumSuggestors.put("action", new Suggestor(new ActionSuggestionProvider()));
        criteriumSuggestors.put("targets", new Suggestor(GameProfileArgumentType.gameProfile()));
        criteriumSuggestors.put("range", new Suggestor(IntegerArgumentType.integer()));
        criteriumSuggestors.put("block", new Suggestor(BlockStateArgumentType.blockState()));
        this.criteria = criteriumSuggestors.keySet();
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String input = builder.getInput();
        int lastSpaceIndex = input.lastIndexOf(' ');
        int relColonIndex = input.substring(lastSpaceIndex).indexOf(':');
        int lastColonIndex = lastSpaceIndex + relColonIndex;
        if (relColonIndex == -1) { // no colon, just suggest criteria
            SuggestionsBuilder offsetBuilder = builder.createOffset(lastSpaceIndex + 1);
            builder.add(suggestCriteria(offsetBuilder));
        } else { // take last colon
            String[] spaceSplit = input.substring(0, lastColonIndex).split(" ");
            String criterium = spaceSplit[spaceSplit.length - 1];
            String criteriumArg = input.substring(lastColonIndex + 1);

            if (!criteriumSuggestors.containsKey(criterium)) {
                return builder.buildFuture();
            } else { // check if suggestor consumes the rest
                Suggestor suggestor = criteriumSuggestors.get(criterium);

                int remaining = suggestor.getRemaining(criteriumArg);
                if (remaining > 0) { // suggest new criterium
                    SuggestionsBuilder offsetBuilder = builder.createOffset(input.length() - remaining + 1);
                    return suggestCriteria(offsetBuilder).buildFuture();
                } else {
                    SuggestionsBuilder offsetBuilder = builder.createOffset(lastColonIndex + 1);
                    return suggestor.listSuggestions(context, offsetBuilder);
                }
            }
        }

        return builder.buildFuture();
    }

    private SuggestionsBuilder suggestCriteria (SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        for (String criterium : criteria) {
            if (criterium.startsWith(input)) {
                builder.suggest(criterium + ":");
            }
        }
        return builder;
    }

    private static class Suggestor {
        boolean useSuggestionProvider = false;
        private SuggestionProvider<ServerCommandSource> suggestionProvider;
        private ArgumentType argumentType;

        public Suggestor (SuggestionProvider<ServerCommandSource> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            this.useSuggestionProvider = true;
        }

        public Suggestor (ArgumentType argumentType) {
            this.argumentType = argumentType;
        }

        public CompletableFuture<Suggestions> listSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            if (this.useSuggestionProvider) {
                try {
                    return this.suggestionProvider.getSuggestions(context, builder);
                } catch (CommandSyntaxException e) {
                    return builder.buildFuture();
                }
            } else {
                return this.argumentType.listSuggestions(context, builder);
            }
        }

        public int getRemaining(String s) {
            if (this.useSuggestionProvider) {
                int spaceIndex = s.lastIndexOf(' ');
                if (spaceIndex == -1) return -1;
                return s.length() - s.lastIndexOf(' ');
            }
            try {
                StringReader reader = new StringReader(s);
                this.argumentType.parse(reader);
                return reader.getRemainingLength();
            } catch (CommandSyntaxException e) {
                return -1;
            }
        }
    }
}
