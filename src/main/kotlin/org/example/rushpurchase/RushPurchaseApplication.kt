package org.example.rushpurchase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class RushPurchaseApplication

fun main(args: Array<String>) {
    runApplication<RushPurchaseApplication>(*args)
}
