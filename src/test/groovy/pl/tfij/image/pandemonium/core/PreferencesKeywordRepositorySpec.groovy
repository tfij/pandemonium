package pl.tfij.image.pandemonium.core

import spock.lang.Specification
import spock.util.time.MutableClock

import java.time.Instant
import java.util.prefs.Preferences

class PreferencesKeywordRepositorySpec extends Specification {

    private Preferences preferences
    private MutableClock clock

    def setup() {
        clock = new MutableClock(Instant.ofEpochSecond(12345678))
        preferences = Preferences.userNodeForPackage(PreferencesKeywordRepositorySpec.class)
    }

    def cleanup() {
        preferences.removeNode()
    }

    def "Should not remove oldest preference on adding last used preference when there is enough space for the new one"() {
        given: "preference repository with limit=3 for last used preferences"
        def keywordRepository = new PreferencesKeywordRepository(
                3,
                preferences,
                clock)

        when: "I add 3 preference to storage"
        keywordRepository.addLastUsedKeyword("a")
        clock.next()
        keywordRepository.addLastUsedKeyword("b")
        clock.next()
        keywordRepository.addLastUsedKeyword("c")

        then: "all preferences are added"
        keywordRepository.lastUsedKeywords() == ["a", "b", "c"]
    }

    def "Should remove oldest preference on adding last used preference when there is no space for the new one"() {
        given: "preference repository with limit=3 for last used preferences"
        def keywordRepository = new PreferencesKeywordRepository(
                3,
                preferences,
                clock)

        and: "I add 3 preference to storage"
        keywordRepository.addLastUsedKeyword("a")
        clock.next()
        keywordRepository.addLastUsedKeyword("b")
        clock.next()
        keywordRepository.addLastUsedKeyword("c")
        clock.next()

        when: "I add another preference"
        keywordRepository.addLastUsedKeyword("d")

        then: "the oldest one is removed and new one is inserted"
        keywordRepository.lastUsedKeywords() == ["b", "c", "d"]
    }

}
