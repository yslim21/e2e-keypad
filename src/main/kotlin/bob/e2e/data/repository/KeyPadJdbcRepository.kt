package bob.e2e.bob.e2e.data.repository

import bob.e2e.bob.e2e.domain.model.Keypad

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository

@Repository
class KeyPadJdbcRepository(
    private val jdbcOperations: NamedParameterJdbcOperations,
) : KeypadRepository{
    override fun insert(keypad: Keypad) {
        TODO("Not yet implemented")
    }

    override fun selectBy(id: String): Keypad {
        TODO("Not yet implemented")
    }

    override fun update(keypad: Keypad) {
        TODO("Not yet implemented")
    }

}