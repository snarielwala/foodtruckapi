package controllers

import java.sql.{Connection, DriverManager, PreparedStatement}
import model.Truck
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.control.NonFatal

/**
 * Created by pdesai on 9/19/15.
 */
object MySQL {

  var connection: Connection = null
  var preparedStmt = mutable.HashMap[String, PreparedStatement]()

  def init(): Unit = {
    val driver = "com.mysql.jdbc.Driver"
    

    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)

    preparedStmt += "list" -> connection.prepareStatement("SELECT * FROM food_trucks")
    preparedStmt += "insertIntoTable" -> connection.prepareStatement("INSERT INTO food_trucks (truck_name, location, time, day, hood, cuisine, latitude,longitude, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
    preparedStmt += "updateCuisine" -> connection.prepareStatement("UPDATE food_trucks SET cuisine = ?, description = ? where truck_name = ?")

  }

  def list : Either[String, Seq[Truck]]= {
    val func = "list"
    val truckList = new ListBuffer[Truck]

    try {
      val stmt = preparedStmt.get("list").get
      val rs = stmt.executeQuery()
      while (rs.next()) {
        truckList += Truck(rs.getString("truck_name"),
          rs.getString("location"),
          rs.getString("time"),
          rs.getString("day"),
          rs.getString("hood"),
          rs.getString("cuisine"),
          rs.getString("description"),
          rs.getString("latitude"),
          rs.getString("longitude"))
      }
      truckList.isEmpty match {
        case true => Left("No data found")
        case false => Right(truckList.toSeq)
      }
    }
   catch{
    case NonFatal(exc) =>
      println(s"$func: ${exc.toString}")
      Left(exc.toString)
  }
  }

  def close(): Unit = {
    connection.close()
  }

  def insertIntoTable(truck: Truck) : Either[String, String]= {
    val func = "insertIntoTable"
    try {
      val stmt = preparedStmt.get("insertIntoTable").get
      stmt.setString(1, s"${truck.truck_name}")
      stmt.setString(2, s"${truck.location}")
      stmt.setString(3, s"${truck.time}")
      stmt.setString(4, s"${truck.day}")
      stmt.setString(5, s"${truck.hood}")
      stmt.setString(6, s"${truck.cuisine}")
      stmt.setString(7, s"${truck.latitude}")
      stmt.setString(8, s"${truck.longitude}")
      stmt.setString(9, s"${truck.description}")
      stmt.execute()
      Right("Success")
    } catch{
      case NonFatal(exc) =>
        println(s"$func: ${exc.toString}")
        Left(exc.toString)
    }
  }

  def updateCuisine(cuisine: String, desc: String, truck_name: String): Either[String, String] = {
    val func = "insertIntoTable"
    try {
      val stmt = preparedStmt.get("updateCuisine").get
      stmt.setString(1, cuisine)
      stmt.setString(2, desc)
      stmt.setString(3, truck_name)
      stmt.execute()
      Right("Success")
    } catch{
      case NonFatal(exc) =>
        println(s"$func: ${exc.toString}")
        Left(exc.toString)
    }
  }


}
