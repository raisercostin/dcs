package org.raisercostin.sekyll

import play.twirl.api.Template1
import play.twirl.api.Html
import eu.dcsi.sekyll.docs._

case class Customer(image:String)
case class Solution(name:String, link:String)
object Solution{
  def apply(name:String):Solution = Solution(name,name)
}

/**
 * The site gets passed to every page.
 * Is a wrapper around the generic RawSite making the mapping to final domain types.
 */
case class Site(baseUrl: String, path: String, currentLagomVersion: String, currentDocsVersion: String,
                        blogSummary: BlogSummary, assetFingerPrint: String){
  def route = RawSite.route
  def route(image:String) = RawSite.route(image)
  def customers:Seq[Customer] = RawSite.documents[Customer](RawSite.collections.customers)
  def solutions:Seq[Solution] = RawSite.documents[Solution](RawSite.collections.solutions)
  def pages = RawSite.pages
}


object RawSite {
  object collections{
    val customers = "customers"
    val solutions = "solutions"
  }
  /**Use the routes in your code for statically checked links.*/
  object route{
    val contact = "contact"
    val home = "index.html"
    val about = "about"
    val services = "services"
  }
  
  def route(image:String):String = s"images/$image"
  
  def documents[T](collection:String):Seq[T] = collection match {
    case collections.customers =>
      Seq(Customer(route("oracle.png")),Customer(route("gothaer.png")),Customer(route("DFPRADM.png")),Customer(route("EuroCenterBank.png"))).asInstanceOf[Seq[T]]
    case collections.solutions =>
      Seq(Solution("Products",route.services),Solution("Development"),Solution("Consultancy"),Solution("Maintenance & Support"),Solution("Academy")).asInstanceOf[Seq[T]]
    case  _ =>
      throw new IllegalArgumentException(s"Collection $collection is not defined")
  }
  
  //Yaml.parse(frontMatter)
  // Templated pages to generate
  val pages: Seq[(String, Template1[Site, Html])] = Seq(
    "index2.html" -> html.index,
    "get-involved.html" -> html.getinvolved,
    "get-started.html" -> html.getstarted,
    "get-started-java.html" -> html.getstartedjava,
    "get-started-java-sbt.html" -> html.getstartedjavasbt,
    "get-started-java-maven.html" -> html.getstartedjavamaven,
    "get-started-scala.html" -> html.getstartedscala,

    route.about -> eu.dcsi.website.html.about,
    route.contact -> eu.dcsi.website.html.contact,
    "blog-home-1.html" -> eu.dcsi.website.html.blogHome1,
    "blog-home-2.html" -> eu.dcsi.website.html.blogHome2,
    //"blog-post.html" -> eu.dcsi.website.html.blogPost,
    "faq.html" -> eu.dcsi.website.html.faq,
    "full-width.html" -> eu.dcsi.website.html.fullWidth,
    route.home -> eu.dcsi.website.html.index,
    "404.html" -> eu.dcsi.website.html.page404,
    "portfolio-1-col.html" -> eu.dcsi.website.html.portfolio1col,
    "portfolio-2-col.html" -> eu.dcsi.website.html.portfolio2col,
    "portfolio-3-col.html" -> eu.dcsi.website.html.portfolio3col,
    "portfolio-4-col.html" -> eu.dcsi.website.html.portfolio4col,
    "portfolio-item.html" -> eu.dcsi.website.html.portfolioItem,
    "pricing.html" -> eu.dcsi.website.html.pricing,
    route.services -> eu.dcsi.website.html.services,
    "sidebar.html" -> eu.dcsi.website.html.sidebar)
}