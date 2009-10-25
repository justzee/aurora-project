package uncertain.ide.eclipse.editor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class XmlWriter extends Thread {
	private PipedOutputStream out;
	private PipedInputStream in;
	private Document doc;
	
	public XmlWriter() throws IOException, ParserConfigurationException {
		out = new PipedOutputStream();
		in = new PipedInputStream(out);
		DocumentBuilder builder =
			DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.newDocument();
	}

	public Document getDocument() {
		return doc;
	}
	public InputStream getStream() {
		return in;
	}

	public void run(){
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(doc), new StreamResult(out));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
