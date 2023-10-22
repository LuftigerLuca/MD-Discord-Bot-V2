package eu.luftiger.mdbot.database

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource
import com.mysql.cj.jdbc.MysqlDataSource
import eu.luftiger.mdbot.configuration.Configuration
import java.sql.SQLException
import java.util.logging.Logger
import javax.sql.DataSource

object DataSourceProvider {

    fun initMySQLDataSource(logger: Logger, configuration: Configuration) : DataSource{
        val dataSource : MysqlDataSource = MysqlConnectionPoolDataSource()
        dataSource.setURL("jdbc:mysql://${configuration.database.username}:${configuration.database.password}@${configuration.database.host}:${configuration.database.port}/${configuration.database.database}")
        testDataSource(logger, dataSource)
        return dataSource
    }

    @Throws(SQLException::class)
    private fun testDataSource(logger: Logger, dataSource: DataSource){
        val connection = dataSource.connection
        if(!connection.isValid(1000)) throw SQLException("Could not connect to database!")
        logger.info("Successfully connected to database!")
    }
}