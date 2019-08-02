package dk.eatmore.partner.utils

import android.os.AsyncTask


class BackgroundTaskHelper( var backgroundTaskListener: BackgroundTaskListener?) : AsyncTask<Void?, Void?, Void?>() {

    interface BackgroundTaskListener {
        fun doTask()
        fun doAfterTask()
    }

    override fun doInBackground(vararg voids: Void?): Void? {
        backgroundTaskListener!!.doTask()
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        backgroundTaskListener!!.doAfterTask()
        backgroundTaskListener = null
    }
}
