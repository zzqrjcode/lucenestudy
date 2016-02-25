package l1;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class HelloWorld {

    public static void main(String[] args) throws Exception {

        String path = "D:\\baidu\\study\\solr\\lucene\\indexdir";
        // 内存索引目录
        // Directory dir = new RAMDirectory();
        // 若用文件目录
        Directory dir = FSDirectory.open(new File(path));

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        IndexWriter iwriter = new IndexWriter(dir, config);

        Document doc = new Document();
        String title = "标题";
        String content = "被索引的内容，内容";
        doc.add(new Field("title", title, TextField.TYPE_STORED));
        doc.add(new Field("content", content, TextField.TYPE_STORED));

        iwriter.addDocument(doc);

        Document doc1 = new Document();
        String title1 = "标题1";
        String content1 = "被索引的内容，内容";
        doc1.add(new Field("title", title1, TextField.TYPE_STORED));
        doc1.add(new Field("content", content1, TextField.TYPE_STORED));

        iwriter.addDocument(doc1);

        iwriter.close();

        DirectoryReader ireader = DirectoryReader.open(dir);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        QueryParser parser = new QueryParser(Version.LUCENE_43, "content", analyzer);
        Query query = parser.parse("内容");

        ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;

        System.out.println("查询结果数：" + hits.length);

        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);

            System.out.println(hits[i] + "搜索的结果title" + hitDoc.get("title"));

        }

        ireader.close();
        dir.close();

    }

}
