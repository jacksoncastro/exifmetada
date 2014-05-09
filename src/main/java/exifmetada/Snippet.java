package exifmetada;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class Snippet {
	public byte[] writeCustomData(BufferedImage buffImg, String key,
			String value) throws Exception {
		ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
				.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

		// adding metadata
		IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier,
				writeParam);

		IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
		textEntry.setAttribute("keyword", key);
		textEntry.setAttribute("value", value);

		IIOMetadataNode text = new IIOMetadataNode("tEXt");
		text.appendChild(textEntry);

		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
		root.appendChild(text);

		metadata.mergeTree("javax_imageio_png_1.0", root);

		// writing the data
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageOutputStream stream = ImageIO.createImageOutputStream(baos);
		writer.setOutput(stream);
		writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
		stream.close();

		return baos.toByteArray();
	}

	// public String readCustomData(byte[] imageData, String key) throws
	// IOException{
	// ImageReader imageReader =
	// ImageIO.getImageReadersByFormatName("png").next();
	//
	// imageReader.setInput(ImageIO.createImageInputStream(new
	// ByteArrayInputStream(imageData)), true);
	//
	// // read metadata of first image
	// IIOMetadata metadata = imageReader.getImageMetadata(0);
	//
	// //this cast helps getting the contents
	// PNGMetadata pngmeta = (PNGMetadata) metadata;
	// NodeList childNodes = pngmeta.getStandardTextNode().getChildNodes();
	//
	// for (int i = 0; i < childNodes.getLength(); i++) {
	// Node node = childNodes.item(i);
	// String keyword =
	// node.getAttributes().getNamedItem("keyword").getNodeValue();
	// String value = node.getAttributes().getNamedItem("value").getNodeValue();
	// if(key.equals(keyword)){
	// return value;
	// }
	// }
	// return null;
	// }

	public void showMetada(File jpegImageFile) throws IOException,
			ImageReadException, ImageWriteException {
		// IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);

		// for (int i = 0; i < metadata.getItems().size(); i++) {
		// System.out.println(metadata.getItems().get(i));
		// }
		// System.out.println(metadata);

		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegImageFile);
			for (com.drew.metadata.Directory directory : metadata
					.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					System.out.println(tag);
				}
			}
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		}
	}
	
	private void save(byte[] bytes) {
		OutputStream out = null;
		 try {
//			BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
			out = new BufferedOutputStream(new FileOutputStream("/home/jackson/Imagens/teste2.png"));
		    out.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}
	
	private void reader(String file) {
		PngReader pngr = FileHelper.createPngReader(new File(file));
		pngr.readSkippingAllRows();
		for (PngChunk c : pngr.getChunksList().getChunks()) {
		      if (!ChunkHelper.isText(c))   continue;
		      PngChunkTextVar ct = (PngChunkTextVar) c;
		      String key = ct.getKey();
		      String val = ct.getVal();
		      
		      System.out.println("Chave: " + key + " - Valor: " + val);
		}
	}
	
	public static void main(String[] args) {
//		Snippet example = new Snippet();
//		try {
//			byte[] bytes = example.writeCustomData(ImageIO.read(new File("/home/jackson/Imagens/teste.png")), "teste","teste23|sadas");
//			example.save(bytes);
//			example.reader("/home/jackson/Imagens/teste2.png");
//		} catch (ImageReadException e) {
//			e.printStackTrace();
//		} catch (ImageWriteException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		for (int i = -10; i<10; i++) {
			  if ( i%2 == 0 )
			    continue;
			  System.out.println(i);
			}
	}
}
