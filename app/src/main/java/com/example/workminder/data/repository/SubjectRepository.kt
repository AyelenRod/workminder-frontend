package com.example.workminder.data.repository

import com.example.workminder.data.model.Subject
import com.example.workminder.data.remote.RetrofitClient
import com.example.workminder.data.remote.ApiResponse
import retrofit2.Response
import com.example.workminder.data.remote.ApiService
import com.example.workminder.data.local.SubjectDao

class SubjectRepository(
    private val subjectDao: SubjectDao,
    private val api: ApiService
) {
    fun getAllSubjects(): kotlinx.coroutines.flow.Flow<List<Subject>> = subjectDao.getAllSubjects()

    suspend fun syncSubjects() {
        try {
            val response = api.getSubjects()
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteSubjects = response.body()?.data
                if (!remoteSubjects.isNullOrEmpty()) {
                    subjectDao.insertSubjects(remoteSubjects)
                }
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }

    suspend fun createSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
        try {
            val response = api.createSubject(mapOf("subject_name" to subject.subject_name, "color" to subject.color))
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteSubj = response.body()?.data
                if (remoteSubj != null) {
                    if (remoteSubj.id != subject.id) {
                        subjectDao.deleteSubjectById(subject.id)
                    }
                    subjectDao.insertSubject(remoteSubj)
                }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.insertSubject(subject)
        try {
            api.updateSubject(subject.id, mapOf("subject_name" to subject.subject_name, "color" to subject.color))
        } catch (e: Exception) {
        }
    }

    suspend fun deleteSubject(id: String) {
        subjectDao.deleteSubjectById(id)
        try {
            api.deleteSubject(id)
        } catch (e: Exception) {
        }
    }
}
