package net.qubacy.textit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform