package dk.eatmore.partner.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateCalculation {


     fun getDateformat (timestamp : String , changeFormatInto :SimpleDateFormat , targetFormat : String) : String{

        val formatter: DateFormat = SimpleDateFormat(targetFormat)
        val mDate = formatter.parse(timestamp)
        val time=mDate.time
        val date =changeFormatInto.format(time)
        return date
    }

     fun getCalculatedTime (timestamp : String  , targetFormat : String) : Long {

         val formatter: DateFormat = SimpleDateFormat(targetFormat)
         val mDate = formatter.parse(timestamp)
         val time=mDate.time
        return time
    }

}