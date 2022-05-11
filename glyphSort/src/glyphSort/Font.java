package glyphSort;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

public class Font {
	
	File fontFile;
	
	int lineHeight, base, scaleW, scaleH,
	pageCount, packed,
	alphaChnl, redChnl, greenChnl, blueChnl;
	
	Page[] pages;
	
	Char[] chars;
	
	Page getPage(int id) {
		for (Page p : pages) {
			if (p != null && p.id == id)
				return p;
		}
		return null;
	}
	
	
	
	
	
	
	
	public static Font parse(File fntFile) throws IOException {
		Font f = new Font();
		f.fontFile = fntFile;
		
		try (Scanner s = new Scanner(fntFile)) {
			int chIndex = 0;
			while (s.hasNext()) {
				Scanner lineS = new Scanner(s.nextLine());
				String entryType = lineS.next();
				Map<String, String> entryData = getKeyValues(lineS);
				if (entryType.equals("common")) {
					f.pageCount = Integer.parseInt(entryData.get("pages"));
					f.pages = new Page[f.pageCount];
					
					ifKeyExists(entryData, "lineHeight", data -> f.lineHeight = Integer.parseInt(data));
					ifKeyExists(entryData, "base", data -> f.base = Integer.parseInt(data));
					ifKeyExists(entryData, "scaleW", data -> f.scaleW = Integer.parseInt(data));
					ifKeyExists(entryData, "scaleH", data -> f.scaleH = Integer.parseInt(data));
					ifKeyExists(entryData, "packed", data -> f.packed = Integer.parseInt(data));
					ifKeyExists(entryData, "alphaChnl", data -> f.alphaChnl = Integer.parseInt(data));
					ifKeyExists(entryData, "redChnl", data -> f.redChnl = Integer.parseInt(data));
					ifKeyExists(entryData, "greenChnl", data -> f.greenChnl = Integer.parseInt(data));
					ifKeyExists(entryData, "blueChnl", data -> f.blueChnl = Integer.parseInt(data));
				}
				else if (entryType.equals("page")) {
					Page p = new Page();
					p.id = Integer.parseInt(entryData.get("id"));
					p.file = entryData.get("file");
					p.imageFile = new File(f.fontFile.getParentFile(), p.file);
					p.imageBuff = ImageIO.read(p.imageFile);
					f.pages[p.id] = p;
				}
				else if (entryType.equals("chars")) {
					f.chars = new Char[Integer.parseInt(entryData.get("count"))];
				}
				else if (entryType.equals("char")) {
					Char c = new Char();
					c.id = Integer.parseInt(entryData.get("id"));
					c.x = Integer.parseInt(entryData.get("x"));
					c.y = Integer.parseInt(entryData.get("y"));
					c.width = Integer.parseInt(entryData.get("width"));
					c.height = Integer.parseInt(entryData.get("height"));
					ifKeyExists(entryData, "xoffset", data -> c.xoffset = Integer.parseInt(data));
					ifKeyExists(entryData, "yoffset", data -> c.yoffset = Integer.parseInt(data));
					ifKeyExists(entryData, "xadvance", data -> c.xadvance = Integer.parseInt(data));
					c.page = Integer.parseInt(entryData.get("page"));
					c.chnl = Integer.parseInt(entryData.get("chnl"));
					
					c.imageBuff = f.getPage(c.page).imageBuff.getSubimage(c.x, c.y, c.width, c.height);
					
					long sum = 0;
					for (int x = 0; x < c.imageBuff.getWidth(); x++) {
						for (int y = 0; y < c.imageBuff.getHeight(); y++) {
							sum += c.imageBuff.getRGB(x, y) & 0x0000FF;
						}
					}
					
					c.brightness = sum / (float) (255 * c.imageBuff.getWidth() * c.imageBuff.getHeight());
					f.chars[chIndex++] = c;
				}
			}
		}
		
		return f;
	}
	
	
	private static <K, V> void ifKeyExists(Map<K, V> map, K key, Consumer<V> cons) {
		if (map.containsKey(key))
			cons.accept(map.get(key));
	}
	
	
	
	private static Map<String, String> getKeyValues(Scanner s) {
		Map<String, String> map = new LinkedHashMap<>();
		while (s.hasNext()) {
			String t = s.next();
			int eqPos = t.indexOf('=');
			String key = t.substring(0, eqPos);
			String value = t.substring(eqPos + 1);
			
			if (value.startsWith("\"")) {
				while (!value.endsWith("\"") && s.hasNext()) {
					value += " " + s.next();
				}
				value = value.substring(1, value.length() - 1); // remove ""
			}
			map.put(key, value);
		}
		return map;
	}
	
	
	
	public static class Page {
		int id;
		
		String file;
		File imageFile;
		
		BufferedImage imageBuff;
	}
	
	public static class Char {
		int id, x, y, width, height, xoffset, yoffset, xadvance, page, chnl;
		
		BufferedImage imageBuff;
		
		float brightness;
	}
}
