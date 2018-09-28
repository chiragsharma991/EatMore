package dk.eatmore.partner.testing

import android.util.Log
import android.R.attr.countDown
import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.CountDownLatch


object Test_two : Thread() {


    override fun run() {

            println("run thread call ---")




/*
           val latch = CountDownLatch(1)
     val value = IntArray(1)
     val uiThread = object : HandlerThread("UIHandler") {
         override fun run() {
             println("run thread call ---")

             value[0] = 2
             latch.countDown() // Release await() in the test thread.
         }
     }
     uiThread.start()
     latch.await() // Wait for countDown() in the UI thread. Or could uiThread.join();
     // value[0] holds 2 at this point.
     println("countDown ---"+value[0])
*/

    }

    @JvmStatic
    fun main(args: Array<String>) {

        println("run main ---")





        val t1 = Test_two
        t1.setUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread, e: Throwable) {
                System.out.printf("Exception thrown by %s with id : %d",
                        t.name, t.id)
                println("\n" + e.javaClass)
            }
        })
        t1.start()

    }

}
