package image.jpeg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import com.twelvemonkeys.image.ResampleOp;

/**
 * @author Patrick Nascimento
 * @since 17/05/2019 16:20:00
 *
 */
public class JPEGImageHelper {
	
	public static final String IMAGE_FORMAT = "jpeg";
	
	private JPEGImageHelper() {}
	
	/**
	 * Resizes the image to a new width keeping the proportion on height.
	 * 
	 * @param image the image to be resized.
	 * @param width new width of image.
	 * @return a new BufferedImage resized.
	 */
	public static BufferedImage resizeByWidth(BufferedImage image, int width) {
		int newHeight = (image.getHeight() * width) / image.getWidth();
		
		 // A good default filter, see class documentation for more info
		BufferedImageOp resampler = new ResampleOp(width, newHeight, ResampleOp.FILTER_LANCZOS);
		
		return resampler.filter(image, null);
	}
	
	/**
	 * Adjust the image height according two rules:
	 * 
	 * If image is smaller than expected height, create a new image and fill top and bottom (50% each) with a white background.
	 * If image is bigger than expected, crop and create a new image with expected height. The vertical cut is made centralized.
	 * 
	 * @param image the image to be adjusted.
	 * @param expectedHeight expected height of new image.
	 * @return a new BufferedImage with right height.
	 */
	public static BufferedImage adjustHeight(BufferedImage image, int expectedHeight) {
		
		int originalWidth = image.getWidth();
		
		if (image.getHeight() > expectedHeight) {
			// image size greater than expected
			
			// gets Y coordinate to crop the image centralized
			int y =  ((image.getHeight() - expectedHeight) / 2);
			BufferedImage croped = image.getSubimage(0, y, originalWidth, expectedHeight);
			
			// create a copy because getSubimage shares the original data array.
			BufferedImage copyOfImage = new BufferedImage(croped.getWidth(), croped.getHeight(), BufferedImage.TYPE_INT_RGB);
			copyOfImage.createGraphics().drawImage(croped, 0, 0, null);
			return copyOfImage;
			
		} else {
			// image size smaller than expected
			
			// gets Y coordinate centralizing the image inside expected height
			int y = (( expectedHeight - image.getHeight()) / 2);
			
			// create a new image with the expected height
			BufferedImage copyOfImage = new BufferedImage(originalWidth, expectedHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = copyOfImage.createGraphics();
			
			// fill all image with a white background color
			graphics.setBackground(Color.WHITE);
			graphics.fillRect(0, 0, originalWidth, expectedHeight);
			
			// draw original image centralized
			graphics.drawImage(image, 0, y, null);
			
			return copyOfImage;
		}
	}
	
	/**
	 * Save the image to the specified file, applying dpi and compression if informed. <br><br>
	 * 
	 * <b>dpi</b> dots per inch density value, applied to horizontal and vertical resolutions. <br>
	 * <b>compression</b> compression quality, float from 0 to 1, where 1 prioritizes quality to compression and 0 prioritizes compression to quality.
	 * 
	 * @param output File output to write the image.
	 * @param image BufferedImage containing the image data.
	 * @param dpi Dots per Inch value, applied if greater than zero.
	 * @param compression Compression quality value, applied if greater than zero.
	 * @return true case saved the image successfully, otherwise false.
	 * @throws IOException exceptions writing image in file.
	 */
	public static boolean saveImage(File output, BufferedImage image, int dpi, float compression) throws IOException {
		Files.delete(output.toPath());
		
		for (Iterator <ImageWriter> iw = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT); iw.hasNext();) {
			ImageWriter writer = iw.next();
			ImageWriteParam writeParam = writer.getDefaultWriteParam();
			IIOMetadata metadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB), writeParam);
			
			if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
				continue;
			} 
			
			if (dpi > 0) { 
				setImageMetadataDPI(metadata, dpi);
			}
			final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
			try {
				writer.setOutput(stream);
				
				if (compression > 0) {
					// instantiate an ImageWriteParam object with default compression options
					writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					writeParam.setCompressionQuality(compression);  
				}
				
				writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
			} finally {
				stream.close();
			}
			return true;
		}
		
		return false;
	}
	
	private static void setImageMetadataDPI(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException {
		String  densityUnitsPixelsPerInch = "01";
		
		String metadataFormat = "javax_imageio_jpeg_image_1.0";
		IIOMetadataNode root = new IIOMetadataNode(metadataFormat);
		IIOMetadataNode jpegVariety = new IIOMetadataNode("JPEGvariety");
		IIOMetadataNode markerSequence = new IIOMetadataNode("markerSequence");
		
		IIOMetadataNode app0JFIF = new IIOMetadataNode("app0JFIF");
		app0JFIF.setAttribute("majorVersion", "1");
		app0JFIF.setAttribute("minorVersion", "2");
		app0JFIF.setAttribute("thumbWidth", "0");
		app0JFIF.setAttribute("thumbHeight", "0");
		app0JFIF.setAttribute("resUnits", densityUnitsPixelsPerInch);
		app0JFIF.setAttribute("Xdensity", String.valueOf(dpi));
		app0JFIF.setAttribute("Ydensity", String.valueOf(dpi));
		
		root.appendChild(jpegVariety);
		root.appendChild(markerSequence);
		jpegVariety.appendChild(app0JFIF);
		
		metadata.mergeTree(metadataFormat, root);
	}
}
