package sortable.matching

import scala.collection.GenIterable
import scala.collection.Map
import scala.collection.mutable
import scala.collection.parallel.ParSeq

class Matcher(products: TraversableOnce[Product],
              ambiguityStrategy: Matcher.AmbiguityStrategy = Matcher.NoMatchOnAmbiguity) {
  import Matcher._

  lazy val productIndex: Map[String,       // Manufacturer
                             Map[String,   // Model
                                 Product]] = {
    val idx = mutable.Map[String, mutable.Map[String, Product]]()
    for (p <- products) {
      val man = p.manufacturer.toUpperCase
      val mod = p.model.toUpperCase
      idx.get(man) match {
        case Some(mods) => mods += (mod -> p)
        case None       => idx += (man -> mutable.Map(mod -> p))
      }
    }
    idx
  }

  def matchListings(listings: TraversableOnce[Listing]): GenIterable[Result] =
    listings.to[ParSeq].groupBy(matchListing).flatMap {
      case (Some(p), ls) => Some(Result(p.name, ls.seq))
      case _             => None
    }

  def matchListing(listing: Listing): Option[Product] = {
    val title = listing.title.toUpperCase
    val candidates = for {
      mods <- matchManufacturer(listing.manufacturer.toUpperCase)
      (mod, p) <- mods if title.contains(mod)
    } yield p
    candidates match {
      case Array()  => None
      case Array(p) => Some(p)
      case ps       =>
        val filteredByFamily = for {
          p <- ps
          f <- p.family.toArray if title.contains(f.toUpperCase)
        } yield p
        (filteredByFamily, ambiguityStrategy) match {
          case (Array(p), _) =>
            Some(p)
          case (_, ThrowOnAmbiguity) =>
            throw new AmbiguousMatchException(listing, ps)
          case (_, NoMatchOnAmbiguity) =>
            None
          case (Array(), AnyOneMatchOnAmbiguity) =>
            Some(ps.head)
          case (ps, AnyOneMatchOnAmbiguity) =>
            Some(ps.head)
        }
    }
  }

  def matchManufacturer(manufacturer: String): Array[Map[String, Product]] =
    productIndex.get(manufacturer) match {
      case Some(x) =>
        Array(x)
      case None =>
        val toks = manufacturer.split("\\s+").filter(_.nonEmpty)
        toks.flatMap(productIndex.get) match {
          case Array() if toks.length > 1 =>
            productIndex.get(toks.map(_.head).mkString).toArray
          case xs =>
            xs
        }
    }
}

object Matcher {
  sealed abstract class AmbiguityStrategy
  case object ThrowOnAmbiguity extends AmbiguityStrategy
  case object NoMatchOnAmbiguity extends AmbiguityStrategy
  case object AnyOneMatchOnAmbiguity extends AmbiguityStrategy

  class AmbiguousMatchException(listing: Listing, products: Seq[Product])
      extends Exception(s"$listing matches multiple products: $products")
}
