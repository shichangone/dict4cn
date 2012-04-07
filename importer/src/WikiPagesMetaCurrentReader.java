/*  Copyright (c) 2010 Xiaoyun Zhu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy  
 *  of this software and associated documentation files (the "Software"), to deal  
 *  in the Software without restriction, including without limitation the rights  
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell  
 *  copies of the Software, and to permit persons to whom the Software is  
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in  
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR  
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,  
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER  
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,  
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN  
 *  THE SOFTWARE.  
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.kk.kkdict.utils.Helper;

public class WikiPagesMetaCurrentReader {
    public static final String WIKI_PAGES_META_CURRENT_XML_FILE = "D:\\张克\\dev\\kkdict\\dicts\\wiki\\zhwiki-20120211-pages-meta-current.xml";

    private static final String[] LANGUAGES = { "zh", "jp", "ko", "de", "en", "ru", "fr", "es", "it" };

    public static void main(String args[]) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(WIKI_PAGES_META_CURRENT_XML_FILE), Helper.BUFFER_SIZE);

        String line;
        String name = null;
        Map<String, String> languages = null;
        String tmp;

        Set<String> irrelevantPrefixes = new HashSet<String>();
        boolean irrelevantPrefixesNeeded = true;
        while ((line = reader.readLine()) != null) {
            if (irrelevantPrefixesNeeded && line.contains("</namespaces>")) {
                irrelevantPrefixesNeeded = false;
            } else if (irrelevantPrefixesNeeded
                    && isNotEmptyOrNull(tmp = substringBetweenLast(line, ">", "</namespace>"))
                    && !(tmp = tmp.trim()).isEmpty()) {
                irrelevantPrefixes.add(tmp + ":");
            } else if (isNotEmptyOrNull(tmp = substringBetween(line, "<title>", "</title>"))) {
                if (name != null && languages != null && !languages.isEmpty()) {
                    System.out.println(name + " -> " + languages.toString());
                }
                boolean relevant = true;
                for (String prefix : irrelevantPrefixes) {
                    if (tmp.startsWith(prefix)) {
                        relevant = false;
                        break;
                    }
                }
                if (tmp.matches("(.?[0-9]+.?)+$")) {
                    relevant = false;
                }
                if (!relevant) {
                    name = null;
                } else {
                    name = tmp;
                    languages = new TreeMap<String, String>();
                }
            } else if (name != null) {
                if (isNotEmptyOrNull(tmp = substringBetween(line, "[[", "]]"))) {
                    for (String lng : LANGUAGES) {
                        if ((tmp = substringBetween(line, "[[" + lng + ":", "]]")) != null) {
                            languages.put(lng, tmp);
                            break;
                        }
                    }
                }
            }
        }
        reader.close();
    }

    public static final String substringBetween(final String text, final String start, final String end) {
        final int nStart = text.indexOf(start);
        final int nEnd = text.indexOf(end, nStart + start.length() + 1);
        if (nStart != -1 && nEnd != -1) {
            return text.substring(nStart + start.length(), nEnd);
        } else {
            return null;
        }
    }

    public final static String substringBetweenLast(String text, String start, String end) {
        int nEnd = text.lastIndexOf(end);
        int nStart = -1;
        if (nEnd > 1) {
            nStart = text.lastIndexOf(start, nEnd - 1);
        } else {
            return null;
        }
        if (nStart < nEnd && nStart != -1 && nEnd != -1) {
            return text.substring(nStart + start.length(), nEnd);
        } else {
            return null;
        }

    }

    public final static boolean isNotEmptyOrNull(String text) {
        return text != null && text.length() > 0;
    }

}
