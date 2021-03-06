package org.raisercostin.sekyll

import play.twirl.api.Template1
import play.twirl.api.Html
import eu.dcsi.sekyll.docs._
import org.raisercostin.jedi.Locations
import org.raisercostin.jedi.FileLocation
import org.raisercostin.jedi.RelativeLocation
import scala.util.Try
import java.util.Arrays

case class Customer(image: String)
case class Solution(name: String, link: String)
case class DcsPartner(name: String, url: String, logo: String, content: String, rendered: Html)

object Solution {
  def apply(name: String): Solution = Solution(name, name)
}

object DcsSite {
  object collections {
    val customers = "customers"
    val solutions = "solutions"
    //val partners = "partners"
  }
  /**Use the routes in your code for statically checked links.*/
  object route {
    //TODO add validation that all links are used.
    //var used = Seq[String]()
    def use(name: String): String = {
      //used = name +: used
      name
    }

    val contact = use("contact")
    val home = use("index.html")
    val about = use("about")
    val services = use("services")
    //val partners = use("partners")
  }
}


trait Item{
  def title:Option[String]
  def image:String
  def fullImage:String
  def rendered:Html
  def slug:String
  def description:String
}
case class SiteDocument(yaml: Yaml, markdown: String, file: FileLocation, site: Site, rendered: Html) extends Item {
  def relative(relativePath: String): RelativeLocation = file.parent.child(relativePath).extractPrefix(Locations.file(site.config.source.getOrElse(""))).get
  def title:Option[String] = yaml.getString("title")
  def image:String = yaml.getString("image").getOrElse("")
  def fullImage:String = if(isAbsolute(image)) image else slugForChildren+"/"+image
  def isAbsolute(image:String) = image.startsWith("http://") || image.startsWith("https://") || image.startsWith("://")
  
  def contains(item:SiteDocument):Boolean = {
    val parent = site.slug(this.file)
    val child = site.relativeFile(item.file)
    val contains = item!=this && child.ancestor(parent) == parent
    println(s"check $parent includes $child: $contains")
    contains
  }
  def slug:String = site.slug(this.file).relativePath
  def slugForChildren:String = 
    if(file.baseName=="index")
      site.slug(this.file).relativePath
    else
      site.slug(this.file).parent.relativePath
  //val parent:RelativeLocation = item.file.parent.extractPrefix(item.site.src).get
  //println("parent="+parent)
  //site.isPartOf_.file.ancestor(parent)==parent
  
  def description:String = yaml.getString("description").getOrElse("no-description")
  
  println(s"SiteDocument: slug $slug from $file")
  println(s"SiteDocument: slugForChildren $slugForChildren from $file")
}
case class FolderItem(item:SiteDocument) extends Item{
  def children:Seq[Item] = item.site.allCollections.filter(item.contains)
  def title:Option[String] = item.title
  def rendered:Html = item.rendered
  def image:String = item.image
  def slug:String = item.slug
  def description:String = item.description
  def fullImage:String = item.fullImage
}

/**
 * The site gets passed to every page.
 * Is a wrapper around the generic RawSite making the mapping to final domain types.
 */
