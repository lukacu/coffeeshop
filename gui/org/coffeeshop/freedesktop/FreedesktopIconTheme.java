package org.coffeeshop.freedesktop;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.coffeeshop.external.External;
import org.coffeeshop.external.OperatingSystem;
import org.coffeeshop.io.Files;
import org.coffeeshop.string.IniProperties;
import org.coffeeshop.string.StringUtils;

public class FreedesktopIconTheme {

	public enum Type {
		FIXED, SCALED, THRESHOLD
	}

	private static final String ENV_PATHS = "XDG_DATA_DIRS";

	private static final String THEME_FILE = "index.theme";

	private static Collection<String> extensions;

	private static Collection<String> themePaths;

	private static Map<String, FreedesktopIconTheme> cache = new HashMap<String, FreedesktopIconTheme>();
	
	static {
		extensions = new Vector<String>();
		extensions.add("png");
		// extensions.add("svg");
		extensions.add("xpm");

		themePaths = new Vector<String>();
		themePaths.add(Files.join(System.getProperty("user.home"), ".icons"));

		String paths = System.getenv(ENV_PATHS);

		if (!StringUtils.empty(paths)) {

			String[] apaths = paths.split(File.pathSeparator);
			for (int i = 0; i < apaths.length; i++) {
				themePaths.add(apaths[i]);
			}

		}
		themePaths.add("/usr/share/pixmaps");

	}

	private static FreedesktopIconTheme resolveTheme(String name) {
		FreedesktopIconTheme theme = cache.get(name);

		if (theme == null) {

			try {

				theme = new FreedesktopIconTheme(name);

				cache.put(name, theme);

				return theme;

			} catch (IOException e) {

			}

		}

		return theme;
	}

	private static File find(String suffix) {

		for (String path : themePaths) {
			File f = new File(path, suffix);
			if (f.exists())
				return f;
		}

		return null;
	}

	public static FreedesktopIconTheme getUserTheme() throws IOException {
		
		if (OperatingSystem.getOperatingSystemType() != OperatingSystem.OperatingSystemType.LINUX)
			return null;
		
		String name = null;
		
		if (External.execute("which gconftool") == 0) {
			name = External.gather("gconftool -g /desktop/gnome/interface/icon_theme");
		} else if (External.execute("which gconftool-2") == 0) {
			name = External.gather("gconftool-2 -g /desktop/gnome/interface/icon_theme");
		}
		
		if (name != null)
			return new FreedesktopIconTheme(name);
		
		return null;
	}
	
	private IniProperties properties;

	private Collection<String> parents;

	private Collection<String> subdirectories;

	private String name;

	private FreedesktopIconTheme(String name) throws IOException {

		File themeFile = find(name + File.separator + THEME_FILE);

		this.name = name;

		if (themeFile == null)
			throw new IOException("Theme not found");

		properties = new IniProperties(themeFile);

		parents = new Vector<String>();
		String p = properties.getProperty("Icon Theme", "inherits");

		if (!StringUtils.empty(p)) {

			String[] parts = p.split(",");
			for (int i = 0; i < parts.length; i++) {
				parents.add(parts[i]);
			}

		}

		subdirectories = new Vector<String>();

		for (String section : properties.sections()) {
			if (section.compareTo("Icon Theme") == 0)
				continue;

			subdirectories.add(section);

		}

	}

	public boolean hasParents() {
		return !parents.isEmpty();
	}

	public boolean isHicolor() {
		return false;
	}

	public Collection<String> getParents() {
		return parents;
	}

	public String getName() {
		return name;
	}

	private boolean directoryMatchesSize(String subdir, int iconsize) {

		Type type = Type.valueOf(properties.getProperty(subdir, "Type")
				.toUpperCase());
		int size;
		switch (type) {
		case FIXED:
			size = Integer.parseInt(properties.getProperty(subdir, "Size"));
			return iconsize == size;
		case SCALED:
			int minSize = Integer.parseInt(properties.getProperty(subdir,
					"MinSize"));
			int maxSize = Integer.parseInt(properties.getProperty(subdir,
					"MaxSize"));

			return (minSize <= iconsize) && (iconsize <= maxSize);
		case THRESHOLD:
			size = Integer.parseInt(properties.getProperty(subdir, "Size"));
			int threshold = Integer.parseInt(properties.getProperty(subdir,
					"Threshold"));

			return (size - threshold <= iconsize)
					&& (iconsize <= size + threshold);
		}

		return false;
	}

