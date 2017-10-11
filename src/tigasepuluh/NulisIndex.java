/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigasepuluh;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author abc
 */
public class NulisIndex {

    public static void write() throws IOException
    {
        String txtPath= "D:\\Kuliah\\rancangan document indexing\\dir-txt\\";
    	String indexPath= "D:\\Kuliah\\rancangan document indexing\\dir-index\\";
        final Path indexDir = Paths.get(indexPath);
        final Path txtDir = Paths.get(txtPath);

    	
    	if (!Files.isReadable(indexDir)) {
            System.out.println("Directory '" +indexDir.toAbsolutePath()+ "' does not exist or is not readable, please check the index path");
            System.exit(1);
        }else{
        	if (!Files.isReadable(txtDir)) {
                System.out.println("Directory '" +txtDir.toAbsolutePath()+ "' does not exist or is not readable, please check the txt path");
                System.exit(1);
            }else{
            	if (PDFManager.isDirEmpty(txtDir)) {
                    System.out.println("Directory '" +txtDir.toAbsolutePath()+ "' empty.");
                    System.exit(1);
                }
            }
        }
        try
        {
            System.out.println("Please Wait, indexing Document from '" +txtDir.toAbsolutePath()+ "' to '"+ indexDir.toAbsolutePath()+"'");
            System.out.println("\n");
            
            Directory directory;
            directory = FSDirectory.open(indexDir);
					
            Analyzer analyzer = new SimpleAnalyzer();
            
            IndexWriterConfig indexConfig = new IndexWriterConfig(analyzer);
            IndexWriterConfig setOpenMode = indexConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);

            try (IndexWriter writer = new IndexWriter(directory, indexConfig)) {
                System.out.println("Indexing...");
                indexDocs(writer, txtDir);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
        	System.out.println("---------------------------------------");
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
	{
            try (InputStream stream = Files.newInputStream(file))
            {
                Document doc = new Document();
                doc.add(new StringField("path", file.toString(), Field.Store.YES));
                doc.add(new LongPoint("modified", lastModified));
                doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
                writer.updateDocument(new Term("path", file.toString()), doc);
            System.out.println(file.getFileName()+" DONE.. ");
            }
	}
}
/*
iterasi file di folder
    lakukan indexwriter tiap file
*/