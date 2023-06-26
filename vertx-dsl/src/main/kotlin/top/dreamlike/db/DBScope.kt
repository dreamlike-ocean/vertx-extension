package top.dreamlike.db

import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.vertxFuture
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import kotlin.experimental.ExperimentalTypeInference

class DBScope(val context: Context,val factory: SqlSessionFactory) {
    companion object {
        const val SQL_SESSION_KEY = "_sql_session_key_"

        suspend inline fun <T> SqlSessionFactory.openDBScope(crossinline scope: suspend DBScope.() -> T) = DBScope(Vertx.currentContext(),this).scope()

    }

    suspend inline fun <reified T : MybatisMarkInterface, V> AsyncMapper(
        once: Boolean = true,
        autoCommit: Boolean = true,
        crossinline handle:T.() -> V
    ): V {
        val sqlSession = currentSession() ?: factory.openSession(autoCommit)
        Vertx.currentContext().putLocal(SQL_SESSION_KEY, sqlSession)
        val future = StartOnCurrentContext(context) {
            try {
                sqlSession.getMapper(T::class.java).handle()
            } finally {
                if (once) {
                    sqlSession.close()
                    context.remove(SQL_SESSION_KEY)
                }
            }
        }

        return future.await()
    }
    fun currentSession(): SqlSession? = context.getLocal<SqlSession>(SQL_SESSION_KEY)
}
