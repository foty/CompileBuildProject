package com.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Create by lxx
 * Date : 2022/6/14 17:57
 * Use by
 */
public class Utils {

    public static String relativePath(File self, File to) throws IOException {
        String fromPath = self.getCanonicalPath();
        String toPath = to.getCanonicalPath();
        String[] fromPathStack = getPathStack(fromPath);
        String[] toPathStack = getPathStack(toPath);
        if (0 < toPathStack.length && 0 < fromPathStack.length) {
            if (!fromPathStack[0].equals(toPathStack[0])) {
                return getPath(Arrays.asList(toPathStack));
            } else {
                int minLength = Math.min(fromPathStack.length, toPathStack.length);

                int same;
                for (same = 1; same < minLength && fromPathStack[same].equals(toPathStack[same]); ++same) {
                }

                List<String> relativePathStack = new ArrayList();

                for (int i = same; i < fromPathStack.length; ++i) {
                    relativePathStack.add("..");
                }

                relativePathStack.addAll(Arrays.asList(toPathStack).subList(same, toPathStack.length));
                return getPath(relativePathStack);
            }
        } else {
            return getPath(Arrays.asList(toPathStack));
        }
    }

    private static String[] getPathStack(String path) {
        String normalizedPath = path.replace(File.separatorChar, '/');
        return normalizedPath.split("/");
    }

    private static String getPath(List pathStack) {
        return getPath(pathStack, '/');
    }

    private static String getPath(List pathStack, char separatorChar) {
        StringBuilder buffer = new StringBuilder();
        Iterator iter = pathStack.iterator();
        if (iter.hasNext()) {
            buffer.append(iter.next());
        }

        while (iter.hasNext()) {
            buffer.append(separatorChar);
            buffer.append(iter.next());
        }

        return buffer.toString();
    }

}
