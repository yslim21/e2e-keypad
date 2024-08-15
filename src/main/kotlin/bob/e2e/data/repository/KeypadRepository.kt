package bob.e2e.bob.e2e.data.repository

import bob.e2e.bob.e2e.domain.model.Keypad

interface KeypadRepository {
    fun insert(keypad: Keypad)

    fun selectBy(id: String): Keypad

    fun update(keypad: Keypad)
}