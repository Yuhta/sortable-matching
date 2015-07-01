package sortable.matching

import spray.json.DefaultJsonProtocol

case class Product(name: String,
                   manufacturer: String,
                   family: Option[String],
                   model: String,
                   announcedDate: String)

object ProductJsonProtocol extends DefaultJsonProtocol {
  implicit val productFormat = jsonFormat(Product.apply,
                                          "product_name",
                                          "manufacturer",
                                          "family",
                                          "model",
                                          "announced-date")
}
