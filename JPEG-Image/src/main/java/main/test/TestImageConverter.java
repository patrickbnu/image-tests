package main.test;

import image.jpeg.JPEGImageHelper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class TestImageConverter {
	
	private static final int width = 1000;
	private static final int height = 1384;
	
	private static final int dpi = 72;
	private static final float compression = 0.6f;
	
	public static void main(String[] args) throws IOException, ImageProcessingException {
		BufferedImage image = ImageIO.read(new File("image/original.jpg"));
		
		image = JPEGImageHelper.resizeByWidth(image, width);
		
		image = JPEGImageHelper.adjustHeight(image, height);
		
		JPEGImageHelper.saveImage(new File("image/best.jpg"), image, dpi, compression);
	}
	
	// https://stackoverflow.com/questions/19224328/how-to-read-jpeg-file-attributes-with-java
	static void printMetadata(BufferedImage path) throws IOException, ImageProcessingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(path, "jpeg", os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		
		Metadata metadata = JpegMetadataReader.readMetadata(is);
		
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				System.out.println(tag);
			}
		}
		
		System.out.println(" ---------------- ------------- ---------------");
	}
	
}
