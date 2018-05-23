import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.dao.*


// Define the database tables to be created. Extends IntIdTable which creates an integer primary key named: id
object Users2 : IntIdTable() {
    val name = varchar("username", 50).index()
    val city = reference("city", Cities) // User table references City table
    val age = integer("age")
}

// Define the entity class to be mapped to the equivalent database table by using the compannion object.
class User2(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users2)

    var name by Users2.name
    var city by City referencedOn Users2.city
    var age by Users2.age
}

object Cities: IntIdTable() {
    val name = varchar("username", 50)
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)

    var name by Cities.name
    val users by User referrersOn Users2.city //TwoWayBinding.
}

fun main(args: Array<String>) {

//    val dataSource: MysqlDataSource = MysqlDataSource()
    val dataSource: MysqlDataSource = MysqlConnectionPoolDataSource()
    dataSource.serverName = Secret.SERVER
    dataSource.port = Secret.PORT
    dataSource.databaseName = Secret.DB
    dataSource.user = Secret.USER
    dataSource.setPassword(Secret.PASSWORD)


//    Database.connect("jdbc:mysql://localhost:3306/test", user="root",password = "root", driver = "com.mysql.jdbc.Driver")
    Database.connect(dataSource)
    transaction {
        logger.addLogger(StdOutSqlLogger)
    //Opret tabellerne i databasen hvis de ikke er der allerede.
        create (Cities, Users2)

        val stPete = City.new {
            name = "St. Petersburg"
        }

        val munich = City.new {
            name = "Munich"
        }

        User.new {
            username = "a"
            city = stPete
            age = 5
        }

        User.new {
            username = "b"
            city = stPete
            age = 27
        }

        User.new {
            username = "c"
            city = munich
            age = 42
        }

        println("Cities: ${City.all().joinToString {it.name}}")
        println("Users2 in ${stPete.name}: ${stPete.users.joinToString {it.username}}")
        println("Adults: ${User.find { Users2.age greaterEq 18 }.joinToString {it.username}}")
    }
}