package com.silva.pix.keymanager

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.silva.pix.keymanager")
		.start()
}

