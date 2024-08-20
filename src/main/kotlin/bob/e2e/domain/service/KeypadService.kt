package bob.e2e.bob.e2e.domain.service

import bob.e2e.bob.e2e.presentaion.dto.KeypadResponseDto
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import javax.imageio.ImageIO

@Service
class KeypadService {

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

    private val secretKey = "bob" // secretKey를 여기에 설정
    private val keyHashMapStorage: MutableMap<String, Map<String, String>> = mutableMapOf()

    fun getKeypad(): KeypadResponseDto {
        val keypadId = generateKeypadId()
        val timestamp = getTimestamp()
        val hash = doHash(keypadId + timestamp + secretKey)

        // ID를 "timestamp_hash" 형식으로 생성
        val id = "${timestamp}_${hash}"

        val imageHashMap = imagePathMap.mapValues { (_, imagePath) -> generateHashForImage(imagePath) }

        val (imageBase64, shuffledHashes) = getShuffledImageBase64AndHashes(imageHashMap)

        // KeyHashMap을 생성하고 저장
        val keyHashMap = imageHashMap.mapKeys { it.key.toString() } // 키를 String으로 변환하여 저장
        keyHashMapStorage[id] = keyHashMap

        return KeypadResponseDto(
            hashList = shuffledHashes,
            image = imageBase64,
            id = id
        )
    }


    fun extractTimestampFromId(id: String): String{
        return id.split("_")[0]
    }

    fun isTimestampValid(timestamp: Long): Boolean {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val keypadTime = LocalDateTime.parse(timestamp.toString(), dateFormatter)
        val now = LocalDateTime.now()

        val minuteDifference = ChronoUnit.MINUTES.between(keypadTime, now)
        return minuteDifference < 1 //유효시간 1분

    }

    fun findKeyHashMapById(id: String): Map<String, String> {
        // 메모리에서 ID에 매핑된 KeyHashMap을 조회
        return keyHashMapStorage[id] ?: error("No KeyHashMap found for the given ID")
    }

    fun sendPostRequest(userInput: String, keyHashMap: Map<String, String>): String {
        val url = "http://146.56.119.112:8081/auth"

        print(keyHashMap)
        val postData = mapOf(
            "userInput" to userInput,
            "keyHashMap" to keyHashMap,
            //"keyLength" to keyHashMap.size
        )

        //요청 데이터를 JSON으로 변환하여 외부 서버로 전송
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(url, postData, String::class.java)

        return response.body ?: "No response from server"
    }

    private fun generateKeypadId(): String {
        return UUID.randomUUID().toString()
    }

    private fun getTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        return dateFormat.format(Date())
    }

    private fun doHash(data: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(data.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun generateHashForImage(imagePath: String): String {
        val resource = ClassPathResource(imagePath)
        val imageBytes = resource.inputStream.readBytes()
        val randomBytes = ByteArray(16)
        Random().nextBytes(randomBytes)
        val combinedBytes = imageBytes + randomBytes

        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(combinedBytes)

        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun getShuffledImageBase64AndHashes(imageHashMap: Map<Int, String>): Pair<String, List<String>> {
        val extendedImageHashMap = imageHashMap.toMutableMap()
        extendedImageHashMap[-1] = ""
        extendedImageHashMap[-2] = ""
        //println(extendedImageHashMap)

        val shuffledPairs = extendedImageHashMap.toList().shuffled()
        val shuffledHashes = shuffledPairs.map { (_, hash) -> hash }
        //println(shuffledHashes)

        val shuffledImages = shuffledPairs.filter { it.first >= 0 }.map { (number, _) ->
            val imagePath = imagePathMap[number]
            val resource = ClassPathResource(imagePath!!)
            ImageIO.read(resource.inputStream)
        }

        val width = shuffledImages.sumOf { it.width }
        val height = shuffledImages.first().height
        val combinedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = combinedImage.createGraphics()

        var currentWidth = 0
        shuffledImages.forEach { img ->
            graphics.drawImage(img, currentWidth, 0, null)
            currentWidth += img.width
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(combinedImage, "png", byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        return Base64.getEncoder().encodeToString(byteArray) to shuffledHashes
    }
}

