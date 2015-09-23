/**
 * Created by snarielwala on 9/23/15.
 */

import java.util.Calendar

import model.{ByCuisine, Truck, ByDay}
import org.jsoup.Jsoup
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Document
import play.api.Application
import play.api.GlobalSettings
import play.api.Play.current
import play.api.libs.concurrent._
import play.api.libs.concurrent.Execution.Implicits._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import controllers.MySQL
import play.api.{Logger, GlobalSettings}


object Global extends GlobalSettings {

  var links = new mutable.HashMap[String, List[String]]
  links.put("cuisine", List("http://www.seattlefoodtruck.com/index.php/by-cuisine/asian/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/bbq/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/burgerssandwiches/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/coffee/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/hot-dogs/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/italian/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/mexican/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/mediterranean/",
    "http://www.seattlefoodtruck.com/index.php/by-cuisine/sweets/"))

  links.put("day", List("http://www.seattlefoodtruck.com/index.php/by-day/monday/",
    "http://www.seattlefoodtruck.com/index.php/by-day/tuesday/",
    "http://www.seattlefoodtruck.com/index.php/by-day/wednesday/",
    "http://www.seattlefoodtruck.com/index.php/by-day/thursday/",
    "http://www.seattlefoodtruck.com/index.php/by-day/friday/",
    "http://www.seattlefoodtruck.com/index.php/by-day/saturday/",
    "http://www.seattlefoodtruck.com/index.php/by-day/sunday/"))

  links.put("hood", List("http://www.seattlefoodtruck.com/index.php/neighborhoods/chucks-hop-shop/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/south-lake-union/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/downtown-seattle/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/sodo/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/ballard/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/queen-anne/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/eastside/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/south-end/",
    "http://www.seattlefoodtruck.com/index.php/neighborhoods/everywhere-else/"))

  override def onStart(app: Application) {
    Logger.info("Food Truck Application Start")
    MySQL.init()
    indexFoodTruckData()

  }

  override def onStop(app: Application): Unit = {
  Logger.info("Food Truck Application Stop")
  }

  def indexFoodTruckData() = {
    Akka.system.scheduler.schedule(0 seconds, 1 day) {

      Logger.info("Updating Food Truck Data: "+Calendar.getInstance().getTime())

      val truckByCuisines = getByCuisine(links.get("cuisine").get)
      val datas: List[ByDay] = getByDay(links.get("day").get)
      //println(x)

      MySQL.init()
      datas.foreach(
        data =>MySQL.insertIntoTable(new Truck(data.name, data.location, data.time, data.day, data.hood, "", "","",""))
      )

      truckByCuisines.foreach(truck =>
        MySQL.updateTruck(truck.cuisine, truck.description, truck.name))

    }

    def getByCuisine(links: List[String]): List[ByCuisine] = {
      var listByCuisines = new ListBuffer[ByCuisine]
      links.foreach(link => {
        val slashes = link.split('/')
        val cuisine = slashes.last

        val doc: Document = Jsoup.connect(link).get()
        val anchorTags = doc.select(".entry-content tr")
        val itr = anchorTags.iterator()

        while (itr.hasNext) {
          val anchorTag = itr.next()
          var name = anchorTag.select("a")
          val link = anchorTag.select("td").last().text()
          listByCuisines += ByCuisine(cuisine, name.text(), link)
        }
      })
      listByCuisines.toList
    }

    def getByDay(links: List[String]): List[ByDay] = {
      var listByDay = new ListBuffer[ByDay]
      links.foreach(link => {
        val slashes = link.split('/')
        val day = slashes.last

        val doc: Document = Jsoup.connect(link).get()
        val anchorTags = doc.select(".entry-content tr")
        val itr = anchorTags.iterator()

        while (itr.hasNext) {
          val anchorTag = itr.next()
          val name = anchorTag.select("a")
          val hood = anchorTag.select("strong")
          val data = anchorTag.select("td").last()
          val newdata = data.text().replaceAll(hood.text(), "")
          val data2 = newdata.split(',')

          listByDay += new ByDay(name.text(), hood.text(), data2.init.mkString(","), data2.last, day)
        }
      })
      listByDay.toList
    }


  }
}
