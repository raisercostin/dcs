package org.raisercostin.sekyll

import play.twirl.api.Template1
import play.twirl.api.Html
import eu.dcsi.sekyll.docs._
import org.raisercostin.jedi.Locations

case class Customer(image:String)
case class Solution(name:String, link:String)
case class DcsPartner(name:String, url:String, logo:String)

object Solution{
  def apply(name:String):Solution = Solution(name,name)
}

/**
 * The site gets passed to every page.
 * Is a wrapper around the generic RawSite making the mapping to final domain types.
 */
case class Site(currentLagomVersion: String, currentDocsVersion: String,
                        blogSummary: BlogSummary, assetFingerPrint: String){
  def route = DcsSite.route
  def route(image:String) = RawSite.routeImage(image)
  def customers:Seq[Customer] = RawSite.documents[Customer](DcsSite.collections.customers)
  def solutions:Seq[Solution] = RawSite.documents[Solution](DcsSite.collections.solutions)
  def partners:Seq[DcsPartner] = RawSite.documents[DcsPartner](DcsSite.collections.partners)
  def pages = RawSite.pages
  
  // Set this to Some("your-github-account-name") if you want to deploy the docs to the gh-pages of your own fork
  // of the repo
  //val gitHubAccount: Option[String] = None
  //for master
  //val (baseUrl, context) = ("http://raisercostin.org/sekyll","/sekyll")
  //for template
  //val (baseUrl, context) = ("http://localhost:8080","")
    //gitHubAccount match {
    //case Some(account) => (s"http://$account.github.io/lagom.github.io", "/lagom.github.io")
    //case None => ("https://www.lagomframework.com", "")
  //}
  def baseUrl: String = RawSite.baseUrl.getOrElse("")
  @deprecated
  def path: String = baseUrl
  @deprecated
  def context: String = baseUrl
  
  def collections:Seq[OutputFile] = RawSite.rawCollections
}

object DcsSite{
  object collections{
    val customers = "customers"
    val solutions = "solutions"
    val partners = "partners"
  }
  /**Use the routes in your code for statically checked links.*/
  object route{
    //TODO add validation that all links are used.
    //var used = Seq[String]()
    def use(name:String):String = {
      //used = name +: used
      name
    }
    
    val contact = use("contact")
    val home = use("index.html")
    val about = use("about")
    val services = use("services")
    val partners = use("partners")
  }
}

object RawSite {
  import DcsSite.route
  import DcsSite.collections
  
  val yamlContent = Locations.classpath("default.sekyll.yml").readContent + "\n"+ Locations.file(".sekyll.yml").readContent

  {
    val effective = Locations.file("target/effective.sekyll.yml")
    effective.mkdirOnParentIfNecessary.writeContent(yamlContent)
    println(s"write content to $effective")
  }
  val yaml = Yaml.parse(yamlContent)
  
  Locations.file("target/effective-final.sekyll.yml").usingWriter(Yaml.write(yaml, _))
  val baseUrl = yaml.getString("baseUrl")
  println(s"baseUrl=$baseUrl")
  
  def routeImage(image:String):String = s"images/$image"
  
  def documents[T](collection:String):Seq[T] = collection match {
    case collections.customers =>
      Seq(Customer(routeImage("oracle.png")),Customer(routeImage("gothaer.png")),Customer(routeImage("DFPRADM.png")),Customer(routeImage("EuroCenterBank.png"))).asInstanceOf[Seq[T]]
    case collections.solutions =>
      Seq(Solution("Products",route.services),Solution("Development"),Solution("Consultancy"),Solution("Maintenance & Support"),Solution("Academy")).asInstanceOf[Seq[T]]
    case collections.partners =>
      Seq(DcsPartner("evolveum","https://evolveum.com","partners/evolveum/evolveum-logo-trademark.png")).asInstanceOf[Seq[T]]
    case  _ =>
      throw new IllegalArgumentException(s"Collection $collection is not defined")
  }
  
  //Yaml.parse(frontMatter)
  // Templated pages to generate
  val pages: Seq[(String, Template1[Site, Html])] = Seq(
    route.partners -> eu.dcsi.website.html.partners,
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
    
  def rawCollections:Seq[OutputFile] = {
        //savePage(s"blogPost ${post.id}", s"blog/${post.id}.html", page, sitemapPriority = "0.8")
//      // Feed
//      val file = new File(outputDir, "blog/atom.xml")
//      file.getParentFile.mkdirs()
//
//      XML.save(file.getAbsolutePath, FeedFormatter.atom(renderedBlogPosts.take(10)), enc = "utf-8")
//
//      OutputFile(file, "", includeInSitemap = false, sitemapPriority = "")
        Seq()
    }

}