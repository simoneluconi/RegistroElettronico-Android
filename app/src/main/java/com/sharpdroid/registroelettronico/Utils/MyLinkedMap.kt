package com.sharpdroid.registroelettronico.Utils

import java.util.*

class MyLinkedMap<K, V> : LinkedHashMap<K, V>() {

    fun getValue(i: Int): V? {

        val entry = this.getEntry(i) ?: return null

        return entry.value
    }

    fun getEntry(i: Int): Map.Entry<K, V>? {
        // check if negetive index provided
        val entries = entries
        var j = 0

        for (entry in entries)
            if (j++ == i) return entry

        return null

    }

    fun getKey(i: Int): K? {
        val entry = this.getEntry(i) ?: return null

        return entry.key
    }


}