/**
 *
 */
package ca.footeware.e4.textify.ui.providers;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Provides...wait for it...images!
 */
public class ImageProvider {

	private static final String IMAGE_PATH = "/icons/";
	private Image backgroundImage;
	private Image keyboardImage;
	private Image menuImage;
	private Image newImage;
	private Image openImage;
	private Image programmerImage;
	private Image saveAsImage;
	private Image saveImage;
	private Image searchImage;

	/**
	 * Create and return an image with the provided file name.
	 *
	 * @param name {@link String}
	 * @return {@link Image}
	 */
	private Image createImage(String name) {
		InputStream in = getClass().getResourceAsStream(IMAGE_PATH + name);
		if (in != null) {
			try {
				return new Image(Display.getDefault(), in);
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return null;
	}

	/**
	 * Disposes the images and removes preferences listener.
	 */
	public void dispose() {
		if (newImage != null && !newImage.isDisposed()) {
			newImage.dispose();
		}
		if (openImage != null && !openImage.isDisposed()) {
			openImage.dispose();
		}
		if (saveImage != null && !saveImage.isDisposed()) {
			saveImage.dispose();
		}
		if (saveAsImage != null && !saveAsImage.isDisposed()) {
			saveAsImage.dispose();
		}
		if (menuImage != null && !menuImage.isDisposed()) {
			menuImage.dispose();
		}
		if (searchImage != null && !searchImage.isDisposed()) {
			searchImage.dispose();
		}
		if (programmerImage != null && !programmerImage.isDisposed()) {
			programmerImage.dispose();
		}
		if (keyboardImage != null && !keyboardImage.isDisposed()) {
			keyboardImage.dispose();
		}
		if (backgroundImage != null && !backgroundImage.isDisposed()) {
			backgroundImage.dispose();
		}
	}

	/**
	 * @return the backgroundImage
	 */
	public Image getBackgroundImage() {
		if (backgroundImage == null) {
			backgroundImage = createImage("background.png");
		}
		return backgroundImage;
	}

	/**
	 * @return the keyboardImage
	 */
	public Image getKeyboardImage() {
		if (keyboardImage == null) {
			keyboardImage = createImage("keyboard.png");
		}
		return keyboardImage;
	}

	/**
	 * @return the menuImage
	 */
	public Image getMenuImage() {
		if (menuImage == null) {
			menuImage = createImage("menu.png");
		}
		return menuImage;
	}

	/**
	 * @return the newImage
	 */
	public Image getNewImage() {
		if (newImage == null) {
			newImage = createImage("new.png");
		}
		return newImage;
	}

	/**
	 * @return the openImage
	 */
	public Image getOpenImage() {
		if (openImage == null) {
			openImage = createImage("open.png");
		}
		return openImage;
	}

	/**
	 * @return the programmerImage
	 */
	public Image getProgrammerImage() {
		if (programmerImage == null) {
			programmerImage = createImage("programmer.jpg");
		}
		return programmerImage;
	}

	/**
	 * @return the saveAsImage
	 */
	public Image getSaveAsImage() {
		if (saveAsImage == null) {
			saveAsImage = createImage("save-as.png");
		}
		return saveAsImage;
	}

	/**
	 * @return the saveImage
	 */
	public Image getSaveImage() {
		if (saveImage == null) {
			saveImage = createImage("save.png");
		}
		return saveImage;
	}

	/**
	 * @return the searchImage
	 */
	public Image getSearchImage() {
		if (searchImage == null) {
			searchImage = createImage("search-24.png");
		}
		return searchImage;
	}
}
