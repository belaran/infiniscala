import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Field;

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


@Indexed
public class MyProduct {
    public Long id;
    public String name;
    public String description;

    @Field(name = "categories", index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    public String categories;

    public MyProduct(Long id, String name, String desc, String cat) {
      this.id = id;
      this.name = name;
      this.description = desc;
      this.categories = cat;
   }

   public String toString() {
    return "id:" + id + ", name:" + name + ",desc:" + description;
   }
}
