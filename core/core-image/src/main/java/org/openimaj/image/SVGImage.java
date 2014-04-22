package org.openimaj.image;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Comparator;
import java.util.Scanner;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.renderer.ImageRenderer;
import org.openimaj.image.renderer.RenderHints;
import org.openimaj.image.renderer.SVGRenderHints;
import org.openimaj.image.renderer.SVGRenderer;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Circle;
import org.openimaj.math.geometry.shape.Rectangle;

import Jama.Matrix;

public class SVGImage extends Image<Float[], SVGImage> {
	
	private SVGRenderer renderer;

	/**
	 * @param hints
	 */
	public SVGImage(SVGRenderHints hints) {
		this.renderer = new SVGRenderer(hints);
	}
	

	private SVGImage() {
		// TODO Auto-generated constructor stub
	}

	public SVGImage(int w, int h) {
		this(new SVGRenderHints(w, h));
	}

	@Override
	public SVGImage abs() {
		return this;
	}

	@Override
	public SVGImage addInplace(Image<?, ?> im) {
		if(!(im instanceof SVGImage)){
			this.renderer.drawOIImage(im);
		} else {
			this.renderer.drawImage((SVGImage) im, 0, 0);
		}
		return null;
	}

	@Override
	public SVGImage addInplace(Float[] num) {
		return this;
	}

	@Override
	public SVGImage clip(Float[] min, Float[] max) {
		return this;
	}

	@Override
	public SVGImage clipMax(Float[] thresh) {
		return this;
	}

	@Override
	public SVGImage clipMin(Float[] thresh) {
		return this;
	}

	@Override
	public SVGImage clone() {
		SVGImage svgImage = new SVGImage();
		svgImage.renderer = new SVGRenderer(svgImage, this.renderer.getGraphics2D().create());
		return svgImage;
	}
	
	@Override
	public SVGRenderer createRenderer() {
		return this.renderer;
	}

	@Override
	public ImageRenderer<Float[], SVGImage> createRenderer(RenderHints options) {
		return this.renderer;
	}

	@Override
	public SVGImage divideInplace(Image<?, ?> im) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage divideInplace(Float[] val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage extractROI(int x, int y, SVGImage img) {
		img.renderer = new SVGRenderer(img,img.renderer.getRenderHints(),this.renderer.getGraphics2D().create(x, y, img.getWidth(), img.getHeight()));
		return img;
	}

	@Override
	public SVGImage extractROI(int x, int y, int w, int h) {
		SVGImage ret = new SVGImage(w,h);
		return extractROI(x, y, ret);
	}

	@Override
	public SVGImage fill(Float[] colour) {
		SVGRenderHints hint = (SVGRenderHints)this.renderer.getRenderHints();
		this.renderer = new SVGRenderer(hint);
		this.renderer.drawShapeFilled(this.getBounds(), colour);
		return this;
	}

	@Override
	public SVGImage flipX() {
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(this.getWidth(), 0);
		this.renderer.getGraphics2D().transform(tx);
		return this;
	}

	@Override
	public SVGImage flipY() {
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -this.getHeight());
		this.renderer.getGraphics2D().transform(tx);
		return this;
	}

	@Override
	public Rectangle getContentArea() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}

	@Override
	public SVGImage getField(org.openimaj.image.Image.Field f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage getFieldCopy(org.openimaj.image.Image.Field f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage getFieldInterpolate(org.openimaj.image.Image.Field f) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getHeight() {
		return this.renderer.getGraphics2D().getSVGCanvasSize().height;
	}

	@Override
	public Float[] getPixel(int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Comparator<? super Float[]> getPixelComparator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Float[] getPixelInterp(double x, double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Float[] getPixelInterp(double x, double y, Float[] backgroundColour) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getWidth() {
		return this.renderer.getGraphics2D().getSVGCanvasSize().width;
	}

	@Override
	public SVGImage internalCopy(SVGImage im) {
		this.renderer = im.renderer.clone();
		this.renderer.setImage(this);
		return this;
	}

	@Override
	public SVGImage internalAssign(SVGImage im) {
		this.renderer = im.renderer;
		return this;
	}

	@Override
	public SVGImage internalAssign(int[] pixelData, int width, int height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage inverse() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Float[] max() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Float[] min() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage multiplyInplace(Image<?, ?> im) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage multiplyInplace(Float[] num) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage newInstance(int width, int height) {
		return new SVGImage(width, height);
	}

	@Override
	public SVGImage normalise() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPixel(int x, int y, Float[] val) {
		this.renderer.drawPoint(new Point2dImpl(x, y), val, 1);
	}

	@Override
	public SVGImage subtractInplace(Image<?, ?> im) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage subtractInplace(Float[] num) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage threshold(Float[] thresh) {
		throw new UnsupportedOperationException();
	}
	
	private static class BufferedImageTranscoder extends ImageTranscoder{

		private BufferedImage img;

		@Override
		public BufferedImage createImage(int w, int h) {
			BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			return bi;
		}

		@Override
		public void writeImage(BufferedImage img, TranscoderOutput arg1)
				throws TranscoderException {
			this.img = img;
		}
		
		public BufferedImage getBufferedImage(){
			return this.img;
		}
		
	}
	
	@Override
	public byte[] toByteImage() {
		MBFImage mbf = createMBFImage();
		return mbf.toByteImage();
	}


	public MBFImage createMBFImage() {
		BufferedImageTranscoder t = new BufferedImageTranscoder();
		t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float)getWidth());
		t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float)getHeight());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			this.renderer.write(new OutputStreamWriter(baos));
			baos.flush();
			baos.close();
			byte[] barr = baos.toByteArray();
			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(barr ));
			t.transcode(input, null);
		} catch (SVGGraphics2DIOException e) {
		} catch (IOException e) {
		} catch (TranscoderException e) {
		}
		MBFImage mbf = ImageUtilities.createMBFImage(t.getBufferedImage(), true);
		return mbf;
	}

	@Override
	public int[] toPackedARGBPixels() {
		MBFImage mbf = createMBFImage();
		return mbf.toPackedARGBPixels();
	}

	@Override
	public SVGImage zero() {
		SVGRenderHints hint = (SVGRenderHints)this.renderer.getRenderHints();
		this.renderer = new SVGRenderer(hint);
		this.renderer.drawShapeFilled(this.getBounds(), RGBColour.BLACK);
		return this;
	}

	@Override
	public SVGImage overlayInplace(SVGImage image, int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SVGImage replace(Float[] target, Float[] replacement) {
		throw new UnsupportedOperationException();
	}
	
	public static void main(String[] args) throws IOException, TranscoderException {
		SVGImage img = new SVGImage(10000,10000);
		img.drawShape(new Circle(50, 50, 30), RGBColour.RED);
//		ImageUtilities.write(img, new File("/home/ss/out.pdf"), new PDFTranscoder());
	}

}
