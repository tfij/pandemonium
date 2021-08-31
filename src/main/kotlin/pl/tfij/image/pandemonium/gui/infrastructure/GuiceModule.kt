package pl.tfij.image.pandemonium.gui.infrastructure

import com.google.inject.AbstractModule
import pl.tfij.image.pandemonium.core.JpgMetadataService
import pl.tfij.image.pandemonium.core.KeywordRepository
import pl.tfij.image.pandemonium.core.PreferencesKeywordRepository
import pl.tfij.image.pandemonium.gui.ImageDetailsPanel
import pl.tfij.image.pandemonium.gui.StatusBar
import java.time.Clock
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.prefs.Preferences

class GuiceModule : AbstractModule() {
    override fun configure() {
        bind(ExecutorService::class.java).toInstance(Executors.newFixedThreadPool(10))
        bind(JpgMetadataService::class.java).toInstance(
            JpgMetadataService(
                PreferencesKeywordRepository(
                    maxNumberOfStoredLastUsedKeywords = 10,
                    mainPreferences = Preferences.userNodeForPackage(KeywordRepository::class.java),
                    clock = Clock.systemDefaultZone()
                )
            )
        )
        bind(StatusBar::class.java).asEagerSingleton()
        bind(ImageDetailsPanel::class.java).asEagerSingleton()
    }
}
