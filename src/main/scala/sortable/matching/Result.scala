package sortable.matching

import spray.json.DefaultJsonProtocol

case class Result(productName: String, listings: Seq[Listing])

object ResultJsonProtocol extends DefaultJsonProtocol {
  import ListingJsonProtocol._
  implicit val resultFormat = jsonFormat(Result.apply,
                                         "product_name",
                                         "listings")
}
