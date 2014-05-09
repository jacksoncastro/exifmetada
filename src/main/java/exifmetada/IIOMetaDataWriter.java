package exifmetada;

import java.io.*;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.*;

import org.w3c.dom.*;

public class IIOMetaDataWriter {

	public static void run(String[] args) throws IOException {
		try {
			File f = new File("/home/jackson/Imagens/teste.jpeg");

			ImageInputStream ios = ImageIO.createImageInputStream(f);

			Iterator readers = ImageIO.getImageReaders(ios);

			ImageReader reader = (ImageReader) readers.next();

			reader.setInput(ImageIO.createImageInputStream(f));

			ImageWriter writer = ImageIO.getImageWriter(reader);

			writer.setOutput(ImageIO.createImageOutputStream(f));

			JPEGImageWriteParam param = new JPEGImageWriteParam(
					Locale.getDefault());

			IIOMetadata metaData = writer.getDefaultStreamMetadata(param);

			String MetadataFormatName = metaData.getNativeMetadataFormatName();

			IIOMetadataNode root = (IIOMetadataNode) metaData
					.getAsTree(MetadataFormatName);

			IIOMetadataNode markerSequence = getChildNode(root,
					"markerSequence");
			if (markerSequence == null) {
				markerSequence = new IIOMetadataNode("JPEGvariety");
				root.appendChild(markerSequence);
			}

			IIOMetadataNode jv = getChildNode(root, "JPEGvariety");
			if (jv == null) {
				jv = new IIOMetadataNode("JPEGvariety");
				root.appendChild(jv);
			}

			IIOMetadataNode child = getChildNode(jv, "myNode");
			if (child == null) {
				child = new IIOMetadataNode("myNode");
				jv.appendChild(child);
			}
			child.setAttribute("myAttName", "myAttValue");

			metaData.mergeTree(MetadataFormatName, root);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	protected static IIOMetadataNode getChildNode(Node n, String name) {
		NodeList nodes = n.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node child = nodes.item(i);
			if (name.equals(child.getNodeName())) {
				return (IIOMetadataNode) child;
			}
		}
		return null;
	}

	static void displayMetadata(Node node, int level) {
		indent(level); // emit open tag
		System.out.print("<" + node.getNodeName());
		NamedNodeMap map = node.getAttributes();
		if (map != null) { // print attribute values
			int length = map.getLength();
			for (int i = 0; i < length; i++) {
				Node attr = map.item(i);
				System.out.print(" " + attr.getNodeName() + "=\""
						+ attr.getNodeValue() + "\"");
			}
		}

		Node child = node.getFirstChild();
		if (child != null) {
			System.out.println(">"); // close current tag
			while (child != null) { // emit child tags recursively
				displayMetadata(child, level + 1);
				child = child.getNextSibling();
			}
			indent(level); // emit close tag
			System.out.println("</" + node.getNodeName() + ">");
		} else {
			System.out.println("/>");
		}
	}

	static void indent(int level) {
		for (int i = 0; i < level; i++) {
			System.out.print(" ");
		}
	}
	
	public static void main(String[] args) {
		try {
			IIOMetaDataWriter.run(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}