package pl.tfij.image.pandemonium.core

import spock.lang.Specification
import spock.lang.Unroll

class ExposureTimeSpec extends Specification {

    @Unroll
    def "Should convert ExposureTime(#numerator, #divisor) to #expectedText"() {
        given:
        ExposureTime exposureTime = new ExposureTime(numerator, divisor)

        expect:
        exposureTime.toText() == expectedText

        where:
        numerator | divisor | expectedText
        10        | 900     | "1/90 s"
        1         | 90      | "1/90 s"
        3         | 100     | "3/100 s"
        10        | 10      | "1 s"
        1         | 1       | "1 s"
        15        | 1       | "15 s"
        20        | 5       | "4 s"
        20        | 6       | "3.3 s"
        200       | 10      | "20 s"
    }

}
