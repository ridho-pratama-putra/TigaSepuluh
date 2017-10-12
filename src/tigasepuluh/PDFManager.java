/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tigasepuluh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author abc
 */
public class PDFManager {
    private int Count;
    private String filePath;
    //public static void PDFToTxt() throws IOException
	public static void main(String[] args) throws IOException 
    {
        String txtPath= "D:\\Kuliah\\rancangan document indexing\\dir-txt\\";
        String pdfPath= "D:\\Kuliah\\rancangan document indexing\\dir-pdf";
        
        final Path pdfDir = Paths.get(pdfPath);
        final Path txtDir = Paths.get(txtPath);
        
        if (!Files.isReadable(pdfDir)) {
            System.out.println("Directory '" +pdfDir.toAbsolutePath()+ "' does not exist or is not readable, please check the pdf path");
            System.exit(1);
        }else{
            if (!Files.isReadable(txtDir)) {
                System.out.println("Directory '" +txtDir.toAbsolutePath()+ "' does not exist or is not readable, please check the txt path");
                System.exit(1);
            }else
            {
                if (isDirEmpty(pdfDir)) {
                    System.out.println("Directory '" +pdfDir.toAbsolutePath()+ "' empty.");
                    System.exit(1); 
                }
            }
        }
        
        System.out.println("Please Wait, converting Document from '" +pdfDir.toAbsolutePath()+ "' to '"+txtDir.toAbsolutePath()+"'");
        System.out.println("\n");
        if (Files.isDirectory(pdfDir))
        {
            Files.walkFileTree(pdfDir, new SimpleFileVisitor<Path>() 
            {
                private File read_file;
                private PDFParser parser;
                private COSDocument cosDoc;
                private PDFTextStripper pdfStripper;
                private PDDocument pdDoc ;
                
                // menggeledah semua isi folder
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
                {
                    String text;
                    if (!(file.toString().endsWith(".pdf") || file.toString().endsWith(".PDF"))) {
                        System.out.println("error processing '"+file +"'");
                        System.exit(1);
                        return FileVisitResult.TERMINATE;
                    }
                    String fileNameExtension = file.getFileName().toString();
                    String fileName = fileNameExtension.replaceFirst("[.][^.]+$", "");
                    String filePath = file.toString();
                    String encoding = "UTF-8";
                    System.out.println("converting '"+file.toString()+"' file to txt" );
                    try
                    {
                        read_file = new File(filePath);
                        this.parser = new PDFParser((RandomAccessRead) new RandomAccessFile(read_file,"r"));
                        parser.parse();
                        this.cosDoc = parser.getDocument();
                        this.pdfStripper = new PDFTextStripper();
                        this.pdDoc = new PDDocument(cosDoc);
                        text = pdfStripper.getText(pdDoc); // semua isi teks

                        String txtFileName;
                        txtFileName = fileName.concat(".txt");
                        String txtPathPlaced = txtPath.concat(txtFileName);
                        System.out.println("Placing '"+txtPathPlaced+"' .." );


                        // titik tapi suwe: 8 pdf 10 detik
                        // OutputStreamWriter outputStream = new OutputStreamWriter(new FileOutputStream(txtFileName), encoding);
                        // pdfStripper.writeText(pdDoc, outputStream);
                        // outputStream.close();
                        // pdDoc.close();
                        
                        // akeh tapi cepet, 8 pdf 8 detik
                        PrintWriter pw = new PrintWriter(new FileWriter(txtPathPlaced));
                        pw.write(text);
                        System.out.println("file placed as '"+txtFileName+"' " );
                        cosDoc.close();
                        pw.close();
                        System.out.println("\n" );
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            System.out.println("Files.isDirectory(path) return false" );
        }
    }
    
    public static boolean isDirEmpty(final Path directory) throws IOException
    {
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory))
        {
            return !dirStream.iterator().hasNext();
        }
    }
}


/*
convert seluruh dokumen ke txt
    iterasi file di folder
        convert ke judul masing-masing file
*/