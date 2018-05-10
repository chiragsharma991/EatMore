package dk.eatmore.softtech360.model


class UserDetail_model {


    private var name: String? = null

    private var realname: String? = null

    private var team: String? = null

    private var firstappearance: String? = null

    private var createdby: String? = null

    private var publisher: String? = null

    private var imageurl: String? = null

    private var bio: String? = null



    fun getName(): String? {
        return name
    }

    fun getRealname(): String? {
        return realname
    }

    fun getTeam(): String? {
        return team
    }
    fun getFirstappearance(): String? {
        return firstappearance
    }
    fun getCreatedby(): String? {
        return createdby
    }
    fun getPublisher(): String? {
        return publisher
    }
    fun getImageurl(): String? {
        return imageurl
    }
    fun getBio(): String? {
        return bio
    }
}