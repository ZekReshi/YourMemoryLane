package at.jku.yourmemorylane.db.entities

enum class Type(val value: Int) {
    IMAGE(1),
    VIDEO(2),
    AUDIO(3),
    TEXT(4)
}