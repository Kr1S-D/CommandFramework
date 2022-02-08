package me.kr1s_d.commandframework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandMapBuilder {
    private final Map<Integer, List<String>> map;

    public CommandMapBuilder() {
        this.map = new HashMap<>();
    }

    public CommandMapBuilder append(int args, String added){
        List<String> a = map.getOrDefault(args, new ArrayList<>());
        a.add(added);
        map.put(args, a);
        return this;
    }

    public Map<Integer, List<String>> getMap(){
        return map;
    }

    public CommandMapBuilder set(int args, List<String> strings){
        map.put(args, strings);
        return this;
    }

    public static CommandMapBuilder builder(){
        return new CommandMapBuilder();
    }
}