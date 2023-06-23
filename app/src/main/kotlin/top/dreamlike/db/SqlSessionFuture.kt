package top.dreamlike.db

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import org.apache.ibatis.session.SqlSession

data class SqlSessionFuture<T>(val sqlSession: SqlSession,val op_future:Future<T>) : Future<T> by op_future {

}