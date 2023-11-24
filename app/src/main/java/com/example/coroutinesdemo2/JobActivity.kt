package com.egecius.coroutinesdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coroutinesdemo2.R
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class JobActivity : AppCompatActivity() {

    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job)
//        initJob()

//        job_button.setOnClickListener {
//            startOrResetJob()
//        }
    }

//    private fun startOrResetJob() {
//        if (job_progress_bar.progress > 0 && job_progress_bar.progress != PROGRESS_MAX) {
//            resetJob()
//        } else {
//            startJob()
//        }
//    }
//
//    private fun resetJob() {
//        job.cancel()
//        initJob()
//    }
//
//    private fun startJob() {
//        CoroutineScope(IO + job).launch {
//            for (i in PROGRESS_START..PROGRESS_MAX) {
//                delay(DELAY)
//                job_progress_bar.progress = i
//            }
//            job.complete()
//        }
//        job_button.text = getString(R.string.cancel)
//        job_complete_text.text = ""
//    }
//
//    private fun initJob() {
//        job = Job()
//        job.invokeOnCompletion {
//            val isCancelled = it != null
//            if (isCancelled) {
//                val msg = it?.message ?: "unknown cause"
//                val name = Thread.currentThread().name
//                // invokeOnCompletion is called on the thread as the scope that the job was added to
//                Log.i("Eg:JobActivity:58", "initJob invokeOnCompletion thread name: $name")
//                showToast(msg)
//                resetViews()
//            } else {
//                showJobComplete()
//                initJob()
//            }
//        }
//    }
//
//    private fun showJobComplete() {
//        GlobalScope.launch(Main) {
//            job_complete_text.text = getString(R.string.job_complete)
//            job_button.text = getString(R.string.start_again)
//        }
//    }
//
//    private fun showToast(msg: String) {
//        GlobalScope.launch(Main) {
//            Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun resetViews() {
//        GlobalScope.launch(Main) {
//            job_progress_bar.progress = PROGRESS_START
//            job_progress_bar.max = PROGRESS_MAX
//            job_button.text = getString(R.string.start)
//            job_complete_text.text = ""
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        job.cancel()
//    }

    companion object {

        private const val TAG: String = "AppDebug"

        private const val PROGRESS_MAX = 100
        private const val PROGRESS_START = 0
        private const val JOB_TIME = 2000 // ms
        private const val DELAY = (JOB_TIME / PROGRESS_MAX).toLong()
    }
}
