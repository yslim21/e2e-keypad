package bob.e2e.bob.e2e.domain.service

import bob.e2e.bob.e2e.data.repository.KeyPadJdbcRepository
import org.springframework.stereotype.Service

@Service
class KeypadService(
    private val keypadJdbcRepository: KeyPadJdbcRepository
) {

}