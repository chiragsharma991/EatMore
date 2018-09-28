package dk.eatmore.partner.utils

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object ConversionUtils {


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

    fun getCalculatedDate(dateFormat: String, days: Int): String {
        val cal = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    fun convertCurrencyToDanish(amount: String): String? {
        val deviceLocale = Locale.getDefault().language //  if (deviceLocale.equalsIgnoreCase("en")) {
        //      return formatValueToMoney(amount);
        //  } else {
        val mNumberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)
        var convertedAmount: String? = mNumberFormat.format(amount.toDouble())
        println(convertedAmount)
        if (convertedAmount != null && convertedAmount.length > 2) {
            convertedAmount = convertedAmount.substring(0, convertedAmount.length - 2)
        }
        return convertedAmount+"kr"
        //  }
    }

}