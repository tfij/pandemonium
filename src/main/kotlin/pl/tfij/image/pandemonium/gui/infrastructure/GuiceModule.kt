package pl.tfij.image.pandemonium.gui.infrastructure

import com.google.inject.AbstractModule
import pl.tfij.image.pandemonium.core.JpgMetadataService
import pl.tfij.image.pandemonium.core.PreferencesKeywordRepository
import pl.tfij.image.pandemonium.gui.ImageDetailsPanel
import pl.tfij.image.pandemonium.gui.StatusBar
import java.time.Clock


class GuiceModule : AbstractModule() {
    override fun configure() {
        bind(JpgMetadataService::class.java).toInstance(JpgMetadataService(PreferencesKeywordRepository(10, Clock.systemDefaultZone())))
        bind(StatusBar::class.java).asEagerSingleton()
        bind(ImageDetailsPanel::class.java).asEagerSingleton()
    }
}