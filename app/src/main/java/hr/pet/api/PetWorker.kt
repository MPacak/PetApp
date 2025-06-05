package hr.pet.api

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class PetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams){
    override fun doWork(): Result {
        //bg
        PetOrgFetcher(context).fetchInitalPetOrg("dog", 1, 10, "CA");
        return Result.success()
    }

}