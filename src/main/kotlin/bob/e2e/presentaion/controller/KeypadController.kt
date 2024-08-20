package bob.e2e.bob.e2e.presentaion.controller

import bob.e2e.bob.e2e.domain.service.KeypadService
import bob.e2e.bob.e2e.presentaion.dto.EncryptedDataResponseDto
import bob.e2e.bob.e2e.presentaion.dto.KeypadResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @PostMapping("/api/receiveEncryptedData")
    fun receiveEncryptedData(@RequestBody request: EncryptedDataResponseDto): ResponseEntity<out Any>? {
        println("Received Encrypted Data: ${request.encryptedData}")
        println("Received ID: ${request.id}")

        //val timestamp = keypadService.

        //ID로부터 타임스탬프를 추출하여 검증
        val timestamp = keypadService.extractTimestampFromId(request.id)

        if(!keypadService.isTimestampValid(timestamp.toLong())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The keypad has expired")
        }

        // ID에 매핑된 keyHashMap을 찾아서 외부 서버로 POST 요청을 보냄
        val keyHashMap = keypadService.findKeyHashMapById(request.id)
        val externalServerResponse = keypadService.sendPostRequest(request.encryptedData, keyHashMap)

        println(externalServerResponse)
        // 외부 서버의 응답을 클라이언트에게 반환
        return ResponseEntity.ok(externalServerResponse)

        //return ResponseEntity.ok(request)
    }



}