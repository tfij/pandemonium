package pl.tfij.image.pandemonium.core

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class ImageScannerBuilderSpec extends Specification {
    private static File BASE_DIR = File.createTempDir("test")
    private static Path a = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "a"))
    private static Path aa = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "a", "a"))
    private static Path aaa = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "a", "a", "a"))
    private static Path aaaa = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "a", "a", "a", "a"))
    private static Path ab = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "a", "b"))
    private static Path b = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "b"))
    private static Path ba = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "b", "a"))
    private static Path bb = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "b", "b"))
    private static Path c = Files.createDirectory(Paths.get(BASE_DIR.getAbsolutePath(), "c"))
    private static Path sourceImg = new File("src/test/resources/image-with-metadata.jpg").toPath()
    private static Path sourceTxt = new File("src/test/resources/text-file.txt").toPath()

    static {
        copyFile(sourceImg, a.resolve("a1.jpg"))
        copyFile(sourceImg, a.resolve("a2.jpg"))
        copyFile(sourceImg, aa.resolve("aa1.jpg"))
        copyFile(sourceImg, aaa.resolve("aaa1.jpg"))
        copyFile(sourceImg, aaaa.resolve("aaaa1.jpg"))
        copyFile(sourceImg, ab.resolve("ab1.jpg"))
        copyFile(sourceImg, b.resolve("b1.jpg"))
        // `ba` dir is empty
        copyFile(sourceImg, bb.resolve("bb1.jpg"))
        copyFile(sourceTxt, c.resolve("c1.txt"))
    }

    def "should return empty list for no recursive scan of dir with no files"() {
        when: "I fetch dir with no files (only with dirs)"
        List<File> files = ImageScannerBuilder.baseDir(BASE_DIR)
                .scanRecursively(ImageScannerBuilder.NoRecursionSetup.INSTANCE)
                .toList()

        then: "result list is empty"
        files == []
    }

    def "should return empty list for no recursive scan of empty dir"() {
        when: "I fetch dir with no files (only with dirs)"
        List<File> files = ImageScannerBuilder.baseDir(ba.toFile())
                .scanRecursively(ImageScannerBuilder.NoRecursionSetup.INSTANCE)
                .toList()

        then: "result list is empty"
        files == []
    }

    def "should return all images for no recursive scan of dir with images files"() {
        when: "I fetch dir witch to images and with no recursion"
        List<String> files = ImageScannerBuilder.baseDir(a.toFile())
                .scanRecursively(ImageScannerBuilder.NoRecursionSetup.INSTANCE)
                .toList()
                .collect { it.getName() }

        then: "only images from base dir are returned"
        files == ["a1.jpg", "a2.jpg"]
    }

    def "should return empty list for no recursive scan of not empty dir but with no images files"() {
        when: "I fetch dir with one txt file"
        List<File> files = ImageScannerBuilder.baseDir(c.toFile())
                .scanRecursively(ImageScannerBuilder.NoRecursionSetup.INSTANCE)
                .toList()

        then: "no return list is empty"
        files == []
    }

    def "should return only images from the base dir for recursive scan with deep=1 (like NoRecursionSetup)"() {
        when: "I fetch images with recursion deep equal to 1"
        Set<String> files = ImageScannerBuilder.baseDir(a.toFile())
                .scanRecursively(new ImageScannerBuilder.RegularRecursionSetup(1, 100))
                .toList()
                .collect { it.getName() }
                .toSet()

        then: "only images from base dir (equal to `a`) are returned"
        files == ["a1.jpg", "a2.jpg"].toSet()
    }

    def "should return all images from the base dir and only level 1 sub dirs for recursive scan with deep=2"() {
        when: "I fetch `a` dir with recursion deep equal 2"
        Set<String> files = ImageScannerBuilder.baseDir(a.toFile())
                .scanRecursively(new ImageScannerBuilder.RegularRecursionSetup(2, 100))
                .toList()
                .collect { it.getName() }
                .toSet()

        then: "images from the `a` dir and sub dirs (level = 1) are returned"
        files == ["a1.jpg", "a2.jpg", "aa1.jpg", "ab1.jpg"].toSet()
    }

    def "should return all images from the base dir and all sub dirs for recursive scan with deep=10"() {
        when: "I fetch `a` dir with recursion deep equal 10"
        Set<String> files = ImageScannerBuilder.baseDir(a.toFile())
                .scanRecursively(new ImageScannerBuilder.RegularRecursionSetup(10, 100))
                .toList()
                .collect { it.getName() }
                .toSet()

        then: "images from the `a` dir and all sub dirs are returned"
        files == ["a1.jpg", "a2.jpg", "aa1.jpg", "ab1.jpg", "aaa1.jpg", "aaaa1.jpg"].toSet()
    }

    def "should limit result set to recursionLimit"() {
        given: "recursion limit equal 3"
        int recursionLimit = 3

        when: "I fetch `a` dir with the recursion limit"
        Set<String> files = ImageScannerBuilder.baseDir(a.toFile())
                .scanRecursively(new ImageScannerBuilder.RegularRecursionSetup(5, recursionLimit))
                .toList()
                .collect { it.getName() }

        then: "3 files was returned"
        files.size() == recursionLimit
    }

    def "should return empty list for null base dir"() {
        when: "base dir is null"
        List<File> files = ImageScannerBuilder.baseDir(null)
                .toList()

        then: "empty list is returned"
        files == []
    }

    private static void copyFile(Path source, Path destination) {
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
    }

}
