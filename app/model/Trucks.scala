package model

import play.api.libs.json._

/**
 * Created by pdesai on 9/19/15.
 */
case class Truck(truck_name: String, location: String, time: String, day: String, hood: String, cuisine: String, description: String, latitude: String, longitude: String)

object Truck {

  implicit object TweetFormat extends Format[Truck] {

    // convert from Tweet object to JSON (serializing to JSON)
    def writes(truck: Truck): JsValue = {
      //  tweetSeq == Seq[(String, play.api.libs.json.JsString)]
      val truckSeq = Seq(
        "truck_name" -> JsString(truck.truck_name),
        "location" -> JsString(truck.location),
        "time" -> JsString(truck.time),
        "day" -> JsString(truck.day),
        "hood" -> JsString(truck.hood),
        "cuisine" -> JsString(truck.cuisine),
        "description" -> JsString(truck.description),
        "latitude" -> JsString(truck.latitude),
        "longitude" -> JsString(truck.longitude)
      )
      JsObject(truckSeq)
    }

    // convert from JSON string to a Tweet object (de-serializing from JSON)
    // (i don't need this method; just here to satisfy the api)
    def reads(json: JsValue): JsResult[Truck] = {
      JsSuccess(Truck("", "", "", "", "", "", "", "", ""))
    }

  }

}