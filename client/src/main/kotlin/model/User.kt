package model

data class User(val id: String, val name: String, val friends: List<Pair<String,String>>) {
    fun getChannels(): List<Channel> {
        return friends.zip(1..friends.size).map { (friend, idx) ->
            val mod = idx.mod(5)
            Channel(
                name = friend.second, iconSrc = "avatars/avatar$mod.svg")
        }
    }
}