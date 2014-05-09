package exifmetada;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class Test {

	public static void test(BufferedImage image, File jpegImageFile) {
		// new ExifRewriter().updateExifMetadataLossless(src, os, outputSet);
	}

	public static void addExifMetadata(File jpegImageFile, File dst)
			throws IOException, ImageReadException, ImageWriteException {
		OutputStream os = null;
		try {
			TiffOutputSet outputSet = new TiffOutputSet();
			TiffOutputField colorspace = TiffOutputField.create(
                    TiffConstants.EXIF_TAG_APPLICATION_NOTES, outputSet.byteOrder, "asd");
			TiffOutputDirectory exifDirectory = outputSet
					.getOrCreateExifDirectory();
			exifDirectory.removeField(TiffConstants.EXIF_TAG_APPLICATION_NOTES);
			exifDirectory.add(colorspace);

			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);
			new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
					outputSet);

			os.close();
			os = null;
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {

				}
		}
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
			addExifMetadata(new File("/home/jackson/Imagens/ceu.jpg"), new File("/home/jackson/Imagens/teste.jpeg"));
			showMetada(new File("/home/jackson/Imagens/teste.jpeg"));
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}