package org.raisercostin.sekyll

import org.junit.Test
import org.junit.Assert._
import org.raisercostin.jedi.RelativeLocation
import org.raisercostin.jedi.Locations

class SiteTest {
  @Test def acceptFile(){
    assertEquals(false,RawSite.accept(Locations.relative("""./project/target/node-modules/webjars/npm/node_modules/wrappy/README.md""")))
  }
}