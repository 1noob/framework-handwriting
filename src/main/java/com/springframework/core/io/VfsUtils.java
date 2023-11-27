package com.springframework.core.io;

import com.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public abstract class VfsUtils {
    private static final String VFS3_PKG = "org.jboss.vfs.";
    private static final String VFS_NAME = "VFS";

    private static final Method VFS_METHOD_GET_ROOT_URL;
    private static final Method VFS_METHOD_GET_ROOT_URI;

    private static final Method VIRTUAL_FILE_METHOD_EXISTS;
    private static final Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
    private static final Method VIRTUAL_FILE_METHOD_GET_SIZE;
    private static final Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
    private static final Method VIRTUAL_FILE_METHOD_TO_URL;
    private static final Method VIRTUAL_FILE_METHOD_TO_URI;
    private static final Method VIRTUAL_FILE_METHOD_GET_NAME;
    private static final Method VIRTUAL_FILE_METHOD_GET_PATH_NAME;
    private static final Method VIRTUAL_FILE_METHOD_GET_PHYSICAL_FILE;
    private static final Method VIRTUAL_FILE_METHOD_GET_CHILD;

    protected static final Class<?> VIRTUAL_FILE_VISITOR_INTERFACE;
    protected static final Method VIRTUAL_FILE_METHOD_VISIT;

    private static final Field VISITOR_ATTRIBUTES_FIELD_RECURSE;

    static {
        ClassLoader loader = VfsUtils.class.getClassLoader();
        try {
            Class<?> vfsClass = loader.loadClass(VFS3_PKG + VFS_NAME);
            VFS_METHOD_GET_ROOT_URL = vfsClass.getMethod("getChild", URL.class);
            VFS_METHOD_GET_ROOT_URI = vfsClass.getMethod("getChild", URI.class);

            Class<?> virtualFile = loader.loadClass(VFS3_PKG + "VirtualFile");
            VIRTUAL_FILE_METHOD_EXISTS = virtualFile.getMethod("exists");
            VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = virtualFile.getMethod("openStream");
            VIRTUAL_FILE_METHOD_GET_SIZE = virtualFile.getMethod("getSize");
            VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = virtualFile.getMethod("getLastModified");
            VIRTUAL_FILE_METHOD_TO_URI = virtualFile.getMethod("toURI");
            VIRTUAL_FILE_METHOD_TO_URL = virtualFile.getMethod("toURL");
            VIRTUAL_FILE_METHOD_GET_NAME = virtualFile.getMethod("getName");
            VIRTUAL_FILE_METHOD_GET_PATH_NAME = virtualFile.getMethod("getPathName");
            VIRTUAL_FILE_METHOD_GET_PHYSICAL_FILE = virtualFile.getMethod("getPhysicalFile");
            VIRTUAL_FILE_METHOD_GET_CHILD = virtualFile.getMethod("getChild", String.class);

            VIRTUAL_FILE_VISITOR_INTERFACE = loader.loadClass(VFS3_PKG + "VirtualFileVisitor");
            VIRTUAL_FILE_METHOD_VISIT = virtualFile.getMethod("visit", VIRTUAL_FILE_VISITOR_INTERFACE);

            Class<?> visitorAttributesClass = loader.loadClass(VFS3_PKG + "VisitorAttributes");
            VISITOR_ATTRIBUTES_FIELD_RECURSE = visitorAttributesClass.getField("RECURSE");
        } catch (Throwable ex) {
            throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
        }
    }

    protected static Object invokeVfsMethod(Method method, Object target, Object... args) throws IOException {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof IOException) {
                throw (IOException) targetEx;
            }
            ReflectionUtils.handleInvocationTargetException(ex);
        } catch (Exception ex) {
            ReflectionUtils.handleReflectionException(ex);
        }

        throw new IllegalStateException("Invalid code path reached");
    }


    // protected methods used by the support sub-package

    protected static Object getRoot(URL url) throws IOException {
        return invokeVfsMethod(VFS_METHOD_GET_ROOT_URL, null, url);
    }

    protected static String doGetPath(Object resource) {
        return (String) ReflectionUtils.invokeMethod(VIRTUAL_FILE_METHOD_GET_PATH_NAME, resource);
    }

    protected static Object doGetVisitorAttributes() {
        return ReflectionUtils.getField(VISITOR_ATTRIBUTES_FIELD_RECURSE, null);
    }

    static URL getURL(Object vfsResource) throws IOException {
        return (URL) invokeVfsMethod(VIRTUAL_FILE_METHOD_TO_URL, vfsResource);
    }
}
