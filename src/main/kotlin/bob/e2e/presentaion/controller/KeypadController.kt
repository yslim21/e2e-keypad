package bob.e2e.bob.e2e.presentaion.controller

import bob.e2e.bob.e2e.domain.service.KeypadService
import bob.e2e.bob.e2e.presentaion.dto.KeypadResponseDto
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin(origins = ["http://localhost:3000"])  // CORS 설정 추가
class KeypadController(
    private val keypadService: KeypadService
){
    @GetMapping("/api/keypad")
    fun getKeypad(): KeypadResponseDto{
        return keypadService.getKeypad()
    }

}