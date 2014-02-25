import scala.collection.JavaConversions._

import org.hibernate.search.annotations.Indexed
import org.hibernate.search.annotations.Field

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;

import org.infinispan.manager._

val fieldName = "categories"
/*
@Indexed
case class MyProduct(id:Long, name:String, description:String, @Field(name = "categories", index =
Index.YES, analyze = Analyze.YES, store = Store.NO) val categories:String) {

  def getCategories() = this.categories
  def setCategories(newCat:String) = { }
}
*/

def runWithBenchmark(toExecute: () => Unit, header:String):Unit = {
  val startTime = System.currentTimeMillis()
  toExecute()
  println(header + (System.currentTimeMillis - startTime) + "ms")
}

def buildQuery(defaultCache:Cache[Long, MyProduct], p:MyProduct, cat:String): (Query,SearchManager) = {
  val searchManager = Search.getSearchManager(defaultCache)
  val builder = searchManager.buildQueryBuilderForClass(p.getClass).get()
  val query = builder.bool().must(builder.keyword().onField(fieldName).matching(cat).createQuery()).createQuery()
  (query, searchManager)
}

object QuickSell {
  def main(args: Array[String]) {
      println("Starting QuickSell App...")
      val manager = new DefaultCacheManager("configurations.xml")

      val defaultCache = manager.getCache[Long, MyProduct]()
      val regularCache = manager.getCache[Long, MyProduct]("regularCache")

      //val cat =  Array("Une catégorie", "une autre","un tag pour faciliter la recherche")
      val cat = "string"
      val p = new MyProduct(1,"Nom du produit", "Description du produit",cat)

      val importInCache : () => Unit = () => for ( i <- 0 until 1000 ) regularCache.put(i, new MyProduct(i, "Other","Other", "other"))

      runWithBenchmark(
        () => for ( i <- 0 until 1000 ) regularCache.put(i, new MyProduct(i, "Other","Other", "other")),
      "Regular import lasted for:")

      runWithBenchmark( () => for ( i <- 0 until 1000 ) defaultCache.put(i,new MyProduct(i,
        "Other","Other", "other")),"Import index:")

      runWithBenchmark( () => defaultCache.put(p.id, p) , "Simple put:")
      runWithBenchmark( () => defaultCache.get(p.id), " - Returns [" + defaultCache.get(p.id) + "]:")

      val (query, searchManager) = buildQuery(defaultCache,p,cat)
      val cl = p.getClass
      runWithBenchmark( () => searchManager.getQuery(query, cl ).list(),
        "Query:")
      val results = searchManager.getQuery(query, cl).list()
      println("Print Query result(s):")
      results.toList.foreach { result => println(" - " + result) }

      println("App shutdown.")
      manager.stop()
    }
}
QuickSell.main(args)
