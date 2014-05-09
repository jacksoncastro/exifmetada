package exifmetada;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;
import org.apache.sanselan.formats.tiff.write.TiffOutputDirectory;
import org.apache.sanselan.formats.tiff.write.TiffOutputField;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;
import org.apache.sanselan.util.IOUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class WriteExifMetadataExample {

	public void showMetada(File jpegImageFile) throws IOException,
			ImageReadException, ImageWriteException {
//		IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
		
		// for (int i = 0; i < metadata.getItems().size(); i++) {
		// System.out.println(metadata.getItems().get(i));
		// }
//		System.out.println(metadata);
		

		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegImageFile);
			for (com.drew.metadata.Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					System.out.println(tag);
				}
			}
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		}
	}

	public void removeExifMetadata(File jpegImageFile, File dst)
			throws IOException, ImageReadException, ImageWriteException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(dst);
			os = new BufferedOutputStream(os);

			new ExifRewriter().removeExifMetadata(jpegImageFile, os);
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {

				}
		}
	}

	/**
	 * This example illustrates how to add/update EXIF metadata in a JPEG file.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	public void changeExifMetadata(File jpegImageFile, File dst)
			throws IOException, ImageReadException, ImageWriteException {
		OutputStream os = null;
		try {
			TiffOutputSet outputSet = null;

			// note that metadata might be null if no metadata is found.
			IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			if (null != jpegMetadata) {
				// note that exif might be null if no Exif metadata is found.
				TiffImageMetadata exif = jpegMetadata.getExif();

				if (null != exif) {
					// TiffImageMetadata class is immutable (read-only).
					// TiffOutputSet class represents the Exif data to write.
					//
					// Usually, we want to update existing Exif metadata by
					// changing
					// the values of a few fields, or adding a field.
					// In these cases, it is easiest to use getOutputSet() to
					// start with a "copy" of the fields read from the image.
					outputSet = exif.getOutputSet();
				}
			}

			// if file does not contain any exif metadata, we create an empty
			// set of exif metadata. Otherwise, we keep all of the other
			// existing tags.
			if (null == outputSet)
				outputSet = new TiffOutputSet();

			{
				// Example of how to add a field/tag to the output set.
				//
				// Note that you should first remove the field/tag if it already
				// exists in this directory, or you may end up with duplicate
				// tags. See above.
				//
				// Certain fields/tags are expected in certain Exif directories;
				// Others can occur in more than one directory (and often have a
				// different meaning in different directories).
				//
				// TagInfo constants often contain a description of what
				// directories are associated with a given tag.
				//
				// see
				// org.apache.sanselan.formats.tiff.constants.AllTagConstants
				//
				
				TiffOutputField aperture = TiffOutputField.create(
						TiffConstants.EXIF_TAG_APERTURE_VALUE,
						outputSet.byteOrder, new Double(0.3));
				TiffOutputDirectory exifDirectory = outputSet
						.getOrCreateExifDirectory();
				// make sure to remove old value if present (this method will
				// not fail if the tag does not exist).
				exifDirectory
						.removeField(TiffConstants.EXIF_TAG_APERTURE_VALUE);
				exifDirectory.add(aperture);
			}

			{
				// Example of how to add/update GPS info to output set.

				// New York City
				double longitude = -75.0; // 74 degrees W (in Degrees East)
				double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees
				// North)

				outputSet.setGPSInDegrees(longitude, latitude);
			}

			// printTagValue(jpegMetadata, TiffConstants.TIFF_TAG_DATE_TIME);

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

	/**
	 * This example illustrates how to remove a tag (if present) from EXIF
	 * metadata in a JPEG file.
	 * 
	 * In this case, we remove the "aperture" tag from the EXIF metadata if
	 * present.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	public void removeExifTag(File jpegImageFile, File dst) throws IOException,
			ImageReadException, ImageWriteException {
		OutputStream os = null;
		try {
			TiffOutputSet outputSet = null;

			// note that metadata might be null if no metadata is found.
			IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			if (null != jpegMetadata) {
				// note that exif might be null if no Exif metadata is found.
				TiffImageMetadata exif = jpegMetadata.getExif();

				if (null != exif) {
					// TiffImageMetadata class is immutable (read-only).
					// TiffOutputSet class represents the Exif data to write.
					//
					// Usually, we want to update existing Exif metadata by
					// changing
					// the values of a few fields, or adding a field.
					// In these cases, it is easiest to use getOutputSet() to
					// start with a "copy" of the fields read from the image.
					outputSet = exif.getOutputSet();
				}
			}

			if (null == outputSet) {
				// file does not contain any exif metadata. We don't need to
				// update the file; just copy it.
				IOUtils.copyFileNio(jpegImageFile, dst);
				return;
			}

			{
				// Example of how to remove a single tag/field.
				// There are two ways to do this.

				// Option 1: brute force
				// Note that this approach is crude: Exif data is organized in
				// directories. The same tag/field may appear in more than one
				// directory, and have different meanings in each.
				outputSet.removeField(TiffConstants.EXIF_TAG_APERTURE_VALUE);

				// Option 2: precision
				// We know the exact directory the tag should appear in, in this
				// case the "exif" directory.
				// One complicating factor is that in some cases, manufacturers
				// will place the same tag in different directories.
				// To learn which directory a tag appears in, either refer to
				// the constants in ExifTagConstants.java or go to Phil Harvey's
				// EXIF website.
				TiffOutputDirectory exifDirectory = outputSet
						.getExifDirectory();
				if (null != exifDirectory)
					exifDirectory
							.removeField(TiffConstants.EXIF_TAG_APERTURE_VALUE);
			}

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

	/**
	 * This example illustrates how to set the GPS values in JPEG EXIF metadata.
	 * 
	 * @param jpegImageFile
	 *            A source image file.
	 * @param dst
	 *            The output file.
	 * @throws IOException
	 * @throws ImageReadException
	 * @throws ImageWriteException
	 */
	public void setExifGPSTag(File jpegImageFile, File dst) throws IOException,
			ImageReadException, ImageWriteException {
		OutputStream os = null;
		try {
			TiffOutputSet outputSet = null;

			// note that metadata might be null if no metadata is found.
			IImageMetadata metadata = Sanselan.getMetadata(jpegImageFile);
			JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
			if (null != jpegMetadata) {
				// note that exif might be null if no Exif metadata is found.
				TiffImageMetadata exif = jpegMetadata.getExif();

				if (null != exif) {
					// TiffImageMetadata class is immutable (read-only).
					// TiffOutputSet class represents the Exif data to write.
					//
					// Usually, we want to update existing Exif metadata by
					// changing
					// the values of a few fields, or adding a field.
					// In these cases, it is easiest to use getOutputSet() to
					// start with a "copy" of the fields read from the image.
					outputSet = exif.getOutputSet();
				}
			}

			// if file does not contain any exif metadata, we create an empty
			// set of exif metadata. Otherwise, we keep all of the other
			// existing tags.
			if (null == outputSet)
				outputSet = new TiffOutputSet();

			{
				// Example of how to add/update GPS info to output set.

				// New York City
				double longitude = -74.0; // 74 degrees W (in Degrees East)
				double latitude = 40 + 43 / 60.0; // 40 degrees N (in Degrees
				// North)

				outputSet.setGPSInDegrees(longitude, latitude);
			}

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
	
	public void getTagSpecific(File jpegImageFile) {
		
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegImageFile);
			// obtain the Exif directory
			ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			
			// query the tag's value
			String date = directory.getDescription(ExifSubIFDDirectory.TAG_APERTURE);
			System.out.println(date);
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getDecorater(File jpegImageFile) {
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(jpegImageFile);
			// obtain a specific directory
			ExifSubIFDDirectory directory = metadata.getDirectory(ExifSubIFDDirectory.class);
			
			// create a descriptor
			ExifSubIFDDescriptor descriptor = new ExifSubIFDDescriptor(directory);
			
			// get tag description
			String program = descriptor.getExposureProgramDescription();
			System.out.println(program);
		} catch (ImageProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		WriteExifMetadataExample example = new WriteExifMetadataExample();
		try {
			example.changeExifMetadata(new File(
					"/home/jackson/Imagens/teste.jpeg"), new File(
					"/home/jackson/Imagens/teste2.jpeg"));
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (ImageWriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		try {
//			example.showMetada(new File("/home/jackson/Imagens/teste2.jpeg"));
//		} catch (ImageReadException e) {
//			e.printStackTrace();
//		} catch (ImageWriteException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		example.getTagSpecific(new File("/home/jackson/Imagens/teste2.jpeg"));
		example.getDecorater(new File("/home/jackson/Imagens/teste2.jpeg"));
	}
}