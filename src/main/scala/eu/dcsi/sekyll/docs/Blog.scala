package eu.dcsi.sekyll.docs

import java.io.{ File, FileInputStream, IOException }

import org.joda.time.DateTime

import scala.util.control.NonFatal
import org.raisercostin.jedi.Locations

object Blog {

  /** Find the blog posts, sorted by date in reverse, ie most recent first */
  def findBlogPosts(blogDir: File): Seq[BlogPost] =
    Locations.file(blogDir).list.filter(_.extension == "md").filter(!_.name.startsWith("_")).map { file =>
      file.usingInputStream(stream =>
        BlogMetaDataParser.parsePostFrontMatter(stream, file.baseName))
    }.toSeq.sortBy(_.date.toDate.getTime).reverse

}

final case class BlogPost(id: String, date: DateTime, markdown: String, title: String,
                          summary: String, author: BlogAuthor, tags: Set[String])

final case class BlogAuthor(name: String, url: String, avatar: String)

final case class BlogSummary(recent: Seq[BlogPost], tags: Seq[(String, Int)])
