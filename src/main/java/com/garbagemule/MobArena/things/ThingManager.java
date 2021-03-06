package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;

import java.util.ArrayList;
import java.util.List;

public class ThingManager implements ThingParser {
    private List<ThingParser> parsers;
    private ItemStackThingParser items;

    public ThingManager(MobArena plugin, ItemStackThingParser parser) {
        parsers = new ArrayList<>();
        parsers.add(new CommandThingParser());
        parsers.add(new MoneyThingParser(plugin::getEconomy));
        items = parser;
    }

    public ThingManager(MobArena plugin) {
        this(plugin, new ItemStackThingParser());
    }

    /**
     * Register a new thing parser in the manager.
     *
     * @param parser a thing parser, non-null
     * @param beforeCoreParsers if true, the parser will be invoked before the
     * core parsers, effectively allowing it to override the defaults. Only do
     * this if you know what you're doing.
     */
    public void register(ThingParser parser, boolean beforeCoreParsers) {
        if (beforeCoreParsers) {
            parsers.add(0, parser);
        } else {
            parsers.add(parser);
        }
    }

    /**
     * Register a new thing parser in the manager.
     * <p>
     * This is a convenience method for {@link #register(ThingParser, boolean)}
     * that registers the parser <i>after</i> the core parsers.
     *
     * @param parser a thing parser, non-null
     */
    public void register(ThingParser parser) {
        register(parser, false);
    }

    /**
     * Register a new ItemStack parser in the manager.
     * <p>
     * The provided parser will be invoked if no other thing parsers return a
     * non-null result, and if all other ItemStack parsers also return null.
     *
     * @param parser an ItemStack parser, non-null
     */
    public void register(ItemStackParser parser) {
        items.register(parser);
    }

    @Override
    public Thing parse(String s) {
        for (ThingParser parser : parsers) {
            Thing thing = parser.parse(s);
            if (thing != null) {
                return thing;
            }
        }
        return items.parse(s);
    }
}
