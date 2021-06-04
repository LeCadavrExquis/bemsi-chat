package model

data class User(val id: String, val name: String, val friends: List<String>) {
    fun getChannels(): List<MessagingChannel> {
        return friends.zip(1..friends.size).map { (friend, idx) ->
            val mod = idx.mod(5)
            MessagingChannel(
                name = friend, iconSrc = "avatars/avatar$mod.svg")
        }
    }
}