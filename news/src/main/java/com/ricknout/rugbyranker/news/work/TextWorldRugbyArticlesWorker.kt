package com.ricknout.rugbyranker.news.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.news.repository.ArticlesRepository
import com.ricknout.rugbyranker.news.vo.ArticleType
import javax.inject.Inject

class TextWorldRugbyArticlesWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    articlesRepository: ArticlesRepository
) : WorldRugbyArticlesWorker(context, workerParams, ArticleType.TEXT, articlesRepository) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_articles_worker_text"
    }
}
