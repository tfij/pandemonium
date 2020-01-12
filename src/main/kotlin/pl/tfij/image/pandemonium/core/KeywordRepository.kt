package pl.tfij.image.pandemonium.core

interface KeywordRepository {
    fun lastUsedKeywords(): List<String>
    fun standardKeywords(): List<String>
    fun addLastUsedKeyword(keyword: String)
}

class InMemoryKeywordRepository : KeywordRepository {
    private val lastUsedKeywords: MutableList<String> = mutableListOf()

    override fun lastUsedKeywords(): List<String> {
        return lastUsedKeywords.toList().sorted()
    }

    override fun standardKeywords(): List<String> {
        return listOf("+5", "Agnieszka", "Architektura", "Dania", "Egipt", "Ewa", "Fauna", "Flora", "Floryda", "Franek",
            "Grazyna", "Grecja", "Góry", "Japonia", "Jesień", "Jezioro", "Julek", "Kosmos", "Krajobraz", "Krysia",
            "Kwiaty", "Las", "Ludzie", "Makro", "Marcelina", "Martwa natura", "Mazury", "Meksym", "Mirek", "Morze",
            "Mysłów", "Niemcy", "Noc", "Norwegia", "Ola", "Owady", "Panorama", "Piotrek", "Plac zabaw", "Pogoda",
            "Praca", "Przedszkole", "Ptaki", "Rodzina", "Rzeka", "Sport", "Szwecja", "Tatry", "Tomek", "Tło", "USA",
            "Woda", "Wycieczki", "Włochy", "Zima")
    }

    override fun addLastUsedKeyword(keyword: String) {
        lastUsedKeywords.add(keyword)
    }

}
