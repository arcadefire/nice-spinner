package org.angmarch.views

internal enum class PopUpTextAlignment(private val id: Int) {
    START(0), END(1), CENTER(2);

    companion object {
        fun fromId(id: Int): PopUpTextAlignment {
            for (value in values()) {
                if (value.id == id) return value
            }
            return CENTER
        }
    }
}