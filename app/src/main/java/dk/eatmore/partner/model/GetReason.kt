package dk.eatmore.partner.model

data class GetReason(val msg: String = "",
                     val Openinghours: List<OpeninghoursItem>? = null,
                     val status: Boolean = false)


data class OpeninghoursItem(

                            val reason: String = "",
                            val actionBy: String = "",
                            val is_deleted: String = "",
                            val is_activated: String = "",
                            val or_id: String = "",
                            val restaurant_id: String = "",
                            val action_dt: String = ""


)