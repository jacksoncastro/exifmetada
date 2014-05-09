package exifmetada;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.w3c.dom.Element;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;



public class MetaJpeg {
	
	public static void saveAsJPEG(String jpgFlag, BufferedImage image_to_save, float JPEGcompression, FileOutputStream fos) throws IOException {
		 
	    //useful documentation at http://docs.oracle.com/javase/7/docs/api/javax/imageio/metadata/doc-files/jpeg_metadata.html
	    //useful example program at http://johnbokma.com/java/obtaining-image-metadata.html to output JPEG data
	 
	    //old jpeg class
	    //com.sun.image.codec.jpeg.JPEGImageEncoder jpegEncoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fos);
	    //com.sun.image.codec.jpeg.JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image_to_save);
	 
	    // Image writer
	    ImageWriter imageWriter =  ImageIO.getImageWritersBySuffix("jpeg").next();
	    ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
	    imageWriter.setOutput(ios);
	 
	    //and metadata
	    IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image_to_save), null);
	 
	    if (jpgFlag != null){
	 
	        int dpi = 96;
	 
	        try {
	            dpi = Integer.parseInt(jpgFlag);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	 
	        //old metadata
	        //jpegEncodeParam.setDensityUnit(com.sun.image.codec.jpeg.JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
	        //jpegEncodeParam.setXDensity(dpi);
	        //jpegEncodeParam.setYDensity(dpi);
	 
	        //new metadata
	        Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
	        Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
	        jfif.setAttribute("Xdensity", Integer.toString(dpi));
	        jfif.setAttribute("Ydensity", Integer.toString(dpi));
	 
	    }
	 
	    if(JPEGcompression>=0 && JPEGcompression<=1f){

	        //old compression
	        //jpegEncodeParam.setQuality(JPEGcompression,false);
	 
	        // new Compression
	        ImageWriteParam jpegParams =  imageWriter.getDefaultWriteParam();
	        jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
	        jpegParams.setCompressionQuality(JPEGcompression);
	 
	    }
	 
	    //old write and clean
	    //jpegEncoder.encode(image_to_save, jpegEncodeParam);
	 
	    //new Write and clean up
	    imageWriter.write(imageMetaData, new IIOImage(image_to_save, null, null), null);
	    ios.close();
	    imageWriter.dispose();
	 
	}
	
	public static void showMetada(File jpegImageFile) throws IOException,
			ImageReadException, ImageWriteException {

		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegImageFile);
			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					System.out.println(tag);
				}
			}
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			File f = new File("/home/jackson/Imagens/teste3.jpeg");
			MetaJpeg.saveAsJPEG("9", ImageIO.read(new File("/home/jackson/Imagens/teste3.jpeg")), 1f, new FileOutputStream(f));
			showMetada(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		}
	}

}
