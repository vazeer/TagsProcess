import java.awt.List;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTML.Tag;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ReadCrawledData {
	private static String DIRECTORY = "/home/vazeer/Desktop/ScrappersData/nordy_products.jl";

	public static void main(String[] args) {
		GetInfo info = new GetInfo();

		
//		  info.parseAndWriteinFile(
//		  "/home/vazeer/Desktop/ScrappersData/IndianData/myntra/myntra_products_2.jl"
//		  ,
//		  "/home/vazeer/Desktop/ScrappersData/IndianData/myntra/myntra_content_2.txt"
//		  );
		 

		// info.readTagsData("/home/vazeer/Desktop/ScrappersData/IndianData/nordstorm/nordstormProductTags_products.jl");

		runToCombine(info);

		// divideIntoFiles("/home/vazeer/ScrappersData/IndianData/myntra/myntra_products.jl");
	}

	private static void runToCombine(GetInfo info) {
		info.updateTags(
				"/home/vazeer/Desktop/ScrappersData/IndianData/jabong/jabong_products.jl",
				"/home/vazeer/Desktop/ScrappersData/IndianData/jabong/jabongTags_products.jl",
				"jabong");
	}

	private static void divideIntoFiles(String filepath) {

		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(filepath, "r");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		long numSplits = 2; // from user input, extract it from args
		long sourceSize = 0;
		try {
			sourceSize = raf.length();
			System.out.println("System sourceSize:::" + sourceSize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long bytesPerSplit = sourceSize / numSplits;
		long remainingBytes = sourceSize % numSplits;
		System.out.println("System remainingBytes:::" + remainingBytes);
		int maxReadBufferSize = 8 * 1024; // 8KB
		for (int destIx = 1; destIx <= numSplits; destIx++) {
			BufferedOutputStream bw = null;
			try {
				bw = new BufferedOutputStream(new FileOutputStream(
						"/home/vazeer/ScrappersData/IndianData/myntra/myntra_products_"
								+ destIx + ".jl"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bytesPerSplit > maxReadBufferSize) {
				long numReads = bytesPerSplit / maxReadBufferSize;
				long numRemainingRead = bytesPerSplit % maxReadBufferSize;
				for (int i = 0; i < numReads; i++) {
					try {
						readWrite(raf, bw, maxReadBufferSize);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (numRemainingRead > 0) {
					try {
						readWrite(raf, bw, numRemainingRead);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				try {
					readWrite(raf, bw, bytesPerSplit);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (remainingBytes > 0) {
			BufferedOutputStream bw = null;
			try {
				bw = new BufferedOutputStream(new FileOutputStream("split."
						+ numSplits + 1));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				readWrite(raf, bw, remainingBytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			raf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void readWrite(RandomAccessFile raf, BufferedOutputStream bw,
			long numBytes) throws IOException {
		byte[] buf = new byte[(int) numBytes];
		int val = raf.read(buf);
		if (val != -1) {
			bw.write(buf);
		}
	}
}
