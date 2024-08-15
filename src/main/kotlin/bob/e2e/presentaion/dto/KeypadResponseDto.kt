package bob.e2e.bob.e2e.presentaion.dto

import bob.e2e.bob.e2e.domain.model.Keypad

data class KeypadResponseDto(
    val id: String?,
    val hashList: List<String>,
    val image: String,
) {
    companion object {
        fun from(keypad: Keypad): KeypadResponseDto {
            return KeypadResponseDto(
                id = keypad.id,
                hashList = listOf(keypad.hash), // 기존에 있던 hash를 hashList로 수정
                image = keypad.image
            )
        }
    }
}
