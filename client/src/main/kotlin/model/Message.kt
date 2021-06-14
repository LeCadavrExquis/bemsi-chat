package model

import kotlin.random.Random

data class Message(
    val id: String = "id" + Random.nextInt(),
    val nonce: String = "",
    val senderId: String,
    val recipientId: String,
    val senderName: String,
    val recipientName: String,
    val content: String,
    val status: String = "CHAT"
)