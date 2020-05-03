package searcher1;

import java.util.HashSet;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XComponent;
import com.sun.star.text.XText;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;

public class Main {
	
	private static int d_di(String s1, String s2) {
		int m = s1.length(), n = s2.length(); 
        int L[][] = new int[m + 1][n + 1]; 
        for (int i = 0; i <= m; i++) { 
            for (int j = 0; j <= n; j++) { 
                if (i == 0 || j == 0) { 
                    L[i][j] = 0; 
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) { 
                    L[i][j] = L[i - 1][j - 1] + 1; 
                } else { 
                    L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]); 
                } 
            } 
        } 
        int lcs = L[m][n];
        
        return (m - lcs) + (n - lcs); 
	};
	
	private static boolean compare(String w1, String w2, String p) {
		int d1 = d_di(w1, p),
				d2 = d_di(w2, p);
		if (d1 == d2)
			return w1.length() > w2.length();
		else
			return d1 < d2;
	}

	public static void main(String[] args) throws Exception {
		
		String file = args[0],
				pattern = "сто триллионов миллиардов лет"; //args[1];
		double k =  0.577;
		
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
        String text = xtext.getString();
        
        int len = pattern.length(),
        		L_w = (int) Math.round(len / k),
        		begin = 0,
        		end = L_w,
        		textLength = text.length();
        double k_di = len * (1/k + 1) * (1 - k*k);
        
        // Phase 1
        HashSet<Pair<Integer, Integer>> W1 = new HashSet<Pair<Integer, Integer>>();
        String w1;
        while (end < textLength) {
        	w1 = text.substring(begin, end);
        	if (d_di(w1, pattern) <= k_di)
        		W1.add(new Pair<Integer, Integer>(begin, end));
        	++begin;
        	++end;
        }
        
        // Phase 2
        HashSet<Pair<Integer, Integer>> W2 = new HashSet<Pair<Integer, Integer>>();
        String w, w21, w2;
        int b, e;
        for (Pair<Integer, Integer> wPair : W1) {
        	b = wPair.first;
        	e = wPair.second;
        	w = text.substring(b, e);
        	w21 = w;
        	for (int l = (int) Math.round(k * len); l < (int) Math.round(len / k); ++l) {
        		begin = wPair.first;
        		end = begin + l;
        		while (end < wPair.second) {
        			w2 = text.substring(begin, end);
        			if (compare(w2, w21, pattern)) {
        				w21 = w2;
        				b = begin;
        				e = end;
        			}
        			++begin;
        			++end;
        		}
        	}
        	W2.add(new Pair<Integer, Integer>(b, e));
        }
        
        // Phase 3
        HashSet<Pair<Integer, Integer>> W3 = (HashSet<Pair<Integer, Integer>>) W2.clone();
        boolean remove;
        for (Pair<Integer, Integer> w2pair : W2) {
        	remove = false;
        	for (Pair<Integer, Integer> w3pair : W3)
        		if ((w3pair.first <= w2pair.first) &&
        				(w2pair.second <= w3pair.second) &&
        				(!w3pair.equals(w2pair))) {
        			remove = true;
        			break;
        		}
        	if (remove)
        		W3.remove(w2pair);
        }
        
        // output
        System.out.println("-----");
        for (Pair<Integer, Integer> pair : W3)
        	System.out.println(text.substring(pair.first, pair.second));
        System.out.println("-----");
        
        System.out.println("text length equals " + text.length());
        System.out.println("L_w = " + L_w);
        System.out.println("W1 size equals " + W1.size());
        System.exit(0);
	}

}
