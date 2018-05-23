import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.dao.*


// Define the database tables to be created. Extends IntIdTable which creates an integer primary key named: id
object Users : IntIdTable() {
    val username = varchar("username", 50).index()
    val email = varchar("email", 50)
    val password = varchar("password", 80)
}

// Define the entity class to be mapped to the equivalent database table by using the compannion object.
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users2)

    var username by Users.username
    var email by Users.email
    var password by Users.password
}

object Images: IntIdTable() {
    val name = varchar("username", 50)
    val path = varchar("path", 50)
}

class Image(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Image>(Images)

    var name by Images.name
    var path by Images.path
}

object Articles: IntIdTable() {
    val name = varchar("username", 50)
    val caption = varchar("caption", 200)
    val text = text("text")
}

class Article(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Article>(Articles)

    var name by Articles.name
    var caption by Articles.caption
    var text by Articles.text
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
        create (Images, Users, Articles)


        User.new {
            username = "demouser"
            email = "test@mail.com"
            password = "test123"
        }



        }
}