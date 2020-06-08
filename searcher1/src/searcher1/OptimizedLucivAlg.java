package searcher1;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class OptimizedLucivAlg extends LucivAlg {
	
	protected int d_min,
		opt2_delta;
	
	protected Hashtable<String, Integer> d;

	public OptimizedLucivAlg(String pattern, double k) {
		super(pattern, k);
		this.d = new Hashtable<String, Integer>();
	}
	
	protected int d_di(String s1) {
		// Optimization 5
		Integer res = this.d.get(s1);
		if (res != null)
			return res;
		res = super.d_di(s1);
		this.d.put(s1, res);
		return res;
	}
	
	protected boolean compare(String w1, String w2) {
		int d1 = this.d_di(w1),
				d2 = this.d_di(w2);
		// Optimization 2.1
		this.d_min = this.d_min > d1 ? d1 : this.d_min;
		this.opt2_delta = d1 > this.d_min + 1 ? (d1 - this.d_min) / 2 : 1;
		if (d1 == d2)
			return w1.length() > w2.length();
		else
			return d1 < d2;
	}
	
	private HashSet<Pair<Integer, Integer>> widenToWholeWords(String text, Set<Pair<Integer, Integer>> ranges) {
		 LinkedList<Integer> leftBorders = new LinkedList<Integer>(),
				 rightBorders = new LinkedList<Integer>();
		 int len = text.length();
		 for (int i = 0; i < len; ++i) {
			 char c = text.charAt(i);
			 if (c == ' ' || c == '\n') {
				 leftBorders.add(i + 1);
				 rightBorders.add(i);
			 }				 
		 }
		 Collections.reverse(leftBorders);
		 int leftBorder, rightBorder;
		 HashSet<Pair<Integer, Integer>> result = new HashSet<Pair<Integer, Integer>>();
		 for (Pair<Integer, Integer> range : ranges) {
			 leftBorder = range.first;
			 for (int i : leftBorders)
				 if (i <= leftBorder) {
					 leftBorder = i;
					 break;
				 }
			 rightBorder = range.second;
			 for (int i : rightBorders)
				 if (i >= rightBorder) {
					 rightBorder = i;
					 break;
				 }
			 result.add(new Pair<Integer, Integer>(leftBorder, rightBorder));
		 }
		 return result;
	}

	public Set<Pair<Integer, Integer>> execute(String text){
		int begin = 0,
				end = L_w,
        		textLength = text.length(),
        		delta,
        		d;
        
		// Phase 1
		LinkedList<Pair<Integer, Integer>> W1 = new LinkedList<Pair<Integer, Integer>>();
        String w1;
        while (end < textLength) {
        	w1 = text.substring(begin, end);
        	d = this.d_di(w1);
        	if (d <= this.k_di)
        		W1.add(new Pair<Integer, Integer>(begin, end));
        	// Optimization 1        	
        	delta = d > this.k_di + 1 ? (int) ((d - this.k_di) / 2) : 1;
        	begin += delta;
        	end += delta;
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
        		this.d_min = Integer.MAX_VALUE;
        		while (end < wPair.second) {
        			w2 = text.substring(begin, end);
        			if (this.compare(w2, w21)) {
        				w21 = w2;
        				b = begin;
        				e = end;
        			}
        			// Optimization 2.2
        			begin += this.opt2_delta;
        			end += this.opt2_delta;
        		}
        	}
        	W2.add(new Pair<Integer, Integer>(b, e));
        }
        
        // Phase 3
        // Optimization 4
        HashSet<Pair<Integer, Integer>> W3 = this.widenToWholeWords(text, new HashSet<Pair<Integer, Integer>>(W2)),
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
