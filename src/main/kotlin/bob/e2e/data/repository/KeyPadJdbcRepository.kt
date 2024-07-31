package bob.e2e.bob.e2e.data.repository

import bob.e2e.bob.e2e.domain.model.Keypad

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository
import java.security.spec.NamedParameterSpec


@Repository
class KeyPadJdbcRepository(
    private val jdbcOperations: NamedParameterJdbcOperations,
) : KepadRepository{
    override fun insert(keypad: Keypad) {
        TODO("Not yet implemented")
    }

    override fun selectBy(id: String): Keypad {
        return jdbcOperations.queryForObject(
            "select * from car where id=:id",
            mapOf("id" to id),
        ){rs, _ ->
            Keypad(
                id = rs.getString("id"),

            )

        }
    }

    override fun update(keypad: Keypad) {
        TODO("Not yet implemented")
    }

}