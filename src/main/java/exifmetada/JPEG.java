package exifmetada;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

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
import org.w3c.dom.Element;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class JPEG {

	public void carimba(BufferedImage buffImg) throws IOException {
		ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg")
				.next();
		ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
				.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(
				typeSpecifier, null);
		ImageWriteParam writeParam = imageWriter.getDefaultWriteParam();
		
		IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
		textEntry.setAttribute("keyword", "teste");
		textEntry.setAttribute("value", "teste123");
		
		IIOMetadataNode text = new IIOMetadataNode("tEXt");
		text.appendChild(textEntry);
		
		
		IIOMetadataNode root = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
		root.appendChild(text);

		imageMetaData.mergeTree("javax_imageio_jpeg_image_1.0", root);
//
//		Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
//		Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
//		jfif.setAttribute("Xdensity", Integer.toString(96));
//		jfif.setAttribute("Ydensity", Integer.toString(96));
//		
		// writing the data
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageOutputStream stream = ImageIO.createImageOutputStream(baos);
		imageWriter.setOutput(stream);
		imageWriter.write(imageMetaData, new IIOImage(buffImg, null, imageMetaData), writeParam);
		stream.close();
	}

	public void showMetada(File jpegImageFile) throws IOException,
		ImageReadException, ImageWriteException {

		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegImageFile);
			for (Directory directory : metadata
					.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					System.out.println(tag);
				}
			}
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JPEG j = new JPEG();
		try {
			j.carimba(ImageIO.read(new File("/home/jackson/Imagens/teste.jpeg")));
			j.showMetada(new File("/home/jackson/Imagens/teste.jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		}
	}
}