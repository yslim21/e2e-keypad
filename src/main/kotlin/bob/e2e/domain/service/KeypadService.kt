package bob.e2e.bob.e2e.domain.service

import bob.e2e.bob.e2e.presentaion.dto.KeypadResponseDto
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService {

    // 이미지 경로와 숫자를 매핑하는 해시맵
    private val imagePathMap = hashMapOf(
        0 to "keypad/_0.png",
        1 to "keypad/_1.png",
        2 to "keypad/_2.png",
        3 to "keypad/_3.png",
        4 to "keypad/_4.png",
        5 to "keypad/_5.png",
        6 to "keypad/_6.png",
        7 to "keypad/_7.png",
        8 to "keypad/_8.png",
        9 to "keypad/_9.png"
    )

    // KeypadResponseDto를 생성하는 메서드
    fun getKeypad(): KeypadResponseDto {
        // 매번 새로운 ID 생성
        val keypadId = UUID.randomUUID().toString()

        // 해시맵도 새롭게 생성
        val imageHashMap = imagePathMap.mapValues { (_, imagePath) -> generateHashForImage(imagePath) }

        // 이미지 경로를 셔플하여 Base64로 인코딩
        val (imageBase64, shuffledHashes) = getShuffledImageBase64AndHashes(imageHashMap)

        // KeypadResponseDto에 ID, hash 리스트와 Base64 인코딩된 이미지를 담아 반환
        return KeypadResponseDto(id = keypadId, hashList = shuffledHashes, image = imageBase64)
    }

    // 이미지를 기반으로 해시값을 생성하는 메서드
    private fun generateHashForImage(imagePath: String): String {
        val resource = ClassPathResource(imagePath)
        val imageBytes = resource.inputStream.readBytes()

        // 무작위 요소 추가
        val randomBytes = ByteArray(16)
        Random().nextBytes(randomBytes)

        // 이미지 바이트 데이터와 무작위 데이터를 결합하여 해시 처리
        val combinedBytes = imageBytes + randomBytes

        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(combinedBytes)

        // 해시 값을 Hex 문자열로 변환하여 반환
        return digest.joinToString("") { "%02x".format(it) }
    }
    // 셔플된 이미지와 해당 이미지들의 해시 리스트를 반환하는 메서드
    private fun getShuffledImageBase64AndHashes(imageHashMap: Map<Int, String>): Pair<String, List<String>> {
        // 원본 해시맵 출력
        println("Original HashMap: $imageHashMap")

        // 빈 해시값 추가
        val extendedImageHashMap = imageHashMap.toMutableMap()
        extendedImageHashMap[-1] = "" // 첫 번째 빈 해시값 추가 (가상의 키 -1 사용)
        extendedImageHashMap[-2] = "" // 두 번째 빈 해시값 추가 (가상의 키 -2 사용)

        // 숫자와 해시 값을 포함한 리스트를 셔플
        val shuffledPairs = extendedImageHashMap.toList().shuffled()

        // 셔플된 해시맵 출력
        val shuffledHashes = shuffledPairs.map { (_, hash) -> hash }
        println("Shuffled Hashes with Empty Strings: $shuffledHashes")

        // 셔플된 순서대로 이미지를 로드 (빈 해시값에 대해서는 이미지를 로드하지 않음)
        val shuffledImages = shuffledPairs.filter { it.first >= 0 }.map { (number, _) ->
            val imagePath = imagePathMap[number]
            val resource = ClassPathResource(imagePath!!)
            ImageIO.read(resource.inputStream)
        }

        // 이미지를 하나로 합치는 로직 (여기서는 가로로 합침)
        val width = shuffledImages.sumOf { it.width }
        val height = shuffledImages.first().height
        val combinedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = combinedImage.createGraphics()

        var currentWidth = 0
        shuffledImages.forEach { img ->
            graphics.drawImage(img, currentWidth, 0, null)
            currentWidth += img.width
        }

        // 메모리스트림에 이미지를 저장
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(combinedImage, "png", byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Base64로 인코딩된 이미지와 셔플된 해시 리스트를 반환
        return Base64.getEncoder().encodeToString(byteArray) to shuffledHashes
    }


}
