package pl.tfij.image.pandemonium.core

import java.util.prefs.Preferences

interface InitDirectoryRepository {
    fun setInitDirectory(initDirectory: String)
    fun getInitDirectory(): String
}

class PreferencesInitDirectoryRepository(mainPreferences: Preferences) : InitDirectoryRepository {
    private val initDirPreferences = mainPreferences.node("initDir")

    override fun setInitDirectory(initDirectory: String) {
        initDirPreferences.put("directory", initDirectory)
    }

    override fun getInitDirectory(): String {
        return initDirPreferences.get("directory", System.getProperty("user.home"))
    }
}
