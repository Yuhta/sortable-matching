package sortable.matching

import spray.json.DefaultJsonProtocol

case class Listing(title: String,
                   manufacturer: String,
                   currency: String,
                   price: String)

object ListingJsonProtocol extends DefaultJsonProtocol {
  implicit val listingFormat = jsonFormat4(Listing.apply)
}
