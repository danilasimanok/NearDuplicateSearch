package searcher1;

import java.util.Set;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.text.XText;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String file = args[0],
				pattern = args[1];
		double k = Double.parseDouble(args[2]); // 0.577
		
		com.sun.star.uno.XComponentContext xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
        System.out.println("Connected to a running office ...");
        com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();
        String available = (xMCF != null ? "available" : "not available");
        System.out.println("remote ServiceManager is " + available);
        
        Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);

        XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);

        PropertyValue[] loadProps = new PropertyValue[0];

        XComponent xWriterComponent = xComponentLoader.loadComponentFromURL("file:///" + file, "_blank", 0, loadProps);
        
        XTextDocument xWriterDocument =
                (XTextDocument)UnoRuntime.queryInterface(XTextDocument.class, xWriterComponent);
        
        XText xtext = xWriterDocument.getText();
        String text = xtext.getString().replaceAll("\r", "");
        
        NearDuplicateSearchAlgorithm alg = new OptimizedLucivAlg(pattern, k);
        Set<Pair<Integer, Integer>> result = alg.execute(text.toLowerCase());
        
        // output
        System.out.println("-----");
        for (Pair<Integer, Integer> pair : result)
        	System.out.println("{" + text.substring(pair.first, pair.second) + "}");
        System.out.println("-----");
        
        TextSelector.selectTextRanges(xtext, result, "Yu Gothic UI Semibold");
        
        System.exit(0);
	}

}
