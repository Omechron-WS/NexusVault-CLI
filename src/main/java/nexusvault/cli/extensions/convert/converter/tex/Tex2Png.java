package nexusvault.cli.extensions.convert.converter.tex;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import nexusvault.cli.core.PathUtil;
import nexusvault.cli.extensions.convert.ConversionManager;
import nexusvault.cli.extensions.convert.Converter;
import nexusvault.format.tex.Image;
import nexusvault.format.tex.TextureReader;
import nexusvault.format.tex.util.AwtImageConverter;

public final class Tex2Png implements Converter {

	private final boolean exportMipMaps;

	public Tex2Png(boolean exportMipMaps) {
		this.exportMipMaps = exportMipMaps;
	}

	@Override
	public void deinitialize() {
	}

	@Override
	public void convert(ConversionManager manager) throws IOException {
		final var resource = manager.getResource();
		final var images = new LinkedList<Image>();
		if (this.exportMipMaps) {
			final var data = resource.getData();
			final var texture = TextureReader.read(data);
			for (var i = 0; i < texture.getMipMapCount(); ++i) {
				images.add(texture.getMipMap(i));
			}
		} else {
			images.add(TextureReader.readFirstImage(resource.getData()));
		}

		final var fileName = PathUtil.getFileName(resource.getFile());
		for (var i = 0; i < images.size(); ++i) {
			final var image = images.get(i);
                        if(image.getDepth() > 1) {
                            ArrayList<Image> slices = image.split3DImage();
                            for(var j = 0; j < slices.size(); ++j) {
                                final var outputPath = manager.resolveOutputPath(getFileName(fileName, i, j) + ".png");
                                writeImage(slices.get(j), outputPath);
                                manager.addCreatedFile(outputPath);
                            }
                        }
                        else {
                            final var outputPath = manager.resolveOutputPath(getFileName(fileName, i, -1) + ".png");
                            writeImage(image, outputPath);
                            manager.addCreatedFile(outputPath);
                        }
		}
	}

	private String getFileName(String fileName, int mipmap, int slice) {
                if(exportMipMaps) {
                        fileName += String.format(".m%02d", mipmap);
                }
                if(slice != -1) {
                        fileName += String.format(".s%02d", slice);
                }
		return fileName;
	}

	private void writeImage(Image image, Path path) throws IOException {
		final var bufferedImage = AwtImageConverter.convertToBufferedImage(image);
		try (OutputStream writer = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
			ImageIO.write(bufferedImage, "PNG", writer);
		}
	}

}
