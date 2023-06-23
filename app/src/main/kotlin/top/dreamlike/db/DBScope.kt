package top.dreamlike.db

import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import org.apache.ibatis.session.SqlSession
import kotlin.experimental.ExperimentalTypeInference

class DBScope(val context: Context) {
    companion object {
        const val SQL_SESSION_KEY = "_sql_session_key_"
        suspend inline fun <T> Context.openDBScope(crossinline scope: suspend DBScope.() -> T) = DBScope(this).scope()

    }

    suspend inline fun <reified T : MybatisMarkInterface, V> AsyncMapper(
        once: Boolean = true,
        autoCommit: Boolean = true,
        crossinline handle:T.() -> V
    ): Pair<SqlSession, V> {
        val sqlSession = currentSession() ?: GLOBAL_FACTORY.openSession(autoCommit)
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

        return sqlSession to future.await()
    }


    fun currentSession(): SqlSession? = context.getLocal<SqlSession>(SQL_SESSION_KEY)

}