package org.raisercostin.sekyll

import play.twirl.api.Template1
import eu.dcsi.sekyll.docs.LagomContext
import play.twirl.api.Html
import eu.dcsi.sekyll.docs._

case class Customer(image:String)


object Site {
  /**Use the routes in your code for statically checked links.*/
  object route{
    val contact = "contact"
    val home = "index.html"
    val about = "about"
  }
  
  def documents[T](collection:String):Seq[T] =
    Seq(Customer(route("oracle.png")),Customer(route("gothaer.png")),Customer(route("DFPRADM.png")),Customer(route("EuroCenterBank.png"))).asInstanceOf[Seq[T]]
  
  def route(image:String):String = s"images/$image"
  
  //Yaml.parse(frontMatter)
  // Templated pages to generate
  val pages: Seq[(String, Template1[LagomContext, Html])] = Seq(
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
    Site.route.home -> eu.dcsi.website.html.index,
    "404.html" -> eu.dcsi.website.html.page404,
    "portfolio-1-col.html" -> eu.dcsi.website.html.portfolio1col,
    "portfolio-2-col.html" -> eu.dcsi.website.html.portfolio2col,
    "portfolio-3-col.html" -> eu.dcsi.website.html.portfolio3col,
    "portfolio-4-col.html" -> eu.dcsi.website.html.portfolio4col,
    "portfolio-item.html" -> eu.dcsi.website.html.portfolioItem,
    "pricing.html" -> eu.dcsi.website.html.pricing,
    "services.html" -> eu.dcsi.website.html.services,
    "sidebar.html" -> eu.dcsi.website.html.sidebar)
}