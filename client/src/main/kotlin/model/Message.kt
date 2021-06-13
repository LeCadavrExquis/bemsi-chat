package model

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val recipientId: String,
    val senderName: String,
    val recipientName: String,
    val content: String,
    val status: String
)