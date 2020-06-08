package searcher1;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class LucivAlg implements NearDuplicateSearchAlgorithm {
	
	protected String pattern;
	protected double k,
		k_di;
	protected int len;
	protected int L_w;
	
	public LucivAlg(String pattern, double k) {
		this.pattern = pattern;
		this.len = pattern.length();
		this.k = k;
		this.k_di = this.len * (1/k + 1) * (1 - k*k);
		this.L_w = (int) Math.round(this.len / k);
	}
	
	protected int d_di(String s1) {
		int m = s1.length(), n = this.pattern.length();
        int L[][] = new int[m + 1][n + 1]; 
        for (int i = 0; i <= m; i++) { 
            for (int j = 0; j <= n; j++) { 
                if (i == 0 || j == 0) { 
                    L[i][j] = 0; 
                } else if (s1.charAt(i - 1) == this.pattern.charAt(j - 1)) { 
                    L[i][j] = L[i - 1][j - 1] + 1; 
                } else { 
                    L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]); 
                } 
            } 
        } 
        int lcs = L[m][n];
        
        return (m - lcs) + (n - lcs); 
	};
	
	protected boolean compare(String w1, String w2) {
		int d1 = this.d_di(w1),
				d2 = this.d_di(w2);
		if (d1 == d2)
			return w1.length() > w2.length();
		else
			return d1 < d2;
	}
	
	public Set<Pair<Integer, Integer>> execute(String text) {
		int begin = 0,
				end = L_w,
        		textLength = text.length();
        
		// Phase 1
		LinkedList<Pair<Integer, Integer>> W1 = new LinkedList<Pair<Integer, Integer>>();
        String w1;
        while (end < textLength) {
        	w1 = text.substring(begin, end);
        	if (this.d_di(w1) <= this.k_di)
        		W1.add(new Pair<Integer, Integer>(begin, end));
        	++begin;
        	++end;
        }
        
        // Phase 2
        LinkedList<Pair<Integer, Integer>> W2 = new LinkedList<Pair<Integer, Integer>>();
        String w, w21, w2;
        int b, e;
        for (Pair<Integer, Integer> wPair : W1) {
        	b = wPair.first;
        	e = wPair.second;
        	w = text.substring(b, e);
        	w21 = w;
        	for (int l = (int) Math.round(this.k * this.len); l < (int) Math.round(this.len / this.k); ++l) {
        		begin = wPair.first;
        		end = begin + l;
        		while (end < wPair.second) {
        			w2 = text.substring(begin, end);
        			if (this.compare(w2, w21)) {
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
        HashSet<Pair<Integer, Integer>> W3 = new HashSet<Pair<Integer, Integer>>(W2),
        		W31 = (HashSet<Pair<Integer, Integer>>) W3.clone();
        boolean remove;
        for (Pair<Integer, Integer> w2pair : W31) {
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
        
        return W3;
	}
}