case class Site(currentLagomVersion: String, currentDocsVersion: String,
                blogSummary: BlogSummary, assetFingerPrint: String) extends RawSite {
  import DcsSite.collections

  def route = DcsSite.route
  def route(image: String) = routeImage(image)
  def customers: Seq[Customer] = documents[Customer](DcsSite.collections.customers)
  def solutions: Seq[Solution] = documents[Solution](DcsSite.collections.solutions)
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
  def baseUrl: String = RawSite.config.baseUrl.getOrElse("")
  @deprecated
  def path: String = baseUrl
  @deprecated
  def context: String = baseUrl

  private def routeImage(image: String): String = s"images/$image"

  def documents[T](collection: String): Seq[T] = collection match {
    case collections.customers =>
      Seq(Customer(routeImage("oracle.png")), Customer(routeImage("gothaer.png")), Customer(routeImage("DFPRADM.png")), Customer(routeImage("EuroCenterBank.png"))).asInstanceOf[Seq[T]]
    case collections.solutions =>
      Seq(Solution("Consultancy","solutions/consultancy"), Solution("Products", "solutions/products"), Solution("Development","solutions/development"), 
          Solution("Maintenance & Support","solutions/maintenance-support"), Solution("Academy","solutions/academy")).asInstanceOf[Seq[T]]
    case _ =>
      throw new IllegalArgumentException(s"Collection $collection is not defined")
  }
  def isPartner(doc: SiteDocument) = doc.file.path.contains("/partners/")

  val allCollections: Seq[SiteDocument] = RawSite.rawCollections(this).toSeq

  lazy val renderer = new MarkdownRenderer()
  def markdownToHtml(markdown: String) = renderer.markdownToHtml(markdown)

  def markdownPages: Seq[OutputFile] = allCollections.map { item =>
    val page = item.yaml.getString("layout") match {
      case Some("folder") =>
        eu.dcsi.website.html.folder(new FolderItem(item))(this)
      case _ =>
        eu.dcsi.website.html.page(item)(this)
    }
    DocumentationGenerator.savePage(s"markdown page ${item.file}", relativeFile(item.file).withExtension(_ => "").relativePath, page, sitemapPriority = "0.8")
  }
  
  def slug(file:FileLocation): RelativeLocation = {
    relativeFile(file).withExtension(_ => "")
  }
  def relativeFile(file: FileLocation): RelativeLocation = {
    val f = file.extractPrefix(src).get
    if(f.name=="index.md")
      f.parent
    else
      f
  }
}

case class SiteConfig(yaml: Yaml) {
  val baseUrl = yaml.getString("baseUrl")
  val source = yaml.getString("source")
  val destination = yaml.getString("destination")
  val exclude = yaml.getList[String]("exclude")
  println(s"""loaded config ${this.yaml.map.mkString("  ", "\n  ", "\n")}""")
}
class RawSite {
  val config = SiteConfig(readYaml())

  def readYaml() = {
    val yamlContent = Locations.classpath("default.sekyll.yml").readContent + "\n" + Locations.file(".sekyll.yml").readContent

    {
      val effective = Locations.file("target/effective.sekyll.yml")
      effective.mkdirOnParentIfNecessary.writeContent(yamlContent)
      println(s"write content to $effective")
    }

    val yaml = Yaml.parse(yamlContent)
    Locations.file("target/effective-final.sekyll.yml").usingWriter(Yaml.write(yaml, _))
    yaml
  }

  val src = Locations.file(config.source.getOrElse(""))
}

object RawSite extends RawSite {
  import DcsSite.route

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

  //TODO implement 
  //- https://gist.github.com/Aivean/6b2bb7c2473b4b7e1376fac1d2d49cf8
  //- https://www.google.com/search?q=fnmatch+implemenataion+in+scala
  def accept(file: RelativeLocation): Boolean = {
    val path = file.relativePath
    config.exclude.getOrElse(Seq()).forall { x: String => !path.contains(x) }
  }
  val files =
    src.descendants.filter(f => accept(f.extractPrefix(src).get)).filter(!_.name.startsWith("_")).partition(_.extension == "md")

  def rawCollections(site: Site): Iterable[SiteDocument] = files._1.map { file =>
    file.usingInputStream { stream =>
      val (yaml, markdown) = BlogMetaDataParser.extractFrontMatter(stream)
      println(s"""analyze ${file.absolute} ... \n  ${yaml.map.mkString("\n  ")}""")
      //val post = BlogMetaDataParser.toBlogPost(file.baseName, yaml, markdown)
      val renderedPost = Html(site.markdownToHtml(markdown))
      val base = file.extractPrefix(Locations.file(site.config.source.getOrElse(""))).get.parent.relativePath

      val fixedLinks =
        if (site.baseUrl.nonEmpty)
          FeedFormatter.makeAbsoluteLinks(renderedPost, site.baseUrl + "/" + base)
        else
          FeedFormatter.makeAbsoluteLinks(renderedPost, base)
      SiteDocument(yaml, markdown, file, site, fixedLinks)
    }
  }

  val copyAll = {
    val dest = Locations.file(config.destination.getOrElse(".")) //.backupExistingOne
    files._2.map { file =>
      val child = file.extractPrefix(src).get
      println(s"copy $child")
      dest.child(child).mkdirOnParentIfNecessary.copyFrom(file)
    }
  }
}