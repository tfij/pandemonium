package pl.tfij.image.pandemonium.core

import java.time.Clock
import java.util.prefs.Preferences

interface KeywordRepository {
    fun lastUsedKeywords(): List<String>
    fun standardKeywords(): List<String>
    fun addLastUsedKeyword(keyword: String)
    fun addStandardKeyword(keyword: String)
    fun deleteStandardKeyword(keyword: String)
}

class InMemoryKeywordRepository : KeywordRepository {
    private val lastUsedKeywords: MutableList<String> = mutableListOf()
    private val standardKeywords: MutableList<String> = initialStandardKeywords.toMutableList()

    override fun lastUsedKeywords(): List<String> {
        return lastUsedKeywords.toList().sorted()
    }

    override fun standardKeywords(): List<String> {
        return standardKeywords
    }

    override fun addLastUsedKeyword(keyword: String) {
        if (!lastUsedKeywords.contains(keyword)) {
            lastUsedKeywords.add(keyword)
            lastUsedKeywords.distinct()
        }
    }

    override fun addStandardKeyword(keyword: String) {
        standardKeywords.plus(keyword).distinct()
    }

    override fun deleteStandardKeyword(keyword: String) {
        standardKeywords.removeIf { it == keyword }
    }

    companion object {
        private val initialStandardKeywords = listOf("Architecture", "Landscape", "Portrait")
    }
}

class PreferencesKeywordRepository(
    private val maxNumberOfStoredLastUsedKeywords: Int,
    mainPreferences: Preferences,
    private val clock: Clock
) : KeywordRepository {

    private val lastUsedKeywordPreferences = mainPreferences.node("keywords").node("lastUsed")
    private val standardKeywordPreferences = mainPreferences.node("keywords").node("standard")

    init {
        require(maxNumberOfStoredLastUsedKeywords > 0) { "maxNumberOfStoredLastUsedKeywords must be positive number, given '$maxNumberOfStoredLastUsedKeywords'" }
    }

    override fun lastUsedKeywords(): List<String> {
        return lastUsedKeywordPreferences.keys()
            .map { key -> lastUsedKeywordPreferences.get(key, "") }
            .filterNot { it.isEmpty() }
            .sorted()
    }

    override fun standardKeywords(): List<String> {
        return standardKeywordPreferences.keys().toList()
    }

    override fun addLastUsedKeyword(keyword: String) {
        removeIfKeywordExistOnListOfLastUsed(keyword)
        prepareSpaceForNewLastUsedKeywords()
        lastUsedKeywordPreferences.put(clock.millis().toString(), keyword)
    }

    override fun addStandardKeyword(keyword: String) {
        standardKeywordPreferences.putBoolean(keyword, true)
    }

    override fun deleteStandardKeyword(keyword: String) {
        standardKeywordPreferences.remove(keyword)
    }

    private fun removeIfKeywordExistOnListOfLastUsed(keyword: String) {
        lastUsedKeywordPreferences.keys()
            .filter { key -> lastUsedKeywordPreferences.get(key, "") == keyword }
            .forEach { key -> lastUsedKeywordPreferences.remove(key) }
    }

    private fun prepareSpaceForNewLastUsedKeywords() {
        lastUsedKeywordPreferences.keys()
            .sorted()
            .reversed()
            .drop(maxNumberOfStoredLastUsedKeywords - 1)
            .forEach { key -> lastUsedKeywordPreferences.remove(key) }
    }
}
