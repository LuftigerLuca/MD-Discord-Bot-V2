package eu.luftiger.mdbot.discord.util

fun String.isDate(): Boolean {
    val trimmed = this.trim()
    return trimmed.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}"))
}