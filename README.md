The sample site generated in `master` branch: http://raisercostin.org/sekyll

A second sample is at http://raisercostin.gitlab.io/sekyll

# Sekyll

## How it works

The content is generated in `docs/` folder.
Steps:
  - there are several types of files that will be statical types
    - **/*.md - markdon files converted to html
    - `src/main/twirl` - layout/tags pages that are compiled
       - any page can refer the entire content via `@site.xxx` properties
    - all other files - copied directly in docs folder

Everything in `docs/` is published at raisercostin.gitlab.io .
The content in `docs` is a manual copy of a `target/web/stage`.

Publish on
- github.com - See https://help.github.com/articles/configuring-a-publishing-source-for-github-pages/

## Generating site locally

The build is made with `sbt`.
- eclipse: `sbt eclipse`
- compile: `sbt clean compile`
- generate web: `sbt webStage`
- start server for in dev mode with autorefresh: `sbt run` . Then go on http://localhost:8080/
- start server in debug mode `activator -jvm-debug 5005 "run 8080"`

# Features

## History

### 2017-08-11
- more control of assetFingerPrint:
  1. manually changed in the `publicVersion` value in `build.sbt`? Drawback: browsers might cache styles and javascripts for long time and will not reload new versions. The generator should generate a new file from time to time or when the resources are changed.

# Backlog

## ToDo
- publish at http://sekyll.gitlab.io and http://sekyll.github.io
- generate directly in `docs/` not in `target/web/stage`
- switch from style and sass to less as less has native compilation in sbt.
- cleanup
- deploy command to release static site to
  - folder
  - other branch
  - via travis
    - https://gist.github.com/domenic/ec8b0fc8ab45f39403dd
    - https://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/
    - https://www.google.ro/search?q=github+publish+gitpages+after+travis
- render current branch by scripts in
  - github
  - gitlab (for private repos)
- yaml inheritance with default config
  - https://web.archive.org/web/20130213112648/http://blog.101ideas.cz/posts/dry-your-yaml-files.html
- publish via travis: https://github.com/jostly/template-sbt-travis
- swtich tags to semantic tags
  - https://coderwall.com/p/wixovg/bootstrap-without-all-the-debt
  - https://css-tricks.com/semantic-class-names/
  - https://stackoverflow.com/questions/22133639/bootstrap-and-html5-semantic-tags
  - https://stackoverflow.com/questions/23583235/how-to-combine-twitter-bootstrap-3-and-html5-semantic-elements
- render first requested page and continue with others in background (to speedup dev lifecycle)
- add `@site.page` context info about current page. Two ways to implement it:
  - the iterator should modify this.
  - the Site is actually a Page that refers the site.  
- strict/lax evaluation of meta parameters 
- fnmatch (gitignore pattern format) implementation in scala 
  - https://gist.github.com/Aivean/6b2bb7c2473b4b7e1376fac1d2d49cf8
  - https://www.google.com/search?q=fnmatch+implemenataion+in+scala
- detect changed markdown files
- change markdown from pegdown to kramdown
- add mega menu https://bootsnipp.com/snippets/featured/bootstrap-mega-menu

## Bugs
- hover doesn't work on tablets/devices

## How could work
- Generate in a separate branch and publish from there.
  - There are some issues on keeping the history of that.
- Uploading only the sources and generate the public folder at runtime has the drawback of not having the history of the final generated site.

## Quircks
Here we explain what we tried and failed.

### Gitlab
- Don't publish the `target/web/stage` folder directory by configuring it in `.gitlab-ci.yml` as this will fail.
- Sass and Style sbt-web plugins need external dependency on nodejs/npm and it will take longer time to compile.
- Sbt fails to build in gitlab after 1h.

## Rejected
- more control of assetFingerPrint:
  - based on the git head: `val assetFingerPrint = "git rev-parse HEAD".!!.trim`
     - This has the problem that on any change anywhere all the files are affected.
  - based on the actual content of each generated/combined file instead of current version in head.
     - This is a fingerprinting on each file. Might work if a fingerprint on content on all of the files is computed.


# Old lagom site
## Lagom website

This project is responsible for generating the Lagom website.

The Lagom website is a static website deployed to GitHub pages that is generated from this project.  This project uses twirl for templating, along with sbt-web to process web assets and webjar for dependencies.

The documentation is generated from the lagom main project as unstyled HTML snippets, along with javadocs/scaladocs, and an index file that describes the navigation structure of the documentation.  Each time the website is generated, the documentation pages are recreated from these HTML snippets, allowing the layout and styling to be kept up to date, and allowing links between versions of the documentation.

### Project layout

    - src
      - blog - each markdown file in here gets converted to a blog post
      - docs
        - 1.0.x - the documentation for each version of Lagom
          - index.json - the index for this version of the docs
          - api - api docs
      - main
        - markdown - each file in here gets served as a web page
        - twirl - twirl templates for the documentation
        - assets - compiled assets, eg, stylus stylesheets
        - public - static assets, eg images
        - scala - the Scala source code that actually does the documentation generation

### Developing the website

A static version of the website can be generated by running `sbt web-stage`.  This will output the entire website to the `target/web/stage` directory.  You should start a simple HTTP server from here to serve the docs, eg using `python -m SimpleHTTPServer`.  The `sbt ~web-stage` command can be used to tell sbt to watch for changes and rebuild the site whenever anything changes.

For convenience, `sbt run` does the above for you, starting a static Akka HTTP server in the stage directory, and then watching the filesystem for changes and rebuilding the site whenever it changes.

### Deploying the documentation

A new version of the markdown docs can be generated by running `sbt markdownGenerateAllDocumentation` in the `docs` directory of the main Lagom project, this outputs the documentation to `docs/target/markdown-generated-docs`.  This can be then copied into the corresponding `src/docs/<version>` directory in this project.

A new version of the API docs can be generated by running `sbt unidoc` in the main Lagom project.  This will output the javadocs in `target/javaunidoc`, which can then be copied into the corresponding `src/docs/<version>/api/java` directory in this project.

### Writing blog posts

Lagom uses a format similar to Jekyll for writing blog posts. Blog posts are written in markdown, prefixed with a front matter in YAML format. To create a new blog post, copy the `src/blog/_template.md` file to a new file in the same directory. The name of the file should match the URL that you want it served at, so for example, if you want it to be served at `blog/my-blog-post.html`, it should be in `src/blog/my-blog-post.md`.

Edit the front matter to suit your needs. The `title` and `date` fields are compulsory. The `author_github`, if provided, will be used by the website generator to lookup the author details from GitHub, including the name, URL and Avatar of the author. These can all be overridden, and must be provided manually if no GitHub username is provided, using the `author_name`, `author_url` and `author_avatar` fields.

The `tags` field can be used to specify a space separated list of tags. Multi-word tags can be configured by separating the words with a `+` symbol. The `summary` field can be used to provide a summary, this is what's displayed on the blog index page as well as on the tag index pages, and it will be markdown rendered. Usually it will simply be the first paragraph of the blog post.

The remainder of the file is the blog post. You can preview your blog post by following the [Developing the website](#developing-the-website) instructions above.


# Thanks

This project started as a fork of https://github.com/lagom/lagom.github.io

This project exists because [lagom site generator](https://github.com/lagom/lagom.github.io) project contains a lot of generated content: site and java docs and is focused on generating the site for lagom.
