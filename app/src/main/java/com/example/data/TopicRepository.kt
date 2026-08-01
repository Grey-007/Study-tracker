package com.example.data

import kotlinx.coroutines.flow.Flow

class TopicRepository(private val topicDao: TopicDao) {
    fun getTopicsByExam(exam: String): Flow<List<Topic>> = topicDao.getTopicsByExam(exam)

    suspend fun updateTopic(topic: Topic) {
        topicDao.updateTopic(topic)
    }

    suspend fun insertTopic(topic: Topic) {
        topicDao.insertTopic(topic)
    }

    suspend fun deleteTopic(topic: Topic) {
        topicDao.deleteTopic(topic)
    }

    suspend fun insertTopics(topics: List<Topic>) {
        topicDao.insertTopics(topics)
    }

    suspend fun getTopicCount(): Int {
        return topicDao.getTopicCount()
    }
}