	private int directorySizeDistance(String subdir, int iconsize) {
		Type type = Type.valueOf(properties.getProperty(subdir, "Type")
				.toUpperCase());
		int size = Integer.parseInt(properties.getProperty(subdir, "Size"));
		int minSize = Integer.parseInt(properties
				.getProperty(subdir, "MinSize"));
		int maxSize = Integer.parseInt(properties
				.getProperty(subdir, "MaxSize"));
		switch (type) {
		case FIXED:
			return Math.abs(size - iconsize);
		case SCALED:
			if (iconsize < minSize)
				return minSize - iconsize;
			if (iconsize > maxSize)
				return iconsize - maxSize;
			return 0;

		case THRESHOLD:
			int threshold = Integer.parseInt(properties.getProperty(subdir,
					"Threshold"));

			if (iconsize < size - threshold)
				return minSize - iconsize;
			if (iconsize > size + threshold)
				return iconsize - maxSize;
			return 0;
		}

		return 0;
	}

	public File findIcon(String icon, int size) {

		File filename = findIconHelper(icon, size, this.getName());
		if (filename != null)
			return filename;
		return lookupFallbackIcon(icon);
	}

	private static File findIconHelper(String icon, int size, String name) {

		FreedesktopIconTheme theme = resolveTheme(name);

		if (theme == null)
			return null;

		File filename = lookupIcon(icon, size, theme);

		if (filename != null)
			return filename;

		Collection<String> parents = new Vector<String>();
		if (theme.hasParents())
			parents.addAll(theme.parents);
		else if (theme.isHicolor())
			parents.add("hicolor");

		for (String parent : parents) {
			filename = findIconHelper(icon, size, parent);
			if (filename != null)
				return filename;
		}
		return null;
	}

	private static File lookupIcon(String iconname, int size,
			FreedesktopIconTheme theme) {

		for (String subdirectory : theme.subdirectories) {
			for (String basename : themePaths) {
				File path = new File(basename, theme.name + File.separator
						+ subdirectory);
				for (String extension : extensions) {

					if (theme.directoryMatchesSize(subdirectory, size)) {
						File filename = new File(path, iconname + "."
								+ extension);
						if (filename.exists())
							return filename;
					}
				}
			}
		}

		int minimal_size = Integer.MAX_VALUE;
		File closest_filename = null;

		for (String subdirectory : theme.subdirectories) {
			for (String basename : themePaths) {
				File path = new File(basename, theme.name + File.separator
						+ subdirectory);
				for (String extension : extensions) {
					File filename = new File(path, iconname + "." + extension);
					if (filename.exists()
							&& theme.directorySizeDistance(subdirectory, size) < minimal_size) {
						closest_filename = filename;
						minimal_size = theme.directorySizeDistance(
								subdirectory, size);
					}
				}
			}
		}

		if (closest_filename != null)
			return closest_filename;
		return null;
	}

	private File lookupFallbackIcon(String iconname) {
		for (String basename : themePaths) {
			File path = new File(basename);
			for (String extension : extensions) {
				File filename = new File(path, iconname + "." + extension);
				if (filename.exists())
					return filename;
			}
		}
		return null;
	}

	public File findBestIcon(String[] iconList, int size) {
		File filename = findBestIconHelper(iconList, size, getName());
		if (filename != null)
			return filename;

		for (int i = 0; i < iconList.length; i++) {
			String icon = iconList[i];
			filename = lookupFallbackIcon(icon);
			if (filename != null)
				return filename;
		}
		return null;
	}

	private File findBestIconHelper(String[] iconList, int size,
			String themeName) {

		FreedesktopIconTheme theme = resolveTheme(themeName);

		for (int i = 0; i < iconList.length; i++) {
			File filename = lookupIcon(iconList[i], size, theme);
			if (filename != null)
				return filename;
		}

		Collection<String> parents = new Vector<String>();
		if (theme.hasParents())
			parents.addAll(theme.parents);
		else if (theme.isHicolor())
			parents.add("hicolor");

		for (String parent : parents) {
			File filename = findBestIconHelper(iconList, size, parent);
			if (filename != null)
				return filename;
		}
		return null;
	}

}
