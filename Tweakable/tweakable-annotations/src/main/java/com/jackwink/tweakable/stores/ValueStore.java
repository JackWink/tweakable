package com.jackwink.tweakable.stores;

public interface ValueStore {

    boolean contains(String key);

    int getInt(String key, int defaultValue);

    void setInt(String key, int value);

}
