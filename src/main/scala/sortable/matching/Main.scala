package sortable.matching

import scala.io.Source

object Main extends App {
  import ProductJsonProtocol._, ListingJsonProtocol._, ResultJsonProtocol._
  import spray.json._

  if (args.size != 2) sys.error("Usage: match PRODUCTS-FILE LISTINGS-FILE")
  val sources = args.map(Source.fromFile)
  try {
    val Array(productJsons, listingJsons) = sources.map(_.getLines.map(_.parseJson))
    val products = productJsons.map(_.convertTo[Product])
    val listings = listingJsons.map(_.convertTo[Listing])
    new Matcher(products).matchListings(listings).foreach(r => println(r.toJson))
  } finally sources.foreach(_.close())
}
