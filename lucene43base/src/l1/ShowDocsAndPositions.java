package l1;

import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class ShowDocsAndPositions {

    public static void main(String[] args) throws Exception {

        ShowDocsAndPositions hello = new ShowDocsAndPositions();
        hello.run();
        System.out.println(System.currentTimeMillis());

        Date date = new Date();
        date.setTime(1456111475971l);
        System.out.println(date.toLocaleString());
    }

    public void run() throws Exception {

        Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_43);

        // System.out.println("EnglishAnalyzer.getDefaultStopSet()："+EnglishAnalyzer.getDefaultStopSet()+"\n");

        // 将索引存放到内存中
        Directory directory = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        Document doc = new Document();
        String content = "Tom lives in Guangzhou,I live in Guangzhou too";
        doc.add(new Field("content", content, TextField.TYPE_STORED));
        iwriter.addDocument(doc);

        Document doc1 = new Document();
        String content1 = "He lived in Shanghai";
        doc1.add(new Field("content", content1, TextField.TYPE_STORED));
        iwriter.addDocument(doc1);

        iwriter.close();

        // 搜索索引
        DirectoryReader reader = DirectoryReader.open(directory);

        // 显示document数
        System.out.println(new Date() + "该索引共含 " + reader.numDocs() + " 篇文档");

        for (int i = 0; i < reader.numDocs(); i++) {
            System.out.println("文档" + i + "：" + reader.document(i));
        }

        // 枚举term，获得<document, term freq, position* >信息
        TermsEnum termEnum = MultiFields.getTerms(reader, "content").iterator(null);

        System.out.println("关键词\t文章号[出现频率]\t出现位置");
        while (termEnum.next() != null) {
            System.out.print(termEnum.term().utf8ToString() + "\t");

            // System.out.print("出现该词的文档数="+termEnum.docFreq());
            final DocsAndPositionsEnum dpEnum = termEnum.docsAndPositions(null, null);

            int i = 0;
            int j = 0;
            while (dpEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
                if (i > 0)
                    System.out.print("\t");
                System.out.print(dpEnum.docID() + "[" + dpEnum.freq() + "]\t");
                for (j = 0; j < dpEnum.freq(); j++)
                    System.out.print(dpEnum.nextPosition() + ",");
                System.out.print("\n");
                i++;
            }

        }

        reader.close();
        directory.close();
    }

}
