package eu.dcsi.sekyll.docs

import org.joda.time.DateTime

import scala.xml.{Elem, PCData}
import org.joda.time.format.ISODateTimeFormat
import play.twirl.api.Html
import org.raisercostin.sekyll.Site

object FeedFormatter {
  def atom(posts: Seq[(BlogPost, Html)])(implicit site: Site): Elem = {
    val blogUpdate = ISODateTimeFormat.dateTime.print(posts.head._1.date)
    val blogUrl = site.baseUrl + "/blog"
    val year = DateTime.now().getYear

    <feed xmlns="http://www.w3.org/2005/Atom" xmlns:dc="http://purl.org/dc/elements/1.1/">
      <title>Lagom Blog</title>
      <link rel="alternate" type="text/html" href={blogUrl} />
      <link rel="self" type="application/atom+xml" href={blogUrl + "/atom.xml"} />
      <id>{blogUrl}</id>
      <rights>Copyright {year}, Lightbend</rights>
      <updated>{blogUpdate}</updated>
      <dc:creator>Lightbend</dc:creator>
      <dc:date>{blogUpdate}</dc:date>
      <dc:language>en</dc:language>
      <dc:rights>Copyright {year}, Lightbend</dc:rights>
      {posts.map {
      case (post, content) =>
        val postDate = ISODateTimeFormat.dateTime.print(post.date)
        val postUrl = s"$blogUrl/${post.id}.html"

        <entry>
          <title>{post.title}</title>
          <link rel="alternate" href={postUrl} />
          {post.tags.map { tag =>
            <category term={tag} scheme={s"$blogUrl/tags/${html.encodePathSegment(tag)}.html"} />
        }}
          <author>
            <name>{post.author.name}</name>
            <uri>{post.author.url}</uri>
          </author>
          <id>{postUrl}</id>
          <updated>{postDate}</updated>
          <published>{postDate}</published>
          <content type="html">{PCData(makeAbsoluteLinks(content, site.baseUrl).body)}</content>
          <dc:date>{postDate}</dc:date>
        </entry>
    }}
    </feed>
  }

  def makeAbsoluteLinks(html: Html, baseUrl: String): Html = {
    val body1 = html.body
    val body2 = linkFinder.replaceAllIn(body1, "$1" + baseUrl + "/")
    val body3 = imgFinder.replaceAllIn(body2, "$1" + baseUrl + "/")
    val body4 = relImgFinder.replaceAllIn(body3, "$1" + baseUrl + "/")
    Html(body4)
  }

  val linkFinder = """(<a [^>]*href=")/(?![/"])""".r
  val imgFinder = """(<img [^>]*src=")/(?![/"])""".r
  val relImgFinder = """(<img [^>]*src=")(?!(http://|https://))""".r
}
