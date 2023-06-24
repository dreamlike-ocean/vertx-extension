package top.dreamlike

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.apache.ibatis.logging.stdout.StdOutImpl
import org.apache.ibatis.mapping.Environment
import org.apache.ibatis.session.Configuration
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory
import javax.sql.DataSource

val GLOBAL_FACTORY = createFactory(createDataSource())

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
