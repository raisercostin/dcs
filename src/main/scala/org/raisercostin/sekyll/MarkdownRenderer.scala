package org.raisercostin.sekyll
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension
import com.vladsch.flexmark.ext.definition.DefinitionExtension
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.typographic.TypographicExtension
import java.util.Arrays

class MarkdownRenderer{
  import com.vladsch.flexmark.util.options.MutableDataSet
  import com.vladsch.flexmark.parser.Parser
  import com.vladsch.flexmark.html.HtmlRenderer
  import com.vladsch.flexmark.ast.Node
  
  lazy val options = asKramdown()

    // uncomment to set optional extensions
    //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

    // uncomment to convert soft-breaks to hard breaks
    //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

    lazy val parser = Parser.builder(options).build();
    lazy val renderer = HtmlRenderer.builder(options).build();
  
//  import play.doc.PrettifyVerbatimSerializer
//  import org.pegdown.{ Extensions, LinkRenderer, PegDownProcessor, VerbatimSerializer }
//  import scala.collection.JavaConverters._
//  private lazy val pegdown = new PegDownProcessor(Extensions.ALL)
  def markdownToHtml(markdown: String) = {
    val document:Node = parser.parse(markdown)
    val html = renderer.render(document)
    html
//    pegdown.markdownToHtml(markdown, new LinkRenderer,
//      Map[String, VerbatimSerializer](VerbatimSerializer.DEFAULT -> PrettifyVerbatimSerializer).asJava)
  }
  
  def asKramdown():MutableDataSet = {
    import com.vladsch.flexmark.parser._;
        val options = new MutableDataSet()
        options.setFrom(ParserEmulationProfile.KRAMDOWN)
        options.set(Parser.EXTENSIONS, Arrays.asList(
                AbbreviationExtension.create(),
                DefinitionExtension.create(),
                FootnoteExtension.create(),
                TablesExtension.create(),
                TypographicExtension.create()
        ));
        options
    }
}
