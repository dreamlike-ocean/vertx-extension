package top.dreamlike.db



import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.apache.ibatis.session.SqlSessionFactory
import top.dreamlike.helper.VirtualThreadUnsafe
import java.util.concurrent.Executor


interface MybatisMarkInterface


inline fun <reified T : MybatisMarkInterface> SqlSessionFactory.getMapper() = this.openSession().getMapper(T::class.java)

inline fun <T> StartOnCurrentContext(context: Context = Vertx.currentContext() ?: throw IllegalStateException("current context is null"),
                                     crossinline supplier: () -> T): Future<T> {
    val res = Promise.promise<T>()
    VirtualThreadUnsafe.VIRTUAL_THREAD_BUILDER
            .apply(Executor { r -> context.runOnContext { r.run() } })
            .start {
                try {
                    val block_op_result = supplier()
                    context.runOnContext {
                        res.complete(block_op_result)
                    }
                } catch (e: Throwable) {
                    context.runOnContext {
                        res.fail(e)
                    }
                }
            }

    return res.future()
}

