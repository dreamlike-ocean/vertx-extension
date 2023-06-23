package top.dreamlike.db


import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.core.Vertx
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import top.dreamlike.Owner
import top.dreamlike.helper.VirtualThreadUnsafe
import java.util.concurrent.Executor
import javax.sql.DataSource


private fun createFactory(dataSource: DataSource): SqlSessionFactory {
    val factory = JdbcTransactionFactory()
    val environment = Environment("vertx", factory, dataSource)
    val configuration = Configuration(environment)
    configuration.setLogImpl(StdOutImpl::class.java)
    configuration.addMapper(Owner.OwnerMapper::class.java)
    configuration.setMapUnderscoreToCamelCase(true)
    return SqlSessionFactoryBuilder()
            .build(configuration)
}

private fun createDataSource(): DataSource {
    val config = HikariConfig()

    config.jdbcUrl = "jdbc:mariadb://[::1]:3306/petclinic"
//    config.jdbcUrl = "jdbc:mysql://[::1]:3306/petclinic"
    config.username = "root"
    config.password = "123456789"
    config.addDataSourceProperty("cachePrepStmts", "true")
    config.addDataSourceProperty("prepStmtCacheSize", "250")
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    config.maximumPoolSize = 1

    return HikariDataSource(config)
}

val GLOBAL_FACTORY = createFactory(createDataSource())


interface MybatisMarkInterface


inline fun <reified T : MybatisMarkInterface> getMapper() = GLOBAL_FACTORY.openSession().getMapper(T::class.java)

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

