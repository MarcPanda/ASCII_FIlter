package glyphSort;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class GlyphSort {
	static File fntFile = new File("../ascii_font.fnt");
	
	
	
	public static void main(String[] args) throws Exception {
		Font f = Font.parse(fntFile);
		
		Font.Char[] sortedChar = Arrays.copyOf(f.chars, f.chars.length);
		Arrays.sort(sortedChar, Comparator.comparing((Font.Char c) -> c.brightness));
		System.out.println("	static int nbChars = " + (sortedChar.length) + ";");
		System.out.println("	static struct Char chars[" + (sortedChar.length + 1) + "] = {");
		for (Font.Char c : sortedChar) {
			System.out.println("		{ " + (c.brightness) + ", "
					+ "float2(" + (c.x / (float)f.scaleW) + ", " + (c.y / (float)f.scaleH) + "), "
					+ "float2(" + (c.width / (float)f.scaleW) + ", " + (c.height / (float)f.scaleH) + ") },");
		}
		System.out.println("		{ 1.1, float2(0, 0), float2(0.1, 0.1) }");
		System.out.println("	};");
	}
	
	static float round(float v) {
		return Math.round(v * 100_000) / 100_000f;
	}
	
	
}
