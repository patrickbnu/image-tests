package teste;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarInputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class Teste {
	
	private static final int width = 1000;
	private static final int height = 1384;
	private static final int compressao = 60;
	
	public static void main(String[] args) throws IOException {
		// 1000 x 1384
		// .jpg
		// 60% compress
		
		// preencher com tarjas para entrar na proporção
		
		// redimensionar imagem
		
		// comprimir (??)
		
		BufferedImage read = ImageIO.read(new File("teste.png"));
		System.out.println(read);
		
		
		BufferedImage resized = Scalr.resize(read, 500);
		
		ImageIO.write(resized, "png", new File("teste500.png"));
		
		Graphics2D g = resized.createGraphics();
		g.drawImage(resized, 0, 0, null);
		// g.setPaint ( new Color ( r, g, b ) );
		// g.fillRect ( 0, 0, b_img.getWidth(), b_img.getHeight() );
		g.dispose();
		
	}
	//https://commons.apache.org/proper/commons-imaging/gettingstarted.html
	private BufferedImage tratarImagem(BufferedImage image) {
		// 72 DPI - verificar ordem dessa chamada
		image = ajustarDPI(image);
		
		// width 1000
		image = redimensionarLargura(image, width);
		
		// cortar ou preencher centralizado,
		image = tratarAltura(image, height);
		
		// aplicar compressão 60%
		image = comprimirJPG(image, compressao);
		
		return image;
		
	}
	
	private BufferedImage ajustarDPI(BufferedImage image) {
		return image;
	}
	
	private BufferedImage redimensionarLargura(BufferedImage image, int _width) {
		BufferedImage resized = Scalr.resize(image, _width, 0);
		return resized;
	}
	
	private BufferedImage tratarAltura(BufferedImage image, int expectedHeight) {
		
		if ( image.getHeight() > expectedHeight ){
			// cortar imagem
		} else {
			// preencher imagem
		}
		return image;
	}
	
	private BufferedImage comprimirJPG(BufferedImage image, int compressao2) {
		return image;
	}
	
}
